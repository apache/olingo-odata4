/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/

 grammar UriParser;


//Antlr4 (as most parsers) has a lexer for token detection and a parser which defines
//rules based on the tokens. However its hard to define a clear lexer on the
//ODATA URI syntax due to some reasons:
// - the syntax is based on the URI specification and there fore contains the definition
//   of delimiters and percent encoding
// - the syntax includes JSON
// - the syntax includes a expression syntax which comes from ODATA itself (e.g. $filter)
// - the syntax includes searchstring and searchword 
// - the ABNF describing syntax is not defined in a context free manner
// so there are several kinds of "String" tokens:
// -  strings with single quotes, 
// -  strings with single quotes and a special syntax within the quotes (like geometry data)
// -  strings with double quotes
// -  strings without quotes ( usually identifiers, searchstrings, searchwords, custom parameters)
//    but each with different allowed charactersets
// Drawing a simple line between lexer and parser is not possible.
//
// This grammer is a compromiss we have choosen to satisfy the requirements we have
// - the grammer is context free
//   - this makes the parser much simpler and we have a clear saparation between parsing and
//     EDM validation, but also creates a parse tree which is not semantically correct from the
//     EDM perspective ( e.g.it will not pass the EDM validation)
// - the grammer can not be applied on a full URI string
//   - the URI should be split according the URI specification before used as input for the 
//     ODATA parser
// - while creating the grammer the antlr lexer modes where only allowed in pure lexer grammers
//   not in combined grammers, and is was not possible to include lexer grammer with a mode into
//   a combined grammar without creating JAVA errors.

//   see https://github.com/antlr/antlr4/issues/160 "Support importing multi-mode lexer grammars"


//Naming convention 
//  ...
//Decoding encoding
//- within rule "resourcePath": special chars used in EDM.Strings must still be encoded when 
//  used as tokenizer input
//  e.g. .../Employees(id='Hugo%2FMueller')/EmployeeName <-- SLASH must be encoded to '%2F' in "Hugo/Mueller"
//    but it must not be encoded before the EmployeeName



options {
    language = Java;
    tokenVocab=UriLexer;
}





//;------------------------------------------------------------------------------
//; 0. URI
//;------------------------------------------------------------------------------

//ABNF odataUri and serviceRoot are currently not used here
//odataUri = serviceRoot [ odataRelativeUri ]  
//
//serviceRoot = ( "https" / "http" )                    ; Note: case-insensitive 
//              "://" host [ ":" port ]
//              "/" *( segment-nz "/" )


odataRelativeUriEOF : odataRelativeUri? EOF;

//QM and FRAGMENT enable next lexer mode
odataRelativeUri    : BATCH                                                      # batchAlt //TODO alt at beginnig
                    | ENTITY         QM eo=entityOptions                         # entityAlt
                    | METADATA     ( QM format )? ( FRAGMENT contextFragment )?  # metadataAlt
                    | resourcePath ( QM queryOptions )?                          # resourcePathAlt

                    ;

//;------------------------------------------------------------------------------
//; 1. Resource Path
//;------------------------------------------------------------------------------
                   
resourcePath        : ALL           # allAlt
                    | crossjoin     # crossjoinAlt
                    | pathSegments  # pathSegmentsAlt
                    ;
crossjoin           : CROSSJOIN OPEN odi+=odataIdentifier ( COMMA odi+=odataIdentifier )* CLOSE;

pathSegments        : ps+=pathSegment (SLASH ps+=pathSegment)* constSegment?;

pathSegment         : ns=namespace? odi=odataIdentifier nvl=nameValueOptList*;

nameValueOptList    : vo=valueOnly | nvl=nameValueList;
valueOnly           : OPEN (primitiveLiteral ) CLOSE;
nameValueList       : OPEN kvp+=nameValuePair ( COMMA kvp+=nameValuePair )* CLOSE;
nameValuePair       : odi=odataIdentifier EQ (AT ali=odataIdentifier |  val1=primitiveLiteral /*| val2=enumX*/);

constSegment        : SLASH (v=value | c=count | r=ref );

count               : COUNT;
ref                 : REF;
value               : VALUE;
//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

queryOptions    : qo+=queryOption ( AMP qo+=queryOption )*;

queryOption     : systemQueryOption 
                | AT aliasAndValue 
                | customQueryOption 
                ;

entityOptions   : (eob+=entityOption AMP )* ID EQ REST ( AMP eoa+=entityOption )*;
entityOption    : ( expand | format | select )
                | customQueryOption 

                ;



systemQueryOption   : expand
                    | filter 
                    | format 
                    | id
                    | inlinecount 
                    | orderby 
                    | search
                    | select 
                    | skip 
                    | skiptoken
                    | top
                    ;

id                  : ID EQ REST;
skiptoken           : SKIPTOKEN EQ REST;
expand              : EXPAND EQ expandItemList;

expandItemList      : expandItem ( COMMA expandItem )*;

expandItem          : STAR ( SLASH ref | OPEN (LEVELS EQ INT | LEVELSMAX)  CLOSE )?
                    | expandPath expandPathExtension?;

expandPath          : ( namespace? odataIdentifier ) ( SLASH namespace? odataIdentifier )*;
expandPathExtension : SLASH ref   ( OPEN expandRefOption   ( SEMI expandRefOption   )* CLOSE )?
                    | SLASH count ( OPEN expandCountOption ( SEMI expandCountOption )* CLOSE )?
                    |             OPEN expandOption      ( SEMI expandOption      )* CLOSE 
                    ;  
expandCountOption   : filter
                    | search
                    ;
expandRefOption     : expandCountOption
                    | orderby
                    | skip 
                    | top 
                    | inlinecount
                    ;
expandOption        : expandRefOption
                    | select 
                    | expand
                    | LEVELS;

filter              : FILTER EQ commonExpr;

orderby             : ORDERBY EQ orderbyItem ( COMMA orderbyItem )*;
orderbyItem         : commonExpr ( WSP ( ASC | DESC ) )?;

//this is completly done in lexer grammer to avoid ambiguities with odataIdentifier and STRING
skip                : SKIP EQ INT;
top                 : TOP EQ INT;
format              : FORMAT EQ ( ATOM | JSON | XML | PCHARS ( SLASH PCHARS)?);

inlinecount         : COUNT EQ booleanNonCase;

search              : SEARCH searchSpecialToken;

searchSpecialToken  : EQ WSP? searchExpr;

searchExpr          : (NOT WSP) searchExpr
                    | searchExpr searchExpr
                    | searchExpr  WSP searchExpr
                    | searchExpr ( WSP AND WSP) searchExpr
                    | searchExpr ( WSP OR WSP) searchExpr
                    | searchPhrase
                    | searchWord
                    ;

searchPhrase        : SEARCHPHRASE;
searchWord          : SEARCHWORD;  

select              : SELECT EQ selectItem ( COMMA selectItem )*;
selectItem          : namespace? STAR
                    | (namespace? odataIdentifier nameValueOptList? ) ( SLASH namespace? odataIdentifier nameValueOptList? )*
                    ;

aliasAndValue       : odataIdentifier EQ parameterValue;
parameterValue      : //arrayOrObject
                      commonExpr
                    ;


                    
customQueryOption   : customName ( EQ customValue)?
                    ;
customName          : CUSTOMNAME;
customValue         : CUSTOMVALUE;



//;------------------------------------------------------------------------------
//; 3. Context URL Fragments
//;------------------------------------------------------------------------------
//ps+=pathSegment (SLASH ps+=pathSegment)*
//PRIMITIVETYPENAME
contextFragment     : REF
                    | PRIMITIVETYPENAME
                    | 'Collection($ref)'
                    | 'Collection(Edm.EntityType)'
                    | 'Collection(Edm.ComplexType)'

                    | COLLECTION_FIX OPEN ( PRIMITIVETYPENAME | namespace odataIdentifier ) CLOSE

                    | namespace? odataIdentifier 
                      ( '/$deletedEntity'
                      | '/$link'
                      | '/$deletedLink'
                      | nameValueOptList? ( SLASH namespace? odataIdentifier)* ( propertyList )? ( '/$delta'  )? ( entity )?
                      )
                    ;              

propertyList         : OPEN propertyListItem ( COMMA propertyListItem )* CLOSE;
propertyListItem     : STAR           //; all structural properties
                     | propertyListProperty
                     ;
propertyListProperty : namespace? odataIdentifier ( SLASH namespace? odataIdentifier)* ( '+' )? ( propertyList)?
                     ;
                 
entity               : '/$entity';
//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------

// this expression part of the grammer is not similar to the ABNF because
// we had to introduced operator precesence witch is not reflected in the ABNF

test        : test_expr EOF;
test_expr   : INT
            //| test_expr /*WSP*/ (  '!' | '*' ) /*WSP*/ test_expr;
            //| test_expr WSP (  '!' | '*' ) WSP test_expr;
            | test_expr ( WSP '!' WSP | WSP '*' WSP ) test_expr;

commonExpr          : OPEN commonExpr CLOSE                                                     #altPharenthesis
                    | methodCallExpr                                                            #altMethod
                    | ( unary WSP ) commonExpr                                                  #altUnary
                    | memberExpr                                                                #altMember
                    | commonExpr (WSP MUL WSP | WSP DIV WSP | WSP MOD WSP ) commonExpr    #altMult
                    | commonExpr (WSP ADD WSP | WSP SUB WSP) commonExpr                     #altAdd
                    | commonExpr (WSP GT WSP | WSP GE WSP | WSP LT WSP | WSP LE WSP | WSP ISOF WSP) commonExpr    #altComparisn
                    | commonExpr (WSP EQ_ALPHA WSP | WSP NE WSP) commonExpr                       #altEquality
                    | commonExpr (WSP AND WSP) commonExpr                                     #altAnd
                    | commonExpr (WSP OR WSP) commonExpr                                      #altOr
                    | rootExpr                                                                  #altRoot  //; $...
                    | AT odataIdentifier                                                        #altAlias  // @...
                    | primitiveLiteral                                                          #altLiteral  // ...
                    ;

unary               : (MINUS| NOT) ;

rootExpr            : ROOT pathSegments;

memberExpr          : '$it' | '$it/'? pathSegments;

anyExpr             : 'any' OPEN WS* /* [ lambdaVariableExpr BWS COLON BWS lambdaPredicateExpr ] WS* */ CLOSE;
allExpr             : 'all' OPEN WS* /*   lambdaVariableExpr BWS COLON BWS lambdaPredicateExpr   WS* */ CLOSE;

methodCallExpr      : indexOfMethodCallExpr
                    | toLowerMethodCallExpr
                    | toUpperMethodCallExpr
                    | trimMethodCallExpr
                    | substringMethodCallExpr
                    | concatMethodCallExpr
                    | lengthMethodCallExpr
                    | yearMethodCallExpr
                    | monthMethodCallExpr
                    | dayMethodCallExpr
                    | hourMethodCallExpr
                    | minuteMethodCallExpr
                    | secondMethodCallExpr
                    | fractionalsecondsMethodCallExpr
                    | totalsecondsMethodCallExpr
                    | dateMethodCallExpr
                    | timeMethodCallExpr
                    | roundMethodCallExpr
                    | floorMethodCallExpr
                    | ceilingMethodCallExpr
                    | distanceMethodCallExpr
                    | geoLengthMethodCallExpr
                    | totalOffsetMinutesMethodCallExpr
                    | minDateTimeMethodCallExpr
                    | maxDateTimeMethodCallExpr
                    | nowMethodCallExpr
                    //from boolean
                    | isofExpr
                    | castExpr
                    | endsWithMethodCallExpr
                    | startsWithMethodCallExpr
                    | containsMethodCallExpr
                    | intersectsMethodCallExpr
                    ;


containsMethodCallExpr    : CONTAINS_WORD    WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
startsWithMethodCallExpr  : STARTSWITH_WORD  WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
endsWithMethodCallExpr    : ENDSWITH_WORD    WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
lengthMethodCallExpr      : LENGTH_WORD      WS* commonExpr WS* CLOSE;
indexOfMethodCallExpr     : INDEXOF_WORD     WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
substringMethodCallExpr   : SUBSTRING_WORD   WS* commonExpr WS* COMMA WS* commonExpr WS* ( COMMA WS* commonExpr WS* )? CLOSE;
toLowerMethodCallExpr     : TOLOWER_WORD     WS* commonExpr WS* CLOSE;
toUpperMethodCallExpr     : TOUPPER_WORD     WS* commonExpr WS* CLOSE;
trimMethodCallExpr        : TRIM_WORD        WS* commonExpr WS* CLOSE;
concatMethodCallExpr      : CONCAT_WORD      WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

yearMethodCallExpr                : YEAR_WORD                WS* commonExpr WS* CLOSE;
monthMethodCallExpr               : MONTH_WORD               WS* commonExpr WS* CLOSE;
dayMethodCallExpr                 : DAY_WORD                 WS* commonExpr WS* CLOSE;
hourMethodCallExpr                : HOUR_WORD                WS* commonExpr WS* CLOSE;
minuteMethodCallExpr              : MINUTE_WORD              WS* commonExpr WS* CLOSE;
secondMethodCallExpr              : SECOND_WORD              WS* commonExpr WS* CLOSE;
fractionalsecondsMethodCallExpr   : FRACTIONALSECONDS_WORD   WS* commonExpr WS* CLOSE;
totalsecondsMethodCallExpr        : TOTALSECONDS_WORD        WS* commonExpr WS* CLOSE;
dateMethodCallExpr                : DATE_WORD                WS* commonExpr WS* CLOSE;
timeMethodCallExpr                : TIME_WORD                WS* commonExpr WS* CLOSE;
totalOffsetMinutesMethodCallExpr  : TOTALOFFSETMINUTES_WORD  WS* commonExpr WS* CLOSE;

minDateTimeMethodCallExpr         : MINDATETIME_WORD WS* CLOSE;
maxDateTimeMethodCallExpr         : MAXDATETIME_WORD WS* CLOSE;
nowMethodCallExpr                 : NOW_WORD         WS* CLOSE;

roundMethodCallExpr               : ROUND_WORD   WS* commonExpr WS* CLOSE;
floorMethodCallExpr               : FLOOR_WORD   WS* commonExpr WS* CLOSE;
ceilingMethodCallExpr             : CEILING_WORD WS* commonExpr WS* CLOSE;

distanceMethodCallExpr            : GEO_DISTANCE_WORD   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
geoLengthMethodCallExpr           : GEO_LENGTH_WORD     OPEN WS* commonExpr WS* CLOSE;
intersectsMethodCallExpr          : GEO_INTERSECTS_WORD OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

isofExpr                          : ISOF_WORD  WS* ( commonExpr WS* COMMA WS* )? qualifiedtypename WS* CLOSE;
castExpr                          : CAST_WORD  WS* ( commonExpr WS* COMMA WS* )? qualifiedtypename WS* CLOSE;

//;------------------------------------------------------------------------------
//; 5. JSON format for function parameters
//;------------------------------------------------------------------------------
//; Note: the query part of a URI needs to be partially percent-decoded before
//; applying these rules, see comment at the top of this file
//;------------------------------------------------------------------------------
/*
arrayOrObject       : complexColInUri  
                    | complexInUri
                    | rootExprCol
                    | primitiveColInUri;
               
complexColInUri     : BEGIN_ARRAY 
                      ( complexInUri ( WS* COMMA WS* complexInUri )* )?
                      END_ARRAY;
                  
complexInUri        : BEGIN_OBJECT ( jv+=json_value ( WS* COMMA WS* jv+=json_value)* )? END_OBJECT;

json_value          : annotationInUri 
                    | primitivePropertyInUri 
                    | complexPropertyInUri 
                    | collectionPropertyInUri  
                    | navigationPropertyInUri
                    ;

collectionPropertyInUri : QUOTATION_MARK odataIdentifier QUOTATION_MARK
                          NAME_SEPARATOR 
                          ( primitiveColInUri | complexColInUri )
                        ;

primitiveColInUri       : BEGIN_ARRAY ( plj+=primitive1LiteralInJSON *( WS* COMMA WS* plj+=primitive1LiteralInJSON )  )? END_ARRAY
                        ;
                    
complexPropertyInUri    : QUOTATION_MARK odataIdentifier QUOTATION_MARK 
                          NAME_SEPARATOR 
                           complexInUri
                        ;

annotationInUri         : QUOTATION_MARK ns=namespace? odi=odataIdentifier QUOTATION_MARK
                          NAME_SEPARATOR
                          ( complexInUri | complexColInUri | primitive1LiteralInJSON | primitiveColInUri );

primitivePropertyInUri  : QUOTATION_MARK odataIdentifier QUOTATION_MARK 
                          NAME_SEPARATOR 
                          primitive1LiteralInJSON;


navigationPropertyInUri : QUOTATION_MARK odataIdentifier QUOTATION_MARK
                          NAME_SEPARATOR ( rootExpr | rootExprCol )
                        ;

rootExprCol   : BEGIN_ARRAY 
                ( rootExpr *( WS* COMMA WS* rootExpr ) )?
                END_ARRAY
              ;
                                        
//; JSON syntax: adapted to URI restrictions from [RFC4627]                 
primitive1LiteralInJSON  : STRING_IN_JSON
                        | number_in_json
                        | TRUE
                        | FALSE
                        | 'null'
                        ;

number_in_json          : INT | DECIMAL;
*/
//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------

qualifiedtypename       : PRIMITIVETYPENAME
                        | namespace odataIdentifier
                        | 'collection' OPEN ( PRIMITIVETYPENAME | namespace odataIdentifier ) CLOSE
                        ;

namespace               : (odataIdentifier POINT)+;

odataIdentifier         : ODATAIDENTIFIER;

//;------------------------------------------------------------------------------
//; 7. Literal Data Values
//;------------------------------------------------------------------------------


/*TODO add missing values*/
primitiveLiteral    : nullrule
                    | booleanNonCase
                    | DECIMAL   //includes double and single literals
                    | INT       //includes int16/int32 and int64 literals
                    | BINARY  
                    | DATE
                    | DATETIMEOFFSET
                    | DURATION
                    | GUID
                    | string
                    | TIMEOFDAY
                    | geographyCollection
                    | geographyLineString
                    | geographyMultilineString
                    | geographyMultipoint
                    | geographyMultipolygon
                    | geographyPoint
                    | geographyPolygon
                    | geometryCollection
                    | geometryLineString
                    | geometryMultilineString
                    | geometryMultipoint
                    | geometryMultipolygon
                    | geometryPoint
                    | geometryPolygon
                    | enumX
                    ;


nullrule            : NULLVALUE;// (SQUOTE qualifiedtypename SQUOTE)?;
booleanNonCase      : BOOLEAN | TRUE | FALSE;
string              : STRING;


enumX               : namespace odataIdentifier STRING /*SQUOTE enumValue SQUOTE*/;
enumValue           : singleEnumValue *( COMMA singleEnumValue );
singleEnumValue     : odataIdentifier / INT;


geographyCollection        : GEOGRAPHY  fullCollectionLiteral SQUOTE;
fullCollectionLiteral      : sridLiteral collectionLiteral;

collectionLiteral          : (COLLECTION | COLLECTION_FIX) OPEN geoLiteral ( COMMA geoLiteral )* CLOSE;

geoLiteral                 : collectionLiteral
                           | lineStringLiteral
                           | multipointLiteral
                           | multilineStringLiteral
                           | multipolygonLiteral
                           | pointLiteral
                           | polygonLiteral;

geographyLineString        : GEOGRAPHY  fullLineStringLiteral SQUOTE;
fullLineStringLiteral      : sridLiteral lineStringLiteral;
lineStringLiteral          : LINESTRING lineStringData;
lineStringData             : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;

geographyMultilineString   : GEOGRAPHY  fullMultilineStringLiteral SQUOTE;
fullMultilineStringLiteral : sridLiteral multilineStringLiteral;
multilineStringLiteral     : MULTILINESTRING OPEN ( lineStringData ( COMMA lineStringData )* )? CLOSE;

geographyMultipoint        : GEOGRAPHY  fullMultipointLiteral SQUOTE;
fullMultipointLiteral      : sridLiteral multipointLiteral;
multipointLiteral          : MULTIPOINT OPEN ( pointData ( COMMA pointData )* )? CLOSE ;

geographyMultipolygon      : GEOGRAPHY  fullmultipolygonLiteral SQUOTE;
fullmultipolygonLiteral    : sridLiteral multipolygonLiteral;
multipolygonLiteral        : MULTIPOLYGON OPEN ( polygonData ( COMMA polygonData )* )? CLOSE;

geographyPoint             : GEOGRAPHY  fullpointLiteral SQUOTE;
fullpointLiteral           : sridLiteral pointLiteral;

pointLiteral               : GEO_POINT pointData;
pointData                  : OPEN positionLiteral CLOSE;

positionLiteral            : (DECIMAL | INT ) WSP (DECIMAL | INT );  //; longitude, then latitude


geographyPolygon           : GEOGRAPHY fullPolygonLiteral SQUOTE;
fullPolygonLiteral         : sridLiteral polygonLiteral;
polygonLiteral             : POLYGON polygonData;
polygonData                : OPEN ringLiteral ( COMMA ringLiteral )* CLOSE;
ringLiteral                : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;
                 

geometryCollection        : GEOMETRY  fullCollectionLiteral      SQUOTE;
geometryLineString        : GEOMETRY  fullLineStringLiteral      SQUOTE;
geometryMultilineString   : GEOMETRY  fullMultilineStringLiteral SQUOTE;
geometryMultipoint        : GEOMETRY  fullMultipointLiteral      SQUOTE;
geometryMultipolygon      : GEOMETRY  fullmultipolygonLiteral    SQUOTE;
geometryPoint             : GEOMETRY  fullpointLiteral           SQUOTE;
geometryPolygon           : GEOMETRY  fullPolygonLiteral         SQUOTE;

sridLiteral               : SRID EQ INT SEMI;





/*
mode MODEd333gh;

MO12E1 : 'ASD' -> mode(DEFAULT_MODE);*/

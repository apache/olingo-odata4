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
//TODO add the new "ENTITYCAST"
odataRelativeUri    : BATCH                                                                     # altBatch 
                    | ENTITY       QM vEO=entityOptions                                          # altEntity
                    | ENTITY       SLASH vNS=namespace vODI=odataIdentifier QM vEO=entityOptionsCast  # altEntityCast
                    | METADATA     ( QM vF=format )? ( FRAGMENT vCF=contextFragment )?                 # altMetadata
                    | vRP=resourcePath ( QM vQO=queryOptions )?                                         # altResourcePath
                    ;

//;------------------------------------------------------------------------------
//; 1. Resource Path
//;------------------------------------------------------------------------------
                   
resourcePath        : vAll=ALL          
                    | vCJ=crossjoin     
                    | vlPS=pathSegments  
                    ;
crossjoin           : CROSSJOIN OPEN WSP? vlODI+=odataIdentifier WSP? ( COMMA WSP? vlODI+=odataIdentifier  WSP?)* CLOSE;

pathSegments        : vlPS+=pathSegment (SLASH vlPS+=pathSegment)* vCS=constSegment?;

pathSegment         : vNS=namespace? vODI=odataIdentifier vlNVO+=nameValueOptList*;

nameValueOptList    : OPEN (vVO=valueOnly | vNVL=nameValueList)? CLOSE;
valueOnly           : vV=commonExpr ;
nameValueList       : WSP* vlNVP+=nameValuePair WSP* ( COMMA WSP* vlNVP+=nameValuePair  WSP*)* ;
nameValuePair       : vODI=odataIdentifier EQ (AT vALI=odataIdentifier | vCOM=commonExpr /*TODO | val2=enumX*/);

constSegment        : SLASH (vV=value | vC=count | vR=ref | vAll=allExpr | vAny=anyExpr);

count               : COUNT;
ref                 : REF;
value               : VALUE;
//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

queryOptions    : vlQO+=queryOption ( AMP vlQO+=queryOption )*;

queryOption     : systemQueryOption
                | AT_Q aliasAndValue
                | customQueryOption
                ;

entityOptions   : (vlEOb+=entityOption AMP )* vlEOm=id ( AMP vlEOa+=entityOption )*;
entityOption    : format
                | customQueryOption 
                ;

entityOptionsCast : (vlEOb+=entityOptionCast AMP )* vlEOm=id ( AMP vlEOa+=entityOptionCast )*;
entityOptionCast  : expand 
                  | format 
                  | select 
                  | filter
                  | customQueryOption 
                  ;

systemQueryOption   : expand
                    | filter 
                    | format 
                    | id
                    | inlinecount 
                    | orderBy 
                    | search
                    | select 
                    | skip 
                    | skiptoken
                    | top
                    ;

id                  : ID EQ REST;
skiptoken           : SKIPTOKEN EQ REST;
expand              : EXPAND EQ vlEI+=expandItem ( COMMA vlEI+=expandItem )*;

expandItem          : vS=STAR ( SLASH vR=ref | OPEN LEVELS EQ ( vL=INT | vM=MAX)  CLOSE )?
                    | vEP=expandPath vEPE=expandPathExtension?;


expandPath          : vlPS+=pathSegment (SLASH vlPS+=pathSegment)*;
//expandPath          : expandPathSegment ( SLASH expandPathSegment )*;
//expandPathSegment   : vNS=namespace? vODI=odataIdentifier;

expandPathExtension : OPEN vlEO+=expandOption                        ( SEMI vlEO+=expandOption       )* CLOSE 
                    | SLASH vR=ref   ( OPEN vlEOR+=expandRefOption   ( SEMI vlEOR+=expandRefOption   )* CLOSE )?
                    | SLASH vC=count ( OPEN vlEOC+=expandCountOption ( SEMI vlEOC+=expandCountOption )* CLOSE )?
                    ;  
expandCountOption   : filter
                    | search
                    ;
expandRefOption     : expandCountOption
                    | orderBy
                    | skip
                    | top 
                    | inlinecount
                    ;
expandOption        : expandRefOption
                    | select 
                    | expand
                    | levels;

levels              : LEVELS EQ ( INT | MAX );

filter              : FILTER EQ commonExpr;

orderBy             : ORDERBY EQ orderByItem ( COMMA orderByItem )*;
orderByItem         : commonExpr ( WSP ( ASC | DESC ) )?;

//this is completly done in lexer grammer to avoid ambiguities with odataIdentifier and STRING
skip                : SKIP EQ INT;
top                 : TOP EQ INT;
format              : FORMAT EQ ( ATOM | JSON | XML | PCHARS ( SLASH PCHARS)?);

inlinecount         : COUNT EQ booleanNonCase;

search              : SEARCH searchSpecialToken;
searchInline        : SEARCH_INLINE searchSpecialToken;

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

select              : SELECT EQ vlSI+=selectItem ( COMMA vlSI+=selectItem )*;
selectItem          : vlSS+=selectSegment ( SLASH vlSS+=selectSegment ) *;
selectSegment       : vNS=namespace? ( vODI=odataIdentifier | vS=STAR );

aliasAndValue       : vODI=ODATAIDENTIFIER EQ vV=parameterValue;
parameterValue      : //TODO json not supported arrayOrObject
                      commonExpr
                    ;


                    
customQueryOption   : customName ( EQ customValue)?
                    ;
customName          : CUSTOMNAME;
customValue         : REST;



//;------------------------------------------------------------------------------
//; 3. Context URL Fragments
//;------------------------------------------------------------------------------
//TODO add ps+=pathSegment (SLASH ps+=pathSegment)*
contextFragment     : REF
                    | COLLECTION_REF
                    | COLLECTION_ENTITY_TYPE
                    | COLLECTION_COMPLEX_TYPE
                    | namespace? odataIdentifier 
                      ( SLASH ( DELETED_ENTITY | LINK | DELETED_LINK )
                      | nameValueOptList? ( SLASH namespace? odataIdentifier)* ( propertyList )? ( SLASH DELTA) ? (SLASH ENTITY) ? 
                      )
                    ;              

propertyList         : OPEN propertyListItem ( COMMA propertyListItem )* CLOSE;
propertyListItem     : STAR           //; all structural properties
                     | propertyListProperty
                     ;
propertyListProperty : namespace? odataIdentifier ( SLASH namespace? odataIdentifier)* ( PLUS )? ( propertyList)?
                     ;
                 

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
                    | vE1=commonExpr (WSP HAS WSP) vE2=commonExpr                               #altHas
                    | methodCallExpr                                                            #altMethod
                    | ( unary WSP ) commonExpr                                                  #altUnary
                    | anyExpr                                                                   #altAny
                    | allExpr                                                                   #altAll
                    | memberExpr                                                                #altMember
                    | vE1=commonExpr (WSP vO=MUL WSP | WSP vO=DIV WSP | WSP vO=MOD WSP ) vE2=commonExpr  #altMult
                    | vE1=commonExpr (WSP vO=ADD WSP | WSP vO=SUB WSP) vE2=commonExpr           #altAdd
                    | vE1=commonExpr (WSP vO=GT WSP | WSP vO=GE WSP | WSP vO=LT WSP 
                                     | WSP vO=LE WSP | WSP vO=ISOF WSP) vE2=commonExpr          #altComparism
                    | vE1=commonExpr (WSP vO=EQ_ALPHA WSP | WSP vO=NE WSP) vE2=commonExpr       #altEquality
                    | vE1=commonExpr (WSP AND WSP) vE2=commonExpr                               #altAnd
                    | vE1=commonExpr (WSP OR WSP) vE2=commonExpr                                #altOr
                    | rootExpr                                                                  #altRoot  //; $...
                    | AT odataIdentifier                                                        #altAlias  // @...
                    | primitiveLiteral                                                          #altLiteral  // ...
                    ;

unary               : (MINUS| NOT) ;

rootExpr            : ROOT vPs=pathSegments;

memberExpr          :  vIt=IT ( SLASH (vANY=anyExpr | vALL=allExpr))?
                    |  vIts=ITSLASH? vPs=pathSegments ( SLASH (vANY=anyExpr | vALL=allExpr))?;

anyExpr             : ANY_LAMDA OPEN WSP? ( vLV=odataIdentifier WSP? COLON WSP? vLE=commonExpr WSP? )?  CLOSE;
allExpr             : ALL_LAMDA OPEN WSP?   vLV=odataIdentifier WSP? COLON WSP? vLE=commonExpr WSP? CLOSE;

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


containsMethodCallExpr    : CONTAINS_WORD    WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
startsWithMethodCallExpr  : STARTSWITH_WORD  WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
endsWithMethodCallExpr    : ENDSWITH_WORD    WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
lengthMethodCallExpr      : LENGTH_WORD      WSP? vE1=commonExpr WSP? CLOSE;
indexOfMethodCallExpr     : INDEXOF_WORD     WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
substringMethodCallExpr   : SUBSTRING_WORD   WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? ( COMMA WSP? vE3=commonExpr WSP? )? CLOSE;
toLowerMethodCallExpr     : TOLOWER_WORD     WSP? vE1=commonExpr WSP? CLOSE;
toUpperMethodCallExpr     : TOUPPER_WORD     WSP? vE1=commonExpr WSP? CLOSE;
trimMethodCallExpr        : TRIM_WORD        WSP? vE1=commonExpr WSP? CLOSE;
concatMethodCallExpr      : CONCAT_WORD      WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;

yearMethodCallExpr                : YEAR_WORD                WSP? vE1=commonExpr WSP? CLOSE;
monthMethodCallExpr               : MONTH_WORD               WSP? vE1=commonExpr WSP? CLOSE;
dayMethodCallExpr                 : DAY_WORD                 WSP? vE1=commonExpr WSP? CLOSE;
hourMethodCallExpr                : HOUR_WORD                WSP? vE1=commonExpr WSP? CLOSE;
minuteMethodCallExpr              : MINUTE_WORD              WSP? vE1=commonExpr WSP? CLOSE;
secondMethodCallExpr              : SECOND_WORD              WSP? vE1=commonExpr WSP? CLOSE;
fractionalsecondsMethodCallExpr   : FRACTIONALSECONDS_WORD   WSP? vE1=commonExpr WSP? CLOSE;
totalsecondsMethodCallExpr        : TOTALSECONDS_WORD        WSP? vE1=commonExpr WSP? CLOSE;
dateMethodCallExpr                : DATE_WORD                WSP? vE1=commonExpr WSP? CLOSE;
timeMethodCallExpr                : TIME_WORD                WSP? vE1=commonExpr WSP? CLOSE;
totalOffsetMinutesMethodCallExpr  : TOTALOFFSETMINUTES_WORD  WSP? vE1=commonExpr WSP? CLOSE;

minDateTimeMethodCallExpr         : MINDATETIME_WORD WSP? CLOSE;
maxDateTimeMethodCallExpr         : MAXDATETIME_WORD WSP? CLOSE;
nowMethodCallExpr                 : NOW_WORD         WSP? CLOSE;

roundMethodCallExpr               : ROUND_WORD   WSP? vE1=commonExpr WSP? CLOSE;
floorMethodCallExpr               : FLOOR_WORD   WSP? vE1=commonExpr WSP? CLOSE;
ceilingMethodCallExpr             : CEILING_WORD WSP? vE1=commonExpr WSP? CLOSE;

distanceMethodCallExpr            : GEO_DISTANCE_WORD   WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
geoLengthMethodCallExpr           : GEO_LENGTH_WORD     WSP? vE1=commonExpr WSP? CLOSE;
intersectsMethodCallExpr          : GEO_INTERSECTS_WORD WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;

isofExpr                          : ISOF_WORD  WSP? ( vE1=commonExpr WSP? COMMA WSP? )? vNS=namespace vODI=odataIdentifier WSP? CLOSE;
castExpr                          : CAST_WORD  WSP? ( vE1=commonExpr WSP? COMMA WSP? )? vNS=namespace vODI=odataIdentifier WSP? CLOSE;

//;------------------------------------------------------------------------------
//; 5. JSON format for function parameters
//;------------------------------------------------------------------------------
//; Note: the query part of a URI needs to be partially percent-decoded before
//; applying these rules, see comment at the top of this file
//;------------------------------------------------------------------------------

arrayOrObject       : json_array
                    | json_object;

json_array          : BEGIN_ARRAY json_value ( WSP? COMMA WSP? json_value)* END_ARRAY;

json_value          : jsonPrimitiv
                    | rootExpr
                    | json_object
                    | json_array;

json_object         : BEGIN_OBJECT 
                      STRING_IN_JSON
                      NAME_SEPARATOR
                      json_value
                      END_OBJECT;

                                        
//; JSON syntax: adapted to URI restrictions from [RFC4627]                 
jsonPrimitiv        : STRING_IN_JSON
                    | number_in_json
                    | TRUE
                    | FALSE
                    | 'null'
                    ;

number_in_json          : INT | DECIMAL;

//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------

qualifiedtypename       : namespace odataIdentifier
                        | 'collection' OPEN ( namespace odataIdentifier ) CLOSE
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
                    | enumX
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
                    ;


nullrule            : NULLVALUE;// (SQUOTE qualifiedtypename SQUOTE)?;
booleanNonCase      : BOOLEAN | TRUE | FALSE;
string              : STRING;


enumX               : namespace odataIdentifier STRING;
enumValue           : singleEnumValue *( COMMA singleEnumValue );
singleEnumValue     : odataIdentifier / INT;


geographyCollection        : GEOGRAPHY  fullCollectionLiteral SQUOTE;
fullCollectionLiteral      : sridLiteral collectionLiteral;

collectionLiteral          : (COLLECTION ) OPEN geoLiteral ( COMMA geoLiteral )* CLOSE;

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

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
 grammar Uri;
//Naming convention 
//  ...
//Decoding encoding
//- within rule "resourcePath": special chars used in EDM.Strings must still be encoded when 
//  used as tokenizer input
//  e.g. .../Employees(id='Hugo%2FMueller')/EmployeeName <-- '/' must be encoded to '%2F' in "Hugo/Mueller"
//    but it must not be encoded before the EmployeeName

options {
    language = Java;
}

import UriLexerPart; //contain Lexer rules


test        : test_expr;
test_expr   : test_expr '*' test_expr
            | test_expr '+' test_expr
            | INT;

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
odataRelativeUri    : '$batch'                                                    # batchAlt
                    | '$entity' '?' eo=entityOptions                            # entityAlt
                    | '$metadata' ( '?' format )? ( FRAGMENT contextFragment )? # metadataAlt
                    | resourcePath ( '?' queryOptions )?                        # resourcePathAlt
                    ;

//;------------------------------------------------------------------------------
//; 1. Resource Path
//;------------------------------------------------------------------------------
                   
resourcePath        : '$all'        # allAlt
                    | crossjoin     # crossjoinAlt
                    | pathSegments  # pathSegmentsAlt
                    ;
crossjoin           : '$crossjoin' OPEN odi+=odataIdentifier ( COMMA odi+=odataIdentifier )* CLOSE;

pathSegments        : ps+=pathSegment ('/' ps+=pathSegment)* constSegment?;

pathSegment         : ns=namespace? odi=odataIdentifier nvl=nameValueOptList*;

nameValueOptList    : vo=valueOnly | nvl=nameValueList;
valueOnly           : OPEN (primitiveLiteral /*| enumX*/) CLOSE;
nameValueList       : OPEN kvp+=nameValuePair ( COMMA kvp+=nameValuePair )* CLOSE;
nameValuePair       : odi=odataIdentifier EQ (AT ali=odataIdentifier |  val1=primitiveLiteral /*| val2=enumX*/);

constSegment        : '/' (v=VALUE | c=COUNT | r=REF );

//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

queryOptions    : qo+=queryOption ( '&' qo+=queryOption )*;
queryOption     : systemQueryOption 
                | aliasAndValue 
                | customQueryOption  
                ;
             
entityOptions   : (eob+=entityOption '&' )* id=ID ( '&' eoa+=entityOption )*;
entityOption    : expand 
                | format
                | select
                | customQueryOption
                ;

systemQueryOption   : expand
                    | filter 
                    | format 
                    | ID
                    | inlinecount 
                    | orderby 
                    | search
                    | select 
                    | skip 
                    | SKIPTOKEN
                    | top ;

expand              : '$expand' EQ expandItemList;

expandItemList      : expandItem ( COMMA expandItem )*;

expandItem          : STAR ( '/' REF | OPEN LEVELS CLOSE )?
                    | expandPath expandPathExtension?;

expandPath          : ( namespace? odataIdentifier ) ( '/' namespace? odataIdentifier )*;
expandPathExtension : '/' REF   ( OPEN expandRefOption   ( SEMI expandRefOption   )* CLOSE )?
                    | '/' COUNT ( OPEN expandCountOption ( SEMI expandCountOption )* CLOSE )?
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

filter              : '$filter' EQ commonExpr;

orderby             : '$orderby' EQ orderbyItem ( COMMA orderbyItem )*;
orderbyItem         : commonExpr ( WS+ ( 'asc' | 'desc' ) )?;

//this is completly done in lexer grammer to avoid ambiguities with odataIdentifier and STRING
skip                : SKIP;
top                 : TOP;
format              : FORMAT;

inlinecount         : '$count' EQ booleanNonCase;

search              : '$search' searchSpecialToken;

searchSpecialToken  : { ((UriLexer) this.getInputStream().getTokenSource()).setInSearch(true); }
                      EQ WS* searchExpr
                      { ((UriLexer) this.getInputStream().getTokenSource()).setInSearch(false); }
                    ;

searchExpr          : 'NOT' WS+ searchExpr
                    | searchExpr WS+ ('AND' WS+)? searchExpr
                    | searchExpr WS+ 'OR' WS+ searchExpr
                    | searchPhrase
                    | searchWord
                    ;

searchPhrase        : SEARCHPHRASE;
searchWord          : SEARCHWORD;  

select              : '$select' EQ selectItem ( COMMA selectItem )*;
selectItem          : namespace? '*'
                    | (namespace? odataIdentifier nameValueOptList? ) ( '/' namespace? odataIdentifier nameValueOptList? )*
                    ;

aliasAndValue       : AT odataIdentifier EQ parameterValue;
parameterValue      : arrayOrObject
                      commonExpr
                    ;
customQueryOption   : { ((UriLexer) this.getInputStream().getTokenSource()).setINCustomOption(true); }
                      customName ( EQ customValue)?
                      { ((UriLexer) this.getInputStream().getTokenSource()).setINCustomOption(false); }
                    ;
customName          : 'CUSTOMNAME';
customValue         : 'CUSTOMVALUE';

//;------------------------------------------------------------------------------
//; 3. Context URL Fragments
//;------------------------------------------------------------------------------

contextFragment     : 'Collection($ref)'
                    | '$ref'
                    | 'Collection(Edm.EntityType)'
                    | 'Collection(Edm.ComplexType)'
                    | PRIMITIVETYPENAME
                    | 'collection' OPEN ( PRIMITIVETYPENAME | namespace odataIdentifier ) CLOSE
                    | namespace? odataIdentifier 
                      ( '/$deletedEntity'
                      | '/$link'
                      | '/$deletedLink'
                      | nameValueOptList? ( '/' namespace? odataIdentifier)* ( propertyList )? ( '/$delta'  )? ( entity )?
                      )?
                    ;              

propertyList         : OPEN propertyListItem ( COMMA propertyListItem )* CLOSE;
propertyListItem     : STAR           //; all structural properties
                     | propertyListProperty
                     ;
propertyListProperty : odataIdentifier ( '+' )? ( propertyList )?
                     | odataIdentifier ( '/' propertyListProperty )?
                     ;
                 
entity               : '/$entity';
//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------

// this expression part of the grammer is not similar to the ABNF because
// we had to introduced operator precesence witch is not reflected in the ABNF


commonExpr          : OPEN commonExpr CLOSE
                    | methodCallExpr
                    | unary WS+ commonExpr
                    | memberExpr
                    | commonExpr WS+ ('mul'|'div'|'mod') WS+ commonExpr 
                    | commonExpr WS+ ('add'|'sub') WS+ commonExpr 
                    | commonExpr WS+ ('gt'|'ge'|'lt'|'le'|'isof') WS+ commonExpr 
                    | commonExpr WS+ ('eq'|'ne') WS+ commonExpr 
                    | commonExpr WS+ ('and') WS+ commonExpr 
                    | commonExpr WS+ ('or') WS+ commonExpr 
                    | rootExpr                          //; $...
                    //| AT odataIdentifier                //; @...
                    | primitiveLiteral                  //; ...
                    ;

unary               : ('-'|'not') ;

rootExpr            : '$root/' pathSegments;

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


containsMethodCallExpr    : CONTAINS   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
startsWithMethodCallExpr  : STARTSWITH OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
endsWithMethodCallExpr    : ENDSWITH   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
lengthMethodCallExpr      : LENGTH     OPEN WS* commonExpr WS* CLOSE;
indexOfMethodCallExpr     : INDEXOF    OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
substringMethodCallExpr   : SUBSTRING  OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* ( COMMA WS* commonExpr WS* )? CLOSE;
toLowerMethodCallExpr     : TOLOWER    OPEN WS* commonExpr WS* CLOSE;
toUpperMethodCallExpr     : TOUPPER    OPEN WS* commonExpr WS* CLOSE;
trimMethodCallExpr        : TRIM       OPEN WS* commonExpr WS* CLOSE;
concatMethodCallExpr      : CONCAT     OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

yearMethodCallExpr                : 'year'               OPEN WS* commonExpr WS* CLOSE;
monthMethodCallExpr               : 'month'              OPEN WS* commonExpr WS* CLOSE;
dayMethodCallExpr                 : 'day'                OPEN WS* commonExpr WS* CLOSE;
hourMethodCallExpr                : 'hour'               OPEN WS* commonExpr WS* CLOSE;
minuteMethodCallExpr              : 'minute'             OPEN WS* commonExpr WS* CLOSE;
secondMethodCallExpr              : 'second'             OPEN WS* commonExpr WS* CLOSE;
fractionalsecondsMethodCallExpr   : 'fractionalseconds'  OPEN WS* commonExpr WS* CLOSE;
totalsecondsMethodCallExpr        : 'totalseconds'       OPEN WS* commonExpr WS* CLOSE;
dateMethodCallExpr                : 'date'               OPEN WS* commonExpr WS* CLOSE;
timeMethodCallExpr                : 'time'               OPEN WS* commonExpr WS* CLOSE;
totalOffsetMinutesMethodCallExpr  : 'totaloffsetminutes' OPEN WS* commonExpr WS* CLOSE;

minDateTimeMethodCallExpr         : 'mindatetime' OPEN WS* CLOSE;
maxDateTimeMethodCallExpr         : 'maxdatetime' OPEN WS* CLOSE;
nowMethodCallExpr                 : 'now' OPEN WS* CLOSE;

roundMethodCallExpr               : 'round'   OPEN WS* commonExpr WS* CLOSE;
floorMethodCallExpr               : 'floor'   OPEN WS* commonExpr WS* CLOSE;
ceilingMethodCallExpr             : 'ceiling' OPEN WS* commonExpr WS* CLOSE;

distanceMethodCallExpr            : 'geo.distance'   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
geoLengthMethodCallExpr           : 'geo.length'     OPEN WS* commonExpr WS* CLOSE;
intersectsMethodCallExpr          : 'geo.intersects' OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

isofExpr                          : 'isof' OPEN WS* ( commonExpr WS* COMMA WS* )? qualifiedtypename WS* CLOSE;
castExpr                          : 'cast' OPEN WS* ( commonExpr WS* COMMA WS* )? qualifiedtypename WS* CLOSE;

//;------------------------------------------------------------------------------
//; 5. JSON format for function parameters
//;------------------------------------------------------------------------------
//; Note: the query part of a URI needs to be partially percent-decoded before
//; applying these rules, see comment at the top of this file
//;------------------------------------------------------------------------------

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

//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------
POINT                   : '.';

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


geographyCollection        : GEOGRAPHYPREFIX  fullCollectionLiteral SQUOTE;
fullCollectionLiteral      : sridLiteral collectionLiteral;
collectionLiteral          : COLLECTION_CS OPEN geoLiteral ( COMMA geoLiteral )* CLOSE;
geoLiteral                 : collectionLiteral
                           | lineStringLiteral
                           | multipointLiteral
                           | multilineStringLiteral
                           | multipolygonLiteral
                           | pointLiteral
                           | polygonLiteral;

geographyLineString        : GEOGRAPHYPREFIX  fullLineStringLiteral SQUOTE;
fullLineStringLiteral      : sridLiteral lineStringLiteral;
lineStringLiteral          : LINESTRING_CS lineStringData;
lineStringData             : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;

geographyMultilineString   : GEOGRAPHYPREFIX  fullMultilineStringLiteral SQUOTE;
fullMultilineStringLiteral : sridLiteral multilineStringLiteral;
multilineStringLiteral     : MULTILINESTRING_CS OPEN ( lineStringData ( COMMA lineStringData )* )? CLOSE;

geographyMultipoint        : GEOGRAPHYPREFIX  fullMultipointLiteral SQUOTE;
fullMultipointLiteral      : sridLiteral multipointLiteral;
multipointLiteral          : MULTIPOINT_CS OPEN ( pointData ( COMMA pointData )* )? CLOSE ;

geographyMultipolygon      : GEOGRAPHYPREFIX  fullmultipolygonLiteral SQUOTE;
fullmultipolygonLiteral    : sridLiteral multipolygonLiteral;
multipolygonLiteral        : MULTIPOLYGON_CS OPEN ( polygonData ( COMMA polygonData )* )? CLOSE;

geographyPoint             : GEOGRAPHYPREFIX  fullpointLiteral SQUOTE;
fullpointLiteral           : sridLiteral pointLiteral;

pointLiteral               : POINT_CS pointData;
pointData                  : OPEN positionLiteral CLOSE;
positionLiteral            : (DECIMAL | INT ) WS (DECIMAL | INT );  //; longitude, then latitude

geographyPolygon           : GEOGRAPHYPREFIX fullPolygonLiteral SQUOTE;
fullPolygonLiteral         : sridLiteral polygonLiteral;
polygonLiteral             : POLYGON_CS polygonData;
polygonData                : OPEN ringLiteral ( COMMA ringLiteral )* CLOSE;
ringLiteral                : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;
                 

geometryCollection        : GEOMETRYPREFIX  fullCollectionLiteral      SQUOTE;
geometryLineString        : GEOMETRYPREFIX  fullLineStringLiteral      SQUOTE;
geometryMultilineString   : GEOMETRYPREFIX  fullMultilineStringLiteral SQUOTE;
geometryMultipoint        : GEOMETRYPREFIX  fullMultipointLiteral      SQUOTE;
geometryMultipolygon      : GEOMETRYPREFIX  fullmultipolygonLiteral    SQUOTE;
geometryPoint             : GEOMETRYPREFIX  fullpointLiteral           SQUOTE;
geometryPolygon           : GEOMETRYPREFIX  fullPolygonLiteral         SQUOTE;

sridLiteral               : SRID_CS EQ INT SEMI;



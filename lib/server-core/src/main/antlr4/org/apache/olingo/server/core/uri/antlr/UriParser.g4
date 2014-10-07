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

//------------------------------------------------------------------------------
// This grammar refers to the "odata-abnf-construction-rules.txt" Revision 517.
// URL: https://tools.oasis-open.org/version-control/browse/wsvn/odata/trunk/spec/ABNF/odata-abnf-construction-rules.txt?rev=517

// While contructing this grammar we tried to keep it close to the ABNF.
// However this is not really possible in order to support
// - percent decoding
// - operator precedence
// - having a context free grammar ( without java snipplets to add context)
// - generating the parser in different target languages
// Currently not supported are 
// - $search
// - geometry data
// - json data in url
//------------------------------------------------------------------------------


options {
    language   = Java;
    tokenVocab = UriLexer;
}

//;------------------------------------------------------------------------------
//; 0. URI
//;------------------------------------------------------------------------------

batchEOF            : BATCH EOF;

entityEOF           : vNS=namespace vODI=odataIdentifier;

metadataEOF         : METADATA EOF;

//;------------------------------------------------------------------------------
//; 1. Resource Path
//;------------------------------------------------------------------------------

//resourcePathEOF     : vlPS=pathSegments EOF;

crossjoinEOF        : CROSSJOIN OPEN WSP? vlODI+=odataIdentifier WSP? ( COMMA WSP? vlODI+=odataIdentifier  WSP?)* CLOSE EOF;

allEOF              : ALL;

pathSegmentEOF      : (pathSegment | constSegment) EOF;

pathSegments        : vlPS+=pathSegment (SLASH vlPS+=pathSegment)* (SLASH vCS=constSegment)?;

pathSegment         : vNS=namespace? vODI=odataIdentifier vlNVO+=nameValueOptList*;

nameValueOptList    : OPEN (vVO=commonExpr | vNVL=nameValueList)? CLOSE;
nameValueList       : WSP* vlNVP+=nameValuePair WSP* ( COMMA WSP* vlNVP+=nameValuePair  WSP*)* ;
nameValuePair       : vODI=odataIdentifier EQ (AT vALI=odataIdentifier | vCOM=commonExpr /*TODO | val2=enumX*/);

constSegment        : (vV=value | vC=count | vR=ref | vAll=allExpr | vAny=anyExpr);

count               : COUNT;
ref                 : REF;
value               : VALUE;

//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

queryOptions        : vlQO+=queryOption ( AMP vlQO+=queryOption )*;//TODO can this be removed

queryOption         : systemQueryOption; 

systemQueryOption   : expand
                    | filter 
                    | inlinecount 
                    | orderBy
                    | search
                    | select 
                    | skip 
                    | skiptoken
                    | top
                    ;

skiptoken           : SKIPTOKEN EQ REST;
expand              : EXPAND EQ expandItems;

expandItemsEOF      : expandItems EOF;
expandItems         : vlEI+=expandItem ( COMMA vlEI+=expandItem )*; 


expandItem          : vS=STAR ( SLASH vR=ref | OPEN LEVELS EQ ( vL=INT | vM=MAX)  CLOSE )?
                    | vEP=expandPath vEPE=expandPathExtension?;


expandPath          : vlPS+=pathSegment (SLASH vlPS+=pathSegment)*;

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

filterExpressionEOF : commonExpr EOF;

orderBy             : ORDERBY EQ orderList;

orderByEOF          : orderList EOF;

orderList           : vlOI+=orderByItem ( WSP* COMMA WSP* vlOI+=orderByItem )*;

orderByItem         : vC=commonExpr ( WSP ( vA=ASC | vD=DESC ) )?;

skip                : SKIP EQ INT;
top                 : TOP EQ INT;
//format              : FORMAT EQ ( ATOM | JSON | XML | PCHARS SLASH PCHARS);

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
selectEOF           : vlSI+=selectItem ( COMMA vlSI+=selectItem )*;

selectItem          : vlSS+=selectSegment ( SLASH vlSS+=selectSegment ) *;
selectSegment       : vNS=namespace? ( vODI=odataIdentifier | vS=STAR );

aliasAndValue       : vODI=ODATAIDENTIFIER EQ vV=parameterValue;

parameterValue      : commonExpr  //TODO json not supported arrayOrObject
                    ;

//;------------------------------------------------------------------------------
//; 3. Context URL Fragments
//;------------------------------------------------------------------------------

contextFragment     : REST; // the context fragment is only required on the client side

//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------

commonExpr          : OPEN commonExpr CLOSE                                                             #altPharenthesis
                    | vE1=commonExpr (WSP HAS WSP) vE2=commonExpr                                       #altHas
                    | methodCallExpr                                                                    #altMethod
                    | ( unary WSP ) commonExpr                                                          #altUnary
                    | anyExpr                                                                           #altAny
                    | allExpr                                                                           #altAll
                    | memberExpr                                                                        #altMember
                    | vE1=commonExpr (WSP vO=MUL WSP | WSP vO=DIV WSP | WSP vO=MOD WSP ) vE2=commonExpr #altMult
                    | vE1=commonExpr (WSP vO=ADD WSP | WSP vO=SUB WSP) vE2=commonExpr                   #altAdd
                    | vE1=commonExpr (WSP vO=GT WSP | WSP vO=GE WSP | WSP vO=LT WSP 
                                     | WSP vO=LE WSP ) vE2=commonExpr                                   #altComparism
                    | vE1=commonExpr (WSP vO=EQ_ALPHA WSP | WSP vO=NE WSP) vE2=commonExpr               #altEquality
                    | vE1=commonExpr (WSP AND WSP) vE2=commonExpr                                       #altAnd
                    | vE1=commonExpr (WSP OR WSP) vE2=commonExpr                                        #altOr
                    | rootExpr                                                                          #altRoot     // $...
                    | AT odataIdentifier                                                                #altAlias    // @...
                    | primitiveLiteral                                                                  #altLiteral  // ...
                    ;

unary               : (MINUS| NOT) ;

rootExpr            : ROOT vPs=pathSegments;

memberExpr          :  vIt=IT ( SLASH (vANY=anyExpr | vALL=allExpr))?
                    |  vIts=ITSLASH? vPs=pathSegments ( SLASH (vANY=anyExpr | vALL=allExpr))?;

anyExpr             : ANY_LAMDA OPEN WSP? ( vLV=odataIdentifier WSP? COLON WSP? vLE=commonExpr WSP? )? CLOSE;
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
                    | geoDistanceMethodCallExpr
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
                    | geoIntersectsMethodCallExpr
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

geoDistanceMethodCallExpr         : GEO_DISTANCE_WORD   WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;
geoLengthMethodCallExpr           : GEO_LENGTH_WORD     WSP? vE1=commonExpr WSP? CLOSE;
geoIntersectsMethodCallExpr       : GEO_INTERSECTS_WORD WSP? vE1=commonExpr WSP? COMMA WSP? vE2=commonExpr WSP? CLOSE;

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
                      WSP? COLON WSP? 
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


primitiveLiteral    : nullrule
                    | booleanNonCase
                    | DECIMAL   //includes double and single literals
                    | naninfinity
                    | INT       //includes int16/int32 and int64 literals
                    | BINARY  
                    | DATE
                    | DATETIMEOFFSET
                    | DURATION
                    | GUID
                    | string
                    | TIMEOFDAY
                    | enumLit
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

naninfinity         : NANINFINITY;

nullrule            : NULLVALUE;
booleanNonCase      : BOOLEAN | TRUE | FALSE;
string              : STRING;

enumLit             : vNS=namespace vODI=odataIdentifier vValues=STRING;
enumValues          : vlODI+=odataIdentifier ( COMMA vlODI+=odataIdentifier )*;

geographyCollection         : GEOGRAPHY  fullCollectionLiteral SQUOTE;
fullCollectionLiteral       : sridLiteral collectionLiteral;

collectionLiteral           : (COLLECTION ) OPEN geoLiteral ( COMMA geoLiteral )* CLOSE;

geoLiteral                  : collectionLiteral
                            | lineStringLiteral
                            | multipointLiteral
                            | multilineStringLiteral
                            | multipolygonLiteral
                            | pointLiteral
                            | polygonLiteral;

geographyLineString         : GEOGRAPHY  fullLineStringLiteral SQUOTE;
fullLineStringLiteral       : sridLiteral lineStringLiteral;
lineStringLiteral           : LINESTRING lineStringData;
lineStringData              : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;

geographyMultilineString    : GEOGRAPHY  fullMultilineStringLiteral SQUOTE;
fullMultilineStringLiteral  : sridLiteral multilineStringLiteral;
multilineStringLiteral      : MULTILINESTRING OPEN ( lineStringData ( COMMA lineStringData )* )? CLOSE;

geographyMultipoint         : GEOGRAPHY  fullMultipointLiteral SQUOTE;
fullMultipointLiteral       : sridLiteral multipointLiteral;
multipointLiteral           : MULTIPOINT OPEN ( pointData ( COMMA pointData )* )? CLOSE ;

geographyMultipolygon       : GEOGRAPHY  fullmultipolygonLiteral SQUOTE;
fullmultipolygonLiteral     : sridLiteral multipolygonLiteral;
multipolygonLiteral         : MULTIPOLYGON OPEN ( polygonData ( COMMA polygonData )* )? CLOSE;

geographyPoint              : GEOGRAPHY  fullpointLiteral SQUOTE;
fullpointLiteral            : sridLiteral pointLiteral;

pointLiteral                : GEO_POINT pointData;
pointData                   : OPEN positionLiteral CLOSE;

positionLiteral             : (DECIMAL | INT ) WSP (DECIMAL | INT );  //; longitude, then latitude


geographyPolygon            : GEOGRAPHY fullPolygonLiteral SQUOTE;
fullPolygonLiteral          : sridLiteral polygonLiteral;
polygonLiteral              : POLYGON polygonData;
polygonData                 : OPEN ringLiteral ( COMMA ringLiteral )* CLOSE;
ringLiteral                 : OPEN positionLiteral ( COMMA positionLiteral )* CLOSE;
                            

geometryCollection          : GEOMETRY  fullCollectionLiteral      SQUOTE;
geometryLineString          : GEOMETRY  fullLineStringLiteral      SQUOTE;
geometryMultilineString     : GEOMETRY  fullMultilineStringLiteral SQUOTE;
geometryMultipoint          : GEOMETRY  fullMultipointLiteral      SQUOTE;
geometryMultipolygon        : GEOMETRY  fullmultipolygonLiteral    SQUOTE;
geometryPoint               : GEOMETRY  fullpointLiteral           SQUOTE;
geometryPolygon             : GEOMETRY  fullPolygonLiteral         SQUOTE;

sridLiteral                 : SRID EQ INT SEMI;



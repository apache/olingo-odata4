grammar Uri;
//Naming convention 
//- Helper Rules ending with "Path" like "colNavigationPath" are expected to be used behind a 
//  slash ('/') e.g. "rule : entitySetName( '/' colNavigationPath)?;"
//- Helper Rules ending with "OnQual" like "colNavigationPathOnQual" are expected to be used behind a 
//  qualified identifiere.g. "colNavigationPath           : ...| qualified colNavigationPathOnQual;"
//- Helper Rules ending with "OnCast" like "singleNavigationPathOnCast" are expected to be used behind a 
//  cast to a typename e.g. "singleNavigationPathOnQual  : ... | entityTypeName '/' singleNavigationPathOnCast;"
//Decoding encoding
//- within rule "resourcePath": special chars used in EDM.Strings must still be encoded when 
//  used as tokenizer input
//  e.g. .../Employees(id='Hugo%2FMueller')/EmployeeName <-- '/' must be encodet to '%2F' in "Hugo/Mueller"
//    but it must not be encoded before the EmployeeName
//- within rules "entityOptions"/"format"/"queryOptions": 

options {
    language = Java;
}

import UriLexerPart; //contain Lexer rules



//;------------------------------------------------------------------------------
//; 0. URI
//;------------------------------------------------------------------------------

//ABNF odataUri and serviceRoot are currently not used here
//odataUri = serviceRoot [ odataRelativeUri ]  
//
//serviceRoot = ( "https" / "http" )                    ; Note: case-insensitive 
//              "://" host [ ":" port ]
//              "/" *( segment-nz "/" )



odataRelativeUri    : ( '$batch'                                //; Note: case-sensitive!
                        | '$entity' '?' entityOptions           //; Note: case-sensitive!
                        | '$metadata' ( '?' format )? ( FRAGMENT contextFragment )?
                        | resourcePath ( '?' queryOptions )?
                      ) EOF;

odataRelativeUriA   : odataRelativeUriB? EOF;
odataRelativeUriB   : '$batch'  # batch                         //; Note: case-sensitive!
                    | '$entity' '?' entityOptions # entityA     //; Note: case-sensitive!
                    | '$metadata' ( '?' format )? ( FRAGMENT contextFragment )? # metadata
                    | resourcePath ( '?' queryOptions )?  # resourcePathA
                    ;

//;------------------------------------------------------------------------------
//; 1. Resource Path
//;------------------------------------------------------------------------------
                   
resourcePath        : '$all'  # all
                    | crossjoin #crossjoinA
                    | pathSegments #pathSegmentsA
                    ;
crossjoin           : '$crossjoin' OPEN odataIdentifier ( COMMA odataIdentifier )* CLOSE;

pathSegments        : pathSegment ('/' pathSegment)* constSegment?;

pathSegment         : ns=namespace* odi=odataIdentifier fp=functionParameters? kp=keypredicates?;

pathOdataIdentifier : ODATAIDENTIFIER;

functionParameters  : OPEN ( fps+=functionParameter ( COMMA fps+=functionParameter )* )? CLOSE;
functionParameter   : odi=parameterName EQ ( ali=parameterAlias | val=primitiveLiteral );
parameterName       : odataIdentifier;
parameterAlias      : AT_ODATAIDENTIFIER;      

keypredicates       : simpleKey | compoundKey;
simpleKey           : OPEN keyPropertyValue CLOSE;
compoundKey         : OPEN kvp+=keyValuePair ( COMMA kvp+=keyValuePair )* CLOSE;
keyValuePair        : odi=odataIdentifier EQ val=keyPropertyValue;
keyPropertyValue    : primitiveLiteral;

               
constSegment        : '/' (value | count | ref );
count               : COUNT;
ref                 : REF;
value               : VALUE;

//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

queryOptions : queryOption ( '&' queryOption )*;
queryOption  : systemQueryOption 
             | aliasAndValue 
             | customQueryOption  
             ;
             
entityOptions : (entityOption '&' )* id ( '&' entityOption )*;
entityOption  : expand 
              | format
              | select
              | customQueryOption
              ;

id            : ID;

systemQueryOption   : expand
                    | filter 
                    | format 
                    | id
                    | inlinecount 
                    | orderby 
                    | search
                    | select 
                    | skip 
                    | SKIPTOKEN
                    | top ;

expand              : '$expand' EQ expandItemList;

expandItemList      : expandItem ( COMMA expandItem )*;

expandItem          : STAR ( '/' ref | OPEN LEVELS CLOSE )?
                    | expandPath expandPathExtension?;

expandPath          : ( namespace* odataIdentifier ) ( '/' namespace* odataIdentifier )*;
expandPathExtension : '/' ref   ( OPEN expandRefOption   ( SEMI expandRefOption   )* CLOSE )?
                    | '/' count ( OPEN expandCountOption ( SEMI expandCountOption )* CLOSE )?
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

inlinecount         : '$count' EQ BOOLEAN;

//search is not like the ABNF to support operator precedence
search              : '$search' searchSpecialToken;

searchSpecialToken  : { ((UriLexer) this.getInputStream().getTokenSource()).SetSWallowed(true); }
                      EQ WS* searchExpr
                      { ((UriLexer) this.getInputStream().getTokenSource()).SetSWallowed(false); }
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
selectItem          : namespace* '*'
                    | (namespace* odataIdentifier functionParameters? ) ( '/' namespace* odataIdentifier functionParameters? )*
                    ;

aliasAndValue       : parameterAlias EQ parameterValue;
parameterValue      : /*arrayOrObject*/
                      commonExpr;
customQueryOption   : { ((UriLexer) this.getInputStream().getTokenSource()).SetCUSTallowed(true); }
                      customName ( EQ customValue)?
                      { ((UriLexer) this.getInputStream().getTokenSource()).SetCUSTallowed(false); }
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
                    | 'collection' OPEN 
                      ( PRIMITIVETYPENAME 
                      | namespace odataIdentifier
                      ) CLOSE
                    | namespace* odataIdentifier ( '/$deletedEntity'
                                      | '/$link'
                                      | '/$deletedLink'
                                      | keypredicates? ( '/' namespace* odataIdentifier)* ( propertyList )? ( '/$delta'  )? ( entity )?
                                      )?
                    ;              

propertyList         : OPEN propertyListItem ( COMMA propertyListItem )* CLOSE;
propertyListItem     : STAR //; all structural properties
                     | propertyListProperty
                     ;
propertyListProperty : odataIdentifier ( '+' )? ( propertyList )?
                     | odataIdentifier ( '/' propertyListProperty )?
                     ;
                 
entity                  : '/$entity';
//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------

// this expression part of the grammer is not similar to the ABNF because
// we had to introduced operator precesence witch is not reflected in the ABNF


commonExpr          : OPEN commonExpr  CLOSE
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
                    | parameterAlias                    //; @...
                    | primitiveLiteral                  //; ...
                    ;

unary : ('-'|'not') ;

rootExpr            : '$root/' pathSegments;

memberExpr          : '$it' | '$it/'? pathSegments;

anyExpr                 : 'any' OPEN WS* /* [ lambdaVariableExpr BWS COLON BWS lambdaPredicateExpr ] WS* */ CLOSE;
allExpr                 : 'all' OPEN WS* /*   lambdaVariableExpr BWS COLON BWS lambdaPredicateExpr   WS* */ CLOSE;

methodCallExpr          : indexOfMethodCallExpr 
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


containsMethodCallExpr   : 'contains'   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
startsWithMethodCallExpr : 'startswith' OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
endsWithMethodCallExpr   : 'endswith'   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
lengthMethodCallExpr     : 'length'     OPEN WS* commonExpr WS* CLOSE;
indexOfMethodCallExpr    : 'indexof'    OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
substringMethodCallExpr  : 'substring'  OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* ( COMMA WS* commonExpr WS* ) CLOSE;
toLowerMethodCallExpr    : 'tolower'    OPEN WS* commonExpr WS* CLOSE;
toUpperMethodCallExpr    : 'toupper'    OPEN WS* commonExpr WS* CLOSE;
trimMethodCallExpr       : 'trim'       OPEN WS* commonExpr WS* CLOSE;
concatMethodCallExpr     : 'concat'     OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

yearMethodCallExpr               : 'year'               OPEN WS* commonExpr WS* CLOSE;
monthMethodCallExpr              : 'month'              OPEN WS* commonExpr WS* CLOSE;
dayMethodCallExpr                : 'day'                OPEN WS* commonExpr WS* CLOSE;
hourMethodCallExpr               : 'hour'               OPEN WS* commonExpr WS* CLOSE;
minuteMethodCallExpr             : 'minute'             OPEN WS* commonExpr WS* CLOSE;
secondMethodCallExpr             : 'second'             OPEN WS* commonExpr WS* CLOSE;
fractionalsecondsMethodCallExpr  : 'fractionalseconds'  OPEN WS* commonExpr WS* CLOSE;
totalsecondsMethodCallExpr       : 'totalseconds'       OPEN WS* commonExpr WS* CLOSE;
dateMethodCallExpr               : 'date'               OPEN WS* commonExpr WS* CLOSE;
timeMethodCallExpr               : 'time'               OPEN WS* commonExpr WS* CLOSE;
totalOffsetMinutesMethodCallExpr : 'totaloffsetminutes' OPEN WS* commonExpr WS* CLOSE;

minDateTimeMethodCallExpr : 'mindatetime' OPEN WS* CLOSE;
maxDateTimeMethodCallExpr : 'maxdatetime' OPEN WS* CLOSE;
nowMethodCallExpr         : 'now' OPEN WS* CLOSE;

roundMethodCallExpr   : 'round'   OPEN WS* commonExpr WS* CLOSE;
floorMethodCallExpr   : 'floor'   OPEN WS* commonExpr WS* CLOSE;
ceilingMethodCallExpr : 'ceiling' OPEN WS* commonExpr WS* CLOSE;

distanceMethodCallExpr   : 'geo.distance'   OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;
geoLengthMethodCallExpr  : 'geo.length'     OPEN WS* commonExpr WS* CLOSE;
intersectsMethodCallExpr : 'geo.intersects' OPEN WS* commonExpr WS* COMMA WS* commonExpr WS* CLOSE;

isofExpr            : 'isof' OPEN  WS*  commonExpr WS* COMMA WS*  qualifiedtypename WS* CLOSE;
castExpr            : 'cast' OPEN WS*  ( commonExpr WS* COMMA WS* ) qualifiedtypename WS* CLOSE;
//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------
POINT : '.';

qualifiedtypename       : PRIMITIVETYPENAME
                        | namespace odataIdentifier
                        | 'collection' OPEN 
                          ( PRIMITIVETYPENAME 
                            | namespace odataIdentifier
                          ) CLOSE
                        ;

namespace               : (odataIdentifier POINT)+;

odataIdentifier     : ODATAIDENTIFIER;
//;------------------------------------------------------------------------------
//; 7. Literal Data Values
//;------------------------------------------------------------------------------

/*TODO add missing values*/
primitiveLiteral    : nullrule
                    | BOOLEAN 
                    | DECIMAL  
                    | INT  
                    | BINARY  
                    | DATE
                    | DATETIMEOFFSET
                    //|duration
                    | string
                    | TIMEOFDAY
                    // enum
                    | parameterAlias
                    ;


nullrule            : NULLVALUE;// (SQUOTE qualifiedtypename SQUOTE)?;
string              : STRING;
/*TODO
enum                : qualifiedEnumTypeName SQUOTE enumValue SQUOTE
enumValue           : singleEnumValue *( COMMA singleEnumValue )
singleEnumValue     : enumerationMember / int64Value
*/

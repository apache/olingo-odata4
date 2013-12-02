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
lexer grammar UriLexer;

//;==============================================================================
// Mode "DEFAULT_MODE": Processes everything bevor the first '?' char
// On '?' the next mode "MODE_QUERY" is used
// The percent encoding rules a defined in RFC3986 ABNF rule "path-rootless" apply
//;==============================================================================
QM            : '?'  -> pushMode(MODE_QUERY); //first query parameter
AMP           : '&'  -> pushMode(MODE_QUERY); //more query parameters
STRING        : '\'' -> more, pushMode(MODE_ODATA_STRING);

GEOGRAPHY    : G E O G R A P H Y SQUOTE   -> pushMode(MODE_ODATA_GEO); //TODO make case insensitive
GEOMETRY     : G E O M E T R Y   SQUOTE   -> pushMode(MODE_ODATA_GEO);

//Letters for case insensitivity
fragment A    : 'A'|'a';
fragment B    : 'B'|'b';
fragment D    : 'D'|'d';
fragment E    : 'E'|'e';
fragment F    : 'F'|'f';
fragment G    : 'G'|'g';
fragment H    : 'H'|'h';
fragment I    : 'I'|'i';
fragment L    : 'L'|'l';
fragment M    : 'M'|'m';
fragment N    : 'N'|'n';
fragment O    : 'O'|'o';
fragment P    : 'P'|'p';
fragment R    : 'R'|'r';
fragment S    : 'S'|'s';
fragment T    : 'T'|'t';
fragment U    : 'U'|'u';
fragment Y    : 'Y'|'y';
fragment Z    : 'Z'|'z';

//special chars
OPEN          : '(' | '%28';
CLOSE         : ')' | '%29';
COMMA         : ',' | '%2C';
SLASH         : '/';
POINT         : '.';
AT            : '@';
EQ            : '=' ;
STAR          : '*';
SEMI          : ';';
FRAGMENT      : '#';

EQ_sq         : '='           -> type(EQ);
AMP_sq        : '&'           -> type(AMP), popMode;
fragment WS   : ( ' ' | '%09' | '%20' | '%09' );
WSP           : WS+;

//alpha stuff
fragment ALPHA                : 'a'..'z' | 'A'..'Z';
fragment ALPHA_A_TO_F         : 'a'..'f' | 'A'..'F';
fragment DIGIT                : '0'..'9';
fragment DIGITS               : DIGIT+;
fragment HEXDIG               : DIGIT | ALPHA_A_TO_F;
fragment ODI_LEADINGCHARACTER : ALPHA | '_';            //TODO; add Unicode characters from the categories L or Nl
fragment ODI_CHARACTER        : ALPHA | '_' | DIGIT;    //TODO; add Unicode characters from the categories L, Nl, Nd, Mn, Mc, Pc, or Cf

//helper for date/time values
fragment ONE_TO_NINE        : '1'..'9';
fragment ZERO_TO_FIFTYNINE  : ('0'..'5') DIGIT;
fragment FRACTIONALSECONDS  : DIGIT+;
fragment SECOND             : ZERO_TO_FIFTYNINE;
fragment MINUTE             : ZERO_TO_FIFTYNINE;
fragment HOUR               : ('0' | '1') DIGIT | '2' ( '0'..'3');
fragment DAY                : '0' '1'..'9' | ('1'|'2') DIGIT | '3' ('0'|'1');
fragment MONTH              : '0' ONE_TO_NINE | '1' ( '0' | '1' | '2' );
fragment YEAR               : ('-')? ( '0' DIGIT DIGIT DIGIT | ONE_TO_NINE DIGIT DIGIT DIGIT );

//tag start with $ 
BATCH         : '$batch';
ENTITY        : '$entity';
METADATA      : '$metadata';

ALL           : '$all';
CROSSJOIN     : '$crossjoin';

VALUE         : '$value';
REF           : '$ref';
COUNT         : '$count';

//inlined query parameters ( e.g. $skip)
SKIP_INLINE   : '$skip';
FILTER_INLINE : '$filter';
ORDERBY_INLINE: '$orderby';
SEARCH_INLINE : '$search'-> pushMode(MODE_SYSTEM_QUERY_SEARCH);



//rest
NULLVALUE     : 'null';

TRUE          : 'true';
FALSE         : 'false';
BOOLEAN       :  T R U E |  F A L S E; 
PLUS          : '+';
SIGN          : PLUS  | '%2B'   |'-';
INT           : SIGN? DIGITS;
DECIMAL       : INT '.' DIGITS ('e' SIGN?  DIGITS)?;

//primary types
BINARY                      : B I N A R Y SQUOTE (HEXDIG HEXDIG)* SQUOTE; 
DATE                        : D A T E                       SQUOTE YEAR '-' MONTH '-' DAY SQUOTE;
DATETIMEOFFSET              : D A T E T I M E O F F S E T   SQUOTE YEAR '-' MONTH '-' DAY T HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )? ( Z | SIGN HOUR ':' MINUTE ) SQUOTE;
fragment DUSECONDFRAG       : DIGITS ('.' DIGITS)? 'S';
fragment DUTIMEFRAG         : 'T' (   
                              ( DIGITS 'H' (DIGITS 'M')? DUSECONDFRAG?) 
                              | (DIGITS 'M' DUSECONDFRAG?) 
                              | DUSECONDFRAG
                            );
fragment DUDAYTIMEFRAG      : DIGITS 'D' DUTIMEFRAG? | DUTIMEFRAG;
DURATION                    : D U R A T I O N   SQUOTE '-'? 'P' DUDAYTIMEFRAG SQUOTE;
TIMEOFDAY                   : T I M E O F D A Y SQUOTE HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )? SQUOTE;
fragment GUIDVALUE          : HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG'-' 
                              HEXDIG HEXDIG HEXDIG HEXDIG  '-' 
                              HEXDIG HEXDIG HEXDIG HEXDIG  '-' 
                              HEXDIG HEXDIG HEXDIG HEXDIG  '-' 
                              HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG;
GUID                        : G U I D SQUOTE GUIDVALUE SQUOTE;

//expression tokens
ASC             : 'asc'; 
DESC            : 'desc';
MUL             : 'mul';
DIV             : 'div';
MOD             : 'mod';
ADD             : 'add';
SUB             : 'sub';
GT              : 'gt';
GE              : 'ge';
LT              : 'lt';
LE              : 'le';
EQ_ALPHA        : 'eq';
NE              : 'ne';
AND             : 'and';
OR              : 'or';
ISOF            : 'isof';
NOT             : 'not';
MINUS           :'-';
ROOT            : '$root/';
NANINFINITY     : 'NaN' | '-INF' | 'INF';

IMPLICIT_VARIABLE_EXPR  : '$it';
LEVELS               : '$levels';

CONTAINS_WORD             : 'contains(';
STARTSWITH_WORD           : 'startswith(';
ENDSWITH_WORD             : 'endswith(';
LENGTH_WORD               : 'length(';
INDEXOF_WORD              : 'indexof(';
SUBSTRING_WORD            : 'substring(';
TOLOWER_WORD              : 'tolower(';
TOUPPER_WORD              : 'toupper(';
TRIM_WORD                 : 'trim(';
CONCAT_WORD               : 'concat(';
YEAR_WORD                 : 'year(';
MONTH_WORD                : 'month(';
DAY_WORD                  : 'day(';
HOUR_WORD                 : 'hour(';
MINUTE_WORD               : 'minute(';
SECOND_WORD               : 'second(';
FRACTIONALSECONDS_WORD    : 'fractionalseconds(';
TOTALSECONDS_WORD         : 'totalseconds(';
DATE_WORD                 : 'date(';
TIME_WORD                 : 'time(';
TOTALOFFSETMINUTES_WORD   : 'totaloffsetminutes(';

MINDATETIME_WORD          : 'mindatetime(';
MAXDATETIME_WORD          : 'maxdatetime(';
NOW_WORD                  : 'now(';

ROUND_WORD                : 'round(';
FLOOR_WORD                : 'floor(';
CEILING_WORD              : 'ceiling(';

GEO_DISTANCE_WORD         : 'geo.distance(';
GEO_LENGTH_WORD           : 'geo.length(';
GEO_INTERSECTS_WORD       : 'geo.intersects(';
ISOF_WORD                 : 'isof(';
CAST_WORD                 : 'cast(';

COLLECTION_REF            : 'Collection($ref)';
COLLECTION_ENTITY_TYPE    : 'Collection(Edm.EntityType)';
COLLECTION_COMPLEX_TYPE   : 'Collection(Edm.ComplexType)';
COLLECTION                : 'Collection(' -> type(COLLECTION);

//used in fragment only
DELETED_ENTITY            : '$deletedEntity';
LINK                      : '$link';
DELETED_LINK              : '$deletedLink';
DELTA                     : '$delta';
//ENTITY_IN_FRAGMENT        : '/$entity';


LEVELSMAX                     : '$levels=max';


//ODI
ODATAIDENTIFIER             : ODI_LEADINGCHARACTER (ODI_CHARACTER)*;

//;==============================================================================
// Mode "QUERY": Processes everything between the first '?' and the '#' char
// On '?' the next mode "FRAGMENT" is used
// The percent encoding rules a defined in RFC3986 ABNF rule "query" apply
mode MODE_QUERY;
//;==============================================================================

FRAGMENT_q            : '#'       -> type(FRAGMENT);
FILTER              : '$filter'   -> pushMode(DEFAULT_MODE);
ORDERBY             : '$orderby'  -> pushMode(DEFAULT_MODE);
EXPAND              : '$expand'   -> pushMode(DEFAULT_MODE);
SELECT              : '$select'   -> pushMode(DEFAULT_MODE);
SKIP                : '$skip'     -> pushMode(DEFAULT_MODE);
SKIPTOKEN           : '$skiptoken' -> pushMode(MODE_SYSTEM_QUERY_REST_QCHAR_NO_AMP);

TOP                 : '$top'      -> pushMode(DEFAULT_MODE);
LEVELS_q            : '$levels'   -> type(LEVELS), pushMode(DEFAULT_MODE);
FORMAT              : '$format'   -> pushMode(MODE_SYSTEM_QUERY_PCHAR);
COUNT_q             : '$count'    -> type(COUNT), pushMode(DEFAULT_MODE);
REF_q               : '$ref'      -> type(REF);
VALUE_q             : '$value'      -> type(VALUE);

ID                  : '$id'-> pushMode(MODE_SYSTEM_QUERY_REST_QCHAR_NO_AMP);

SEARCH              : '$search'-> pushMode(MODE_SYSTEM_QUERY_SEARCH);

EQ_q          : '=' -> type(EQ);

AMP_q           : '&' -> type(AMP);

CUSTOMNAME    : ~[&=@$] ~[&=]*;
CUSTOMVALUE   : ~[&=]+;

//;==============================================================================
mode MODE_SYSTEM_QUERY_PCHAR;
//;==============================================================================

AMP_sqp   : '&' -> popMode,popMode;

//fragment EQ_sqp : '=';
fragment ALPHA_sqp          : 'a'..'z'|'A'..'Z';
fragment A_TO_F_sqp         : 'a'..'f'|'A'..'F';
fragment DIGIT_sqp          : '0'..'9';
fragment HEXDIG_sqp         : DIGIT_sqp | A_TO_F_sqp;
fragment PCT_ENCODED_sqp    : '%' HEXDIG_sqp HEXDIG_sqp;
fragment SUB_DELIMS_sqp     : '$' | '&' | '\'' | EQ_sqp | OTHER_DELIMS_sqp;
fragment OTHER_DELIMS_sqp   : '!' | '(' | ')' | '*' | '+' | ',' | ';';
fragment UNRESERVED_sqp     : ALPHA_sqp | DIGIT_sqp | '-' |'.' | '_' | '~'; 
fragment PCHAR              : UNRESERVED_sqp | PCT_ENCODED_sqp | SUB_DELIMS_sqp | ':' | '@'; 
fragment PCHARSTART         : UNRESERVED_sqp | PCT_ENCODED_sqp | '$' | '&' | '\'' | OTHER_DELIMS_sqp | ':' | '@'; 


ATOM : [Aa][Tt][Oo][Mm];
JSON : [Jj][Ss][Oo][Nn];
XML  : [Xx][Mm][Ll];

PCHARS : PCHARSTART PCHAR*;

SLASH_sqp : '/' -> type(SLASH);
EQ_sqp    : '=' -> type(EQ);

//;==============================================================================
mode MODE_SYSTEM_QUERY_REST_QCHAR_NO_AMP;
//;==============================================================================

AMP_sqr       : '&'     -> type(AMP), popMode;
FRAGMENT_sqr  : '#'     -> popMode;
/*
fragment ALPHA_sqr            : 'a'..'z'|'A'..'Z';
fragment A_TO_F_sqr           : 'a'..'f'|'A'..'F';
fragment DIGIT_sqr            : '0'..'9';
fragment HEXDIG_sqr           : DIGIT_sqr | A_TO_F_sqr;
fragment PCT_ENCODED_sqr      : '%' HEXDIG_sqr HEXDIG_sqr;
fragment UNRESERVED_sqr       : ALPHA_sqr | DIGIT_sqr | '-' |'.' | '_' | '~'; 
fragment OTHER_DELIMS_sqr     : '!' | '(' | ')' | '*' | '+' | ',' | ';';
fragment QCHAR_NO_AMP_sqr     : UNRESERVED_sqr | PCT_ENCODED_sqr | OTHER_DELIMS_sqr | ':' | '@' | '/' | '?' | '$' | '\'' | '=';
fragment QCHAR_NO_AMP_EQ_sqr  : UNRESERVED_sqr | PCT_ENCODED_sqr | OTHER_DELIMS_sqr | ':' | '@' | '/' | '?' | '$' | '\'' ;
*/

EQ_sqr        : '='     -> type(EQ);
REST          : ~[&#=] ~[&#]*;



//;==============================================================================
mode MODE_SYSTEM_QUERY_SEARCH;
//;==============================================================================

fragment ALPHA_sqc  : 'a'..'z'|'A'..'Z';
fragment WS_sqc   : ( SP_g | HTAB_g | '%20' | '%09' );
fragment DQUOTE              : '\u0022';
NOT_sqc             : 'NOT'   -> type(NOT);
AND_sqc             : 'AND'   -> type(AND);
OR_sqc              : 'OR'    -> type(OR);
EQ_sqc              : '='     -> type(EQ);

WSP_sqc             : WS_sqc+ -> type(WSP);


QUOTATION_MARK      : DQUOTE | '%22';

REF_sqc             : '$ref'  -> type(REF);

SEARCHWORD          : ALPHA_sqc+;
SEARCHPHRASE        : QUOTATION_MARK /*QCHAR_NO_AMP_DQUOTE+*/ ~[&"]* QUOTATION_MARK -> popMode;


//;==============================================================================
mode MODE_ODATA_STRING;
//;==============================================================================

fragment SQUOTE_s               : '\'';
STRING_s                        : ('\'\'' | ~[\u0027] )* SQUOTE_s -> type(STRING), popMode;

//;==============================================================================
mode MODE_ODATA_GEO;
//;==============================================================================

fragment C_g  : 'c'|'C';
fragment D_g  : 'd'|'D';
fragment E_g  : 'e'|'E';
fragment G_g  : 'g'|'G';
fragment H_g  : 'h'|'H';
fragment I_g  : 'i'|'I';
fragment L_g  : 'l'|'L';
fragment M_g  : 'm'|'M';
fragment N_g  : 'n'|'N';
fragment O_g  : 'o'|'O';
fragment P_g  : 'p'|'P';
fragment R_g  : 'r'|'R';
fragment S_g  : 's'|'S';
fragment T_g  : 't'|'T';
fragment U_g  : 'u'|'U';
fragment Y_g  : 'y'|'Y';

fragment SP_g   : ' ';//'\u0020'; // a simple space
fragment HTAB_g : '%09'; 
fragment WS_g   : ( ' ' | HTAB_g | '%20' | '%09' );

OPEN_g          : ('(' | '%28') -> type(OPEN);
CLOSE_g         : (')' | '%29') -> type(CLOSE);
COMMA_g         : (',' | '%2C') -> type(COMMA);
WSP_g           : WS_g+ -> type(WSP);
POINT_g         : '.' -> type(POINT);
AT_g            : '@' -> type(AT);
SEMI_g            : (';' | '%3B') -> type(SEMI);
EQ_g            : '=' -> type(EQ);

fragment DIGIT_g    : '0'..'9';
fragment DIGITS_g   : DIGIT_g+;
SIGN_g              : ('+' | '%2B' |'-') -> type(SIGN);
INT_g               : SIGN_g? DIGITS_g -> type(INT);
DECIMAL_g           : INT_g '.' DIGITS_g ('e' SIGN_g?  DIGITS_g)? -> type(DECIMAL);
COLLECTION_g          : C_g O_g L_g L_g E_g C_g T_g I_g O_g N_g -> type(COLLECTION);
LINESTRING          : L_g I_g N_g E_g S_g T_g R_g I_g N_g G_g ;
MULTILINESTRING     : M_g U_g L_g T_g I_g L_g I_g N_g E_g S_g T_g R_g I_g N_g G_g;
MULTIPOINT          : M_g U_g L_g T_g I_g P_g O_g I_g N_g T_g ;
MULTIPOLYGON        : M_g U_g L_g T_g I_g P_g O_g L_g Y_g G_g O_g N_g;
GEO_POINT           : P_g O_g I_g N_g T_g;
POLYGON             : P_g O_g L_g Y_g G_g O_g N_g ;

SRID                : S_g R_g I_g D_g;

SQUOTE              : '\''  -> popMode;

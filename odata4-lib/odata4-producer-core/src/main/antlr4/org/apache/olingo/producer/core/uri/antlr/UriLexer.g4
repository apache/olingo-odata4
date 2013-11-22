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
QM            : '?'  -> pushMode(MODE_QUERY);
STRING        : '\'' -> more, pushMode(MODE_ODATA_STRING);

fragment A    : 'A'|'a';
fragment B    : 'B'|'b';
fragment D    : 'D'|'d';
fragment E    : 'E'|'e';
fragment F    : 'F'|'f';
fragment G    : 'G'|'g';
fragment I    : 'I'|'i';
fragment L    : 'L'|'l';
fragment M    : 'M'|'m';
fragment N    : 'N'|'n';
fragment O    : 'O'|'o';
fragment R    : 'R'|'r';
fragment S    : 'S'|'s';
fragment T    : 'T'|'t';
fragment U    : 'U'|'u';
fragment Y    : 'Y'|'y';
fragment Z    : 'Z'|'z';

fragment ALPHA                : 'a'..'z' | 'A'..'Z';
fragment ALPHA_A_TO_F         : 'a'..'f' | 'A'..'F';
fragment DIGIT                : '0'..'9';
fragment DIGITS               : DIGIT+;
fragment HEXDIG               : DIGIT | ALPHA_A_TO_F;
fragment ODI_LEADINGCHARACTER : ALPHA | '_';            //TODO; add Unicode characters from the categories L or Nl
fragment ODI_CHARACTER        : ALPHA | '_' | DIGIT;    //TODO; add Unicode characters from the categories L, Nl, Nd, Mn, Mc, Pc, or Cf


BATCH         : '$batch';
ENTITY        : '$entity';
METADATA      : '$metadata';

ALL           : '$all';
CROSSJOIN     : '$crossjoin';

VALUE         : '$value';
REF           : '$ref';
COUNT         : '$count';

NULLVALUE     : 'null';

OPEN          : '(' | '%28';
CLOSE         : ')' | '%29';
COMMA         : ',' | '%2 SLASH_sqpC';
SLASH         : '/';
POINT         : '.';
AT            : '@';
EQ            : '=' ;

BOOLEAN       :  T R U E |  F A L S E; 
SIGN          : '+' | '%2B' |'-';
INT           : SIGN? DIGITS;
DECIMAL       : INT '.' DIGITS ('e' SIGN?  DIGITS)?;

//primary types
BINARY                      : ('X'| B I N A R Y) SQUOTE (HEXDIG HEXDIG)* SQUOTE; //TODO remove 'x' here and in unit tests

fragment ONE_TO_NINE        : '1'..'9';
fragment ZERO_TO_FIFTYNINE  : ('0'..'5') DIGIT;
fragment FRACTIONALSECONDS  : DIGIT+;
fragment SECOND             : ZERO_TO_FIFTYNINE;
fragment MINUTE             : ZERO_TO_FIFTYNINE;
fragment HOUR               : ('0' | '1') DIGIT | '2' ( '0'..'3');
fragment DAY                : '0' '1'..'9' | ('1'|'2') DIGIT | '3' ('0'|'1');
fragment MONTH              : '0' ONE_TO_NINE | '1' ( '0' | '1' | '2' );
fragment YEAR               : ('-')? ( '0' DIGIT DIGIT DIGIT | ONE_TO_NINE DIGIT DIGIT DIGIT );

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

ODATAIDENTIFIER             : ODI_LEADINGCHARACTER (ODI_CHARACTER)*;


//;==============================================================================
// Mode "QUERY": Processes everything between the first '?' and the '#' char
// On '?' the next mode "FRAGMENT" is used
// The percent encoding rules a defined in RFC3986 ABNF rule "query" apply
mode MODE_QUERY;
//;==============================================================================

FRAGMENT            : '#'         -> pushMode(MODE_FRAGMENT);
FILTER              : '$filter'   -> pushMode(MODE_SYSTEM_QUERY);
ORDERBY             : '$orderby'  -> pushMode(MODE_SYSTEM_QUERY);
EXPAND              : '$expand'   -> pushMode(MODE_SYSTEM_QUERY);
SELECT              : '$select'   -> pushMode(MODE_SYSTEM_QUERY);
SKIP                : '$skip'     -> pushMode(MODE_SYSTEM_QUERY);
TOP                 : '$top'      -> pushMode(MODE_SYSTEM_QUERY);
LEVELS              : '$levels'   -> pushMode(MODE_SYSTEM_QUERY);
FORMAT              : '$format'   -> pushMode(MODE_SYSTEM_QUERY_PCHAR);
COUNT_q             : '$count'    -> type(COUNT), pushMode(MODE_SYSTEM_QUERY);
REF_q               : '$ref'      -> type(REF);
VALUE_q             : '$value'      -> type(VALUE);

ID                  : '$id'-> pushMode(MODE_SYSTEM_QUERY_REST_QCHAR_NO_AMP);
SKIPTOKEN           : '$skiptoken' -> pushMode(MODE_SYSTEM_QUERY_REST_QCHAR_NO_AMP);
SEARCH              : '$search'-> pushMode(MODE_SYSTEM_QUERY_SEARCH);

GEOGRAPHY           : G_q   E_q   O_q   G_q   R_q   A_q   P_q   H_q   Y_q -> pushMode(MODE_ODATA_GEO);//TODO make case insensitive
GEOMETRY            : G_q   E_q   O_q   M_q   E_q   T_q   R_q   Y_q       -> pushMode(MODE_ODATA_GEO);

fragment A_q        : 'A'|'a';
fragment E_q        : 'E'|'e';
fragment G_q        : 'G'|'g';
fragment H_q        : 'H'|'h';
fragment M_q        : 'M'|'m';
fragment O_q        : 'O'|'o';
fragment P_q        : 'P'|'p';
fragment R_q        : 'R'|'r';
fragment S_q        : 'S'|'s';
fragment T_q        : 'T'|'t';
fragment Y_q        : 'Y'|'y';

fragment ALPHA_q    : 'a'..'z'|'A'..'Z';
fragment A_TO_F_q   : 'a'..'f'|'A'..'F';
fragment DIGIT_q    : '0'..'9';
fragment HEXDIG_q   : DIGIT_q | A_TO_F_q;

fragment PCT_ENCODED_q                : '%' HEXDIG_q HEXDIG_q;
fragment UNRESERVED_q                 : ALPHA_q | DIGIT_q | '-' |'.' | '_' | '~'; 
fragment OTHER_DELIMS_q               : '!' | '(' | ')' | '*' | '+' | ',' | ';';
fragment QCHAR_NO_AMP_q               : UNRESERVED_q | PCT_ENCODED_q | OTHER_DELIMS_q | ':' | '@' | '/' | '?' | '$' | '\'' | '=';
fragment QCHAR_NO_AMP_EQ_q            : UNRESERVED_q | PCT_ENCODED_q | OTHER_DELIMS_q | ':' | '@' | '/' | '?' | '$' | '\'';
fragment QCHAR_NO_AMP_EQ_AT_DOLLAR_q  : UNRESERVED_q | PCT_ENCODED_q | OTHER_DELIMS_q | ':' |       '/' | '?' |       '\'';

EQ_q          : '=' -> type(EQ);

AMP           : '&';

CUSTOMNAME    : QCHAR_NO_AMP_EQ_AT_DOLLAR_q QCHAR_NO_AMP_EQ_q*;
CUSTOMVALUE   : QCHAR_NO_AMP_EQ_q+;

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

fragment ALPHA_sqr            : 'a'..'z'|'A'..'Z';
fragment A_TO_F_sqr           : 'a'..'f'|'A'..'F';
fragment DIGIT_sqr            : '0'..'9';
fragment HEXDIG_sqr           : DIGIT_sqr | A_TO_F_sqr;
fragment PCT_ENCODED_sqr      : '%' HEXDIG_sqr HEXDIG_sqr;
fragment UNRESERVED_sqr       : ALPHA_sqr | DIGIT_sqr | '-' |'.' | '_' | '~'; 
fragment OTHER_DELIMS_sqr     : '!' | '(' | ')' | '*' | '+' | ',' | ';';
fragment QCHAR_NO_AMP_sqr     : UNRESERVED_sqr | PCT_ENCODED_sqr | OTHER_DELIMS_sqr | ':' | '@' | '/' | '?' | '$' | '\'' | '=';
fragment QCHAR_NO_AMP_EQ_sqr  : UNRESERVED_sqr | PCT_ENCODED_sqr | OTHER_DELIMS_sqr | ':' | '@' | '/' | '?' | '$' | '\'' ;


//REST : ~[&#]*;
AMP_sqr       : '&'     -> type(AMP), popMode;
EQ_sqr        : '='     -> type(EQ);
FRAGMENT_sqr  : '#'     -> popMode;
REST          : QCHAR_NO_AMP_EQ_sqr QCHAR_NO_AMP_sqr*;


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
mode MODE_SYSTEM_QUERY;
//;==============================================================================

fragment SQUOTE_sq       : '\'' -> type(SQUOTE);

STRING_sq       : SQUOTE_sq  ->  more, pushMode(MODE_ODATA_STRING);
GEOGRAPHY_sq    : G_sq E_sq O_sq G_sq R_sq A_sq P_sq H_sq Y SQUOTE_sq   -> type(GEOGRAPHY), pushMode(MODE_ODATA_GEO); //TODO make case insensitive
GEOMETRY_sq     : G_sq E_sq O_sq M_sq E_sq T_sq R_sq Y_sq   SQUOTE_sq   -> type(GEOMETRY),pushMode(MODE_ODATA_GEO);

fragment A_sq   : 'A'|'a';
fragment B_sq   : 'B'|'b';
fragment D_sq   : 'D'|'d';
fragment E_sq   : 'E'|'e';
fragment F_sq   : 'F'|'f';
fragment G_sq   : 'G'|'g';
fragment H_sq   : 'H'|'h';
fragment I_sq   : 'I'|'i';
fragment L_sq   : 'L'|'l';
fragment M_sq   : 'M'|'m';
fragment N_sq   : 'N'|'n';
fragment O_sq   : 'O'|'o';
fragment P_sq   : 'P'|'p';
fragment R_sq   : 'R'|'r';
fragment S_sq   : 'S'|'s';
fragment T_sq   : 'T'|'t';
fragment U_sq   : 'U'|'u';
fragment Y_sq   : 'Y'|'y';
fragment Z_sq   : 'Z'|'z';

fragment ALPHA_sq                 : 'a'..'z'|'A'..'Z';
fragment ALPHA_A_TO_F_sq          : 'a'..'f'|'A'..'F';
fragment DIGIT_sq                 : '0'..'9';
fragment DIGITS_sq                : DIGIT_sq+;
fragment HEXDIG_sq                : DIGIT_sq | ALPHA_A_TO_F_sq;
fragment ODI_LEADINGCHARACTER_sq  : ALPHA_sq | '_';   //TODO; plus Unicode characters from the categories L or Nl
fragment ODI_CHARACTER_sq         : ALPHA_sq | '_' | DIGIT_sq; //TODO; plus Unicode characters from the categories L, Nl, Nd, Mn, Mc, Pc, or Cf
fragment WS_sqr                   : ( SP_g | HTAB_g | '%20' | '%09' );

OPEN_sq         : ('(' | '%28') -> type(OPEN);
CLOSE_sq        : (')' | '%29') -> type(CLOSE);
COMMA_sq        : (',' | '%2C') -> type(COMMA);
SLASH_sq        : '/'           -> type(SLASH);
POINT_sq        : '.'           -> type(POINT);
AT_sq           : '@'           -> type(AT);
STAR            : '*';
SEMI_sq            : ';'        -> type(SEMI);
EQ_sq           : '='           -> type(EQ);
AMP_sq          : '&'           -> type(AMP), popMode;



WSP_sqr         : WS_sqr+ -> type(WSP);

NULLVALUE_sq    : 'null' -> type(NULLVALUE);
TRUE            : 'true';
FALSE           : 'false';
BOOLEAN_sq      : (T_sq R_sq U_sq E_sq  |  F_sq A_sq L_sq S_sq E_sq) -> type(BOOLEAN);
SIGN_sq         : ('+' | '%2B' |'-') -> type(SIGN);
INT_sq          : SIGN_sq? DIGITS_sq -> type(INT);
DECIMAL_sq      : INT_sq '.' DIGITS_sq ('e' SIGN_sq?  DIGITS_sq)? -> type(DECIMAL);
BINARY_sq       : ('X'| B_sq I_sq N_sq A_sq R_sq Y_sq) SQUOTE_sq (HEXDIG_sq HEXDIG_sq)* SQUOTE_sq -> type(BINARY);

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
 
fragment ONE_TO_NINE_sq         : '1'..'9';
fragment ZERO_TO_FIFTYNINE_sq   : ('0'..'5') DIGIT_sq;
fragment FRACTIONALSECONDS_sq   : DIGIT_sq+;
fragment SECOND_sq              : ZERO_TO_FIFTYNINE_sq;
fragment MINUTE_sq              : ZERO_TO_FIFTYNINE_sq;
fragment HOUR_sq                : ('0' | '1') DIGIT_sq | '2' ( '0'..'3');
fragment DAY_sq                 : '0' '1'..'9' | ('1'|'2') DIGIT_sq | '3' ('0'|'1');
fragment MONTH_sq               : '0' ONE_TO_NINE_sq | '1' ( '0' | '1' | '2' );
fragment YEAR_sq                : ('-')? ( '0' DIGIT_sq DIGIT_sq DIGIT_sq | ONE_TO_NINE DIGIT_sq DIGIT_sq DIGIT_sq );

DATE_sq                         : D_sq A_sq T_sq E_sq                                                     SQUOTE_sq YEAR_sq '-' MONTH_sq '-' DAY_sq SQUOTE_sq  -> type(DATE);
DATETIMEOFFSET_sq               : D_sq A_sq T_sq E_sq T_sq I_sq M_sq E_sq O_sq F_sq F_sq S_sq E_sq T_sq   SQUOTE_sq YEAR_sq '-' MONTH_sq '-' DAY_sq T_sq HOUR_sq ':' MINUTE_sq ( ':' SECOND_sq ( '.' FRACTIONALSECONDS_sq )? )? ( Z_sq | SIGN_sq HOUR_sq ':' MINUTE_sq ) SQUOTE_sq -> type(DATETIMEOFFSET);

fragment DUSECONDFRAG_sq        : DIGITS_sq ('.' DIGITS_sq)? 'S';
fragment DUTIMEFRAG_sq          : 'T' (   
                                ( DIGITS_sq 'H' (DIGITS_sq 'M')? DUSECONDFRAG_sq?) 
                                  | (DIGITS_sq 'M' DUSECONDFRAG_sq?) 
                                  | DUSECONDFRAG_sq
                                );
fragment DUDAYTIMEFRAG_sq      : DIGITS 'D' DUTIMEFRAG? | DUTIMEFRAG;

DURATION_sq                    : D_sq U_sq R_sq A_sq T_sq I_sq O_sq N_sq   SQUOTE_sq '-'? 'P' DUDAYTIMEFRAG_sq SQUOTE_sq -> type(DURATION);
TIMEOFDAY_sq                   : T_sq I_sq M_sq E_sq O_sq F_sq D_sq A_sq Y_sq SQUOTE_sq HOUR_sq ':' MINUTE_sq ( ':' SECOND_sq ( '.' FRACTIONALSECONDS_sq )? )? SQUOTE_sq -> type(TIMEOFDAY);

GUID_sq                        : G_sq U_sq I_sq D_sq SQUOTE_sq GUIDVALUE_sq SQUOTE_sq -> type(GUID);
fragment GUIDVALUE_sq          : HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq'-' 
                                 HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq  '-' 
                                 HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq  '-' 
                                 HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq  '-' 
                                 HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq HEXDIG_sq;

fragment PCT_ENCODED_sq        : '%' HEXDIG_sq HEXDIG_sq;
fragment UNRESERVED_sq         : ALPHA_sq | DIGIT_sq | '-' |'.' | '_' | '~'; 
fragment OTHER_DELIMS_sq       : '!' | '(' | ')' | '*' | '+' | ',' | ';';
fragment QCHAR_NO_AMP_sq       : UNRESERVED_sq | PCT_ENCODED_sq | OTHER_DELIMS_sq | ':' | '@' | '/' | '?' | '$' | '\'' | '=';


IMPLICIT_VARIABLE_EXPR  : '$it';
REF_sq                  : '$ref' -> type(REF);
LEVELS_sq               : '$levels' -> type(LEVELS);


CONTAINS_WORD                : 'contains(';
STARTSWITH_WORD              : 'startswith(';
ENDSWITH_WORD                : 'endswith(';
LENGTH_WORD                  : 'length(';
INDEXOF_WORD                 : 'indexof(';
SUBSTRING_WORD               : 'substring(';
TOLOWER_WORD                 : 'tolower(';
TOUPPER_WORD                 : 'toupper(';
TRIM_WORD                    : 'trim(';
CONCAT_WORD                  : 'concat(';
YEAR_WORD                    : 'year(';
MONTH_WORD                   : 'month(';
DAY_WORD                     : 'day(';
HOUR_WORD                    : 'hour(';
MINUTE_WORD                  : 'minute(';
SECOND_WORD                  : 'second(';
FRACTIONALSECONDS_WORD       : 'fractionalseconds(';
TOTALSECONDS_WORD            : 'totalseconds(';
DATE_WORD                    : 'date(';
TIME_WORD                    : 'time(';
TOTALOFFSETMINUTES_WORD      : 'totaloffsetminutes(';

MINDATETIME_WORD             : 'mindatetime(';
MAXDATETIME_WORD             : 'maxdatetime(';
NOW_WORD                     : 'now(';

ROUND_WORD                   : 'round(';
FLOOR_WORD                   : 'floor(';
CEILING_WORD                 : 'ceiling(';

GEO_DISTANCE_WORD            : 'geo.distance(';
GEO_LENGTH_WORD              : 'geo.length(';
GEO_INTERSECTS_WORD          : 'geo.intersects(';
ISOF_WORD                    : 'isof(';
CAST_WORD                    : 'cast(';



LEVELSMAX                     : '$levels=max';
SKIP_sq                       : '$skip'   -> type(SKIP);
COUNT_sq                      : '$count'  -> type(COUNT);
FILTER_sq                     : '$filter' -> type(FILTER);
SEARCH_sq                     : '$search' -> type(SEARCH), pushMode(MODE_SYSTEM_QUERY_SEARCH);
//IRI_IN_QUERY                   : /*EQ*/ QCHAR_NO_AMP_sq*;
ODATAIDENTIFIER_sq            : ODI_LEADINGCHARACTER_sq (ODI_CHARACTER_sq)* ->type(ODATAIDENTIFIER);



//;==============================================================================
// Mode "QUERY": Processes everything after the '#' char
// The percent encoding rules a defined in RFC3986 ABNF rule "fragment" apply
//;==============================================================================
mode MODE_FRAGMENT;

TMP_FRAGMENT : 'TMP_FRAGMENT';

//;==============================================================================
//;==============================================================================
mode MODE_ODATA_STRING;//2


fragment COMMA_s                : ',' | '%2C';
fragment ALPHA_s                : 'a'..'z'|'A'..'Z';
fragment ALPHA_A_TO_F_s         : 'a'..'f'|'A'..'F';
fragment DIGIT_s                : '0'..'9';
fragment HEXDIG_s               : DIGIT_s | ALPHA_A_TO_F_s;
fragment UNRESERVED_s           : ALPHA_s | DIGIT_s | '-' |'.' | '_' | '~'; 
fragment OTHER_DELIMS_s         : '!' | '(' | ')' | '*' | '+' | COMMA_s | ';';
fragment PCTENCODEDnoSQUOTE_s   : '%' ( '0'|'1'|'3'..'9' | ALPHA_A_TO_F_s ) HEXDIG_s | '%' '2' ( '0'..'6'|'8'|'9' | ALPHA_A_TO_F_s );
fragment PCHARnoSQUOTE_s        : UNRESERVED_s| PCTENCODEDnoSQUOTE_s |  OTHER_DELIMS_s | '$' | '&' | '=' | ':' | '@';
fragment SQUOTE_s               : '\'';
STRING_s                        : ('\'\'' | PCHARnoSQUOTE_s )* SQUOTE_s -> type(STRING), popMode;

//;==============================================================================
//;==============================================================================
mode MODE_ODATA_GEO;

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
fragment WS_g   : ( SP_g | HTAB_g | '%20' | '%09' );

OPEN_g          : ('(' | '%28') -> type(OPEN);
CLOSE_g         : (')' | '%29') -> type(CLOSE);
COMMA_g         : (',' | '%2C') -> type(COMMA);
WSP             : WS_g+;
POINT_g         : '.' -> type(POINT);
AT_g            : '@' -> type(AT);
SEMI            : (';' | '%3B');
EQ_g            : '=' -> type(EQ);

fragment DIGIT_g    : '0'..'9';
fragment DIGITS_g   : DIGIT_g+;
SIGN_g              : ('+' | '%2B' |'-') -> type(SIGN);
INT_g               : SIGN_g? DIGITS_g -> type(INT);
DECIMAL_g           : INT_g '.' DIGITS_g ('e' SIGN_g?  DIGITS_g)? -> type(DECIMAL);
COLLECTION          : C_g O_g L_g L_g E_g C_g T_g I_g O_g N_g ;
LINESTRING          : L_g I_g N_g E_g S_g T_g R_g I_g N_g G_g ;
MULTILINESTRING     : M_g U_g L_g T_g I_g L_g I_g N_g E_g S_g T_g R_g I_g N_g G_g;
MULTIPOINT          : M_g U_g L_g T_g I_g P_g O_g I_g N_g T_g ;
MULTIPOLYGON        : M_g U_g L_g T_g I_g P_g O_g L_g Y_g G_g O_g N_g;
GEO_POINT           : P_g O_g I_g N_g T_g;
POLYGON             : P_g O_g L_g Y_g G_g O_g N_g ;

SRID                : S_g R_g I_g D_g;

SQUOTE              : '\''  -> popMode;

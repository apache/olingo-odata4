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
 lexer grammar UriLexerPart;

@lexer::members {
//custom lexer members -->  
    boolean debug = false;
    private void out(String out) { if(debug) { System.out.println(out); } };
    
    
    boolean        bInSearch = false;
    public boolean inSearch() { /*out("?SW?="+bInSearch);*/ return bInSearch; };
    public void    setInSearch(boolean value) { bInSearch=value; /*out("SW=set to "+ bInSearch);*/ };

    //testing
    boolean        bInCustomOption = false;
    public boolean inCustomOption() { /*out("?CUST?="+bInCustomOption);*/ return bInCustomOption; };
    public void    setINCustomOption(boolean value) { bInCustomOption=value; /*out("CUST=set to "+ bInCustomOption);*/ };

    
//<-- custom lexer members
}


//;------------------------------------------------------------------------------
//; 0. URI
//;------------------------------------------------------------------------------

FRAGMENT    : '#';

COUNT       : '$count';
REF         : '$ref';
VALUE       : '$value';


//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------

SKIP        : '$skip' EQ DIGIT+;
TOP         : '$top'  EQ DIGIT+;

LEVELS      : '$levels' EQ ( DIGIT+ | 'max' );

FORMAT      : '$format' EQ
               ( 'atom'
               | 'json'
               | 'xml'
               | PCHAR+ '/' PCHAR+ //; <a data service specific value indicating a
            );                     //; format specific to the specific data service> or
                                   //; <An IANA-defined [IANA-MMT] content type>

ID          : '$id' EQ QCHAR_NO_AMP+;

SKIPTOKEN   : '$skiptoken' EQ QCHAR_NO_AMP+;


//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------

IMPLICIT_VARIABLE_EXPR  : '$it';

CONTAINS                : 'contains';
STARTSWITH              : 'startswith';
ENDSWITH                : 'endswith';
LENGTH                  : 'length';
INDEXOF                 : 'indexof';
SUBSTRING               : 'substring';
TOLOWER                 : 'tolower';
TOUPPER                 : 'toupper';
TRIM                    : 'trim';
CONCAT                  : 'concat';


//;------------------------------------------------------------------------------
//; 5. JSON format for function parameters
//;------------------------------------------------------------------------------

QUOTATION_MARK                : DQUOTE | '%22';

fragment CHAR_IN_JSON         : QCHAR_UNESCAPED  
                              | QCHAR_JSON_SPECIAL
                              | ESCAPE 
                              ( QUOTATION_MARK 
                                | ESCAPE
                                | ( '/' | '%2F' ) //; solidus         U+002F
                                | 'b'             //; backspace       U+0008                
                                | 'f'             //; form feed       U+000C
                                | 'n'             //; line feed       U+000A
                                | 'r'             //; carriage return U+000D
                                | 't'             //; tab             U+0009
                                | 'u' HEXDIG HEXDIG HEXDIG HEXDIG //;                 U+XXXX
                              );

fragment  QCHAR_JSON_SPECIAL  : SP | ':' | '{' | '}' | '[' | ']'; //; some agents put these unencoded into the query part of a URL

fragment  ESCAPE              : '\\' | '%5C'; //; reverse solidus U+005C

//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------

fragment ODI_LEADINGCHARACTER : ALPHA | '_';         //TODO; plus Unicode characters from the categories L or Nl
fragment ODI_CHARACTER        : ALPHA | '_' | DIGIT; //TODO; plus Unicode characters from the categories L, Nl, Nd, Mn, Mc, Pc, or Cf


PRIMITIVETYPENAME             : ('Edm.')? 
                                ( 'Binary'
                                  | 'Boolean'
                                  | 'Byte'
                                  | 'Date' 
                                  | 'DateTimeOffset'
                                  | 'Decimal'
                                  | 'Double'
                                  | 'Duration' 
                                  | 'Guid' 
                                  | 'Int16'
                                  | 'Int32'
                                  | 'Int64'
                                  | 'SByte'
                                  | 'Single'
                                  | 'Stream'
                                  | 'String'
                                  | 'TimeOfDay'
                                  | ABSTRACTSPATIALTYPENAME ( CONCRETESPATIALTYPENAME )?
                                );

fragment ABSTRACTSPATIALTYPENAME  : 'Geography'
                                  | 'Geometry'
                                  ;

fragment CONCRETESPATIALTYPENAME  : 'Collection'
                                  | 'LineString'
                                  | 'MultiLineString'
                                  | 'MultiPoint'
                                  | 'MultiPolygon'
                                  | 'Point'
                                  | 'Polygon'
                                  ;

//;------------------------------------------------------------------------------
//; 7. Literal Data Values
//;------------------------------------------------------------------------------

NULLVALUE                 : 'null';
fragment SQUOTEinSTRING   : SQUOTE SQUOTE ;

BINARY                    : ('X'| B I N A R Y) SQUOTE (HEXDIG HEXDIG)* SQUOTE;

TRUE                      : 'true';
FALSE                     : 'false';
BOOLEAN                   :  T R U E 
                          |  F A L S E 
                          ; 

fragment DIGITS           : DIGIT+;

INT                       : SIGN? DIGITS;
DECIMAL                   : INT '.' DIGITS ('e' SIGN?  DIGITS)?;

NANINFINITY               : 'NaN' | '-INF' | 'INF';

// --------------------- DATE ---------------------
DATE                     : DATETOKEN SQUOTE DATETOKEN_VALUE SQUOTE;
fragment DATETOKEN       : D A T E;
fragment DATETOKEN_VALUE : YEAR '-' MONTH '-' DAY;

// --------------------- DATETIMEOFFSET ---------------------
DATETIMEOFFSET                : DATEOFFSETTOKEN SQUOTE DATETIMEOFFSET_VALUE SQUOTE;
fragment DATEOFFSETTOKEN      : D A T E T I M E O F F S E T;  
fragment DATETIMEOFFSET_VALUE : YEAR '-' MONTH '-' DAY T HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )? ( Z | SIGN HOUR ':' MINUTE );

// --------------------- DURATION ---------------------
DURATION                      : DURATIONTOKEN SQUOTE DURATIONVALUE SQUOTE;
fragment DURATIONTOKEN        : D U R A T I O N;

fragment DUDAYFRAG            : DIGITS 'D';
fragment DUHOURFRAG           : DIGITS 'H';
fragment DUMINUTEFRAG         : DIGITS 'M';
fragment DUSECONDFRAG         : DIGITS ('.' DIGITS)? 'S';
fragment DUTIMEFRAG           : 'T' ( 
                                      ( DUHOURFRAG DUMINUTEFRAG? DUSECONDFRAG?) 
                                      | (DUMINUTEFRAG DUSECONDFRAG?) 
                                      | DUSECONDFRAG
                                    );
fragment DUDAYTIMEFRAG        : DUDAYFRAG DUTIMEFRAG? | DUTIMEFRAG;
                    
fragment DURATIONVALUE        : '-'? 'P' DUDAYTIMEFRAG;

// --------------------- TIMEOFDAY ---------------------
TIMEOFDAY                     : TIMEOFDAYTO SQUOTE TIMEOFDAYVALUE SQUOTE;
fragment TIMEOFDAYTO          : T I M E O F D A Y;  
fragment TIMEOFDAYVALUE       : HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )?;

// --------------------- date helper ---------------------

fragment ONEtoNINE          : '1'..'9';                   

fragment YEAR               : ('-')? ( '0' DIGIT DIGIT DIGIT | ONEtoNINE DIGIT DIGIT DIGIT );

fragment MONTH              : '0' ONEtoNINE
                            | '1' ( '0' | '1' | '2' )
                            ;

fragment DAY                : '0' ONEtoNINE
                            | ('1'|'2') DIGIT
                            | '3' ('0'|'1')
                            ;

fragment HOUR               : ('0' | '1') DIGIT
                            | '2' ( '0'..'3')
                            ;

fragment MINUTE             : ZEROtoFIFTYNINE;
fragment SECOND             : ZEROtoFIFTYNINE;
fragment FRACTIONALSECONDS  : DIGIT+;
fragment ZEROtoFIFTYNINE    : ('0'..'5') DIGIT;


//;------------------------------------------------------------------------------
//; 9. Punctuation
//;------------------------------------------------------------------------------

WS        : ( SP | HTAB | '%20' | '%09' );

AT        : '@' | '%40';
COLON     : ':' | '%3A';
COMMA     : ',' | '%2C';
EQ        :  '=';
SIGN      : '+' | '%2B' |'-';
SEMI      : ';' | '%3B';
STAR      : '*';
SQUOTE    : '\'' | '%27';
OPEN      : '(' | '%28';
CLOSE     : ')' | '%29';


//;------------------------------------------------------------------------------
//; A. URI syntax [RFC3986]
//;------------------------------------------------------------------------------

QM                          :  '?' -> channel(HIDDEN);

fragment PCHAR              : UNRESERVED | PCT_ENCODED | SUB_DELIMS | ':' | '@'; 

fragment PCHARnoSQUOTE      : UNRESERVED| PCTENCODEDnoSQUOTE |  OTHER_DELIMS | '$' | '&' | EQ | ':' | '@';

fragment PCT_ENCODED        : '%' HEXDIG HEXDIG;
fragment UNRESERVED         : ALPHA | DIGIT | '-' |'.' | '_' | '~'; 
fragment SUB_DELIMS         : '$' | '&' | '\'' | '=' | OTHER_DELIMS;
fragment OTHER_DELIMS       : '!' | '(' | ')' | '*' | '+' | ',' | ';';


fragment PCTENCODEDnoSQUOTE :  '%' ( '0'|'1'|'3'..'9' | AtoF ) HEXDIG
                            | '%' '2' ( '0'..'6'|'8'|'9' | AtoF )
                            ;

fragment QCHAR_NO_AMP               : UNRESERVED | PCT_ENCODED | OTHER_DELIMS | ':' | '@' | '/' | '?' | '$' | '\'' | EQ;
fragment QCHAR_NO_AMP_EQ            : UNRESERVED | PCT_ENCODED | OTHER_DELIMS | ':' | '@' | '/' | '?' | '$' | '\'';
fragment QCHAR_NO_AMP_EQ_AT_DOLLAR  : UNRESERVED | PCT_ENCODED | OTHER_DELIMS | ':' |       '/' | '?' |       '\'';

fragment QCHAR_UNESCAPED            : UNRESERVED | PCT_ENCODED_UNESCAPED | OTHER_DELIMS | ':' | '@' | '/' | '?' | '$' | '\'' | EQ;
fragment PCT_ENCODED_UNESCAPED      : '%' ( '0' | '1' |   '3' | '4' |   '6' | '8' | '9' | 'A'..'F' ) HEXDIG 
                                    | '%' '2' ( '0' | '1' |   '3' | '4' | '5' | '6' | '7' | '8' | '9' | 'A'..'F' ) 
                                    | '%' '5' ( DIGIT | 'A' | 'B' |   'D' | 'E' | 'F' );

fragment QCHAR_NO_AMP_DQUOTE        : QCHAR_UNESCAPED
                                    | ESCAPE ( ESCAPE | QUOTATION_MARK );

//;------------------------------------------------------------------------------
//; B. IRI syntax [RFC3987]
//;------------------------------------------------------------------------------
//; Note: these are over-generous stubs, for the actual patterns refer to RFC3987
//;------------------------------------------------------------------------------

//IRI_IN_HEADER           : ( VCHAR | obs-text )+;

//;------------------------------------------------------------------------------
//; C. ABNF core definitions [RFC5234]
//;------------------------------------------------------------------------------

fragment    ALPHA     : 'a'..'z'|'A'..'Z';
fragment    HEXDIG    : DIGIT | AtoF;
fragment    AtoF      : 'a'..'f'|'A'..'F';

//fragment    VCHAR   : '\u0021'..'\u007E';

DIGIT                 : '0'..'9';
DQUOTE                : '\u0022';
SP                    : ' ';//'\u0020'; // a simple space
HTAB                  : '%09'; 
            
//;------------------------------------------------------------------------------
//; End of odata-abnf-construction-rules
//;------------------------------------------------------------------------------


//;------------------------------------------------------------------------------
//; HELPER
//;------------------------------------------------------------------------------

fragment A  : 'a'|'A';
fragment B  : 'b'|'B';
fragment D  : 'd'|'D';
fragment E  : 'e'|'E';
fragment F  : 'f'|'F';
fragment I  : 'i'|'I';
fragment L  : 'l'|'L';
fragment M  : 'm'|'M';
fragment N  : 'n'|'N';
fragment O  : 'o'|'O';
fragment R  : 'r'|'R';
fragment S  : 's'|'S';
fragment T  : 't'|'T';
fragment U  : 'u'|'U';
fragment Y  : 'y'|'Y';
fragment Z  : 'z'|'Z';


/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
STRING              : SQUOTE (SQUOTEinSTRING | PCHARnoSQUOTE )* SQUOTE; 

//conflict with ODATAIDENTIFIER, fixed with predicate
SEARCHWORD          : ALPHA+ { inSearch() }?;       //; Actually: any character from the Unicode categories L or Nl, 
                                                    //; but not the words AND, OR, and NOT which are match far above

//conflict with STRING_IN_JSON, fixed with predicate
SEARCHPHRASE: QUOTATION_MARK QCHAR_NO_AMP_DQUOTE+ QUOTATION_MARK { inSearch() }?;

//TODO fix conflict
//CUSTOMNAME        : QCHAR_NO_AMP_EQ_AT_DOLLAR QCHAR_NO_AMP_EQ* { IsCUSTallowed() }?;
//CUSTOMVALUE       : QCHAR_NO_AMP+ { IsCUSTallowed() }?;

ODATAIDENTIFIER     : ODI_LEADINGCHARACTER (ODI_CHARACTER)*;

AT_ODATAIDENTIFIER  : AT ODATAIDENTIFIER;

STRING_IN_JSON      : QUOTATION_MARK CHAR_IN_JSON* QUOTATION_MARK;
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
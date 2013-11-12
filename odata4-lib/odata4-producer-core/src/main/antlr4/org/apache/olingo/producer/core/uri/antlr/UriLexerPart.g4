lexer grammar UriLexerPart;

@lexer::members {
//custom lexer members -->  
    boolean debug = false;
    private void out(String out) { if(debug) { System.out.println(out); } };
    
    
    boolean SWallowed = false;
    public boolean IsSWallowed() { out("?SW?="+SWallowed); return SWallowed; };
    public void SetSWallowed(boolean value) { SWallowed=value; out("SW=set to "+ SWallowed); };
    
    boolean CUSTallowed = false;
    public boolean IsCUSTallowed() { out("?CUST?="+CUSTallowed); return CUSTallowed; };
    public void SetCUSTallowed(boolean value) { SWallowed=value; out("CUST=set to "+ CUSTallowed); };
    
//<-- custom lexer members
}

//;------------------------------------------------------------------------------
//; 0. URI
//;------------------------------------------------------------------------------

FRAGMENT:'#';

COUNT   : '$count';
REF     : '$ref';
VALUE   : '$value';

//;------------------------------------------------------------------------------
//; 2. Query Options
//;------------------------------------------------------------------------------


SKIP                : '$skip' EQ DIGIT+;
TOP                 : '$top'  EQ DIGIT+;

LEVELS              : '$levels' EQ ( DIGIT+ | 'max' );

FORMAT              : '$format' EQ
                    ( 'atom'
                      | 'json'
                      | 'xml'
                      | PCHAR+ '/' PCHAR+ //; <a data service specific value indicating a
                    );                       //; format specific to the specific data service> or
                                            //; <An IANA-defined [IANA-MMT] content type>
ID                  : '$id' EQ QCHAR_NO_AMP+;

SKIPTOKEN           : '$skiptoken' EQ QCHAR_NO_AMP+;


//;------------------------------------------------------------------------------
//; 4. Expressions
//;------------------------------------------------------------------------------


ImplicitVariableExpr    : '$it';

INDEXOF: 'indexof';

//;------------------------------------------------------------------------------
//; 5. JSON format for function parameters
//;------------------------------------------------------------------------------
//; Note: the query part of a URI needs to be partially percent-decoded before
//; applying these rules, see comment at the top of this file
//;------------------------------------------------------------------------------

SEARCHPHRASE        : QUOTATION_MARK QCHAR_NO_AMP_DQUOTE+ QUOTATION_MARK;


fragment QUOTATION_MARK  : DQUOTE | '%22';

fragment ESCAPE : '\\' | '%5C' ;    //     ; reverse solidus U+005C
//;------------------------------------------------------------------------------
//; 6. Names and identifiers
//;------------------------------------------------------------------------------

fragment IDENTIFIERLEADINGCHARACTER  : ALPHA | '_';         //TODO; plus Unicode characters from the categories L or Nl
fragment IDENTIFIERCHARACTER         : ALPHA | '_' | DIGIT; //TODO; plus Unicode characters from the categories L, Nl, Nd, Mn, Mc, Pc, or Cf


PRIMITIVETYPENAME           : ('Edm.')? ( 'Binary'

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
                                )
                            ;

ABSTRACTSPATIALTYPENAME : 'Geography'
                        | 'Geometry';
CONCRETESPATIALTYPENAME : 'Collection'
                        | 'LineString'
                        | 'MultiLineString'
                        | 'MultiPoint'
                        | 'MultiPolygon'
                        | 'Point'
                        | 'Polygon';

//;------------------------------------------------------------------------------
//; 7. Literal Data Values
//;------------------------------------------------------------------------------

NULLVALUE  : 'null';
fragment SQUOTEinSTRING : SQUOTE SQUOTE ;

BINARY              : ('X'| B I N A R Y) SQUOTE BINARYVALUE SQUOTE;
fragment BINARYVALUE: (HEXDIG HEXDIG)*;

BOOLEAN             : T R U E 
                    | F A L S E
                    ;
//DIGITS :DIGIT+;

DECIMAL : SIGN? DIGIT+ '.' DIGIT+; // decimal = [SIGN] 1*DIGIT ["." 1*DIGIT] der 2. optionale Teil erschwert der Unterschied zwischen INT und DECIMAL -> nicht optional

INT     : SIGN? DIGIT+;// { !IsIRIallowed() }?;





// --------------------- DATE ---------------------
DATE                : DATETOKEN SQUOTE DATEVALUE SQUOTE;
fragment DATETOKEN  : D A T E;
fragment DATEVALUE  : YEAR '-' MONTH '-' DAY;
                    

// --------------------- DATETIMEOFFSET ---------------------
DATETIMEOFFSET      : DATEOSTOKEN SQUOTE DATETIMEOFFSETVALUE SQUOTE;
fragment DATEOSTOKEN: D A T E T I M E O F F S E T;  
                    
fragment DATETIMEOFFSETVALUE
                    :  YEAR '-' MONTH '-' DAY T HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )? ( Z | SIGN HOUR ':' MINUTE )
                    ;
// --------------------- TIMEOFDAY ---------------------
TIMEOFDAY           : TIMEOFDAYTO SQUOTE TIMEOFDAYVALUE SQUOTE;
fragment TIMEOFDAYTO: T I M E O F D A Y;  
fragment TIMEOFDAYVALUE	: HOUR ':' MINUTE ( ':' SECOND ( '.' FRACTIONALSECONDS )? )?;

// --------------------- date helper ---------------------

fragment ONEtoNINE  : '1'..'9';                   

fragment YEAR       : ('-')? ( '0' DIGIT DIGIT DIGIT | ONEtoNINE DIGIT DIGIT DIGIT );

fragment MONTH      : '0' ONEtoNINE
                    | '1' ( '0' | '1' | '2' )
                    ;

fragment DAY        : '0' ONEtoNINE
                    | ('1'|'2') DIGIT
                    | '3' ('0'|'1')
                    ;

fragment HOUR       : ('0' | '1') DIGIT
                    | '2' ( '1'..'3')
                    ;

fragment MINUTE     : ZEROtoFIFTYNINE;
fragment SECOND     : ZEROtoFIFTYNINE;
fragment FRACTIONALSECONDS  : DIGIT+;
fragment ZEROtoFIFTYNINE    : ('0'..'5') DIGIT;


//;------------------------------------------------------------------------------
//; 9. Punctuation
//;------------------------------------------------------------------------------

WS : ( SP | HTAB | '%20' | '%09' );  //; "required" whitespace 
//fragment OWS : WS*;
//rws : WS+;


AT 	: '@' | '%40';

COMMA  	: ',' | '%2C';
EQ     	:	 '=';
SIGN    : '+' | '%2B' |'-';
SEMI    : ';' | '%3B';
STAR    : '*';
SQUOTE  : '\'' | '%27';
OPEN    : '(' | '%28';
CLOSE   : ')' | '%29';


//;------------------------------------------------------------------------------
//; A. URI syntax [RFC3986]
//;------------------------------------------------------------------------------

QM                               : '?' -> channel(HIDDEN);

fragment PCHAR                   : UNRESERVED | PCT_ENCODED | SUB_DELIMS | ':' | '@';

fragment PCHARnoSQUOTE           : UNRESERVED| PCTENCODEDnoSQUOTE |  OTHERDELIMS | '$' | '&' | EQ | ':' | '@';

fragment PCT_ENCODED    : '%' HEXDIG HEXDIG;
fragment UNRESERVED     : ALPHA | DIGIT | '-' |'.' | '_' | '~';
fragment SUB_DELIMS     : '$' | '&' | '\'' | '=' | OTHER_DELIMS;
fragment OTHER_DELIMS   : '!' | '(' | ')' | '*' | '+' | ',' | ';';

fragment OTHERDELIMS    : '!' |  '(' | ')' | '*' | '+' | ',' | ';'
                        ;

fragment PCTENCODEDnoSQUOTE
                        :  '%' ( '0'|'1'|'3'..'9' | AtoF ) HEXDIG
                        | '%' '2' ( '0'..'6'|'8'|'9' | AtoF )
                        ;

fragment QCHAR_NO_AMP              : UNRESERVED | PCT_ENCODED | OTHERDELIMS | ':' | '@' | '/' | '?' | '$' | '\'' | '=';// { IsIRIallowed() }?; 
fragment QCHAR_NO_AMP_EQ           : UNRESERVED | PCT_ENCODED | OTHERDELIMS | ':' | '@' | '/' | '?' | '$' | '\'';
fragment QCHAR_NO_AMP_EQ_AT_DOLLAR : UNRESERVED | PCT_ENCODED | OTHERDELIMS | ':' |       '/' | '?' |       '\'';

fragment QCHAR_UNESCAPED           : UNRESERVED | PCT_ENCODED_UNESCAPED | OTHERDELIMS | ':' | '@' | '/' | '?' | '$' | '\'' | '=';
PCT_ENCODED_UNESCAPED              : '%' ( '0' | '1' |   '3' | '4' |   '6' | '8' | '9' | 'A'..'F' ) HEXDIG 
                                   | '%' '2' ( '0' | '1' |   '3' | '4' | '5' | '6' | '7' | '8' | '9' | 'A'..'F' ) 
                                   | '%' '5' ( DIGIT | 'A' | 'B' |   'D' | 'E' | 'F' );

fragment QCHAR_NO_AMP_DQUOTE   : QCHAR_UNESCAPED
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


fragment    ALPHA   : 'a'..'z'|'A'..'Z';
            DIGIT   : '0'..'9';/* ('0'|ONEtoNINE)
                    | '%x3'('0'..'9');*/
fragment    HEXDIG  : DIGIT | AtoF;
fragment    AtoF    : 'a'..'f'|'A'..'F';
            DQUOTE  : '\u0022';
            SP      : ' ';//'\u0020'; // a simple space
            HTAB    : '%09'; 
           
fragment    VCHAR   : '\u0021'..'\u007E';
            
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

SEARCHWORD          : ALPHA+ { IsSWallowed() }?;    //; Actually: any character from the Unicode categories L or Nl, 
//                                                  //; but not the words AND, OR, and NOT which are match far above
//CUSTOMNAME          : QCHAR_NO_AMP_EQ_AT_DOLLAR QCHAR_NO_AMP_EQ* { IsCUSTallowed() }?;
//CUSTOMVALUE         : QCHAR_NO_AMP+ { IsCUSTallowed() }?;


ODATAIDENTIFIER     : IDENTIFIERLEADINGCHARACTER (IDENTIFIERCHARACTER)*;

AT_ODATAIDENTIFIER   : AT ODATAIDENTIFIER;
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
/* D O N T   C H A N G E   T H E   O R D E R   O F   T H I S*/
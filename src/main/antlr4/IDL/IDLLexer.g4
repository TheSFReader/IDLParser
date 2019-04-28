/*
[The "BSD licence"]
Copyright (c) 2014 AutoTest Technologies, LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** CORBA IDL v3.5 grammar built from the OMG IDL language spec 'ptc-13-02-02'
    http://www.omg.org/spec/IDL35/Beta1/PDF/

    Initial IDL spec implementation in ANTLR v3 by Dong Nguyen.
    Migrated to ANTLR v4 by Steve Osselton.
    Current revision prepared by Nikita Visnevski.
*/
lexer grammar IDLLexer;


INTEGER_LITERAL
   : ('0' | '1' .. '9' '0' .. '9'*) INTEGER_TYPE_SUFFIX?
   ;


OCTAL_LITERAL
   : '0' ('0' .. '7') + INTEGER_TYPE_SUFFIX?
   ;


HEX_LITERAL
   : '0' ('x' | 'X') HEX_DIGIT + INTEGER_TYPE_SUFFIX?
   ;


fragment HEX_DIGIT
   : ('0' .. '9' | 'a' .. 'f' | 'A' .. 'F')
   ;


fragment INTEGER_TYPE_SUFFIX
   : ('l' | 'L')
   ;


FLOATING_PT_LITERAL
   : ('0' .. '9') + '.' ('0' .. '9')* EXPONENT? FLOAT_TYPE_SUFFIX? | '.' ('0' .. '9') + EXPONENT? FLOAT_TYPE_SUFFIX? | ('0' .. '9') + EXPONENT FLOAT_TYPE_SUFFIX? | ('0' .. '9') + EXPONENT? FLOAT_TYPE_SUFFIX
   ;


FIXED_PT_LITERAL
   : FLOATING_PT_LITERAL
   ;


fragment EXPONENT
   : ('e' | 'E') (PLUS | MINUS)? ('0' .. '9') +
   ;


fragment FLOAT_TYPE_SUFFIX
   : ('f' | 'F' | 'd' | 'D')
   ;


WIDE_CHARACTER_LITERAL
   : 'L' CHARACTER_LITERAL
   ;


CHARACTER_LITERAL
   : '\'' (ESCAPE_SEQUENCE | ~ ('\'' | '\\')) '\''
   ;


WIDE_STRING_LITERAL
   : 'L' STRING_LITERAL
   ;


STRING_LITERAL
   : '"' (ESCAPE_SEQUENCE | ~ ('\\' | '"'))* '"'
   ;


BOOLEAN_LITERAL
   : 'TRUE' | 'FALSE'
   ;


fragment ESCAPE_SEQUENCE
   : '\\' ('b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\') | UNICODE_ESCAPE | OCTAL_ESCAPE
   ;


fragment OCTAL_ESCAPE
   : '\\' ('0' .. '3') ('0' .. '7') ('0' .. '7') | '\\' ('0' .. '7') ('0' .. '7') | '\\' ('0' .. '7')
   ;


fragment UNICODE_ESCAPE
   : '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
   ;


fragment LETTER
   : '\u0024' | '\u0041' .. '\u005a' | '\u005f' | '\u0061' .. '\u007a' | '\u00c0' .. '\u00d6' | '\u00d8' .. '\u00f6' | '\u00f8' .. '\u00ff' | '\u0100' .. '\u1fff' | '\u3040' .. '\u318f' | '\u3300' .. '\u337f' | '\u3400' .. '\u3d2d' | '\u4e00' .. '\u9fff' | '\uf900' .. '\ufaff'
   ;


fragment ID_DIGIT
   : '\u0030' .. '\u0039' | '\u0660' .. '\u0669' | '\u06f0' .. '\u06f9' | '\u0966' .. '\u096f' | '\u09e6' .. '\u09ef' | '\u0a66' .. '\u0a6f' | '\u0ae6' .. '\u0aef' | '\u0b66' .. '\u0b6f' | '\u0be7' .. '\u0bef' | '\u0c66' .. '\u0c6f' | '\u0ce6' .. '\u0cef' | '\u0d66' .. '\u0d6f' | '\u0e50' .. '\u0e59' | '\u0ed0' .. '\u0ed9' | '\u1040' .. '\u1049'
   ;


SEMICOLON
   : ';'
   ;


COLON
   : ':'
   ;


COMA
   : ','
   ;

DOT
   : '.'
   ;



LEFT_BRACE
   : '{'
   ;


RIGHT_BRACE
   : '}'
   ;


LEFT_BRACKET
   : '('
   ;


RIGHT_BRACKET
   : ')'
   ;


LEFT_SQUARE_BRACKET
   : '['
   ;


RIGHT_SQUARE_BRACKET
   : ']'
   ;


TILDE
   : '~'
   ;


SLASH
   : '/'
   ;


LEFT_ANG_BRACKET
   : '<'
   ;


RIGHT_ANG_BRACKET
   : '>'
   ;


STAR
   : '*'
   ;


PLUS
   : '+'
   ;


MINUS
   : '-'
   ;


CARET
   : '^'
   ;


AMPERSAND
   : '&'
   ;


PIPE
   : '|'
   ;


EQUAL
   : '='
   ;


PERCENT
   : '%'
   ;


DOUBLE_COLON
   : '::'
   ;


RIGHT_SHIFT
   : '>>'
   ;


LEFT_SHIFT
   : '<<'
   ;


KW_SETRAISES
   : 'setraises'
   ;


KW_OUT
   : 'out'
   ;


KW_EMITS
   : 'emits'
   ;


KW_STRING
   : 'string'
   ;


KW_SWITCH
   : 'switch'
   ;


KW_PUBLISHES
   : 'publishes'
   ;


KW_TYPEDEF
   : 'typedef'
   ;


KW_USES
   : 'uses'
   ;


KW_PRIMARYKEY
   : 'primarykey'
   ;


KW_CUSTOM
   : 'custom'
   ;

KW_OCTET
   : 'octet'
   ;


KW_SEQUENCE
   : 'sequence'
   ;
   
KW_STRUCT
   : 'struct'
   ;


KW_IMPORT
   : 'import'
   ;





KW_NATIVE
   : 'native'
   ;


KW_READONLY
   : 'readonly'
   ;


KW_FINDER
   : 'finder'
   ;


KW_RAISES
   : 'raises'
   ;


KW_VOID
   : 'void'
   ;


KW_PRIVATE
   : 'private'
   ;


KW_EVENTTYPE
   : 'eventtype'
   ;


KW_WCHAR
   : 'wchar'
   ;


KW_IN
   : 'in'
   ;


KW_DEFAULT
   : 'default'
   ;


KW_PUBLIC
   : 'public'
   ;


KW_SHORT
   : 'short'
   ;


KW_LONG
   : 'long'
   ;


KW_ENUM
   : 'enum'
   ;


KW_WSTRING
   : 'wstring'
   ;


KW_CONTEXT
   : 'context'
   ;


KW_HOME
   : 'home'
   ;


KW_FACTORY
   : 'factory'
   ;


KW_EXCEPTION
   : 'exception'
   ;


KW_GETRAISES
   : 'getraises'
   ;


KW_CONST
   : 'const'
   ;


KW_VALUEBASE
   : 'ValueBase'
   ;


KW_VALUETYPE
   : 'valuetype'
   ;


KW_SUPPORTS
   : 'supports'
   ;


KW_MODULE
   : 'module'
   ; 


KW_FIXED
   : 'fixed'
   ;



KW_OBJECT
   : 'Object'
   ;


KW_TRUNCATABLE
   : 'truncatable'
   ;
   
KW_UNION
   : 'union'
   ;


KW_ONEWAY
   : 'oneway'
   ;


KW_ANY
   : 'any'
   ;





KW_CASE
   : 'case'
   ;




KW_MULTIPLE
   : 'multiple'
   ;


KW_ABSTRACT
   : 'abstract'
   ;


KW_INOUT
   : 'inout'
   ;


KW_PROVIDES
   : 'provides'
   ;


KW_CONSUMES
   : 'consumes'
   ;





KW_TYPEPREFIX
   : 'typeprefix'
   ;


KW_TYPEID
   : 'typeid'
   ;


KW_ATTRIBUTE
   : 'attribute'
   ;


KW_LOCAL
   : 'local'
   ;


KW_MANAGES
   : 'manages'
   ;


KW_INTERFACE
   : 'interface'
   ;


KW_COMPONENT
   : 'component'
   ;
   
KW_UNSIGNED
   : 'unsigned'
   ;

KW_CHAR
   : 'char'
   ;
   
KW_FLOAT
   : 'float'
   ;


KW_BOOLEAN
   : 'boolean'
   ;

KW_DOUBLE
   : 'double'
   ;

KW_PRAGMA
	: '#pragma'
	;

KW_KEYLIST
	: 'keylist'
	;
	
	
KW_ATKEY
   : '//@key' '\r'? '\n'
   ;
   
KW_TOPLEVEL_FALSE
   : '//@top-level false' '\r'? '\n'
   ;
   
ID
   : LETTER (LETTER | ID_DIGIT)*
   ;


WS
   : (' ' | '\r' | '\t' | '\u000C' | '\n') -> channel (HIDDEN)
   ;


COMMENT
   : '/*' .*? '*/' -> channel (HIDDEN)
   ;
	

LINE_COMMENT
   : '//' ~ ('\n' | '\r')* '\r'? '\n' -> channel (HIDDEN)
   ;
   


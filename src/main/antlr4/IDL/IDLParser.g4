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
parser grammar IDLParser;

options { tokenVocab=IDLLexer; }


specification
   : import_decl* definition +
   ;


definition
   : type_decl SEMICOLON toplevel_false?
   | const_decl SEMICOLON
   | except_decl SEMICOLON
   | interface_or_forward_decl SEMICOLON
   | module SEMICOLON
   | value SEMICOLON
   | type_id_decl SEMICOLON
   | type_prefix_decl SEMICOLON
   | event SEMICOLON
   | component SEMICOLON
   | home_decl SEMICOLON
   | keydef
   ;

toplevel_false
	: KW_TOPLEVEL_FALSE
	;

atkey
   : KW_ATKEY
   ;
module
   : KW_MODULE ID LEFT_BRACE definition + RIGHT_BRACE
   ;

keydef
	: KW_PRAGMA KW_KEYLIST key_name key_elem* 
	;

key_name
	: ID
	;
	
key_parent
	: ID DOT
	;
	
key_elem
	:(ID DOT)* ID
	;
	
scoped_name
   : (DOUBLE_COLON)? ID (DOUBLE_COLON ID)*
   ;

interface_or_forward_decl
   : interface_decl
   | forward_decl
   ;

interface_decl
   : interface_header LEFT_BRACE interface_body RIGHT_BRACE
   ;

forward_decl
   : (KW_ABSTRACT | KW_LOCAL)? KW_INTERFACE ID
   ;

interface_header
   : (KW_ABSTRACT | KW_LOCAL)? KW_INTERFACE ID (interface_inheritance_spec)?
   ;

interface_body
   : export*
   ;

export
   : type_decl SEMICOLON
   | const_decl SEMICOLON
   | except_decl SEMICOLON
   | attr_decl SEMICOLON
   | op_decl SEMICOLON
   | type_id_decl SEMICOLON
   | type_prefix_decl SEMICOLON
   ;

interface_inheritance_spec
   : COLON interface_name (COMA interface_name)*
   ;

interface_name
   : scoped_name
   ;

value
   : (value_decl | value_abs_decl | value_box_decl | value_forward_decl)
   ;

value_forward_decl
   : (KW_ABSTRACT)? KW_VALUETYPE ID
   ;

value_box_decl
   : KW_VALUETYPE ID type_spec
   ;

value_abs_decl
   : KW_ABSTRACT KW_VALUETYPE ID value_inheritance_spec LEFT_BRACE export* RIGHT_BRACE
   ;

value_decl
   : value_header LEFT_BRACE value_element* RIGHT_BRACE
   ;

value_header
   : (KW_CUSTOM)? KW_VALUETYPE ID value_inheritance_spec
   ;

value_inheritance_spec
   : (COLON (KW_TRUNCATABLE)? value_name (COMA value_name)*)? (KW_SUPPORTS interface_name (COMA interface_name)*)?
   ;

value_name
   : scoped_name
   ;

value_element
   : (export | state_member | init_decl)
   ;

state_member
   : (KW_PUBLIC | KW_PRIVATE) type_spec declarators SEMICOLON
   ;

init_decl
   : KW_FACTORY ID LEFT_BRACKET (init_param_decls)? RIGHT_BRACKET (raises_expr)? SEMICOLON
   ;

init_param_decls
   : init_param_decl (COMA init_param_decl)*
   ;

init_param_decl
   : init_param_attribute param_type_spec simple_declarator
   ;

init_param_attribute
   : KW_IN
   ;

const_decl
   : KW_CONST const_type name=ID EQUAL const_exp
   ;

const_type
   : integer_type
   | char_type
   | wide_char_type
   | boolean_type
   | floating_pt_type
   | string_type
   | wide_string_type
   | fixed_pt_const_type
   | scoped_name
   | octet_type
   ;

const_exp
   : or_expr
   ;

or_expr
   : xor_expr (PIPE xor_expr)*
   ;

xor_expr
   : and_expr (CARET and_expr)*
   ;

and_expr
   : shift_expr (AMPERSAND shift_expr)*
   ;

shift_expr
   : add_expr ((RIGHT_SHIFT | LEFT_SHIFT) add_expr)*
   ;

add_expr
   : mult_expr ((PLUS | MINUS) mult_expr)*
   ;

mult_expr
   : unary_expr ((STAR | SLASH | PERCENT) unary_expr)*
   ;

unary_expr
   : unary_operator primary_expr
   | primary_expr
   ;

unary_operator
   : (MINUS | PLUS | TILDE)
   ;

primary_expr
   : scoped_name
   | literal
   | LEFT_BRACKET const_exp RIGHT_BRACKET
   ;


literal
   : HEX_LITERAL #hexLitteral
   | INTEGER_LITERAL #intLitteral
//   | STRING_LITERAL 
//   | WIDE_STRING_LITERAL | CHARACTER_LITERAL | WIDE_CHARACTER_LITERAL | FIXED_PT_LITERAL 
	| FLOATING_PT_LITERAL #floatLiteral
	| BOOLEAN_LITERAL #boolLiteral
   ;

positive_int_const
   : const_exp
   ;

type_decl
   : struct_type
   | KW_TYPEDEF type_declarator
   | union_type
   | enum_type
   | KW_NATIVE simple_declarator
   | constr_forward_decl
   ;

type_declarator
   : type_spec declarators
   ;

type_spec
   : simple_type_spec
   | constr_type_spec
   ;

simple_type_spec
   : base_type_spec
   | template_type_spec
   | scoped_name
   ;

base_type_spec
   : floating_pt_type
   | integer_type
   | char_type
   | wide_char_type
   | boolean_type
   | octet_type
   | any_type
   | object_type
   | value_base_type
   ;

template_type_spec
   : sequence_type
   | string_type
   | wide_string_type
   | fixed_pt_type
   ;

constr_type_spec
   : struct_type
   | union_type
   | enum_type
   ;

declarators
   : declarator (COMA declarator)*
   ;

declarator
   : simple_declarator
   | complex_declarator
   ;

simple_declarator
   : ID
   ;

complex_declarator
   : array_declarator
   ;

floating_pt_type
   : (KW_FLOAT | KW_DOUBLE | KW_LONG KW_DOUBLE)
   ;

integer_type
   : signed_int
   | unsigned_int
   ;

signed_int
   : signed_short_int
   | signed_long_int
   | signed_longlong_int
   ;

signed_short_int
   : KW_SHORT
   ;

signed_long_int
   : KW_LONG
   ;

signed_longlong_int
   : KW_LONG KW_LONG
   ;

unsigned_int
   : unsigned_short_int
   | unsigned_long_int
   | unsigned_longlong_int
   ;

unsigned_short_int
   : KW_UNSIGNED KW_SHORT
   ;

unsigned_long_int
   : KW_UNSIGNED KW_LONG
   ;

unsigned_longlong_int
   : KW_UNSIGNED KW_LONG KW_LONG
   ;

char_type
   : KW_CHAR
   ;



boolean_type
   : KW_BOOLEAN
   ;

octet_type
   : KW_OCTET
   ;

any_type
   : KW_ANY
   ;

object_type
   : KW_OBJECT
   ;


struct_type
   : KW_STRUCT ID LEFT_BRACE member_list RIGHT_BRACE
   ;


member_list
   : member_and_key+
   ;
   
member_and_key
	: member atkey?
	;

member
   : type_spec declarators SEMICOLON
   ;

union_type
   : KW_UNION ID KW_SWITCH LEFT_BRACKET switch_type_spec RIGHT_BRACKET LEFT_BRACE switch_body RIGHT_BRACE
   ;

switch_type_spec
   : integer_type
   | char_type
   | boolean_type
   | enum_type
   | scoped_name
   ;

switch_body
   : case_stmt +
   ;

case_stmt
   : case_label + element_spec SEMICOLON
   ;

case_label
   : KW_CASE const_exp COLON
   | KW_DEFAULT COLON
   ;

element_spec
   : type_spec declarator
   ;

enum_type
   : KW_ENUM ID LEFT_BRACE enumerator (COMA enumerator)* RIGHT_BRACE
   ;

enumerator
   : ID
   ;

sequence_type
   : KW_SEQUENCE LEFT_ANG_BRACKET simple_type_spec COMA positive_int_const RIGHT_ANG_BRACKET
   | KW_SEQUENCE LEFT_ANG_BRACKET simple_type_spec RIGHT_ANG_BRACKET
   ;

string_type
   : KW_STRING LEFT_ANG_BRACKET positive_int_const RIGHT_ANG_BRACKET
   | KW_STRING
   ;

array_declarator
   : ID fixed_array_size +
   ;

fixed_array_size
   : LEFT_SQUARE_BRACKET positive_int_const RIGHT_SQUARE_BRACKET
   ;
   

wide_string_type
   : KW_WSTRING LEFT_ANG_BRACKET positive_int_const RIGHT_ANG_BRACKET
   | KW_WSTRING
   ;
   
wide_char_type
   : KW_WCHAR
   ;



attr_decl
   : readonly_attr_spec
   | attr_spec
   ;

except_decl
   : KW_EXCEPTION ID LEFT_BRACE member* RIGHT_BRACE
   ;

op_decl
   : (op_attribute)? op_type_spec ID parameter_decls (raises_expr)? (context_expr)?
   ;

op_attribute
   : KW_ONEWAY
   ;

op_type_spec
   : param_type_spec
   | KW_VOID
   ;

parameter_decls
   : LEFT_BRACKET param_decl (COMA param_decl)* RIGHT_BRACKET
   | LEFT_BRACKET RIGHT_BRACKET
   ;

param_decl
   : param_attribute param_type_spec simple_declarator
   ;

param_type_spec
   : base_type_spec
   | string_type
   | wide_string_type
   | scoped_name
   ;
   
param_attribute
   : KW_IN
   | KW_OUT
   | KW_INOUT
   ;

raises_expr
   : KW_RAISES LEFT_BRACKET scoped_name (COMA scoped_name)* RIGHT_BRACKET
   ;

context_expr
   : KW_CONTEXT LEFT_BRACKET STRING_LITERAL (COMA STRING_LITERAL)* RIGHT_BRACKET
   ;



fixed_pt_type
   : KW_FIXED LEFT_ANG_BRACKET positive_int_const COMA positive_int_const RIGHT_ANG_BRACKET
   ;

fixed_pt_const_type
   : KW_FIXED
   ;

value_base_type
   : KW_VALUEBASE
   ;

constr_forward_decl
   : KW_STRUCT ID
   | KW_UNION ID
   ;

import_decl
   : KW_IMPORT imported_scope SEMICOLON
   ;

imported_scope
   : scoped_name
   | STRING_LITERAL
   ;

type_id_decl
   : KW_TYPEID scoped_name STRING_LITERAL
   ;

type_prefix_decl
   : KW_TYPEPREFIX scoped_name STRING_LITERAL
   ;

readonly_attr_spec
   : KW_READONLY KW_ATTRIBUTE param_type_spec readonly_attr_declarator
   ;

readonly_attr_declarator
   : simple_declarator raises_expr
   | simple_declarator (COMA simple_declarator)*
   ;

attr_spec
   : KW_ATTRIBUTE param_type_spec attr_declarator
   ;

attr_declarator
   : simple_declarator attr_raises_expr
   | simple_declarator (COMA simple_declarator)*
   ;

attr_raises_expr
   : get_excep_expr (set_excep_expr)?
   | set_excep_expr
   ;

get_excep_expr
   : KW_GETRAISES exception_list
   ;

set_excep_expr
   : KW_SETRAISES exception_list
   ;

exception_list
   : LEFT_BRACKET scoped_name (COMA scoped_name)* RIGHT_BRACKET
   ;

component
   : component_decl
   | component_forward_decl
   ;

component_forward_decl
   : KW_COMPONENT ID
   ;

component_decl
   : component_header LEFT_BRACE component_body RIGHT_BRACE
   ;

component_header
   : KW_COMPONENT ID (component_inheritance_spec)? (supported_interface_spec)?
   ;

supported_interface_spec
   : KW_SUPPORTS scoped_name (COMA scoped_name)*
   ;

component_inheritance_spec
   : COLON scoped_name
   ;

component_body
   : component_export*
   ;

component_export
   : provides_decl SEMICOLON
   | uses_decl SEMICOLON
   | emits_decl SEMICOLON
   | publishes_decl SEMICOLON
   | consumes_decl SEMICOLON
   | attr_decl SEMICOLON
   ;

provides_decl
   : KW_PROVIDES interface_type ID
   ;

interface_type
   : scoped_name
   | KW_OBJECT
   ;

uses_decl
   : KW_USES (KW_MULTIPLE)? interface_type ID
   ;

emits_decl
   : KW_EMITS scoped_name ID
   ;

publishes_decl
   : KW_PUBLISHES scoped_name ID
   ;

consumes_decl
   : KW_CONSUMES scoped_name ID
   ;

home_decl
   : home_header home_body
   ;

home_header
   : KW_HOME ID (home_inheritance_spec)? (supported_interface_spec)? KW_MANAGES scoped_name (primary_key_spec)?
   ;

home_inheritance_spec
   : COLON scoped_name
   ;

primary_key_spec
   : KW_PRIMARYKEY scoped_name
   ;

home_body
   : LEFT_BRACE home_export* RIGHT_BRACE
   ;

home_export
   : export
   | factory_decl SEMICOLON
   | finder_decl SEMICOLON
   ;

factory_decl
   : KW_FACTORY ID LEFT_BRACKET (init_param_decls)? RIGHT_BRACKET (raises_expr)?
   ;

finder_decl
   : KW_FINDER ID LEFT_BRACKET (init_param_decls)? RIGHT_BRACKET (raises_expr)?
   ;

event
   : (event_decl | event_abs_decl | event_forward_decl)
   ;

event_forward_decl
   : (KW_ABSTRACT)? KW_EVENTTYPE ID
   ;

event_abs_decl
   : KW_ABSTRACT KW_EVENTTYPE ID value_inheritance_spec LEFT_BRACE export* RIGHT_BRACE
   ;

event_decl
   : event_header LEFT_BRACE value_element* RIGHT_BRACE
   ;

event_header
   : (KW_CUSTOM)? KW_EVENTTYPE ID value_inheritance_spec
   ;



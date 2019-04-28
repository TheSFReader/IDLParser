lexer grammar PreprocessorBaseLexer;


INCLUDE
	:	'#include' [ \t]* STRING [ \t]* '\r'? '\n'
		{
		// launch another lexer on the include file, get tokens,
		// emit them all at once here, replacing this token
		java.util.List<PreprocessedToken> tokens = Preprocessor.include(getText());
		System.out.println("back from include");
		if ( tokens!=null ) {
			for (Token t : tokens) emit(t);
		}
  }
	;

fragment
STRING : '"' .*? '"' ;

OTHER_CMD : '#' ~[\r\n]* '\r'? '\n' ; // can't use .*; scarfs \n\n after include

CHUNK : ~'#'+ ; // anything else



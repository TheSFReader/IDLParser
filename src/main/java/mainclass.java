
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import IDL.IDLLexer;
import IDL.IDLParser;
import IDL.IDLTokenErrorListener;
import IDL.IDLTokenFactory;
import preprocessor.PreprocessedCharStream;
import preprocessor.PreprocessedToken;
import preprocessor.Preprocessor;
import ptypes.PType;
    
public class mainclass {

	public static void main(String[] args) {
			String filename = "src/main/resources/sample.idl";
			if(args.length > 0) {
				filename = args[0];
			}
			

    		final List<PreprocessedToken> loadedtokens = Preprocessor.load(filename);
    		PreprocessedCharStream preprocessedinput = new PreprocessedCharStream(loadedtokens);
 			IDLLexer idllexer = new IDLLexer(preprocessedinput);

    		idllexer.setTokenFactory(new IDLTokenFactory(preprocessedinput));
 			//idllexer.setTokenFactory(new CommonTokenFactory(true));
    		
 			
 			CommonTokenStream tokens = new CommonTokenStream(idllexer);
//    		tokens.fill();
//    		for(Token token : tokens.getTokens()) {
//    			System.out.println(token);
//    		}
//    		tokens.seek(0);
    		
    		IDLParser parser = new IDLParser(tokens);
    		parser.removeErrorListeners();
    		parser.addErrorListener(new IDLTokenErrorListener());
    		
    		ParseTree tree = parser.specification(); // begin parsing at rule 'r'
    		
    		System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    		MyIDLListener extractor = new MyIDLListener(tokens);
    		
    		ParseTreeWalker.DEFAULT.walk(extractor, tree);
    		
    		
    		PType type = extractor.typeStack.pop();
    		System.out.println(type.output());
    		//System.out.println(type.outputIDL());
    		System.out.println(type.toIDL(""));
    	}

}

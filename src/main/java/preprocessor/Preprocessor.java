package preprocessor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.UnbufferedCharStream;

public class Preprocessor {

	static final PreprocessedTokenFactory tokenFactory = new PreprocessedTokenFactory();

	public static List<PreprocessedToken> include(String includeCommand) {
		String currentFileName = tokenFactory.peekFileName();
		System.out.println("process " + includeCommand + " from " + tokenFactory.peekFileName());
		String currentpath = currentFileName.substring(0,currentFileName.lastIndexOf('/') + 1);
		int l = includeCommand.indexOf('"');
		int r = includeCommand.lastIndexOf('"');
		String filename = includeCommand.substring(l+1, r);
		//tokenFactory.pushFileName(filename);
		List<PreprocessedToken> tokens = load(currentpath + filename);
		//tokenFactory.popFileName();
		return tokens;
	}

	@SuppressWarnings("unchecked")
	public static List<PreprocessedToken> load(String filename) {
		System.out.println("opening " + filename);
		tokenFactory.pushFileName(filename);
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			// don't buffer since we're copying. these buffers disappear
			// so we must copy
			UnbufferedCharStream input = new UnbufferedCharStream(br);
			PreprocessorLexer lexer = new PreprocessorLexer(input);
			lexer.setTokenFactory(tokenFactory); // force creation of PreprocessedToken
			List<PreprocessedToken> result = (List<PreprocessedToken>)lexer.getAllTokens(); 
			tokenFactory.popFileName();
			return result;
		}
		catch (IOException ioe) {
			System.err.println("Can't load "+filename);
			tokenFactory.popFileName();
		}
		return null;
	}

}

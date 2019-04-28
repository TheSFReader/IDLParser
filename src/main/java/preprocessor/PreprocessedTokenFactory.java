package preprocessor;
import java.util.ArrayDeque;
import java.util.Deque;

/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

public class PreprocessedTokenFactory implements TokenFactory<PreprocessedToken> {
	/** Stack of include files */
	Deque<String> stack = new ArrayDeque<String>();

	public void pushFileName(String filename) {
		stack.push(filename);
	}

	public void popFileName() { stack.pop(); }
	
	public String peekFileName() { return stack.peek(); }

	@Override
	public PreprocessedToken create(Pair<TokenSource, CharStream> source, int type, String text,
						   int channel, int start, int stop, int line,
						   int charPositionInLine)
	{
		PreprocessedToken t = new PreprocessedToken(source, type, channel, start, stop);
		t.setLine(line);
		t.setCharPositionInLine(charPositionInLine);
		CharStream input = source.b;
		t.setText(input.getText(Interval.of(start,stop)));
		t.filename = stack.peek();
		return t;
	}

	@Override
	public PreprocessedToken create(int type, String text) {
		return new PreprocessedToken(type, text);
	}
}

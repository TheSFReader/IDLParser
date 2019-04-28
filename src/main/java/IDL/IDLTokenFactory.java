package IDL;

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

import preprocessor.PreprocessedCharStream;

public class IDLTokenFactory implements TokenFactory<IDLToken> {
	private final PreprocessedCharStream cinput;

	public IDLTokenFactory(PreprocessedCharStream cinput) {
		this.cinput = cinput;
	}

	@Override
	public IDLToken create(int type, String text) {
		return new IDLToken(type, text);
	}

	@Override
	public IDLToken create(Pair<TokenSource, CharStream> source, int type, String text,
						 int channel, int start, int stop, int line,
						 int charPositionInLine)
	{
		IDLToken t = new IDLToken(source, type, channel, start, stop);
		t.setLine(line);
		t.setCharPositionInLine(charPositionInLine);
		CharStream input = source.b;
		t.setText(input.getText(Interval.of(start, stop)));
		t.filename = cinput.getFilenameFromCharIndex(start);
		t.setLine(cinput.getLineFromCharIndex(start));
		return t;
	}
}

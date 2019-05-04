package ptypes;

public class PStruct extends PType{

	public PStruct(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = currentIndent + "struct" + " " + value + " {\n";
		String noTopLevel = "";
		String childrenIndent = currentIndent + oneIndent;
		for(PType child : children) {
			if( child.eventName .equals("NoTopLevel")) {
				noTopLevel = " //@top-level false";
			} else {
				result += child.toIDL(childrenIndent);
			}
		}
		result += currentIndent + "};"+ noTopLevel + "\n\n";
		return result;
		
	}

	
}

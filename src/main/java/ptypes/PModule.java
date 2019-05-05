package ptypes;

public class PModule extends PType{

	public PModule(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = outputComment(currentIndent) + currentIndent + "module" + " " + value + " {\n";
		String childrenIndent = currentIndent + oneIndent;
		for(PType child : children) {
			result += child.toIDL(childrenIndent);
		}
		result += currentIndent + "};\n\n";
		return result;
		
	}

	
}

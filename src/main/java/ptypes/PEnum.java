package ptypes;

public class PEnum extends PType {

	public PEnum(String string, String eventName) {
		super(string, eventName);
	} 
	
	public String toIDL(String currentIndent) {
		String result = currentIndent + "enum" + " " + value + " {\n";
		String childrenIndent = currentIndent + oneIndent;
		for(int i = 0; i < children.size() -1; i++) {
			result += childrenIndent + children.get(i).value + ",\n";
		}
		if( children.size() > 0) {
			result += childrenIndent + children.get(children.size() -1).value + "\n";
		}
		result += currentIndent + "};\n\n";
		
		
		return result;
	}

}

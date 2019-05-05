package ptypes;

public class PMember extends PType {

	public PMember(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = outputComment(currentIndent);
		String typeSpecName = null;
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				typeSpecName = child.toIDL(currentIndent);
			} else if( child.eventName.contentEquals("Declarator")) {
				result += currentIndent + typeSpecName + " " + child.toIDL(currentIndent) + ";\n";
			}
		}
		
		return result;
	}

}

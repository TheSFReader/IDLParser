package ptypes;

public class PMember extends PType {

	public PMember(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = outputComment(currentIndent);
		String typeSpecName = null;
		String atKey = hasKey() ? " //@key" : "";
		String declarator = "";
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				typeSpecName = child.toIDL(currentIndent);
			} else if( child.eventName.contentEquals("Declarator")) {
				declarator = child.toIDL(currentIndent);
				result += currentIndent + typeSpecName + " " + declarator + ";" + atKey + "\n";
			}
		}
		return result;
	}
	
	String getTypeSpec() {
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				return child.value;
			} 
		}
		return "";
	}
	boolean hasKey() {
		for(PType child : children) {
			if( child.eventName.contentEquals("AtKey")) {
				return true;
			}
		}
		return false;
	}
	
	String getKeyList() {
		if( ! hasKey()) {
			return "";
		}
		String result = "";
		for(PType child : children) {
			if( child.eventName.contentEquals("Declarator")) {
				result += " " + child.value;
			}
		}
		return result;
	}

	
}

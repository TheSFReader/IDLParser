package ptypes;

public class PMember extends PType {

	public PMember(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = outputComment(currentIndent);
		String typeSpecName = null;
		String atKey = "";
		String declarator = "";
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				typeSpecName = child.toIDL(currentIndent);
			} else if( child.eventName.contentEquals("Declarator")) {
				declarator = child.toIDL(currentIndent);
			}else if( child.eventName.contentEquals("AtKey")) {
				atKey = " //@key";
			}
		}
		result += currentIndent + typeSpecName + " " + declarator + ";" + atKey + "\n";
		return result;
	}
	
	String getKeyList() {
		for(PType child : children) {
			if( child.eventName.contentEquals("AtKey")) {
				return " " + value;
			}
		}
		return "";
	}

}

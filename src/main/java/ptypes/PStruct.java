package ptypes;

public class PStruct extends PType{

	
	public PStruct(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = outputComment(currentIndent) + currentIndent + "struct" + " " + value + " {\n";
		String topicDef = "";
		String noTopLevel = "";
		String childrenIndent = currentIndent + oneIndent;
		for(PType child : children) {
			if( child.eventName .equals("NoTopLevel")) {
				noTopLevel = " //@top-level false";
			} else {result += child.toIDL(childrenIndent);
			}
		}
		String keyList = getKeyList();
		if(!  keyList.isEmpty()) {
			topicDef = currentIndent + "#pragma keylist " + value + keyList + "\n";
		}
		result += currentIndent + "};"+ noTopLevel + "\n" + topicDef + "\n";
		return result;
		
	}

	public String getKeyList() {
		String result = "";
		
		for(PType child : children) {
			if( child instanceof PMember) {
				if( child instanceof PMember) {
					result += ((PMember)child).getKeyList();
				}
			}
		}
		return result;
	}
	
}

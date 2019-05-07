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
			} else {
				result += child.toIDL(childrenIndent);
			}
		}
		if( noTopLevel.isEmpty() && ! hasDefinedKeyList()) {
			String keyList = getKeyList();
			if(!  keyList.isEmpty()) {
				topicDef = currentIndent + "#pragma keylist " + value + keyList + "\n";
			}
		}
		result += currentIndent + "};"+ noTopLevel + "\n" + topicDef + "\n";
		return result;
		
	}

	public String getKeyList() {
		String result = "";
		
		for(PType child : children) {
			if( child instanceof PMember) {
				PMember member = ((PMember)child);
				if( member.hasKey()) {
					String typeSpec = member.getTypeSpec();
					PType type = findPType(typeSpec);
					if( type != null && type instanceof PStruct) {
						result += ((PStruct)type).getListMember(member);
					} else {
						result += member.getKeyList();
					}
					
				}
			}
		}
		return result;
	}
	
	public String getListMember(PMember member) {
		String result = "";
		for(PType child : children) {
			if( child instanceof PMember) {
				result += " " + member.value + "." + child.value;
			}
		}
		return result;
	}
	
	public boolean isTopLevelAllowed() {
		boolean result = true;
		return result;
	}
	
	public boolean hasDefinedKeyList() {
		for( PType child : parent.children) {
			if(child instanceof PKeyDef && ((PKeyDef)child).getDefinedStructName().equals( value)) {
				return true;
			}
		}
		return false;
	}
}

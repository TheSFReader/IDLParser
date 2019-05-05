package ptypes;

public class PTypedef extends PType {

	public PTypedef(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String typeSpec = null;
		String declarator = null;
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				typeSpec = child.toIDL(currentIndent);
			} else if(child.eventName.contentEquals("Declarator")){
				declarator  = child.toIDL(currentIndent);
			}
			
		}
		
		if( typeSpec != null && declarator != null) {
			return outputComment(currentIndent) + currentIndent + "typedef " + typeSpec + " " + declarator +";\n\n";
		}
		return null;
	}

}

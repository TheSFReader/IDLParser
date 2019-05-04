package ptypes;

public class PDeclarator extends PType {

	public PDeclarator(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result = value;
		for(PType child : children) {
			if( child.eventName.contentEquals("FixedArraySize")) {
				result += "[" + child.value + "]";
			}
		}
		return result;
		
	}

}

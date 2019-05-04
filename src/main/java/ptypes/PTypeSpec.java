package ptypes;

public class PTypeSpec extends PType{

	public PTypeSpec(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String result =  value;
		PType sequence= null;
		for(PType child : children) {
			if( child.eventName.contentEquals("Sequence")) {
				sequence = child;
			}
			if( child.eventName.contentEquals("Bound") && value.contentEquals("string")) {
				result = value + "<" + child.value + ">";
			}
		}
		if( sequence != null) {
			return sequenceToIDL(sequence, result);
		}
		
		return result;
		
	}

	private String sequenceToIDL(PType sequence, String value) {
		String result =  "sequence<" + value;
		String bound= null;
		for(PType child : sequence.children) {
			
			if( child.eventName.contentEquals("Bound")) {
				bound = child.value;
			}
		}
		if(bound != null) {
			result += "," + bound;
		}
		result += ">";
		
		return result;
	}

	
}

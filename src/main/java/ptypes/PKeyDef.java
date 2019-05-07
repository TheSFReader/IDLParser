package ptypes;

import java.util.ArrayList;
import java.util.List;

public class PKeyDef extends PType {

	public PKeyDef(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String structName = null;
		List<String> keys = new ArrayList<>();
		for(PType child : children) {
			if( child.eventName.contentEquals("KeyName")) {
				structName = child.value;
			} else if(child.eventName.contentEquals("KeyElem")){
				keys.add(child.value);
			}
			
		}
		
		if( structName != null) {
			String result = outputComment(currentIndent) + currentIndent+ "#pragma keylist " + structName;
			for(String key : keys) {
				result += " " + key;
			}
			result += "\n\n";
			return result;
		}
		return "";
	}

	public String getDefinedStructName() {
		for(PType child : children) {
			if( child.eventName.contentEquals("KeyName")) {
				return child.value;
			}
		}
		return "";
	}

}
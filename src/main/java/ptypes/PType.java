package ptypes;
import java.util.ArrayList;
import java.util.List;

public class PType {

	public static String oneIndent = "   ";
	
	public PType(String string, String eventName) {
		value = string;
		this.eventName = eventName;
	}
	public String value;
	public String eventName;
	public List<PType> children = new ArrayList<>();
	public List<String> commentlines;

	public String toString() {
		String result = value + "(" + eventName + ")";
		if (! children.isEmpty()) {
			result += "=" + children;
		}
		return result;
	}

	
	public String toIDL(String currentIndent) {
		String result = currentIndent + eventName + " " + value + " {\n";
		String childrenIndent = currentIndent + oneIndent;
		for(PType child : children) {
			result += child.toIDL(childrenIndent);
		}
		result += currentIndent + "};\n";
		return result;
	}
	
	

	public String outputIDL() {
		return outputIDL(0);

	}


	private String outputIDL(int level) {
		String tabResult = getTabResult(level);

		String result ="";
		switch( eventName) {
		case "": {
			for( PType child : children) {
				result += child.outputIDL(level);
			}
		}
		case "Module":
			result += tabResult + "module " + value+ "\n" + tabResult + "{\n";
			for( PType child : children) {
				result += child.outputIDL(level+1);
			}
			result += tabResult + "};\n";
			break;
		case "Enum":
			result += tabResult + "enum " + value + "\n" + tabResult + "{\n";
			String nextTabResult = getTabResult(level + 1);
			for(int i = 0; i < children.size(); i++) {
				PType child = children.get(i);
				result += nextTabResult + child.value + ((i < children.size() -1) ? ",\n" : "\n");
			}
			result += tabResult + "};\n";
			break;
		case "Struct":
			result += tabResult + "struct " + value+ "\n" + tabResult + "{\n";
			for( PType child : children) {
				result += child.outputIDL(level+1);
			}
			result += tabResult + "};\n";
			break;
		case "Member":
			if(children.size() > 0) {
				result += tabResult + children.get(0).value + " " + value;
			}
			boolean hasAtKey = false;
			for( int i = 1; i < children.size(); i++) {
				PType child = children.get(i);
				if( child.eventName.equals("AtKey")) {
					hasAtKey = true;
				} else if(child.eventName.equals("FixedArraySize") ) {
					result += "[" + child.value + "]";
				}
			}
			result += ";";
			if( hasAtKey) {
				result += " //@key";
			}
			result += "\n";
			break;

		}

		return result;
	}

	private String getTabResult( int level) {
		String tabResult = "";
		for( int i = 0; i < level; i++) {
			tabResult += "   "; 
		}
		return tabResult;
	}

	public String output() {
		return output(0);
	}

	public String output(int level) {
		String tabResult = "";
		for( int i = 0; i < level; i++) {
			tabResult += "\t"; 
		}
		String result = tabResult + value + "(" + eventName + ")";

		if(children.isEmpty()) {
			result += "\n" + outputComment(level);
		}
		else {
			result += "\n" + outputComment(level);
			for( PType child : children) {
				result += child.output(level+1);
			}
			result+= tabResult + "/" + value + "\n";
		}

		return result;
	}
	String outputComment(int level) {

		if(commentlines == null || commentlines.isEmpty()) {
			return "";
		}

		String tabResult = "";
		for( int i = 0; i < level-1; i++) {
			tabResult += "\t"; 
		}

		String result = "";
		for( String comment : commentlines) {
			result += tabResult + "// " + comment;
		}
		return result;
	}
}

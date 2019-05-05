package ptypes;
import java.util.ArrayList;
import java.util.List;

public class PType {

	public static String oneIndent = "   ";
	
	
	public PType(String string, String eventName) {
		value = string;
		this.eventName = eventName;
	}
	
	public PType parent = null;
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

	public void addChild(PType newChild) {
		children.add(newChild);
		newChild.parent = this;
	}
	
	public String toIDL() {
		String result="";
		for(PType child : children) {
			result += child.toIDL("");
		}
		result += "\n";
		return result;
	}
	
	public String toIDL() {
		String result = "";
		for(PType child : children) {
			result += child.toIDL("");
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
	

	public String output() {
		return output("");
	}

	public String output(String indent) {
		String result = indent + value + "(" + eventName + ")";
		if(children.isEmpty()) {
			result += "\n" + outputComment(indent);
		}
		else {
			result += "\n" + outputComment(indent);
			for( PType child : children) {
				result += child.output(indent+ oneIndent);
			}
			result+= indent + "/" + value + "\n";
		}

		return result;
	}
	
	String outputComment(String indent) {

		if(commentlines == null || commentlines.isEmpty()) {
			return "";
		}

		String result = "";
		for( String comment : commentlines) {
			result += indent + "// " + comment;
		}
		return result;
	}
	
	
}

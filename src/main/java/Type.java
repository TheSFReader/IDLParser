import java.util.ArrayList;
import java.util.List;

public class Type {

	public Type(String string, String eventName) {
		value = string;
		this.eventName = eventName;
	}
	Object value;
	String eventName;
	List<Type> children = new ArrayList<>();
	public List<String> commentlines;

	public String toString() {
		String result = value + "(" + eventName + ")";
		if (! children.isEmpty()) {
			result += "=" + children;
		}
		return result;
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
			for( Type child : children) {
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

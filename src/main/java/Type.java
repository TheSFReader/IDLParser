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
		if(children.isEmpty())
			result += "/\n";
		else {
			result += "\n";
			for( Type child : children) {
				result += child.output(level+1);
			}
			result+= tabResult + "/" + value + "\n";
		}
		
		return result;
	}
}

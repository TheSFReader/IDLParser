package ptypes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

	public PType findPType(String trail) {
		String[] cutTrail = trail.split("[.:]");
		List<String> trailList = Arrays.asList(cutTrail);
		
		// Filter out empty elements;
		trailList =  trailList.stream()
			    .filter(p -> !p.isEmpty()).collect(Collectors.toList());
		
		if(trailList.isEmpty()) {
			return null;
		}
		
		
		PType currentScope = this;
		PType found = null;
		while (found == null && currentScope != null) {
			// At each level going up, check if we find the definition going down
			found =  currentScope.findTypeDescending(trailList, 0);
			currentScope = currentScope.parent;
		}
		return found;
	}

	protected PType findTypeDescending(List<String> list, int index) {
		String name = list.get(index);
		for(PType child : children ) {
			if(name.equals(child.value)) {
				if(list.size() == index +1) {
					return child;
				}
				PType childFound = child.findTypeDescending(list, index + 1);
				if(  childFound != null) {
					return childFound;
				}
			}
		}
		return null;
	}
}

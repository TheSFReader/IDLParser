package ptypes;

public class PConst extends PType {

	public PConst(String string, String eventName) {
		super(string, eventName);
	}
	
	@Override
	public String toIDL(String currentIndent) {
		String typeName = null;
		String constValue = null;
		for(PType child : children) {
			if( child.eventName.contentEquals("TypeSpec")) {
				typeName = child.toIDL(currentIndent);
			} else if(child.eventName.contentEquals("ConstExpr")){
				constValue  = child.value;
			}
			
		}
		
		if( typeName != null && constValue != null && this.value != null) {
			return currentIndent + "const " + typeName + " " + this.value + " = " + constValue + ";\n";
		}
		return null;
	}

}

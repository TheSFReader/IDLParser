import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import IDL.IDLParser.Array_declaratorContext;
import IDL.IDLParser.AtkeyContext;
import IDL.IDLParser.Const_declContext;
import IDL.IDLParser.Const_expContext;
import IDL.IDLParser.Const_typeContext;
import IDL.IDLParser.Enum_typeContext;
import IDL.IDLParser.EnumeratorContext;
import IDL.IDLParser.Fixed_array_sizeContext;
import IDL.IDLParser.Key_elemContext;
import IDL.IDLParser.Key_nameContext;
import IDL.IDLParser.KeydefContext;
import IDL.IDLParser.MemberContext;
import IDL.IDLParser.ModuleContext;
import IDL.IDLParser.Sequence_typeContext;
import IDL.IDLParser.String_typeContext;
import IDL.IDLParser.Struct_typeContext;
import IDL.IDLParser.Toplevel_falseContext;
import IDL.IDLParser.Type_specContext;
import IDL.IDLParserBaseListener;

public class MyIDLListener extends IDLParserBaseListener {
	
	Deque<Type> typeStack = new ArrayDeque<>();
	MyIDLListener() {
		typeStack.push(new Type("Root",""));
	}
	
	@Override
	public void enterModule(ModuleContext ctx) {
		pushType(ctx,1,"Module");
	}

	@Override
	public void exitModule(ModuleContext ctx) {
		popType();
	}
	
	@Override
	public void enterEnum_type(Enum_typeContext ctx) {
		pushType(ctx,1,"Enum");
	}
	
	@Override
	public void exitEnum_type(Enum_typeContext ctx) {
		popType();
	}
	
	
	@Override
	public void enterEnumerator(EnumeratorContext ctx) {
		pushType(ctx,0,"Enumerator");
	}
	@Override
	public void exitEnumerator(EnumeratorContext ctx) {
		popType();
	}
	
	
	@Override
	public void enterStruct_type(Struct_typeContext ctx) {
		pushType(ctx,1,"Struct");
	}

	@Override
	public void exitStruct_type(Struct_typeContext ctx) {
		popType();
	}
	
	@Override
	public void enterToplevel_false(Toplevel_falseContext ctx) {
		addToLastBrother("NoTopLevel", "NoTopLevel");
	}
	

	
	@Override
	public void enterMember(MemberContext ctx) {
		pushType(ctx,1,"Member");
	}
	
	@Override
	public void exitMember(MemberContext ctx) {
		popType();
	}
	
	@Override
	public void enterType_spec(Type_specContext ctx) {
		pushType(ctx,0,"TypeSpec");
	}
	
	@Override
	public void exitType_spec(Type_specContext ctx) {
		popType();
	}
	
	@Override
	public void enterKeydef(KeydefContext ctx) {
		pushType(ctx,1,"KeyDef");
	}
	
	@Override
	public void exitKeydef(KeydefContext ctx) {
		popType();
	}
	
	
	@Override
	public void enterKey_name(Key_nameContext ctx) {
		pushType(ctx,0,"KeyName");
	}
	@Override
	public void exitKey_name(Key_nameContext ctx) {
		popType();
	}
	
	@Override
	public void enterKey_elem(Key_elemContext ctx) {
		addType(ctx,"KeyElem");
	}

	
	@Override
	public void enterSequence_type(Sequence_typeContext ctx) {
		setParentName(getChildText(ctx,2));
		pushType(ctx,0,"Sequence");
	}
	
	@Override
	public void exitSequence_type(Sequence_typeContext ctx) {
		if( ctx.getChildCount() > 4 ) {
			setLastBrotherTypeName("Bound");
		}
		popType();
	}
	
	
	@Override
	public void exitAtkey(AtkeyContext ctx) {
		addToLastBrother("AtKey","AtKey");
	}
	
	@Override
	public void exitFixed_array_size(Fixed_array_sizeContext ctx) {
		setLastBrotherTypeName("FixedArraySize");
	}
		
	@Override
	public void enterArray_declarator(Array_declaratorContext ctx) {
		setParentName(getChildText(ctx,0));
	}
	
	@Override
	public void enterString_type(String_typeContext ctx) {
		setParentName(getChildText(ctx,0));
	}
	
	@Override
	public void exitString_type(String_typeContext ctx) {
		
		if(ctx.getChildCount() > 1) {
			setLastBrotherTypeName("Bound");
		}
	}
	
	
	@Override
	public void enterConst_decl(Const_declContext ctx) {
		pushType(ctx,2,"Const");
	}
	
	
	@Override
	public void exitConst_decl(Const_declContext ctx) {
		popType();
	}
	
	@Override
	public void enterConst_type(Const_typeContext ctx) {
		addType(ctx,"Type");
	}
	
	@Override
	public void enterConst_exp(Const_expContext ctx) {
		pushType(ctx,"ConstExpr");
	}
	
	@Override
	public void exitConst_exp(Const_expContext ctx) {
		popType();
	}
	
	/*
	 * Helpers
	 */
	
	private String getChildText(ParserRuleContext ctx, int index) {
		return ctx.children.get(index).getText();
	}
	
	private void setParentName(String name) {
		typeStack.peek().value = name;
	}
		
	private Type getLastBrother() {
		List<Type> brothers = typeStack.peek().children;
		if(brothers.size() > 0) {
			return brothers.get(brothers.size() -1);
		}
		return null;
	}
	
	private void setLastBrotherTypeName(String typeName) {
		Type lastBrother = getLastBrother();
		if( lastBrother != null) {
			lastBrother.eventName = typeName;
		}
	}
	
	
	private void addToLastBrother(String name, String typeName) {
		Type type = new Type(name,typeName);
		getLastBrother().children.add(type);
		
	}
	
	private void  pushType(ParserRuleContext ctx, int index, String typeName) {		
		Type type = addType( ctx,  index,  typeName);
		typeStack.push(type);
	}
	
	private void  pushType(ParserRuleContext ctx, String typeName) {
		String name = ctx.getText();
		pushType(name,typeName);
		
	}
	
	private void  pushType(String name, String typeName) {
		Type type = addType(name,typeName);
		typeStack.push(type);	
	}
	
	
	private Type addType(ParserRuleContext ctx, int index, String typeName) {
		ParseTree child = ctx.getChild(index);
		if( child.getChildCount() > 0)
			child = child.getChild(0);
		String name = child.getText();
		return addType(name,typeName);
	}
	
	
	private Type addType(ParserRuleContext ctx, String typeName) {
		return addType(ctx.getText(), typeName);
	}
	
	private Type addType(String name, String typeName) {
		Type type = new Type(name,typeName);
		typeStack.peek().children.add(type);
		return type;
	}
	
	private void popType() {
		typeStack.pop();
	}
	
}

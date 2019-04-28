import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import IDL.IDLParser;
import IDL.IDLParser.Add_exprContext;
import IDL.IDLParser.And_exprContext;
import IDL.IDLParser.Array_declaratorContext;
import IDL.IDLParser.AtkeyContext;
import IDL.IDLParser.BoolLiteralContext;
import IDL.IDLParser.Const_declContext;
import IDL.IDLParser.Const_expContext;
import IDL.IDLParser.Const_typeContext;
import IDL.IDLParser.Enum_typeContext;
import IDL.IDLParser.EnumeratorContext;
import IDL.IDLParser.Fixed_array_sizeContext;
import IDL.IDLParser.FloatLiteralContext;
import IDL.IDLParser.HexLitteralContext;
import IDL.IDLParser.IntLitteralContext;
import IDL.IDLParser.Key_elemContext;
import IDL.IDLParser.Key_nameContext;
import IDL.IDLParser.KeydefContext;
import IDL.IDLParser.MemberContext;
import IDL.IDLParser.ModuleContext;
import IDL.IDLParser.Mult_exprContext;
import IDL.IDLParser.Or_exprContext;
import IDL.IDLParser.Primary_exprContext;
import IDL.IDLParser.Sequence_typeContext;
import IDL.IDLParser.Shift_exprContext;
import IDL.IDLParser.String_typeContext;
import IDL.IDLParser.Struct_typeContext;
import IDL.IDLParser.Toplevel_falseContext;
import IDL.IDLParser.Type_specContext;
import IDL.IDLParser.Unary_exprContext;
import IDL.IDLParser.Xor_exprContext;
import IDL.IDLParserBaseListener;

public class MyIDLListener extends IDLParserBaseListener {

	ParseTreeProperty<Integer> expressionsEvaluatorValues = new ParseTreeProperty<>();

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
		Integer val = expressionsEvaluatorValues.get(ctx.or_expr());
		expressionsEvaluatorValues.put(ctx,val);
		if( val != null) {
			typeStack.peek().value += " (val : " + val + ")";
		}
		popType();
	}
	
	@Override
	public void exitPrimary_expr(Primary_exprContext ctx) {
		if(ctx.literal() != null) {
			expressionsEvaluatorValues.put(ctx, expressionsEvaluatorValues.get(ctx.literal()));
		} else if(ctx.const_exp() != null){
			expressionsEvaluatorValues.put(ctx, expressionsEvaluatorValues.get(ctx.const_exp()));
		} else if(ctx.scoped_name() != null){
			expressionsEvaluatorValues.put(ctx, expressionsEvaluatorValues.get(ctx.scoped_name()));
		}
	}
	
	@Override
	public void exitOr_expr(Or_exprContext ctx) {
		
		Integer val = 0;
		boolean found = false;
		for(Xor_exprContext xor :  ctx.xor_expr()) {
			Integer newVal = expressionsEvaluatorValues.get(xor);
			if(newVal !=null) {
				val |= newVal;
				found = true;
			}
		}
		if( found)
		{
			expressionsEvaluatorValues.put(ctx, val);
		}
	}
	
	@Override
	public void exitXor_expr(Xor_exprContext ctx) {
		
		Integer val = 0;
		boolean found = false;
		for(And_exprContext and :  ctx.and_expr()) {
			Integer newVal = expressionsEvaluatorValues.get(and);
			if(newVal !=null) {
				val ^= newVal;
				found = true;
			}
		}
		if( found)
		{
			expressionsEvaluatorValues.put(ctx, val);
		}
	}
	
	@Override
	public void exitAnd_expr(And_exprContext ctx) {
		
		Integer val = expressionsEvaluatorValues.get(ctx.shift_expr().get(0));
		if( val != null) {
			boolean found = false;
			for(Shift_exprContext and :  ctx.shift_expr()) {
				Integer newVal = expressionsEvaluatorValues.get(and);
				if(newVal !=null) {
					val &= newVal;
					found = true;
				}
			}
			if( found)
			{
				expressionsEvaluatorValues.put(ctx, val);
			}
		}
	}
	
	@Override
	public void exitShift_expr(Shift_exprContext ctx) {
		Integer val = expressionsEvaluatorValues.get(ctx.add_expr().get(0));
		if( val != null)
		{
			int i  = 1;
			while (i < ctx.getChildCount()) {
				ParseTree opChild = ctx.getChild(i++);
				ParseTree shiftByChild = ctx.getChild(i++);
				if( opChild instanceof TerminalNode) {
					int type = ((TerminalNode)opChild).getSymbol().getType();
					Integer shiftByVal = expressionsEvaluatorValues.get(shiftByChild);
					if( shiftByVal != null) {
						if( type== IDLParser.RIGHT_SHIFT) {
							val >>= shiftByVal;
						} else if(type== IDLParser.LEFT_SHIFT) {
							val <<= shiftByVal;
						} else {
							System.err.println("OOPS");
						}
					}	
				}	
			}
			expressionsEvaluatorValues.put(ctx, val);
		}
		
	}

	@Override
	public void exitAdd_expr(Add_exprContext ctx) {
		Integer val = expressionsEvaluatorValues.get(ctx.mult_expr().get(0));
		if( val != null)
		{
			int i  = 1;
			while (i < ctx.getChildCount()) {
				ParseTree opChild = ctx.getChild(i++);
				ParseTree addChild = ctx.getChild(i++);
				if( opChild instanceof TerminalNode) {
					int type = ((TerminalNode)opChild).getSymbol().getType();
					Integer addVal = expressionsEvaluatorValues.get(addChild);
					if( addVal != null) {
						if( type== IDLParser.PLUS) {
							val += addVal;
						} else if(type== IDLParser.MINUS) {
							val -= addVal;
						} else {
							System.err.println("OOPS");
						}
					}	
				}	
			}
			expressionsEvaluatorValues.put(ctx, val);
		}
		
	}
	
	@Override
	public void exitMult_expr(Mult_exprContext ctx) {
		Integer val = expressionsEvaluatorValues.get(ctx.unary_expr().get(0));
		if( val != null)
		{
			int i  = 1;
			while (i < ctx.getChildCount()) {
				ParseTree opChild = ctx.getChild(i++);
				ParseTree mulChild = ctx.getChild(i++);
				if( opChild instanceof TerminalNode) {
					int type = ((TerminalNode)opChild).getSymbol().getType();
					Integer mulVal = expressionsEvaluatorValues.get(mulChild);
					if( mulVal != null) {
						if( type== IDLParser.STAR) {
							val *= mulVal;
						} else if(type== IDLParser.SLASH) {
							val /= mulVal;
						} else if(type== IDLParser.PERCENT) {
							val %= mulVal;
						} else {
							System.err.println("OOPS");
						}
					}	
				}	
			}
			expressionsEvaluatorValues.put(ctx, val);
		}
		
	}
	

@Override
public void exitUnary_expr(Unary_exprContext ctx) {
	
	Integer val = expressionsEvaluatorValues.get(ctx.primary_expr());
	if( val != null) {
		if( ctx.op != null) {
			if( ctx.op.MINUS() != null) {
				val = -val;
			} if( ctx.op.TILDE() != null) {
				val = ~val;
			}
		}
		expressionsEvaluatorValues.put(ctx, val);
	}
	
}


	@Override
	public void exitIntLitteral(IntLitteralContext ctx) {
		Integer val = Integer.valueOf(ctx.getText());
		expressionsEvaluatorValues.put(ctx, val);

	}

	@Override
	public void exitHexLitteral(HexLitteralContext ctx) {
		String hexString = ctx.getText().substring(2);
		Integer val = Integer.valueOf(hexString, 16);
		expressionsEvaluatorValues.put(ctx, val);
	}
	
	@Override
	public void exitFloatLiteral(FloatLiteralContext ctx) {
		Float val = Float.valueOf(ctx.getText());
		//// TODO
		expressionsEvaluatorValues.put(ctx, val.intValue());
	}
	
	@Override
	public void exitBoolLiteral(BoolLiteralContext ctx) {
		Boolean val = Boolean.valueOf(ctx.getText());
		/// TODO
		expressionsEvaluatorValues.put(ctx, val ? 1 : 0);
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

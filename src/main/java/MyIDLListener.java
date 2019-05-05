import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import IDL.IDLParser;
import IDL.IDLParser.Add_exprContext;
import IDL.IDLParser.And_exprContext;
import IDL.IDLParser.Array_declaratorContext;
import IDL.IDLParser.AtkeyContext;
import IDL.IDLParser.BoolLiteralContext;
import IDL.IDLParser.Const_declContext;
import IDL.IDLParser.Const_expContext;
import IDL.IDLParser.Const_typeContext;
import IDL.IDLParser.DeclaratorContext;
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
import IDL.IDLParser.Type_declaratorContext;
import IDL.IDLParser.Type_specContext;
import IDL.IDLParser.Unary_exprContext;
import IDL.IDLParser.Xor_exprContext;
import IDL.IDLParserBaseListener;
import ptypes.PConst;
import ptypes.PDeclarator;
import ptypes.PEnum;
import ptypes.PKeyDef;
import ptypes.PMember;
import ptypes.PModule;
import ptypes.PStruct;
import ptypes.PType;
import ptypes.PTypeSpec;
import ptypes.PTypedef;

public class MyIDLListener extends IDLParserBaseListener {

	ParseTreeProperty<Object> expressionsEvaluatorValues = new ParseTreeProperty<>();

	HashMap<String,Object> constValues = new HashMap<>();
	
	Deque<PType> typeStack = new ArrayDeque<>();

	private CommonTokenStream tokens;

	public MyIDLListener(CommonTokenStream tokens) {
		typeStack.push(new PType("Root",""));
		this.tokens = tokens;
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
	public void enterType_declarator(Type_declaratorContext ctx) {
		pushType(ctx,0,"Typedef");
	}
	
	@Override
	public void exitType_declarator(Type_declaratorContext ctx) {
		popType();
	}
	
	
	@Override
	public void enterDeclarator(DeclaratorContext ctx) {
		pushType(ctx,0,"Declarator");
	}
	@Override
	public void exitDeclarator(DeclaratorContext ctx) {
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
		Object val = expressionsEvaluatorValues.get(ctx.const_exp());
		if(val != null) {
			constValues.put(ctx.ID().getText(), val);
		}
		popType();
	}

	@Override
	public void enterConst_type(Const_typeContext ctx) {
		addType(ctx,"TypeSpec");
	}

	@Override
	public void enterConst_exp(Const_expContext ctx) {
		pushType(ctx,"ConstExpr");
	}

	@Override
	public void exitConst_exp(Const_expContext ctx) {
		Object val = expressionsEvaluatorValues.get(ctx.or_expr());
		expressionsEvaluatorValues.put(ctx,val);
		if( val != null) {
			typeStack.peek().value = "" + val ;
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
			String nameAndScope = ctx.scoped_name().getText();
			Object val = constValues.get(nameAndScope);
			if( val != null) {
				expressionsEvaluatorValues.put(ctx, val);
			}
		}
	}

	@Override
	public void exitOr_expr(Or_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}

	@Override
	public void exitXor_expr(Xor_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}

	@Override
	public void exitAnd_expr(And_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}
	
	@Override
	public void exitShift_expr(Shift_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}

	@Override
	public void exitAdd_expr(Add_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}

	@Override
	public void exitMult_expr(Mult_exprContext ctx) {
		evalOneLevelOfMathExpr(ctx);
	}


	@Override
	public void exitUnary_expr(Unary_exprContext ctx) {

		Object val = expressionsEvaluatorValues.get(ctx.primary_expr());
		if( val != null) {
			if( ctx.unary_operator() != null) {
				if( ctx.unary_operator().MINUS() != null) {
					if( val instanceof Integer) {
						val = - (Integer) val;
					} else if (val instanceof Float) {
						val = - (Float) val;
					}
						
				} else if( ctx.unary_operator().TILDE() != null) {
					if( val instanceof Integer) {
						val = ~ (Integer) val;
					}
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
		expressionsEvaluatorValues.put(ctx, val);
	}

	@Override
	public void exitBoolLiteral(BoolLiteralContext ctx) {
		Boolean val = Boolean.valueOf(ctx.getText());
		/// TODO
		expressionsEvaluatorValues.put(ctx, val ? 1 : 0);
	}

	
	private void evalOneLevelOfMathExpr(ParseTree tree) {
		Object val = expressionsEvaluatorValues.get(tree.getChild(0));
		if( val != null) {
			if( val instanceof Integer) {
				val = evalOneLevelOfMathExprInteger(tree);
				expressionsEvaluatorValues.put(tree, val);
			} else if( val instanceof Float) {
				val = evalOneLevelOfMathExprFloat(tree);
				expressionsEvaluatorValues.put(tree, val);
			}
		}
		else {
			System.out.println("OOOPS");
		}
		
	}
	
	private Integer evalOneLevelOfMathExprInteger(ParseTree tree) {
		Integer val = (Integer) expressionsEvaluatorValues.get(tree.getChild(0));
		if( val != null) {
			int i  = 1;
			while (i < tree.getChildCount()) {
				ParseTree opChild = tree.getChild(i++);
				ParseTree newChild = tree.getChild(i++);
				if( opChild instanceof TerminalNode) {
					int type = ((TerminalNode)opChild).getSymbol().getType();
					Object newChildObject = expressionsEvaluatorValues.get(newChild);
					
					Integer newChildVal;
					if( newChildObject instanceof Integer) {
						newChildVal = (Integer) newChildObject;
					} else if (newChildObject instanceof Float) {
						newChildVal = ((Float) newChildObject).intValue();
					} else {
						/// TODO ERROR
						System.out.println("OOPS");
						continue;
					}
					
					if( newChildVal != null) {
						
						switch(type)  {
						case IDLParser.RIGHT_SHIFT:
							val >>= newChildVal;
							break;
						case IDLParser.LEFT_SHIFT:
							val <<= newChildVal;
							break;
						case IDLParser.PLUS:
							val += newChildVal;
							break;
						case IDLParser.MINUS:
							val -= newChildVal;
							break;
						case IDLParser.STAR:
							val *= newChildVal;
							break;
						case IDLParser.SLASH:
							val /= newChildVal;
							break;
						case IDLParser.PERCENT:
							val %= newChildVal;
							break;
						case IDLParser.AMPERSAND:
							val &= newChildVal;
							break;
						case IDLParser.PIPE:
							val |= newChildVal;
							break;
						case IDLParser.CARET:
							val ^= newChildVal;
							break;
							
						default:
							System.err.println("OOPS!");
							break;
						}
					}	
				}	
			}
		}
		return val;
		
	}
	
	private Float evalOneLevelOfMathExprFloat(ParseTree tree) {
		Float val = (Float)expressionsEvaluatorValues.get(tree.getChild(0));
		if( val != null) {
			int i  = 1;
			while (i < tree.getChildCount()) {
				ParseTree opChild = tree.getChild(i++);
				ParseTree newChild = tree.getChild(i++);
				if( opChild instanceof TerminalNode) {
					int type = ((TerminalNode)opChild).getSymbol().getType();
					

					Object newChildObject = expressionsEvaluatorValues.get(newChild);
					Float newChildVal;
					if( newChildObject instanceof Float) {
						newChildVal = (Float) newChildObject;
					} else if (newChildObject instanceof Integer) {
						newChildVal =(float) ((Integer) newChildObject);
					} else {
						/// TODO ERROR
						continue;
					}
					
					if( newChildVal != null) {
						
						switch(type)  {
						case IDLParser.PLUS:
							val += newChildVal;
							break;
						case IDLParser.MINUS:
							val -= newChildVal;
							break;
						case IDLParser.STAR:
							val *= newChildVal;
							break;
						case IDLParser.SLASH:
							val /= newChildVal;
							break;
						case IDLParser.PERCENT:
							val %= newChildVal;
							break;
							
						default:
							System.err.println("OOPS!");
							break;
						}
					}	
				}	
			}
		}
		return val;
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

	private PType getLastBrother() {
		List<PType> brothers = typeStack.peek().children;
		if(brothers.size() > 0) {
			return brothers.get(brothers.size() -1);
		}
		return null;
	}

	private void setLastBrotherTypeName(String typeName) {
		PType lastBrother = getLastBrother();
		if( lastBrother != null) {
			lastBrother.eventName = typeName;
		}
	}


	private void addToLastBrother(String name, String typeName) {
		PType type = new PType(name,typeName);
		getLastBrother().children.add(type);

	}

	private void  pushType(ParserRuleContext ctx, int index, String typeName) {	
		
		PType type = addType( ctx,  index,  typeName);
		typeStack.push(type);
	}

	private void  pushType(ParserRuleContext ctx, String typeName) {
		String name = ctx.getText();
		List<String> comments = getPreComments(ctx);
		pushType(name,typeName, comments);

	}

	private void  pushType(String name, String typeName, List<String> comments) {
		PType type = addType(name,typeName, comments);
		typeStack.push(type);	
	}


	private PType addType(ParserRuleContext ctx, int index, String typeName) {
		ParseTree child = ctx.getChild(index);
		if( child.getChildCount() > 0)
			child = child.getChild(0);
		String name = child.getText();
		
		List<String> comments = getPreComments(ctx);
		return addType(name,typeName, comments);
	}


	private PType addType(ParserRuleContext ctx, String typeName) {
		List<String> comments = getPreComments(ctx);
		return addType(ctx.getText(), typeName, comments);
	}

	private PType addType(String name, String typeName, List<String> comments) {
		PType type = null;
		
		switch(typeName) {
		case "Module":
			type = new PModule(name,typeName);
			break;

		case "Const":
			type = new PConst(name,typeName);
			break;
			
		case "Enum":
			type = new PEnum(name,typeName);
			break;
		case "Struct":
			type = new PStruct(name,typeName);
			break;
		case "Member":
			type = new PMember(name,typeName);
			break;
			
		case "TypeSpec":
			type = new PTypeSpec(name,typeName);
			break;
			
		
		case "Declarator":
			type = new PDeclarator(name,typeName);
			break;
			
		case "KeyDef":
			type = new PKeyDef(name,typeName);
			break;
			
		case "Typedef":
			type = new PTypedef(name,typeName);
			break;			
		}

		
		if( type == null) type = new PType(name,typeName);
		
		if(comments != null) {
			type.commentlines = comments; 
		}
		typeStack.peek().children.add(type);
		return type;
	}

	private void popType() {
		typeStack.pop();
	}
	
	
	private List<String> getPreComments(ParserRuleContext tree) {
		List<String> result = new ArrayList<>();
		Token startToken = tree.getStart();
		int tokenIndex = startToken.getTokenIndex();
		List<Token> comments = tokens.getHiddenTokensToLeft(tokenIndex);
		if(comments != null) { 
			for( Token token : comments) {
				String strippedComment = token.getText().replaceAll("//","").trim();
				if(!StringUtils.isBlank(strippedComment)) {
					result.add(strippedComment + "\n");
				}
			}
			if( !result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

}

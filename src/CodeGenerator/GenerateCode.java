package CodeGenerator;
import AST.*;
import Utilities.Error;
import Utilities.Visitor;
import java.util.*;
import Instruction.*;
import Jasmin.*;

class GenerateCode extends Visitor {

	private Generator gen;
	private ClassFile classFile;
	private ClassDecl currentClass;
	private boolean insideLoop = false;
	private boolean insideSwitch = false;
	private boolean RHSofAssignment = false;


    // if a left-hand side of an assignment is an actual parameter,
    // RHSofAssignment will be false, but the extra dup is needed; so
    // use this for that. Should be set to true before parameters are
    // visited in Inovcation, CInvocation, and New
    private boolean isParameter = false; 
    private boolean StringBuilderCreated = false;
	
	public GenerateCode(Generator g, boolean debug) {
		gen = g;
		this.debug = debug;
		classFile = gen.getClassFile();
	}

	public void setCurrentClass(ClassDecl cd) {
		this.currentClass = cd;
	}

	// ASSIGNMENT (PROVIDED)
	public Object visitAssignment(Assignment as) {
		println(as.line + ": Assignment:\tGenerating code for an Assignment.");
		classFile.addComment(as, "Assignment");
		/* If a reference is needed then compute it
	          (If array type then generate reference to the	target & index)
	          - a reference is never needed if as.left() is an instance of a NameExpr
	          - a reference can be computed for a FieldRef by visiting the target
	          - a reference can be computed for an ArrayAccessExpr by visiting its target 
		 */
		if (as.left() instanceof FieldRef) {
			println(as.line + ": Generating reference for FieldRef target ");
			FieldRef fr= (FieldRef)as.left();
			fr.target().visit(this);		
			// if the target is a New and the field is static, then the reference isn't needed, so pop it! 
			if (fr.myDecl.isStatic()) // && fr.target() instanceof New) // 3/10/2017 - temporarily commented out
			    // issue pop if target is NOT a class name.
			    if (fr.target() instanceof NameExpr && (((NameExpr)fr.target()).myDecl instanceof ClassDecl))
				;
			    else
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));			
		} else if (as.left() instanceof ArrayAccessExpr) {
			println(as.line + ": Generating reference for Array Access target");
			ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
			classFile.addComment(as, "ArrayAccessExpr target");
			ae.target().visit(this);
			classFile.addComment(as, "ArrayAccessExpr index");
			ae.index().visit(this);
		}

		/* If the assignment operator is <op>= then
	            -- If the left hand side is a non-static field (non array): dup (object ref) + getfield
	            -- If the left hand side is a static field (non array): getstatic   
	            -- If the left hand side is an array reference: dup2 +	Xaload 
				-- If the left hand side is a local (non array): generate code for it: Xload Y 
		 */	        
		if (as.op().kind != AssignmentOp.EQ) {
			if (as.left() instanceof FieldRef) {
				println(as.line + ": Duplicating reference and getting value for LHS (FieldRef/<op>=)");
				FieldRef fr = (FieldRef)as.left();
				if (!fr.myDecl.isStatic()) {
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
					classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield, fr.targetType.typeName(),
							fr.fieldName().getname(), fr.type.signature()));
				} else 
					classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic, fr.targetType.typeName(),
							fr.fieldName().getname(), fr.type.signature()));
			} else if (as.left() instanceof ArrayAccessExpr) {
				println(as.line + ": Duplicating reference and getting value for LHS (ArrayAccessRef/<op>=)");
				ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
				classFile.addInstruction(new Instruction(Generator.getArrayLoadInstruction(ae.type)));
			} else { // NameExpr
				println(as.line + ": Getting value for LHS (NameExpr/<op>=)");
				NameExpr ne = (NameExpr)as.left();
				int address = ((VarDecl)ne.myDecl).address();

				if (address < 4)
					classFile.addInstruction(new Instruction(Generator.getLoadInstruction(((VarDecl)ne.myDecl).type(), address, true)));
				else
					classFile.addInstruction(new SimpleInstruction(Generator.getLoadInstruction(((VarDecl)ne.myDecl).type(), address, true), address));
			}
		}

		/* Visit the right hand side (RHS) */
		boolean oldRHSofAssignment = RHSofAssignment;
		RHSofAssignment = true;
		as.right().visit(this);
		RHSofAssignment = oldRHSofAssignment;
		/* Convert the right hand sides type to that of the entire assignment */

		if (as.op().kind != AssignmentOp.LSHIFTEQ &&
		    as.op().kind != AssignmentOp.RSHIFTEQ &&
		    as.op().kind != AssignmentOp.RRSHIFTEQ)
		    gen.dataConvert(as.right().type, as.type);

		/* If the assignment operator is <op>= then
				- Execute the operator
		 */
		if (as.op().kind != AssignmentOp.EQ)
			classFile.addInstruction(new Instruction(Generator.getBinaryAssignmentOpInstruction(as.op(), as.type)));

		/* If we are the right hand side of an assignment
		     -- If the left hand side is a non-static field (non array): dup_x1/dup2_x1
			 -- If the left hand side is a static field (non array): dup/dup2
			 -- If the left hand side is an array reference: dup_x2/dup2_x2 
			 -- If the left hand side is a local (non array): dup/dup2 
		 */    
		if (RHSofAssignment || isParameter) {
			String dupInstString = "";
			if (as.left() instanceof FieldRef) {
				FieldRef fr = (FieldRef)as.left();
				if (!fr.myDecl.isStatic())  
					dupInstString = "dup" + (fr.type.width() == 2 ? "2" : "") + "_x1";
				else 
					dupInstString = "dup" + (fr.type.width() == 2 ? "2" : "");
			} else if (as.left() instanceof ArrayAccessExpr) {
				ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
				dupInstString = "dup" + (ae.type.width() == 2 ? "2" : "") + "_x2";
			} else { // NameExpr
				NameExpr ne = (NameExpr)as.left();
				dupInstString = "dup" + (ne.type.width() == 2 ? "2" : "");
			}
			classFile.addInstruction(new Instruction(Generator.getOpCodeFromString(dupInstString)));
		}

		/* Store
		     - If LHS is a field: putfield/putstatic
			 -- if LHS is an array reference: Xastore 
			 -- if LHS is a local: Xstore Y
		 */
		if (as.left() instanceof FieldRef) {
			FieldRef fr = (FieldRef)as.left();
			if (!fr.myDecl.isStatic()) 
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putfield,
						fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
			else 
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putstatic,
						fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
		} else if (as.left() instanceof ArrayAccessExpr) {
			ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
			classFile.addInstruction(new Instruction(Generator.getArrayStoreInstruction(ae.type)));
		} else { // NameExpr				
			NameExpr ne = (NameExpr)as.left();
			int address = ((VarDecl)ne.myDecl).address() ;

			// CHECK!!! TODO: changed 'true' to 'false' in these getStoreInstruction calls below....
			if (address < 4)
				classFile.addInstruction(new Instruction(Generator.getStoreInstruction(((VarDecl)ne.myDecl).type(), address, false)));
			else {
				classFile.addInstruction(new SimpleInstruction(Generator.getStoreInstruction(((VarDecl)ne.myDecl).type(), address, false), address));
			}
		}
		classFile.addComment(as, "End Assignment");
		return null;
	}

	// EXPRESSION STATEMENT (PROVIDED)
	public Object visitExprStat(ExprStat es) {	
		println(es.line + ": ExprStat:\tVisiting an Expression Statement.");
		classFile.addComment(es, "Expression Statement");

		es.expression().visit(this);
		if (es.expression() instanceof Invocation) {
			Invocation in = (Invocation)es.expression();

			if (in.targetType.isStringType() && in.methodName().getname().equals("length")) {
			    println(es.line + ": ExprStat:\tInvocation of method length, return value not uses.");
			    gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
			} else if (in.targetType.isStringType() && in.methodName().getname().equals("charAt")) {
			    println(es.line + ": ExprStat:\tInvocation of method charAt, return value not uses.");
			    gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
			} else if (in.targetMethod.returnType().isVoidType())
				println(es.line + ": ExprStat:\tInvocation of Void method where return value is not used anyways (no POP needed)."); 
			else {
				println(es.line + ": ExprStat:\tPOP added to remove non used return value for a '" + es.expression().getClass().getName() + "'.");
				gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
			}
		}
		else 
			if (!(es.expression() instanceof Assignment)) {
				gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
				println(es.line + ": ExprStat:\tPOP added to remove unused value left on stack for a '" + es.expression().getClass().getName() + "'.");
			}
		classFile.addComment(es, "End ExprStat");
		return null;
	}

	// FIELD DECLARATION (PROVIDED)
	public Object visitFieldDecl(FieldDecl fd) {
		println(fd.line + ": FieldDecl:\tGenerating code.");

		classFile.addField(fd);

		return null;
	}

	// FIELD REFERENCE (PROVIDED)
	public Object visitFieldRef(FieldRef fr) {
		println(fr.line + ": FieldRef:\tGenerating code (getfield code only!).");

		// Changed June 22 2012 Array
		// If we have and field reference with the name 'length' and an array target type
		if (fr.myDecl == null) { // We had a array.length reference. Not the nicest way to check!!
			classFile.addComment(fr, "Array length");
			fr.target().visit(this);
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_arraylength));
			return null;
		}

		classFile.addComment(fr,  "Field Reference");

		// Note when visiting this node we assume that the field reference
		// is not a left hand side, i.e. we always generate 'getfield' code.

		// Generate code for the target. This leaves a reference on the 
		// stack. pop if the field is static!
		fr.target().visit(this);
		if (!fr.myDecl.modifiers.isStatic()) 
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield, 
					fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
		else {
			// If the target is that name of a class and the field is static, then we don't need a pop; else we do:
			if (!(fr.target() instanceof NameExpr && (((NameExpr)fr.target()).myDecl instanceof ClassDecl))) 
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic,
					fr.targetType.typeName(), fr.fieldName().getname(),  fr.type.signature()));
		}
		classFile.addComment(fr, "End FieldRef");
		return null;
	}

	// LITERAL (PROVIDED)
	public Object visitLiteral(Literal li) {
		println(li.line + ": Literal:\tGenerating code for Literal '" + li.getText() + "'.");
		classFile.addComment(li, "Literal");

		switch (li.getKind()) {
		case Literal.ByteKind:
		case Literal.CharKind:
		case Literal.ShortKind:
		case Literal.IntKind:
			gen.loadInt(li.getText());
			break;
		case Literal.NullKind:
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_aconst_null));
			break;
		case Literal.BooleanKind:
			if (li.getText().equals("true")) 
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
			else
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_0));
			break;
		case Literal.FloatKind:
			gen.loadFloat(li.getText());
			break;
		case Literal.DoubleKind:
			gen.loadDouble(li.getText());
			break;
		case Literal.StringKind:
			gen.loadString(li.getText());
			break;
		case Literal.LongKind:
			gen.loadLong(li.getText());
			break;	    
		}
		classFile.addComment(li,  "End Literal");
		return null;
	}

	// METHOD DECLARATION (PROVIDED)
	public Object visitMethodDecl(MethodDecl md) {
		println(md.line + ": MethodDecl:\tGenerating code for method '" + md.name().getname() + "'.");	
		classFile.startMethod(md);

		classFile.addComment(md, "Method Declaration (" + md.name() + ")");

		if (md.block() !=null) 
			md.block().visit(this);
		gen.endMethod(md);
		return null;
	}

	// SWITCH STATEMENT (PROVIDED)
	public Object visitSwitchStat(SwitchStat ss) {
		println(ss.line + ": Switch Statement:\tGenerating code for Switch Statement.");
		int def = -1;
		SortedMap<Object, SwitchLabel> sm = new TreeMap<Object, SwitchLabel>();
		classFile.addComment(ss,  "Switch Statement");

		SwitchGroup sg = null;
		SwitchLabel sl = null;

		// just to make sure we can do breaks;
		boolean oldinsideSwitch = insideSwitch;
		insideSwitch = true;
		String oldBreakLabel = Generator.getBreakLabel();
		Generator.setBreakLabel("L"+gen.getLabel());

		if (ss.expr().type.isStringType()) {

		} else {		
		    // Generate code for the item to switch on.
		    ss.expr().visit(this);	
		    // Write the lookup table
		    for (int i=0;i<ss.switchBlocks().nchildren; i++) {
			sg = (SwitchGroup)ss.switchBlocks().children[i];
			sg.setLabel(gen.getLabel());
			for(int j=0; j<sg.labels().nchildren;j++) {
			    sl = (SwitchLabel)sg.labels().children[j];
			    sl.setSwitchGroup(sg);
			    if (sl.isDefault())
				def = i;
			    else
				sm.put(sl.expr().constantValue(), sl);
			}
		    }
		    
		    for (Iterator<Object> ii=sm.keySet().iterator(); ii.hasNext();) {
			sl = sm.get(ii.next());
		    }
		    
		    // default comes last, if its not there generate an empty one.
		    if (def != -1) {
			classFile.addInstruction(new LookupSwitchInstruction(RuntimeConstants.opc_lookupswitch, sm, 
									     "L" + ((SwitchGroup)ss.switchBlocks().children[def]).getLabel()));
		    } else {
			// if no default label was there then just jump to the break label.
			classFile.addInstruction(new LookupSwitchInstruction(RuntimeConstants.opc_lookupswitch, sm, 
									     Generator.getBreakLabel()));
		    }
		    
		    // Now write the code and the labels.
		    for (int i=0;i<ss.switchBlocks().nchildren; i++) {
			sg = (SwitchGroup)ss.switchBlocks().children[i];
			classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L"+sg.getLabel()));
			sg.statements().visit(this);
		    }

		}
		// Put the break label in;
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, Generator.getBreakLabel()));
		insideSwitch = oldinsideSwitch;
		Generator.setBreakLabel(oldBreakLabel);
		classFile.addComment(ss, "End SwitchStat");
		return null;
	}

	// CONTINUE STATEMENT (COMPLETED)
	public Object visitContinueStat(ContinueStat cs) {
		println(cs.line + ": ContinueStat:\tGenerating code.");
		classFile.addComment(cs, "Continue Statement");

		// YOUR CODE HERE
		if (!insideLoop) {
			Error.error("Continue statement must be inside a loop.");
		}else{
			classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, "L"+gen.getContinueLabel()));
		}
		// - END -

		classFile.addComment(cs, "End ContinueStat");
		return null;
	}

	// DO STATEMENT (COMPLETED)
	public Object visitDoStat(DoStat ds) {
		println(ds.line + ": DoStat:\tGenerating code.");
		classFile.addComment(ds, "Do Statement");

		// YOUR CODE HERE
		String topLabel = "L"+gen.getLabel();
		String endLabel = "L"+gen.getLabel();
		String contLabel = "L"+gen.getLabel();
		
		String metaBreakLabel = gen.getBreakLabel();
		String metaContinueLabel = gen.getContinueLabel();
		
		gen.setBreakLabel(endLabel);
		gen.setContinueLabel(contLabel);
		
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
		
		boolean metaInsideLoop = insideLoop;
		insideLoop = true;
		if (ds.stat() != null) {
			ds.stat().visit(this);
		}
		insideLoop = metaInsideLoop;
		
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, contLabel));
		
		if (ds.expr() != null) {
			ds.expr().visit(this);
			classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, endLabel));
		}
		
		classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, topLabel));
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, endLabel));
		
		gen.setBreakLabel(metaBreakLabel);
		gen.setContinueLabel(metaContinueLabel);
		// - END -

		classFile.addComment(ds, "End DoStat");
		return null; 
	}

	// FOR STATEMENT (COMPLETED)
	public Object visitForStat(ForStat fs) {
		println(fs.line + ": ForStat:\tGenerating code.");
		classFile.addComment(fs, "For Statement");
		
		// YOUR CODE HERE
		String topLabel = "L"+gen.getLabel();
		String endLabel = "L"+gen.getLabel();
		String contLabel = "L"+gen.getLabel();
		
		String metaBreakLabel = gen.getBreakLabel();
		String metaContinueLabel = gen.getContinueLabel();
		gen.setBreakLabel(endLabel);
		gen.setContinueLabel(contLabel);
		
		if (fs.init() != null) {
			fs.init().visit(this);
		}
		
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
		
		if (fs.expr() != null) {
			fs.expr().visit(this);
			classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, endLabel));
		}
		
		boolean metaInsideLoop = insideLoop;
		insideLoop = true;
		if (fs.stats() != null) {
			fs.stats().visit(this);
		}
		insideLoop = metaInsideLoop;
		
		if (fs.incr() != null) {
			fs.incr().visit(this);
		}
		
		classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, topLabel));
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, endLabel));
		
		gen.setBreakLabel(metaBreakLabel);
		gen.setContinueLabel(metaContinueLabel);
		// - END -
		
		classFile.addComment(fs, "End ForStat");	
		return null;
	}

	// WHILE STATEMENT (COMPLETED)
	public Object visitWhileStat(WhileStat ws) {
		println(ws.line + ": While Stat:\tGenerating Code.");

		classFile.addComment(ws, "While Statement");

		// YOUR CODE HERE
		String topLabel = "L"+gen.getLabel();
		String endLabel = "L"+gen.getLabel();
		
		String metaContinueLabel = gen.getContinueLabel();
		String metaBreakLabel = gen.getBreakLabel();
		
		gen.setContinueLabel(topLabel);
		gen.setBreakLabel(endLabel);
		
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
		
		if (ws.expr() != null) {
			ws.expr().visit(this);
			classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, endLabel));
		}
		
		boolean metaInsideLoop = insideLoop;
		insideLoop = true;
		if (ws.stat() != null) {
			ws.stat().visit(this);
		}
		insideLoop = metaInsideLoop;
		
		classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, topLabel));
		classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, endLabel));
		
		gen.setContinueLabel(metaContinueLabel);
		gen.setBreakLabel(metaBreakLabel);
		// - END -

		classFile.addComment(ws, "End WhileStat");	
		return null;
	}

	// IF STATEMENT (COMPLETED)
	public Object visitIfStat(IfStat is) {
		println(is.line + ": IfStat:\tGenerating code.");
		classFile.addComment(is, "If Statement");

		// YOUR CODE HERE
		String elseLabel = "L"+gen.getLabel();
		String endLabel = "L"+gen.getLabel();
		
		is.expr().visit(this);
		
		classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, elseLabel));
		
		if (is.thenpart() != null) {
			is.thenpart().visit(this);
			if(is.elsepart() != null) {
				classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, endLabel));
			}
		}
		
		if(is.elsepart() != null) {
			classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, elseLabel));
			is.elsepart().visit(this);
			classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, endLabel));
		}
		else {
			classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, elseLabel));
		}
		// - END -
		
		classFile.addComment(is,  "End IfStat");
		return null;
	}
	
	// RETURN STATEMENT (COMPLETED)
	public Object visitReturnStat(ReturnStat rs) {
		println(rs.line + ": ReturnStat:\tGenerating code.");
		classFile.addComment(rs, "Return Statement");

		// YOUR CODE HERE
		if (rs.expr() == null) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
		}else{
			rs.expr().visit(this);
			
			if (rs.getType().isClassType() || rs.getType().isStringType() || rs.getType().isNullType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_areturn));
			}else if (rs.getType().isFloatType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_freturn));
			}else if (rs.getType().isLongType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_lreturn));
			}else if (rs.getType().isDoubleType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dreturn));
			}else if (rs.getType().isIntegerType() || rs.getType().isBooleanType() || rs.getType().isByteType() || rs.getType().isCharType() || rs.getType().isShortType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_ireturn));
			}
		}
		// - END -

		classFile.addComment(rs, "End ReturnStat");
		return null;
	}
	
	// THIS (COMPLETED - LOAD1?)
	public Object visitThis(This th) {
		println(th.line + ": This:\tGenerating code (access).");       
		classFile.addComment(th, "This");

		// YOUR CODE HERE
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
		// - END -

		classFile.addComment(th, "End This");
		return null;
	}

    // BREAK STATEMENT (COMPLETED)
    public Object visitBreakStat(BreakStat br) {
		println(br.line + ": BreakStat:\tGenerating code.");
		classFile.addComment(br, "Break Statement");

		// YOUR CODE HERE
		if (!insideLoop && !insideSwitch) {
			Error.error("Break statement must be inside a loop or a switch.");
		}else{
			classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, gen.getBreakLabel()));
		}
		// - END -

		classFile.addComment(br, "End BreakStat");
		return null;
    }
    
	// LOCAL VARIABLE DECLARATION (COMPLETED)
	public Object visitLocalDecl(LocalDecl ld) {
		if (ld.var().init() != null) {
			println(ld.line + ": LocalDecl:\tGenerating code for the initializer for variable '" + 
					ld.var().name().getname() + "'.");
			classFile.addComment(ld, "Local Variable Declaration");

			// YOUR CODE HERE
			ld.var().init().visit(this);
			gen.dataConvert(ld.var().init().type, ld.type());
			classFile.addInstruction(new Instruction(gen.getStoreInstruction(ld.type(), ld.address, false))); //Use SimpleInstruction here also? Remember for testing
			// - END -

			classFile.addComment(ld, "End LocalDecl");
		}
		else
			println(ld.line + ": LocalDecl:\tVisiting local variable declaration for variable '" + ld.var().name().getname() + "'.");

		return null;
	}
    
	// INVOCATION (COMPLETED)
	public Object visitInvocation(Invocation in) {
	    println(in.line + ": Invocation:\tGenerating code for invoking method '" + in.methodName().getname() + "' in class '" + in.targetType.typeName() + "'.");
		classFile.addComment(in, "Invocation");
		
		// YOUR CODE HERE
		int opCode = 0;
		String className = "";
		String methodName = "";
		String signature = "";

		if (in.target() != null){
			println(in.targetType.typeName() + "’.");
		}else{
			println(currentClass.name() + "’.");
		}

		if (!in.targetMethod.getModifiers().isStatic()) {
			if (in.target() != null) {
				in.target().visit(this);
			}else{
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
			}
		}

		if (in.params() != null) {
			for (int i=0; i<in.params().nchildren; i++) {
				in.params().children[i].visit(this);
				gen.dataConvert(((Expression)in.params().children[i]).type,((ParamDecl)in.targetMethod.params().children[i]).type());
			}
		}

		if (!(in.targetMethod.getModifiers().isStatic())) {

			if (!(in.targetMethod.isInterfaceMember())) {
				if (in.target() instanceof Super) {
					opCode = RuntimeConstants.opc_invokenonvirtual;
				} else {
					opCode = RuntimeConstants.opc_invokevirtual;
				}
			} else {
				opCode = RuntimeConstants.opc_invokeinterface;
			}

		} else {

			opCode = RuntimeConstants.opc_invokestatic;			

		}

		if (in.target() != null) {
			className = in.targetType.typeName();
		} else {
			className = currentClass.name();
		}

		methodName = in.methodName().getname();

		signature = "(";
		
		if (in.params() != null) {
			in.targetMethod.params().visit(this);
		}
		
		for (int i=0; i<in.targetMethod.params().nchildren; i++){
			signature += ((ParamDecl)in.targetMethod.params().children[i]).type().signature();
		}
		
		signature += ")";

		if (in.targetMethod.returnType() != null) {
			signature += in.targetMethod.returnType().signature();
		} else {
			signature += "V";
		}

		if (opCode != RuntimeConstants.opc_invokeinterface){
			classFile.addInstruction(new MethodInvocationInstruction(opCode, className, methodName, signature));
		}else{
			classFile.addInstruction(new InterfaceInvocationInstruction(opCode,className, methodName, signature, in.params().nchildren+1));
		}
		// - END -

		classFile.addComment(in, "End Invocation");
		return null;
	}

	// NAME EXPRESSION (COMPLETED)
	public Object visitNameExpr(NameExpr ne) {
		classFile.addComment(ne, "Name Expression --");

		// ADDED 22 June 2012 
		if (ne.myDecl instanceof ClassDecl) {
			println(ne.line + ": NameExpr:\tWas a class name - skip it :" + ne.name().getname());
			classFile.addComment(ne, "End NameExpr");
			return null;
		}

		// YOUR CODE HERE
		if (!(ne.myDecl instanceof FieldDecl)) {

			println(ne.line + ": NameExpr:\tGenerating code for a local var/param (access) for ’" + ne.name().getname() + "’.");

			int address = ((VarDecl)ne.myDecl).address();
			if (address >= 4){
				classFile.addInstruction(new SimpleInstruction(gen.getLoadInstruction(ne.type, address, false), address));
			}else{
				classFile.addInstruction(new Instruction(gen.getLoadInstruction(ne.type, address, false)));
			}

		} else {

			FieldDecl fd = (FieldDecl) ne.myDecl;
			println(ne.line + ": NameExpr:\tGenerating code for a field reference (getfield) for field ’." + ne.name().getname() + "’ in class ’" + currentClass.name() + "’.");

			if (!fd.modifiers.isStatic()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield, currentClass.name(), ne.name().getname(), ne.type.signature()));
			} else {
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic, currentClass.name(), ne.name().getname(), ne.type.signature()));
			}

		}
		// - END -

		classFile.addComment(ne, "End NameExpr");
		return null;
	}

	// NEW (COMPLETED)
	public Object visitNew(New ne) {
		println(ne.line + ": New:\tGenerating code");
		classFile.addComment(ne, "New");

		// YOUR CODE HERE
		classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_new, ne.type().typeName()));
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
		
		if (ne.args() != null) {
			for (int i=0; i<ne.args().nchildren; i++) {
				ne.args().children[i].visit(this);
				gen.dataConvert(((Expression)ne.args().children[i]).type,((ParamDecl)ne.getConstructorDecl().params().children[i]).type());
			}
		}

		String signature;
		if (ne.getConstructorDecl() != null) {
			signature = "(";
			if (ne.getConstructorDecl().params() != null){
				ne.getConstructorDecl().params().visit(this);
			}

			for (int i=0; i<ne.getConstructorDecl().params().nchildren; i++){
				signature += ((ParamDecl)ne.getConstructorDecl().params().children[i]).type().signature();
				signature += ")V";
			}

			classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokenonvirtual, ne.type().typeName(), "<init>", signature));
		} else {
			signature = "()V";
		}
		// - END -

		classFile.addComment(ne, "End New");
		return null;
	}

	// STATIC INITIALIZER (COMPLETED)
	public Object visitStaticInitDecl(StaticInitDecl si) {
		println(si.line + ": StaticInit:\tGenerating code for a Static initializer.");	

		classFile.startMethod(si);
		classFile.addComment(si, "Static Initializer");

		// YOUR CODE HERE
		currentClass.visit(new GenerateFieldInits(gen, currentClass, true));
		si.initializer().visit(this);
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
		si.setCode(classFile.getCurrentMethodCode());
		classFile.endMethod();
		// - END -

		si.setCode(classFile.getCurrentMethodCode());
		classFile.endMethod();
		return null;
	}

	// SUPER (COMPLETED - LOAD0?)
	public Object visitSuper(Super su) {
		println(su.line + ": Super:\tGenerating code (access).");	
		classFile.addComment(su, "Super");

		// YOUR CODE HERE
		// "Should be the same as visitThis except it loads address 0"
		// Wait 'this' loads address 0, maybe super loads a different address? Might be in the book 
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
		// - END -

		classFile.addComment(su, "End Super");
		return null;
	}

	// UNARY POST EXPRESSION (COMPLETED)
	public Object visitUnaryPostExpr(UnaryPostExpr up) {
		println(up.line + ": UnaryPostExpr:\tGenerating code.");
		classFile.addComment(up, "Unary Post Expression");

		// YOUR CODE HERE
		up.expr().visit(this);
		if (up.expr().type.isLongType()) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
		} else {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
		}

		if (up.expr().type.isIntegerType()) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1)); classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_iadd : RuntimeConstants.opc_isub));
		} else if (up.expr().type.isLongType()) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1)); classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_ladd : RuntimeConstants.opc_lsub));
		} else if (up.expr().type.isFloatType()) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1)); classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_fadd : RuntimeConstants.opc_fsub));
		} else if (up.expr().type.isDoubleType()) {
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1)); classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_dadd : RuntimeConstants.opc_dsub));
		}

		// Now determine if its a FieldRef or a NameExpr
		if (up.expr() instanceof FieldRef) {

			FieldRef fr = (FieldRef)up.expr();
			FieldDecl fd = fr.myDecl;
		
		} else if (up.expr() instanceof NameExpr) {
		
			// could still be a field! check what the myDecl is!
			NameExpr ne = (NameExpr)up.expr();
		
			if (ne.myDecl instanceof FieldDecl) {
		
				FieldDecl fd = (FieldDecl)ne.myDecl;
		
			} else {
		
				int address = ((VarDecl)ne.myDecl).address();
				println(up.line + ": UnaryPostExpr:\tGenerating code (local/param store) for var ’" + ((VarDecl)ne.myDecl).name() + "’.");
		
				if (address < 4){
					classFile.addInstruction(new Instruction(gen.getStoreInstruction(((VarDecl)ne.myDecl).type(), address, false)));
				}else{
					classFile.addInstruction(new SimpleInstruction(gen.getStoreInstruction(((VarDecl)ne.myDecl).type(),address, false), address));
				}
			}
		}
		// - END -

		classFile.addComment(up, "End UnaryPostExpr");
		return null;
	}

	// UNARY PRE EXPRESSION (COMPLETED - NOT REFACTORED)
	public Object visitUnaryPreExpr(UnaryPreExpr up) {
		println(up.line + ": UnaryPreExpr:\tGenerating code for " + up.op().operator() + " : " + up.expr().type.typeName() + " -> " + up.expr().type.typeName() + ".");
		classFile.addComment(up,"Unary Pre Expression");

		// YOUR CODE HERE
		up.expr().visit(this);
		if (up.op().operator().equals("−")){

			if (up.expr().type.isIntegerType()){
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_ineg));
			} else if (up.expr().type.isLongType()){
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_lneg));
			}else if (up.expr().type.isFloatType())
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_fneg));
			else if (up.expr().type.isDoubleType()){
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dneg));
			}

		}else if (up.op().operator().equals("+")){

			// do nothing.

		}else if (up.op().operator().equals("~")) {
		
			if (up.expr().type.isIntegerType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_m1));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_ixor));
			} else {
				int operand = -1;
				classFile.addInstruction(new SimpleInstruction(RuntimeConstants.opc_ldc2_w, operand));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_lxor));
			}

		} else if (up.op().operator().equals("!")) {

			classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_ixor));

		} else {
				
			if (up.expr().type.isIntegerType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1)); classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_iadd : RuntimeConstants.opc_isub));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
			} else if (up.expr().type.isLongType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
				classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_ladd : RuntimeConstants.opc_lsub));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
			} else if (up.expr().type.isFloatType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1));
				classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_fadd : RuntimeConstants.opc_fsub));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
			} else if (up.expr().type.isDoubleType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1));
				classFile.addInstruction(new Instruction(up.op().operator().equals("++") ? RuntimeConstants.opc_dadd : RuntimeConstants.opc_dsub));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
			}

			if (up.expr() instanceof FieldRef) {
				FieldRef fr = (FieldRef)up.expr();
				FieldDecl fd = fr.myDecl;
				println(up.line + ": UnaryPreExpr:\tGenerating code (FieldRef putfield) for field ’" + fr.fieldName().getname() + "’ in class ’" + fr.targetType.typeName() + "’.");
			} else if (up.expr() instanceof NameExpr) {
				NameExpr ne = (NameExpr)up.expr();
				if (ne.myDecl instanceof FieldDecl) {
					FieldDecl fd = (FieldDecl)ne.myDecl;
					println(up.line + ": UnaryPreExpr:\tGenerating code (FieldRef putfield) for field ’" + fd.var().name().getname() + "’ in class ’" + currentClass.name() + "’.");
				} else {
					println(up.line + ": UnaryPreExpr:\tGenerating code (local/param store) for var’" + ((VarDecl)ne.myDecl).name() + "’.");
					int address = ((VarDecl)ne.myDecl).address();
					if (address < 4){
						classFile.addInstruction(new Instruction(gen.getStoreInstruction(((VarDecl)ne.myDecl).type(), address, false)));
					}else{
						classFile.addInstruction(new SimpleInstruction(gen.getStoreInstruction(((VarDecl)ne.myDecl).type(),address, false), address));
					}
				}
			}

		}
		// - END -

		classFile.addComment(up, "End UnaryPreExpr");
		return null;
	}

	// BINARY EXPRESSION (COMPLETED - NOT REFACTORED)
    public Object visitBinaryExpr(BinaryExpr be) {
		println(be.line + ": BinaryExpr:\tGenerating code for " + be.op().operator() + " :  " + be.left().type.typeName() + " -> " + be.right().type.typeName() + " -> " + be.type.typeName() + ".");
		classFile.addComment(be, "Binary Expression");
			
		// YOUR CODE HERE
		String op = be.op().operator();
		if (op.equals("+") || op.equals("−") || op.equals("*") || op.equals("/") || op.equals("%") || op.equals("&") || op.equals("|") || op.equals("^")) {

			PrimitiveType ct = PrimitiveType.ceilingType((PrimitiveType) be.left().type, (PrimitiveType) be.right().type);
			be.left().visit(this);
			gen.dataConvert(be.left().type, ct);
			be.right().visit(this);
			gen.dataConvert(be.right().type, ct);

			if (ct.isIntegerType() || ct.isShortType() || ct.isCharType() || ct.isByteType() || ct.isBooleanType()) {
				
				if (op.equals("+"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_iadd));
				else if (op.equals("−"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_isub));
				else if (op.equals("*"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_imul));
				else if (op.equals("/"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_idiv));
				else if (op.equals("%"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_irem));
				else if (op.equals("^"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ixor));
				else if (op.equals("|"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ior));
				else if (op.equals("&"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_iand));

			} else if (ct.isLongType()) {

				if (op.equals("+"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ladd));
				else if (op.equals("−"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lsub));
				else if (op.equals("*"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lmul));
				else if (op.equals("/"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ldiv));
				else if (op.equals("%"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lrem));
				else if (op.equals("^"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lxor));
				else if (op.equals("|"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lor));
				else if (op.equals("&"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_land));

			} else if (ct.isFloatType()) {

				if (op.equals("+"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_fadd));
				else if (op.equals("−"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_fsub));
				else if (op.equals("*"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_fmul));
				else if (op.equals("/"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_fdiv));
				else if (op.equals("%"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_frem));

			} else if (ct.isDoubleType()) {

				if (op.equals("+"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_dadd));
				else if (op.equals("−"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_dsub));
				else if (op.equals("*"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_dmul));
				else if (op.equals("/"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ddiv));
				else if (op.equals("%"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_drem));

			}
		} else if (op.equals("&&") || op.equals("||")) {
			
			String donelabel;
			be.left().visit(this);
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
			donelabel = gen.getLabel();
			classFile.addInstruction(new JumpInstruction((op.equals("&&") ? RuntimeConstants.opc_ifeq : RuntimeConstants.opc_ifne), "L" + donelabel));
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));
			be.right().visit(this);
			classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L" + donelabel));

		} else if (op.equals("==") || op.equals("!=") || op.equals("<=") || op.equals(">=") || op.equals(">") || op.equals("<")) {

			String truelabel, donelabel;
			truelabel = gen.getLabel();
			donelabel = gen.getLabel();
			if (be.left().type.isClassType()) {
				be.left().visit(this);
				be.right().visit(this);
				if (op.equals("=="))
					classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_acmpeq, "L" + truelabel));
				else
					classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_acmpne, "L" + truelabel));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_0));
				classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, "L" + donelabel));
				classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L" + truelabel));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
				classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L" + donelabel));
			} else if (be.left().type.isStringType()) {
				be.left().visit(this);
				be.right().visit(this);
				classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual, "java/lang/String", "equals", "(Ljava/lang/Object;)Z"));
			} else {
				PrimitiveType ct = PrimitiveType.ceilingType((PrimitiveType) be.left().type, (PrimitiveType) be.right().type);
				be.left().visit(this);
				gen.dataConvert(be.left().type, ct);
				be.right().visit(this);
				gen.dataConvert(be.right().type, ct);
				String jumpTarget = "L" + truelabel;
				if (ct.isIntegerType()) {
					if (op.equals("==")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmpeq, jumpTarget));
					} else if (op.equals("!=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmpne, jumpTarget));
					} else if (op.equals("<=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmple, jumpTarget));
					} else if (op.equals(">=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmpge, jumpTarget));
					} else if (op.equals("<")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmplt, jumpTarget));
					} else if (op.equals(">")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_if_icmpgt, jumpTarget));
					}
				} else if (ct.isLongType() || ct.isFloatType() || ct.isDoubleType()) {
					if (ct.isLongType())
						classFile.addInstruction(new Instruction(RuntimeConstants.opc_lcmp));
					else if (ct.isFloatType())
						classFile.addInstruction(new Instruction(RuntimeConstants.opc_fcmpg));
					else
						classFile.addInstruction(new Instruction(RuntimeConstants.opc_dcmpg));
					jumpTarget = "L" + truelabel;
					if (op.equals("==")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, jumpTarget));
					} else if (op.equals("!=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifne, jumpTarget));
					} else if (op.equals("<=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifle, jumpTarget));
					} else if (op.equals(">=")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifge, jumpTarget));
					} else if (op.equals("<")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_iflt, jumpTarget));
					} else if (op.equals(">")) {
						classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifgt, jumpTarget));
					}
				} else
					Error.error("no case found for type: " + ct);
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_0));
					classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_goto, "L" + donelabel));
					classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L" + truelabel));
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
					classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, "L" + donelabel));
			}

		} else if (op.equals("<<") || op.equals(">>") || op.equals(">>>")) {

			be.left().visit(this);
			be.right().visit(this);

			if (be.right().type.isIntegerType()) {
				if (op.equals("<<"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ishl));
				else if (op.equals(">>"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_ishr));
				else if (op.equals(">>>"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_iushr));
			} else {
				// Only Long type left, type checking made sure of that!
				if (op.equals("<<"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lshl));
				else if (op.equals(">>"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lshr));
				else if (op.equals(">>>"))
					classFile.addInstruction(new Instruction(RuntimeConstants.opc_lushr));
			}

		} else if (op.equals("instanceof")) {

			be.left().visit(this);
			classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_instanceof, ((NameExpr) be.right()).name().getname()));

		}
		// - END -

		classFile.addComment(be, "End BinaryExpr");
		return null;
    }

	// CONSTRUCTOR INVOCATION (EXPLICIT) (COMPLETED - NOT REFACTORED)
	public Object visitCInvocation(CInvocation ci) {
		println(ci.line + ": CInvocation:\tGenerating code for Explicit Constructor Invocation.");     
		classFile.addComment(ci, "Explicit Constructor Invocation");

		// YOUR CODE HERE
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));

		if (ci.args() != null)
			ci.args().visit(this);

		String className;
		
		if (ci.superConstructorCall())
			className = currentClass.superClass().typeName();
		else
			className = currentClass.name();
		
		String signature = "(";
		if (ci.args() != null) {
			ci.constructor.params().visit(this);
		}
		
		signature += ci.constructor.paramSignature();
		
		signature += ")V";
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokespecial, className,"<init>",signature));
		// - END -

		classFile.addComment(ci, "End CInvocation");
		return null;
	}

    // CAST EXPRESSION (YET TO COMPLETE -- FINISH THIS IN TESTING)
    public Object visitCastExpr(CastExpr ce) {
		println(ce.line + ": CastExpr:\tGenerating code for a Cast Expression.");
		classFile.addComment(ce, "Cast Expression");
		String instString;
		
		// YOUR CODE HERE
		ce.expr().visit(this);
		// - END -

		classFile.addComment(ce, "End CastExpr");
		return null;
    }
    
	// CLASS DECLARATION ("COMPLETE" -- UNSURE HOW TO REFACTOR THIS)
	public Object visitClassDecl(ClassDecl cd) {
		println(cd.line + ": ClassDecl:\tGenerating code for class '" + cd.name() + "'.");

		// We need to set this here so we can retrieve it when we generate
		// field initializers for an existing constructor.
		currentClass = cd;

		// YOUR CODE HERE
		boolean needClinit = false;
		StaticInitDecl si = null;
		
		for (int i = 0; i < cd.body().nchildren; i++) {
			if (cd.body().children[i] instanceof FieldDecl) {
				cd.body().children[i].visit(this);
			}
		}
		
		if (cd.methodTable.get("<clinit>") == null) {
			FieldDecl fd;
			
			for (Enumeration en = cd.fieldTable.entries.elements(); en.hasMoreElements(); ) {
				fd = (FieldDecl)en.nextElement();
				if (fd.modifiers.isStatic() && fd.var().init() != null) {
					needClinit = true;
				}
			}
			
			if (needClinit) {
				si = new StaticInitDecl(new Block(new Sequence()));
				
				classFile.startMethod(si);
				cd.visit(new GenerateFieldInits(gen, currentClass, true));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
				si.setCode(classFile.getCurrentMethodCode());
				classFile.endMethod();
			}
		}
		
		for (int i = 0; i < cd.body().nchildren; i++) {
			if (!(cd.body().children[i] instanceof FieldDecl)) {
				cd.body().children[i].visit(this);
			}
		}
		
		if (needClinit) {
			cd.body().append(si);
		}
		// - END -

		return null;
	}

	// CONSTRUCTOR DECLARATION ("COMPLETE")
	public Object visitConstructorDecl(ConstructorDecl cd) {
		println(cd.line + ": ConstructorDecl: Generating Code for constructor for class " + cd.name().getname());

		classFile.startMethod(cd);
		classFile.addComment(cd, "Constructor Declaration");

		// 12/05/13 = removed if (just in case this ever breaks ;-) )
		cd.cinvocation().visit(this);

		// YOUR CODE HERE
		gen.setAddress(1);
		
		if (cd.cinvocation() == null) {
			if (currentClass.superClass() == null) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
				classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokenonvirtual, "java/lang/Object", "<init>", "0V"));
			}
			else {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
				classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokenonvirtual, currentClass.superClass().name().getname(), "<init>", "0V"));
			}
		}
		
		classFile.addComment(cd, "Field Init Generation Start");
		currentClass.visit(new GenerateFieldInits(gen, currentClass, false));
		classFile.addComment(cd, "Field Init Generation End");
		
		if (cd.body() != null) {
			cd.body().visit(this);
		}
		// - END -

		classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));

		// We are done generating code for this method, so transfer it to the classDecl.
		cd.setCode(classFile.getCurrentMethodCode());
		classFile.endMethod();

		return null;
	}

	// E+ and E* functions below
	
	// TERNARY EXPRESSION (YET TO COMPLETE - E+)
	public Object visitTernary(Ternary te) {
		println(te.line + ": Ternary:\tGenerating code.");
		classFile.addComment(te, "Ternary Statement");

		boolean OldStringBuilderCreated = StringBuilderCreated;
		StringBuilderCreated = false;

		// YOUR CODE HERE
		
		// - END -

		classFile.addComment(te, "Ternary");
		StringBuilderCreated = OldStringBuilderCreated;
		return null;
	}

	// ARRAY VISITORS START HERE
	/** ArrayAccessExpr (YET TO COMPLETE - E++)*/
	public Object visitArrayAccessExpr(ArrayAccessExpr ae) {
		println(ae.line + ": Visiting ArrayAccessExpr");
		classFile.addComment(ae, "ArrayAccessExpr");

		// YOUR CODE HERE

		// - END -

		classFile.addComment(ae,"End ArrayAccessExpr");
		return null;
	}

	/** ArrayLiteral (YET TO COMPLETE - E++)*/
	public Object visitArrayLiteral(ArrayLiteral al) {
		println(al.line + ": Visiting an ArrayLiteral ");
		
		// YOUR CODE HERE
		
		// - END -

		return null;
	}

	/** NewArray (YET TO COMPLETE - E++)*/
	public Object visitNewArray(NewArray ne) {
		println(ne.line + ": NewArray:\t Creating new array of type " + ne.type.typeName());
		
		// YOUR CODE HERE
		
		// - END -

		return null;
	}
	// END OF ARRAY VISITORS

}


package TypeChecker;

import AST.*;
import Parser.sym;
import Utilities.Error;
import Utilities.SymbolTable;
import Utilities.Visitor;
import java.util.*;
import java.math.*;

/**
 * TypeChecker implements the type checker for the Espresso langauge.
 */
public class TypeChecker extends Visitor {
    
    /**
     * <p>Returns the 'best-fitting' method or constructor from a list 
     * of potential candidates given a set of actual parameters.</p>
     * <p>See Section 7.2.9: Finding the Right Method to Call in Practical Compiler Construction.</p>
     * <p>Remember to visit all the arguments BEFORE calling findMethod. findMethod uses the <i>.type</i> field of the parameters.</p>
     * @param candidateMethods Sequence of methods or constructors. Use {@link AST.ClassDecl#allMethods} or {@link AST.ClassDecl#constructors} from the appropriate {@link AST.ClassDecl}.
     * @param name The name of the method or constructor you are looking for.
     * @param actualParams The sequence of actual parameters being passed to the method invocation or constructor invocation. 
     * @param lookingForMethods <i>true</i> if you pass a sequence of methods ({@link AST.ClassDecl#allMethods}), <i>false</i> if you pass a sequence of constructors ({@link AST.ClassDecl#constructors}).
     * @return The {@link AST.MethodDecl}/{@link AST.ConstructorDecl} found; null if nothing was found.
     */
    public static ClassBodyDecl findMethod(Sequence candidateMethods, String name, Sequence actualParams, 
					   boolean lookingForMethods) {
	
	if (lookingForMethods) {
	    println("+------------- findMethod (Method) ------------");
	    println("| Looking for method: " + name);
	} else {
	    println("+---------- findMethod (Constructor) ----------");
	    println("| Looking for constructor: " + name);
	}
	println("| With parameters:");
	for (int i=0; i<actualParams.nchildren; i++){
	    println("|   " + i + ". " + ((actualParams.children[i] instanceof ParamDecl)?(((ParamDecl)actualParams.children[i]).type()):((Expression)actualParams.children[i]).type));
	}
	// The number of actual parameters in the invocation.
	int count = 0;
	
	// Make an array big enough to hold all the methods if needed
	ClassBodyDecl cds[] = new ClassBodyDecl[candidateMethods.nchildren];
	
	// Initialize the array to point to null
	for(int i=0;i<candidateMethods.nchildren;i++) 
	    cds[i] = null;
	
	Sequence args = actualParams;
	Sequence params;
	
	// Insert all the methods from the symbol table that:
	// 1.) has the right number of parameters
	// 2.) each formal parameter can be assigned its corresponding
	//     actual parameter.
	if (lookingForMethods)
	    println("| Finding methods with the right number of parameters and types");
	else
	    println("| Finding constructors with the right number of parameters and types");
	for (int cnt=0; cnt<candidateMethods.nchildren; cnt++) {
	    ClassBodyDecl cbd = (ClassBodyDecl)candidateMethods.children[cnt];
	    
	    // if the method doesn't have the right name, move on!
	    if (!(cbd.getname().equals(name)))
		continue;
	    
	    // Fill params with the formal parameters.
	    if (cbd instanceof ConstructorDecl) 
		params = ((ConstructorDecl)cbd).params();
	    else if (cbd instanceof MethodDecl)
		params = ((MethodDecl)cbd).params();
	    else
		// we have a static initializer, don't do anything - just skip it.
		continue;
	    
	    print("|   " + name + "(");
	    if (cbd instanceof ConstructorDecl) 
		print(Type.parseSignature(((ConstructorDecl)cbd).paramSignature()));
	    else 
		print(Type.parseSignature(((MethodDecl)cbd).paramSignature()));
	    print(" )  ");
	    
	    if (args.nchildren == params.nchildren) {
		// The have the same number of parameters
		// now check that the formal parameters are
		// assignmentcompatible with respect to the 
		// types of the actual parameters.
		// OBS this assumes the type field of the actual
		// parameters has been set (in Expression.java),
		// so make sure to call visit on the parameters first.
		boolean candidate = true;
		
		for (int i=0;i<args.nchildren; i++) {
		    candidate = candidate &&
			Type.assignmentCompatible(((ParamDecl)params.children[i]).type(),
						  (args.children[i] instanceof Expression) ?
						  ((Expression)args.children[i]).type :
						  ((ParamDecl)args.children[i]).type());
		    
		    if (!candidate) {
			println(" discarded");
			break;
		    }
		}
		if (candidate) {
		    println(" kept");
		    cds[count++] = cbd;
		}
	    }
	    else {
		println(" discarded");
	    }
	    
	}
	// now count == the number of candidates, and cds is the array with them.
	// if there is only one just return it!
	println("| " + count + " candidate(s) were found:");
	for ( int i=0;i<count;i++) {
	    ClassBodyDecl cbd = cds[i];
	    print("|   " + name + "(");
	    if (cbd instanceof ConstructorDecl) 
		print(Type.parseSignature(((ConstructorDecl)cbd).paramSignature()));
	    else 
		print(Type.parseSignature(((MethodDecl)cbd).paramSignature()));
	    println(" )");
	}
	
	if (count == 0) {
	    println("| No candidates were found.");
	    println("+------------- End of findMethod --------------");
	    return null;
	}
	
	if (count == 1) {
	    println("| Only one candidate - thats the one we will call then ;-)");
	    println("+------------- End of findMethod --------------");
	    return cds[0];
	}
	println("| Oh no, more than one candidate, now we must eliminate some >:-}");
	// there were more than one candidate.
	ClassBodyDecl x,y;
	int noCandidates = count;
	
	for (int i=0; i<count; i++) {
	    // take out a candidate
	    x = cds[i];
	    
	    if (x == null)
		continue;		    
	    cds[i] = null; // this way we won't find x in the next loop;
	    
	    // compare to all other candidates y. If any of these
	    // are less specialised, i.e. all types of x are 
	    // assignment compatible with those of y, y can be removed.
	    for (int j=0; j<count; j++) {
		y = cds[j];
		if (y == null) 
		    continue;
		
		boolean candidate = true;
		
		// Grab the parameters out of x and y
		Sequence xParams, yParams;
		if (x instanceof ConstructorDecl) {
		    xParams = ((ConstructorDecl)x).params();
		    yParams = ((ConstructorDecl)y).params();
		} else {
		    xParams = ((MethodDecl)x).params();
		    yParams = ((MethodDecl)y).params();
		}
		
		// now check is y[k] <: x[k] for all k. If it does remove y.
		// i.e. check if y[k] is a superclass of x[k] for all k.
		for (int k=0; k<xParams.nchildren; k++) {
		    candidate = candidate &&
			Type.assignmentCompatible(((ParamDecl)yParams.children[k]).type(),
						  ((ParamDecl)xParams.children[k]).type());
		    
		    if (!candidate)
			break;
		}
		if (candidate) {
		    // x is more specialized than y, so throw y away.
		    print("|   " + name + "(");
		    if (y instanceof ConstructorDecl) 
			print(Type.parseSignature(((ConstructorDecl)y).paramSignature()));
		    else 
			print(Type.parseSignature(((MethodDecl)y).paramSignature()));
		    print(" ) is less specialized than " + name + "(");
		    if (x instanceof ConstructorDecl) 
			print(Type.parseSignature(((ConstructorDecl)x).paramSignature()));
		    else 
			print(Type.parseSignature(((MethodDecl)x).paramSignature()));
		    println(" ) and is thus thrown away!");
		    
		    cds[j] = null;
		    noCandidates--;
		}
	    }
	    // now put x back in to cds
	    cds[i] = x;
	}
	if (noCandidates != 1) {
	    // illegal function call
	    println("| There is more than one candidate left!");
	    println("+------------- End of findMethod --------------");
	    return null;
	}
	
	// just find it and return it.
	println("| We were left with exactly one candidate to call!");
	println("+------------- End of findMethod --------------");
	for (int i=0; i<count; i++)
	    if (cds[i] != null)
		return cds[i];
	
	return null;
    }

    /**
     * Given a list of candiate methods and a name of the method this method prints them all out.
     *
     * @param cd The {@link AST.ClassDecl} for which the methods or constructors are being listed.
     * @param candidateMethods A {@link AST.Sequence} of either {@link AST.MethodDecl}s ({@link AST.ClassDecl#allMethods}) or {@link AST.ConstructorDecl}s ({@link AST.ClassDecl#constructors}). 
     * @param name The name of the method or the constructor for which the candidate list should be produced.
     */
    public void listCandidates(ClassDecl cd, Sequence candidateMethods, String name) {

	for (int cnt=0; cnt<candidateMethods.nchildren; cnt++) {
	    ClassBodyDecl cbd = (ClassBodyDecl)(candidateMethods.children[cnt]);

	    if (cbd.getname().equals(name)) {
		if (cbd instanceof MethodDecl)
		    System.out.println("  " + name + "(" + Type.parseSignature(((MethodDecl)cbd).paramSignature()) + " )");
		else
		    System.out.println("  " + cd.name() + "(" + Type.parseSignature(((ConstructorDecl)cbd).paramSignature()) + " )");
	    }
	}
    }
    /**
     * The global class tabel. This should be set in the constructor.
     */
    private SymbolTable   classTable;
    /**
     * The class of which children are currently being visited. This should be updated when visiting a {@link AST.ClassDecl}.
     */
    private ClassDecl     currentClass;     
    /**
     * The particular {@link AST.ClassBodyDecl} (except {@link AST.FieldDecl}) of which children are currently being visited.
     */
    private ClassBodyDecl currentContext;  
    /**
     * The current {@link AST.FieldDecl} of which children are currently being visited (if applicable).
     */
    private FieldDecl     currentFieldDecl; 
    /**
     * Indicates if children being visited are part of a {@link AST.FieldDecl} initializer. (accessible though {@link AST.FieldDecl#var()}). Used for determining forward reference of a non-initialized field. You probably don't want to bother with this one.
     */
    private boolean       inFieldInit;      

    /**
     * Constructs a new type checker.
     * @pisaram classTable The global class table.
     * @param debug determins if debug information should printed out.
     */
    public TypeChecker(SymbolTable classTable, boolean debug) { 
	this.classTable = classTable; 
	this.debug = debug;
    }

    /** v
     * @param ae An {@link AST.ArrayAccessExpr} parse tree node.
     * @return Returns the type of the array access expression.
     */
    public Object visitArrayAccessExpr(ArrayAccessExpr ae) {
	println(ae.line + ": Visiting ArrayAccessExpr");
	//<--
	Type t = (Type)ae.target().visit(this);
	if (!t.isArrayType())
	    Error.error(ae,"Array type required, but found type " + t.typeName());
	ArrayType at = (ArrayType)t;
		
	if (at.getDepth() == 1)
	    ae.type = at.baseType();
	else
	    ae.type = new ArrayType(at.baseType(), at.getDepth()-1);

	Type indexType = (Type)ae.index().visit(this);
	if (!indexType.isIntegralType()) 
	    Error.error(ae,"Array access index must be of integral type");
	println(ae.line + ": ArrayAccessExpr has type " + ae.type);
	//-->
	return ae.type;
    }

    /** 
     * @param ae An {@link AST.ArrayType} parse tree node.
     * @return Returns itself.
     */
    public Object visitArrayType(ArrayType at) {
	println(at.line + ": Visiting an ArrayType");
	println(at.line + ": ArrayType type is " + at);
	// An ArrayType is already a type, so nothing to do.
	return at;
    }

    /** NewArray */
    public Object visitNewArray(NewArray ne) {
	println(ne.line + ": Visiting a NewArray " + ne.dimsExpr().nchildren + " " + ne.dims().nchildren);
	//<--
	//  check that each dimension is of integral type
	for (int i=0; i<ne.dimsExpr().nchildren; i++) {
	    Type dimT = (Type)ne.dimsExpr().children[i].visit(this);
	    if (!dimT.isIntegralType())
		Error.error(ne.dimsExpr().children[i], "Array dimension must be of integral type");
	}
	// if there is an initializer, then make sure it is of proper and equal depth.
	ne.type = new ArrayType(ne.baseType(), ne.dims().nchildren+ne.dimsExpr().nchildren);
	if (ne.init() != null)  {
	    if (!arrayAssignmentCompatible(ne.type, ne.init()))
		Error.error(ne, "Array Initializer is not compatible with " + ne.type.typeName());
	    ne.init().type = ne.type;
	}	
	//-->
	println(ne.line + ": NewArray type is " + ne.type);
	return ne.type;
    }

    // arrayAssignmentCompatible: Determines if the expression 'e' can be assigned to the type 't'.
    // See Section 7.2.6 sub-heading 'Constructed Types'
    public boolean arrayAssignmentCompatible(Type t, Expression e) {
	if (t instanceof ArrayType && (e instanceof ArrayLiteral)) {
	    ArrayType at = (ArrayType)t;
	    e.type = at; //  we don't know that this is the type - but if we make it through it will be!
	    ArrayLiteral al = (ArrayLiteral)e;
	    
	    // t is an array type i.e. XXXXXX[ ]
	    // e is an array literal, i.e., { }
	    if (al.elements().nchildren == 0) // the array literal is { }
		return true;   // any array variable can hold an empty array
	    // Now check that XXXXXX can hold value of the elements of al
	    // we have to make a new type: either the base type if |dims| = 1
	    boolean b = true;
	    for (int i=0; i<al.elements().nchildren; i++) {
		if (at.getDepth() == 1) 
		    b = b && arrayAssignmentCompatible(at.baseType(), (Expression)al.elements().children[i]);
		else { 
		    ArrayType at1 = new ArrayType(at.baseType(), at.getDepth()-1);
		    b = b  && arrayAssignmentCompatible(at1, (Expression)al.elements().children[i]);
		}
	    }
	    return b;
	} else if (t instanceof ArrayType && !(e instanceof ArrayLiteral)) {
	    Type t1 = (Type)e.visit(this);
	    if (t1 instanceof ArrayType)
		if (!Type.assignmentCompatible(t,t1))
		    Error.error("Incompatible type in array assignment");
		else
		    return true;
	    Error.error(t, "Error: cannot assign non array to array type " + t.typeName());	    
	}
	else if (!(t instanceof ArrayType) && (e instanceof ArrayLiteral)) {
	    Error.error(t, "Error: cannot assign value " + ((ArrayLiteral)e).toString() + " to type " + t.typeName());
	}
	return Type.assignmentCompatible(t,(Type)e.visit(this));
    }
    
    public Object visitArrayLiteral(ArrayLiteral al) {
	// Espresso does not allow array literals without the 'new <type>' part.
	Error.error(al, "Array literal must be preceeded by a 'new <type>'");
	return null;
    }
    
    /** ASSIGNMENT */
    public Object visitAssignment(Assignment as) {
	println(as.line + ": Visiting an assignment");

	// get the types of the LHS (v) and the RHS(e)
	Type vType = (Type) as.left().visit(this);
	Type eType = (Type) as.right().visit(this);

	/** Note: as.left() should be of NameExpr or FieldRef class! */

	if (!vType.assignable())          
	    Error.error(as,"Left hand side of assignment not assignable.");

	// Cannot assign to a classname
	if (as.left() instanceof NameExpr && (((NameExpr)as.left()).myDecl instanceof ClassDecl))
	    Error.error(as,"Left hand side of assignment not assignable.");

	// Now switch on the operator
	switch (as.op().kind) {
	case AssignmentOp.EQ : {
	    // Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}

	    //<--
	case AssignmentOp.MULTEQ :
	case AssignmentOp.DIVEQ : 
	case AssignmentOp.MODEQ : 
	case AssignmentOp.PLUSEQ :
	case AssignmentOp.MINUSEQ : 
	    // It is always legal to do String += <anything>.
	    if (as.op().kind == AssignmentOp.PLUSEQ && vType.isStringType())
		break;

	    // Now do an assignment compatability check.
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + 
			    " to variable of type " + vType.typeName() + ".");

	    // Though this only works if both are numeric (we already dealt with Strings)
	    if (!eType.isNumericType())
		Error.error(as,"Right hand side operand of operator '" + as.op().operator() + 
			    "' must be of numeric type.");	   
	    if (!vType.isNumericType())
		Error.error(as,"Left hand side operand of operator '" + as.op().operator() + 
			    "' must be of numeric type.");
	    break;
	case AssignmentOp.LSHIFTEQ :
	case AssignmentOp.RSHIFTEQ :
	case AssignmentOp.RRSHIFTEQ :
	    // LHS must be integral and RHS must be integer.
	    if (!vType.isIntegralType())
		Error.error(as,"Left hand side operand of operator '" + as.op().operator() +
			    "' must be of integral type.");
	    if (!eType.isIntegralType())
		Error.error(as,"Right hand side operand of operator '" + as.op().operator() + 
			    "' must be of integral type.");
	    break;
	case AssignmentOp.ANDEQ : 
	case AssignmentOp.OREQ : 
	case AssignmentOp.XOREQ :
	    // Both sides must be _either_ integral or Boolean. If they are integral they must
	    // be the same type [can't do long and int, for example].
	    if (!vType.identical(eType) || 
		!((vType.isIntegralType() && eType.isIntegralType()) ||
		  (vType.isBooleanType() && eType.isBooleanType())))
		Error.error(as,"Both right and left hand side operands of operator '" + 
			    as.op().operator() + "' must be either of boolean or similar integral type.");
	    break;
	    //-->

	}
	// The overall type is always that of the LHS.
	as.type = vType;
	println(as.line + ": Assignment has type: " + as.type);

	return vType;
    }

    /** BINARY EXPRESSION */
    public Object visitBinaryExpr(BinaryExpr be) {
	println(be.line + ": Visiting a Binary Expression");

	//<--
	Type lType = (Type) be.left().visit(this);
	Type rType = (Type) be.right().visit(this);
	String op = be.op().operator();

	switch(be.op().kind) {
	    // < > <= >= : Type can be Integer only.	
	case BinOp.LT:
	case BinOp.GT:
	case BinOp.LTEQ:
	case BinOp.GTEQ: {
	    if (lType.isNumericType() && rType.isNumericType()) {
		be.type = new PrimitiveType(PrimitiveType.BooleanKind); 
	    } else
		Error.error(be,"Operator '" + op + "' requires operands of numeric type.");
	    break;
	}
	    // == != : Type can be anything but void.
	case BinOp.EQEQ:
	case BinOp.NOTEQ: {
	    if (be.left() instanceof NameExpr && ((NameExpr)be.left()).myDecl instanceof ClassDecl)
		Error.error(be,"Class name '" + ((ClassDecl)((NameExpr)be.left()).myDecl).className() + "' cannot appear as parameter to operator '" +be.op().operator() + "'.");
	    
	    if (be.right() instanceof NameExpr && ((NameExpr)be.right()).myDecl instanceof ClassDecl)
		Error.error(be,"Class name '" + ((ClassDecl)((NameExpr)be.right()).myDecl).className() + "' cannot appear as parameter to operator '" +be.op().operator() + "'.");

	    if (lType.identical(rType))
		if (lType.isVoidType())
		    Error.error(be,"Void type cannot be used here.");
		else 
		    be.type = new PrimitiveType(PrimitiveType.BooleanKind);
	    else if (lType.isNumericType() && rType.isNumericType()) 
		be.type = new PrimitiveType(PrimitiveType.BooleanKind);
	    else
		Error.error(be,"Operator '" + op + "' requires operands of the same type.");
	    break;
	}
	    // && || : Type can be Boolean only.
	case BinOp.ANDAND:
	case BinOp.OROR: {
	    if (lType.isBooleanType() && rType.isBooleanType()) 
		be.type = lType;
	    else
		Error.error(be,"Operator '" + op + "' requires operands of boolean type."); 
	    break;
	}
	    // & | ^ : Type can be Boolean or Integral
	case BinOp.AND:
	case BinOp.OR:
	case BinOp.XOR: {
	    if ((lType.isBooleanType() && rType.isBooleanType())) {
		be.type = lType;
	    } else if (lType.isIntegralType() && rType.isIntegralType()) {
		be.type = PrimitiveType.ceilingType((PrimitiveType)lType, (PrimitiveType)rType);;
		
		// promote byte, short and char to int
		if (be.type.isByteType() || be.type.isShortType() || be.type.isCharType())
		    be.type = new PrimitiveType(PrimitiveType.IntKind);
		
	    } else
		  Error.error(be,"Operator '" + op + 
			      "' requires both operands of either integral or boolean type.");
	    break;
	}
	    // + - * / % : Type must be numeric
	case BinOp.PLUS:
	case BinOp.MINUS:
	case BinOp.MULT:
	case BinOp.DIV:
	case BinOp.MOD: {
	    // 12/06/13 added + for Strings.
	    if (be.op().kind == BinOp.PLUS &&
		(lType.isStringType() || rType.isStringType())) {
		be.type = new PrimitiveType(PrimitiveType.StringKind);			
	    } else if (lType.isNumericType() && rType.isNumericType()) {
		// ceilingType promotes to at least int.
		be.type = PrimitiveType.ceilingType((PrimitiveType)lType, (PrimitiveType)rType);
	    }
	    else
		Error.error(be,"Operator '" + op + "' requires operands of numeric type.");
	    break;
	}
	    // << >> >>>: 
	case BinOp.LSHIFT:
	case BinOp.RSHIFT:
	case BinOp.RRSHIFT: {
	    if (!lType.isIntegralType())
		Error.error(be,"Operator '" + op + "' requires left operand of integral type.");
	    if (!rType.isIntegralType())
		Error.error(be,"Operator '" + op + "' requires right operand of integral type.");
	    be.type = lType;

	    // Promote byte, short and char to int.
	    if (be.type.isByteType() || be.type.isShortType() || be.type.isCharType())
		be.type = new PrimitiveType(PrimitiveType.IntKind);
	    break;
	}
	case BinOp.INSTANCEOF: {
	    // Check that the right hand side is a class name
	    NameExpr ne = (NameExpr)be.right();
	    if (classTable.get(ne.name().getname()) == null)
		Error.error(be,"'" + ne.name().getname() + "' is not a class name."); 

	    // Left hand side must be of class type
	    if (!lType.isClassType()) 
		Error.error(be,"Left hand side of instanceof needs expression of class type");

	    // But it may not be a classname !
	    if (be.left() instanceof NameExpr && ((NameExpr)be.left()).myDecl instanceof ClassDecl)
		//					classTable.get(((NameExpr)be.left()).name().getname()) != null)
		Error.error(be,"Left hand side of instanceof cannot be a class.");

	    be.type = new PrimitiveType(PrimitiveType.BooleanKind); 
	    break;
	}
	default: Error.error(be,"Unknown operator '" + op + "'.");
	}   
	//-->

	println(be.line + ": Binary Expression has type: " + be.type);
	return be.type;
    }

    /** CAST EXPRESSION */
    public Object visitCastExpr(CastExpr ce) {
	println(ce.line + ": Visiting a cast expression");

	// We have two different types of casts:
	// Numeric: any numeric type can be cast to any other numeric type.
	// Class: (A)e, where Type(e) is B. This is legal ONLY if A :> B or B :> A
	//        that is either A has to be above B in the class hierarchy or 
	//        B has to be above A.
	// Do note that if the cast type and the expression type are identical,
	// then the cast is fine: for example (String)"Hello" or (Boolean)true.
	//
	// One small caveat: (A)a is not legal is 'a' is a class name. So if the 
	// expression is a NameExpr then it cannot be the name of a class - that is,
	// its myDecl cannot be a ClassDecl.
	//<--
	Type exprType = (Type)ce.expr().visit(this);
	Type castType = ce.type();

	// Numeric to numeric is always OK.
	if (exprType.isNumericType() && castType.isNumericType()) {
	    ce.type = castType;
	    println(ce.line + ": Cast Expression has type: " + ce.type);
	    return castType;
	} 

	// Expression cannot be a class name.
	if ((ce.expr() instanceof NameExpr) && 
	    ((NameExpr)ce.expr()).myDecl instanceof ClassDecl)
	    Error.error(ce,"Cannot use class name '" + ((ClassDecl)((NameExpr)ce.expr()).myDecl).className() + "'. Object name expected in cast.");

	// Both are class types, now they have to be in the same class hierarchy.
	if (exprType.isClassType() && castType.isClassType())
	    if (Type.isSuper((ClassType)exprType, (ClassType)castType) ||
		Type.isSuper((ClassType)castType, (ClassType)exprType)) {
		ce.type = castType;
		println(ce.line + ": Cast Expression has type: " + ce.type);
		return castType;
	    }
	
	// Otherwise, they both have to be the same type.
	if (!exprType.identical(castType))
	    Error.error(ce,"Illegal type cast. Cannot cast type '" + exprType.typeName() + "' to type '" + castType.typeName() + "'.");
	ce.type = castType;
	//-->

	// The overall type of a cast expression is always the cast type.

	println(ce.line + ": Cast Expression has type: " + ce.type);
	return ce.type;
    }

    /** CLASSTYPE */
    public Object visitClassType(ClassType ct) {
	println(ct.line + ": Visiting a class type");
	// A class type is alreayd a type, so nothing to do.
	println(ct.line + ": Class Type has type: " + ct);
	return ct;
    }

    /** CONSTRUCTOR (EXPLICIT) INVOCATION */
    public Object visitCInvocation(CInvocation ci) {
	println(ci.line + ": Visiting an explicit constructor invocation");

	// An explicit constructor invocation takes one of two forms:
	// this ( ... )  -- this calls a constructor in the same class (currentClass)
	// super ( ... ) -- this calls a constructor in the super class (of currentClass)

	//<--
	ConstructorDecl constructor; 
	ClassDecl targetClass;

	// Determine the target class
	if (ci.superConstructorCall()) {
	    ClassType superClass = currentClass.superClass();
	    targetClass = superClass != null ? superClass.myDecl : null;
	}
	else
	    targetClass = currentClass;

	// If the targetClass is null then it was a super(...) call on a class without a superclass
	// (this can probably never happen cause everyone extends Object).
	if (targetClass == null) 
	    // Super class does not exist. targetClass can never be null for a 'this' 
	    // constructor call.
	    Error.error(ci, "Class '" + currentClass.name() + "' does not have a super class.");	

	// Type check the actual parameters, and create a parameter signature 
	// to be used for lookup in the symbol table.
	Sequence actualParams = ci.args();
	Expression ap = null;               // Holds the actual parameter.
	int actualParamCount = 0;
	String s = "";                      // The parameter signature
	Type apt;                           // Holds the type of the actual parameter.

	if (actualParams != null)
	    actualParamCount = actualParams.nchildren;

	for (int i=0; i<actualParamCount; i++) {
	    ap = (Expression)actualParams.children[i];
	    apt = (Type) ap.visit(this);
	    s = s + apt.signature();
	}

	// Call find method to find the constructor of the target class.
	constructor = (ConstructorDecl)findMethod(targetClass.constructors, targetClass.name(), ci.args(), false);

	// If we didn't find anything list the candidates.
	if (constructor == null) {
	    System.out.println("No constructor " + targetClass.name() + "(" + Type.parseSignature(s) + " ) found.\nCandidates are:");
	    listCandidates(targetClass, targetClass.constructors, targetClass.name());
	    System.exit(1);
	}

	// Check if we have a circular call.
	if (constructor == currentContext)
	    Error.error(ci,"recursive constructor invocation of constructor " + targetClass.name() + "(" + Type.parseSignature(constructor.paramSignature()) + " ).");

	ci.targetClass = targetClass;
	ci.constructor = constructor;
	//-->

	return null;
    }

    /** CLASS DECLARATION */
    public Object visitClassDecl(ClassDecl cd) {
	println(cd.line + ": Visiting a class declaration " + cd.name());

	// The only check to do here is that we cannot have repreated interface implementations.
	// E.g.: class A implements I, I { ... } is illegal.

	// Update the current class.
	currentClass = cd;
	//<--
	// Check that we don't repeat any interfaces.
	for (int i=0;i<cd.interfaces().nchildren;i++) 
	    for (int j=i+1;j<cd.interfaces().nchildren;j++) 
		if (((ClassType)cd.interfaces().children[i]).name().getname().equals(((ClassType)cd.interfaces().children[j]).name().getname()))
		    Error.error(cd,"Repeated interface '" + ((ClassType)cd.interfaces().children[i]).name() + "'");

	super.visitClassDecl(cd);
	//-->

	return null;
    }

    /** CONSTRUCTOR DECLARATION */
    public Object visitConstructorDecl(ConstructorDecl cd) {
	println(cd.line + ": Visiting a constructor declaration");

	// Update the current context
	currentContext = cd;

	//<--
	super.visitConstructorDecl(cd);
	//-->

	return null;
    }

    /** DO STATEMENT */
    public Object visitDoStat(DoStat ds) {
	println(ds.line + ": Visiting a do statement");

	//<--
	// Compute the type of the expression
	Type eType = (Type) ds.expr().visit(this);

	// Check that the type of the expression is a boolean
	if (!eType.isBooleanType())
	    Error.error(ds, "Non boolean Expression found as test in do-statement.");

	// Type check the statement of the do statement;
	if (ds.stat() != null)
	    ds.stat().visit(this);
	//-->

	return null;
    }

    /** FIELD DECLARATION */
    public Object visitFieldDecl(FieldDecl fd) {
	println(fd.line + ": Visiting a field declaration");

	// Update the current context
	currentContext = fd;
	// set inFieldInit to true as we are about to visit the field initializer.
	// (happens from visitVar() if it isn't null.
	inFieldInit = true;
	// Set the current field.
	currentFieldDecl = fd;
	// Visit the var
	if (fd.var().init() != null)
	    fd.var().visit(this);
	// Set current field back to null (fields cannot be nested, so this is OK)
	currentFieldDecl = null;
	// set ifFieldInit back to false as we are done with the initializer.
	inFieldInit = false;

	return fd.type();
    }

    /** FIELD REFERENCE */
    public Object visitFieldRef(FieldRef fr) {
	println(fr.line + ": Visiting a field reference" + fr.target());

	Type targetType = (Type) fr.target().visit(this);
	String field    = fr.fieldName().getname();
	// Changed June 22 2012 ARRAY
	if (fr.fieldName().getname().equals("length")) {
	    if (targetType.isArrayType()) {
		fr.type = new PrimitiveType(PrimitiveType.IntKind);
		println(fr.line + ": Field Reference was a an Array.length reference, and it has type: " + fr.type);
		fr.targetType = targetType;
		return fr.type;
	    }
	}

	if (targetType.isClassType()) {
	    ClassType c = (ClassType)targetType;
	    ClassDecl cd = c.myDecl;
	    fr.targetType = targetType;

	    println(fr.line + ": FieldRef: Looking up symbol '" + field + "' in fieldTable of class '" + 
		    c.typeName() + "'.");

	    // Lookup field in the field table of the class associated with the target.
	    FieldDecl lookup = (FieldDecl) NameChecker.NameChecker.getField(field, cd);

	    // Field not found in class.
	    if (lookup == null)
		Error.error(fr,"Field '" + field + "' not found in class '" + cd.name() + "'.");
	    else {
		fr.myDecl = lookup;
		fr.type = lookup.type();
	    }
	} else 
	    Error.error(fr,"Attempt to access field '" + field + "' in something not of class type.");
	println(fr.line + ": Field Reference has type: " + fr.type);

	/*if (inFieldInit && currentFieldDecl.fieldNumber <= fr.myDecl.fieldNumber && currentClass.name().equals(   (((ClassType)fr.targetType).myDecl).name()))
	    Error.error(fr,"Illegal forward reference of non-initialized field.");
	*/
	return fr.type;
    }

    /** FOR STATEMENT */
    public Object visitForStat(ForStat fs) {
	println(fs.line + ": Visiting a for statement");

	//<--
	fs.init().visit(this);
	if (fs.expr() != null) {
	    Type eType = (Type) fs.expr().visit(this);

	    if (!eType.isBooleanType())
		Error.error(fs, "Non boolean Expression found in for-statement.");
	}
        fs.incr().visit(this);
	if (fs.stats() != null)
	    fs.stats().visit(this);
	//-->

	return null;
    }

    /** IF STATEMENT */
    public Object visitIfStat(IfStat is) {
	println(is.line + ": Visiting a if statement");

	//<--
	Type eType = (Type) is.expr().visit(this);

	if (!eType.isBooleanType())
	    Error.error(is, "Non boolean Expression found as test in if-statement.");
	if (is.thenpart() != null) 
	    is.thenpart().visit(this);
	if (is.elsepart() != null) 
	    is.elsepart().visit(this);
	//-->

	return null;
    }

    /** INVOCATION */
    public Object visitInvocation(Invocation in) {
	println(in.line + ": Visiting an Invocation");

	//<--
	Type targetType = null;
	ClassDecl cd = null;
	String methodName = in.methodName().getname();
		
	// target == null, i.e., we have an invocation of a localmethod.
	if (in.target() == null) {
	    cd = currentClass;
	    // Dec 6. 2017 - test! ;-)
	    in.targetType = new ClassType(currentClass.className());
	    ((ClassType)in.targetType).myDecl = currentClass;
	}
	else {
	    // obj.method or class.method.
	    targetType = (Type) in.target().visit(this);
	    in.targetType = targetType;
			
	    // 12/06/13 - .length() in String
	    if (in.target() != null && in.targetType.isStringType() && methodName.equals("length") && in.params().nchildren == 0) {	
		in.type = new PrimitiveType(PrimitiveType.IntKind);	
		println(in.line	 + ": Invocation has type: " + in.type);
		in.targetMethod = null;
		return in.type;		
	    }
	    // 12/06/13 - .charAt(<int>) in String
	    if (in.target() != null && in.targetType.isStringType() && methodName.equals("charAt") && in.params().nchildren == 1) {
		Type t = (Type)in.params().children[0].visit(this);
		if (!t.isIntegerType()) 
		    Error.error(in,"method charAt in class String cannot be applied to " + t.typeName() + ".");
		in.type = new PrimitiveType(PrimitiveType.CharKind);
		println(in.line	 + ": Invocation has type: " + in.type);
		in.targetMethod = null;
		return in.type;		
	    }
					
	    if (targetType instanceof ClassType) 
		cd = ((ClassType)targetType).myDecl;
	    else 
		Error.error(in,"Attempt to invoke method '" + methodName + 
			    "' in something not of class type.");
	}

	// Generate the signature of the actual parameters for the lookup.
	Sequence actualParams = in.params();
	Expression ap = null;             // Holds the actual parameter.
	int actualParamCount = 0;
	String s = "";                    // Signature for nice error messages
	Type apt;                         // Holds the type of the actual parameter.

	if (actualParams != null)
	    actualParamCount = actualParams.nchildren;

	for (int i=0; i<actualParamCount; i++) {
	    ap = (Expression)actualParams.children[i];
	    apt = (Type) ap.visit(this);
	    s = s + apt.signature();
	}

	// Lookup method in the method table of the class associated with the target.
	MethodDecl method = (MethodDecl)findMethod(cd.allMethods, methodName, in.params(), true);

	// Method not found.
	if (method == null) {
	    System.out.print(Error.fileName + ": No method " + methodName);
	    System.out.println("(" + Type.parseSignature(s) + " ) found. \nCandidates are:");
	    listCandidates(cd, cd.allMethods, methodName);
	    System.exit(1);
	}

	in.targetMethod = method;

	// Everything is ok - return the methods type
	// This type is the return type of the method
	in.type = method.returnType();
	//-->

	println(in.line + ": Invocation has type: " + in.type);
	return in.type;
    }

    /** LITERAL */
    public Object visitLiteral(Literal li) {
	println(li.line + ": Visiting a literal (" + li.getText() + ")");

	//<--
	// Remember that the constants in PrimitiveType are defined from the ones
	// in Literal, so its it ok to just use li.kind! -- except for the null literal.

	if (li.getKind() == Literal.NullKind) 
	    li.type = new NullType(li);
	else {
	    li.type = new PrimitiveType(li.getKind());
	}

	/* This experiment backfired!!!
	    //
	    // experimental 3/17/22
	    // this should alleviate all checks of this kind anywhere else.
	    // Literal.constantValue will return either a String or a BigDecimal
	    if (li.getKind() == Literal.BooleanKind)
		li.type = new PrimitiveType(li.getKind());
	    else if (li.getKind() == Literal.CharKind)
		li.type = new PrimitiveType(li.getKind());
	    else if (!(li.constantValue() instanceof String)) {
		if (li.getKind() == Literal.FloatKind || li.getKind() == Literal.DoubleKind)
		    li.type = new PrimitiveType(li.getKind());
		else if (Literal.isByteValue(((BigDecimal)li.constantValue()).longValue())) 
		    li.type = new PrimitiveType(PrimitiveType.ByteKind);
		else if (Literal.isShortValue(((BigDecimal)li.constantValue()).longValue()))
		    li.type = new PrimitiveType(PrimitiveType.ShortKind);
		else if (Literal.isCharValue(((BigDecimal)li.constantValue()).longValue()))
		    li.type = new PrimitiveType(PrimitiveType.CharKind);
		else
		    li.type = new PrimitiveType(li.getKind());
	    } else
		li.type = new PrimitiveType(li.getKind());
	}
	*/
	//-->

	println(li.line + ": Literal has type: " + li.type);
	return li.type;
    }

    /** METHOD DECLARATION */
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ": Visiting a method declaration");
	currentContext = md;

	//<--
	super.visitMethodDecl(md); 
	//-->

	return null;
    }

    /** NAME EXPRESSION */
    public Object visitNameExpr(NameExpr ne) {
	println(ne.line + ": Visiting a Name Expression");

	//<--
	if (ne.myDecl instanceof LocalDecl || ne.myDecl instanceof ParamDecl) {
	    ne.type = ((VarDecl)ne.myDecl).type(); 
	}
	else if (ne.myDecl instanceof ClassDecl) {
	    // it wasn't a field - so it must be a class.
	    // if it weren't a class it would have been caught in the 
	    // name resolution phase
	    ne.type = new ClassType(ne.name());
	    // or how about just new ClassType(ne.name()) Einstein!!!
	    ((ClassType)ne.type).myDecl = (ClassDecl)ne.myDecl;
	} else 
	    Error.error(ne,"Unknown name expression '" + ne.name().getname() + "'.");
	//-->

	println(ne.line + ": Name Expression has type: " + ne.type);
	return ne.type;
    }

    /** NEW */
    public Object visitNew(New ne) {
	println(ne.line + ": Visiting a new");

	//<--
	ConstructorDecl constructor;
	ne.type().visit(this);

	// Get the class of which we want to create a new object.
	ClassType ct = ne.type();
	ClassDecl cd = ct.myDecl;

	// We cannot create a new object based on an interface either.
	if (cd.isInterface())
	    Error.error(ne, "Cannot instantiate interface '" + cd.name() + "'.");

	// Generate the signature of the actual parameters
	Sequence actualParams = ne.args();
	Expression ap = null; // Holds the actual parameter.
	int actualParamCount = 0;
	String s = "";
	Type apt; // Holds the type of the actual parameter.

	if (actualParams != null)
	    actualParamCount = actualParams.nchildren;

	for (int i=0; i<actualParamCount; i++) {
	    ap = (Expression)actualParams.children[i];
	    apt = (Type)ap.visit(this);
	    s = s + apt.signature();
	}

	// findMethod needs a sequence of methods to search, so make
	// the symboltable entry for <init> into a sequence

	constructor = (ConstructorDecl)findMethod(cd.constructors, cd.name(), ne.args(), false);

	if (constructor == null) {
	    System.out.println("No constructor " + cd.name() + "(" + Type.parseSignature(s) + " ) found.\nCandidates are:");
	    listCandidates(cd, cd.constructors, cd.name());
	    System.exit(1);
	}

	ne.setConstructorDecl(constructor);
	ne.type = ct;
	//-->

	println(ne.line + ": New has type: " + ne.type);
	return ne.type;
    }


    /** RETURN STATEMENT */
    public Object visitReturnStat(ReturnStat rs) {
	println(rs.line + ": Visiting a return statement");
	Type returnType;

	if (currentContext instanceof MethodDecl)
	    returnType = ((MethodDecl)currentContext).returnType();
	else
	    returnType = null;

	// Check is there is a return in a Static Initializer
	if (currentContext instanceof StaticInitDecl) 
	    Error.error(rs,"return outside method");

	// Check if a void method is returning something.
	if (returnType == null || returnType.isVoidType()) {
	    if (rs.expr() != null)
		Error.error(rs, "Return statement of a void function cannot return a value.");
	    return null;
	}

	// Check if a non void method is returning without a proper value.
	if (rs.expr() == null && returnType != null)
	    Error.error(rs, "Non void function must return a value.");

	Type returnValueType = (Type) rs.expr().visit(this);	
	if (rs.expr().isConstant()) {
	    if (returnType.isShortType() && Literal.isShortValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		;// is ok break;                                                                                                    
	    else if (returnType.isByteType() && Literal.isByteValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		; // is ok break;                                                                                                   
	    else if (returnType.isCharType() && Literal.isCharValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		; // break;
	    else if (!Type.assignmentCompatible(returnType,returnValueType))
		Error.error(rs, "Illegal value of type " + returnValueType.typeName() + 
			    " in method expecting value of type " + returnType.typeName() + ".");
	} else if (!Type.assignmentCompatible(returnType,returnValueType))
	    Error.error(rs, "Illegal value of type " + returnValueType.typeName() + 
			" in method expecting value of type " + returnType.typeName() + ".");
	rs.setType(returnType);
	return null;
    }

    /** STATIC INITIALIZER */
    public Object visitStaticInitDecl(StaticInitDecl si) {
	println(si.line + ": Visiting a static initializer");

	//<--
	currentContext = si;
	si.initializer().visit(this);
	//-->

	return null;
    }

    /** SUPER */
    public Object visitSuper(Super su) {
	println(su.line + ": Visiting a super");

	//<--
	// Check that currentClass has a super class, and return it.
	if (currentClass.superClass() == null) 
	    // Reference to super brought us here, but there is no super
	    // class, so report an error
	    Error.error(su, "Class '" + currentClass.name() + "' does not have a superclass.");

	su.type = currentClass.superClass();
	println(su.line + ": Super has type: " + su.type);
	//-->

	return su.type;
    }

    /** SWITCH STATEMENT */
    public Object visitSwitchStat(SwitchStat ss) {
	println(ss.line + ": Visiting a Switch statement");

	//<--
	SwitchGroup sg = null;
	SwitchLabel sl = null;
	int i,j;
	Type lType;
	Type eType = (Type) ss.expr().visit(this); 
	Set<String> ht = new HashSet<String>();
	if ((!eType.isIntegralType() || eType.isLongType()) && !eType.isStringType())
	    Error.error(ss, "Switch statement expects integer or string type." + eType);

	
	
	for (i=0;i<ss.switchBlocks().nchildren; i++) {
	    sg = (SwitchGroup)ss.switchBlocks().children[i];
	    for(j=0; j<sg.labels().nchildren;j++) {
		sl = (SwitchLabel)sg.labels().children[j];
		if (sl.isDefault())
		    continue;
		if (!sl.expr().isConstant())
		    Error.error(sl, "Switch labels must be constants.");
		lType = (Type) sl.expr().visit(this);

		if ((!lType.isIntegralType() || lType.isLongType()) && !lType.isStringType())
		    if (eType.isStringType())
			Error.error(sl, "Switch labels must be of type string.");
		    else
			Error.error(sl, "Switch labels must be of type int.");

		if ((lType.isIntegralType() && eType.isStringType()) || (lType.isStringType() && eType.isIntegralType()))
		    Error.error(sl, "Switch labels must match the type of the expression.");

	    }
	    sg.statements().visit(this);
	}

	for (i=0;i<ss.switchBlocks().nchildren; i++) {
	    sg = (SwitchGroup)ss.switchBlocks().children[i];
	    for(j=0; j<sg.labels().nchildren;j++) {
		sl = (SwitchLabel)sg.labels().children[j];
		if (sl.isDefault()) {
		    if (ht.contains("default")) 
			Error.error(sl,"Duplicate default label.");
		    else
			ht.add("default");
		    continue;
		}
		String strval;
		if (!eType.isStringType() && sl.isDefault())
		    strval = (String)sl.expr().constantValue();
		else
		    strval = "" + ((BigDecimal)sl.expr().constantValue()).intValue();
		if (ht.contains(strval))
		    Error.error(sl,"Duplicate case label.");
		else {
		    ht.add(strval);
		}
	    }

	}
	if (eType.isStringType()) {
	    System.out.println("**********");

	    Sequence s = new Sequence();

	    // String __s = <expr>;
	    LocalDecl ld1 = new LocalDecl(new PrimitiveType(PrimitiveType.StringKind),
					  new Var(
						  new Name(new Token(sym.IDENTIFIER, "__s", 0, 0, 0)),
						  ss.expr()));
	    s.append(ld1);

	    // int __i = s.hasCode()
	    LocalDecl ld2 = new LocalDecl(new PrimitiveType(PrimitiveType.IntKind),
                                          new Var(
                                                  new Name(new Token(sym.IDENTIFIER, "__i", 0, 0, 0)),
						  new Invocation(
								 new NameExpr(new Name(new Token(sym.IDENTIFIER, "__s", 0, 0 ,0))),
								 new Name(new Token(sym.IDENTIFIER, "hashCode", 0, 0, 0)),
								 new Sequence())));	        
	    s.append(ld2);
	    s.visit(new Utilities.PrintVisitor());

	    for (i=0;i<ss.switchBlocks().nchildren; i++) {
		sg = (SwitchGroup)ss.switchBlocks().children[i];
		for(j=0; j<sg.labels().nchildren;j++) {
		    sl = (SwitchLabel)sg.labels().children[j];
		    String labelString = ((String)sl.expr().constantValue()).replaceAll("\"","");
		    System.out.println(labelString + " = " + labelString.hashCode());
		    System.out.println(labelString.equals("Hello"));
									
		}
	    }
	    
	    System.out.println("DONE PRINTING");
	    



	    
	}	
	//-->

	return null;
    }

    public void buildClassHierarchyList(ArrayList<ClassDecl> classes, HashSet<String> seenClasses, ClassDecl cd) {
	if (seenClasses.contains(cd.name()))
	    return;
	
	classes.add(cd);
	seenClasses.add(cd.name());
	
        if (cd.superClass() != null) 
	    buildClassHierarchyList(classes, seenClasses, cd.superClass().myDecl);
        
        Sequence interfaces = cd.interfaces();
        if (interfaces.nchildren > 0) {
            for (int i=0; i<interfaces.nchildren; i++) {
		buildClassHierarchyList(classes, seenClasses,((ClassType)interfaces.children[i]).myDecl);
            }
        }
    }
   
    public Type computeIntersectionType(ClassType trueType, ClassType falseType) {

	// 3/24/22 experimental - the next 6 lines removed - they are probably not needed.
	/*		
	if (Type.isSuper(trueType, falseType)) {
	    return trueType;
	} else if (Type.isSuper(falseType, trueType)) {
	    return falseType;
	 
	    }*/
	// build a list of superclasses for both.
	ArrayList<ClassDecl> trueHierarchy = new ArrayList<ClassDecl>();
	HashSet<String> trueSeenClasses = new HashSet<String>();

	buildClassHierarchyList(trueHierarchy, trueSeenClasses, trueType.myDecl);
	//System.out.print(trueType.myDecl.name() + ": { ");
	//for (ClassDecl s : trueHierarchy)
	//    System.out.print(s.name() + " ");
	//System.out.println("}");

	ArrayList<ClassDecl> falseHierarchy = new ArrayList<ClassDecl>();
	HashSet<String> falseSeenClasses = new HashSet<String>();
	buildClassHierarchyList(falseHierarchy, falseSeenClasses, falseType.myDecl);

	//System.out.print(falseType.myDecl.name() + ": { ");
	//for (ClassDecl s : falseHierarchy)
	//    System.out.print(s.name() + " ");
	//System.out.println("}");

	// compute their intersection
	ArrayList<ClassDecl> commonHierarchy = new ArrayList<ClassDecl>();
	HashSet<String> commonClasses = new HashSet<String>();
	for (ClassDecl cd : trueHierarchy) {
	    if (falseSeenClasses.contains(cd.name())) {
		commonHierarchy.add(cd);
		commonClasses.add(cd.name());
	    }
	}

	//System.out.print("common classes: { ");
        //for (int i=0; i<commonHierarchy.size(); i++) {
	//    ClassDecl cd = commonHierarchy.get(i);
        //    System.out.print(cd.name() + " ");
	//}
        //System.out.println("}");
	//System.out.println("Size: = " + commonHierarchy.size());
	// Do some weeding out here.
	for (int i=0; i<commonHierarchy.size(); i++) {
	    ClassDecl cd1 = commonHierarchy.get(i);
	    if (cd1 == null) {
		//System.out.println("oops i=" + i + " is null!");
		continue;
	    }
	    for (int j=i+1; j<commonHierarchy.size(); j++) {
		//System.out.println("(i,j) = (" + i + "," + j + ")");
	       ClassDecl cd2 = commonHierarchy.get(j);
	       if (cd2 == null) {
		   //System.out.println("oops j=" + j + " is null!");
		   continue;
	       }
	       //System.out.print("Comparing " + cd1.name() + " to " + cd2.name() + ": ");
	       ClassType ct1 = new ClassType(cd1.className());
	       ct1.myDecl = cd1;
	       ClassType ct2 = new ClassType(cd2.className());
	       ct2.myDecl = cd2;
	    
	       if (Type.isSuper(ct1, ct2)) {
		   //System.out.println(" Throwing away " + cd1.name());
		   commonHierarchy.set(i, null);
	       } else if (Type.isSuper(ct2, ct1)) {
		   //System.out.println(" Throwing away " + cd2.name());
		   commonHierarchy.set(j, null);
	       } else
		   ;//System.out.println(" Keeping both");
	    }
	}
	// what's left?
	//System.out.print("common classes left over: { ");
        //for (ClassDecl s : commonHierarchy) {
	//    if (s == null)
	//		continue;
	//   System.out.print(s.name() + " ");
	//}
        //System.out.println("}");

	// there should be exactly ONE class and any number of interfaces left.
	ClassType superClass = null;
	Sequence interfaces = new Sequence();
	for (ClassDecl s : commonHierarchy) {
            if (s == null)
                continue;
	    ClassType ct = new ClassType(s.className());
	    ct.myDecl = s;
	    
	    if (s.isInterface())
		interfaces.append(ct);
	    else
		superClass = ct;
	}
	int intNo = ClassDecl.interSectionTypeCounter++;
	ClassDecl cd = new ClassDecl(new Sequence(new Modifier(Modifier.Public)),
				     new Name(new Token(sym.IDENTIFIER, "INT#"+intNo, 0,0,0)),
				     superClass, interfaces, new Sequence(), ClassDecl.IS_NOT_INTERFACE);

	ClassType ct = new ClassType(cd.className());
	ct.myDecl = cd;
	//cd.visit(new Utilities.PrintVisitor());
	ct.isIntersectionType = true;
	return ct;
    }
    
    /** TERNARY EXPRESSION */
    public Object visitTernary(Ternary te) {
	println(te.line + ": Visiting a ternary expression");

	//<--
	Type eType = (Type)te.expr().visit(this);
	Type trueBranchType  = (Type)te.trueBranch().visit(this);
	Type falseBranchType = (Type)te.falseBranch().visit(this);

	if (!eType.isBooleanType())
	    Error.error(te, "Non boolean Expression found as test in ternary expression.");
	if (trueBranchType instanceof PrimitiveType && falseBranchType instanceof PrimitiveType) {
	    if (Type.assignmentCompatible(falseBranchType, trueBranchType) ||
		Type.assignmentCompatible(trueBranchType, falseBranchType)) 
		te.type = new PrimitiveType(PrimitiveType.ceiling((PrimitiveType)trueBranchType, (PrimitiveType)falseBranchType));
	    else
		Error.error(te,"Both branches of a ternary expression must be of assignment compatible types.");
	} else if (trueBranchType.isClassType() && falseBranchType.isClassType()) {
	    te.type = computeIntersectionType((ClassType)trueBranchType, (ClassType)falseBranchType);
	} else if ((trueBranchType.isClassType() && falseBranchType.isNullType()) ||
		   (trueBranchType.isNullType() && falseBranchType.isClassType()) ||
		   (trueBranchType.isNullType() && falseBranchType.isNullType()))
	    
	    te.type = (trueBranchType.isNullType() ? falseBranchType : trueBranchType);
	else
	    Error.error(te,"Both branches of a ternary expression must be of assignment compatible types.");       
	//-->
	println(te.line + ": Ternary has type: " + te.type);
	return te.type;
    }

    /** THIS */
    public Object visitThis(This th) {
	println(th.line + ": Visiting a this statement");

	th.type = th.type();

	println(th.line + ": This has type: " + th.type);
	return th.type;
    }

    /** UNARY POST EXPRESSION */
    public Object visitUnaryPostExpr(UnaryPostExpr up) {
	println(up.line + ": Visiting a unary post expression");
	//<--
	Type eType = null;

	eType = (Type) up.expr().visit(this);

	if (up.expr() instanceof NameExpr || up.expr() instanceof FieldRef || up.expr() instanceof ArrayAccessExpr) {
	    if (!eType.isNumericType())
		Error.error(up, "Cannot apply operator '" + up.op().operator() +
			    "' to something of type " + eType.typeName() + ".");	   
	} else 
	    Error.error(up, "Variable expected, found value.");

	up.type = eType;
	//-->

	println(up.line + ": Unary Post Expression has type: " + up.type);
	return up.type;
    }

    /** UNARY PRE EXPRESSION */
    public Object visitUnaryPreExpr(UnaryPreExpr up) {
	println(up.line + ": Visiting a unary pre expression");

	//<--
	Type eType = (Type) up.expr().visit(this);

	switch (up.op().getKind()) {
	case PreOp.PLUS:
	case PreOp.MINUS:
	    if (!eType.isNumericType())
		Error.error(up, "Cannot apply operator '" + up.op().operator() + 
			    "' to something of type " + eType.typeName() + ".");
	    break;
	case PreOp.NOT:
	    if (!eType.isBooleanType())
		Error.error(up, "Cannot apply operator '" + up.op().operator() +
			    "' to something of type " + eType.typeName() + ".");
	    break;
	case PreOp.COMP:
	    if (!eType.isIntegralType())
		Error.error(up, "Cannot apply operator '" + up.op().operator() +
			    "' to something of type " + eType.typeName() + ".");
	    break;
	case PreOp.PLUSPLUS:
	case PreOp.MINUSMINUS:
	    if (!(up.expr() instanceof NameExpr) && !(up.expr() instanceof FieldRef) &&
		!(up.expr() instanceof ArrayAccessExpr))
		Error.error(up, "Variable expected, found value.");

	    if (!eType.isNumericType())
		Error.error(up, "Cannot apply operator '" + up.op().operator() +
			    "' to something of type " + eType.typeName() + ".");
	    break;
	}

	// Promote operations on byte, short and char to int.
	if (eType.isByteType() || eType.isShortType() || eType.isCharType())
	    eType = new PrimitiveType(PrimitiveType.IntKind); 

	up.type = eType;
	//-->

	println(up.line + ": Unary Pre Expression has type: " + up.type);
	return up.type;
    }

    /** VAR */
    public Object visitVar(Var va) {
	println(va.line + ": Visiting a var");

	//<--
	if (va.init() != null) {
	    Type vType = va.myDecl.type();
	    Type iType = (Type)va.init().visit(this);

	    // TODO: cant we just visit the literal here....
	    
	    if (va.init().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)va.init().constantValue()).longValue()))
		    return null;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)va.init().constantValue()).longValue()))
		    return null;
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)va.init().constantValue()).longValue()))
		    return null;
	    }

	    if (!Type.assignmentCompatible(vType,iType))
		Error.error(va, "Cannot assign value of type " + iType.typeName() + " to variable of type " +
			    vType.typeName() + ".");


	}
	//-->

	return null;
    }

    /** WHILE STATEMENT */
    public Object visitWhileStat(WhileStat ws) {
	println(ws.line + ": Visiting a while statement"); 

	//<--
	Type eType = (Type) ws.expr().visit(this);

	if (!eType.isBooleanType())
	    Error.error(ws, "Non boolean Expression found as test in while-statement.");
	if (ws.stat() != null)
	    ws.stat().visit(this);
	//-->

	return null;
    }

}

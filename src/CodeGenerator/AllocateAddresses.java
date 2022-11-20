package CodeGenerator;

import AST.*;
import Utilities.Visitor;

class AllocateAddresses extends Visitor {

	private Generator gen;
	private ClassDecl currentClass;
    private ClassBodyDecl currentBodyDecl;

	AllocateAddresses(Generator g, ClassDecl currentClass, boolean debug) {
		this.debug = debug;
		gen = g;
		this.currentClass = currentClass;
	}

        // BLOCK
        public Object visitBlock(Block bl) {
    	    // YOUR CODE HERE
	    return null;   
        }
    

	// LOCAL VARIABLE DECLARATION
	public Object visitLocalDecl(LocalDecl ld) {
		// YOUR CODE HERE
		println(ld.line + ": LocalDecl:\tAssigning address:  " + ld.address + " to local variable '" + ld.var().name().getname() + "'.");
		return null;
	}

	// PARAMETER DECLARATION
	public Object visitParamDecl(ParamDecl pd) {
		// YOUR CODE HERE
		println(pd.line + ": ParamDecl:\tAssigning address:  " + pd.address + " to parameter '" + pd.paramName().getname() + "'.");
		return null;
	}

	// METHOD DECLARATION
	public Object visitMethodDecl(MethodDecl md) {
		println(md.line + ": MethodDecl:\tResetting address counter for method '" + md.name().getname() + "'.");
		// YOUR CODE HERE
		println(md.line + ": End MethodDecl");	
		return null;
	}

	// CONSTRUCTOR DECLARATION
	public Object visitConstructorDecl(ConstructorDecl cd) {	
		println(cd.line + ": ConstructorDecl:\tResetting address counter for constructor '" + cd.name().getname() + "'.");
		gen.resetAddress();
		gen.setAddress(1);
		currentBodyDecl = cd;
		super.visitConstructorDecl(cd);
		cd.localsUsed = gen.getLocalsUsed();
		//System.out.println("Locals Used: " + cd.localsUsed);
		println(cd.line + ": End ConstructorDecl");
		return null;
	}

	// STATIC INITIALIZER
	public Object visitStaticInitDecl(StaticInitDecl si) {
		println(si.line + ": StaticInit:\tResetting address counter for static initializer for class '" + currentClass.name() + "'.");
		// YOUR CODE HERE
		println(si.line + ": End StaticInit");
		return null;
	}
}


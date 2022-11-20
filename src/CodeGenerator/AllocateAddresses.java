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

    // BLOCK (COMPLETE)
    public Object visitBlock(Block bl) {
    	// YOUR CODE HERE
        int tempAddress = gen.getAddress();
        bl.visitChldren(this);
        gen.setAddress(tempAddress);
        // - END -
        return null;   
    }
    

	// LOCAL VARIABLE DECLARATION (COMPLETE)
	public Object visitLocalDecl(LocalDecl ld) {
		// YOUR CODE HERE
		ld.address = gen.getAddress();
		
		if (ld.type()isDoubleType() || ld.type()isLongType()) {
			gen.inc2Address();
		}
		else {
			gen.incAddress();
		}
		// - END -
		
		println(ld.line + ": LocalDecl:\tAssigning address:  " + ld.address + " to local variable '" + ld.var().name().getname() + "'.");
		return null;
	}

	// PARAMETER DECLARATION (COMPLETE)
	public Object visitParamDecl(ParamDecl pd) {
		// YOUR CODE HERE
		pd.address = gen.getAddress();
		
		if (pd.type()isDoubleType() || pd.type()isLongType()) {
			gen.inc2Address();
		}
		else {
			gen.incAddress();
		}
		// - END -
		
		println(pd.line + ": ParamDecl:\tAssigning address:  " + pd.address + " to parameter '" + pd.paramName().getname() + "'.");
		return null;
	}

	// METHOD DECLARATION (COMPLETE)
	public Object visitMethodDecl(MethodDecl md) {
		println(md.line + ": MethodDecl:\tResetting address counter for method '" + md.name().getname() + "'.");
		// YOUR CODE HERE
		gen.resetAddress();
		if (md.getModifiers().isStatic()) {
			gen.setAddress(0);
		}
		else {
			gen.setAddress(1);
		}
		currentBodyDecl = md;
		super.visitMethodDecl(md);
		md.localsUsed = gen.getLocalsUsed();
		// - END -
		
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

	// STATIC INITIALIZER (COMPLETE)
	public Object visitStaticInitDecl(StaticInitDecl si) {
		println(si.line + ": StaticInit:\tResetting address counter for static initializer for class '" + currentClass.name() + "'.");
		// YOUR CODE HERE
		gen.resetAddress();
		gen.setAddress(0);
		currentBodyDecl = si;
		super.visitStaticInitDecl(si);
		si.localsUsed = gen.getLocalsUsed();
		// - END -
		
		println(si.line + ": End StaticInit");
		return null;
	}
}


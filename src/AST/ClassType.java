package AST;
import Utilities.Visitor;

public class ClassType extends Type {

    public ClassDecl myDecl; // Point to the class representing this class type
    public Boolean isIntersectionType = false; // used for ternary expressions with class types in both branches.
    
	public ClassType(Name className) { 
		super(className);
		nchildren = 1;
		children = new AST[] { className };
	}

	public Name name() { 
		return (Name)children[0]; 
	}

	public String toString() {
		return "(ClassType: " + name() + ")";
	}

	public String typeName() {
	    if (name().getname().startsWith("INT#")) {
		String s = name().getname() + " (extends ";
		s += myDecl.superClass().name().getname();
		if (myDecl.interfaces().nchildren>0) {
		    s += " implements ";
		    for (int i=0; i<myDecl.interfaces().nchildren; i++) {
			s += ((ClassType)myDecl.interfaces().children[i]).name().getname();
			if (i <myDecl.interfaces().nchildren-1)
			    s += ", ";
		    }
		}
		s += ")"; 

		return s;
	    } else	    
		return name().getname();
	}
    
	public String signature() {
		return "L"+typeName()+";";
	}


	/* *********************************************************** */
	/* **                                                       ** */
	/* ** Generic Visitor Stuff                                 ** */
	/* **                                                       ** */
	/* *********************************************************** */

	public Object visit(Visitor v) {
		return v.visitClassType(this);
	}


}             





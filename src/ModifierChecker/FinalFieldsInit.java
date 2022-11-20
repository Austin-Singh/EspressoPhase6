package ModifierChecker;
import Utilities.Visitor;
import AST.*;
import Utilities.Error;
import java.util.*;
// Notes:

// We can assume that no initialized final fields are ever
// re-assigned. This was checked in the ModifierChecker.

// all fields in loop bodies like while and for (but not do) that
// aren't guaranteed to execute at least once should not be added to
// initializedFields. Make a copy and restore it afterwards.

// redo: we need two sets: definitely initialized fields and
//                         possibly initilaized fields
// and check need to be made against both of them.
// if (e)
//   S1
// else
//   S2
//
//  defInit = defInit(S1) intersected defInit(S2)
//  possiblyInit = possiblyInit(S1) union possiblyInit(S2)

public class FinalFieldsInit extends Visitor {
    /*    private boolean insideLoop = false;
    private boolean leftHandSide = false;

    HashSet<String> defininite = new HashSet<String>();
    HashSet<STring> maybe      = new HashSet<String>();
    
   
    public FinalFieldsInit() {
	System.out.println("FINAL FIELD INIT CHECKER");
    }



    public Object visitCInovcation(CInvocation ci) {
	println(ci.line + ": Visiting a constructor invocation.");
	ci.args().visit(this);
	// visit the constructor only if it is a 'this' call.
	if (ci.thisConstructorCall())
	    ci.constructor.visit(this);
	return null;
    }
    
    public Object visitClassDecl(ClassDecl cd) {
	println(cd.line + ": Visiting a class declaration.");

	for (int i = 0; i < cd.body().nchildren; i++) {
	    ClassBodyDecl cbd = (ClassBodyDecl)cd.body().children[i];
	    if (cbd instanceof ConstructorDecl) {
		initializedFields = new HashSet<String>();
		ConstructorDecl cdd = (ConstructorDecl)cbd;
		System.out.println(cdd.getname() + "/( " + cdd.paramSignature() + " )");
		cdd.visit(this);
		dumpHashSet("Intialized fields for " + cdd.getname() + "/( " + cdd.paramSignature() + " )", initializedFields);
		
	    }
	}
	return null;
    }

    // Loop-related; give a warning/error if final fields
    // are assigned inside a loop.

    public Object visitWhileStat(WhileStat ws) {
	println(ws.line + ": Visiting a while statement.");
	boolean oldInsideLoop = insideLoop;
	insideLoop = true;

	HashSet<String> tempDefinite = definite;
	HashSet<String> tempMaybe    = maybe;
	definite = new HashSet<String>();
	maybe    = new HashSet<String>();
	
	super.visitWhileStat(ws);

	definite = tempDefinite;
	maybe    = tempMaybe.addAll(maybe);

	insideLoop = oldInsideLoop;
	return null;
    }

    public Object visitForStat(ForStat fs) {
	// for (e1 ; e2 ; e3 ) S
	// M = M U M(e1) U M(e2) U M(e3) U M(S)
	// D = D U D(e1) U D(e2)

	println(fs.line + ": Visiting a for statement.");
	boolean	oldInsideLoop =	insideLoop;
	insideLoop = true;

	fs.init().visit(this);
	if (fs.expr != null)
	    fs.expr().visit(this);
		
	HashSet<String> tempDefinite = definite;
        HashSet<String> tempMaybe    = maybe;
	definite = new HashSet<String>();
        maybe    = new HashSet<String>();
	
	if (fs.stats() != null)
	    fs.stats().visit(this);
	fs.incr().visit(this);
	
	definite = tempDefinite;
	maybe    = tempMaybe.addAll(maybe);
	
	insideLoop = oldInsideLoop;
	return null;
    }

    public Object visitDoStat(DoStat ds) {
	// do S while (e)
	// M = M U M(S) U M(e)
	// D = D U D(S) U D(e)
	
	println(ds.line + ": Visiting a do statement.");
	boolean	oldInsideLoop =	insideLoop;
	insideLoop = true;

	super.visitDoStat(ds);
		
	insideLoop = oldInsideLoop;
	return null;
    }

    
    public Object visitAssignment(Assignment as) {
	println(as.line + ": Visiting an assignment.");
	boolean oldLeftHandSide = leftHandSide;
	leftHandSide = true;
	super.visitAssignment(as);	
	leftHandSide = oldLeftHandSide;
	return null;
    }





    ///GOT THIS FAR
	
    public Object visitFieldRef(FieldRef fr) {

	println(fr.line + ": Visiting a field reference.");
	FieldDecl fd = fr.myDecl;      

	// If we are assignming to a final field (has to be one of our
	// own, so therefore the 'instanceof This' check.)
	if (fr.target() instanceof This && leftHandSide) {
	    if (fd.modifiers.isFinal()) {
		// final fields can only be assigned once, and it is done inside a loop
		if (insideLoop) {
		    Error.error(fr,"Final variable '" + fr.fieldName().getname() + "' might be assigned in loop", false);
		}	    
		// Check if it has already been assigned


		if (definite.contains(fr.fieldName().getname()) || maybe.contains(fr.fieldName().getname()))
		    Error.error(fr.line + ": variable '" + fr.fieldName().getname() + "' may already have been assigned", false);
	    } 
	    definite.add(fr.fieldName().getname());
	    maybe.add(fr.fieldName().getname());
	    return null;
	}
    }

    public Object visitIfStat(IfStat is) {
	println(is.line + ": Visiting an if statement.");
	
	is.expr().visit(this);
	
	HashSet<String> tempDefinite = definite;
	HashSet<String> tempMaybe    = maybe;

	definite = new HashSet<String>();
	maybe    = new HashSet<String>();
	

	// First visit the 'then' part with a the original 'initializedFields'
	is.thenpart().visit(this);
	
	HashSet<String> thenDefinite = definite;
	HashSet<String> thenMaybe    = maybe;

	definite = new HashSet<String>();
        maybe    = new HashSet<String>();
	
	
	HashSet<String> thenPartHashSet = initializedFields;

	if (is.elsepart() != null) {
	    // Make a new copy of temp
	    initializedFields = new HashSet<String>();
	    initializedFields = copyHashSet(temp);
	    //for (String s : temp)
	    //	initializedFields.add(s);
	    dumpHashSet("NOW: ", initializedFields);
	    is.elsepart().visit(this);
	} else
	    initializedFields = new HashSet<String>();

	// temp holds the fileds initialized up until before the if stat
	// thenPartHashSet holds the fields intialized after the then-part
	// initializedFields holds the fields initialized after the else-part

	// make a new list with all the fields from thenPartHashSet that is
	// also in initializedFields and make that the new initializedFields.

	HashSet<String> temp2 = new HashSet<String>();
	temp2 = copyHashSet(thenPartHashSet);
	//for (String s : thenPartHashSet)
	//    if (initializedFields.contains(s))
	//	temp2.add(s);
	dumpHashSet("if-temp ", temp);
	dumpHashSet("if-thenPartHashSet ", thenPartHashSet);
	dumpHashSet("if-initializedFields ", initializedFields);
	dumpHashSet("if ", temp2);
	initializedFields = temp2;
	return null;	
    }
   
    private void dumpHashSet(String name, HashSet<String> hs) {
	System.out.print(name + " = [ ");
	Iterator itr = hs.iterator();
	while (itr.hasNext()) {
	    String s = (String)itr.next();
	    if (itr.hasNext())
		System.out.print(s + ", ");
	    else
		System.out.print(s);
	}
	System.out.println(" ]");
    }

    private HashSet<String> copyHashSet(HashSet<String> s) {
	HashSet<String> copy = new HashSet<String>();
	for (String str : s)
	    copy.add(str);
	return copy;
    }
    
    */    
}

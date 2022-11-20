package CodeGenerator;

import AST.*;

public class CodeGenerator {
    public void generate(Compilation program, boolean debug) {
	int i;
	if (debug) 
	    System.out.println("---------------------------------------------------------");
	
	for (i=0; i<program.types().nchildren;i++) {
	    ClassDecl cd = (ClassDecl)program.types().children[i];
	    if (!cd.generateCode()) {
		System.out.println("Not generating code for '" + cd.name() + "'");
		continue;
	    }
	    if (!Utilities.Settings.generateEVMCode) {
		// do not generate code for Runnable, Object or Thread
		if (cd.name().equals("java/lang/Runnable") ||
		    cd.name().equals("java/lang/Thread") ||
		    cd.name().equals("java/lang/Object"))
		    continue;
	    }
	    // The header of the class is printed out here!
	    Generator g = new Generator(cd, debug);
	    if (debug)
		System.out.println("\n** Assigning Addresses:");
	    cd.visit(new AllocateAddresses(g, cd, debug));
	    if (debug)
		System.out.println("\n** Generating Bytecode Instructions:");
	    cd.visit(new GenerateCode(g, debug));
	    cd.classFile = g.getClassFile();
	}
	
	if (debug) 
	    System.out.println("---------------------------------------------------------");
    }
}

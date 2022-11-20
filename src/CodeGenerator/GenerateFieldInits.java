package CodeGenerator;

import AST.*;
import Utilities.Visitor;
import Jasmin.*;
import Instruction.*;

class GenerateFieldInits extends Visitor {

	private Generator gen;
	private ClassDecl currentClass;
	private boolean generateForStaticFields;
	private ClassFile classFile;
	
	public GenerateFieldInits(Generator g, ClassDecl currentClass, boolean generateForStaticFields) {
		this.currentClass = currentClass;
		this.classFile = g.getClassFile();
		this.gen = g;
		this.generateForStaticFields = generateForStaticFields;
	}

	public Object visitFieldDecl(FieldDecl fd) {
		GenerateCode g = new GenerateCode(gen, debug);
		// we need to set this one manually as the code for 
		// fieldInits might need it - e.g. if trying to generate
		// code for accessing another field.
		g.setCurrentClass (currentClass);

		// Field has an initializer and is not final.
		if (fd.var().init() != null && (!fd.modifiers.isFinal() || (fd.modifiers.isFinal() && !(fd.var().init() instanceof Literal)))) {
			if (fd.modifiers.isStatic() && generateForStaticFields) {
				fd.var().init().visit(g);
				gen.dataConvert(fd.var().init().type, fd.type());
				//gen.println("\tputstatic " + currentClass.name() + "/" + fd.var().name().getname() + 
				//		" " + fd.type().signature());
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putstatic, currentClass.name(), 
						fd.var().name().getname(), fd.type().signature()));
			}
			else if (!fd.modifiers.isStatic() && !generateForStaticFields) {
				println(fd.line + ": FieldDecl:\tGenerating init code for non static field '" + fd.var().name().getname() + "'.");
				//gen.println("\taload_0");
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
				fd.var().init().visit(g);
				gen.dataConvert(fd.var().init().type, fd.type());

				//gen.println("\tputfield "  + currentClass.name() + "/" + fd.var().name().getname() +
				//		" " + fd.type().signature());
				classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putfield,
						currentClass.name(), fd.var().name().getname(), fd.type().signature()));			
			}
		}
		return null;
	}
}


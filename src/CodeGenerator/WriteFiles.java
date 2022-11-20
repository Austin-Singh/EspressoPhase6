package CodeGenerator;

import AST.*;
import java.util.*;
import Jasmin.*;
import Instruction.*;
import Utilities.Settings;
/** The WriteFiles class is used to write the content of a classFile object 
 * to a file. The extension of the file is determined by the fileExt in the 
 * Settings class.
 * 
 * @author Matt Pedersen
 *
 */
public class WriteFiles {

	public static void writeFile(ClassDecl cd, boolean writeOptimized, boolean writeComments) {
		ClassFile classFile = cd.classFile;

		String outputFileName = cd.name() + Settings.fileExt;
		System.out.println("Writing " + (writeOptimized ? "Optimized" : "Unoptimized") + " jasmin file : '" + outputFileName + "'");
		try {
			java.io.PrintWriter out;
			out = new java.io.PrintWriter(new java.io.FileOutputStream(outputFileName));

			// Write the class header 
			writeClass(out, cd);

			// Write all the fields
			Iterator<Field> it = classFile.getFieldsIterator();
			while (it.hasNext()) {
				writeField(out, it.next().getField());
			}

			// Write all the methods
			Iterator<Method> it2 = classFile.getMethodsIterator();
			while (it2.hasNext()) {
				writeMethod(out, it2.next().getMethod(), writeOptimized, writeComments);
			}   
			out.close();
		} catch (java.io.IOException e) {
			System.out.println("An I/O error occured while opening output file " + outputFileName);
			System.out.println(e);
			System.exit(1);
		}
	}


	private static void writeClass(java.io.PrintWriter out, ClassDecl cd) {
		if (cd.isInterface()) {         
			// interface name and modifiers
			out.println(".interface "+ cd.modifiers + " " + cd.name());

			// superclass
			out.println(".super java/lang/Object");
		} else {
			// class name and modifiers
			out.println(".class "+ cd.modifiers + cd.name());

			// super class
			out.print(".super ");
			if (cd.superClass() == null) {
				out.println("java/lang/Object\n");
			}
			else {
				out.println(cd.superClass().myDecl.className());
			}
		}

		// implemented interfaces 
		for (int i=0;i<cd.interfaces().nchildren;i++) {
			out.println(".implements " + ((ClassType)cd.interfaces().children[i]).name().getname());
		}
		out.println("");
	}

	private static void writeMethod(java.io.PrintWriter out,
			ClassBodyDecl method,
			boolean writeOptimized,
			boolean writeComments) {

		if (method instanceof MethodDecl && 
				((MethodDecl)method).name().getname().equals("main") && 
				((MethodDecl)method).returnType().isVoidType() && 
				((MethodDecl)method).getModifiers().isStatic() && 
                                ((MethodDecl)method).getModifiers().isPublic()) {
		    System.out.println("Generating code for the EVM? " + Utilities.Settings.generateEVMCode);
		    if (Utilities.Settings.generateEVMCode) 
			out.println(".method public static main()V");
		    else
			out.println(".method public static main([Ljava/lang/String;)V");
		    out.println("\t.limit stack 50");
		    out.println("\t.limit locals " + ((MethodDecl)method).localsUsed);              
		} else {
			//address = 1;
			boolean isAbstract = false;
			// print parameter signatures
			Sequence params = null;
			String methodName = "";
			Modifiers modifiers = null;
			if (method instanceof MethodDecl) {
				params     = ((MethodDecl)method).params();
				methodName = ((MethodDecl)method).name().getname();
				modifiers  = ((MethodDecl)method).getModifiers();
				isAbstract = ((MethodDecl)method).getModifiers().isAbstract();
			} else if (method instanceof ConstructorDecl) {
				params     = ((ConstructorDecl)method).params();
				methodName = "<init>";
				modifiers  = ((ConstructorDecl)method).getModifiers();
				isAbstract = false;
			} else {
				// Static Initializer
				modifiers = new Modifiers();
				modifiers.set(false, false, new Modifier(Modifier.Abstract));
				isAbstract = false;
			}

			if (method instanceof MethodDecl || method instanceof ConstructorDecl) {
				out.print(".method " + modifiers + methodName);

				out.print("(");
				if (params != null) 
					for (int i=0; i<params.nchildren; i++) 
						out.print(((ParamDecl)params.children[i]).type().signature());
				out.print(")");

				// print return type signature
				if ((method instanceof MethodDecl && ((MethodDecl)method).returnType() == null) ||
						(method instanceof ConstructorDecl)){
					out.println("V");
				} else 
					out.println(((MethodDecl)method).returnType().signature());
			}
			else if (method instanceof StaticInitDecl)
				out.println(".method static <clinit>()V");

			if (!isAbstract) {
				out.println("\t.limit stack 50");
				out.println("\t.limit locals " + method.localsUsed);     
			}
		}

		Vector<Instruction> code;
		code = method.getCode();

		Iterator<Instruction> it = code.iterator();

		// experiemental
		// skip the first block cause it is empty!!
		//it.next();
		// SEEIMS TO WORK!!
		Instruction inst = null;
		while (it.hasNext ()) {
			inst = it.next();

			if (inst instanceof CommentInstruction) {
				if (writeComments) 
					out.println(inst);
			} else if (inst instanceof LabelInstruction) 
				out.println(inst);
			else
				out.println("\t" + inst);
		}

		out.println(".end method\n");
	}

	private static void writeField(java.io.PrintWriter out, FieldDecl field) {
		out.print(".field ");
		out.print("" + field.modifiers);
		if (field.interfaceMember && !field.modifiers.isStatic()) {
			out.print(" static ");
		}
		out.print(field.name() + " ");
		out.print(field.type().signature());
		if (field.modifiers.isFinal()) {
			if (field.var().init() != null)
				if (field.var().init() instanceof Literal)
					out.print(" = " + ((Literal)field.var().init()).getText());
		}
		out.println("\n");
	}
}

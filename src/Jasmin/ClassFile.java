package Jasmin;

import java.util.*;
import AST.*;
import Instruction.*;

public class ClassFile {
	// Hook to point back to the class that this represents the class file for.
	private ClassDecl cd;

	private Method currentMethod;

	private Vector<Field>  fields;
	private Vector<Method> methods;


	public ClassFile(ClassDecl cd) {
		this.cd = cd;
		currentMethod = null;
		fields  = new Vector<Field>();
		methods = new Vector<Method>();	
	}
	
	public ClassDecl getClassDecl() {
		return this.cd;
	}
	
	public Vector<Instruction> getCurrentMethodCode() {
		return currentMethod.getCode();
	}

	public Method getCurrentMethod() {
		return currentMethod;
	}
	
	// ------------------------------------------------------------

	// Methods for 'Methods' and 'Fields'
	public void startMethod(ClassBodyDecl md) {
		currentMethod = new Method(md);
	}    

	public void endMethod() {
		methods.addElement(currentMethod);
		currentMethod = null;
	}

	public void addField(FieldDecl fd) {
		fields.addElement(new Field(fd));
	}

	// ------------------------------------------------------------

	// Adding instructions to the current method
	public void addInstruction(Instruction inst) {
		currentMethod.addInstruction(inst);
	}

	public Iterator<Method> getMethodsIterator() {
		return methods.iterator();
	}

	public Iterator<Field> getFieldsIterator() {
		return fields.iterator();
	}

	public Instruction getLastInstruction() {
		if (currentMethod.getCode().size() > 0)
			return currentMethod.getCode().lastElement();
		return new Instruction(RuntimeConstants.opc_nop);
	}

	public void addComment(AST a, String comment) {
		addInstruction(new CommentInstruction(RuntimeConstants.opc_comment, " (" + a.line + ") " + comment));
	}
} 






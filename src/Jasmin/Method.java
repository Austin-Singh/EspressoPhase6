package Jasmin;

import java.util.*;
import AST.*;
import Instruction.Instruction;

public class Method {

	private Vector<Instruction> code;
	private ClassBodyDecl method;

	public void addInstruction(Instruction inst) {
		code.add(inst);
	}

	public Method(ClassBodyDecl md) {
		method = md;
		code = new Vector<Instruction>();	    
	}

	public Vector<Instruction> getCode() {
		return code;
	}	

	public ClassBodyDecl getMethod() {
		return method;
	}

}
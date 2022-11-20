package AST;
import java.util.*;
import Instruction.Instruction;

public abstract class ClassBodyDecl extends AST {

	private Vector<Instruction> code;  // Set in CodeGenerator.java

	public int localsUsed = 1;

	// Mapping between Variable addresses and their VarDecls
//		public Hashtable<Integer, VarDecl> varNames; 

	public ClassBodyDecl(AST a) {
		super(a);
		//	varNames = new Hashtable<Integer,VarDecl>();
	}

	public abstract boolean isStatic() ;

	public abstract String getname() ;

	public Vector<Instruction> getCode() {
		return code;
	}

	public void setCode(Vector<Instruction> code) {
		this.code = code;
	}

	public Vector<Instruction> getOptmizedCode() {
		//return cfg.getAllCode();
	return getCode();
	}
}

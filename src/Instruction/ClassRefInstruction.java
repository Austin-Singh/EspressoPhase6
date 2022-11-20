package Instruction;

/**
 * Used for the following instructions:
 * 
 * checkcast, instanceof, new
 */
public class ClassRefInstruction extends Instruction {
	private String className;

	public ClassRefInstruction(int opCode, String className) {
		super(opCode);
		this.className = className;
	}

	public String toString() {
		return super.toString() + " " + className;
	} 

	public String getClassName() {
		return className;
	}

}

package Instruction;

/**
 * Used for the following instructions:
 * 
 * newarray, anewarray, multinewarray
 */
public class ArrayInstruction extends Instruction {
	private String typeName;

	public ArrayInstruction(int opCode, String typeName) {
		super (opCode);
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String toString() {
		return super.toString() + " " + typeName;
	}
}
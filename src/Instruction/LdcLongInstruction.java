package Instruction;

/**
 * Used for large long values:
 * 
 *  ldc
 */

public class LdcLongInstruction extends LdcInstruction {
	private long value;
	public LdcLongInstruction(int opCode, long longOperand) {
		super(opCode);
		value = longOperand;
	}

	public long getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + " " + value;
	}

}
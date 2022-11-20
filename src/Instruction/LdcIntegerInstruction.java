package Instruction;

/**
 * Used for large integer values:
 * 
 * ldc
 */

public class LdcIntegerInstruction extends LdcInstruction {
	private int value;
	public LdcIntegerInstruction(int opCode, int intOperand) {
		super(opCode);
		value = intOperand;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + " " + value;
	}

}

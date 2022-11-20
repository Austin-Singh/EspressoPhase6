package Instruction;

/**
 * Used for loading strings:
 * 
 * ldc
 */
public class LdcStringInstruction extends LdcInstruction {
	private String value;
	public LdcStringInstruction(int opCode, String stringOperand) {
		super(opCode);
		value = stringOperand;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + " " + value;
	}
}








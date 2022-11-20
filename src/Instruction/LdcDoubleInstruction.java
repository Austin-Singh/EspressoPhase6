package Instruction;

/**
 * Used for loading double values
 * 
 * ldc2, ldc2_w
 * 
 */
public class LdcDoubleInstruction extends LdcInstruction {
	private double value;
	public LdcDoubleInstruction(int opCode, double doubleOperand) {
		super(opCode);
		value = doubleOperand;
	}

	public double getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + " " + value;
	}
}
package Instruction;

/**
 * Used for all loads and stores that require a parameter as well as a few pushes:
 * 
 * Xload Y
 * Xaload Y
 * Xstore Y
 * Xastore Y
 * bipush
 * sipush
 * 
 */
public class SimpleInstruction extends Instruction {
	private int operand;

	public SimpleInstruction(int opCode, int operand) {
		super(opCode);
		this.operand = operand;
	}

	public String toString() {
		return super.toString() + " " + operand;
	}

	public int getOperand() {
		return operand;
	}

}

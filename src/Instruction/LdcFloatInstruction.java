package Instruction;
/**
 * Used for loading float values:
 * 
 * ldc
 * 
 */
public class LdcFloatInstruction extends LdcInstruction {
	private float value;
	public LdcFloatInstruction(int opCode, float floatOperand) {
		super(opCode);
		value = floatOperand;
	}

	public float getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + " " + value + "F";
	}
}
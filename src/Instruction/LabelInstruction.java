package Instruction;

/*
 * Used for the following instructions:
 * 
 * Labels
 */
public class LabelInstruction extends Instruction {
	private String label;

	public LabelInstruction(int opCode, String label) {
		super (opCode);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return label + ":";
	}
}
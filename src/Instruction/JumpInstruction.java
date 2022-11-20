package Instruction;

/** 
 * Used for the following instructions:
 * 
 * goto, goto_w, 
 * if_acmpeq, if_acmpne, 
 * if_icmpeq, if_icmpge, if_icmpgt, if_icmple, if_icmplt, if_icmpne, 
 * ifeq, ifge, ifgt, ifle, iflt, ifne, 
 * ifnonnull, ifnull
 * 
 */
public class JumpInstruction extends Instruction {
	private String label;

	public JumpInstruction(int opCode, String label) {
		super(opCode);
		this.label = label;
	}

	public String toString() {
		return super.toString() + " " + label;
	}

	public String getLabel() {
		return label;
	}

}

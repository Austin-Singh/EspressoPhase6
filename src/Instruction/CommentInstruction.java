package Instruction;

/**
 * 
 * This class is not used in the currect compiler
 */

public class CommentInstruction extends Instruction {
	private String comment;

	public CommentInstruction(int opCode, String comment) {
		super (opCode);
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public String toString() {
		return  "; " + comment;
	}
}

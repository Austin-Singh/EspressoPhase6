package Instruction;

import Jasmin.RuntimeConstants;

/**
 * Used for the following instructions:
 *
 * Xload_Y
 * Xstore_Y
 * Xadd, Xsub, Xmul, Xdiv, Xrem, Xsh*
 * X2Y
 * Xconst_Y
 * Xreturn
 * arraylength
 * aconst_null
 * 
 */
public class Instruction {

	public static final int INTEGER = 1;
	public static final int FLOAT   = 2;
	public static final int LONG    = 3;
	public static final int DOUBLE  = 4;
	public static final int STRING  = 5;

	/*
	 * opcode takes its values from Jasmin/RuntimeConstants.opc_XXX
	 */
	private int opCode;

	public Instruction(int opCode) {
		this.opCode = opCode;
	}

	public int getOpCode() {
		return opCode;
	}

	public String getName() {
		if (opCode >= 0)
			return RuntimeConstants.opcNames[opCode];
		else if (opCode == RuntimeConstants.opc_label)
			return "L";
		else
			return "";
	}

	public String toString() {
		return getName();
	}

	// this won't be useful for all instructions, but it saves us a lot
	// of casting if it is available in this class rather than only in 
	// the subclasses.
	public int getOperand() {
		return 0;
	}
}









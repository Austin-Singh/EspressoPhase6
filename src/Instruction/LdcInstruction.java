package Instruction;

/**
 * Abstract class used as a super class for all Ldc* classes
 */
public abstract class LdcInstruction extends Instruction {
	LdcInstruction(int opCode) {
		super(opCode);
	}
}
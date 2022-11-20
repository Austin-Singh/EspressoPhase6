package Instruction;

/**
 * Used for the following instructions:
 * 
 * iinc
 */
public class IincInstruction extends Instruction {
	private int address;
	private int inc;
	
	public IincInstruction(int opCode, int address, int inc) {
		super(opCode);
		this.address = address;
		this.inc = inc;
	}

	public int getAddress() {
		return address;
	}

	public int getInc() {
		return inc;
	}
	
	public String toString() {
		return super.toString() + " " + address + " " + inc;
	}
}
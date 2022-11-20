package Instruction;

/**
 * Used for the following instructions:
 * 
 * getfield, getstatic, putfield, putstatic
 */
public class FieldRefInstruction extends Instruction {
	private String className;
	private String fieldName;
	private String signature;

	public FieldRefInstruction(int opCode, 
			String className,
			String fieldName, 
			String signature) {
		super(opCode);
		this.className = className;
		this.fieldName = fieldName;
		this.signature = signature;
	}

	public String toString() {
		return super.toString() + " " + className + "/" + fieldName + " " + signature;
	}

	public String getClassName() {
		return className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getSignature() {
		return signature;
	}
}

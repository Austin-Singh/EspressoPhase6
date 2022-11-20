package Instruction;

/**
 * Used for the following instructions:
 * 
 * invokeinterface
 */
public class InterfaceInvocationInstruction extends Instruction {
	private String className;
	private String methodName;
	private String signature;
	private int paramCount;

	public InterfaceInvocationInstruction(int opCode, 
			String className,
			String methodName, 
			String signature,
			int paramCount) {
		super(opCode);
		this.className = className;
		this.methodName = methodName;
		this.signature = signature;
		this.paramCount = paramCount;
	}   

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getSignature() {
		return signature;
	}

	public int getParamCount() {
		return paramCount;
	}

	public String toString() {
		return super.toString() + " " + className + "/" + methodName + signature + " " + paramCount;
	}

}
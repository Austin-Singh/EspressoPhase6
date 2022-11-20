package Instruction;


/**
 * Used for the following instructions:
 * 
 * invokenonvirtual, invokespecial, invokestatic, invokevirtual
 */
public class MethodInvocationInstruction extends Instruction {
	private String className;
	private String methodName;
	private String signature;

	public MethodInvocationInstruction(int opCode, String className,
			String methodName, String signature) {
		super(opCode);
		this.className = className;
		this.methodName = methodName;
		this.signature = signature;
	}   

	public String toString() {
		return super.toString() + " " + className + "/" + methodName + signature;
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
}

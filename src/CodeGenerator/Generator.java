package CodeGenerator;

import java.util.Vector;

import AST.*;
import Jasmin.*;
import Utilities.Error;
import Instruction.*;

public class Generator {
	private static String continueLabel; // Holds the current continue label
	private static String breakLabel;    // Holds the current break label
	private int nextlabel = 1;           // Label counter - you should never use it directly
	private int address = 1;             // Holds the next available address
	private boolean debug;
	private ClassFile classFile;	     // Reference to the classfile associated with this generator
        private int maxAddress = 0;          // the highest address used (well, the one after)

	// Set and get continue labels
	public static void setContinueLabel(String cl) {
		continueLabel = cl;
	}
	public static String getContinueLabel() {
		return continueLabel;
	}
	
	// set and get break labels
	public static void setBreakLabel(String bl) {
		breakLabel = bl;
	}
	public static String getBreakLabel() {
		return breakLabel;
	}
	
	// get addresses, increment them by one or two or set them
	public int getAddress() { 
		return address;
	}
	public void incAddress() {
		address++;
		if (address > maxAddress) {
		    maxAddress = address;
		}
		//System.out.println("Max Address: " + maxAddress);
	}
	public void inc2Address() {
		address += 2;
		if (address > maxAddress) {
		    maxAddress = address;
		}
		//System.out.println("Max Address: " + maxAddress);
	}
	public void setAddress(int address) {
		this.address = address;
		//System.out.println("Setting address to " + address);
		if (address > maxAddress)
		    this.maxAddress = address;
	}

        public int getLocalsUsed() {
	    //System.out.println("MAX ADDRESS USED: " + maxAddress);
                return maxAddress;
        }
    
    public void resetAddress() {
	maxAddress = 0;
    }


	// get the class file associated with this generator
	public ClassFile getClassFile() {
		return classFile;
	}

	Generator(ClassDecl cd, boolean d) {
		System.out.println("Generating code for: " + cd.name().toString());
		debug = d;

		classFile = new ClassFile(cd);
	}

    // endMethod: call this method to end 
	public void endMethod(MethodDecl md) {
		Type t = md.returnType();

	    // if the last instruction is a return there in no need to add another!
		Vector<Instruction> insts = classFile.getCurrentMethodCode();
		int index = insts.size()-1;
		// fast backwards past comments
		while (index > 0 && insts.elementAt(index).getOpCode() == -4)
			index--;
		// index points to the last real instruction
		Instruction inst = insts.elementAt(index);
		if (inst.getOpCode() >= RuntimeConstants.opc_ireturn && inst.getOpCode() <= RuntimeConstants.opc_return) {
			md.setCode(classFile.getCurrentMethodCode());
			classFile.endMethod();
			return;	
		}
			
		if (!md.getModifiers().isAbstract() && !(RuntimeConstants.returnSet.contains(classFile.getLastInstruction().getOpCode()))) {			
			if (t == null || t.isVoidType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
			}
			else if (t.isIntegerType() || t.isShortType() || t.isByteType()
					|| t.isCharType() || t.isBooleanType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_0));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_ireturn));
			} else if (t.isClassType() || t.isStringType() || t.isArrayType()) {
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_aconst_null));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_areturn));
			} else if (t.isLongType()) {
				classFile.addInstruction(new LdcLongInstruction(RuntimeConstants.opc_ldc2_w, 0L));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_lreturn));
			} else if (t.isDoubleType()) {
				classFile.addInstruction(new LdcDoubleInstruction(RuntimeConstants.opc_ldc2_w, 0.0));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_dreturn));				
			} else if (t.isFloatType()) {
				classFile.addInstruction(new LdcFloatInstruction(RuntimeConstants.opc_ldc, 0F));
				classFile.addInstruction(new Instruction(RuntimeConstants.opc_freturn));
			}
		}
		md.setCode(classFile.getCurrentMethodCode());
		classFile.endMethod();
	}

	// dataConvert: Convert from one type to another
	public void dataConvert(Type from, Type to) {

		if (!(to instanceof PrimitiveType))
			return;
		PrimitiveType f = (PrimitiveType) from;
		PrimitiveType t = (PrimitiveType) to;
		
		if (f.getTypePrefix().equals(t.getTypePrefix()))
			return;

		String instString = f.getTypePrefix() + "2" + t.getTypePrefix();

		classFile.addInstruction(new Instruction(Generator.getOpCodeFromString(instString)));
	}

	// loadInt: loads an integer value on to the stack
	public void loadInt(String s) {
		int base = 10;
		if (s.startsWith("0x") || s.startsWith("0X")) {
			base = 16;
			s = s.substring(2);
		} else if (s.startsWith("0"))
			base = 8;
		int x = Integer.parseInt(s, base);
		switch (x) {
		case -1:
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			String instString = "iconst_" + (x == -1 ? "m1" : ""+x);
			classFile.addInstruction(new Instruction(Generator.getOpCodeFromString(instString)));
			break;
		default:
			if (-128 <= x && x <= 127) 
				classFile.addInstruction(new SimpleInstruction(RuntimeConstants.opc_bipush, x));
			else if (-32768 <= x && x <= 32767) 
				classFile.addInstruction(new SimpleInstruction(RuntimeConstants.opc_sipush, x));
			else 
				classFile.addInstruction(new LdcIntegerInstruction(RuntimeConstants.opc_ldc, x));
			break;
		}
	}

	// loadLong: loads a long value on to the stack
	public void loadLong(String s) {
		int base = 10;
		if (s.startsWith("0x") || s.startsWith("0X")) {
			base = 16;
			s = s.substring(2);
		} else if (s.startsWith("0"))
			base = 8;
		long x = Long.parseLong(s, base);
		if (x == 0)
		    classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_0));
		else if (x == 1)
		    classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
		else
		    classFile.addInstruction(new LdcLongInstruction(RuntimeConstants.opc_ldc2_w, x));
	}

	// loadFloat: loads a float value on to the stack
	public void loadFloat(String s) {
		float x = Float.parseFloat(s);
		classFile.addInstruction(new LdcFloatInstruction(RuntimeConstants.opc_ldc, x));
	}

	// loadDouble: loads a double value on to the stack
	public void loadDouble(String s) {
		double x = Double.parseDouble(s);
		classFile.addInstruction(new LdcDoubleInstruction(RuntimeConstants.opc_ldc2_w, x));
	}

	// loadString: loads a string value on to the stack
	public void loadString(String s) {
		classFile.addInstruction(new LdcStringInstruction(RuntimeConstants.opc_ldc, s));
	}

	// getLabel: get a new label
	public String getLabel() {
		return Integer.toString(nextlabel++);
	}

	// getStoreInstruction: returns the appropriate store instruction based on type and address and whether
	//                      the store is into an array
	public static int getStoreInstruction(Type type, int address, boolean storeArrayRef) {
		// if the address is less than 4 we can generate Xload_Y (y=address) but we 
		// don't want to do that for array types unless storeArrayRef is 'true'
		if (address < 4 && (!type.isArrayType() || (type.isArrayType() && storeArrayRef))) {
			String instString = type.getTypePrefix() + "store_" + address;
			return Generator.getOpCodeFromString(instString);			
		} 
		// Ok, we have an array type
		if (type.isArrayType()) {
			ArrayType at = (ArrayType)type;
			if (storeArrayRef)
				// We are storing a into an array location so we need to generate a Xastore
				return Generator.getOpCodeFromString(at.baseType().getTypePrefix() + "astore");			
			else
				// We are not storing into an array location, so we just need to store an array reference
				// which is simply a reference, i.e., astore Y or astore_Y
				if (address < 4)
					return Generator.getOpCodeFromString("astore_"+address);
				else
					return RuntimeConstants.opc_astore;	
		}
		String instString = type.getTypePrefix() + "store";
		return Generator.getOpCodeFromString(instString);			
	}

	// getLoadInstruction: returns the appropriate load instruction based on type and address and whether
	//                     the load is into an array
	public static int getLoadInstruction(Type type, int address, boolean loadArrayRef) {
		if (address < 4 && (!type.isArrayType() || (type.isArrayType() && loadArrayRef))) {
			String instString = type.getTypePrefix() + "load_" + address;
			return Generator.getOpCodeFromString(instString);			
		} 

		if (type.isArrayType()) {
			ArrayType at = (ArrayType)type;
			if (loadArrayRef)			
				return Generator.getOpCodeFromString(at.baseType().getTypePrefix() + "aload");
			else
				if (address < 4)
					return Generator.getOpCodeFromString("aload_"+address);
				else	
					return RuntimeConstants.opc_aload;
		}
		String instString = type.getTypePrefix() + "load";
		return Generator.getOpCodeFromString(instString);			
	}

	// getArrayStoreInstruction: returns the appropriate array store instruction
	public static int getArrayStoreInstruction(Type type) {
	    String prefix; // here bytes, booleans, chars and shorts are NOT 'i'
	    if (type.isBooleanType() || type.isByteType())
		prefix = "b";
	    else if (type.isShortType())
		prefix = "s";
	    else if (type.isCharType())
		prefix = "c";
	    else
		prefix = type.getTypePrefix();
		return Generator.getOpCodeFromString(prefix + "astore");
	}

	// getArrayLoadInstruction: returns the appropriate array load instruction
	public static int getArrayLoadInstruction(Type type) {
	    String prefix; // here bytes, booleans, chars and shorts are NOT 'i'
	    if (type.isBooleanType() || type.isByteType())
		prefix = "b";
	    else if (type.isShortType())
		prefix = "s";
	    else if (type.isCharType())
		prefix = "c";
	    else
		prefix = type.getTypePrefix();

		return Generator.getOpCodeFromString(prefix + "aload");
	}

	// getBinaryAssignmentOpInstruction: return the appropriate binary operator instruction based on operator and type
	//                         only for <op>= assignments it seems !!
	public static int getBinaryAssignmentOpInstruction(AssignmentOp op, Type pt) {
		if (pt.isIntegerType() || pt.isShortType() || pt.isByteType() || pt.isCharType() || pt.isBooleanType()) {
			switch (op.kind) {
			case AssignmentOp.PLUSEQ:  return RuntimeConstants.opc_iadd;
			case AssignmentOp.MINUSEQ: return RuntimeConstants.opc_isub;
			case AssignmentOp.MULTEQ:  return RuntimeConstants.opc_imul;
			case AssignmentOp.DIVEQ:   return RuntimeConstants.opc_idiv;
			case AssignmentOp.MODEQ:   return RuntimeConstants.opc_irem;
			case AssignmentOp.ANDEQ:   return RuntimeConstants.opc_iand;
			case AssignmentOp.OREQ:    return RuntimeConstants.opc_ior;
			case AssignmentOp.XOREQ:   return RuntimeConstants.opc_ixor;
			case AssignmentOp.LSHIFTEQ: return RuntimeConstants.opc_ishl;
			case AssignmentOp.RSHIFTEQ: return RuntimeConstants.opc_ishr;
			case AssignmentOp.RRSHIFTEQ: return RuntimeConstants.opc_iushr;
			}
		} else if (pt.isLongType()) {
			switch (op.kind) {
			case AssignmentOp.PLUSEQ:  return RuntimeConstants.opc_ladd;
			case AssignmentOp.MINUSEQ: return RuntimeConstants.opc_lsub;
			case AssignmentOp.MULTEQ:  return RuntimeConstants.opc_lmul;
			case AssignmentOp.DIVEQ:   return RuntimeConstants.opc_ldiv;
			case AssignmentOp.MODEQ:   return RuntimeConstants.opc_lrem;
			case AssignmentOp.ANDEQ:   return RuntimeConstants.opc_land;
			case AssignmentOp.OREQ:    return RuntimeConstants.opc_lor;
			case AssignmentOp.XOREQ:   return RuntimeConstants.opc_lxor;
			case AssignmentOp.LSHIFTEQ: return RuntimeConstants.opc_lshl;
			case AssignmentOp.RSHIFTEQ: return RuntimeConstants.opc_lshr;
			case AssignmentOp.RRSHIFTEQ: return RuntimeConstants.opc_lushr;
			}
		} else if (pt.isFloatType()) {
			switch (op.kind) {
			case AssignmentOp.PLUSEQ:  return RuntimeConstants.opc_fadd;
			case AssignmentOp.MINUSEQ: return RuntimeConstants.opc_fsub;
			case AssignmentOp.MULTEQ:  return RuntimeConstants.opc_fmul;
			case AssignmentOp.DIVEQ:   return RuntimeConstants.opc_fdiv;
			case AssignmentOp.MODEQ:   return RuntimeConstants.opc_frem;
			}
		} else if (pt.isDoubleType()) {
			switch (op.kind) {
			case AssignmentOp.PLUSEQ:  return RuntimeConstants.opc_dadd;
			case AssignmentOp.MINUSEQ: return RuntimeConstants.opc_dsub;
			case AssignmentOp.MULTEQ:  return RuntimeConstants.opc_dmul;
			case AssignmentOp.DIVEQ:   return RuntimeConstants.opc_ddiv;
			case AssignmentOp.MODEQ:   return RuntimeConstants.opc_drem;
			}
		} else
			System.out.println("Generator: oops - I don't know what that type is! " + op + " " + op.line + " " + pt.getTypePrefix());
		return 0;	
	}


	/** Issues an instruction described by singleDup or doubleDup depending
	 * on the type of 'type'. Typically called with dup and dup2, dup_x1 and dup2_x1
	 * or dup_x2 and dup2_x2
	 * @param type The type of the element on the stack to be duplicated
	 * @param singleDup (dup, dup_x1, dup_x2)
	 * @param doubleDup (dup2, dup_x2, dup2_x2)
	 */
	public void dup(Type type, int singleDup, int doubleDup) {
		if (type.isLongType() || type.isDoubleType()) 
			classFile.addInstruction(new Instruction(doubleDup));
		else 
			classFile.addInstruction(new Instruction(singleDup));
	}

	// loadConstant1: loads the constant 1 based on type
	public static int loadConstant1(Type type) {
		return Generator.getOpCodeFromString(type.getTypePrefix() + "const_1");
	}

	// addOrSub: generates a *add or *sub based on the type
	public static int addOrSub(Type type, boolean add) {
		if (add) 
			return Generator.getOpCodeFromString(type.getTypePrefix() + "add");
		return Generator.getOpCodeFromString(type.getTypePrefix() + "sub");		
	}

	// getOpcodeFromString: returns the RuntimeConstants.opc_xxxxx opcode associated with the string 's'
	public static int getOpCodeFromString(String s) {
		Object o = RuntimeConstants.instructions.get(s);
		if (o == null)
			Error.error("Generator.getOpcodeFromString(): '" + s + "': no such bytecode instruction.");
		return (Integer)o;
	}

}

package Jasmin;

import java.util.*;

public class RuntimeConstants {

	public static final int ACC_PUBLIC       = 1;
	public static final int ACC_PRIVATE      = 2;
	public static final int ACC_PROTECTED    = 4;
	public static final int ACC_STATIC       = 8;
	public static final int ACC_FINAL        = 16;
	public static final int ACC_SYNCHRONIZED = 32;
	public static final int ACC_VOLATILE     = 64;
	public static final int ACC_TRANSIENT    = 128;
	public static final int ACC_NATIVE       = 256;
	public static final int ACC_INTERFACE    = 512;
	public static final int ACC_ABSTRACT     = 1024;
	public static final int ACC_SUPER        = 32;


	// Opcodes
	public static final int opc_comment         = -4;
	public static final int opc_try             = -3;
	public static final int opc_dead            = -2;
	public static final int opc_label           = -1;
	public static final int opc_nop             = 0;
	public static final int opc_aconst_null     = 1;
	public static final int opc_iconst_m1       = 2;
	public static final int opc_iconst_0        = 3;
	public static final int opc_iconst_1        = 4;
	public static final int opc_iconst_2        = 5;
	public static final int opc_iconst_3        = 6;
	public static final int opc_iconst_4        = 7;
	public static final int opc_iconst_5        = 8;
	public static final int opc_lconst_0        = 9;
	public static final int opc_lconst_1        = 10;
	public static final int opc_fconst_0        = 11;
	public static final int opc_fconst_1        = 12;
	public static final int opc_fconst_2        = 13;
	public static final int opc_dconst_0        = 14;
	public static final int opc_dconst_1        = 15;
	public static final int opc_bipush          = 16;
	public static final int opc_sipush          = 17;
	public static final int opc_ldc             = 18;
	public static final int opc_ldc_w           = 19;
	public static final int opc_ldc2_w          = 20;
	public static final int opc_iload           = 21;
	public static final int opc_lload           = 22;
	public static final int opc_fload           = 23;
	public static final int opc_dload           = 24;
	public static final int opc_aload           = 25;
	public static final int opc_iload_0         = 26;
	public static final int opc_iload_1         = 27;
	public static final int opc_iload_2         = 28;
	public static final int opc_iload_3         = 29;
	public static final int opc_lload_0         = 30;
	public static final int opc_lload_1         = 31;
	public static final int opc_lload_2         = 32;
	public static final int opc_lload_3         = 33;
	public static final int opc_fload_0         = 34;
	public static final int opc_fload_1         = 35;
	public static final int opc_fload_2         = 36;
	public static final int opc_fload_3         = 37;
	public static final int opc_dload_0         = 38;
	public static final int opc_dload_1         = 39;
	public static final int opc_dload_2         = 40;
	public static final int opc_dload_3         = 41;
	public static final int opc_aload_0         = 42;
	public static final int opc_aload_1         = 43;
	public static final int opc_aload_2         = 44;
	public static final int opc_aload_3         = 45;
	public static final int opc_iaload          = 46;
	public static final int opc_laload          = 47;
	public static final int opc_faload          = 48;
	public static final int opc_daload          = 49;
	public static final int opc_aaload          = 50;
	public static final int opc_baload          = 51;
	public static final int opc_caload          = 52;
	public static final int opc_saload          = 53;
	public static final int opc_istore          = 54;
	public static final int opc_lstore          = 55;
	public static final int opc_fstore          = 56;
	public static final int opc_dstore          = 57;
	public static final int opc_astore          = 58;
	public static final int opc_istore_0        = 59;
	public static final int opc_istore_1        = 60;
	public static final int opc_istore_2        = 61;
	public static final int opc_istore_3        = 62;
	public static final int opc_lstore_0        = 63;
	public static final int opc_lstore_1        = 64;
	public static final int opc_lstore_2        = 65;
	public static final int opc_lstore_3        = 66;
	public static final int opc_fstore_0        = 67;
	public static final int opc_fstore_1        = 68;
	public static final int opc_fstore_2        = 69;
	public static final int opc_fstore_3        = 70;
	public static final int opc_dstore_0        = 71;
	public static final int opc_dstore_1        = 72;
	public static final int opc_dstore_2        = 73;
	public static final int opc_dstore_3        = 74;
	public static final int opc_astore_0        = 75;
	public static final int opc_astore_1        = 76;
	public static final int opc_astore_2        = 77;
	public static final int opc_astore_3        = 78;
	public static final int opc_iastore         = 79;
	public static final int opc_lastore         = 80;
	public static final int opc_fastore         = 81;
	public static final int opc_dastore         = 82;
	public static final int opc_aastore         = 83;
	public static final int opc_bastore         = 84;
	public static final int opc_castore         = 85;
	public static final int opc_sastore         = 86;
	public static final int opc_pop             = 87;
	public static final int opc_pop2            = 88;
	public static final int opc_dup             = 89;
	public static final int opc_dup_x1          = 90;
	public static final int opc_dup_x2          = 91;
	public static final int opc_dup2            = 92;
	public static final int opc_dup2_x1         = 93;
	public static final int opc_dup2_x2         = 94;
	public static final int opc_swap            = 95;
	public static final int opc_iadd            = 96;
	public static final int opc_ladd            = 97;
	public static final int opc_fadd            = 98;
	public static final int opc_dadd            = 99;
	public static final int opc_isub            = 100;
	public static final int opc_lsub            = 101;
	public static final int opc_fsub            = 102;
	public static final int opc_dsub            = 103;
	public static final int opc_imul            = 104;
	public static final int opc_lmul            = 105;
	public static final int opc_fmul            = 106;
	public static final int opc_dmul            = 107;
	public static final int opc_idiv            = 108;
	public static final int opc_ldiv            = 109;
	public static final int opc_fdiv            = 110;
	public static final int opc_ddiv            = 111;
	public static final int opc_irem            = 112;
	public static final int opc_lrem            = 113;
	public static final int opc_frem            = 114;
	public static final int opc_drem            = 115;
	public static final int opc_ineg            = 116;
	public static final int opc_lneg            = 117;
	public static final int opc_fneg            = 118;
	public static final int opc_dneg            = 119;
	public static final int opc_ishl            = 120;
	public static final int opc_lshl            = 121;
	public static final int opc_ishr            = 122;
	public static final int opc_lshr            = 123;
	public static final int opc_iushr           = 124;
	public static final int opc_lushr           = 125;
	public static final int opc_iand            = 126;
	public static final int opc_land            = 127;
	public static final int opc_ior             = 128;
	public static final int opc_lor             = 129;
	public static final int opc_ixor            = 130;
	public static final int opc_lxor            = 131;
	public static final int opc_iinc            = 132;
	public static final int opc_i2l             = 133;
	public static final int opc_i2f             = 134;
	public static final int opc_i2d             = 135;
	public static final int opc_l2i             = 136;
	public static final int opc_l2f             = 137;
	public static final int opc_l2d             = 138;
	public static final int opc_f2i             = 139;
	public static final int opc_f2l             = 140;
	public static final int opc_f2d             = 141;
	public static final int opc_d2i             = 142;
	public static final int opc_d2l             = 143;
	public static final int opc_d2f             = 144;
	public static final int opc_i2b             = 145;
	public static final int opc_i2c             = 146;
	public static final int opc_i2s             = 147;
	public static final int opc_lcmp            = 148;
	public static final int opc_fcmpl           = 149;
	public static final int opc_fcmpg           = 150;
	public static final int opc_dcmpl           = 151;
	public static final int opc_dcmpg           = 152;
	public static final int opc_ifeq            = 153;
	public static final int opc_ifne            = 154;
	public static final int opc_iflt            = 155;
	public static final int opc_ifge            = 156;
	public static final int opc_ifgt            = 157;
	public static final int opc_ifle            = 158;
	public static final int opc_if_icmpeq       = 159;
	public static final int opc_if_icmpne       = 160;
	public static final int opc_if_icmplt       = 161;
	public static final int opc_if_icmpge       = 162;
	public static final int opc_if_icmpgt       = 163;
	public static final int opc_if_icmple       = 164;
	public static final int opc_if_acmpeq       = 165;
	public static final int opc_if_acmpne       = 166;
	public static final int opc_goto            = 167;
	public static final int opc_jsr             = 168;
	public static final int opc_ret             = 169;
	public static final int opc_tableswitch     = 170;
	public static final int opc_lookupswitch    = 171;
	public static final int opc_ireturn         = 172;
	public static final int opc_lreturn         = 173;
	public static final int opc_freturn         = 174;
	public static final int opc_dreturn         = 175;
	public static final int opc_areturn         = 176;
	public static final int opc_return          = 177;
	public static final int opc_getstatic       = 178;
	public static final int opc_putstatic       = 179;
	public static final int opc_getfield        = 180;
	public static final int opc_putfield        = 181;
	public static final int opc_invokevirtual   = 182;
	public static final int opc_invokespecial   = 183;
	public static final int opc_invokestatic    = 184;
	public static final int opc_invokeinterface = 185;
	public static final int opc_xxxunusedxxx    = 186;
	public static final int opc_new             = 187;
	public static final int opc_newarray        = 188;
	public static final int opc_anewarray       = 189;
	public static final int opc_arraylength     = 190;
	public static final int opc_athrow          = 191;
	public static final int opc_checkcast       = 192;
	public static final int opc_instanceof      = 193;
	public static final int opc_monitorenter    = 194;
	public static final int opc_monitorexit     = 195;
	public static final int opc_wide            = 196;
	public static final int opc_multianewarray  = 197;
	public static final int opc_ifnull          = 198;
	public static final int opc_ifnonnull       = 199;
	public static final int opc_goto_w          = 200;
	public static final int opc_jsr_w           = 201;
	public static final int opc_breakpoint      = 202;
        public static final int opc_invokenonvirtual = 203;

	// for backwards compatibility with 1.0
	public static final int opc_int2byte        = opc_i2b;
	public static final int opc_int2char        = opc_i2c;
	public static final int opc_int2short       = opc_i2s;




	public static final String opcNames[] = {
		"nop", "aconst_null", "iconst_m1", "iconst_0",
		"iconst_1", "iconst_2", "iconst_3", "iconst_4",
		"iconst_5", "lconst_0", "lconst_1", "fconst_0",
		"fconst_1", "fconst_2", "dconst_0", "dconst_1",
		"bipush", "sipush", "ldc", "ldc_w", "ldc2_w",
		"iload", "lload", "fload", "dload", "aload",
		"iload_0", "iload_1", "iload_2", "iload_3",
		"lload_0", "lload_1", "lload_2", "lload_3",
		"fload_0", "fload_1", "fload_2", "fload_3",
		"dload_0", "dload_1", "dload_2", "dload_3",
		"aload_0", "aload_1", "aload_2", "aload_3",
		"iaload", "laload", "faload", "daload", "aaload",
		"baload", "caload", "saload", "istore", "lstore",
		"fstore", "dstore", "astore", "istore_0",
		"istore_1", "istore_2", "istore_3", "lstore_0",
		"lstore_1", "lstore_2", "lstore_3", "fstore_0",
		"fstore_1", "fstore_2", "fstore_3", "dstore_0",
		"dstore_1", "dstore_2", "dstore_3", "astore_0",
		"astore_1", "astore_2", "astore_3", "iastore",
		"lastore", "fastore", "dastore", "aastore",
		"bastore", "castore", "sastore", "pop", "pop2",
		"dup", "dup_x1", "dup_x2", "dup2", "dup2_x1",
		"dup2_x2", "swap", "iadd", "ladd", "fadd",
		"dadd", "isub", "lsub", "fsub", "dsub",
		"imul", "lmul", "fmul", "dmul", "idiv",
		"ldiv", "fdiv", "ddiv", "irem", "lrem",
		"frem", "drem", "ineg", "lneg", "fneg",
		"dneg", "ishl", "lshl", "ishr", "lshr",
		"iushr", "lushr", "iand", "land", "ior",
		"lor", "ixor", "lxor", "iinc", "i2l", "i2f",
		"i2d", "l2i", "l2f", "l2d", "f2i", "f2l",
		"f2d", "d2i", "d2l", "d2f", "i2b", "i2c",
		"i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl",
		"dcmpg", "ifeq", "ifne", "iflt", "ifge",
		"ifgt", "ifle", "if_icmpeq", "if_icmpne",
		"if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple",
		"if_acmpeq", "if_acmpne", "goto", "jsr", "ret",
		"tableswitch", "lookupswitch", "ireturn", "lreturn",
		"freturn", "dreturn", "areturn", "return",
		"getstatic", "putstatic", "getfield", "putfield",
		"invokevirtual", "invokespecial", "invokestatic",
		"invokeinterface", "xxxunusedxxx", "new", "newarray",
		"anewarray", "arraylength", "athrow", "checkcast",
		"instanceof", "monitorenter", "monitorexit", "wide",
		"multianewarray", "ifnull", "ifnonnull", "goto_w",
		"jsr_w", "breakpoint", "invokenonvirtual"
	};

	static int opcLengths[] = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2,
		2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3,
		3, 3, 3, 3, 3, 3, 3, 3, 2, 99, 99, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3,
		3, 5, 0, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5, 1, 3
	};


	static Integer jumps[] = {
		opc_ifeq, opc_ifne, opc_iflt, opc_ifge, opc_ifgt, opc_ifle, opc_if_icmpeq,
		opc_if_icmpne, opc_if_icmplt, opc_if_icmpge, opc_if_icmpgt, opc_if_icmple,
		opc_if_acmpeq, opc_if_acmpne, opc_goto, opc_jsr, opc_ret, opc_ireturn,
		opc_lreturn, opc_freturn, opc_dreturn, opc_areturn, opc_return };
	static HashSet<Integer> jumpSet = new HashSet<Integer>(Arrays.asList(jumps));


	static Integer loads[] = {opc_lload, opc_fload, opc_dload, opc_aload, opc_iload, 
		opc_iload_0, opc_iload_1, opc_iload_2, opc_iload_3, 
		opc_lload_0, opc_lload_1, opc_lload_2, opc_lload_3, 
		opc_fload_0, opc_fload_1, opc_fload_2, opc_fload_3, 
		opc_dload_0, opc_dload_1, opc_dload_2, opc_dload_3, 
		opc_aload_0, opc_aload_1, opc_aload_2, opc_aload_3, 
		opc_iaload, opc_laload, opc_faload, opc_daload, 
		opc_aaload, opc_baload, opc_caload, opc_saload } ;
	public static HashSet<Integer> loadSet;

	static Integer stores[] = {opc_lstore, opc_fstore, opc_dstore, opc_astore, opc_istore,
		opc_istore_0, opc_istore_1, opc_istore_2, opc_istore_3, 
		opc_lstore_0, opc_lstore_1, opc_lstore_2, opc_lstore_3, 
		opc_fstore_0, opc_fstore_1, opc_fstore_2, opc_fstore_3, 
		opc_dstore_0, opc_dstore_1, opc_dstore_2, opc_dstore_3, 
		opc_astore_0, opc_astore_1, opc_astore_2, opc_astore_3, 
		opc_iastore, opc_lastore, opc_fastore, opc_dastore, 
		opc_aastore, opc_bastore, opc_castore, opc_sastore } ;
	public static HashSet<Integer> storeSet;

	private static Integer returns[] = { opc_return, opc_areturn, opc_ireturn,
		opc_freturn, opc_lreturn, opc_dreturn };
	public static HashSet<Integer> returnSet;

	private static Integer uncondJumps[] = { opc_goto, opc_goto_w, opc_jsr, opc_jsr_w} ;
	public static HashSet<Integer> uncondJumpSet;

	public static Hashtable<String,Integer>instructions = new Hashtable<String,Integer>();
	
	static {
		try {
			storeSet      = new HashSet<Integer>(Arrays.asList(stores));
			loadSet       = new HashSet<Integer>(Arrays.asList(loads));
			returnSet     = new HashSet<Integer>(Arrays.asList(returns));
			uncondJumpSet = new HashSet<Integer>(Arrays.asList(uncondJumps));
		} catch (Exception e) {
		}
		
		for (int i=0; i<opcNames.length;i++)
			instructions.put(opcNames[i],i);
	}
	
	public static boolean isPublic(int access) {
		return ((access & ACC_PUBLIC) != 0);
	}

	public static boolean isPrivate(int access) {
		return ((access & ACC_PRIVATE) != 0);
	}

	public static boolean isProtected(int access) {
		return ((access & ACC_PROTECTED) != 0);
	}

	public static boolean isStatic(int access) {
		return ((access & ACC_STATIC) != 0);
	}

	public static boolean isFinal(int access) {
		return ((access & ACC_STATIC) != 0);
	}

	public static boolean isSynchronized(int access) {
		return ((access & ACC_SYNCHRONIZED) != 0);
	}

	public static boolean isVolatile(int access) {
		return ((access & ACC_VOLATILE) != 0);
	}

	public static boolean isTransient(int access) {
		return ((access & ACC_TRANSIENT) != 0);
	}

	public static boolean isNative(int access) {
		return ((access & ACC_NATIVE) != 0);
	}

	public static boolean isInterface(int access) {
		return ((access & ACC_INTERFACE) != 0);
	}

	public static boolean isAbstract(int access) {
		return ((access & ACC_ABSTRACT) != 0);
	}
}




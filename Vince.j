.class public Vince
.super java/lang/Object

.field public localParamInt I

.field public localParamBool Z

.method public <init>()V
	.limit stack 50
	.limit locals 1
;  (6) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (6) Field Init Generation Start
;  (6) Field Init Generation End
	return
.end method

.method public <init>(Z)V
	.limit stack 50
	.limit locals 2
;  (10) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (10) Field Init Generation Start
;  (10) Field Init Generation End
;  (11) Expression Statement
;  (11) Assignment
;  (11) This
	aload_0
;  (11) End This
;  (11) Name Expression --
	iload_1
;  (11) End NameExpr
	putfield Vince/localParamBool Z
;  (11) End Assignment
;  (11) End ExprStat
	return
.end method

.method public <init>(I)V
	.limit stack 50
	.limit locals 2
;  (14) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (14) Field Init Generation Start
;  (14) Field Init Generation End
;  (15) Expression Statement
;  (15) Assignment
;  (15) This
	aload_0
;  (15) End This
;  (15) Name Expression --
	iload_1
;  (15) End NameExpr
	putfield Vince/localParamInt I
;  (15) End Assignment
;  (15) End ExprStat
	return
.end method

.method public visitReturn(Z)Z
	.limit stack 50
	.limit locals 2
;  (19) Method Declaration (visitReturn)
;  (20) Return Statement
;  (20) Field Reference
;  (20) This
	aload_0
;  (20) End This
	getfield Vince/localParamBool Z
;  (20) End FieldRef
	ireturn
;  (20) End ReturnStat
.end method

.method public visitNew()LVince;
	.limit stack 50
	.limit locals 4
;  (23) Method Declaration (visitNew)
;  (25) Local Variable Declaration
;  (25) New
	new Vince
	dup
	invokespecial Vince/<init>()V
;  (25) End New
	astore_2
;  (25) End LocalDecl
;  (26) Local Variable Declaration
;  (26) New
	new Vince
	dup
;  (26) Literal
	sipush 420
;  (26) End Literal
	invokespecial Vince/<init>(I)V
;  (26) End New
	astore_3
;  (26) End LocalDecl
;  (27) Return Statement
;  (27) Name Expression --
	aload_1
;  (27) End NameExpr
	areturn
;  (27) End ReturnStat
.end method

.method public testMultipleLocalParams()V
	.limit stack 50
	.limit locals 6
;  (30) Method Declaration (testMultipleLocalParams)
;  (31) Local Variable Declaration
;  (31) Literal
	iconst_0
;  (31) End Literal
	istore_1
;  (31) End LocalDecl
;  (32) Local Variable Declaration
;  (32) Literal
	iconst_0
;  (32) End Literal
	istore_2
;  (32) End LocalDecl
;  (33) Local Variable Declaration
;  (33) Literal
	iconst_0
;  (33) End Literal
	istore_3
;  (33) End LocalDecl
;  (34) Local Variable Declaration
;  (34) Literal
	iconst_0
;  (34) End Literal
	istore
;  (34) End LocalDecl
;  (35) Local Variable Declaration
;  (35) Literal
	iconst_0
;  (35) End Literal
	istore
;  (35) End LocalDecl
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 2
;  (38) Method Declaration (main)
;  (40) Local Variable Declaration
;  (40) Literal
	iconst_1
;  (40) End Literal
	istore_1
;  (40) End LocalDecl
;  (42) If Statement
;  (42) Name Expression --
	iload_1
;  (42) End NameExpr
	ifeq L1
;  (43) If Statement
;  (43) Name Expression --
	iload_1
;  (43) End NameExpr
	ifeq L3
;  (44) If Statement
;  (44) Name Expression --
	iload_1
;  (44) End NameExpr
	ifeq L4
L4:
;  (44) End IfStat
L3:
;  (43) End IfStat
	goto L2
L1:
;  (49) If Statement
;  (49) Name Expression --
	iload_1
;  (49) End NameExpr
	ifeq L5
;  (50) If Statement
;  (50) Name Expression --
	iload_1
;  (50) End NameExpr
	ifeq L6
L6:
;  (50) End IfStat
L5:
;  (49) End IfStat
L2:
;  (42) End IfStat
;  (56) While Statement
L7:
;  (56) Name Expression --
	iload_1
;  (56) End NameExpr
	ifeq L8
;  (57) Break Statement
	goto L8
;  (57) End BreakStat
	goto L7
L8:
;  (56) End WhileStat
	return
.end method


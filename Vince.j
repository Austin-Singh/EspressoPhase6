.class public Vince
.super java/lang/Object

.field public localParam I

.method public <init>()V
	.limit stack 50
	.limit locals 1
;  (5) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (5) Field Init Generation Start
;  (5) Field Init Generation End
	return
.end method

.method public <init>(I)V
	.limit stack 50
	.limit locals 2
;  (9) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (9) Field Init Generation Start
;  (9) Field Init Generation End
;  (10) Expression Statement
;  (10) Assignment
;  (10) This
	aload_0
;  (10) End This
;  (10) Name Expression --
	iload_1
;  (10) End NameExpr
	putfield Vince/localParam I
;  (10) End Assignment
;  (10) End ExprStat
	return
.end method

.method public visitReturn(I)I
	.limit stack 50
	.limit locals 2
;  (13) Method Declaration (visitReturn)
;  (14) Return Statement
;  (14) Name Expression --
	iload_1
;  (14) End NameExpr
	ireturn
;  (14) End ReturnStat
.end method

.method public visitNew()LVince;
	.limit stack 50
	.limit locals 4
;  (17) Method Declaration (visitNew)
;  (19) Local Variable Declaration
;  (19) New
	new Vince
	dup
	invokespecial Vince/<init>()V
;  (19) End New
	astore_2
;  (19) End LocalDecl
;  (20) Local Variable Declaration
;  (20) New
	new Vince
	dup
;  (20) Literal
	sipush 420
;  (20) End Literal
	invokespecial Vince/<init>(I)V
;  (20) End New
	astore_3
;  (20) End LocalDecl
;  (23) Return Statement
;  (23) Name Expression --
	aload_1
;  (23) End NameExpr
	areturn
;  (23) End ReturnStat
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 3
;  (26) Method Declaration (main)
;  (28) Local Variable Declaration
;  (28) Literal
	iconst_1
;  (28) End Literal
	istore_1
;  (28) End LocalDecl
;  (30) If Statement
;  (30) Name Expression --
	iload_1
;  (30) End NameExpr
	ifeq L1
;  (31) Local Variable Declaration
;  (31) Literal
	iconst_1
;  (31) End Literal
	istore_2
;  (31) End LocalDecl
;  (32) If Statement
;  (32) Name Expression --
	iload_2
;  (32) End NameExpr
	ifeq L3
;  (33) If Statement
;  (33) Name Expression --
	iload_2
;  (33) End NameExpr
	ifeq L4
L4:
;  (33) End IfStat
L3:
;  (32) End IfStat
	goto L2
L1:
;  (38) Local Variable Declaration
;  (38) Literal
	iconst_1
;  (38) End Literal
	istore_2
;  (38) End LocalDecl
;  (39) If Statement
;  (39) Name Expression --
	iload_2
;  (39) End NameExpr
	ifeq L5
;  (40) If Statement
;  (40) Name Expression --
	iload_2
;  (40) End NameExpr
	ifeq L6
L6:
;  (40) End IfStat
L5:
;  (39) End IfStat
L2:
;  (30) End IfStat
;  (46) While Statement
L7:
;  (46) Name Expression --
	iload_1
;  (46) End NameExpr
	ifeq L8
;  (47) Break Statement
	goto L8
;  (47) End BreakStat
	goto L7
L8:
;  (46) End WhileStat
	return
.end method


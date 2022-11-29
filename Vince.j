.class public Vince
.super java/lang/Object

.field public localParam I

.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 3
;  (5) Method Declaration (main)
;  (7) Local Variable Declaration
;  (7) Literal
	iconst_1
;  (7) End Literal
	istore_1
;  (7) End LocalDecl
;  (9) If Statement
;  (9) Name Expression --
	iload_1
;  (9) End NameExpr
	ifeq L1
;  (11) Local Variable Declaration
;  (11) Literal
	iconst_1
;  (11) End Literal
	istore_2
;  (11) End LocalDecl
;  (12) If Statement
;  (12) Name Expression --
	iload_2
;  (12) End NameExpr
	ifeq L3
L3:
;  (12) End IfStat
	goto L2
L1:
;  (18) Local Variable Declaration
;  (18) Literal
	iconst_1
;  (18) End Literal
	istore_2
;  (18) End LocalDecl
;  (19) If Statement
;  (19) Name Expression --
	iload_2
;  (19) End NameExpr
	ifeq L4
;  (20) If Statement
;  (20) Name Expression --
	iload_2
;  (20) End NameExpr
	ifeq L5
L5:
;  (20) End IfStat
L4:
;  (19) End IfStat
L2:
;  (9) End IfStat
;  (27) While Statement
L6:
;  (27) Name Expression --
	iload_1
;  (27) End NameExpr
	ifeq L7
;  (28) Break Statement
	goto L7
;  (28) End BreakStat
	goto L6
L7:
;  (27) End WhileStat
	return
.end method

.method public <init>()V
	.limit stack 50
	.limit locals 1
;  (33) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (33) Field Init Generation Start
;  (33) Field Init Generation End
	return
.end method

.method public <init>(I)V
	.limit stack 50
	.limit locals 2
;  (37) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (37) Field Init Generation Start
;  (37) Field Init Generation End
;  (38) Expression Statement
;  (38) Assignment
;  (38) This
	aload_0
;  (38) End This
;  (38) Name Expression --
	iload_1
;  (38) End NameExpr
	putfield Vince/localParam I
;  (38) End Assignment
;  (38) End ExprStat
	return
.end method

.method public visitReturn(I)I
	.limit stack 50
	.limit locals 2
;  (41) Method Declaration (visitReturn)
;  (42) Return Statement
;  (42) Name Expression --
	iload_1
;  (42) End NameExpr
	ireturn
;  (42) End ReturnStat
.end method

.method public visitNew()LVince;
	.limit stack 50
	.limit locals 6
;  (45) Method Declaration (visitNew)
;  (48) Local Variable Declaration
;  (48) New
	new Vince
	dup
	invokespecial Vince/<init>()V
;  (48) End New
	astore_2
;  (48) End LocalDecl
;  (49) Local Variable Declaration
;  (49) New
	new Vince
	dup
;  (49) Literal
	sipush 420
;  (49) End Literal
	invokespecial Vince/<init>(I)V
;  (49) End New
	astore_3
;  (49) End LocalDecl
;  (51) Local Variable Declaration
;  (51) Literal
	bipush 8
;  (51) End Literal
	i2d
	dstore
;  (51) End LocalDecl
;  (54) Return Statement
;  (54) Name Expression --
	aload_1
;  (54) End NameExpr
	areturn
;  (54) End ReturnStat
.end method


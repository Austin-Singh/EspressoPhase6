.class public Vince
.super java/lang/Object

.field public localParamInt I

.field public localParamInt1 I

.field public localParamInt2 I

.field public localParamInt3 I

.field public localParamBool Z

.field public localParamBool1 Z

.field public localParamBool2 Z

.field public localParamBool3 Z

.method public <init>()V
	.limit stack 50
	.limit locals 1
;  (12) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (12) Field Init Generation Start
	aload_0
;  (3) Literal
	iconst_1
;  (3) End Literal
	putfield Vince/localParamInt I
	aload_0
;  (4) Literal
	iconst_1
;  (4) End Literal
	putfield Vince/localParamInt1 I
	aload_0
;  (5) Literal
	iconst_1
;  (5) End Literal
	putfield Vince/localParamInt2 I
	aload_0
;  (6) Literal
	iconst_1
;  (6) End Literal
	putfield Vince/localParamInt3 I
	aload_0
;  (7) Literal
	iconst_1
;  (7) End Literal
	putfield Vince/localParamBool Z
	aload_0
;  (8) Literal
	iconst_1
;  (8) End Literal
	putfield Vince/localParamBool1 Z
	aload_0
;  (9) Literal
	iconst_1
;  (9) End Literal
	putfield Vince/localParamBool2 Z
	aload_0
;  (10) Literal
	iconst_1
;  (10) End Literal
	putfield Vince/localParamBool3 Z
;  (12) Field Init Generation End
	return
.end method

.method public <init>(Z)V
	.limit stack 50
	.limit locals 2
;  (16) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (16) Field Init Generation Start
	aload_0
;  (3) Literal
	iconst_1
;  (3) End Literal
	putfield Vince/localParamInt I
	aload_0
;  (4) Literal
	iconst_1
;  (4) End Literal
	putfield Vince/localParamInt1 I
	aload_0
;  (5) Literal
	iconst_1
;  (5) End Literal
	putfield Vince/localParamInt2 I
	aload_0
;  (6) Literal
	iconst_1
;  (6) End Literal
	putfield Vince/localParamInt3 I
	aload_0
;  (7) Literal
	iconst_1
;  (7) End Literal
	putfield Vince/localParamBool Z
	aload_0
;  (8) Literal
	iconst_1
;  (8) End Literal
	putfield Vince/localParamBool1 Z
	aload_0
;  (9) Literal
	iconst_1
;  (9) End Literal
	putfield Vince/localParamBool2 Z
	aload_0
;  (10) Literal
	iconst_1
;  (10) End Literal
	putfield Vince/localParamBool3 Z
;  (16) Field Init Generation End
;  (17) Expression Statement
;  (17) Assignment
;  (17) This
	aload_0
;  (17) End This
;  (17) Name Expression --
	iload_1
;  (17) End NameExpr
	putfield Vince/localParamBool Z
;  (17) End Assignment
;  (17) End ExprStat
	return
.end method

.method public <init>(I)V
	.limit stack 50
	.limit locals 2
;  (20) Constructor Declaration
;  (0) Explicit Constructor Invocation
	aload_0
	invokespecial java/lang/Object/<init>()V
;  (0) End CInvocation
;  (20) Field Init Generation Start
	aload_0
;  (3) Literal
	iconst_1
;  (3) End Literal
	putfield Vince/localParamInt I
	aload_0
;  (4) Literal
	iconst_1
;  (4) End Literal
	putfield Vince/localParamInt1 I
	aload_0
;  (5) Literal
	iconst_1
;  (5) End Literal
	putfield Vince/localParamInt2 I
	aload_0
;  (6) Literal
	iconst_1
;  (6) End Literal
	putfield Vince/localParamInt3 I
	aload_0
;  (7) Literal
	iconst_1
;  (7) End Literal
	putfield Vince/localParamBool Z
	aload_0
;  (8) Literal
	iconst_1
;  (8) End Literal
	putfield Vince/localParamBool1 Z
	aload_0
;  (9) Literal
	iconst_1
;  (9) End Literal
	putfield Vince/localParamBool2 Z
	aload_0
;  (10) Literal
	iconst_1
;  (10) End Literal
	putfield Vince/localParamBool3 Z
;  (20) Field Init Generation End
;  (21) Expression Statement
;  (21) Assignment
;  (21) This
	aload_0
;  (21) End This
;  (21) Name Expression --
	iload_1
;  (21) End NameExpr
	putfield Vince/localParamInt I
;  (21) End Assignment
;  (21) End ExprStat
	return
.end method

.method public visitReturn(Z)Z
	.limit stack 50
	.limit locals 2
;  (25) Method Declaration (visitReturn)
;  (26) Return Statement
;  (26) Field Reference
;  (26) This
	aload_0
;  (26) End This
	getfield Vince/localParamBool Z
;  (26) End FieldRef
	ireturn
;  (26) End ReturnStat
.end method

.method public visitNew()LVince;
	.limit stack 50
	.limit locals 4
;  (29) Method Declaration (visitNew)
;  (31) Local Variable Declaration
;  (31) New
	new Vince
	dup
	invokespecial Vince/<init>()V
;  (31) End New
	astore_2
;  (31) End LocalDecl
;  (32) Local Variable Declaration
;  (32) New
	new Vince
	dup
;  (32) Literal
	sipush 420
;  (32) End Literal
	invokespecial Vince/<init>(I)V
;  (32) End New
	astore_3
;  (32) End LocalDecl
;  (33) Return Statement
;  (33) Name Expression --
	aload_1
;  (33) End NameExpr
	areturn
;  (33) End ReturnStat
.end method

.method public testMultipleLocalParams()V
	.limit stack 50
	.limit locals 6
;  (36) Method Declaration (testMultipleLocalParams)
;  (37) Local Variable Declaration
;  (37) Literal
	iconst_0
;  (37) End Literal
	istore_1
;  (37) End LocalDecl
;  (38) Local Variable Declaration
;  (38) Literal
	iconst_0
;  (38) End Literal
	istore_2
;  (38) End LocalDecl
;  (39) Local Variable Declaration
;  (39) Literal
	iconst_0
;  (39) End Literal
	istore_3
;  (39) End LocalDecl
;  (40) Local Variable Declaration
;  (40) Literal
	iconst_0
;  (40) End Literal
	istore
;  (40) End LocalDecl
;  (41) Local Variable Declaration
;  (41) Literal
	iconst_0
;  (41) End Literal
	istore
;  (41) End LocalDecl
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 2
;  (47) Method Declaration (main)
;  (49) Local Variable Declaration
;  (49) Literal
	iconst_1
;  (49) End Literal
	istore_1
;  (49) End LocalDecl
;  (51) If Statement
;  (51) Name Expression --
	iload_1
;  (51) End NameExpr
	ifeq L1
;  (52) If Statement
;  (52) Name Expression --
	iload_1
;  (52) End NameExpr
	ifeq L3
;  (53) If Statement
;  (53) Name Expression --
	iload_1
;  (53) End NameExpr
	ifeq L4
L4:
;  (53) End IfStat
L3:
;  (52) End IfStat
	goto L2
L1:
;  (58) If Statement
;  (58) Name Expression --
	iload_1
;  (58) End NameExpr
	ifeq L5
;  (59) If Statement
;  (59) Name Expression --
	iload_1
;  (59) End NameExpr
	ifeq L6
L6:
;  (59) End IfStat
L5:
;  (58) End IfStat
L2:
;  (51) End IfStat
;  (65) While Statement
L7:
;  (65) Name Expression --
	iload_1
;  (65) End NameExpr
	ifeq L8
;  (66) Break Statement
	goto L8
;  (66) End BreakStat
	goto L7
L8:
;  (65) End WhileStat
	return
.end method


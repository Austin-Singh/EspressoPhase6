/**
	Possibly(?) correct:
	[1] visitLocalDecl() 		// missing number for "istore" command
	[4] visitForStat()			// missing (unsued) label
	[5] visitStaticInitDecl()	// correct jasmine code, wrong order in the file (ref has it at the start)

	Need to change:
	[2] visitUnaryPostExpr()	// wrong
	[3] visitUnaryPreExpr()		// wrong
	
	"COMPLETED" List:
	[6] visitBinaryExpr()		// subtraction broken
	[7] visitCastExpr()			// wrong
	
 */

class VinceSuper { // visitClassDecl
    public int vinceSuperField;
}

public class Vince extends VinceSuper { // visitClassDecl

	public int fieldInt;
	public static int staticFieldInt;
	//public static double staticFieldDouble = 4.20; // The output mathces, but the order which the correct lines occur in the .j/.jr files are wrong.

	Vince(){
		//
	}

	Vince(int x){
		fieldInt = x;
	}

	public static void main(String args[]){
		//
	}

	public void visitContinueStat(){
		while(true){continue;}
	}

	public void visitDoStat(){
		do{}while(true);
	}

	// NOT MATCHING
	public void visitForStat(){
		for (int i = 0; i <= 10; i = i + 2) {
		}
	}

	public void visitWhileStat(){
		while(true){

		}
	}

	public void visitIfStat(){
		if(true){
			if(true){
				if(true){

				}
			}else{
				if(true){

				}
			}
		}else{
			if(true){

			}
		}
	}

	public int visitReturnStat(int x){
		return x;
	}

	public int visitThis(){
		this.fieldInt = 0;
	}

	public void visitBreakStat(){
		while(true){break;}
	}

	// PARTIALY NOT MATCHING
	public void visitLocalDecl(){
		int localDecl1 = 0;
		int localDecl2 = 0;
		int localDecl3 = 0;
		//int localDecl4 = 0; // DIFF | OUR: istore 4 | REF: istore (SimpleInstrction related?)
	}

	public void visitInvocation(){
		Vince cInvo = new Vince(); // visitCInvocation
		cInvo.visitContinueStat(); // visitInvocation
	}

	public void visitNameExpr(int x){
		Vince cInvo = new Vince(); // NameExpr
		cInvo.visitContinueStat(); // NameExpr
		//int y = cInvo.fieldInt; // NameExpr
		//int z = x; // NameExpr
	}

	public void visitNew(){
		Vince c = new Vince();
	}

	// NOT MATCHING / WRONG ORDER
	public static int visitStaticInitDecl(){
		Vince v = new Vince();
		v.staticFieldInt = 1;
		return v.staticFieldInt;
	}

	public void visitSuper(){
		int x = super.vinceSuperField;
	}

	// NOT MATCHING
	public int visitUnaryPostExpr(int x){
		//return visitUnaryPostExpr(x++);
	}

	// NOT MATCHING
	public int visitUnaryPreExpr(int x){
		//return visitUnaryPreExpr(x++);
	}

	public void visitBinaryExpr(){
		int x = 10 + 10;
		x = x + x;
		x = 10 * 10;
		x = 10 / 10;
		x = 10 % 10;
		x = 10 - 10; // extra jasmine lines being generated?

		double y = 2.1 + 2.1;
		y = x * x;
		y = 2.1 * x;
		y = 2.1 / x;
		y = 2.1 % x;
		y = 2.1 - x; // extra jasmine lines being generated?

		//float z = x + x; // DIFF | OUR: fstore 4 | REF: fstore (SimpleInstrction related?)

		//long v = x + x; // DIFF | OUR: fstore 4 | REF: lstore (SimpleInstrction related?)
	}

	public void visitCInvocation(){
		Vince cInvo = new Vince(); // visitCInvocation
		cInvo.visitContinueStat(); // visitInvocation
	}

	public void visitCastExpr(){
		int i = 1;
		double d = 2.2;
		float f = 1;
		long l = 1;

		l = (int)f;
		l = (int)d;
		i = (int)f;
		i = (int)d;
		d = (double)i;
		d = (double)f;

	}

	public void visitConstructorDecl(){
		Vince v = new Vince(10);
	}

}
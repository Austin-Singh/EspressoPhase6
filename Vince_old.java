public class Vince_old {

/**
	Functions to check:
	[1] visitLocalDecl()
	[2] visitUnaryPostExpr()
	[3] visitUnaryPreExpr()
 */

	int fieldInt = 0;
	boolean fieldBool = true;
	float fieldFloat = 0;
	double fieldDouble = 0;
	long fieldLong = 0;

	Vince_old(){

	}

	Vince_old(int fieldInt){
		this.fieldInt = fieldInt;
	}

	Vince_old(boolean fieldBool){
		this.fieldBool = fieldBool;
	}

	Vince_old(float fieldFloat){
		this.fieldFloat = fieldFloat;
	}

	Vince_old(double fieldDouble){
		this.fieldDouble = fieldDouble;
	}

	Vince_old(long fieldLong){
		this.fieldLong = fieldLong;
	}

	public boolean visitReturn(boolean fieldBool){
		return fieldBool;
	}

	public int visitUnaryPostExpr(int x){
		//visitUnaryPostExpr(x++); // DIFF 
		
		/**
		OUR:
			dup
			iconst_1
			iadd
			istore_1
		*/

		/**
		REF:
			iinc 1 1
		*/
	} 

	public int visitUnaryPreExpr(int x){
		//visitUnaryPostExpr(++x); // DIFF 
		
		/**
		OUR:
			iinc 1 1
		*/

		/**
		REF:
			iconst_1
			iadd
			dup
			istore_1
		*/
	} 

	public void visitContinueStat(){
		while(true){
			continue;
		}
	}

	public void visitBreakStat(){
		while(true){
			break;
		}
	}

	public Vince_old visitNew(){
		Vince_old t;
		Vince_old t1 = new Vince_old();
		Vince_old t2 = new Vince_old(420);
		//Vince_old t3 = new Vince_old(4.20); // DIFF | our: astore | ref: astore 4
		return t;
	}

	public void testMultipleLocalParams1(){
		boolean localParamBool = false;
		float localParamDouble = 0;
		int localParamInt = 0;
		//boolean t4 = false; // DIFF | our: istore | ref: istore 4
		//boolean t5 = false; // DIFF | our: istore | ref: istore 5
		// Seemingly we are declaring local stack variables incorrectly

	}

	public void testMultipleLocalParams2(){
		//double localParamBool = 0; // DIFF | our: istore | ref: istore 4
		float localParamDouble = 0;
		int localParamInt = 0;
		// This will cause an error because of the double. I guess we have an error after we get to a certain space of stack variables?

		// I think the issue is with visitLocalDecl(). That comment you left about SimpleInstruction is probably the fix?
	}

	public static void main(String args[]) {

		boolean var1 = true;

		if (var1) {
			if(var1){
				if(var1){

				}
			}
		}else{
			if(var1){
				if(var1){

				}
			}
		}

		while(var1){
			break;
		}

	}
}
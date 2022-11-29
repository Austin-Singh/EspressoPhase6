public class Vince {

	int localParamInt = 1;
	int localParamInt1 = 1;
	int localParamInt2 = 1;
	int localParamInt3 = 1;
	boolean localParamBool = true;
	boolean localParamBool1 = true;
	boolean localParamBool2 = true;
	boolean localParamBool3 = true;

	Vince(){

	}

	Vince(boolean localParamBool){
		this.localParamBool = localParamBool;
	}

	Vince(int localParamInt){
		this.localParamInt = localParamInt;
	}


	public boolean visitReturn(boolean localParamBool){
		return this.localParamBool;
	}

	public Vince visitNew(){
		Vince t;
		Vince t1 = new Vince();
		Vince t2 = new Vince(420);
		return t;
	}

	public void testMultipleLocalParams(){
		boolean t1 = false; // MATCH 
		boolean t2 = false; // MATCH
		boolean t3 = false; // MATCH
		boolean t4 = false; // DIFF | our: istore | ref: istore 4
		boolean t5 = false; // DIFF | our: istore | ref: istore 5

		// Seemingly we are declaring local stack variables incorrectly

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
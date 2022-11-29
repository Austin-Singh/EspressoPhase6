public class Vince {

	int localParamInt;
	boolean localParamBool;

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
		boolean t1 = false; // no error 
		boolean t2 = false; // no error
		boolean t3 = false; // no error
		boolean t4 = false; // DIFF | our: istore | ref: istore 4
		boolean t5 = false; // DIFF | our: istore | ref: istore 5
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
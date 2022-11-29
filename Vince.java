public class Vince {

	int localParam;

	public static void main(String args[]) {

		boolean var1 = true;

		if (var1) {

			boolean var2 = true;
			if(var2){

			}

		}else {

			boolean var3 = true;
			if(var3){
				if(var3){

				}
			}

		}

		while(var1){
			break;
		}

	}

	Vince(){

	}

	Vince(int localParam){
		this.localParam = localParam;
	}

	public int visitReturn(int var4){
		return var4;
	}

	public Vince visitNew(){

		Vince t;
		Vince t1 = new Vince();
		Vince t2 = new Vince(420);

		double var6 = 8;
		//Vince t3 = new Vince(var6);

		return t;
	}
}
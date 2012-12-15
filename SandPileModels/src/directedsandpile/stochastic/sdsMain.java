package directedsandpile.stochastic;
import directedsandpile.stochastic.model.SDSModel;

public class sdsMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int L = 1000;
		String file = "sds-avalanche";
		SDSModel sds = new SDSModel(L,L, file+".txt");
		for (int i=0; i< 100; ) {
			sds.Randomize();
			sds.SetFileName(file +String.format("-%1$06d.txt", i));
			if(sds.NextStep()) {
				i++;
			}
			System.out.println("s = " + sds.Get_s());
			System.out.println("t = " + sds.Get_t());
		}
	}

}

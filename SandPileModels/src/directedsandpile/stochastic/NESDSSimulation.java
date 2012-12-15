package directedsandpile.stochastic;

import directedsandpile.stochastic.model.NESDSModel;

public class NESDSSimulation {
	/**
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * November, 2012
	 */
	
	static int lx = 50;
	static int ly = 50;
	static int MAX_ITERATIONS = 1000;

	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		NESDSModel sds = new NESDSModel(lx, ly, 600, 600, "nesds");
		sds.SetRecursionDepth(90);
		
		// Simulation iterations
		for (int iter=0; iter<MAX_ITERATIONS; iter++) {
			// Performing next simulation step
			sds.NextStep();
		}
		System.err.println("\nSimulation is done");
	}

}

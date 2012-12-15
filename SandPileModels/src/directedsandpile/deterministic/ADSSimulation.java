package directedsandpile.deterministic;

import directedsandpile.deterministic.model.ADSModel;

public class ADSSimulation {
	/**
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * November, 2012
	 */
	
	static int lx = 50;
	static int ly = 50;
	static int MAX_ITERATIONS = 500;

	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		ADSModel ads = new ADSModel(lx, ly, 600, 600, "ads");
		ads.SetRecursionDepth(90);
		
		// Simulation iterations
		for (int iter=0; iter<MAX_ITERATIONS; iter++) {
			// Performing next simulation step
			ads.NextStep();
		}
		System.err.println("\nSimulation is done");
	}
}

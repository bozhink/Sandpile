package btw;

import btw.model.NonRecursiveBTWModel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BTWAvalanchesHistograms {
	static int latticeSize=50;
	static int numberOfAvalanches=10000000;
	
	static String fileName = "nonrecbtw.dat";
	static FileOutputStream fout;
	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		NonRecursiveBTWModel model = new NonRecursiveBTWModel(latticeSize);
		System.out.println("Simulation started...");
		for (int i=0; i<numberOfAvalanches; i++) {
			model.NextAvalanche();
		}
		System.out.println("Simulation terminated.");
		System.out.println("Exporting histogram data to output file");
		try {
			fout = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			System.err.println("Error opening output file!");
			return;
		}
		
		String formattedData;
		try {
			for (int i=0; i<model.nAval.length; i++) {
				formattedData = String.format("%1$d %2$d %3$d\r\n", i, 
						model.nAval[i], model.tAval[i]);
				fout.write(formattedData.getBytes());
			}
		} catch (IOException e1) {
			System.err.println("Error writing data to output file!");
		}
				
		try {
			fout.close();
		} catch (IOException e) {
			System.err.println("Error closing output file!");
			e.printStackTrace();
			return;
		}
		
		System.out.println("Probably everything went fine.");
		System.out.println("Program now will exit.");
	}

}

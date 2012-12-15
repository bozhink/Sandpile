package directedsandpile.stochastic.model;

import java.io.*;

public class SDSModel {
	/**
	 * Non-exclusive Stochastic Directed Sandpile Model
	 * 
	 * See arXiv:cond-mat/9907307 for more information.
	 * 
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * November, 2012
	 */
	private MatrixElement[][] matrix;
	private int Lx, Ly, Lmiddle;
	private int recursionDepth;
	private byte THRESHOLD = 2;
	
	private int s, t;
	private String filename;
	private FileOutputStream fout;
	private String buf;
	
	private boolean isValid;
	
	
	/**
	 * This constructor creates and initializes the model's matrix
	 * @param lx x-dimension of the matrix.
	 * In x-direction we have periodic boundary conditions.
	 * @param ly y-dimension of the matrix.
	 * In y-dimension we have open boundary condition.
	 */
	public SDSModel(int lx, int ly) {
		Lx = lx;
		Ly = ly;
		matrix = new MatrixElement[Lx][Ly];
		Lmiddle = Lx/2;
		recursionDepth = 800;
		// Initialization
		for (int i=0; i<Lx; i++) {
			for (int j=0; j<Ly; j++) {
				matrix[i][j] = new MatrixElement();
			}
		}
		filename = "sds-avalanche";
	}
	
	/**
	 * This constructor creates and initializes the model's matrix
	 * @param lx x-dimension of the matrix.
	 * In x-direction we have periodic boundary conditions.
	 * @param ly y-dimension of the matrix.
	 * In y-dimension we have open boundary condition.
	 * @param fname name of file to export avalanche data
	 */
	public SDSModel(int lx, int ly, String fname) {
		Lx = lx;
		Ly = ly;
		matrix = new MatrixElement[Lx][Ly];
		Lmiddle = Lx/2;
		recursionDepth = 800;
		// Initialization
		for (int i=0; i<Lx; i++) {
			for (int j=0; j<Ly; j++) {
				matrix[i][j] = new MatrixElement();
			}
		}
		filename = fname;
	}
	
	/**
	 * This method generates random initial state for the model
	 */
	public void Randomize() {
		for (int x=0; x<Lx; x++)
			for (int y=0; y<Ly; y++)
				matrix[x][y].value = (byte) Math.abs(2.0*Math.random());
	}
	
	/**
	 * This method sets the output file name
	 * @param fname name of the file to export avalanche data
	 */
	public void SetFileName(String fname) {
		filename = fname;
	}
	
	
	
	/**
	 * This method changes the maximal recursion depth for the update method 
	 * @param recDepth value of recursion depth to be set
	 */
	public void SetRecursionDepth(int recDepth) {
		recursionDepth = recDepth;
	}
	
	private void Update(int x, int y, int recursionOrder) {
		isValid = true;
		if (recursionOrder < recursionDepth) {
			t = Math.max(t, y);
			if (y>=Ly) {
				return;
			}
			matrix[x][y].color=1;
			/*
			buf = x + " " + y + "\r\n";
			try {
				fout.write(buf.getBytes());
			} catch (IOException e) {
				System.err.println("Error: Cannot write to output file "+filename);
				e.printStackTrace();
				return;
			}
			*/
			if (matrix[x][y].value >= THRESHOLD) {
				// toppling
				matrix[x][y].value -= THRESHOLD;
				// recalculation of mass parameter s
				s++;
				
				// Updating next row
				if (y+1 >= Ly) {
					return;// Open boundary condition
				} else {
					// Stochastic part
					int x1 = (x + Lx + (int)(3.0*Math.abs(Math.random())) - 1) % Lx;
					int x2 = (x + Lx + (int)(3.0*Math.abs(Math.random())) - 1) % Lx;
					y++;
					matrix[x1][y].value++;
					matrix[x2][y].value++;
					Update(x1, y, recursionOrder+1);
					Update(x2, y, recursionOrder+1);
				}
			}
		} else {
			isValid = false;
			System.err.println("Recursion depth exceeded.");
		}
	}
	
	public int Get_s() { return s; }
	
	public int Get_t() { return t; }
	
	/**
	 * This method performs one simulation step
	 */
	public boolean NextStep() {
		s=0;
		t=0;
		// Performing next step;
		for (int x=0; x<Lx; x++)
			for (int y=0; y<Ly; y++)
				matrix[x][y].color = 0;
		//
		matrix[Lmiddle][0].value += THRESHOLD;
		try {
			fout = new FileOutputStream(filename);
			Update(Lmiddle, 0, 0);
		} catch (FileNotFoundException e) {
			System.err.println("Error: Cannot open output file "+filename);
		}
		if (isValid) {
			for (int x=0; x<Lx; x++) {
				for (int y=0; y<Ly; y++) {
					if (matrix[x][y].color != 0) {
						buf = x + " " + y + "\r\n";
						try {
							fout.write(buf.getBytes());
						} catch (IOException e) {
							System.err.println("Error: Cannot write to output file "+filename);
							e.printStackTrace();
						}
					}
				}
			}
			buf = "#t="+t+" s="+s+"\r\n";
			try {
				fout.write(buf.getBytes());
			} catch (IOException e) {
				System.err.println("Error: Cannot write to output file "+filename);
				e.printStackTrace();
			}
		}
		try {
			fout.close();
		} catch (IOException e) {
			System.err.println("Error: Cannot close output file " + filename);
			e.printStackTrace();
		}
		return isValid;
	}
	
	/**
	 * This method returns the (x,y)-th matrix element
	 * @param x x-coordinate in the matrix (periodic boundary conditions)
	 * @param y y-coordinate in the matrix (open boundary condition)
	 * @return value of matrix[x][y]
	 */
	public int GetMatrixElement(int x, int y) {
		if (y >= Ly || y <0 ){
			return 0;
		} else {
			return matrix[(x + Lx) % Lx][y].value;
		}
	}
	class MatrixElement {
		byte value;
		byte color;
		public MatrixElement() {
			value=0;
			color=0;
		}
	}
}



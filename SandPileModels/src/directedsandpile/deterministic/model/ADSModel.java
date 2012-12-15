package directedsandpile.deterministic.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import directedsandpile.deterministic.model.MatrixElement;


public class ADSModel {
	
	/**
	 * Abelian Directed Sandpile Model
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
	private byte THRESHOLD = 3;
	
	private int s;
	// Graphic parameters
	private int w, h;
	private BufferedImage bi;
	private Graphics2D g;
	private String filename;
	private int current;
	@SuppressWarnings("unused")
	private boolean exportImage;
	
	
	/**
	 * This constructor creates and initializes the model's matrix
	 * @param lx x-dimension of the matrix.
	 * In x-direction we have periodic boundary conditions.
	 * @param ly y-dimension of the matrix.
	 * In y-dimension we have open boundary condition.
	 */
	public ADSModel(int lx, int ly) {
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
		exportImage = false;
		current = 0;
		System.err.println(
				"You must not use exporting methods using this constuctor");
	}
	
	/**
	 * This constructor creates and initializes the model's matrix
	 * @param lx x-dimension of the matrix.
	 * In x-direction we have periodic boundary conditions.
	 * @param ly y-dimension of the matrix.
	 * In y-dimension we have open boundary condition.
	 * @param width width of exported picture
	 * @param height height of exported picture
	 * @param name the initial part (without the extension) of name of exported picture
	 */
	public ADSModel(int lx, int ly, int width, int height, String name) {
		filename = name;
		Lx = lx;
		Ly = ly;
		w = width / Lx;
		h = height / Ly;
		matrix = new MatrixElement[Lx][Ly];
		Lmiddle = Lx/2;
		recursionDepth = 800;
		// Initialization
		for (int i=0; i<Lx; i++) {
			for (int j=0; j<Ly; j++) {
				matrix[i][j] = new MatrixElement();
			}
		}
		bi = new BufferedImage(width+w, height+h, BufferedImage.TYPE_INT_ARGB);
		g = bi.createGraphics();
		s=0;
		exportImage = true;
		current = 0;
	}
	
	/**
	 * This method changes the maximal recursion depth for the update method 
	 * @param recDepth value of recursion depth to be set
	 */
	public void SetRecursionDepth(int recDepth) {
		recursionDepth = recDepth;
	}
	
	private void Update(int x, int y, int recursionOrder) {
		if (recursionOrder < recursionDepth) {
			// Comment the following line if you don't want to
			// export image of the lattice for every iteration
			Export();
			
			if (matrix[x][y].value >= THRESHOLD) {
				// toppling
				matrix[x][y].value -= THRESHOLD;
				// recalculation of mass parameter s
				s++;
				// Marking (x,y) point as a point in the avalanche
				switch (matrix[x][y].value)
				{
				case 0:
					matrix[x][y].color = 4;
					break;
				case 1:
					matrix[x][y].color = 5;
					break;
				case 2:
					matrix[x][y].color = 6;
					break;
				default :
					matrix[x][y].color = 7;
				}
				
				
				
				// Updating next row
				if (y+1 >= Ly) {
					return;// Open boundary condition
				} else {
					// Stochastic part
					int x1 = (x + Lx - 1) % Lx;
					int x2 = (x + Lx + 1) % Lx;
					y++;
					matrix[x1][y].value++;
					matrix[x2][y].value++;
					matrix[x][y].value++;
					Update(x,  y, recursionOrder+1);
					Update(x1, y, recursionOrder+1);
					Update(x2, y, recursionOrder+1);
				}
			}
		} else {
			System.err.println("Recursion depth exceeded.");
		}
	}
	
	public int Get_s() {
		return s;
	}
	
	private void Export() {
		for (int x=0; x<Lx; x++) {
			for (int y=0; y<Ly; y++) {
				switch (matrix[x][y].color)
				{
				case 0:
					g.setColor(Color.WHITE);
					break;
				case 1:
					g.setColor(Color.GRAY);
					break;
				case 2:
					g.setColor(Color.BLACK);
					break;
				case 3:
					g.setColor(Color.RED);
					break;
				case 4:
					g.setColor(Color.YELLOW);
					break;
				case 5:
					g.setColor(Color.ORANGE);
					break;
				case 6:
					g.setColor(Color.GREEN);
					break;
				default:
					g.setColor(Color.RED);
				}
				g.fillRect(x*w, y*h, w, h);
			}
		}
		try {
			String fname = filename + String.format("-%1$09d.png", current++);
			ImageIO.write(bi, "PNG", new File(fname));
			System.err.println("File "+fname+" exported");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	/**
	 * This method performs one simulation step
	 */
	public void NextStep() {
		s=0;
		// Resetting color flags
		for (int i=0; i<Lx; i++) {
			for (int j=0; j<Ly; j++) {
				switch (matrix[i][j].value)
				{
				case 0:
					matrix[i][j].color = 0;
					break;
				case 1:
					matrix[i][j].color = 1;
					break;
				case 2:
					matrix[i][j].color = 2;
					break;
				default :
					matrix[i][j].color = 3;
				}
			}
		}
		// Performing next step;
		matrix[Lmiddle][0].value += THRESHOLD;
		Update(Lmiddle, 0, 0);
		// Uncomment the following line if you want to export
		// image of the lattice for the final result after the
		// avalanche
		//if (exportImage) Export();
	}
	
	/**
	 * This method performs N simulation steps
	 * @param N number of steps to do
	 */
	public void NextStep(int N) {
		for (int i=0; i<N; i++)
			NextStep();
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
	
}

class MatrixElement {
	byte value;
	byte color;
	public MatrixElement() {
		value=0;
		color=0;
	}

}

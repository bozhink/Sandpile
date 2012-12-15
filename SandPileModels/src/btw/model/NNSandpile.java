package btw.model;

public class NNSandpile {
	/**
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * November, 30, 2012
	 * 
	 * Nine-Neighbor Sandpile Model
	 * 
	 * This program is based on source codes, presented in
	 * "Pattern Formation in Growing Sandpiles" 
	 * by Aimee Ross, University of Massachusetts Dartmouth
	 */
	
	private int Lx, Ly; // Matrix sizes
	private int zMatrix[][];
	public static int zCritical = 8; 
	
	/**
	 * Creation of an Abelian sandpile model
	 * @param Lx x-dimension of model's lattice
	 * @param Ly y-dimension of model's lattice
	 */
	public NNSandpile (int Lx, int Ly) {
		this.Lx = Lx;
		this.Ly = Ly;
		zMatrix = new int[Lx+2][Ly+2];
		// Initialization of the lattice
		for (int i=0; i<Lx+2; i++) {
			for (int j=0; j<Ly+2; j++) {
				zMatrix[i][j] = zCritical/2;
			}
		}
	}
	
	/**
	 * X-dimension of lattice
	 * @return Lx
	 */
	public int GetXDimension() {
		return Lx;
	}
	
	/**
	 * Y-dimesion of lattice
	 * @return Ly
	 */
	public int GetYDimension() {
		return Ly;
	}
	
	/**
	 * Get matrix element
	 * @param x x-coordinate of the element; 0<= x < Lx
	 * @param y y-coordinate of the element; 0<= y < Ly
	 * @return lattice (x,y)-element's value
	 */
	public int GetElement(int x, int y) {
		if (x<0 || x>=Lx || y<0 || y>=Ly) {
			return -1;
		}
		x++;
		y++;
		return zMatrix[x][y];
	}
	
	public void DropSandGrain() {
		int x0 = (Lx+2)/2;
		int y0 = (Ly+2)/2;
		zMatrix[x0][y0]++; // Drop a grain of sand on the center of the lattice
		for (int x=1; x<Lx+1; x++) {
			for (int y=1; y<Ly+1; y++) {
				if (zMatrix[x][y] >= zCritical) // Toppling
				{
					zMatrix[x][y] -= zCritical;
					zMatrix[x+1][y]++;
					zMatrix[x+1][y+1]++;
					zMatrix[x][y+1]++;
					zMatrix[x-1][y+1]++;
					zMatrix[x-1][y]++;
					zMatrix[x-1][y-1]++;
					zMatrix[x][y-1]++;
					zMatrix[x+1][y-1]++;
				}
			}
		}
		// Clear useless boundary rows and columns
		for (int i=0; i<Lx; i++) {
			zMatrix[i][0]=0;
			zMatrix[i][Ly+1]=0;
		}
		for (int j=0; j<Ly; j++) {
			zMatrix[0][j]=0;
			zMatrix[Lx+1][j]=0;
		}
	}
}

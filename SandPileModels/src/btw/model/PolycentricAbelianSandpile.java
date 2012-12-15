package btw.model;

public class PolycentricAbelianSandpile {
	/**
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * November, 30, 2012
	 * 
	 * This program is based on source codes, presented in
	 * "Pattern Formation in Growing Sandpiles" 
	 * by Aimee Ross, University of Massachusetts Dartmouth
	 */
	
	private int Lx, Ly; // Matrix sizes
	private int zMatrix[][];
	public static int zCritical = 4;
	private int centers[][];
	
	/**
	 * Creation of an Abelian sandpile model
	 * @param Lx x-dimension of model's lattice
	 * @param Ly y-dimension of model's lattice
	 */
	public PolycentricAbelianSandpile (int Lx, int Ly) {
		this.Lx = Lx;
		this.Ly = Ly;
		zMatrix = new int[Lx+2][Ly+2];
		centers = new int[2][1];
		centers[0][0] = Lx/2;
		centers[1][0] = Ly/2;
		// Initialization of the lattice
		for (int i=0; i<Lx+2; i++) {
			for (int j=0; j<Ly+2; j++) {
				zMatrix[i][j] = 2;
			}
		}
	}
	
	/**
	 * Creation of Abelian Sandpile Model with various centers of grain dropping
	 * @param Lx x-dimension of model's lattice
	 * @param Ly y-dimension of model's lattice
	 * @param centers array of coordinated of dropping centers; must be of type
	 * int[2][N], where N is the number of centers.
	 */
	public PolycentricAbelianSandpile (int Lx, int Ly, int centers[][]) {
		this.Lx = Lx;
		this.Ly = Ly;
		zMatrix = new int[Lx+2][Ly+2];
		this.centers = centers;
		// Initialization of the lattice
		for (int i=0; i<Lx+2; i++) {
			for (int j=0; j<Ly+2; j++) {
				zMatrix[i][j] = 2;
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
		for (int i=0; i<centers[0].length; i++) {
			// Drop a grain of sand
			zMatrix[centers[0][i]][centers[1][i]]++; 
		}
		for (int x=1; x<Lx+1; x++) {
			for (int y=1; y<Ly+1; y++) {
				if (zMatrix[x][y] >= zCritical) // Toppling
				{
					zMatrix[x][y] -= zCritical;
					zMatrix[x+1][y]++;
					zMatrix[x-1][y]++;
					zMatrix[x][y+1]++;
					zMatrix[x][y-1]++;
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


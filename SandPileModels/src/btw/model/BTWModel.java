package btw.model;

public class BTWModel {
	public int nx; // x-dimension
	public int ny; // y-dimension
	public int matrix[][];
	private int cx; // x-coordinate of central point
	private int cy; // y-coordinate of central point
	public int THRESHOLD = 4;
	
	public BTWModel(int nx, int ny) {
		this.nx = nx;
		this.ny = ny;
		matrix = new int[nx][ny];
		for (int i=0; i<nx; i++) {
			for (int j=0; j<ny; j++) {
				matrix[i][j] = 2;
			}
		}
		cx = nx/2;
		cy = ny/2;
	}
	
	private void update(int x, int y) {
		if (matrix[x][y] > THRESHOLD) {
			int xp1 = (x+1+nx) % nx;
			int xm1 = (x-1+nx) % nx;
			int yp1 = (y+1+ny) % ny;
			int ym1 = (y-1+ny) % ny;
			matrix[x][y]-=4;
			matrix[x][yp1]++;
			update(x, yp1);
			matrix[x][ym1]++;
			update(x, ym1);
			matrix[xp1][y]++;
			update(xp1, y);
			matrix[xm1][y]++;
			update(xm1, y);
		}
	}
	
	public void Update() {
		matrix[cx][cy]++;
		update(cx, cy);
	}
}

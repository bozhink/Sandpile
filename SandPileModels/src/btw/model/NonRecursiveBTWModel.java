package btw.model;

public class NonRecursiveBTWModel {
	/**
	 * @author Bozhin Karaivanov
	 * @version 0.1
	 * December, 3, 2012
	 * 
	 * This class simulates the Bak-Tang-Wiesenfeld Sandpile model
	 * without the use of recursive update-matrix algorithm.
	 * 
	 * This class is based on the original SandPile class by
	 * Larsson Omberg, 26-Sep-1998
	 */

	private int nSize;
	private static int nZc = 4;
	private int[][] pile; 
	private boolean[][] changed;
	public int[] nAval;
	public int[] tAval;
	
	public NonRecursiveBTWModel(int size) {
		nSize = size;
		pile = new int[nSize+2][nSize+2];
		changed = new boolean[nSize+2][nSize+2];
		nAval = new int[nSize*nSize];
		tAval = new int[nSize*nSize];
		this.seed();
		this.zeroChanged();
		for (int i=0; i<nSize; i++) {
			nAval[i]=0;
			tAval[i]=0;
		}
	}
	
	public void NextAvalanche() {
		this.seed();
		this.zeroChanged();
		this.dropSand();
		this.evolve();
	}
	
	
	private void seed() {
		for(int i=1; i<=nSize; i++) {
			for(int j=1; j<=nSize; j++) {
				pile[i][j] = (int)Math.round(Math.random()*(nZc-1));
			}
		}
	}
	
	//------------Changes the changed matrix to false between drops----
	private void zeroChanged() {
		for (int i=0; i<nSize+2; i++) {
			for (int j=0; j<nSize+2; j++) {
				changed[i][j]= false;
			}
		}
	}
 

	//-----------Error checking method that writes pile----------------
	public void write() {
		for(int i=1; i<=nSize; i++) {
			for(int j=1; j<=nSize; j++) {
				System.out.print(pile[i][j]+" ");
			}
			System.out.println();
		}
	}
  
	//-----------Simply drops a piece of sand at random position-------
	public void dropSand() {
		int x = (int)Math.round(Math.random()*nSize)+1;
		int y = (int)Math.round(Math.random()*nSize)+1;
		pile[x][y]++;
	}

	//-----------Does the evolving ounce sand has fallen---------------
	public void evolve() {
		int nAvalSize = 0;
		int nAvalTime = 0;
		boolean bCritical = true;
		while (bCritical) {
			bCritical = false;
			nAvalTime++;
			for (int i=1; i<=nSize; i++) {
				for (int j=1; j<=nSize; j++) {
					if (pile[i][j] >= nZc) {
						pile[i][j] -= 4;
						pile[i-1][j]++;
						pile[i+1][j]++;
						pile[i][j-1]++;
						pile[i][j+1]++;
						if (!changed[i][j]) {
							nAvalSize++;
							changed[i][j]=true;
						}
						bCritical = true;
					}
				}
			}
		}  /* while */
		nAval[nAvalSize]++;
		tAval[nAvalTime-1]++;
	}
}

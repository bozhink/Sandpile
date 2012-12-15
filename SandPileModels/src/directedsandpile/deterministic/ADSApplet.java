package directedsandpile.deterministic;

/*
<applet code="ADSApplet" width=300 height=300>
</applet>
*/

import java.applet.Applet;
import java.awt.*;

/********************************************************************
 *   AUTHOR: Larsson Omberg
 *   DATE:   26-Sep-1998
 *   PURPOSE: To simulate sand piles and hopefully get some SOC
 *   NOTES: Eddited by Bozhin Karaivanov, November, 2012
 *   Abelian Directed Sandpile Model
 ********************************************************************/

public class ADSApplet extends Applet implements Runnable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nSize = 50;
	private static int nZc = 3;
	private int[][] pile;
	private boolean[][] changed;
	private Thread evoluThread; 
	private int[] nAval;
	private int[] tAval;
	private Color[] colors;
	private Image offImage;
	private Graphics offG;
	private Button suspendButton;

	private int nGen;

	//----------------------Initialization methods---------------------
	public void init() {
		nGen = 0;
		//suspendButton = new Button("Stop");
		//this.add(suspendButton);
		if (getParameter("size") != null) {
			nSize = Integer.valueOf(getParameter("size")).intValue();
		}
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
		colors = new Color[10];
		colors[0] = new Color(40, 40, 240);
		colors[1] = new Color(255, 255, 255);
		for (int i=2; i<10; i++) {
			colors[i] = colors[i-1].darker();
		}
	}

	public void start() {
		if (evoluThread == null) {
			evoluThread = new Thread(this);
		}
		evoluThread.start();
	}
  
	public void stop() {
		evoluThread = null;
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
	@SuppressWarnings("unused")
	private void write() {
		for(int i=1; i<=nSize; i++) {
			for(int j=1; j<=nSize; j++) {
				System.out.print(pile[i][j]+" ");
			}
			System.out.println();
		}
	}
  
	//-----------Simply drops a piece of sand at random position-------
	private void dropSand() {
		//pile[1][nSize/2+1]++;
		int x = (int)Math.round(Math.random()*nSize)+1;
		int y = (int)Math.round(Math.random()*nSize)+1;
		pile[x][y]++;
	}

	//-----------Does the evolving ounce sand has fallen---------------
	private void evolve() {
		int nAvalSize = 0;
		int nAvalTime = 0;
		boolean bCritical = true;
		while (bCritical) {
			bCritical = false;
			nAvalTime++;
			for (int i=1; i<=nSize; i++) {
				for (int j=1; j<=nSize; j++) {
					if (pile[i][j] >= nZc) {
						pile[i][j] -= nZc;
						pile[i+1][j]++;
						pile[i+1][j-1]++;
						pile[i+1][j+1]++;
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
		this.showStatus("Generation: " + ++nGen);
	}

	//-----------Run Method--------------------------------------------
	public void run() {
		while (evoluThread==Thread.currentThread()) {
			dropSand();
			evolve();
			repaint();
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {System.err.println(e);}
			zeroChanged();
		}
	}

	//----------EventHandling Method-----------------------------------
	public boolean action (Event e, Object arg) {
		if (suspendButton.getLabel() == "Stop") {
			suspendButton.setLabel("Start");
			stop();
			for (int i=0; i<nSize*nSize; i++) {
				if ((nAval[i] != 0) && (tAval[i] != 0)) {
					System.out.println(i+"; " + nAval[i] + "; " + tAval[i]);
				}
			}	
		} else {
			suspendButton.setLabel("Stop");
			start();
		}
		return false;
	}
 
	//----------Update method using double buffering-------------------
	public void update(Graphics g) {
		float deltaW = this.getWidth()/(nSize);
		float deltaH = this.getHeight()/(nSize);
		offImage = createImage(this.getWidth(), this.getHeight());
		offG = offImage.getGraphics();
		offG.setColor(getBackground());
		offG.fillRect(0, 0, this.getWidth(), this.getHeight());
		for (int i= 1; i<=nSize; i++) {
			for (int j=1; j<=nSize; j++) {
				offG.setColor(colors[pile[i][j]]);
				offG.fillRect((int)((j -1)*deltaW+1), (int)((i-1)*deltaH+1),
						(int)deltaW-1, (int)deltaH-1);
				offG.setColor(Color.black);
				if (changed[i][j]) {
					offG.setColor(Color.red);
					offG.drawRect((int)((j-1)*deltaW), (int)((i-1)*deltaH),
							(int)deltaW, (int)deltaH);
				}
			}
		}
		g.drawImage(offImage,0,0, this);
	}

	//----------paint method ------------------------------------------
	public void paint(Graphics g) {
		update(g);
	}
}

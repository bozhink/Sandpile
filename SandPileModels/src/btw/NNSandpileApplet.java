package btw;

import java.awt.Color;
import java.awt.Graphics;
import java.applet.Applet;
import btw.model.NNSandpile;


/*
<applet code="NNSandpileApplet" width=600 height=600 />
*/

public class NNSandpileApplet extends Applet implements Runnable
{
	private static final long serialVersionUID = 1L;
	Thread myThread;
	boolean isStopped;
	private Color color[];
	
	private int nx = 50, ny = 50;
	private NNSandpile model = new NNSandpile(nx, ny);
	private int numberOfDroppedGrain;
	
	
	
	public void init () {
		myThread = null;
		color = new Color[12];
		//color[0] = new Color(255, 255, 255);
		color[0] = new Color(20, 20, 80);
		for (int i=1; i<10; i++) {
			color[i] = color[i-1].brighter();
		}
		numberOfDroppedGrain=0;
	}
	
	public void start () {
		myThread = new Thread(this);
		isStopped = false;
		myThread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stop () {
		isStopped = true;
		myThread.stop();
	}
	
	public void destroy () {
		
	}
	
	public void paint (Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		int w = width / ny;
		int h = height / nx;
		for (int i=0; i<nx; i++) {
			for (int j=0; j<ny; j++) {
				g.setColor(color[model.GetElement(i, j)]);
				g.fillRect(j*w, i*h, w, h);
			}
		}
	}

	@Override
	public void run() {
		for ( ; ; ) {
			try {
				repaint ();
				showStatus("Dropped sand grain #"+ ++numberOfDroppedGrain);
				Thread.sleep(500);
				model.DropSandGrain();
				if (isStopped) {
					break;
				}
			} catch (InterruptedException e) {
				//............................
			}
		}
	}
}

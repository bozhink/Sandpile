package btw;

import java.awt.Color;
import java.awt.Graphics;
import java.applet.Applet;
import btw.model.BTWModel;


/*
<applet code="BTWApplet" width=600 height=600 />
*/

public class BTWApplet extends Applet implements Runnable
{
	private static final long serialVersionUID = 1L;
	Thread myThread;
	boolean isStopped;
	private Color color[];
	
	int nx = 51, ny = 51;
	BTWModel model = new BTWModel(nx, ny);
	
	public void init () {
		myThread = null;
		color = new Color[10];
		color[0] = new Color(255,255,255);
		for (int i=1; i<10; i++) {
			color[i] = color[i-1].darker();
		}
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
		int w = width / (ny-1);
		int h = height / (nx-1);
		for (int i=0; i<model.nx; i++) {
			for (int j=0; j<model.ny; j++) {
				g.setColor(color[model.matrix[i][j]]);
				g.fillRect(j*w, i*h, w, h);
			}
		}
	}

	@Override
	public void run() {
		for ( ; ; ) {
			try {
				repaint ();
				Thread.sleep(500);
				model.Update();
				if (isStopped) {
					break;
				}
			} catch (InterruptedException e) {
				//............................
			}
		}
	}
}

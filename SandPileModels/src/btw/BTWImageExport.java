package btw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import btw.model.BTWModel;

public class BTWImageExport {
	
	static int MAX_ITERATIONS = 3400;
	static int WIDTH = 600;
	static int HEIGHT = 600;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Color color[] = new Color[10];
		color[0] = new Color(255,255,255);
		for (int i=1; i<10; i++) {
			color[i] = color[i-1].darker();
		}
		
		int nx = 51;
		int ny = 51;
		BTWModel model = new BTWModel(nx, ny);
		
		String fileName;
		
		for (int iteration=0; iteration<MAX_ITERATIONS; iteration++) {
			model.Update();
			fileName = String.format("btw-%1$06d.png", iteration);
			int w = WIDTH / ny;
			int h = HEIGHT / nx;
			
			try {
				BufferedImage bi = new BufferedImage(WIDTH, HEIGHT,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				for (int i=0; i<model.nx; i++) {
					for (int j=0; j<model.ny; j++) {
						g.setColor(color[model.matrix[i][j]]);
                        g.fillRect(j*w, i*h, w, h);
					}
				}
				ImageIO.write(bi, "PNG", new File(fileName));
				System.out.print("\r"+fileName+" is written.");
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		System.err.println("\nEnd of simulation.");
	}

}

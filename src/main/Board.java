package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
	
	final int ROW = 8;
	final int COL = 8;
	public static final int SQUARE_SIZE = 100;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;
	private Color whiteSquareColor = new Color(235,236,208);
	private Color blackSquareColor = new Color(119,149,86);
	
	public void drawSquares(Graphics2D g2) {
		
		int currentColor = 0;
		
		for(int i = 0; i < COL; i++) {
			for(int j = 0; j < ROW; j++) {
				if(currentColor == 0) {
					g2.setColor(whiteSquareColor);
					currentColor = 1;
				}
				else {
					g2.setColor(blackSquareColor);
					currentColor = 0;
				}
				g2.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
			if(currentColor == 0) {
				currentColor = 1;
			}
			else {
				currentColor = 0;
			}		
		}
		
	}

}

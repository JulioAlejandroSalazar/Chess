package piece;

import main.MainPanel;

public class Knight extends Piece {
	
	public Knight(int col, int row, int color) {
		super(col, row, color);
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_knight");
		}
		else {
			image = getImage("/pieces/black_knight");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		if(isWithinBoard(col, row)) {
			if(Math.abs(col - preCol) * Math.abs(row - preRow) == 2) {
				if(isSquareOccupied(col, row)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

}

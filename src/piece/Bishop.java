package piece;

import main.MainPanel;

public class Bishop extends Piece {
	
	public Bishop(int col, int row, int color) {
		super(col, row, color);
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_bishop");
		}
		else {
			image = getImage("/pieces/black_bishop");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		if(isWithinBoard(col, row)) {
			if(Math.abs(preCol - col) == (Math.abs(preRow - row))) {
				if(isSquareOccupied(col, row) && !isSameSquare(col, row) && !isHittingPieceInDiagonal(col, row)) {
					return true;
				}
			}
		}
		return false;
	}

}

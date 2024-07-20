package piece;

import main.MainPanel;

public class Queen extends Piece {
	
	public Queen(int col, int row, int color) {
		super(col, row, color);
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_queen");
		}
		else {
			image = getImage("/pieces/black_queen");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		if(isWithinBoard(col, row)) {
			if((preCol == col || preRow == row) && (col < 8 || row < 8)) {
				if(isSquareOccupied(col, row) && !isSameSquare(col, row) && !isHittingPieceInLine(col, row)) {
					return true;
				}
			}
			else if(Math.abs(preCol - col) == (Math.abs(preRow - row))) {
				if(isSquareOccupied(col, row) && !isSameSquare(col, row) && !isHittingPieceInDiagonal(col, row)) {
					return true;
				}
			}
		}
		return false;
	}

}


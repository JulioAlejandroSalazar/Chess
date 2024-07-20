package piece;

import main.MainPanel;

public class Rook extends Piece {

	public Rook(int col, int row, int color) {
		super(col, row, color);
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_rook");
		}
		else {
			image = getImage("/pieces/black_rook");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		if(isWithinBoard(col, row)) {
			if((preCol == col || preRow == row) &&
				(col < 8 || row < 8)) {
				if(isSquareOccupied(col, row) && !isSameSquare(col, row) && !isHittingPieceInLine(col, row)) {
					return true;
				}
			}
		}
		return false;
	}
	
}

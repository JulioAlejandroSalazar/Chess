package piece;

import main.MainPanel;

public class Pawn extends Piece {
	
	public boolean firstMove;

	public Pawn(int col, int row, int color) {
		super(col, row, color);
		firstMove = true;
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_pawn");
		}
		else {
			image = getImage("/pieces/black_pawn");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		
		if(isWithinBoard(col, row) && !isSameSquare(col, row)) {
			int direction;
			if(this.color == 0) {
				direction = -1;
			}
			else {
				direction = 1;
			}
			
				hittingP = getHittingPiece(col, row);
			
			//1 square
			if(preCol == col && preRow + direction == row && hittingP == null) {
				return true;
			}
			
			//2 squares
			if(preCol == col && preRow + direction*2 == row && !isMoved
				&& hittingP == null && !isHittingPieceInLine(col, row)) {
				return true;
			}
			
			//capture
			if(Math.abs(preCol - col) == 1 && preRow + direction == row
				&& hittingP != null && hittingP.color != this.color) {
				return true;
			}
			
			//capture enPassant
			if(Math.abs(preCol - col) == 1 && preRow + direction == row) {
				for(Piece p : MainPanel.currentPieces) {
					if(p.col == col && p.row == preRow && p.isTwoStepped) {
						hittingP = p;
						return true;
					}
				}
			}
		}
			
		return false;
	}

}

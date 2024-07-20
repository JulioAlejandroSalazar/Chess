package piece;

import main.MainPanel;

public class King extends Piece {
	
	public King(int col, int row, int color) {
		super(col, row, color);
		
		if(color == MainPanel.WHITE) {
			image = getImage("/pieces/white_king");
		}
		else {
			image = getImage("/pieces/black_king");
		}
	}
	
	@Override
	public boolean checkMove(int col, int row) {
		if(isWithinBoard(col, row)) {
			if((Math.abs(col - preCol) + Math.abs(row - preRow) == 1) ||	//up, down, left and right
				(Math.abs(col - preCol) * Math.abs(row - preRow) == 1)) {	//corners
				if(isSquareOccupied(col, row)) {
					return true;
				}
			}
			
			//castling
			if(!isMoved) {
				
				//right
				if(col == preCol + 2 && row == preRow && !isHittingPieceInLine(col, row)) {
					for(Piece p : MainPanel.currentPieces) {
						if(p.col == preCol + 3 && p.row == row && !p.isMoved) {
							MainPanel.castlingPiece = p;
							return true;
						}
					}
				}
				
				//left
				if(col == preCol - 2 && row == preRow && !isHittingPieceInLine(col, row)) {
					Piece rook = null, knight = null;
					for(Piece p : MainPanel.currentPieces) {
						if(p.col == preCol - 3 && p.row == row) {
							knight = p;
						}
						if(p.col == preCol - 4 && p.row == row) {
							rook = p;
						}
						if(knight == null && rook != null && !rook.isMoved) {
							MainPanel.castlingPiece = rook;
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}


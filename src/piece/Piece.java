package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import main.Board;
import main.MainPanel;

public class Piece extends JPanel {
	
	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public boolean isValidMove, isMoved, isTwoStepped;
	public Piece hittingP;
	
	public Piece(int col, int row, int color) {
		
		this.col = col;
		this.row = row;
		this.color = color;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
		isValidMove = false;
		isMoved = false;
		
	}
	
	public int getIndex() {
		for(int i = 0; i < MainPanel.currentPieces.size(); i++) {
			if(MainPanel.currentPieces.get(i) == this) {
				return i;
			}
		}
		return 0;
	}
	
	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	
	public int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}
	
	public int getCol(int x) {
		return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	
	public void updateCoor() {
		
		if(this.getClass() == piece.Pawn.class) {
			if(Math.abs(row - preRow) == 2) {
				isTwoStepped = true;
			}
		}
		
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		isMoved = true;
	}
	
	public void resetCoor() {
		x = getX(preCol);
		y = getY(preRow);
		col = preCol;
		row = preRow;
	}
	
	public boolean checkMove(int col, int row) {
		return isValidMove;
	}
	
	public boolean isWithinBoard(int col, int row) {
		if((col >= 0 && col <=7) && (row >= 0 && row <= 7)) {
			return true;
		}
		return false;
	}
	
	public Piece getHittingPiece(int col, int row) {
		for(Piece p : MainPanel.currentPieces) {
			if(p.col == col && p.row == row && p != this) {
				return p;
			}
		}
		return null;
	}
	
	public boolean isSameSquare(int col, int row) {
		if(preCol == col && preRow == row) {
			return true;
		}
		return false;
	}
	
	public boolean isSquareOccupied(int col, int row) {
		
		hittingP = getHittingPiece(col, row);
		if(hittingP == null) {	//vacant
//			System.out.println("empty");
			return true;
		}
		else if(hittingP.color != this.color) {
//			System.out.println("!empty");
			return true;
		}
		return false;
	}
	
	public boolean isHittingPieceInLine(int col, int row) {
		//check up
		for(int r = preRow-1; r > row; r--) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == col && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check down
		for(int r = preRow+1; r < row; r++) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == col && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check left
		for(int c = preCol-1; c > col; c--) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == row) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check right
		for(int c = preCol+1; c < col; c++) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == row) {
					hittingP = p;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isHittingPieceInDiagonal(int col, int row) {
		//check up-left
		for(int c = preCol-1, r = preRow-1; c > col && r > row; c--, r--) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check up-right
		for(int c = preCol+1, r = preRow-1; c < col && r > row; c++, r--) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check down-left
		for(int c = preCol-1, r = preRow+1; c > col && r < row; c--, r++) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		
		//check down-right
		for(int c = preCol+1, r = preRow+1; c < col && r < row; c++, r++) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == c && p.row == r) {
					hittingP = p;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isValidPawnCapture(int col, int row) {
		if(Math.abs(preCol - col) == 1 && preRow - row == 1) {
			for(Piece p : MainPanel.currentPieces) {
				if(p.col == col && p.row == row) {
					return true;
				}				
			}
		}
		return false;
	}
	
	public BufferedImage getImage(String path) {
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path + ".png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return image;
		
	}
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}

}

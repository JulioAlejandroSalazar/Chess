package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import piece.*;

public class MainPanel extends JPanel implements Runnable {
	
	private static final int WIDTH = 1100;
	private static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	private static Board board = new Board();
	private static Mouse mouse = new Mouse();
	
	//Color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	public static int currentTurn = WHITE;
	
	//Pieces
	public static ArrayList<Piece> originalPieces = new ArrayList<>();
	public static ArrayList<Piece> currentPieces = new ArrayList<>();
	ArrayList<Piece> promoPieces = new ArrayList<>();
	public static Piece castlingPiece;
	Piece currentPiece, checkingPiece;
	
	//Boolean
	private static boolean isValidMove, isPromoting, isGameOver, isStaleMate;
	
	public MainPanel() {
		initializeComponents();
		initializeOriginalPieces();
//		initializeCustomScenario();
		initializeCurrentPieces(originalPieces, currentPieces);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	}
 	
	public void initializeComponents() {		
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(new Color(47, 46, 42));
	}
	
	public void initializeOriginalPieces() {
		
		//White
		originalPieces.add(new Pawn(0,6,WHITE));
		originalPieces.add(new Pawn(1,6,WHITE));
		originalPieces.add(new Pawn(2,6,WHITE));
		originalPieces.add(new Pawn(3,6,WHITE));
		originalPieces.add(new Pawn(4,6,WHITE));
		originalPieces.add(new Pawn(5,6,WHITE));
		originalPieces.add(new Pawn(6,6,WHITE));
		originalPieces.add(new Pawn(7,6,WHITE));
		originalPieces.add(new Rook(0,7,WHITE));
		originalPieces.add(new Rook(7,7,WHITE));
		originalPieces.add(new Knight(1,7,WHITE));
		originalPieces.add(new Knight(6,7,WHITE));
		originalPieces.add(new Bishop(2,7,WHITE));
		originalPieces.add(new Bishop(5,7,WHITE));
		originalPieces.add(new King(4,7,WHITE));
		originalPieces.add(new Queen(3,7,WHITE));
		
		//Black
		originalPieces.add(new Pawn(0,1,BLACK));
		originalPieces.add(new Pawn(1,1,BLACK));
		originalPieces.add(new Pawn(2,1,BLACK));
		originalPieces.add(new Pawn(3,1,BLACK));
		originalPieces.add(new Pawn(4,1,BLACK));
		originalPieces.add(new Pawn(5,1,BLACK));
		originalPieces.add(new Pawn(6,1,BLACK));
		originalPieces.add(new Pawn(7,1,BLACK));
		originalPieces.add(new Rook(0,0,BLACK));
		originalPieces.add(new Rook(7,0,BLACK));
		originalPieces.add(new Knight(1,0,BLACK));
		originalPieces.add(new Knight(6,0,BLACK));
		originalPieces.add(new Bishop(2,0,BLACK));
		originalPieces.add(new Bishop(5,0,BLACK));
		originalPieces.add(new King(4,0,BLACK));
		originalPieces.add(new Queen(3,0,BLACK));
		
	}
	
	public void initializeCustomScenario() {
		
		originalPieces.add(new King(4,0,BLACK));
		originalPieces.add(new Knight(6,2,BLACK));
		originalPieces.add(new Bishop(7,3,BLACK));
		originalPieces.add(new Queen(2,5,WHITE));
		originalPieces.add(new Rook(1,1,WHITE));
		originalPieces.add(new King(1,0,WHITE));
		
	}
	
	private void initializeCurrentPieces(ArrayList<Piece> original, ArrayList<Piece> current) {
		
		current.clear();
		for(Piece piece : original) {
			current.add(piece);
		}
		
	}
	
	public void launchGame() {
		
		gameThread = new Thread(this);
		gameThread.start();
		
	}
	
	@Override
	public void run() {
		
		double drawInterval = 1000000000/FPS;	//FPS in nanoseconds
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		while(gameThread != null) {
			
			update();
			repaint();
			
			try {
				
				double remainingTime  = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				
				Thread.sleep((long)remainingTime);
				nextDrawTime += drawInterval;
								
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void update() {
		
		if(isPromoting) {
			promote();
		}
		else if(!isGameOver && !isStaleMate) {
			if(mouse.isPressed) {
				if(currentPiece == null) {	//the player is not holding any piece
					for(Piece p : currentPieces) {
						if(		//check if the current position of the mouse matches any piece
							p.col == mouse.x/Board.SQUARE_SIZE &&
							p.row == mouse.y/Board.SQUARE_SIZE
						) {
							currentPiece = p;
						}						
					}
				}
				else {	//the player is holding a piece
					simulate();
				}
			}
			else {
				if(currentPiece != null) {
					if(isValidMove && currentPiece.color == currentTurn) {	//valid move
						initializeCurrentPieces(currentPieces, originalPieces);
						currentPiece.updateCoor();
						if(castlingPiece != null) {
							castlingPiece.updateCoor();
						}
						if(isKingInCheck() && isCheckMate()) {
							isGameOver = true;
						}
						if(isStaleMate() && !isKingInCheck() ) {
							isStaleMate = true;
						}
						else {
							if(canPromote()) {
								isPromoting = true;
							}
							else {
								changeTurn();
							}
						}			

					}
					else {		//invalid move, reset the piece
						initializeCurrentPieces(originalPieces, currentPieces);
						currentPiece.resetCoor();
						if(castlingPiece != null) {
							castlingPiece.resetCoor();
						}
						currentPiece = null;
					}
				}
			}
		}

	}
	
	private void simulate() {
		
		isValidMove = false;
		initializeCurrentPieces(originalPieces, currentPieces);
		
		//reset castling every loop
		if(castlingPiece != null) {
			castlingPiece.col = castlingPiece.preCol;
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
			castlingPiece = null;
		}
		
		currentPiece.x = mouse.x - Board.HALF_SQUARE_SIZE;	//this sets the pointer to the middle of the piece
		currentPiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
		currentPiece.col = currentPiece.getCol(currentPiece.x);
		currentPiece.row = currentPiece.getRow(currentPiece.y);
		
		if(currentPiece.checkMove(currentPiece.col, currentPiece.row)) {
			if(!checkIllegalMove(currentPiece) && !isOpponentCheckingUs()) {	//////
//				System.out.println("!illegal && !checkingus");
				isValidMove = true;
			}
			if(currentPiece.hittingP != null) {
				currentPieces.remove(currentPiece.hittingP.getIndex());
//				System.out.println("hittingP");
			}
			checkCastling();
		}
		
	}
	
	public void changeTurn() {
		
		if(currentTurn == WHITE) {
			currentTurn = BLACK;
			for(Piece p : currentPieces) {
				if(p.color == BLACK) {
					p.isTwoStepped = false;
				}
			}
		}
		else {
			currentTurn = WHITE;
			for(Piece p : currentPieces) {
				if(p.color == WHITE) {
					p.isTwoStepped = false;
				}
			}
		}
		currentPiece = null;
		
	}
	
	public boolean isKingInCheck() {
		
		Piece king = getKing(true);
		if(currentPiece.checkMove(king.col, king.row)) {
			checkingPiece = currentPiece;
			return true;
		}
		else {
			checkingPiece = null;
		}
		return false;
		
	}
	
	public Piece getKing(boolean oppositeColor) {

		Piece king = null;
		for(Piece p : currentPieces) {
			if(oppositeColor) {
				if(p.getClass() == piece.King.class && p.color != currentTurn) {
					king = p;
				}
			}
			else {
				if(p.getClass() == piece.King.class && p.color == currentTurn) {
					king = p;
				}
			}
			
		}
		return king;
		
	}
	
	public boolean isOpponentCheckingUs() {
		
		Piece king = getKing(false);
		for(Piece p : currentPieces) {
			if(p.color != king.color && p.checkMove(king.col, king.row)) {
				return true;
			}
		}
		return false;
		
	}
	
	public boolean checkIllegalMove(Piece king) {
		
		if(king.getClass() == piece.King.class) {
			for(Piece p : currentPieces) {
				if(p.getClass() != piece.King.class && p.color != king.color &&
					p.checkMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	public void checkCastling() {
		
		if(castlingPiece != null) {
			if(castlingPiece.col == 7) {
				castlingPiece.col -= 2;
			}
			else if(castlingPiece.col == 0) {
				castlingPiece.col += 3;
			}
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
		}
		
	}
	
	public boolean canPromote() {
		
		if(currentPiece.getClass() == piece.Pawn.class) {
			if((currentPiece.color == WHITE && currentPiece.row == 0) ||
				(currentPiece.color == BLACK && currentPiece.row == 7)) {
				promoPieces.clear();
				promoPieces.add(new Queen(9,2,currentPiece.color));
				promoPieces.add(new Rook(9,3,currentPiece.color));
				promoPieces.add(new Bishop(9,4,currentPiece.color));
				promoPieces.add(new Knight(9,5,currentPiece.color));
				return true;
			}
		}
		
		return false;
		
	}
	
	public void promote() {
		
		if(mouse.isPressed) {
			for(Piece p : promoPieces) {
				if(p.col == mouse.x/Board.SQUARE_SIZE && p.row == mouse.y/Board.SQUARE_SIZE) {
					if(p.getClass() == piece.Queen.class) {
						currentPieces.add(new Queen(currentPiece.col, currentPiece.row, currentPiece.color));
					}
					else if(p.getClass() == piece.Rook.class) {
						currentPieces.add(new Rook(currentPiece.col, currentPiece.row, currentPiece.color));
					}
					else if(p.getClass() == piece.Bishop.class) {
						currentPieces.add(new Bishop(currentPiece.col, currentPiece.row, currentPiece.color));
					}
					else if(p.getClass() == piece.Knight.class) {
						currentPieces.add(new Knight(currentPiece.col, currentPiece.row, currentPiece.color));
					}
					currentPieces.remove(currentPiece.getIndex());
					initializeCurrentPieces(currentPieces, originalPieces);
					currentPiece = null;
					isPromoting = false;
					changeTurn();
				}
			}
		}
		
	}
	
	public boolean isStaleMate() {
		
		int count = 0;
		
		for(Piece p : currentPieces) {
			if(p.color != currentTurn) {
				count++;
			}
		}
		
		if(count == 1) {	//only the king is left
			if(!isKingAbleToMove(getKing(true))) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean isCheckMate() {
		
		Piece king = getKing(true);
		
		if(isKingAbleToMove(king)) {
			return false;
		}
		else {
			int difCol = Math.abs(checkingPiece.col - king.col);
			int difRow = Math.abs(checkingPiece.row - king.row);
			
			if(difCol == 0) {	//attacking piece is attacking vertically
				if(checkingPiece.row < king.row) {	//attacking piece is above
					for(int row = checkingPiece.row; row < king.row; row++) {
						for(Piece p : currentPieces) {
							if(p != king && p.color != currentTurn && p.checkMove(checkingPiece.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingPiece.row > king.row) {	//attacking piece is below
					for(int row = checkingPiece.row; row > king.row; row--) {
						for(Piece p : currentPieces) {
							if(p != king && p.color != currentTurn && p.checkMove(checkingPiece.col, row)) {
								return false;
							}
						}
					}
				}
			}
			
			else if(difRow == 0) {	//attacking piece is attacking horizontally
				if(checkingPiece.col < king.col) {	//attacking piece is to the left
					for(int col = checkingPiece.col; col < king.col; col++) {
						for(Piece p : currentPieces) {
							if(p != king && p.color != currentTurn && p.checkMove(col, checkingPiece.row)) {
								return false;
							}
						}
					}
				}
				if(checkingPiece.col > king.col) {	//attacking piece is to the right
					for(int col = checkingPiece.col; col > king.col; col--) {
						for(Piece p : currentPieces) {
							if(p != king && p.color != currentTurn && p.checkMove(col, checkingPiece.row)) {
								return false;
							}
						}
					}
				}
			}
			
			else if(difCol == difRow) {	//attacking piece is attacking diagonally
				if(checkingPiece.row < king.row) {	//attacking piece is above
					if(checkingPiece.col < king.col) {	//attacking piece is in the upper-left
						for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++) {
							for(Piece p : currentPieces) {
								if(p != king && p.color != currentTurn && p.checkMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingPiece.col > king.col) {	//attacking piece is in the upper-right
						for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++) {
							for(Piece p : currentPieces) {
								if(p != king && p.color != currentTurn && p.checkMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				
				if(checkingPiece.row > king.row) {	//attacking piece is below
					if(checkingPiece.col < king.col) {	//attacking piece is in the bottom-left
						for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--) {
							for(Piece p : currentPieces) {
								if(p != king && p.color != currentTurn && p.checkMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingPiece.col > king.col) {	//attacking piece is in the bottom-right
						for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--) {
							for(Piece p : currentPieces) {
								if(p != king && p.color != currentTurn && p.checkMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}
			
			else {	//the attacking piece is a knight
				for(Piece p : currentPieces) {
					if(p != king && p.color != currentTurn && p.checkMove(checkingPiece.col, checkingPiece.row)) {
						return false;
					}
				}
			}
		}
		
		return true;
		
	}
	
	public boolean isKingAbleToMove(Piece king) {
		
		if(isAValidMove(king, -1, -1)) { return true; }
		if(isAValidMove(king, 0, -1)) { return true; }
		if(isAValidMove(king, 1, -1)) { return true; }
		if(isAValidMove(king, 1, 0)) { return true; }
		if(isAValidMove(king, 1, 1)) { return true; }
		if(isAValidMove(king, 0, 1)) { return true; }
		if(isAValidMove(king, -1, 1)) { return true; }
		if(isAValidMove(king, -1, 0)) { return true; }
		
		return false;
		
	}
	
	public boolean isAValidMove(Piece king, int col, int row) {
		
		boolean isAValidMove = false;
		
		king.col = col;
		king.row = row;
		
		if(king.checkMove(king.col, king.row)) {
			if(king.hittingP != null) {
				currentPieces.remove(king.hittingP.getIndex());
			}
			if(!checkIllegalMove(king)) {
				isAValidMove = true;
			}
		}
		
		king.resetCoor();
		initializeCurrentPieces(originalPieces, currentPieces);
		return isAValidMove;
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D) g;
		board.drawSquares(g2D);
		
		for(Piece pieces : currentPieces) {
			pieces.draw(g2D);
		}
		
		if(currentPiece != null) {
			if(isValidMove && !isOpponentCheckingUs()) {	//////////////////////
				g2D.setColor(Color.white);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2D.fillRect(currentPiece.col * Board.SQUARE_SIZE,
							currentPiece.row * Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				currentPiece.draw(g2D);
			}
		}
		
		//current turn
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.setFont(new Font("Calibri", Font.PLAIN, 40));
		g2D.setColor(Color.white);
		
		if(isPromoting) {
			g2D.drawString("Promote to:", 850, 150);
			for(Piece p : promoPieces) {
				g2D.drawImage(p.image, p.getX(p.col), p.getY(p.row),
				Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		else {
			if(currentTurn == WHITE) {
				g2D.drawString("White's turn", 850, 550);
				if(checkingPiece != null && checkingPiece.color == BLACK) {
					g2D.drawString("Check", 890, 650);
				}
			}
			else {
				g2D.drawString("Black's turn", 850, 250);
				if(checkingPiece != null && checkingPiece.color == WHITE) {
					g2D.drawString("Check", 890, 150);
				}
			}
		}
		if(isGameOver) {
			String victory = "";
			if(currentTurn != WHITE) {
				victory = "White wins";
			}
			else {
				victory = "Black wins";
			}
			g2D.drawString(victory, 850, 400);
		}
		if(isStaleMate) {
			g2D.setColor(new Color(47, 46, 42));
			g2D.fillRect(800, 0, 300, 800);
			g2D.setColor(Color.white);
			g2D.drawString("Draw", 900, 400);
		}
	}

}

import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * A class that represents a chess board.
 * 
 * @author jstenglein 11/19/16
 */
public class Board {

	// class constants
	public static final int MIN_ROW = 0, MIN_COL = 0;
	public static final int MAX_ROW = 7, MAX_COL = 7;
	
	// starting columns for pieces
	private static final int[] ROOK_COLUMNS = new int[] {MIN_COL, MAX_COL};
	private static final int[] KNIGHT_COLUMNS = new int[] {1, 6};
	private static final int[] BISHOP_COLUMNS = new int[] {2, 5};
	private static final int QUEEN_COLUMN = 3;
	private static final int KING_COLUMN = 4;

	// instance variables
	private Space[][] spaces;
	private ArrayList<Piece> blackPieces;
	private ArrayList<Piece> whitePieces;
	private Piece blackKing;
	private Piece whiteKing;
	
	
	/**
	 * Creates a new Board object with pieces in their starting location.
	 */
	public Board() {
		spaces = new Space[MAX_ROW + 1][MAX_COL + 1];
		for (int r = 0; r <= MAX_ROW; r++) {
			for (int c = 0; c <= MAX_COL; c++) {
				spaces[r][c] = new Space(r, c);
			}
		}
		whitePieces = createPieces(true);
		whiteKing = whitePieces.get(whitePieces.size() - 1);
		blackPieces = createPieces(false);
		blackKing = blackPieces.get(blackPieces.size() - 1);
	}

	// initializes a board with the same spaces, blackPieces, whitePieces as
	// board
	public Board(Board board) {

	}
	
	private ArrayList<Piece> createPieces(boolean isWhite) {
		
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		pieces.addAll(createPawns(isWhite));

		int row = isWhite ? MAX_ROW : MIN_ROW;
		
		// create the knights
		for (int col : KNIGHT_COLUMNS) {
			Piece piece = new Piece(Piece.PieceType.KNIGHT, isWhite, row, col);
			pieces.add(piece);
			spaces[row][col].setPiece(piece);
		}

		// create the bishops
		for (int col : BISHOP_COLUMNS) {
			Piece piece = new Piece(Piece.PieceType.BISHOP, isWhite, row, col);
			pieces.add(piece);
			spaces[row][col].setPiece(piece);
		}

		// create the rooks
		for (int col : ROOK_COLUMNS) {
			Piece piece = new Piece(Piece.PieceType.ROOK, isWhite, row, col);
			pieces.add(piece);
			spaces[row][col].setPiece(piece);
		}

		// create the queen
		Piece piece = new Piece(Piece.PieceType.QUEEN, isWhite, row, QUEEN_COLUMN);
		pieces.add(piece);
		spaces[row][QUEEN_COLUMN].setPiece(piece);

		// create the king
		piece = new Piece(Piece.PieceType.KING, isWhite, row, KING_COLUMN);
		pieces.add(piece);
		spaces[row][KING_COLUMN].setPiece(piece);

		return pieces;
	}

	private ArrayList<Piece> createPawns(boolean isWhite) {
		ArrayList<Piece> pawns = new ArrayList<Piece>();

		int row = isWhite ? MAX_ROW - 1 : MIN_ROW + 1;
		for (int c = 0; c <= Board.MAX_COL; c++) {
			Piece pawn = new Piece(Piece.PieceType.PAWN, isWhite, row, c);
			pawns.add(pawn);
			spaces[row][c].setPiece(pawn);
		}

		return pawns;
	}

	/**
	 * Set the Space at the specified row and col. <br>
	 * pre: MIN_ROW < row < MAX_ROW && MIN_COL < col < MAX_COL && space != null
	 * 
	 * @param row
	 *            The row to set the Space at.
	 * @param col
	 *            The col to set the Space at.
	 * @param space
	 *            The Space to insert into the Board. May not be null.
	 */
	public void setSpace(int row, int col, Space space) {

		// check preconditions
		if (row < MIN_ROW || row > MAX_ROW || col < MIN_COL || col > MAX_COL)
			throw new IllegalArgumentException("Row/col are not within bounds. Row: " + row + ", col: " + col);
		else if (space == null)
			throw new IllegalArgumentException("The Space may not be null.");

		spaces[row][col] = space;
	}

	/**
	 * Return the Space at the specified row and col. <br>
	 * pre: MIN_ROW <= row <= MAX_ROW && MIN_COL <= col <= MAX_COL
	 * 
	 * @param row
	 *            The row to get the Space at.
	 * @param col
	 *            The col to get the Space at.
	 * @return The Space at the specified row and col
	 */
	public Space getSpace(int row, int col) {
		// check if space is off board
		if (row < MIN_ROW || row > MAX_ROW || col < MIN_COL || col > MAX_COL)
			return null;

		return spaces[row][col];
	}

	/**
	 * Update the Board to make the specified Move. Check for pawn promotion and
	 * update the Piece that last moved.<br>
	 * pre: move != null
	 * 
	 * @param move
	 *            The Move to make. May not be null.
	 */
	public void makeOfficialMove(Move move) {
		makeTestMove(move);
		// set all other color's pieces' movedLastTurn to false
		resetMovedLastTurn(move.getMovedPiece().isWhite());

		// check if pawn needs promotion
		checkPawnPromotion(move.getMovedPiece());
	}

	/**
	 * Update the Board to make the specified Move, but do not reset who moved
	 * last turn or check if a pawn needs promotion. <br>
	 * pre: move != null
	 * 
	 * @param move
	 *            The Move to make. May not be null.
	 */
	public void makeTestMove(Move move) {

		// check precondition
		if (move == null)
			throw new IllegalArgumentException("The Move to make may not be null.");

		// get the necessary info
		Piece movedPiece = move.getMovedPiece();
		Piece capturedPiece = move.getCapturedPiece();
		Space source = move.getSource();
		Space dest = move.getDestination();

		// change the source's piece to null
		source.setPiece(null);

		// check if this is a castle, call helper method if so
		if (movedPiece.getType() == Piece.PieceType.KING
				&& (source.getCol() - dest.getCol() == 2 || source.getCol() - dest.getCol() == -2))
			castle(source, dest);

		// actually make the move
		makeMove(movedPiece, capturedPiece, dest);
	}

	/**
	 * Helper method for makeMove() that actually moves the Piece to the correct
	 * destination and captures the Piece if one exists.<br>
	 * pre: movedPiece != null, dest != null
	 * 
	 * @param movedPiece
	 *            The Piece that is being moved. May not be null.
	 * @param capturedPiece
	 *            The Piece that is being captured. May be null.
	 * @param dest
	 *            The Space to move movedPiece to. May not be null.
	 */
	private void makeMove(Piece movedPiece, Piece capturedPiece, Space dest) {

		// check preconditions
		if (movedPiece == null || dest == null)
			throw new IllegalArgumentException("movedPiece and dest may not be null.");

		// capture a Piece if one exists to be captured
		if (capturedPiece != null) {
			capturedPiece.setCaptured(true);
			spaces[capturedPiece.getRow()][capturedPiece.getCol()].setPiece(null);
		}

		movedPiece.setRow(dest.getRow());
		movedPiece.setCol(dest.getCol());
		movedPiece.incrementTimesMoved();
		movedPiece.setHasJustMoved(true);
		//System.out.println("Moved piece: " + movedPiece);

		dest.setPiece(movedPiece);
		//System.out.println("New space: " + dest);
	}

	/**
	 * Helper method for makeMove() that places the rook in the right spot when
	 * castling. <br>
	 * pre: source != null, dest != null <br>
	 * post: the rook involved in the castle is moved to the correct Space.
	 * 
	 * @param source
	 *            The Space that the king is moving from. May not be null.
	 * @param dest
	 *            The Space that the king is moving to. May not be null.
	 */
	private void castle(Space source, Space dest) {
		
		//System.out.println("CASTLE");

		// check preconditions
		if (source == null || dest == null)
			throw new IllegalArgumentException("Source and dest may not be null.");

		// first get the correct rook
		Space rookSpace;
		int changeFromDest;
		if (source.getCol() > dest.getCol()) {
			rookSpace = spaces[source.getRow()][Board.MIN_COL];
			changeFromDest = 1;
		} else {
			rookSpace = spaces[source.getRow()][Board.MAX_COL];
			changeFromDest = -1;
		}

		Piece rookToMove = rookSpace.getPiece();
		rookSpace.setPiece(null);
		// board_.setSpace(rookSpace.getRow(), rookSpace.getCol(),
		// rookSpace);

		// move the rook
		rookToMove.setCol(dest.getCol() + changeFromDest);
		rookToMove.incrementTimesMoved();
		Space newRookSpace = spaces[dest.getRow()][dest.getCol() + changeFromDest];
		newRookSpace.setPiece(rookToMove);
		
		// reset all Spaces to not be selected
		for(int r = 0; r <= MAX_ROW; r++)
			for(int c = 0; c <= MAX_COL; c++)
				spaces[r][c].setTakingMove(false);
	}

	/**
	 * Helper method for makeMove() that resets movedLastTurn for the given
	 * color's Pieces.
	 * 
	 * @param isBlack A boolean indicating whether to reset black's pieces.
	 */
	private void resetMovedLastTurn(boolean isBlack) {
		ArrayList<Piece> otherPieces = isBlack ? blackPieces : whitePieces;

		for (int i = 0; i < otherPieces.size(); i++)
			otherPieces.get(i).setHasJustMoved(false);
	}

	/**
	 * Helper method for makeMove() that checks if a pawn needs to be promoted.
	 * If it does, display a JOptionPane that allows the user to select their
	 * promotion choice. <br>
	 * pre: movedPiece != null, dest != null
	 * 
	 * @param movedPiece
	 *            The Piece that is being moved. May not be null.
	 */
	private void checkPawnPromotion(Piece movedPiece) {

		// check precondition
		if (movedPiece == null)
			throw new IllegalArgumentException("movedPiece may not be null.");

		if (movedPiece.getType() == Piece.PieceType.PAWN
				&& (movedPiece.getRow() == Board.MIN_ROW || movedPiece.getRow() == Board.MAX_ROW)) {

			// display the JOptionPane
			Object[] buttons = { "Knight", "Bishop", "Rook", "Queen" };
			int selectedValue = JOptionPane.showOptionDialog(null, "Which piece would you like?", "Promote pawn",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[3]);

			// the user actually made a choice
			if (selectedValue > -1)
				movedPiece.promote(Piece.PieceType.valueOf(((String)buttons[selectedValue]).toUpperCase()));

			// otherwise, they cancelled, so
			// just make the default value a queen
			else
				movedPiece.promote(Piece.PieceType.QUEEN);

			movedPiece.setPromoted(true);
		}
	}

	/**
	 * Update the Board to undo the specified Move. <br>
	 * pre: move != null
	 * 
	 * @param move
	 *            The Move to undo. May not be null.
	 */
	public void undoMove(Move move) {
		// check precondition
		if (move == null)
			throw new IllegalArgumentException("The move may not be null.");

		// get the necessary info
		Piece movedPiece = move.getMovedPiece();
		Piece capturedPiece = move.getCapturedPiece();
		Space source = move.getSource();
		Space dest = move.getDestination();
		dest.setPiece(null);
		
		// check if this is a castle, call helper method if so
		if (movedPiece.getType() == Piece.PieceType.KING
				&& (source.getCol() - dest.getCol() == 2 || source.getCol() - dest.getCol() == -2))
					uncastle(source, dest);

		// restore a Piece if one was captured
		else if (capturedPiece != null) {
			//pieceRestored(capturedPiece);
			capturedPiece.setCaptured(false);
			spaces[capturedPiece.getRow()][capturedPiece.getCol()].setPiece(capturedPiece);
		}

		movedPiece.setRow(source.getRow());
		movedPiece.setCol(source.getCol());
		source.setPiece(movedPiece);
		movedPiece.decrementTimesMoved();
		movedPiece.setHasJustMoved(false);
	}
	
	/**
	 * Helper method for undoMove() that places the rook in the right spot when
	 * undoing a castle. <br>
	 * pre: source != null, dest != null <br>
	 * post: the rook involved in the castle is moved to the correct Space.
	 * 
	 * @param source
	 *            The Space that the king moved from. May not be null.
	 * @param dest
	 *            The Space that the king moved to. May not be null.
	 */
	private void uncastle(Space source, Space dest) {
		
		// System.out.println("UNCASTLE");

		// check preconditions
		if (source == null || dest == null)
			throw new IllegalArgumentException("Source and dest may not be null.");

		// first get the correct rook
		Space originalRookSpace;
		int changeFromDest;
		if (source.getCol() > dest.getCol()) {
			originalRookSpace = spaces[source.getRow()][Board.MIN_COL];
			changeFromDest = 1;
		} else {
			originalRookSpace = spaces[source.getRow()][Board.MAX_COL];
			changeFromDest = -1;
		}
		
		// reset the Space objects
		Space newRookSpace = spaces[dest.getRow()][dest.getCol() + changeFromDest];
		Piece rookToMove = newRookSpace.getPiece();
		originalRookSpace.setPiece(rookToMove);
		newRookSpace.setPiece(null);

		// undo the rook move 
		rookToMove.setCol(originalRookSpace.getCol());
		rookToMove.decrementTimesMoved();
	}
	
	public void paint(Graphics graphic) {
		for(int r = 0; r <= MAX_ROW; r++) {
			for(int c = 0; c <= MAX_COL; c++) {
				spaces[r][c].paint(graphic);
			}
		}
		
		int lostPieces = 0;
		for(Piece piece : whitePieces) {
			lostPieces = piece.paint(graphic, lostPieces);
		}
		
		lostPieces = 0;
		for(Piece piece : blackPieces) {
			lostPieces = piece.paint(graphic, lostPieces);
		}
	}

	/**
	 * Return the list of black Pieces. <br>
	 * pre: none
	 * 
	 * @return The list of black Pieces.
	 */
	public ArrayList<Piece> getBlackPieces() {
		return blackPieces;
	}

	/**
	 * Return the list of white Pieces. <br>
	 * pre: none
	 * 
	 * @return The list of white Pieces.
	 */
	public ArrayList<Piece> getWhitePieces() {
		return whitePieces;
	}

	/**
	 * Returns this Board's black king.<br>
	 * pre: none
	 * 
	 * @return The Board's black king
	 */
	public Piece getBlackKing() {
		return blackKing;
	}

	/**
	 * Returns this Board's white king.<br>
	 * pre: none
	 * 
	 * @return The Board's white king
	 */
	public Piece getWhiteKing() {
		return whiteKing;
	}
}
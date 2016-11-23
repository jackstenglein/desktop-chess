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

	// instance variables
	private Space[][] spaces_;
	private ArrayList<Piece> blackPieces_;
	private ArrayList<Piece> whitePieces_;
	private Piece blackKing_;
	private Piece whiteKing_;
	private int blackPawnsLost_;
	private int blackKnightsLost_;
	private int blackBishopsLost_;
	private int blackRooksLost_;
	private int blackQueensLost_;
	private int whitePawnsLost_;
	private int whiteKnightsLost_;
	private int whiteBishopsLost_;
	private int whiteRooksLost_;
	private int whiteQueensLost_;

	/**
	 * Creates a new Board object with 64 empty Spaces.<br>
	 * pre: none
	 */
	public Board() {
		spaces_ = new Space[8][8];
		for (int r = 0; r <= Board.MAX_ROW; r++)
			for (int c = 0; c <= Board.MAX_COL; c++) {
				Space space = new Space(r, c);
				setSpace(r, c, space);
			}
	}

	// initializes a board with the same spaces, blackPieces_, whitePieces_ as
	// board
	public Board(Board board) {

	}

	/**
	 * Sets the list of Pieces and saves a reference to the kings.<br>
	 * pre: blackPieces != null && whitePieces != null
	 * 
	 * @param blackPieces
	 *            The list of black Pieces. May not be null.
	 * @param whitePieces
	 *            The list of white Pieces. May not be null.
	 */
	public void setPieces(ArrayList<Piece> blackPieces, ArrayList<Piece> whitePieces) {

		// check preconditions
		if (blackPieces == null && whitePieces == null)
			throw new IllegalArgumentException("The lists of Pieces may not be null.");

		blackPieces_ = blackPieces;
		whitePieces_ = whitePieces;

		// set the kings to the correct Pieces
		for (int i = 0; i < blackPieces_.size(); i++) {
			if (blackPieces_.get(i).getType() == Piece.TYPE_KING)
				blackKing_ = blackPieces_.get(i);
		}

		for (int i = 0; i < whitePieces_.size(); i++) {
			if (whitePieces_.get(i).getType() == Piece.TYPE_KING)
				whiteKing_ = whitePieces_.get(i);
		}
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

		spaces_[row][col] = space;
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

		return spaces_[row][col];
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
		
		// reset all Spaces to not be selected
		for(int r = 0; r <= MAX_ROW; r++)
			for(int c = 0; c <= MAX_COL; c++)
				spaces_[r][c].setTakingMove(false);
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
		if (movedPiece.getType() == Piece.TYPE_KING
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
			pieceLost(capturedPiece);
			spaces_[capturedPiece.getRow()][capturedPiece.getCol()].setPiece(null);
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
			rookSpace = spaces_[source.getRow()][Board.MIN_COL];
			changeFromDest = 1;
		} else {
			rookSpace = spaces_[source.getRow()][Board.MAX_COL];
			changeFromDest = -1;
		}

		Piece rookToMove = rookSpace.getPiece();
		rookSpace.setPiece(null);
		// board_.setSpace(rookSpace.getRow(), rookSpace.getCol(),
		// rookSpace);

		// move the rook
		rookToMove.setCol(dest.getCol() + changeFromDest);
		rookToMove.incrementTimesMoved();
		Space newRookSpace = spaces_[dest.getRow()][dest.getCol() + changeFromDest];
		newRookSpace.setPiece(rookToMove);
		
		// reset all Spaces to not be selected
		for(int r = 0; r <= MAX_ROW; r++)
			for(int c = 0; c <= MAX_COL; c++)
				spaces_[r][c].setTakingMove(false);
	}

	/**
	 * Helper method for makeMove() that resets movedLastTurn for the given
	 * color's Pieces.<br>
	 * pre: none
	 * 
	 * @param blackPieces
	 *            a boolean indicating whether to reset black's pieces.
	 */
	private void resetMovedLastTurn(boolean blackPieces) {
		ArrayList<Piece> otherPieces;
		if (blackPieces)
			otherPieces = blackPieces_;
		else
			otherPieces = whitePieces_;

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

		if (movedPiece.getType() == Piece.TYPE_PAWN
				&& (movedPiece.getRow() == Board.MIN_ROW || movedPiece.getRow() == Board.MAX_ROW)) {

			// display the JOptionPane
			Object[] buttons = { "Knight", "Bishop", "Rook", "Queen" };
			int selectedValue = JOptionPane.showOptionDialog(null, "Which piece would you like?", "Promote pawn",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[3]);

			// the user actually made a choice
			if (selectedValue > -1)
				movedPiece.setType(selectedValue + 2);

			// otherwise, they cancelled, so
			// just make the default value a queen
			else
				movedPiece.setType(Piece.TYPE_QUEEN);

			movedPiece.setPromoted(true);
		}
	}

	/**
	 * Removes the given Piece from the corresponding list of Pieces and adds to
	 * the count of lost Pieces.
	 * 
	 * @param piece
	 *            The Piece that is captured. May not be null.
	 */
	public void pieceLost(Piece piece) {
		// check precondition
		if (piece == null)
			throw new IllegalArgumentException("Piece may not be null");

		if (blackPieces_.contains(piece)) {
			System.out.println("Remove black piece");
			blackPieces_.remove(piece);
		} else if (whitePieces_.contains(piece)) {
			System.out.println("Remove white piece");
			whitePieces_.remove(piece);
		} else
			System.out.println("Piece not found");

		piece.setCaptured(true);
		incrementLostCount(piece.isWhite(), piece.getType(), 1);
	}

	/**
	 * Helper method for pieceLost() that increments the count of lost Pieces.
	 * <br>
	 * pre: none
	 * 
	 * @param isWhite
	 *            a boolean indicating whether the lost Piece was white
	 * @param type
	 *            an int that represents the type of the Piece
	 */
	private void incrementLostCount(boolean isWhite, int type, int increment) {
		if (isWhite) {
			switch (type) {
			case Piece.TYPE_PAWN:
				whitePawnsLost_ += increment;
				break;
			case Piece.TYPE_KNIGHT:
				whiteKnightsLost_ += increment;
				break;
			case Piece.TYPE_BISHOP:
				whiteBishopsLost_ += increment;
				break;
			case Piece.TYPE_ROOK:
				whiteRooksLost_ += increment;
				break;
			case Piece.TYPE_QUEEN:
				whiteQueensLost_ += increment;
				break;
			}
		} else {
			switch (type) {
			case Piece.TYPE_PAWN:
				blackPawnsLost_ += increment;
				break;
			case Piece.TYPE_KNIGHT:
				blackKnightsLost_ += increment;
				break;
			case Piece.TYPE_BISHOP:
				blackBishopsLost_ += increment;
				break;
			case Piece.TYPE_ROOK:
				blackRooksLost_ += increment;
				break;
			case Piece.TYPE_QUEEN:
				blackQueensLost_ += increment;
				break;
			}
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
		if (movedPiece.getType() == Piece.TYPE_KING
				&& (source.getCol() - dest.getCol() == 2 || source.getCol() - dest.getCol() == -2))
					uncastle(source, dest);

		// restore a Piece if one was captured
		else if (capturedPiece != null) {
			pieceRestored(capturedPiece);
			spaces_[capturedPiece.getRow()][capturedPiece.getCol()].setPiece(capturedPiece);
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
			originalRookSpace = spaces_[source.getRow()][Board.MIN_COL];
			changeFromDest = 1;
		} else {
			originalRookSpace = spaces_[source.getRow()][Board.MAX_COL];
			changeFromDest = -1;
		}
		
		// reset the Space objects
		Space newRookSpace = spaces_[dest.getRow()][dest.getCol() + changeFromDest];
		Piece rookToMove = newRookSpace.getPiece();
		originalRookSpace.setPiece(rookToMove);
		newRookSpace.setPiece(null);

		// undo the rook move 
		rookToMove.setCol(originalRookSpace.getCol());
		rookToMove.decrementTimesMoved();
	}

	/**
	 * Restores the given Piece from the corresponding list of Pieces and
	 * subtracts from the count of lost Pieces.
	 * 
	 * @param piece
	 *            The Piece that was captured. May not be null.
	 */
	private void pieceRestored(Piece piece) {

		// check precondition
		if (piece == null)
			throw new IllegalArgumentException("Piece may not be null");

		if (piece.isWhite()) {
			System.out.println("Add to white pieces");
			whitePieces_.add(piece);
		} else {
			System.out.println("Add to black pieces");
			blackPieces_.add(piece);
		}

		piece.setCaptured(false);
		incrementLostCount(piece.isWhite(), piece.getType(), -1);
	}

	/**
	 * Return the list of black Pieces. <br>
	 * pre: none
	 * 
	 * @return The list of black Pieces.
	 */
	public ArrayList<Piece> getBlackPieces() {
		return blackPieces_;
	}

	/**
	 * Return the list of white Pieces. <br>
	 * pre: none
	 * 
	 * @return The list of white Pieces.
	 */
	public ArrayList<Piece> getWhitePieces() {
		return whitePieces_;
	}

	/**
	 * Sets the black king to the specified Piece. <br>
	 * pre: blackKing != null && blackKing.getType() == Piece.TYPE_KING
	 * 
	 * @param blackKing
	 *            The Piece to set this Board's black king to. Must match the
	 *            given preconditions.
	 */
	/*
	 * public void setBlackKing(Piece blackKing) {
	 * 
	 * // check preconditions if(blackKing == null) throw new
	 * IllegalArgumentException("blackKing may not be null."); else
	 * if(blackKing.getType() != Piece.TYPE_KING) throw new
	 * IllegalArgumentException("blackKing must be of type king.");
	 * 
	 * blackKing_ = blackKing; }
	 */

	/**
	 * Returns this Board's black king.<br>
	 * pre: none
	 * 
	 * @return The Board's black king
	 */
	public Piece getBlackKing() {
		return blackKing_;
	}

	/**
	 * Returns this Board's white king.<br>
	 * pre: none
	 * 
	 * @return The Board's white king
	 */
	public Piece getWhiteKing() {
		return whiteKing_;
	}

	/**
	 * Returns the number of black pawns lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of black pawns lost
	 */
	public int getBlackPawnsLost() {
		return blackPawnsLost_;
	}

	/**
	 * Returns the number of black knights lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of black knights lost
	 */
	public int getBlackKnightsLost() {
		return blackKnightsLost_;
	}

	/**
	 * Returns the number of black bishops lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of black bishops lost
	 */
	public int getBlackBishopsLost() {
		return blackBishopsLost_;
	}

	/**
	 * Returns the number of black rooks lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of black rooks lost
	 */
	public int getBlackRooksLost() {
		return blackRooksLost_;
	}

	/**
	 * Returns the number of black queens lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of black queens lost
	 */
	public int getBlackQueensLost() {
		return blackQueensLost_;
	}

	/**
	 * Returns the number of white pawns lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of white pawns lost
	 */
	public int getWhitePawnsLost() {
		return whitePawnsLost_;
	}

	/**
	 * Returns the number of white knights lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of white knights lost
	 */
	public int getWhiteKnightsLost() {
		return whiteKnightsLost_;
	}

	/**
	 * Returns the number of white bishops lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of white bishops lost
	 */
	public int getWhiteBishopsLost() {
		return whiteBishopsLost_;
	}

	/**
	 * Returns the number of white rooks lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of white rooks lost
	 */
	public int getWhiteRooksLost() {
		return whiteRooksLost_;
	}

	/**
	 * Returns the number of white queens lost on this Board.<br>
	 * pre: none
	 * 
	 * @return The number of white queens lost
	 */
	public int getWhiteQueensLost() {
		return whiteQueensLost_;
	}
}
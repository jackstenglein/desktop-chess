/**
 * A class that represents a chess piece.
 * 
 * @author jstenglein 11/19/16
 */
public class Piece {

	// class constants
	public static final int TYPE_PAWN = 1;
	public static final int TYPE_KNIGHT = 2;
	public static final int TYPE_BISHOP = 3;
	public static final int TYPE_ROOK = 4;
	public static final int TYPE_QUEEN = 5;
	public static final int TYPE_KING = 6;

	// instance variables
	private int type_;
	private boolean isWhite_;
	private int row_;
	private int col_;
	private boolean isCaptured_;
	private boolean hasJustMoved_;
	private boolean promoted_;
	private int timesMoved_;

	/**
	 * Create a Piece with the specified values. <br>
	 * pre: Piece.TYPE_PAWN <= type <= Piece.TYPE_KING && Board.MIN_ROW <= row
	 * <= Board.MAX_ROW && Board.MIN_COL <= col <= Board.MAX_COL
	 * 
	 * @param type
	 *            The type of Piece. Piece.TYPE_PAWN <= type <= Piece.TYPE_KING
	 * @param isWhite
	 *            A boolean indicating whether the Piece is white or black
	 * @param row
	 *            The row of the Board that the Piece is on. Board.MIN_ROW <=
	 *            row <= Board.MAX_ROW
	 * @param col
	 *            The col of the Board that the Piece is on. Board.MIN_COL <=
	 *            col <= Board.MAX_COL
	 */
	public Piece(int type, boolean isWhite, int row, int col) {
		// check precondition
		if (type < TYPE_PAWN || type > TYPE_KING)
			throw new IllegalArgumentException("Specified piece type is not valid: " + type);
		else if (row < Board.MIN_ROW || row > Board.MAX_ROW || col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Specifed row/col is not valid. Row: " + row + ", col: " + col);

		// set values
		type_ = type;
		isWhite_ = isWhite;
		row_ = row;
		col_ = col;
		isCaptured_ = false;
		hasJustMoved_ = false;
		promoted_ = false;
		timesMoved_ = 0;
	}

	/**
	 * Set the type of this Piece. <br>
	 * pre: Piece.TYPE_PAWN <= type <= Piece.TYPE_KING
	 * 
	 * @param type
	 *            The type of the Piece. Piece.TYPE_PAWN <= type <=
	 *            Piece.TYPE_KING
	 */
	public void setType(int type) {
		// check precondition
		if (type < TYPE_PAWN || type > TYPE_KING)
			throw new IllegalArgumentException("Specified piece type is not valid: " + type);

		type_ = type;
	}

	/**
	 * Return the type of this Piece as an int. <br>
	 * pre: none
	 * 
	 * @return an int representing the type of this Piece.
	 */
	public int getType() {
		return type_;
	}

	/**
	 * Return a boolean indicating whether the Piece is white. <br>
	 * pre: none
	 * 
	 * @return True if the Piece is white, false if the Piece is black
	 */
	public boolean isWhite() {
		return isWhite_;
	}

	/**
	 * Set the row of this Piece. <br>
	 * pre: Board.MIN_ROW <= row <= Board.MAX_ROW
	 * 
	 * @param row
	 *            The row to move the Piece to. Board.MIN_ROW <= row <=
	 *            Board.MAX_ROW
	 */
	public void setRow(int row) {
		// check precondition
		if (row < Board.MIN_ROW || row > Board.MAX_ROW)
			throw new IllegalArgumentException("Specifed row is not valid: " + row);

		row_ = row;
	}

	/**
	 * Return the row of the Board this Piece is on. <br>
	 * pre: none
	 * 
	 * @return The row this Piece is on.
	 */
	public int getRow() {
		return row_;
	}

	/**
	 * Set the col of this Piece. <br>
	 * pre: Board.MIN_COL <= col <= Board.MAX_COL
	 * 
	 * @param col
	 *            The col to move the Piece to. Board.MIN_COL <= col <=
	 *            Board.MAX_COL
	 */
	public void setCol(int col) {
		// check precondition
		if (col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Specified col is not valid: " + col);

		col_ = col;
	}

	/**
	 * Return the col of the Board this Piece is on. <br>
	 * pre: none
	 * 
	 * @return The col this Piece is on.
	 */
	public int getCol() {
		return col_;
	}

	/**
	 * Set whether this Piece is captured or not. <br>
	 * pre: none
	 * 
	 * @param isCaptured
	 *            A boolean indicating whether the Piece is captured.
	 */
	public void setCaptured(boolean isCaptured) {
		isCaptured_ = isCaptured;
	}

	/**
	 * Return a boolean indicating whether the Piece is captured or not. <br>
	 * pre: none
	 * 
	 * @return True if the Piece is captured, false if not.
	 */
	public boolean isCaptured() {
		return isCaptured_;
	}
	
	/**
	 * Set whether this Piece was promoted or not <br>
	 * pre: none
	 * @param promoted A boolean indicating whether the Piece has been promoted or not.
	 */
	public void setPromoted(boolean promoted) {
		promoted_ = promoted;
	}
	
	/**
	 * Returns a boolean indicating whether this Piece was promoted or not <br>
	 * pre: none
	 * @return True if this Piece was a pawn that was promoted, false if not.
	 */
	public boolean wasPromoted() {
		return promoted_;
	}

	/**
	 * Adds one to the number of times this Piece has moved. <br>
	 * pre: none
	 */
	public void incrementTimesMoved() {
		timesMoved_++;
	}
	
	/**
	 * Subtracts one from the number of times this Piece has moved. <br>
	 * pre: timesMoved >= 1
	 */
	public void decrementTimesMoved() {
		if(timesMoved_ < 1)
			throw new IllegalStateException("timesMoved cannot be less than 0");
		
		timesMoved_--;
	}

	/**
	 * Returns the number of times this Piece has moved. <br>
	 * pre: none
	 * 
	 * @return The number of moves this Piece has made.
	 */
	public int getTimesMoved() {
		return timesMoved_;
	}

	/**
	 * Set whether this Piece has just moved or not. A Piece has just moved if
	 * it moved and no other Pieces have moved since then. <br>
	 * pre: none
	 * 
	 * @param hasJustMoved
	 *            A boolean indicating whether the Piece just moved.
	 */
	public void setHasJustMoved(boolean hasJustMoved) {
		hasJustMoved_ = hasJustMoved;
	}

	/**
	 * Return a boolean indicating whether this Piece has just moved or not. A
	 * Piece has just moved if it moved and no other Pieces have moved since
	 * then. <br>
	 * pre: none
	 * 
	 * @return True if this Piece has just moved, false otherwise
	 */
	public boolean hasJustMoved() {
		return hasJustMoved_;
	}

	/**
	 * Returns the material value of this Piece according to chess convention.
	 * <br>
	 * pre: none
	 * 
	 * @return The material value of this piece.
	 */
	public int getValue() {
		int value = 0;

		switch (type_) {
		case TYPE_PAWN:
			value = 1;
			break;
		case TYPE_KNIGHT:
			value = 3;
			break;
		case TYPE_BISHOP:
			value = 3;
			break;
		case TYPE_ROOK:
			value = 5;
			break;
		case TYPE_QUEEN:
			value = 9;
			break;
		case TYPE_KING:
			value = 6;
			break;
		}

		return value;
	}

	/**
	 * Returns the Piece's type as a String rather than a number.<br>
	 * pre: none
	 * 
	 * @return The type of the Piece as a String.
	 */
	public String typeAsString() {
		switch (type_) {
		case TYPE_PAWN:
			return "pawn";
		case TYPE_KNIGHT:
			return "knight";
		case TYPE_BISHOP:
			return "bishop";
		case TYPE_ROOK:
			return "rook";
		case TYPE_QUEEN:
			return "queen";
		case TYPE_KING:
			return "king";
		default:
			return "";
		}
	}

	public String toString() {
		String baseString = "";

		switch (type_) {
		case TYPE_PAWN:
			baseString = "Pawn ";
			break;
		case TYPE_KNIGHT:
			baseString = "Knight ";
			break;
		case TYPE_BISHOP:
			baseString = "Bishop ";
			break;
		case TYPE_ROOK:
			baseString = "Rook ";
			break;
		case TYPE_QUEEN:
			baseString = "Queen ";
			break;
		case TYPE_KING:
			baseString = "King ";
			break;
		}

		baseString = baseString + "at (" + row_ + ", " + col_ + ") has moved " + timesMoved_ + " times, ";
		baseString += isCaptured_ ? "is captured, " : "is active, ";
		baseString += hasJustMoved_ ? "and has just moved." : "and has not just moved";

		return baseString;
	}

	/**
	 * Returns a boolean indicating whether this Piece is equal to the passed
	 * Object. The two Objects are equal if other is a Piece object of the same
	 * color and type and at the same row and col as this Piece. <br>
	 * pre: none
	 * 
	 * @return True if this Piece is equal to other, false otherwise
	 */
	public boolean equals(Object other) {

		if (other instanceof Piece) {
			// safe to cast
			Piece otherPiece = (Piece) other;
			if (type_ != otherPiece.type_)
				return false;
			else if (otherPiece.row_ != row_ || otherPiece.col_ != col_)
				return false;
			else if (otherPiece.isWhite_ != isWhite_)
				return false;

			return true;
		}

		return false;
	}
}
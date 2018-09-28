/**
 * A class that represents a chess piece.
 * 
 * @author jstenglein 11/19/16
 */
public class Piece implements Comparable<Piece> {

	// class constants
	public enum PieceType {
		PAWN("pawn", 1),
		KNIGHT("knight", 3),
		BISHOP("bishop", 3),
		ROOK("rook", 5),
		QUEEN("queen", 9),
		KING("king", 6);
		
		private final String name;
		private final int value;
		private PieceType(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}

	// instance variables
	private PieceType type;
	private boolean isWhite;
	private int row;
	private int col;
	private boolean isCaptured;
	private boolean hasJustMoved;
	private boolean promoted;
	private int timesMoved;

	/**
	 * Creates a Piece object with the specified values. <br>
	 * pre: Board.MIN_ROW <= row <= Board.MAX_ROW &&
	 *      Board.MIN_COL <= col <= Board.MAX_COL
	 * 
	 * @param type The type of Piece.
	 * @param isWhite A boolean indicating whether the Piece is white or black
	 * @param row The row of the Board that the Piece is on. Board.MIN_ROW <=
	 *            row <= Board.MAX_ROW
	 * @param col The col of the Board that the Piece is on. Board.MIN_COL <=
	 *            col <= Board.MAX_COL
	 */
	public Piece(PieceType type, boolean isWhite, int row, int col) {
		// check precondition
		if (row < Board.MIN_ROW || row > Board.MAX_ROW || col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Specifed row/col is not valid. Row: " + row + ", col: " + col);

		// set values
		this.type = type;
		this.isWhite = isWhite;
		this.row = row;
		this.col = col;
		isCaptured = false;
		hasJustMoved = false;
		promoted = false;
		timesMoved = 0;
	}

	/**
	 * Promotes a pawn to a new piece type. <br>
	 * pre: This piece is a pawn.
	 * 
	 * @param type The type of the Piece.
	 */
	public void promote(PieceType newType) {
		// check precondition
		if (type != PieceType.PAWN)
			throw new IllegalStateException("The piece must be a pawn, but is instead a " + type.name);

		type = newType;
	}

	/**
	 * Returns the type of this Piece.
	 * 
	 * @return The type of this Piece.
	 */
	public PieceType getType() {
		return type;
	}

	/**
	 * Returns a boolean indicating whether the Piece is white.
	 * 
	 * @return True if the Piece is white, false if the Piece is black
	 */
	public boolean isWhite() {
		return isWhite;
	}

	/**
	 * Sets the row of this Piece.
	 * 
	 * @param row The row to move the Piece to. 
	 *            Board.MIN_ROW <= row <= Board.MAX_ROW
	 */
	public void setRow(int row) {
		if (row < Board.MIN_ROW || row > Board.MAX_ROW)
			throw new IllegalArgumentException("Specifed row is not valid: " + row);
		
		this.row = row;
	}

	/**
	 * Returns the row of the Board this Piece is on.
	 * 
	 * @return The row this Piece is on.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the column of this Piece.
	 * 
	 * @param col The column to move the Piece to. 
	 *            Board.MIN_COL <= col <= Board.MAX_COL
	 */
	public void setCol(int col) {
		if (col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Specified col is not valid: " + col);

		this.col = col;
	}

	/**
	 * Returns the column of the Board this Piece is on. 
	 * 
	 * @return The column this Piece is on.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets whether this Piece is captured or not. 
	 * 
	 * @param isCaptured A boolean indicating whether the Piece is captured.
	 */
	public void setCaptured(boolean isCaptured) {
		this.isCaptured = isCaptured;
	}

	/**
	 * Returns a boolean indicating whether the Piece is captured or not.
	 * 
	 * @return True if the Piece is captured, false if not.
	 */
	public boolean isCaptured() {
		return isCaptured;
	}
	
	/**
	 * Sets whether this Piece was promoted or not.
	 * 
	 * @param promoted A boolean indicating whether the Piece has been promoted or not.
	 */
	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}
	
	/**
	 * Returns a boolean indicating whether this Piece was promoted or not.
	 * 
	 * @return True If this Piece was a pawn that was promoted, false if not.
	 */
	public boolean wasPromoted() {
		return promoted;
	}

	/**
	 * Increments the number of times this Piece has moved. 
	 */
	public void incrementTimesMoved() {
		timesMoved++;
	}
	
	/**
	 * Decrements the number of times this Piece has moved.
	 * <br>pre: timesMoved > 0
	 */
	public void decrementTimesMoved() {
		if(timesMoved < 1)
			throw new IllegalStateException("timesMoved cannot be less than 0");
		
		timesMoved--;
	}

	/**
	 * Returns the number of times this Piece has moved.
	 * 
	 * @return The number of moves this Piece has made.
	 */
	public int getTimesMoved() {
		return timesMoved;
	}

	/**
	 * Sets whether this Piece has just moved or not. A Piece has just moved if
	 * it moved and no other Pieces have moved since then. 
	 * 
	 * @param hasJustMoved A boolean indicating whether the Piece just moved.
	 */
	public void setHasJustMoved(boolean hasJustMoved) {
		this.hasJustMoved = hasJustMoved;
	}

	/**
	 * Returns a boolean indicating whether this Piece has just moved or not. A
	 * Piece has just moved if it moved and no other Pieces have moved since
	 * then. 
	 * 
	 * @return True if this Piece has just moved, false otherwise
	 */
	public boolean hasJustMoved() {
		return hasJustMoved;
	}

	/**
	 * Returns the material value of this Piece according to chess convention.
	 * 
	 * @return The material value of this piece.
	 */
	public int getValue() {
		return type.value;
	}

	/**
	 * Returns the Piece's type as a String.
	 * 
	 * @return The type of the Piece as a String.
	 */
	public String typeAsString() {
		return type.name;
	}

	public String toString() {
		String result = type.name + " ";
		result += "at (" + row + ", " + col + ") has moved " + timesMoved + " times, ";
		result += isCaptured ? "is captured, " : "is active, ";
		result += hasJustMoved ? "and has just moved." : "and has not just moved";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isWhite ? 1231 : 1237);
		result = prime * result + type.name.hashCode();
		return result;
	}

	/**
	 * Returns a boolean indicating whether this Piece is equal to the passed
	 * Object. The two Objects are equal if other is a Piece object of the same
	 * color and type and at the same row and col as this Piece. 
	 * 
	 * @param other The object to compare to this piece.
	 * 
	 * @return True if this Piece is equal to other, false otherwise.
	 */
	public boolean equals(Object other) {
		if (other instanceof Piece) {
			Piece otherPiece = (Piece) other;
			if (type != otherPiece.type) {
				return false;
			} else if (otherPiece.row != row || otherPiece.col != col) {
				return false;
			}
			
			return otherPiece.isWhite == isWhite;
		}

		return false;
	}

	@Override
	public int compareTo(Piece other) {
		if(getValue() < other.getValue())
			return -1;
		else if(getValue() > other.getValue())
			return 1;
		
		return 0;
	}
}

/**
 * A class that represents a move in chess.
 * 
 * @author jstenglein 11/19/16
 */
public class Move {

	// instance variables
	private Piece movedPiece;
	private Piece capturedPiece;
	private Space source;
	private Space dest;
	
	
	/**
	 * Creates a new Move object with the given parameters. This object is
	 * immutable. This is equivalent to the call 
	 * Move(movedPiece, dest.getPiece(), source, dest).
	 * 
	 * @param movedPiece
	 *            The Piece that is being moved. May not be null.
	 * @param source
	 *            The Space movedPiece originally started from. May not be null.
	 * @param dest
	 *            The Space movedPiece will end at. May not be null.
	 */
	public Move(Piece movedPiece, Space source, Space dest) {
		this(movedPiece, dest.getPiece(), source, dest);
	}

	/**
	 * Creates a new Move object with the given parameters. This object is
	 * immutable.
	 * 
	 * @param movedPiece
	 *            The Piece that is being moved. May not be null.
	 * @param capturedPiece
	 *            The Piece that is being captured. May be null.
	 * @param source
	 *            The Space movedPiece originally started from. May not be null.
	 * @param dest
	 *            The Space movedPiece will end at. May not be null.
	 */
	public Move(Piece movedPiece, Piece capturedPiece, Space source, Space dest) {

		// check preconditions
		if (movedPiece == null)
			throw new IllegalArgumentException("movedPiece may not be null.");
		else if (source == null)
			throw new IllegalArgumentException("source may not be null.");
		else if (dest == null)
			throw new IllegalArgumentException("dest may not be null.");

		// set instance variables
		this.movedPiece = movedPiece;
		this.capturedPiece = capturedPiece;
		this.source = source;
		this.dest = dest;
	}

	/**
	 * Returns the Piece that will be captured in this Move or null if no Piece
	 * will be captured.<br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return The Piece that will be captured or null.
	 */
	public Piece getCapturedPiece() {
		return capturedPiece;
	}

	/**
	 * Returns the Piece that will be moved. <br>
	 * pre: none<br>
	 * O(1)
	 * 
	 * @return The Piece that will be moved.
	 */
	public Piece getMovedPiece() {
		return movedPiece;
	}

	/**
	 * Returns true if a Piece will be captured in this Move, false otherwise.
	 * <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return True if a Piece will be captured in this Move, false if not.
	 */
	public boolean isCapture() {
		if (capturedPiece == null)
			return false;

		return true;
	}

	/**
	 * Returns the Space that the Piece to be moved originally started at. <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return The Space where getMovedPiece() originally started.
	 */
	public Space getSource() {
		return source;
	}

	/**
	 * Returns the Space that the Piece to be moved will end at. <br>
	 * pre: none <br>
	 * O(1)
	 * 
	 * @return The Space where getMovedPiece() will move to.
	 */
	public Space getDestination() {
		return dest;
	}

	/**
	 * Returns a boolean indicating whether this Move will be made to the
	 * specified Space. <br>
	 * pre: none
	 * 
	 * @param space
	 *            The Space to compare to this Move's destination Space.
	 * @return A boolean indicating whether this Move will be made to the
	 *         specified Space.
	 */
	public boolean containsDestination(Space space) {
		return dest.equals(space);
	}
	
	/**
	 * Returns a boolean indicating whether this Move will capture the specified Piece.<br>
	 * pre: piece != null
	 * 
	 * @param piece The Piece to compare to this Move's capturedPiece. May not be null.
	 * @return True if piece equals capturedPiece, false otherwise
	 */
	public boolean capturesPiece(Piece piece) {
		
		//check precondition
		if(piece == null)
			throw new IllegalArgumentException("Piece may not be null.");
		
		return piece.equals(capturedPiece);
	}

	/**
	 * Returns a String describing the Move. The description is of the format:
	 * "White pawn at (4, 3) will move to (3, 3)."<br>
	 * pre: none
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (movedPiece.isWhite())
			sb.append("White ");
		else
			sb.append("Black ");

		sb.append(movedPiece.typeAsString());
		sb.append(" at (");
		sb.append(source.getRow());
		sb.append(", ");
		sb.append(source.getCol());
		sb.append(") will move to (");
		sb.append(dest.getRow());
		sb.append(", ");
		sb.append(dest.getCol());
		sb.append(")");

		// if there is a captured piece, say so
		if (capturedPiece != null) {
			sb.append(" and will capture a ");
			sb.append(capturedPiece.typeAsString());
		}

		sb.append(".");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capturedPiece == null) ? 0 : capturedPiece.hashCode());
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((movedPiece == null) ? 0 : movedPiece.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Move) {
			// safe to cast
			Move otherMove = (Move) other;
			if (!otherMove.source.equals(source))
				return false;
			else if (!otherMove.dest.equals(dest))
				return false;
			else if (!otherMove.movedPiece.equals(otherMove.movedPiece))
				return false;
			else if (movedPiece == null)
				return otherMove.movedPiece == null;
			else
				return movedPiece.equals(otherMove.movedPiece);
		}

		return false;
	}
}
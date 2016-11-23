import java.util.ArrayList;

public class MoveValidator {
	// class constants
	public static final int SPACE_OFF_BOARD = 0;
	public static final int SPACE_EMPTY = 1;
	public static final int SPACE_WHITE = 2; // space occupied by a white piece
	public static final int SPACE_BLACK = 3; // space occupied by a black piece

	/**
	 * Return an ArrayList of Pieces that are defending the specified Piece and
	 * Board. <br>
	 * pre: piece != null && board != null
	 * 
	 * @param piece
	 *            The Piece to be defended. May not be null.
	 * @param board
	 *            The Board to search. May not be null.
	 * @return An ArrayList of Pieces defending piece.
	 */
	public static ArrayList<Piece> findDefendingPieces(Piece piece, Board board) {

		System.out.println("Find defending pieces.");

		// check precondition
		if (piece == null || board == null)
			throw new IllegalArgumentException("Piece and board should not be null.");

		ArrayList<Piece> defenders = new ArrayList<Piece>();

		// only Pieces of the same color can be defenders
		ArrayList<Piece> piecesToCheck;
		if (piece.isWhite())
			piecesToCheck = board.getWhitePieces();
		else
			piecesToCheck = board.getBlackPieces();

		// loop through the same colored Pieces, and find their Moves.
		// if they can 'capture' the passed Piece, they are defending it.
		for (int i = 0; i < piecesToCheck.size(); i++) {
			ArrayList<Move> availableMoves = new ArrayList<Move>();
			Piece pieceToCheck = piecesToCheck.get(i);
			if (!pieceToCheck.equals(piece))
				availableMoves = findAvailableMoves(pieceToCheck, board, false);

			for (int j = 0; j < availableMoves.size(); j++)
				if (piece.equals(availableMoves.get(j).getCapturedPiece())) {
					defenders.add(pieceToCheck);
					break;
				}
		}

		return defenders;
	}

	/**
	 * Returns an ArrayList of Pieces that can be attacked by the given Piece on
	 * the Board. <br>
	 * pre: piece != null && board != null
	 * 
	 * @param piece
	 *            The Piece that is attacking the found Pieces. May not be null.
	 * @param board
	 *            The Board to search. May not be null.
	 * @return An ArrayList of Pieces that can be attacked by piece.
	 */
	public static ArrayList<Piece> findAttackedPieces(Piece piece, Board board) {

		System.out.println("Find attacked pieces.");

		// check preconditions
		if (piece == null || board == null)
			throw new IllegalArgumentException("Piece and board may not be null.");

		ArrayList<Piece> piecesAttacked = new ArrayList<Piece>();

		// find the legal moves for the piece and check them
		// to see if they contain an enemy piece. Add the piece if so.
		ArrayList<Move> pieceMoves = findLegalMoves(piece, board, false);
		for (int i = 0; i < pieceMoves.size(); i++) {
			if (pieceMoves.get(i).isCapture())
				piecesAttacked.add(pieceMoves.get(i).getCapturedPiece());
		}

		return piecesAttacked;
	}

	/**
	 * Returns an ArrayList of Pieces of the given color that can attack the
	 * given Space on the Board. <br>
	 * pre: spaceToCheck != null && board != null
	 * 
	 * @param spaceToCheck
	 *            The Space to find attackers for. May not be null.
	 * @param isWhite
	 *            A boolean indicating whether to search for white or black
	 *            attackers.
	 * @param board
	 *            The Board to search. May not be null.
	 */
	public static ArrayList<Piece> findPiecesAttackingSpace(Space spaceToCheck, boolean isWhite, Board board) {
		System.out.println("Find pieces attacking space.");

		// check precondition
		if (spaceToCheck == null || board == null)
			throw new IllegalArgumentException("Space and board may not be null.");

		ArrayList<Piece> piecesAttacking = new ArrayList<Piece>();
		ArrayList<Piece> opposingTeam;

		if (isWhite)
			opposingTeam = board.getBlackPieces();
		else
			opposingTeam = board.getWhitePieces();

		// check the attacking pieces to see if the current piece occupies a
		// space they can move to
		for (int i = 0; i < opposingTeam.size(); i++) {
			if (!opposingTeam.get(i).isCaptured()) {
				ArrayList<Move> moves = findAvailableMoves(opposingTeam.get(i), board, false);
				// System.out.println("Attacking spaces: " + spaces.toString());

				for (int j = 0; j < moves.size(); j++)
					if (moves.get(j).getDestination().equals(spaceToCheck)) {
						piecesAttacking.add(opposingTeam.get(i));
						break;
					}
			}
		}

		return piecesAttacking;
	}

	/**
	 * Returns an ArrayList of Pieces attacking the specified Piece on the
	 * Board. <br>
	 * pre: piece != null && board != null
	 * 
	 * @param piece
	 *            The Piece to check for attackers. May not be null.
	 * @param board
	 *            The Board piece is on. May not be null.
	 * @return An ArrayList of Pieces attacking the specified Piece.
	 */
	public static ArrayList<Piece> findPiecesAttackingPiece(Piece piece, Board board) {
		System.out.println("Find pieces attacking piece.");

		// check precondition
		if (piece == null || board == null)
			throw new IllegalArgumentException("Piece and board may not be null.");

		Space currentSpace = board.getSpace(piece.getRow(), piece.getCol());
		return findPiecesAttackingSpace(currentSpace, piece.isWhite(), board);
	}

	/**
	 * Returns an ArrayList of Move objects that represent the legal moves for
	 * the specified Piece and Board. <br>
	 * pre: piece != null, board != null
	 * 
	 * @param piece
	 *            The Piece to get Moves for. May not be null.
	 * @param board
	 *            The Board the Piece is on. May not be null.
	 * @param checkCastling
	 *            A boolean indicating whether to check for castling Moves or
	 *            not.
	 * @return An ArrayList of all legal Moves for the given Piece and Board.
	 */
	public static ArrayList<Move> findLegalMoves(Piece piece, Board board, boolean checkCastling) {
		System.out.println("Find legal moves.");
		ArrayList<Move> legalMoves = findAvailableMoves(piece, board, checkCastling);
		removeSameColoredMoves(legalMoves, piece.isWhite());
		for (int i = 0; i < legalMoves.size(); i++)
			if (isCheckAfterMove(legalMoves.get(i), board)) {
				legalMoves.remove(i);
				i--;
			}

		return legalMoves;
	}

	/**
	 * Helper method for findLegalMoves and findDefenders that returns an
	 * ArrayList of Move objects that represent all possible moves for the
	 * specified Piece and Board, regardless of whether the Piece captures
	 * another Piece of its own color. <br>
	 * pre: piece != null, board != null
	 * 
	 * @param piece
	 *            The Piece to get Moves for. May not be null.
	 * @param board
	 *            The Board the Piece is on. May not be null.
	 * @param checkCastling
	 *            A boolean indicating whether to check for castling Moves or
	 *            not.
	 * @return An ArrayList of all Moves for the given Piece and Board,
	 *         regardless of the color of capture.
	 */
	private static ArrayList<Move> findAvailableMoves(Piece piece, Board board, boolean checkCastling) {
		System.out.println("Find available moves.");

		// check precondition
		if (piece == null || board == null)
			throw new IllegalArgumentException("Piece and board should not be null.");

		ArrayList<Move> availableMoves = new ArrayList<Move>();

		int pieceType = piece.getType();
		int pieceRow = piece.getRow();
		int pieceCol = piece.getCol();
		boolean pieceIsWhite = piece.isWhite();

		if (pieceType == Piece.TYPE_PAWN) {
			// System.out.println("Find pawn moves");
			availableMoves = findAvailablePawnMoves(pieceRow, pieceCol, pieceIsWhite, board);
			Move enPassant = findAvailableEnPassantMove(piece, board);
			if (enPassant != null)
				availableMoves.add(enPassant);
		} else if (pieceType == Piece.TYPE_KNIGHT) {
			// System.out.println("Find knight moves");
			availableMoves = findAvailableKnightMoves(piece, board);
		} else if (pieceType == Piece.TYPE_BISHOP) {
			// System.out.println("Find bishop moves");
			availableMoves = findAvailableBishopMoves(piece, board);
		} else if (pieceType == Piece.TYPE_ROOK) {
			// System.out.println("Find rook moves");
			availableMoves = findAvailableRookMoves(piece, board);
		} else if (pieceType == Piece.TYPE_QUEEN) {
			// System.out.println("Find queen moves");
			availableMoves = findAvailableQueenMoves(piece, board);
		} else if (pieceType == Piece.TYPE_KING) {
			// System.out.println("Find king moves");
			availableMoves = findAvailableKingMoves(piece, board, checkCastling);
		}

		return availableMoves;
	}

	/**
	 * Helper method for findAvailableMoves that removes illegal Moves (IE: a
	 * Piece attacks its own color) from the list of possible Moves. <br>
	 * pre: availableMoves != null <br>
	 * post: all illegal Moves are removed from availableMoves
	 * 
	 * @param availableMoves
	 *            The list of all Moves a Piece can make, whether it is illegal
	 *            or not. May not be null.
	 * @param pieceIsWhite
	 *            A boolean indicating whether the moved Piece is white or not.
	 */
	private static void removeSameColoredMoves(ArrayList<Move> availableMoves, boolean pieceIsWhite) {
		System.out.println("Remove same colored moves.");

		// check precondition
		if (availableMoves == null)
			throw new IllegalArgumentException("List of available moves may not be null.");

		// loop through all the moves, get the capturedPiece.
		// If it exists and is the same color as the piece being moved,
		// remove the move from the list.
		for (int i = 0; i < availableMoves.size(); i++) {
			Move move = availableMoves.get(i);
			if (move == null)
				System.out.println("Move is null for some reason?");
			Piece piece = availableMoves.get(i).getCapturedPiece();
			if (piece != null && piece.isWhite() == pieceIsWhite) {
				availableMoves.get(i).getDestination().setTakingMove(false);
				availableMoves.remove(i);
				i--;
			}
		}
	}

	/**
	 * Returns the available Moves for a pawn at a given space on the board and
	 * its color, regardless of whether it would capture a Piece on its own
	 * team. <br>
	 * pre: board != null, satisfied by calling method
	 * 
	 * @param pieceRow
	 *            The row the pawn is on.
	 * @param pieceCol
	 *            The col the pawn is on.
	 * @param pieceIsWhite
	 *            A boolean indicating whether the piece is white or not.
	 * @param board
	 *            The Board the pawn is on. May not be null.
	 * @return An ArrayList of Moves for the pawn.
	 */
	private static ArrayList<Move> findAvailablePawnMoves(int pieceRow, int pieceCol, boolean pieceIsWhite,
			Board board) {
		System.out.println("Find available pawn moves.");

		ArrayList<Move> availablePawnMoves = new ArrayList<Move>();
		Space source = board.getSpace(pieceRow, pieceCol);
		int rowChange;
		int startingRow;

		// check if piece is white
		if (pieceIsWhite) {
			rowChange = -1;
			startingRow = 6;
		}
		// piece is black
		else {
			rowChange = 1;
			startingRow = 1;
		}

		// if space above is empty, add as possible move
		Space dest = board.getSpace(pieceRow + rowChange, pieceCol);
		if (dest.isEmpty()) {

			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

			// check if still on starting row
			if (pieceRow == startingRow) {
				// if space two above is empty, add as a possible move
				dest = board.getSpace(pieceRow + 2 * rowChange, pieceCol);
				if (dest.isEmpty())
					availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));
			}
		}

		// check if space diagonal left is on board and not empty
		dest = board.getSpace(pieceRow + rowChange, pieceCol - 1);
		if (isSpaceOnBoard(dest) && !dest.isEmpty())
			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

		// check if space diagonal right is on board and not empty
		dest = board.getSpace(pieceRow + rowChange, pieceCol + 1);
		if (isSpaceOnBoard(dest) && !dest.isEmpty())
			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

		return availablePawnMoves;
	}

	/**
	 * Returns the available en passant Move for a pawn (there will always be
	 * only one). <br>
	 * pre: pawn != null && board != null, satisfied by calling method
	 * 
	 * @param pawn
	 *            The pawn to find en passant for. May not be null.
	 * @param board
	 *            The Board the pawn is on. May not be null.
	 * @return The available en passant Move or null if none exist.
	 */
	public static Move findAvailableEnPassantMove(Piece pawn, Board board) {
		int pieceRow = pawn.getRow();
		int pieceCol = pawn.getCol();
		Space source = board.getSpace(pieceRow, pieceCol);
		
		// the row a pawn has to be on 
		// for en passant to be possible
		int magicRow; 
		int rowChange;
		
		if (pawn.isWhite())
		{
			rowChange = -1;
			magicRow = 3;
		}
		else
		{
			rowChange = 1;
			magicRow = 4;
		}

		// check for en passant to the right
		Space spaceRight = board.getSpace(pieceRow, pieceCol + 1);
		if (spaceRight != null) {
			System.out.println("Check en passant right");
			Piece pieceRight = spaceRight.getPiece();
			System.out.println("Piece right: " + pieceRight);
			if (pieceRow == magicRow && pieceRight != null && pieceRight.getType() == Piece.TYPE_PAWN
					&& pieceRight.getTimesMoved() == 1 && pieceRight.hasJustMoved()) {
				System.out.println("found en passant.");
				return new Move(pawn, pieceRight, source, board.getSpace(pieceRow + rowChange, pieceCol + 1));
			}
		}

		// check for en passant to the left
		Space spaceLeft = board.getSpace(pieceRow, pieceCol - 1);
		if (spaceLeft != null) {
			Piece pieceLeft = spaceLeft.getPiece();
			if (pieceRow == magicRow && pieceLeft != null && pieceLeft.getType() == Piece.TYPE_PAWN
					&& pieceLeft.getTimesMoved() == 1 && pieceLeft.hasJustMoved()) {
				System.out.println("found en passant.");
				return new Move(pawn, pieceLeft, source, board.getSpace(pieceRow + rowChange, pieceCol - 1));
			}
		}

		return null;
	}

	/**
	 * Returns all Moves that the given knight can make, regardless of whether
	 * the captured Piece would be on its own team. <br>
	 * pre: knight != null && board != null, satisfied by calling method
	 * 
	 * @param knight
	 *            The knight to get Moves for. May not be null.
	 * @param board
	 *            The Board the knight is on. May not be null.
	 * @return An ArrayList of Moves the knight can make.
	 */
	private static ArrayList<Move> findAvailableKnightMoves(Piece knight, Board board) {
		System.out.println("Find available knight moves.");
		ArrayList<Move> availableKnightMoves = new ArrayList<Move>();

		// Eight positions to check for a knight,
		// will start at 1 o'clock and go clockwise
		int pieceRow = knight.getRow();
		int pieceCol = knight.getCol();
		Space source = board.getSpace(pieceRow, pieceCol);

		// Position 1
		Space dest = board.getSpace(pieceRow - 2, pieceCol + 1);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 2
		dest = board.getSpace(pieceRow - 1, pieceCol + 2);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 3
		dest = board.getSpace(pieceRow + 1, pieceCol + 2);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 4
		dest = board.getSpace(pieceRow + 2, pieceCol + 1);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 5
		dest = board.getSpace(pieceRow + 2, pieceCol - 1);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 6
		dest = board.getSpace(pieceRow + 1, pieceCol - 2);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 7
		dest = board.getSpace(pieceRow - 1, pieceCol - 2);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 8
		dest = board.getSpace(pieceRow - 2, pieceCol - 1);
		if (isSpaceOnBoard(dest))
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		return availableKnightMoves;
	}

	/**
	 * Returns all Moves that the given bishop can make, regardless of whether
	 * the captured Piece would be on its own team. <br>
	 * pre: bishop != null && board != null, satisfied by calling method
	 * 
	 * @param bishop
	 *            The bishop to get Moves for. May not be null.
	 * @param board
	 *            The Board the knight is on. May not be null.
	 * @return An ArrayList of Moves the bishop can make.
	 */
	private static ArrayList<Move> findAvailableBishopMoves(Piece bishop, Board board) {
		System.out.println("Find available bishop moves.");
		ArrayList<Move> availableBishopMoves = new ArrayList<Move>();

		// Four diagonals to check for a bishop; will start
		// with up/right and go clockwise

		// First diagonal--up and right
		int rowChange = -1;
		int colChange = 1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange, board));

		// Second diagonal--down and right
		rowChange = 1;
		colChange = 1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange, board));

		// Third diagonal--down and left
		rowChange = 1;
		colChange = -1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange, board));

		// Fourth diagonal--up and left
		rowChange = -1;
		colChange = -1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange, board));

		return availableBishopMoves;
	}

	/**
	 * Returns all Moves that the given rook can make, regardless of whether the
	 * captured Piece would be on its own team. <br>
	 * pre: rook != null && board != null, satisfied by calling method
	 * 
	 * @param rook
	 *            The rook to get Moves for. May not be null.
	 * @param board
	 *            The Board the knight is on. May not be null.
	 * @return An ArrayList of Moves the rook can make.
	 */
	private static ArrayList<Move> findAvailableRookMoves(Piece rook, Board board) {
		System.out.println("Find available rook moves");
		ArrayList<Move> availableRookMoves = new ArrayList<Move>();

		// Two verticals, two horizontals to check for a rook; will
		// start with verticals, then horizontals

		// First vertical--up
		int rowChange = -1;
		int colChange = 0;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange, board));

		// Second vertical--down
		rowChange = 1;
		colChange = 0;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange, board));

		// First horizontal--left
		rowChange = 0;
		colChange = -1;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange, board));

		// Second horizontal--right
		rowChange = 0;
		colChange = 1;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange, board));

		return availableRookMoves;
	}

	/**
	 * Returns all Moves that the given queen can make, regardless of whether
	 * the captured Piece would be on its own team. <br>
	 * pre: queen != null && board != null, satisfied by calling method
	 * 
	 * @param queen
	 *            The queen to get Moves for. May not be null.
	 * @param board
	 *            The Board the knight is on. May not be null.
	 * @return An ArrayList of Moves the queen can make.
	 */
	private static ArrayList<Move> findAvailableQueenMoves(Piece queen, Board board) {
		System.out.println("Find available queen moves");
		ArrayList<Move> availableQueenMoves = new ArrayList<Move>();

		// A queen moves like a rook and a bishop combined
		availableQueenMoves.addAll(findAvailableBishopMoves(queen, board));
		availableQueenMoves.addAll(findAvailableRookMoves(queen, board));
		return availableQueenMoves;
	}

	/**
	 * Returns all Moves that the given king can make, regardless of whether the
	 * captured Piece would be on its own team. <br>
	 * pre: piece != null && board != null, satisfied by calling method
	 * 
	 * @param piece
	 *            The king to get Moves for. May not be null.
	 * @param board
	 *            The Board the knight is on. May not be null.
	 * @param checkCastling
	 *            A boolean that indicates whether to check for castling moves
	 *            or not.
	 * @return An ArrayList of Moves the king can make.
	 */
	private static ArrayList<Move> findAvailableKingMoves(Piece king, Board board, boolean checkCastling) {
		System.out.println("Find available king moves");
		ArrayList<Move> availableKingMoves = new ArrayList<Move>();

		// A king can move to each of the eight surrounding squares
		// Will check starting at 1 o'clock and go clockwise
		int pieceRow = king.getRow();
		int pieceCol = king.getCol();
		Space source = board.getSpace(pieceRow, pieceCol);
		boolean pieceIsWhite = king.isWhite();

		// Position 1:
		Space dest = board.getSpace(pieceRow - 1, pieceCol + 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 2:
		dest = board.getSpace(pieceRow, pieceCol + 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 3:
		dest = board.getSpace(pieceRow + 1, pieceCol + 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 4:
		dest = board.getSpace(pieceRow + 1, pieceCol);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 5:
		dest = board.getSpace(pieceRow + 1, pieceCol - 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 6:
		dest = board.getSpace(pieceRow, pieceCol - 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 7:
		dest = board.getSpace(pieceRow - 1, pieceCol - 1);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 8:
		dest = board.getSpace(pieceRow - 1, pieceCol);
		if (isSpaceOnBoard(dest))
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Now we check for castling. Five things must be true to castle.
		// 1: King cannot have moved
		// 2: Rook involved cannot have moved
		// 3: Spaces involved are empty
		// 4: King is not in check
		// 5: Spaces between are not in check
		// The king can castle two directions

		if (checkCastling) {
			// Check kingside first
			Piece rook = board.getSpace(pieceRow, Board.MAX_COL).getPiece();
			Space space1 = board.getSpace(pieceRow, 5);
			dest = board.getSpace(pieceRow, 6);
			if (king.getTimesMoved() == 0 && !isCheck(pieceIsWhite, board) && rook != null && rook.getTimesMoved() == 0
					&& isEmpty(space1) && isEmpty(dest)
					&& findPiecesAttackingSpace(space1, pieceIsWhite, board).size() == 0
					&& findPiecesAttackingSpace(dest, pieceIsWhite, board).size() == 0)
				availableKingMoves.add(new Move(king, null, source, dest));

			// Now check queenside
			rook = board.getSpace(pieceRow, Board.MIN_COL).getPiece();
			space1 = board.getSpace(pieceRow, 3);
			dest = board.getSpace(pieceRow, 2);
			Space space3 = board.getSpace(pieceRow, 1);
			if (king.getTimesMoved() == 0 && !isCheck(pieceIsWhite, board) && rook != null && rook.getTimesMoved() == 0
					&& isEmpty(space1) && isEmpty(dest) && isEmpty(space3)
					&& findPiecesAttackingSpace(space1, pieceIsWhite, board).size() == 0
					&& findPiecesAttackingSpace(dest, pieceIsWhite, board).size() == 0)
				availableKingMoves.add(new Move(king, null, source, dest));
		}

		return availableKingMoves;
	}

	/**
	 * Returns an ArrayList of available Moves based on the given Piece
	 * position, color and direction to check. Loop exit condition is based on
	 * whether the Space is open or not. If the Piece attacks a Piece of its own
	 * color, that Move will be added to the ArrayList as well. <br>
	 * pre: rowChange == -1 || 0 || 1 and colChange == -1 || 0 || 1
	 * 
	 * @param piece
	 *            The Piece to move. May not be null.
	 * @param rowChange
	 *            The increment for the row value when looping.
	 * @param colChange
	 *            The increment for the col value when looping.
	 * @param board
	 *            The Board the Piece is on. May not be null.
	 * @return An ArrayList of available Moves for the Piece.
	 */
	private static ArrayList<Move> findAvailableLoopMoves(Piece piece, int rowChange, int colChange, Board board) {
		System.out.println("Find available loop moves.");

		// check preconditions
		if (!((rowChange == -1 || rowChange == 0 || rowChange == 1)
				&& (colChange == -1 || colChange == 0 || colChange == 1)))
			throw new IllegalArgumentException("Row change and col change must be equal to -1, 0 or 1. Row change: "
					+ rowChange + ", col change: " + colChange);

		// get the Piece's current position/Space
		ArrayList<Move> availableLoopMoves = new ArrayList<Move>();
		int pieceRow = piece.getRow();
		int pieceCol = piece.getCol();
		Space source = board.getSpace(pieceRow, pieceCol);

		// loop through the Spaces along the given path. If they are empty,
		// add the Move to the list and keep going. If not empty, add and stop.
		boolean spaceWasOpen = true;
		int r = pieceRow + rowChange;
		int c = pieceCol + colChange;
		while (spaceWasOpen) {
			if (isSpaceOnBoard(r, c)) {
				Space dest = board.getSpace(r, c);
				spaceWasOpen = isEmpty(dest);
				availableLoopMoves.add(new Move(piece, dest.getPiece(), source, dest));
				r += rowChange;
				c += colChange;
			} else
				spaceWasOpen = false;
		}

		return availableLoopMoves;
	}

	// returns true if the next row and next col are legal
	// for the given piece color and board
	// not for use by pawns
	// pre: met by calling methods
	private static boolean isLegalMove(Space space, boolean pieceIsWhite) {
		if (space == null)
			return false;

		int spaceStatus = getSpaceStatus(space);
		if (spaceStatus == SPACE_EMPTY)
			return true;
		else if (pieceIsWhite && spaceStatus == SPACE_BLACK)
			return true;
		else if (!pieceIsWhite && spaceStatus == SPACE_WHITE)
			return true;

		return false;
	}

	// returns true if the color passed in isWhite is mated
	// pre: board != null
	public static boolean isCheckMate(boolean isWhite, Board board) {

		System.out.println("is Check Mate");

		// check precondition
		if (board == null)
			throw new IllegalArgumentException("Board may not be null");

		int i = 0;
		ArrayList<Piece> pieces;
		if (isWhite)
			pieces = board.getWhitePieces();
		else
			pieces = board.getBlackPieces();

		while (i < pieces.size()) {

			// get the legal moves for this Piece
			ArrayList<Move> movesPreCheck = findLegalMoves(pieces.get(i), board, true);
			for (int j = 0; j < movesPreCheck.size(); j++) {
				// found a move that isn't still in check, return false
				if (!isCheckAfterMove(movesPreCheck.get(j), board)) {
					return false;
				}
			}

			if (pieces.get(i).getType() == Piece.TYPE_PAWN) {
				Move enPassantMove = findAvailableEnPassantMove(pieces.get(i), board);

				if (enPassantMove != null && !isCheckAfterMove(enPassantMove, board))
					return false;
			}

			i++;
		}

		return true;
	}

	/**
	 * Returns a boolean indicating whether the specified color is in check in
	 * the Board's current position. <br>
	 * pre: board != null
	 * 
	 * @param white
	 *            A boolean indicating whether to check white or black.
	 * @param board
	 *            The Board to check. May not be null.
	 * @return True if the current position is check, false otherwise.
	 */
	public static boolean isCheck(boolean white, Board board) {
		// check precondition
		if (board == null)
			throw new IllegalArgumentException("Board may not be null");

		Piece king;
		if (white)
			king = board.getWhiteKing();
		else
			king = board.getBlackKing();

		boolean isCheck;
		if (findPiecesAttackingPiece(king, board).size() > 0)
			isCheck = true;
		else
			isCheck = false;

		return isCheck;
	}

	/**
	 * Return a boolean indicating whether making the specified Move would
	 * causes check. <br>
	 * pre: move != null && board != null
	 * 
	 * @param move
	 *            The Move to test for check. May not be null.
	 * @param board
	 *            The Board to test. May not be null.
	 * @return True if making the Move would cause check, false if not.
	 */
	public static boolean isCheckAfterMove(Move move, Board board) {
		// check preconditions
		if (move == null || board == null)
			throw new IllegalArgumentException("Piece, nextSpace, board may not be null");

		// make the move, see if it is check, then undo the move
		board.makeTestMove(move);
		boolean isCheck = isCheck(move.getMovedPiece().isWhite(), board);
		board.undoMove(move);
		return isCheck;
	}

	/**
	 * Helper method for findAvailableMoves that returns a Move object for the
	 * given Piece, source Space and destination Space. <br>
	 * pre: piece != null && source != null && dest != null
	 * 
	 * @param piece
	 *            The Piece to move. May not be null.
	 * @param source
	 *            The source Space. May not be null.
	 * @param dest
	 *            The destination Space. May not be null.
	 * @return A Move object for the given parameters
	 */
	private static Move createMove(Piece piece, Space source, Space dest) {

		// check preconditions
		if (piece == null || source == null || dest == null)
			throw new IllegalArgumentException("Piece, source and dest must not be null.");

		return new Move(piece, dest.getPiece(), source, dest);
	}

	// returns true if space is empty
	// returns false if not
	// pre: none
	private static boolean isEmpty(Space space) {
		if (space == null)
			return false;

		return getSpaceStatus(space) == SPACE_EMPTY;
	}

	// returns the status of the given space
	// pre: none
	private static int getSpaceStatus(Space space) {

		if (!isSpaceOnBoard(space))
			return SPACE_OFF_BOARD;

		Piece piece = space.getPiece();

		if (piece == null)
			return SPACE_EMPTY;
		else if (piece.isWhite())
			return SPACE_WHITE;
		else
			return SPACE_BLACK;
	}

	// return true if space is on board
	// return false if space is not on board
	// pre: none
	private static boolean isSpaceOnBoard(int row, int col) {
		if (row >= Board.MIN_ROW && row <= Board.MAX_ROW && col >= Board.MIN_COL && col <= Board.MAX_COL)
			return true;
		else
			return false;
	}

	// return true if space is on board
	// return false if space is not on board
	// pre: none
	private static boolean isSpaceOnBoard(Space space) {
		if (space == null)
			return false;

		int row = space.getRow();
		int col = space.getCol();

		if (row >= Board.MIN_ROW && row <= Board.MAX_ROW && col >= Board.MIN_COL && col <= Board.MAX_COL)
			return true;
		else
			return false;
	}
}

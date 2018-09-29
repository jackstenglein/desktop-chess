import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

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
	
	/**
	 * Creates the pieces in their starting position for the given color.
	 * @param isWhite A boolean indicating whether to create the white or black pieces.
	 * @return An ArrayList of the created pieces.
	 */
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

	/**
	 * Creates the pawns in their starting position for the specified color.
	 * 
	 * @param isWhite A boolean indicating whether to create the white or black pawns.
	 * @return An ArrayList of the created pawns.
	 */
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
	 * Returns an ArrayList of Move objects that represent the legal moves for
	 * the specified Piece and Board. 
	 * 
	 * @param piece The Piece to get Moves for. May not be null.
	 * @param checkCastling  A boolean indicating whether to check for castling moves.
	 * @return An ArrayList of legal Moves for the given piece.
	 */
	public ArrayList<Move> findLegalMoves(Piece piece, boolean checkCastling) {
		// System.out.println("Find legal moves.");
		ArrayList<Move> legalMoves = findAvailableMoves(piece, checkCastling);
		removeSameColoredMoves(legalMoves, piece.isWhite());
		for (int i = 0; i < legalMoves.size(); i++)
			if (isCheckAfterMove(legalMoves.get(i))) {
				legalMoves.remove(i);
				i--;
			}

		return legalMoves;
	}
	
	/**
	 * Helper method for findAvailableMoves that removes illegal moves (IE: a
	 * piece attacks its own color) from the list of possible moves.
	 * <br>post: All illegal moves are removed from availableMoves
	 * 
	 * @param availableMoves The list of all moves a piece can make, whether it is illegal
	 *            or not. May not be null. Will be changed by this method.
	 * @param pieceIsWhite A boolean indicating whether the moved piece is white or not.
	 */
	private void removeSameColoredMoves(ArrayList<Move> availableMoves, boolean pieceIsWhite) {
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
	 * Return a boolean indicating whether making the specified move
	 * causes check.
	 * 
	 * @param move The move to test for check. May not be null.
	 * @return True if making the move would cause check, false if not.
	 */
	public boolean isCheckAfterMove(Move move) {
		if (move == null) {
			throw new IllegalArgumentException("The move to check may not be null");
		}

		// make the move, see if it is check, then undo the move
		makeTestMove(move);
		boolean isCheck = isCheck(move.getMovedPiece().isWhite());
		undoMove(move);
		return isCheck;
	}
	
	/**
	 * Returns true if the specified team is checkmated, false otherwise.
	 * @param isWhite A boolean indicating whether to check for mate on white or black.
	 * @return True if the specified team is checkmated.
	 */
	public boolean isCheckMate(boolean isWhite) {
		int i = 0;
		ArrayList<Piece> pieces = isWhite ? whitePieces : blackPieces;
	
		while (i < pieces.size()) {
			// get the legal moves for this Piece
			ArrayList<Move> movesPreCheck = findLegalMoves(pieces.get(i), true);
			for (int j = 0; j < movesPreCheck.size(); j++) {
				// found a move that isn't still in check, return false
				if (!isCheckAfterMove(movesPreCheck.get(j))) {
					return false;
				}
			}

			/*if (pieces.get(i).getType() == Piece.PieceType.PAWN) {
				Move enPassantMove = findAvailableEnPassantMove(pieces.get(i));
				if (enPassantMove != null && !isCheckAfterMove(enPassantMove))
					return false;
			}*/

			i++;
		}

		return true;
	}
	
	/**
	 * Helper method for findLegalMoves and findDefenders that returns an
	 * ArrayList of Move objects that represent all possible moves for the
	 * specified Piece and Board, regardless of whether the Piece captures
	 * another Piece of its own color.
	 * 
	 * @param piece The Piece to get moves for. May not be null.
	 * @param checkCastling  A boolean indicating whether to check for castling moves.
	 * @return An ArrayList of all Moves for the given piece, regardless of the color of capture.
	 */
	private ArrayList<Move> findAvailableMoves(Piece piece, boolean checkCastling) {
		ArrayList<Move> availableMoves = new ArrayList<Move>();

		Piece.PieceType pieceType = piece.getType();
		int pieceRow = piece.getRow();
		int pieceCol = piece.getCol();
		boolean pieceIsWhite = piece.isWhite();
		
		switch(pieceType) {
			case PAWN:
				availableMoves = findAvailablePawnMoves(pieceRow, pieceCol, pieceIsWhite);
				Move enPassant = findAvailableEnPassantMove(piece);
				if (enPassant != null) {
					availableMoves.add(enPassant);
				}
				break;
			case KNIGHT:
				availableMoves = findAvailableKnightMoves(piece);
				break;
			case BISHOP:
				availableMoves = findAvailableBishopMoves(piece);
				break;
			case ROOK:
				availableMoves = findAvailableRookMoves(piece);
				break;
			case QUEEN:
				availableMoves = findAvailableQueenMoves(piece);
				break;
			case KING:
				availableMoves = findAvailableKingMoves(piece, checkCastling);
				break;
		}

		return availableMoves;
	}
	
	/**
	 * Returns the available moves for a pawn at a given space on the board and
	 * its color, regardless of whether it would capture a Piece on its own
	 * team.
	 * 
	 * @param pieceRow The row the pawn is on.
	 * @param pieceCol The col the pawn is on.
	 * @param pieceIsWhite A boolean indicating whether the piece is white or not.
	 * @return An ArrayList of moves for the pawn.
	 */
	private ArrayList<Move> findAvailablePawnMoves(int pieceRow, int pieceCol, boolean pieceIsWhite) {

		ArrayList<Move> availablePawnMoves = new ArrayList<Move>();
		Space source = spaces[pieceRow][pieceCol];
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
		Space dest = spaces[pieceRow + rowChange][pieceCol];
		if (dest.isEmpty()) {

			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

			// check if still on starting row
			if (pieceRow == startingRow) {
				// if space two above is empty, add as a possible move
				dest = spaces[pieceRow + 2 * rowChange][pieceCol];
				if (dest.isEmpty())
					availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));
			}
		}

		// check if space diagonal left is on board and not empty
		dest = getSpace(pieceRow + rowChange, pieceCol - 1);
		if (dest != null && !dest.isEmpty())
			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

		// check if space diagonal right is on board and not empty
		dest = getSpace(pieceRow + rowChange, pieceCol + 1);
		if (dest != null && !dest.isEmpty())
			availablePawnMoves.add(new Move(source.getPiece(), dest.getPiece(), source, dest));

		return availablePawnMoves;
	}
	
	/**
	 * Returns the available en passant Move for a pawn (there will always be
	 * only one).
	 * 
	 * @param pawn The pawn to find en passant for. May not be null.
	 * @return The available en passant Move or null if none exist.
	 */
	public Move findAvailableEnPassantMove(Piece pawn) {
		int pieceRow = pawn.getRow();
		int pieceCol = pawn.getCol();
		Space source = spaces[pieceRow][pieceCol];
		
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
		Space spaceRight = getSpace(pieceRow, pieceCol + 1);
		if (spaceRight != null) {
			//System.out.println("Check en passant right");
			Piece pieceRight = spaceRight.getPiece();
			//System.out.println("Piece right: " + pieceRight);
			if (pieceRow == magicRow && pieceRight != null && pieceRight.getType() == Piece.PieceType.PAWN
					&& pieceRight.getTimesMoved() == 1 && pieceRight.hasJustMoved()) {
				//System.out.println("found en passant.");
				return new Move(pawn, pieceRight, source, getSpace(pieceRow + rowChange, pieceCol + 1));
			}
		}

		// check for en passant to the left
		Space spaceLeft = getSpace(pieceRow, pieceCol - 1);
		if (spaceLeft != null) {
			Piece pieceLeft = spaceLeft.getPiece();
			if (pieceRow == magicRow && pieceLeft != null && pieceLeft.getType() == Piece.PieceType.PAWN
					&& pieceLeft.getTimesMoved() == 1 && pieceLeft.hasJustMoved()) {
				//System.out.println("found en passant.");
				return new Move(pawn, pieceLeft, source, getSpace(pieceRow + rowChange, pieceCol - 1));
			}
		}

		return null;
	}
	
	/**
	 * Returns all moves that the given knight can make, regardless of whether
	 * the captured Piece would be on its own team. 
	 * 
	 * @param knight The knight to get moves for. May not be null.
	 * @return An ArrayList of moves the knight can make.
	 */
	private ArrayList<Move> findAvailableKnightMoves(Piece knight) {
		ArrayList<Move> availableKnightMoves = new ArrayList<Move>();

		// Eight positions to check for a knight,
		// will start at 1 o'clock and go clockwise
		int pieceRow = knight.getRow();
		int pieceCol = knight.getCol();
		Space source = spaces[pieceRow][pieceCol];

		// Position 1
		Space dest = getSpace(pieceRow - 2, pieceCol + 1);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 2
		dest = getSpace(pieceRow - 1, pieceCol + 2);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 3
		dest = getSpace(pieceRow + 1, pieceCol + 2);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 4
		dest = getSpace(pieceRow + 2, pieceCol + 1);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 5
		dest = getSpace(pieceRow + 2, pieceCol - 1);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 6
		dest = getSpace(pieceRow + 1, pieceCol - 2);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 7
		dest = getSpace(pieceRow - 1, pieceCol - 2);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		// Position 8
		dest = getSpace(pieceRow - 2, pieceCol - 1);
		if (dest != null)
			availableKnightMoves.add(new Move(knight, dest.getPiece(), source, dest));

		return availableKnightMoves;
	}
	
	/**
	 * Returns all moves that the given bishop can make, regardless of whether
	 * the captured Piece would be on its own team.
	 * 
	 * @param bishop The bishop to get moves for. May not be null.
	 * @return An ArrayList of moves the bishop can make.
	 */
	private ArrayList<Move> findAvailableBishopMoves(Piece bishop) {
		ArrayList<Move> availableBishopMoves = new ArrayList<Move>();

		// Four diagonals to check for a bishop; will start
		// with up/right and go clockwise

		// First diagonal--up and right
		int rowChange = -1;
		int colChange = 1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange));

		// Second diagonal--down and right
		rowChange = 1;
		colChange = 1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange));

		// Third diagonal--down and left
		rowChange = 1;
		colChange = -1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange));

		// Fourth diagonal--up and left
		rowChange = -1;
		colChange = -1;
		availableBishopMoves.addAll(findAvailableLoopMoves(bishop, rowChange, colChange));

		return availableBishopMoves;
	}
	
	/**
	 * Returns all moves that the given rook can make, regardless of whether the
	 * captured piece would be on its own team.
	 * 
	 * @param rook The rook to get moves for. May not be null.
	 * @return An ArrayList of moves the rook can make.
	 */
	private ArrayList<Move> findAvailableRookMoves(Piece rook) {
		ArrayList<Move> availableRookMoves = new ArrayList<Move>();

		// Two verticals, two horizontals to check for a rook; will
		// start with verticals, then horizontals

		// First vertical--up
		int rowChange = -1;
		int colChange = 0;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange));

		// Second vertical--down
		rowChange = 1;
		colChange = 0;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange));

		// First horizontal--left
		rowChange = 0;
		colChange = -1;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange));

		// Second horizontal--right
		rowChange = 0;
		colChange = 1;
		availableRookMoves.addAll(findAvailableLoopMoves(rook, rowChange, colChange));

		return availableRookMoves;
	}
	
	/**
	 * Returns all moves that the given queen can make, regardless of whether
	 * the captured piece would be on its own team.
	 * 
	 * @param queen The queen to get moves for. May not be null.
	 * @return An ArrayList of moves the queen can make.
	 */
	private ArrayList<Move> findAvailableQueenMoves(Piece queen) {
		ArrayList<Move> availableQueenMoves = new ArrayList<Move>();
		// A queen moves like a rook and a bishop combined
		availableQueenMoves.addAll(findAvailableBishopMoves(queen));
		availableQueenMoves.addAll(findAvailableRookMoves(queen));
		return availableQueenMoves;
	}
	
	/**
	 * Returns all moves that the given king can make, regardless of whether the
	 * captured piece would be on its own team.
	 * 
	 * @param piece The king to get moves for. May not be null.
	 * @param checkCastling A boolean that indicates whether to check for castling moves.
	 * @return An ArrayList of moves the king can make.
	 */
	private ArrayList<Move> findAvailableKingMoves(Piece king, boolean checkCastling) {
		ArrayList<Move> availableKingMoves = new ArrayList<Move>();

		// A king can move to each of the eight surrounding squares
		// Will check starting at 1 o'clock and go clockwise
		int pieceRow = king.getRow();
		int pieceCol = king.getCol();
		Space source = spaces[pieceRow][pieceCol];
		boolean pieceIsWhite = king.isWhite();

		// Position 1:
		Space dest = getSpace(pieceRow - 1, pieceCol + 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 2:
		dest = getSpace(pieceRow, pieceCol + 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 3:
		dest = getSpace(pieceRow + 1, pieceCol + 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 4:
		dest = getSpace(pieceRow + 1, pieceCol);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 5:
		dest = getSpace(pieceRow + 1, pieceCol - 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 6:
		dest = getSpace(pieceRow, pieceCol - 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 7:
		dest = getSpace(pieceRow - 1, pieceCol - 1);
		if (dest != null)
			availableKingMoves.add(new Move(king, dest.getPiece(), source, dest));

		// Position 8:
		dest = getSpace(pieceRow - 1, pieceCol);
		if (dest != null)
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
			Piece rook = getSpace(pieceRow, Board.MAX_COL).getPiece();
			Space space1 = getSpace(pieceRow, 5);
			dest = getSpace(pieceRow, 6);
			if (king.getTimesMoved() == 0 && !isCheck(pieceIsWhite) && rook != null && rook.getTimesMoved() == 0
					&& space1.isEmpty() && dest.isEmpty()
					&& findPiecesAttackingSpace(space1, pieceIsWhite).size() == 0
					&& findPiecesAttackingSpace(dest, pieceIsWhite).size() == 0)
				availableKingMoves.add(new Move(king, null, source, dest));

			// Now check queenside
			rook = getSpace(pieceRow, Board.MIN_COL).getPiece();
			space1 = getSpace(pieceRow, 3);
			dest = getSpace(pieceRow, 2);
			Space space3 = getSpace(pieceRow, 1);
			if (king.getTimesMoved() == 0 && !isCheck(pieceIsWhite) && rook != null && rook.getTimesMoved() == 0
					&& space1.isEmpty() && dest.isEmpty() && space3.isEmpty()
					&& findPiecesAttackingSpace(space1, pieceIsWhite).size() == 0
					&& findPiecesAttackingSpace(dest, pieceIsWhite).size() == 0)
				availableKingMoves.add(new Move(king, null, source, dest));
		}

		return availableKingMoves;
	}
	
	/**
	 * Returns a boolean indicating whether the specified color is in check in
	 * the board's current configuration.
	 * 
	 * @param white A boolean indicating whether to check white or black.
	 * @return True if the current position is check, false otherwise.
	 */
	public boolean isCheck(boolean isWhite) {
		Piece king = isWhite ? whiteKing : blackKing;
		boolean isCheck = findPiecesAttackingPiece(king).size() > 0;
		return isCheck;
	}
	
	/**
	 * Returns an ArrayList of pieces of the given color that can attack the
	 * given space on the board.
	 * 
	 * @param spaceToCheck The space to find attackers for. May not be null.
	 * @param isWhite A boolean indicating whether to search for white or black
	 *            attackers.
	 * @return An ArrayList of pieces attacking the given space, sorted by increasing
	 *         order of value.
	 */
	public ArrayList<Piece> findPiecesAttackingSpace(Space spaceToCheck, boolean isWhite) {
		if (spaceToCheck == null) {
			throw new IllegalArgumentException("Space and board may not be null.");
		}

		ArrayList<Piece> piecesAttacking = new ArrayList<Piece>();
		ArrayList<Piece> opposingTeam = isWhite ? blackPieces : whitePieces;

		// Check the attacking pieces to see if the current piece occupies a
		// space they can move to. You cannot capture with castling, so don't
		// check castling moves.
		for (int i = 0; i < opposingTeam.size(); i++) {
			if (!opposingTeam.get(i).isCaptured()) {
				ArrayList<Move> moves = findAvailableMoves(opposingTeam.get(i), false);

				for (int j = 0; j < moves.size(); j++)
					if (moves.get(j).getDestination().equals(spaceToCheck)) {
						piecesAttacking.add(opposingTeam.get(i));
						break;
					}
			}
		}

		Collections.sort(piecesAttacking);
		return piecesAttacking;
	}
	
	/**
	 * Returns an ArrayList of pieces attacking the specified piece in the board's
	 * current configuration.
	 * 
	 * @param piece The piece to check for attackers. May not be null.
	 * @return An ArrayList of pieces attacking the specified piece.
	 */
	public ArrayList<Piece> findPiecesAttackingPiece(Piece piece) {
		if (piece == null) {
			throw new IllegalArgumentException("The piece to check for attackers may not be null.");
		}

		Space currentSpace = spaces[piece.getRow()][piece.getCol()];
		return findPiecesAttackingSpace(currentSpace, piece.isWhite());
	}
	
	



	
	/**
	 * Returns an ArrayList of available moves based on the given piece
	 * position, color and direction to check. Loop exit condition is based on
	 * whether the Space is open or not. If the piece attacks a piece of its own
	 * color, that Move will be added to the ArrayList as well. <br>
	 * pre: rowChange == -1 || 0 || 1 and colChange == -1 || 0 || 1
	 * 
	 * @param piece
	 *            The Piece to move. May not be null.
	 * @param rowChange
	 *            The increment for the row value when looping.
	 * @param colChange
	 *            The increment for the col value when looping.
	 * @return An ArrayList of available Moves for the Piece.
	 */
	private ArrayList<Move> findAvailableLoopMoves(Piece piece, int rowChange, int colChange) {
		//System.out.println("Find available loop moves.");

		// check preconditions
		if (!((rowChange == -1 || rowChange == 0 || rowChange == 1)
				&& (colChange == -1 || colChange == 0 || colChange == 1)))
			throw new IllegalArgumentException("Row change and col change must be equal to -1, 0 or 1. Row change: "
					+ rowChange + ", col change: " + colChange);

		// get the Piece's current position/Space
		ArrayList<Move> availableLoopMoves = new ArrayList<Move>();
		int pieceRow = piece.getRow();
		int pieceCol = piece.getCol();
		Space source = spaces[pieceRow][pieceCol];

		// loop through the Spaces along the given path. If they are empty,
		// add the Move to the list and keep going. If not empty, add and stop.
		boolean spaceWasOpen = true;
		int r = pieceRow + rowChange;
		int c = pieceCol + colChange;
		while (spaceWasOpen) {
			if (r >= MIN_ROW && r <= MAX_ROW && c >= MIN_COL && c<= MAX_COL) {
				Space dest = getSpace(r, c);
				spaceWasOpen = dest != null && dest.isEmpty();
				availableLoopMoves.add(new Move(piece, dest.getPiece(), source, dest));
				r += rowChange;
				c += colChange;
			} else
				spaceWasOpen = false;
		}

		return availableLoopMoves;
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
	
	/**
	 * Paints this board in the specified graphics context. 
	 * This method paints the spaces in the board, as well as 
	 * the captured and active pieces.
	 * 
	 * @param graphic The graphics context to paint in. Must not be null.
	 */
	public void paint(Graphics graphic) {
		if (graphic == null) {
			throw new IllegalArgumentException("The graphics context cannot be null.");
		}
		
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
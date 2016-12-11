import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AI {

	private Board board_;
	private Piece pieceToMove_;
	private ArrayList<Piece> aiPieces_;
	private ArrayList<Piece> enemyPieces_;
	private ArrayList<Move> possibleMoves_;
	private int difficulty_;
	private boolean isWhite_;

	public AI(Board board,
			/* ArrayList<Piece> blackPieces, ArrayList<Piece> whitePieces, */ boolean isWhite, int difficulty) {
		board_ = board;
		isWhite_ = isWhite;

		if (isWhite_) {
			aiPieces_ = board_.getWhitePieces();
			enemyPieces_ = board_.getBlackPieces();
		} else {
			aiPieces_ = board_.getBlackPieces();
			enemyPieces_ = board_.getWhitePieces();
		}

		possibleMoves_ = new ArrayList<Move>();
		difficulty_ = difficulty;
	}

	public boolean isWhite() {
		return isWhite_;
	}

	public Piece getNextPiece() {

		Piece piece = aiPieces_.get((int) (Math.floor(Math.random() * aiPieces_.size())));

		while (MoveValidator.findLegalMoves(piece, board_, true).size() == 0) {
			piece = aiPieces_.get((int) (Math.floor(Math.random() * aiPieces_.size())));
		}

		System.out.println("Piece chosen by AI: " + piece);
		return piece;
	}

	public Move getNextMove(Piece piece) {
		possibleMoves_.addAll(MoveValidator.findLegalMoves(piece, board_, true));
		Move move = possibleMoves_.get((int) Math.floor(Math.random() * possibleMoves_.size()));
		possibleMoves_.clear();
		return move;
	}

	public Move getNextMove() {
		ArrayList<Move> possibleMoves = getAllPossibleMoves();
		Map<Move, Integer> moveValues = computeValuesForMoves(possibleMoves);
		System.out.println("MoveValues:\n" + moveValues);

		int max = Integer.MIN_VALUE;
		Move maxMove = null;
		for (Move move : moveValues.keySet()) {
			if (moveValues.get(move) > max) {
				max = moveValues.get(move);
				maxMove = move;
			}
		}

		System.out.println("Best move according to AI: " + maxMove);
		return maxMove;
	}

	private ArrayList<Move> getAllPossibleMoves() {

		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for (int i = 0; i < aiPieces_.size(); i++) {
			possibleMoves.addAll(MoveValidator.findLegalMoves(aiPieces_.get(i), board_, true));
		}

		return possibleMoves;
	}

	private Map<Move, Integer> computeValuesForMoves(ArrayList<Move> possibleMoves) {
		Map<Move, Integer> moveValues = new HashMap<Move, Integer>();
		for (int i = 0; i < possibleMoves.size(); i++) {
			Move move = possibleMoves.get(i);
			System.out.println("Move: " + move);
			board_.makeTestMove(move);
			moveValues.put(move, computeValueForBoard());
			board_.undoMove(move);
		}

		// System.out.println(moveValues);

		return moveValues;
	}

	private int computeValueForBoard() {
		int value = 0;

		if (MoveValidator.isCheckMate(!isWhite_, board_))
			return Integer.MAX_VALUE;
		else {
			value += getMaterialDifference();
			System.out.println("Value after Material Difference: " + value);
			value += calculateDefensiveScore();
			System.out.println("Value after Defensive Score: " + value);
			value += calculateAttackingScore();
			System.out.println("Value after Attacking Score: " + value);
			System.out.println();
		}

		return value;
	}

	/**
	 * Private helper method for computeValueForBoard that scores how well the
	 * AI's pieces are defended on the current board. This value is calculated
	 * by finding the attackers and defenders for each piece and adding or subtracting
	 * their value from the score. <br>
	 * pre: none
	 * 
	 * @return An int that represents this board's defensive score.
	 */
	private int calculateDefensiveScore() {

		int defensiveScore = 0;

		for (Piece current : aiPieces_) {
			ArrayList<Piece> defendingPieces = MoveValidator.findDefendingPieces(current, board_);
			ArrayList<Piece> attackingPieces = MoveValidator.findPiecesAttackingPiece(current, board_);

			// assume they take with their lowest score piece
			if (attackingPieces.size() > 0) {
				defensiveScore -= current.getValue();

				// our defenders can keep taking as long as they want
				// they take the ith piece in the attacking list.
				for (int i = 0; i < defendingPieces.size() && i < attackingPieces.size(); i++) {
					defensiveScore += attackingPieces.get(i).getValue();
				}

				// The attackers recapture as long as they want
				// they take the ith-1 piece in the defending list
				for (int i = 1; i < attackingPieces.size() && i <= defendingPieces.size(); i++) {
					defensiveScore -= defendingPieces.get(i - 1).getValue();
				}
			}
		}

		return defensiveScore;
	}

	/**
	 * Private helper method for computeValueForBoard() that scores how well the AI's
	 * pieces are attacking the enemy pieces.
	 * <br>pre: none
	 * 
	 * @return An int that represents the AI's attacking score.
	 */
	private int calculateAttackingScore() {

		int attackingScore = 0;

		for (Piece current : enemyPieces_) {
			ArrayList<Piece> defendingPieces = MoveValidator.findDefendingPieces(current, board_);
			ArrayList<Piece> attackingPieces = MoveValidator.findPiecesAttackingPiece(current, board_);

			// assume we take with our lowest score piece
			if (attackingPieces.size() > 0 && attackingPieces.get(0).getValue() < current.getValue()) {
				attackingScore += current.getValue();

				// their defenders can keep taking as long as they want
				// they take the ith piece in the attacking list.
				for (int i = 0; i < defendingPieces.size() && i < attackingPieces.size(); i++) {
					attackingScore -= attackingPieces.get(i).getValue();
				}

				// Our attackers recapture as long as they want
				// they take the ith-1 piece in the defending list
				for (int i = 1; i < attackingPieces.size() && i <= defendingPieces.size(); i++) {
					attackingScore += defendingPieces.get(i - 1).getValue();
				}
			}
		}

		return attackingScore;
	}

	// takes a piece and a possibleMove and determines the forking value for the
	// piece
	// forking value is equal to the lower value of the two pieces, unless the
	// piece is the king
	// pre: piece and possibleMove are not null
	public int computeForkingValue(Piece piece, Space possibleMove) {
		// check precondition
		if (piece == null || possibleMove == null)
			throw new IllegalArgumentException("Piece and possible move may not be null");

		// move the piece to the possibleMove
		Space currentSpace = board_.getSpace(piece.getRow(), piece.getCol());
		currentSpace.setPiece(null);

		Piece capturedPiece = possibleMove.getPiece();
		if (capturedPiece != null) {
			capturedPiece.setCaptured(true);
		}
		piece.setRow(possibleMove.getRow());
		piece.setCol(possibleMove.getCol());
		possibleMove.setPiece(piece);

		// find the pieces attacked by the piece at its new position
		ArrayList<Piece> attackedPieces = MoveValidator.findAttackedPieces(piece, board_);

		// move the pieces back to where they were
		if (capturedPiece != null) {
			capturedPiece.setCaptured(false);
		}

		possibleMove.setPiece(capturedPiece);
		piece.setRow(currentSpace.getRow());
		piece.setCol(currentSpace.getCol());
		currentSpace.setPiece(piece);

		// find the average value of the pieces
		int average = 0;
		if (attackedPieces.size() > 1) {
			for (int i = 0; i < attackedPieces.size(); i++) {
				average += attackedPieces.get(i).getValue();
			}

			average /= attackedPieces.size();
		}

		System.out.println("Forking value of the piece at space: " + possibleMove + " = " + average);
		return average;
	}

	// computes and returns the value of a trade between the two pieces
	// based on their value and the total material each side has
	// pre: AIPiece and playerPiece are not null
	private int computeValueForTrade(Piece AIPiece, Piece playerPiece) {
		// check precondition
		if (AIPiece == null || playerPiece == null)
			throw new IllegalArgumentException("AIPiece and playerPiece may not be null!");

		int valueForTrade = 0;
		int whiteMaterial = getTotalMaterial(true);
		int blackMaterial = getTotalMaterial(false);
		int pieceDifference = getPieceValueDifference(AIPiece, playerPiece);

		valueForTrade += pieceDifference;
		if (valueForTrade == 0) {
			if (isWhite_ && whiteMaterial > blackMaterial) {
				System.out.println("AI has equal trade opportunity while up in material--good");
				valueForTrade++;
			} else if (isWhite_ && whiteMaterial < blackMaterial) {
				System.out.println("AI has equal trade opportunity while down in material--bad");
				valueForTrade--;
			} else if (!isWhite_ && whiteMaterial < blackMaterial) {
				System.out.println("AI has equal trade opportunity while up in material--good");
				valueForTrade++;
			} else if (!isWhite_ && whiteMaterial > blackMaterial) {
				System.out.println("AI has equal trade opportunity while down in material--bad");
				valueForTrade--;
			}
		}

		return valueForTrade;
	}

	/**
	 * Returns the material difference between the two teams in terms of the
	 * AI's lead (or lack thereof). For example, if the AI is playing as white
	 * and white is up by a knight, this method will return 3. If the AI was
	 * playing as black in the same situation though, the method would return
	 * -3. In other words, this method returns the amount of material the AI is
	 * winning or losing by. <br>
	 * pre: none
	 * 
	 * @return The amount of material the AI is up or down by.
	 */
	private int getMaterialDifference() {
		int whiteMaterial = getTotalMaterial(true);
		int blackMaterial = getTotalMaterial(false);
		return isWhite_ ? whiteMaterial - blackMaterial : blackMaterial - whiteMaterial;
	}

	/**
	 * Returns the total material that the given team has left on the board.
	 * <br>
	 * pre: none
	 * 
	 * @param white
	 *            A boolean indicating whether to check the white team or the
	 *            black team. True for white, false for black.
	 * @return The total material that the given team has in play.
	 */
	private int getTotalMaterial(boolean white) {
		ArrayList<Piece> team;
		if (white)
			team = board_.getWhitePieces();
		else
			team = board_.getBlackPieces();

		int totalMaterial = 0;
		for (int i = 0; i < team.size(); i++)
			if (!team.get(i).isCaptured())
				totalMaterial += team.get(i).getValue();

		return totalMaterial;
	}

	// returns the value of a trade between the two pieces
	// Ex: our knight attacks a queen = 9 - 3 = 6
	// pre: AIPiece && playerPiece may not be null
	private int getPieceValueDifference(Piece AIPiece, Piece playerPiece) {
		// check precondition
		if (AIPiece == null || playerPiece == null)
			throw new IllegalArgumentException("AIPiece and playerPiece may not be null");

		System.out.println("Piece value difference: " + (playerPiece.getValue() - AIPiece.getValue()));
		return playerPiece.getValue() - AIPiece.getValue();
	}
}

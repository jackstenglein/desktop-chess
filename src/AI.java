import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AI 
{
	
	private Board board_;
	private Piece pieceToMove_;
	private ArrayList<Piece> aiPieces_;
	private ArrayList<Piece> enemyPieces_;
	private ArrayList<Move> possibleMoves_;
	private int difficulty_;
	private boolean isWhite_;

	public AI(Board board, /*ArrayList<Piece> blackPieces, ArrayList<Piece> whitePieces,*/ boolean isWhite, int difficulty)
	{
		board_ = board;
		isWhite_ = isWhite;
		
		if(isWhite_) {
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
		
		
		Piece piece = aiPieces_.get((int)(Math.floor(Math.random()*aiPieces_.size())));
		
		while(MoveValidator.findLegalMoves(piece, board_, true).size() == 0)
		{
			piece = aiPieces_.get((int)(Math.floor(Math.random()*aiPieces_.size())));
		}
		
		System.out.println("Piece chosen by AI: " + piece);
		return piece;
	}
	
	public Move getNextMove(Piece piece)
	{
		possibleMoves_.addAll(MoveValidator.findLegalMoves( piece, board_, true) );
		Move move = possibleMoves_.get((int)Math.floor(Math.random() * possibleMoves_.size()));
		possibleMoves_.clear();
		return move;
	}
	
	public Move getNextMove() {
		ArrayList<Move> possibleMoves = getAllPossibleMoves();
		Map<Move, Integer> moveValues = computeValuesForMoves(possibleMoves);
		//System.out.println("MoveValues:\n" + moveValues);
		
		int max = Integer.MIN_VALUE;
		Move maxMove = null;
		for(Move move : moveValues.keySet()) {
			if(moveValues.get(move) > max) {
				max = moveValues.get(move);
				maxMove = move;
			}
		}
		
		return maxMove;
	}
	
	private ArrayList<Move> getAllPossibleMoves() {
		
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for(int i = 0; i < aiPieces_.size(); i++) {
			possibleMoves.addAll(MoveValidator.findLegalMoves(aiPieces_.get(i), board_, true));
		}
		
		return possibleMoves;
	}
	
	private Map<Move, Integer> computeValuesForMoves(ArrayList<Move> possibleMoves) {
		Map<Move, Integer> moveValues = new HashMap<Move, Integer>();
		for(int i = 0; i < possibleMoves.size(); i++) {
			Move move = possibleMoves.get(i);
			//System.out.println("Move: " + move + " Hash: " + move.hashCode());
			board_.makeTestMove(move);
			moveValues.put(move, computeValueForBoard());
			board_.undoMove(move);
		}
		
		System.out.println(moveValues);
		
		return moveValues;
	}
	
	
	private int computeValueForBoard()
	{
		int value = 0;
		
		if(MoveValidator.isCheckMate(!isWhite_, board_))
			return Integer.MAX_VALUE;
		else
			value += getMaterialDifference();
		
		return value;
	}
	
	//takes a piece and a possibleMove and determines the forking value for the piece
	//forking value is equal to the lower value of the two pieces, unless the piece is the king
	//pre: piece and possibleMove are not null
	public int computeForkingValue(Piece piece, Space possibleMove)
	{
		//check precondition
		if(piece == null || possibleMove == null)
			throw new IllegalArgumentException("Piece and possible move may not be null");
		
		//move the piece to the possibleMove
		Space currentSpace = board_.getSpace(piece.getRow(), piece.getCol());
		currentSpace.setPiece(null);
		
		Piece capturedPiece = possibleMove.getPiece();
		if(capturedPiece != null)
		{
			capturedPiece.setCaptured(true);
		}
		piece.setRow(possibleMove.getRow());
		piece.setCol(possibleMove.getCol());
		possibleMove.setPiece(piece);
		

		//find the pieces attacked by the piece at its new position
		ArrayList<Piece> attackedPieces = MoveValidator.findAttackedPieces(piece, board_);

		
		//move the pieces back to where they were
		if(capturedPiece != null)
		{
			capturedPiece.setCaptured(false);	
		}

		possibleMove.setPiece(capturedPiece);
		piece.setRow(currentSpace.getRow());
		piece.setCol(currentSpace.getCol());
		currentSpace.setPiece(piece);
		
		
		//find the average value of the pieces
		int average = 0;
		if(attackedPieces.size() > 1)
		{
			for(int i = 0; i < attackedPieces.size(); i++)
			{
				average += attackedPieces.get(i).getValue();
			}
			
			average /= attackedPieces.size();
		}
		
		System.out.println("Forking value of the piece at space: " + possibleMove + " = " + average);
		return average;
	}
	
	
	//computes and returns the value of a trade between the two pieces
	//based on their value and the total material each side has
	//pre: AIPiece and playerPiece are not null
	private int computeValueForTrade(Piece AIPiece, Piece playerPiece)
	{
		//check precondition
		if(AIPiece == null || playerPiece == null)
			throw new IllegalArgumentException("AIPiece and playerPiece may not be null!");
		
		int valueForTrade = 0;
		int whiteMaterial = getTotalMaterial(true);
		int blackMaterial = getTotalMaterial(false);
		int pieceDifference = getPieceValueDifference(AIPiece, playerPiece);
		
		valueForTrade += pieceDifference;
		if(valueForTrade == 0)
		{
			if(isWhite_ && whiteMaterial > blackMaterial)
			{
				System.out.println("AI has equal trade opportunity while up in material--good");
				valueForTrade++;
			}
			else if(isWhite_ && whiteMaterial < blackMaterial)
			{
				System.out.println("AI has equal trade opportunity while down in material--bad");
				valueForTrade--;
			}
			else if(!isWhite_ && whiteMaterial < blackMaterial)
			{
				System.out.println("AI has equal trade opportunity while up in material--good");
				valueForTrade++;
			}
			else if(!isWhite_ && whiteMaterial > blackMaterial)
			{
				System.out.println("AI has equal trade opportunity while down in material--bad");
				valueForTrade--;
			}
		}
		
		return valueForTrade;
	}
	
	private int getMaterialDifference() {
		int whiteMaterial = getTotalMaterial(true);
		int blackMaterial = getTotalMaterial(false);
		System.out.println("White material: " + whiteMaterial);
		System.out.println("Black material: " + blackMaterial);
		System.out.println("\n");
		return isWhite_ ? whiteMaterial - blackMaterial : blackMaterial - whiteMaterial;
	}
	
	//returns the value of all the material that the team has in play
	//pre: none
	private int getTotalMaterial(boolean white)
	{
		ArrayList<Piece> team;
		if(white)
			team = board_.getWhitePieces();
		else
			team = board_.getBlackPieces();
		
		int totalMaterial = 0;
		for(int i = 0; i < team.size(); i++)
			if(!team.get(i).isCaptured())
				totalMaterial += team.get(i).getValue();
		
		//System.out.println("Total material for " + (white ? "white :" : "black :") + totalMaterial);
		return totalMaterial;
	}
	
	//returns the value of a trade between the two pieces
	//Ex: our knight attacks a queen = 9 - 3 = 6
	//pre: AIPiece && playerPiece may not be null
	private int getPieceValueDifference(Piece AIPiece, Piece playerPiece)
	{
		//check precondition
		if(AIPiece == null || playerPiece == null)
			throw new IllegalArgumentException("AIPiece and playerPiece may not be null");
		
		System.out.println("Piece value difference: " + (playerPiece.getValue() - AIPiece.getValue() ) );
		return playerPiece.getValue() - AIPiece.getValue();
	}
}

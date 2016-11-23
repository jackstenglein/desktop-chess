import java.util.ArrayList;

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
	
	public Space getSelectedSpace() {
		
		
		pieceToMove_ = aiPieces_.get((int)(Math.floor(Math.random()*aiPieces_.size())));
		
		while(MoveValidator.findLegalMoves(pieceToMove_, board_, true).size() == 0)
		{
			pieceToMove_ = aiPieces_.get((int)(Math.floor(Math.random()*aiPieces_.size())));
		}
		
		return board_.getSpace(pieceToMove_.getRow(), pieceToMove_.getCol());
	}
	
	public Move getNextMove()
	{
		
		possibleMoves_.addAll(MoveValidator.findLegalMoves( pieceToMove_, board_, true) );
		return possibleMoves_.get((int)Math.floor(Math.random() * possibleMoves_.size()));
	}
	
	private int[] computeValuesForMoves(Piece piece) 
	{
		return null;
	}
	
	private int computeValueForBoardConfiguration()
	{
		return 0;
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
	
	private void movePiece(Piece piece, Space nextSpace)
	{
		
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
	
	//returns the value of all the material that the team has in play
	//pre: none
	private int getTotalMaterial(boolean white)
	{
		ArrayList<Piece> team;
		if(white)
			team = aiPieces_;
		else
			team = enemyPieces_;
		
		int totalMaterial = 0;
		for(int i = 0; i < team.size(); i++)
			if(!team.get(i).isCaptured())
				totalMaterial += team.get(i).getValue();
		
		System.out.println("Total material for " + (white ? "white :" : "black :") + totalMaterial);
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

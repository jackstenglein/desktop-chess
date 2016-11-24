
public class Space {
	
	//instance variables
	private Piece piece_;
	private int row_;
	private int col_;
	private boolean isSelected_;
	private boolean isPossibleMove_;
	private boolean isTakingMove_;
	
	//initializes with row and col to specified values
	//piece is set to null
	//pre: Board.MIN_ROW <= row <= Board.MAX_ROW, Board.MIN_COL <= col <= Board.MAX_COL
	public Space(int row, int col)
	{
		//check precondition
		if(row < Board.MIN_ROW || row > Board.MAX_ROW || col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Row/col are out of bounds. Row: " + row + ", col: " + col);
		
		row_= row;
		col_ = col;
		piece_ = null;
	}
	
	//initializes with row, col and piece to specified values
	//pre: Board.MIN_ROW <= row <= Board.MAX_ROW, Board.MIN_COL <= col <= Board.MAX_COL
	public Space(int row, int col, Piece piece)
	{
		//check precondition
		if(row < Board.MIN_ROW || row > Board.MAX_ROW || col < Board.MIN_COL || col > Board.MAX_COL)
			throw new IllegalArgumentException("Row/col are out of bounds. Row: " + row + ", col: " + col);
		
		row_= row;
		col_ = col;
		piece_ = piece;
	}
	
	//sets the space to selected or not selected
	//pre: none
	public void setSelected(boolean selected)
	{
		isSelected_ = selected;
	}
	
	//returns the value of isSelected
	//pre: none
	public boolean isSelected()
	{
		return isSelected_;
	}
	
	//inform the space of whether or not it is a taking move
	//pre: none
	public void setTakingMove(boolean isTakingMove)
	{
		isTakingMove_ = isTakingMove;
	}
	
	public boolean isTakingMove()
	{
		return isTakingMove_;
	}
	
	//inform the space of whether or not it is a possible move
	//pre: none
	public void setPossibleMove(boolean isPossibleMove)
	{
		isPossibleMove_ = isPossibleMove;
	}
	
	//returns the value of isPossibleMove
	//pre: none
	public boolean isPossibleMove()
	{
		return isPossibleMove_;
	}
	
	//sets the piece to the specified value, can be null
	//pre: none
	public void setPiece(Piece piece)
	{
		piece_ = piece;
	}
	
	//returns the piece
	//pre: none
	public Piece getPiece()
	{
		return piece_;
	}
	
	//returns whether the space is empty or not
	//pre: none
	public boolean isEmpty()
	{
		return (piece_ == null);
	}
	
	//returns the row
	//pre: none
	public int getRow()
	{
		return row_;
	}
	
	//returns the col
	//pre: none
	public int getCol()
	{
		return col_;
	}
	
	//returns the row, col and piece concatenated in a string
	//pre: none
	public String toString()
	{
		String result = "Row: " + row_ + ", Col: " + col_ + "Piece " + piece_;
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col_;
		result = prime * result + row_;
		return result;
	}

	public boolean equals(Object other) {
		
		if(other instanceof Space) {
			//safe to cast
			Space otherSpace = (Space)other;
			
			if(row_ == otherSpace.row_ && col_ == otherSpace.col_)
				return true;
		}
		
		return false;
	}
}
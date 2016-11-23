public class Timer {
	
	//class constants
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	//instance variables
	private int blackTime_; //milliseconds
	private int whiteTime_; //milliseconds
	
	
	//initializes the timer values to the given time
	//pre: startTime greater than 0
	public Timer(int startTime)
	{
		//check precondition
		if(startTime <= 0)
			throw new IllegalArgumentException("Timer start time must be greater than 0: " + startTime);
		
		blackTime_ = startTime;
		whiteTime_ = startTime;
	}
	
	//decreases the timer for the given player by the given amount
	//pre: decrement > 0
	public void decrementTime(int decrement, boolean isWhite)
	{
		//check precondition
		if(decrement <= 0)
			throw new IllegalArgumentException("Timer decrement must be greater than 0: " + decrement);
		
		if(isWhite)
			whiteTime_ -= decrement;
		else
			blackTime_ -= decrement;
	}
	
	//increases the timer for the given player by the given amount
	//pre: increment >= 0
	public void incrementTime(int increment, boolean isWhite)
	{
		//check precondition
		if(increment < 0)
			throw new IllegalArgumentException("Timer increment must be greater than or equal to 0: " + increment);
		
		if(isWhite)
			whiteTime_ += increment;
		else
			blackTime_ += increment;
	}
	
	//return the value of the timer for the specific player
	//pre: none
	public int getRemainingTime(boolean isWhite)
	{		
		if(isWhite)
			return whiteTime_;
		else
			return blackTime_;
	}
	
	//set the time for both timers to the specified time
	//pre: time > 0
	public void setTime(int time)
	{
		//check precondition
		if(time <= 0)
			throw new IllegalArgumentException("Time must be greater than 0: " + time);
		
		whiteTime_ = time;
		blackTime_ = time;
	}	
}
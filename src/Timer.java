
public class Timer {
	
	
	//instance variables
	private int blackTime; //milliseconds
	private int whiteTime; //milliseconds
	

	/**
	 * Creates a new Timer object with both player's remaining
	 * time initialized to the starting value.
	 * 
	 * @param startTime The initial time for both players. Must be greater than 0.
	 */
	public Timer(int startTime) {
		setTime(startTime);
	}
	
	/**
	 * Sets the time remaining for both players to the specified time.
	 * 
	 * @param time The time in milliseconds. Must be greater than 0.
	 */
	public void setTime(int time) {
		if(time <= 0) {
			throw new IllegalArgumentException("Time must be greater than 0: " + time);
		}	
		whiteTime = time;
		blackTime = time;
	}
	
	/**
	 * Decreases the time remaining for the given player by the given amount.
	 * 
	 * @param decrement The amount to decrement the timer. Must be greater than 0.
	 * @param isWhite A boolean indicating whether to decrement the white or black timer.
	 */
	public void decrementTime(int decrement, boolean isWhite) {
		if(decrement <= 0) {
			throw new IllegalArgumentException("Timer decrement must be greater than 0: " + decrement);
		}
		
		if(isWhite) {
			whiteTime -= decrement;
		} else {
			blackTime -= decrement;
		}
	}
	
	/**
	 * Increases the time remaining for the given player by the given amount.
	 * 
	 * @param increment The amount to increment the timer. Must be non-negative.
	 * @param isWhite
	 */
	public void incrementTime(int increment, boolean isWhite) {
		if(increment < 0) {
			throw new IllegalArgumentException("Timer increment must be greater than or equal to 0: " + increment);
		}
		
		if(isWhite) {
			whiteTime += increment;
		} else {
			blackTime += increment;
		}
	}
	
	/**
	 * Returns the time remaining for the specified player.
	 * 
	 * @param isWhite A boolean indicating whether to get white or black's remaining time.
	 * @return The time remaining for the specified player.
	 */
	public int getRemainingTime(boolean isWhite) {		
		return isWhite ? whiteTime : blackTime;
	}	
}
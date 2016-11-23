import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;
public class JOptionPaneMultiInput {
	
	JTextField startTimeMinutesField;
	JTextField startTimeSecondsField;
	JTextField timeBackSecondsField;
	JPanel panel;
	int startTimeMinutes;
	int startTimeSeconds;
	int timeBackSeconds;
	
	public JOptionPaneMultiInput()
	{	
		panel = new JPanel();
        JLabel startTimeLabel = new JLabel("Start Time: ");
        JLabel startTimePlaceHolder = new JLabel("");
        JLabel startTimeMinutesLabel = new JLabel("Minutes: ");
        JLabel startTimeSecondsLabel = new JLabel("Seconds: ");
        JLabel timeBackLabel = new JLabel("Time Back: ");
        JLabel timeBackSecondsLabel = new JLabel("Seconds: ");
        JLabel timeBackPlaceHolder = new JLabel("");

        startTimeMinutesField = new JTextField(5);
        startTimeSecondsField = new JTextField(5);
        timeBackSecondsField = new JTextField(5);
        

        panel.setLayout(new GridBagLayout());
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.EAST;
        GridBagConstraints right = new GridBagConstraints();
        right.weightx = 1.0;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(startTimeLabel, left);
        panel.add(startTimePlaceHolder, right);
        panel.add(startTimeMinutesLabel, left);
        panel.add(startTimeMinutesField, right);
        panel.add(startTimeSecondsLabel, left);
        panel.add(startTimeSecondsField, right);
        panel.add(timeBackLabel, left);
        panel.add(timeBackPlaceHolder, right);
        panel.add(timeBackSecondsLabel, left);
        panel.add(timeBackSecondsField, right);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	public int display()
	{
		Object[] array = new Object[4];
		array[0] = "Please enter a starting time and an";
		array[1] = "amount of time to get back each move.";
		array[2] = "";
		array[3] = panel;
		
		int result = JOptionPane.showConfirmDialog(null, array, 
	               "Enter custom time", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION) 
	    {
	    	String startTimeMinutesText = startTimeMinutesField.getText();
	    	String startTimeSecondsText = startTimeSecondsField.getText();
	    	String timeBackSecondsText = timeBackSecondsField.getText();
	    	
	    	boolean isNumerical = true;
	    	int i = 0;
	    	while(isNumerical && i < startTimeMinutesText.length())
	    	{
	    		//System.out.println("Check if start minutes numerical");
	    		if( !isNumerical(startTimeMinutesText.charAt(i)) )
	    			isNumerical = false;
	    		
	    		i++;
	    	}
	    	
	    	i = 0;
	    	while(isNumerical && i < startTimeSecondsText.length())
	    	{
	    		//System.out.println("Check if start seconds numerical");
	    		if( !isNumerical(startTimeSecondsText.charAt(i)) )
	    			isNumerical = false;
	    		
	    		i++;
	    	}
	    	
	    	i = 0;
	    	while(isNumerical && i < timeBackSecondsText.length())
	    	{
	    		//System.out.println("Check time back numerical");
	    		if( !isNumerical(timeBackSecondsText.charAt(i)) )
	    			isNumerical = false;
	    		
	    		i++;
	    	}
	    	
	    	
	    	if(startTimeMinutesText.length() == 0 && startTimeSecondsText.length() == 0 && timeBackSecondsText.length() == 0)
	    	{
	    		result = -1;
	    		JOptionPane.showMessageDialog(null, "Please enter at least one value.", "Error", JOptionPane.ERROR_MESSAGE);
	    	}
	    	else if(isNumerical)
	    	{
	    		//System.out.println("Numerical");
	    		
	    		if(startTimeMinutesText.length() == 0)
	    			startTimeMinutes = 0;
	    		else
	    			startTimeMinutes = Integer.parseInt(startTimeMinutesField.getText());
	    		
	    		if(startTimeSecondsText.length() == 0)
	    			startTimeSeconds = 0;
	    		else
	    			startTimeSeconds = Integer.parseInt(startTimeSecondsField.getText());
	    		
	    		if(timeBackSecondsText.length() == 0)
	    			timeBackSeconds = 0;
	    		else
	    			timeBackSeconds = Integer.parseInt(timeBackSecondsField.getText());
	    	}
	    	else
	    	{
	    		//System.out.println("Not numerical or null");
	    		result = -1;
	    		JOptionPane.showMessageDialog(null, "Please make all values numerical.", "Error", JOptionPane.ERROR_MESSAGE);
	    	}
	    }
	    
	    return result;
	}
	
	public int getStartTime()
	{
		return startTimeMinutes * 60000 + startTimeSeconds * 1000;
	}
	
	public int getTimeBack()
	{
		return timeBackSeconds * 1000;
	}
	
	private boolean isNumerical(char num)
	{
		if(num == '0' || num == '1' || num == '2' || num == '3' || num == '4' || num == '5' || num == '6' || num == '7' || num == '8' || num == '9')
			return true;
		
		return false;
	}
}
package com.troy.compsci.maze;
/**
Name: Troy Neubauer
Period: 5
Name of the lab: Lab (BooleanButon.java)
Due Date: 
Date Submitted:
What I learned:
   a. 
      
   b. 
      
*/

import javax.swing.*;

/**
 * Represents a button that automatically flips between two values when clicked
 *
 */
public class BooleanButon extends JButton
{
	private String trueText, falseText;
	private boolean state;

	public BooleanButon(String trueText, String falseText, boolean state)
	{
		super();
		this.trueText = trueText;
		this.falseText = falseText;
		this.state = state;
		updateText();
	}

	public boolean getState()
	{
		return state;
	}

	private void updateText()
	{
		this.setText(state ? trueText : falseText);
	}

	public void setState(boolean state)
	{
		this.state = state;
		updateText();
	}

	public void toggle()
	{
		this.state = !this.state;
		updateText();
	}

}

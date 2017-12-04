package com.troy.compsci.maze.gen;
/**
Name: Troy Neubauer
Peroid: 5
Name of the lab: Lab (WAHMazeCreatorPackage.java)
Due Date: 
Date Submitted:
What I learned:
   a. 
      
   b. 
      
*/

import java.awt.*;

import javax.swing.*;

/**
 * @author Troy Neubauer
 *
 */
public class WAHMazeCreatorPackage extends MazeCreatorPackage
{
	private JTextField widthField = new JTextField(5), heightField = new JTextField(5);
	protected int width, height;

	public String checkRequiredValues()
	{
		try
		{
			width = Integer.parseInt(this.widthField.getText());
			height = Integer.parseInt(this.heightField.getText());
			if (width > 0 && height > 0)
			{
				return null;
			}
			else
			{
				return "Make sure width and height are positive!";
			}

		}
		catch (Exception e)
		{
			return "Make sure width must be positive integers!";
		}

	}

	@Override
	public void addComponets(JPanel panel, GridBagConstraints g)
	{
		panel.add(new JLabel("Width"), g);
		int defaultX = g.gridx;
		g.gridx++;

		panel.add(widthField, g);
		g.gridy++;
		g.gridx = defaultX;

		panel.add(new JLabel("Height"), g);
		g.gridx++;

		panel.add(heightField, g);
		g.gridy++;
		g.gridx = defaultX;
	}
}

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
	public WAHMazeCreatorPackage()
	{
		super("Width And Height Maze");
	}

	private JTextField widthField = new JTextField(5), heightField = new JTextField(5);
	public int width, height;

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
	public void addComponets(GridBagConstraints g)
	{
		g.gridwidth = GridBagConstraints.RELATIVE;
		g.gridheight = GridBagConstraints.RELATIVE;
		
		g.gridx = 0;
		g.gridy = 0;
		add(new JLabel("Width: "), g);

		g.gridwidth = GridBagConstraints.REMAINDER;
		g.gridheight = GridBagConstraints.RELATIVE;
		g.gridx = 1;
		g.gridy = 0;
		add(widthField, g);

		g.gridwidth = GridBagConstraints.RELATIVE;
		g.gridheight = GridBagConstraints.REMAINDER;
		g.gridx = 0;
		g.gridy = 1;
		add(new JLabel("Height: "), g);

		g.gridwidth = GridBagConstraints.REMAINDER;
		g.gridheight = GridBagConstraints.REMAINDER;
		g.gridx = 1;
		g.gridy = 1;
		add(heightField, g);
	}
}

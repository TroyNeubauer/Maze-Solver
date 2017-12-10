package com.troy.compsci.maze.gen;
/**
Name: Troy Neubauer
Peroid: 5
Name of the lab: Lab (WallPercentPackage.java)
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
public class WallPercentPackage extends MazeCreatorPackage
{

	public JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100 * 10, 35 * 10);
	private JLabel percent = new JLabel();

	public WallPercentPackage()
	{
		super("Percent walls");
		slider.addChangeListener((e) -> setText());
		setText();
	}

	private void setText()
	{
		percent.setText(Double.toString(getValue()) + "%");
	}
	
	public double getValue() {
		return slider.getValue() / 10.0;
	}

	@Override
	public void addComponets(GridBagConstraints g)
	{
		this.add(new JLabel("Percent Walls: "), g);
		g.gridx++;
		this.add(slider, g);
		g.gridx++;
		this.add(percent, g);
	}

	@Override
	public String checkRequiredValues()
	{
		return null;
	}

}

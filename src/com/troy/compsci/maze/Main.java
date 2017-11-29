package com.troy.compsci.maze;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.troy.compsci.maze.graphics.*;

/**
Name: Troy Neubauer
Period: 5
Name of the lab: Extra Credit Maze Lab (with extra features!)
Due Date: 12:00AM January 1st, 2018
Date Submitted: 11/17/2017
What I learned:
   a. 
      
   b. 
      
*/

public class Main
{
	public static final int DISPLAY_WIDTH, DISPLAY_HEIGHT;
	static
	{//Get the resolution of the default display
		DisplayMode device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		DISPLAY_WIDTH = device.getWidth();
		DISPLAY_HEIGHT = device.getHeight();
	}
	
	public static void main(String[] args)
	{
		SwingOptionsFrame panel = new SwingOptionsFrame();
		while (true)
		{
			MasterRenderer renderer = new MasterRenderer();
			Maze maze = panel.waitForInformation();
			renderer.setInfo(maze, panel.getWindowOptions());
	
			panel.setVisible(false);//Hide the swing window
			renderer.waitForFinish();
			panel.setVisible(true);
		}
	
	}
}

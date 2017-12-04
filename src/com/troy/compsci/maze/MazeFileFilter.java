package com.troy.compsci.maze;
/**
Name: Troy Neubauer
Peroid: 5
Name of the lab: Lab (MazeFileFilter.java)
Due Date: 
Date Submitted:
What I learned:
   a. 
      
   b. 
      
*/

import java.io.*;

import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

/**
 * @author Troy Neubauer
 *
 */
public class MazeFileFilter extends FileFilter
{

	@Override
	public boolean accept(File f)
	{
		return f.getName().endsWith("maze");
	}


	@Override
	public String getDescription()
	{
		return "Mazes";
	}

}

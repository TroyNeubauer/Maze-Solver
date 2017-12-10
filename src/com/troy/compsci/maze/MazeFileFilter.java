package com.troy.compsci.maze;

import java.io.*;

import javax.swing.filechooser.FileFilter;

/**
 * @author Troy Neubauer
 * Used to select only mazes
 */
public class MazeFileFilter extends FileFilter
{

	@Override
	public boolean accept(File f)
	{
		return f.getName().endsWith("maze") || f.getName().endsWith("png") || f.getName().endsWith("jpeg") || f.getName().endsWith("bmp")
				|| f.getName().endsWith("gif");
	}

	@Override
	public String getDescription()
	{
		return "Mazes";
	}

}

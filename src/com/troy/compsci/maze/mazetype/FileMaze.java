package com.troy.compsci.maze.mazetype;

import java.io.*;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class FileMaze extends MazeType
{

	public FileMaze()
	{
		super(MazeCreatorPackage.FILE);
	}
	
	@Override
	public Maze create()
	{
		try
		{
			return MazeEncoding.readMaze(new File(MazeCreatorPackage.FILE.file.getText()));
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unable to load maze!", e);
		}
	}

}

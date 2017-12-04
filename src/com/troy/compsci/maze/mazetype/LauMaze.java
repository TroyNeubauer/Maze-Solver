package com.troy.compsci.maze.mazetype;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class LauMaze extends MazeType
{
	public LauMaze()
	{
		super();// Requires no packages
	}

	@Override
	public Maze create()
	{
		return MazeCreator.defaultLauMaze();
	}

}

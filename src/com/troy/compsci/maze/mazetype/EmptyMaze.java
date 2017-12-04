package com.troy.compsci.maze.mazetype;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class EmptyMaze extends MazeType
{

	public EmptyMaze()
	{
		super(MazeCreatorPackage.WIDTH_AND_HEIGHT);
	}

	@Override
	public Maze create()
	{
		return MazeCreator.emptyMaze(MazeCreatorPackage.WIDTH_AND_HEIGHT.width, MazeCreatorPackage.WIDTH_AND_HEIGHT.height);
	}

}

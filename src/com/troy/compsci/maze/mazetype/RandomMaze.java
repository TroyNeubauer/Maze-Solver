package com.troy.compsci.maze.mazetype;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class RandomMaze extends MazeType
{
	
	public RandomMaze()
	{
		super(MazeCreatorPackage.WIDTH_AND_HEIGHT);
	}

	@Override
	public Maze create()
	{
		return MazeCreator.randomMaze(MazeCreatorPackage.WIDTH_AND_HEIGHT.width, MazeCreatorPackage.WIDTH_AND_HEIGHT.height);
	}

}

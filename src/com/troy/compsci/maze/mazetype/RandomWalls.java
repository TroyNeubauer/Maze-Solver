package com.troy.compsci.maze.mazetype;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class RandomWalls extends MazeType
{

	public RandomWalls()
	{
		super(MazeCreatorPackage.WIDTH_AND_HEIGHT);
	}

	@Override
	public Maze create()
	{
		return MazeCreator.randomWalls(MazeCreatorPackage.WIDTH_AND_HEIGHT.width, MazeCreatorPackage.WIDTH_AND_HEIGHT.height);
	}

}

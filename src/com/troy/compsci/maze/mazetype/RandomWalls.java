package com.troy.compsci.maze.mazetype;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class RandomWalls extends MazeType
{

	public RandomWalls()
	{
		super(MazeCreatorPackage.WIDTH_AND_HEIGHT, MazeCreatorPackage.WALL_PERCENT);
	}

	@Override
	public Maze create()
	{
		return MazeCreator.randomWalls(MazeCreatorPackage.WIDTH_AND_HEIGHT.getWidth(), MazeCreatorPackage.WIDTH_AND_HEIGHT.getHeight(), MazeCreatorPackage.WALL_PERCENT.getValue());
	}

	@Override
	public void onSelect()
	{}

	@Override
	public void onDeSelect()
	{}

}

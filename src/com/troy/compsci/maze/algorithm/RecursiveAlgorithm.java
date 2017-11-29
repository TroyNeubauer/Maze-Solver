package com.troy.compsci.maze.algorithm;

import com.troy.compsci.maze.*;

public class RecursiveAlgorithm extends MazeAlgorithm
{

	@Override
	public boolean solve()
	{
		return findAnExitHelper(maze.startX, maze.startY);
	}

	public boolean findAnExitHelper(int x, int y)
	{
		if (x < 0 || y < 0 || x >= maze.width || y >= maze.height) return false;
		byte tileId = maze.maze[x + y * maze.width];
		if (tileId == Maze.VISITED || tileId == Maze.WALL) return false;
		if (sleep()) return false;
		maze.steps++;
		if (x == maze.endX && y == maze.endY) return true;//Return true if were at the end
		if (tileId != Maze.START) maze.maze[x + y * maze.width] = Maze.VISITED;//Don't mark the starting square as visited
		boolean result = findAnExitHelper(x + 1, y) || findAnExitHelper(x - 1, y) || findAnExitHelper(x, y + 1) || findAnExitHelper(x, y - 1);
		if (result)
		{
			if (tileId != Maze.START) maze.maze[x + y * maze.width] = Maze.SOLUTION_PATH;
		}
		return result;
	}

}

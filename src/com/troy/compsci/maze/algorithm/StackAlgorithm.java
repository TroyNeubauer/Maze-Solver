package com.troy.compsci.maze.algorithm;

import com.troy.compsci.maze.*;

public class StackAlgorithm extends MazeAlgorithm
{
	private MazePositionsStack stack = new MazePositionsStack();

	@Override
	public boolean solve()
	{
		stack.push(maze.startX);
		stack.push(maze.startY);
		boolean solved = false;
		while (!stack.isEmpty())
		{
			int y = stack.pop(), x = stack.pop();
			if (x < 0 || y < 0 || x >= maze.width || y >= maze.height) continue;
			byte tileId = maze.maze[x + y * maze.width];
			if (tileId == Maze.VISITED || tileId == Maze.WALL) continue;
			maze.steps++;
			idle();
			if (x == maze.endX && y == maze.endY)
			{
				solved = true;
				break;
			}
			maze.maze[x + y * maze.width] = Maze.VISITED;//Don't mark the starting square as visited
			//format:off
			stack.bigPush(x, y-1, x, y + 1, x - 1, y, x + 1, y);
			/*stack.push(x    ); stack.push(y - 1);
			stack.push(x    ); stack.push(y + 1);
			stack.push(x - 1); stack.push(y    );
			stack.push(x + 1); stack.push(y    );*/
			//format:on
		}
		return solved;
	}

}

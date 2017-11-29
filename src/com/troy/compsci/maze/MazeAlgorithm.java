package com.troy.compsci.maze;

public abstract class MazeAlgorithm
{
	protected Maze maze;

	public MazeAlgorithm()
	{}

	/**
	 * Called on another thread 
	 */
	public abstract boolean solve();

	public boolean sleep()
	{
		try
		{
			Thread.sleep(maze.slowDownMS);
		}
		catch (InterruptedException e)
		{
			return true;//If we get interrupted, tell the caller to end this thread
		}
		return false;
	}
	
	public static double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

}

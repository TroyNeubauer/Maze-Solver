package com.troy.compsci.maze;

import java.util.concurrent.*;

import com.troy.compsci.maze.graphics.*;

public abstract class MazeAlgorithm
{
	protected Maze maze;
	private static final long MINIMUN = TimeUnit.MILLISECONDS.toNanos(5);
	public MazeSolver solver;

	/**
	 * Called on another thread 
	 */
	public abstract boolean solve();

	/**
	 * Called to check if this thread is interrupted and to sleep if required by user
	 */
	public void idle()
	{
		long slowDownNS = TimeUnit.MICROSECONDS.toNanos(solver.getSlowDownMicroSeconds());
		if (maze.slowDownMicroSeconds < MINIMUN)
		{
			long now = System.nanoTime();
			long end = now + slowDownNS;
			while (now < end)//While now is before the time that we can resume
			{
				now = System.nanoTime();
			}
		}
		else
		{
			try
			{
				Thread.sleep(TimeUnit.MICROSECONDS.toMillis(maze.slowDownMicroSeconds));
			}
			catch (InterruptedException e)
			{
				throw new StoppedByUserException();//if another thread interrupts this thread, then we need to stop
			}
		}
		if (Thread.interrupted()) { throw new StoppedByUserException(); }
	}

	public static double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public MazeSolver getSolver()
	{
		return solver;
	}

}

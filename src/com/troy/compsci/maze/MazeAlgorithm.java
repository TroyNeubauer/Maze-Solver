package com.troy.compsci.maze;

import java.util.concurrent.*;

import com.troy.compsci.maze.graphics.*;

/**
 * Represents an algorithm used to solve a maze
 *
 */
public abstract class MazeAlgorithm
{
	protected Maze maze;
	//The minimum time to use more precise sleeping
	private static final long MINIMUN = TimeUnit.MILLISECONDS.toMicros(2);
	public MazeSolver solver;

	/**
	 * Called on another thread to solve the maze
	 */
	public abstract boolean solve();

	/**
	 * Called each step to check if this thread is interrupted and to sleep if required by user
	 */
	public void idle()
	{
		maze.steps++;
		long slowDownMicroSeconds = maze.slowDownMicroSeconds;//cache for faster access
		
		/*
		 * Because we can only sleep at the millisecond precision, it the user wants to sleep for less than a millisecond
		 * we must not call Thread.sleep(long), but instead rig up our own sleeping scheme that is accurate to nanosecond precision
		 */
		if (slowDownMicroSeconds < MINIMUN)
		{
			long now = System.nanoTime();
			long end = now + TimeUnit.MICROSECONDS.toNanos(slowDownMicroSeconds);//Calculate the time that we can resume at
			while (now < end)//While now is before the time that we can resume
			{
				now = System.nanoTime();//update now and wait until its time to resume
			}
		}
		else
		{
			try
			{
				Thread.sleep(TimeUnit.MICROSECONDS.toMillis(slowDownMicroSeconds));
			}
			catch (InterruptedException e)
			{
				throw new StoppedByUserException();//if another thread interrupts this thread, then we need to stop
			}
		}
		if (Thread.interrupted()) { throw new StoppedByUserException(); }//If the UI thread interrupts this one, throw an exception to stop the solving of the maze
	}
	
	protected void sleep(long miliseconds) {
		try
		{
			Thread.sleep(miliseconds);
		}
		catch (InterruptedException e)
		{
			throw new StoppedByUserException();
		}
	}

	/**
	 * 
	 * Distance formula
	 */
	public static double distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public MazeSolver getSolver()
	{
		return solver;
	}

}

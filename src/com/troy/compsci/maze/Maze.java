package com.troy.compsci.maze;

import java.util.concurrent.atomic.*;

import javax.swing.*;

import com.troy.compsci.maze.graphics.*;

/**
 * Represents a maze
 * @author Troy Neubauer
 *
 */
public class Maze
{
	//Constants for tile id's
	public static final byte PATH = 0;
	public static final byte WALL = 1;
	public static final byte VISITED = 2;
	public static final byte START = 3;
	public static final byte END = 4;
	public static final byte SOLUTION_PATH = 5;
	public static final byte CLOSED = 6;

	/**
	 * The maze data, where each index is a tile stored in row major
	 */
	public final byte[] maze;// public for fast access in the renderer
	/**
	 * Dimensions of the maze
	 */
	public final int width, height;
	//The algorithm that is being used to solve
	private MazeAlgorithm algorithm;
	//Info about where to start and end
	public int startX, startY, endX, endY;
	
	//How many microseconds to slow down each iteration by
	public long slowDownMicroSeconds;
	//The number of steps that the current algorithm has taken in solving
	public long steps;
	//The thread that is solving the maze
	private Thread thread;
	//Weather or not the solving thread is working
	public AtomicBoolean working = new AtomicBoolean(false);

	public Maze(byte[] maze, int width, int height, int startX, int startY, int endX, int endY)
	{
		this.maze = maze;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		setLocations(startX, startY, endX, endY);
	}

	/**
	 * Creates a maze from a pre-existing set of values
	 * @param maze The data for the maze
	 * @param width The width of the maze. Must be consistent with the maze specified
	 * @param height The height of the maze. Must be consistent with the maze specified
	 */
	public Maze(byte[] maze, int width, int height)
	{
		this.maze = maze;
		this.width = width;
		this.height = height;
		setLocations(0, 0, width - 1, height - 1);
	}

	/**
	 * Creates a new blank maze of the desired size
	 * @param width The width of the maze to create
	 * @param height The height of the maze to create
	 */
	public Maze(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.maze = new byte[width * height];// Because the default value is 0, the maze will be empty with all walkable paths
		setLocations(0, 0, width - 1, height - 1);
	}

	public void setLocations(int startX, int startY, int endX, int endY)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		setTileId(startX, startY, START);
		setTileId(endX, endY, END);
	}

	public byte getTileId(int x, int y)
	{
		if (x < 0 || x >= width || y < 0 || y >= height) return Maze.WALL;
		return maze[x + y * width];
	}

	public void setTileId(int x, int y, byte id)
	{
		maze[x + y * width] = id;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(MazeAlgorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	/**
	 * Solves the maze on a new thread
	 */
	public void solve()
	{
		if (algorithm == null) throw new NullPointerException();
		stop();
		this.thread = new Thread(() ->
		{
			//Weather or not we found a solution
			String result;
			working.set(true);
			//The time solving started at
			long startTime = System.nanoTime();
			try
			{
				result = algorithm.solve() ? "Solved!" : "No Solution";
				algorithm.solver.needsRepaint = true;
			}
			catch (StackOverflowError e)
			{
				result = "No Solution - Stack Overflow Error!";
			}
			catch (StoppedByUserException e)
			{
				result = "No Solution - Stopped by user";
			}
			working.set(false);
			algorithm.getSolver().mazeDone();//Used to change the start/stop button to start because we are done

			//Show info to a JOptionPane
			long time = System.nanoTime() - startTime;
			double seconds = (double) time / 1000000000.0;
			double averageStepsPerSecond = steps / seconds;
			String message = "";
			message += "Results for " + width + "X" + height + " maze" + '\n';
			message += "Total time taken: " + seconds + " seconds\n";
			message += "Total steps " + String.format("%,d", steps) + '\n';
			message += "Average steps per second " + String.format("%,.2f", averageStepsPerSecond) + '\n';
			message += "With a slowdown of " + (slowDownMicroSeconds / 1000000.0) + " seconds per step" + '\n';
			JOptionPane.showMessageDialog(algorithm.solver, message, result, JOptionPane.INFORMATION_MESSAGE);
		});
		thread.start();
	}

	/**
	 * Stops the solving of the maze
	 */
	public void stop()
	{
		if (thread != null && thread.isAlive())
		{
			thread.interrupt();// When the other thread runs idle() it will stop because a StoppedByUserException will be thrown
			try
			{
				Thread.sleep(100);//Give some time for it to die, don't return instantly
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	/**
	 * Resets the maze back to only walls and path
	 */
	public void reset()
	{
		for (int i = 0; i < width * height; i++)
		{
			byte id = maze[i];
			if (id != Maze.WALL)
			{
				maze[i] = Maze.PATH;
			}
		}
		maze[startX + startY * width] = Maze.START;
		maze[endX + endY * width] = Maze.END;
	}

}

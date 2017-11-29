package com.troy.compsci.maze;

import javax.swing.*;

public class Maze
{
	public static final byte PATH = 0;
	public static final byte WALL = 1;
	public static final byte VISITED = 2;
	public static final byte START = 3;
	public static final byte END = 4;
	public static final byte SOLUTION_PATH = 5;
	public static final byte CLOSED = 6;

	public final byte[] maze;// public for fast access in the renderer
	public final int width, height;
	private MazeAlgorithm algorithm;
	public int startX, startY, endX, endY;
	protected long slowDownMS;
	public long steps;
	private Thread thread;

	/**
	 * Creates a maze from a pre-existing set of values
	 * @param maze The data for the maze
	 * @param width The width of the maze. Must be consistent with the maze specified
	 * @param height The height of the maze. Must be consistent with the maze specified
	 */
	protected Maze(MazeAlgorithm algorithm, byte[] maze, int width, int height)
	{
		this.algorithm = algorithm;
		this.maze = maze;
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates a new blank maze of the desired size
	 * @param width The width of the maze to create
	 * @param height The height of the maze to create
	 */
	public Maze(MazeAlgorithm algorithm, int width, int height)
	{
		this.algorithm = algorithm;
		this.width = width;
		this.height = height;
		this.maze = new byte[width * height];// Because the default value is 0, the maze will be empty with all walkable paths
	}

	public void setLocations(int startX, int startY, int endX, int endY)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		maze[startX + startY * width] = START;
		maze[endX + endY * width] = END;
	}

	/**
	 * 
	 */
	public void solve()
	{
		this.thread = new Thread(() ->
		{
			long start = System.nanoTime();
			String result;
			try
			{
				result = algorithm.solve() ? "Solved!" : "No Solution";
			}
			catch (StackOverflowError e)
			{
				System.out.println("stack overflow!");
				result = "No Solution - Stack Overflow Error!";
			}

			long time = System.nanoTime() - start;
			double seconds = (double) time / 1000000000.0;
			double averageStepsPerSecond = steps / seconds;
			String message = "";
			message += "Results for " + width + "X" + height + " maze" + '\n';
			message += "Total time taken: " + seconds + " seconds\n";
			message += "Total steps " + String.format("%,d", steps) + '\n';
			message += "Average steps per second " + String.format("%,.2f", averageStepsPerSecond) + '\n';
			message += "With a slowdown of " + slowDownMS + " ms per step" + '\n';
			JOptionPane.showMessageDialog(null, message, result, JOptionPane.INFORMATION_MESSAGE);
		});
		thread.start();
	}

	public void stop()
	{
		thread.interrupt();
	}

}

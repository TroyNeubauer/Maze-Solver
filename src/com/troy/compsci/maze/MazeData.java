package com.troy.compsci.maze;

public class MazeData
{
	private int width, height;
	private byte[] maze;

	public MazeData()
	{}

	/**
	 * @param width
	 * @param height
	 * @param maze
	 */
	public MazeData(int width, int height, byte[] maze)
	{
		this.width = width;
		this.height = height;
		this.maze = maze;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	 * @return the maze
	 */
	public byte[] getMaze()
	{
		return maze;
	}

	/**
	 * @param maze the maze to set
	 */
	public void setMaze(byte[] maze)
	{
		this.maze = maze;
	}

}

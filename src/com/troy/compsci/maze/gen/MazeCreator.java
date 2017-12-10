package com.troy.compsci.maze.gen;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

import com.troy.compsci.maze.*;

public class MazeCreator
{

	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int width, height;

	public static Maze defaultLauMaze()
	{
		// Use the maze made by Mr. Lau for some of the previous labs
		//format:off
		byte[] maze = {0,0,0,1,0,0,1,1,1,0,0,0,0,
			     			0,1,0,0,0,1,0,0,0,0,1,1,0,
			     			1,1,1,1,0,1,0,1,0,1,0,1,1,
			     			0,0,0,1,0,0,0,1,0,1,1,0,0,
			     			0,1,0,1,1,1,1,0,0,0,1,1,0,
			     			0,1,0,0,0,0,0,0,1,0,0,0,1,
			     			0,1,1,1,1,1,1,1,1,1,1,1,1,
			     			0,0,0,0,0,0,0,0,0,0,0,0,0};//format:on
		Maze result = new Maze(maze, 13, 8);
		return result;
	}

	public static Maze emptyMaze(int width, int height)
	{
		return new Maze(width, height);
	}

	public static Maze randomWalls(int width, int height, double percentChance)
	{
		Maze maze = new Maze(width, height);
		for (int i = 0; i < width * height; i++)
		{
			if (Math.random() < percentChance / 100.0)
			{
				maze.maze[randRange(0, width) + randRange(0, height) * width] = Maze.WALL;
			}
		}
		return maze;
	}

	public static Maze randomMaze(int width, int height)
	{
		Maze maze = new Maze(width, height);
		for (int i = 0; i < maze.maze.length; i++)
		{
			maze.maze[i] = Maze.WALL;
		}
		MazeCreator.width = width;
		MazeCreator.height = height;
		int nGood = 0, locX = 1, locY = 1;
		byte[] grid = maze.maze;
		int direction = 0;
		Stack<Integer> xValues = new Stack<Integer>(), yValues = new Stack<Integer>();

		do
		{
			//find n good moves
			for (int i = 0; i < 4; i++)
			{
				if (isGood(locX, locY, i, grid)) nGood++;
			}

			// if only 1 good move, move there
			if (nGood == 1)
			{
				if (isGood(locX, locY, NORTH, grid)) locY = moveNS(NORTH, locY);
				else if (isGood(locX, locY, SOUTH, grid)) locY = moveNS(SOUTH, locY);
				else if (isGood(locX, locY, EAST, grid)) locX = moveEW(EAST, locX);
				else if (isGood(locX, locY, WEST, grid)) locX = moveEW(WEST, locX);
			}

			// if no good moves, move back in stack
			else if (nGood == 0)
			{
				locX = xValues.pop();
				locY = yValues.pop();
			}

			//if more than 1 good move, push stack
			else if (nGood > 1)
			{
				xValues.push(locX);
				yValues.push(locY);

				//direction to move randomly chosen
				do
				{
					direction = randRange(0, 4);
				}
				while (!isGood(locX, locY, direction, grid));

				locX = moveEW(direction, locX);
				locY = moveNS(direction, locY);
			}

			// set grid
			grid[locX + locY * width] = Maze.PATH;
			nGood = 0;

		}
		while (!xValues.empty());
		maze.setLocations(1, 1, width - 2, height - 2);
		return maze;
	}

	/**@return a random int between the ranges inclusive of min and exclusive of max
	 * */
	public static int randRange(int min, int max)
	{
		return (int) Math.floor(min + Math.random() * (max - min));
	}

	private static int moveEW(int direction, int x)
	{
		if (direction == EAST) return x + 1;
		else if (direction == WEST) return x - 1;
		else return x;
	}

	private static int moveNS(int direction, int y)
	{
		if (direction == NORTH) return y - 1;
		else if (direction == SOUTH) return y + 1;
		else return y;
	}

	private static boolean isGood(int x, int y, int direction, byte[] grid)
	{
		x = moveEW(direction, x);
		y = moveNS(direction, y);

		if (grid[x + y * width] == Maze.PATH || x >= (width - 1) || x <= 0 || y <= 0 || y >= (height - 1)) return false;

		// check cardinal directions
		if (direction == NORTH)
		{
			if (grid[x - 1 + y * width] != Maze.PATH && grid[x + (y - 1) * width] != Maze.PATH && grid[x + 1 + y * width] != Maze.PATH
					&& grid[(x - 1) + (y - 1) * width] != Maze.PATH && grid[(x + 1) + (y - 1) * width] != Maze.PATH) return true;
		}
		if (direction == SOUTH)
		{
			if (grid[x - 1 + y * width] != Maze.PATH && grid[x + (y + 1) * width] != Maze.PATH && grid[x + 1 + y * width] != Maze.PATH
					&& grid[x - 1 + (y + 1) * width] != Maze.PATH && grid[x + 1 + (y + 1) * width] != Maze.PATH) return true;
		}
		if (direction == EAST)
		{
			if (grid[x + 1 + y * width] != Maze.PATH && grid[x + (y - 1) * width] != Maze.PATH && grid[x + (y + 1) * width] != Maze.PATH
					&& grid[x + 1 + (y - 1) * width] != Maze.PATH && grid[x + 1 + (y + 1) * width] != Maze.PATH) return true;
		}
		if (direction == WEST)
		{
			if (grid[x - 1 + y * width] != Maze.PATH && grid[x + (y - 1) * width] != Maze.PATH && grid[x + (y + 1) * width] != Maze.PATH
					&& grid[x - 1 + (y - 1) * width] != Maze.PATH && grid[x - 1 + (y + 1) * width] != Maze.PATH) return true;
		}
		return false;
	}

	/**
	 * Creates a maze that looks like the image specified
	 * @param file The image to base the maze off of
	 * @param width The width of the resulting maze
	 * @param height The height of the resulting maze
	 * @param percentWall The percent of the maze that should be wall
	 * @return A maze representing the image
	 * @throws IOException If the file can't be read
	 */
	public static Maze createImageMaze(File file, int width, int height, double percentWall) throws IOException
	{
		double wallRatio = percentWall / 100.0;
		BufferedImage fromFile = ImageIO.read(file);// Read the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// Create an image thats the desired height
		image.createGraphics().drawImage(fromFile, 0, 0, width, height, null);//Draw the image onto the one thats the correct height
		int[] spread = new int[1000];
		for (int i = 0; i < width * height; i++)
		{
			int x = i % width;
			int y = i / width;
			int rgb = image.getRGB(x, y);
			int r = (rgb >> 16) & 0xFF;
			int g = (rgb >> 8) & 0xFF;
			int b = (rgb >> 0) & 0xFF;
			double pixel = getWeightedPixelAverage(r, g, b);
			int index = (int) (pixel * spread.length);//calculate the index
			spread[index]++;
		}

		double max = -1;
		int total = 0;
		for (int i = 0; i < spread.length; i++)
		{
			total += spread[i];
			if ((double) total / (double) (width * height) > wallRatio)
			{
				max = (double) i / spread.length;
				break;
			}
		}
		if (max == -1) max = 1.0;

		byte[] mazeData = new byte[width * height];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 0) & 0xFF;
				double pixel = getWeightedPixelAverage(r, g, b);
				mazeData[x + y * width] = (pixel < max) ? Maze.WALL : Maze.PATH;
			}
		}

		return new Maze(mazeData, width, height);
	}

	private static double getWeightedPixelAverage(int r, int g, int b)
	{
		return (0.299 * r / 256.0) + (0.587 * g / 256.0) + (0.114 * b / 256.0);// Parentheses for readability
	}

}

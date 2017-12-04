package com.troy.compsci.maze;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.io.*;

public class MazeEncoding
{
	private static final byte PATH = 0, WALL = 1;
	private static final byte HEADER = 'M';
	public static final String EXTENSION = "maze";

	public static Maze readMaze(File file) throws IOException
	{
		int width, height, startX, startY, endX, endY;
		byte[] mazeData;

		FileInputStream stream = new FileInputStream(file);
		try
		{

			byte firstByte = (byte) stream.read();
			if (firstByte != HEADER) throw new IOException("Non maze file: " + file + ". File doesn't start with " + HEADER
					+ " as the first byte! First byte: " + firstByte);
			width = read3ByteInt(stream);
			height = read3ByteInt(stream);

			startX = read3ByteInt(stream);
			startY = read3ByteInt(stream);
			endX = read3ByteInt(stream);
			endY = read3ByteInt(stream);

			byte[] compressedMazeData = IOUtils.toByteArray(stream);
			stream.close();

			mazeData = toBigFormat(decompress(compressedMazeData), width, height);
		}
		catch (Exception e)
		{
			throw new IOException("Unable to read maze!", e);
		}
		finally
		{
			stream.close();
		}

		return new Maze(mazeData, width, height, startX, startY, endX, endY);
	}

	public static void writeMaze(Maze maze, File dest) throws IOException
	{
		String file = dest.getName();
		if (FilenameUtils.getExtension(file) != EXTENSION)
		{
			int dot = file.lastIndexOf('.');
			dest = new File(dest.getParentFile(), file.substring(0, (dot == -1) ? file.length() : dot) + '.' + EXTENSION);
		}
		FileOutputStream stream = new FileOutputStream(dest);

		stream.write(HEADER);

		write3ByteInt(stream, maze.width);
		write3ByteInt(stream, maze.height);

		write3ByteInt(stream, maze.startX);
		write3ByteInt(stream, maze.startY);
		write3ByteInt(stream, maze.endX);
		write3ByteInt(stream, maze.endY);

		stream.write(compress(toSmallFormat(maze)));

		stream.close();
	}

	private static byte[] toSmallFormat(Maze maze)
	{
		byte[] bitMazeData = new byte[(maze.width * maze.height + 7) / Byte.SIZE];
		for (int i = 0; i < maze.maze.length; i++)
		{
			int bitIndex = (Byte.SIZE - 1) - i % Byte.SIZE;// Which bit this binary value (wall or not) should go into in the byte
			int value = ((maze.maze[i] == Maze.WALL) ? WALL : PATH) << bitIndex;// the value of this tile properly stored in the byte
			bitMazeData[i / Byte.SIZE] |= value;
		} // Bit maze data efficiently stores the entire maze using 8X less memory!
		return bitMazeData;
	}

	private static byte[] toBigFormat(byte[] bitMazeData, int mazeWidth, int mazeHeight)
	{
		int tiles = mazeWidth * mazeHeight;
		byte[] result = new byte[tiles];
		for (int i = 0; i < tiles; i++)
		{
			int bitIndex = (Byte.SIZE - 1) - i % Byte.SIZE;
			boolean wall = (((bitMazeData[i / Byte.SIZE]) >> bitIndex) & 0x01) == WALL ? true : false;
			result[i] = wall ? Maze.WALL : Maze.PATH;
		}
		return result;
	}

	private static byte[] compress(byte[] src)
	{
		try
		{
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(src.length);
			DeflaterOutputStream deflater = new DeflaterOutputStream(bytesOut);
			deflater.write(src);
			deflater.flush();
			deflater.close();
			return bytesOut.toByteArray();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unexpected IOException while compressing!", e);
		}
	}

	private static byte[] decompress(byte[] src)
	{
		try
		{
			InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(src));
			return IOUtils.toByteArray(inflater);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unexpected IOException while compressing!", e);
		}
	}

	private static String toBinaryString(int value)
	{
		if (value < (int) Math.pow(2, 8)) return "" + ((value >> 7) & 0x1) + ((value >> 6) & 0x1) + ((value >> 5) & 0x1) + ((value >> 4) & 0x1)
				+ ((value >> 3) & 0x1) + ((value >> 2) & 0x1) + ((value >> 1) & 0x1) + ((value >> 0) & 0x1);
		return "";

	}

	private static int read3ByteInt(FileInputStream stream) throws IOException
	{
		return (stream.read() << 16) | (stream.read() << 8) | (stream.read() << 0);
	}

	private static void write3ByteInt(FileOutputStream stream, int value) throws IOException
	{
		assert value < (int) Math.pow(2, 8 * 3);//make sure value can be represented in 3 bytes
		stream.write(value >> 16);
		stream.write(value >> 8);
		stream.write(value >> 0);
	}

}

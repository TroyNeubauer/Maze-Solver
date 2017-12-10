package com.troy.compsci.maze;

import java.io.*;
import java.util.zip.*;

import org.apache.commons.io.*;

/**
 * Contains some static methods for reading and writing mazes to and from files
 * A maze file 
 *
 */
public class MazeEncoding
{
	//Constants for when the maze is stored in a file
	private static final byte PATH = 0, WALL = 1;
	//This will always be the first byte of a maze file. 
	//If a file's first byte isan't this one, we know it isan't a maze file
	private static final byte HEADER = 'M';
	//The file extension to use when saving files
	public static final String EXTENSION = "maze";

	/**
	 * Returns the maze stored in file {@code file} or throws an {@link IOException} if the file cannot be read or isn't a valid maze file
	 * @param file The file to read from
	 * @return The maze stored in the file
	 * @throws IOException If the file cannot be read
	 */
	public static Maze readMaze(File file) throws IOException
	{
		int width, height, startX, startY, endX, endY;
		byte[] mazeData;

		FileInputStream stream = new FileInputStream(file);
		try
		{
			byte firstByte = (byte) stream.read();
			if (firstByte != HEADER) throw new IOException("Non maze file: " + file + ". File doesn't start with 0x" + Integer.toHexString(HEADER)
					+ " as the first byte! First byte: 0x" + Integer.toHexString(firstByte));
			width = read3ByteInt(stream);
			height = read3ByteInt(stream);

			startX = read3ByteInt(stream);
			startY = read3ByteInt(stream);
			endX = read3ByteInt(stream);
			endY = read3ByteInt(stream);

			byte[] compressedMazeData = IOUtils.toByteArray(stream);//The remainder of the file is maze data so read all of it
			stream.close();//Close the file

			mazeData = toBigFormat(decompress(compressedMazeData));
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			stream.close();
		}

		return new Maze(mazeData, width, height, startX, startY, endX, endY);
	}

	/**
	 * Writes a maze to a file, overwriting the file if it already exists
	 * @param maze The maze to write
	 * @param dest The file to write to 
	 * @throws IOException If some I/O error occurs while writing to the file
	 */
	public static void writeMaze(Maze maze, File dest) throws IOException
	{
		String file = dest.getName();
		if (FilenameUtils.getExtension(file) != EXTENSION)//Set the file to have the .maze extension if it doesn't already
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

	/**
	 * Converts byte maze data to bit maze data
	 * @param maze The maze data to make into bit
	 * @return Bit maze data representing the inputed maze
	 */
	private static byte[] toSmallFormat(Maze maze)
	{
		byte[] mazeData = maze.maze;//cache for faster access
		byte[] bitMazeData = new byte[(maze.width * maze.height + Byte.SIZE - 1) / Byte.SIZE];//Rounds up
		for (int i = 0; i < mazeData.length; i++)
		{
			int bitIndex = (Byte.SIZE - 1) - i % Byte.SIZE;// Which bit this binary value (wall or not) should go into in the byte
			int value = ((mazeData[i] == Maze.WALL) ? WALL : PATH) << bitIndex;// the value of this tile properly stored in the byte
			bitMazeData[i / Byte.SIZE] |= value;
		} // Bit maze data efficiently stores the entire maze using 8X less memory!
		return bitMazeData;
	}

	/**
	 * Converts a maze data where each bit is one tile, to maze data where each byte is a tile
	 * @param bitMazeData The data to expand
	 * @return Maze data that can be passed into a maze object
	 */
	private static byte[] toBigFormat(byte[] bitMazeData)
	{
		int tiles = bitMazeData.length * Byte.SIZE;
		byte[] result = new byte[tiles];
		for (int i = 0; i < tiles; i++)
		{
			int bitIndex = (Byte.SIZE - 1) - i % Byte.SIZE;
			boolean wall = (((bitMazeData[i / Byte.SIZE]) >> bitIndex) & 0x01) == WALL ? true : false;
			result[i] = wall ? Maze.WALL : Maze.PATH;
		}
		return result;
	}

	/**
	 * Compresses data using Deflater compression
	 * @param src The uncompressed data to compress
	 * @return Compressed data
	 */
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

	/**
	 * Decompresses the given byte array from deflater compression to uncompressed
	 * @param src Deflater compressed data
	 * @return The uncompressed data
	 */
	private static byte[] decompress(byte[] src)
	{
		try
		{
			InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(src));
			return IOUtils.toByteArray(inflater);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unexpected IOException while decompressing!", e);
		}
	}

	/**
	 * Reads a three byte integer from the desired input stream
	 * @param stream The stream to read from
	 * @return The integer read
	 * @throws IOException If some I/O error happens while reading
	 */
	private static int read3ByteInt(FileInputStream stream) throws IOException
	{
		return (stream.read() << 16) | (stream.read() << 8) | (stream.read() << 0);
	}

	/**
	 * Writes an integer as three bytes to the specified output stream
	 * @param stream The stream to write to
	 * @param value The integer to write
	 * @throws IOException If some I/O error occurs while writing the value
	 */
	private static void write3ByteInt(FileOutputStream stream, int value) throws IOException
	{
		assert value < (int) Math.pow(2, Byte.SIZE * 3);//make sure value can be represented in 3 bytes
		stream.write(value >> 16);
		stream.write(value >> 8);
		stream.write(value >> 0);
	}

}

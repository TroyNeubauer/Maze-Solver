package com.troy.compsci.maze.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import com.troy.compsci.maze.*;

/**
 * This class is used to render any maze
 *
 */
public class MasterRenderer
{

	/**
	 * Contains a mapping of all tile names and their appropriate color  stored as a 24 bit RGB value
	 */
	private static final HashMap<String, Integer> COLORS = new HashMap<String, Integer>();
	/**
	 * This list contains all the colors that are important and should be avoided when generating random colors
	 * each element is a 6 bit RGB (2 bits for each) color
	 */
	public static final List<Byte> SPECIAL_COLORS = new ArrayList<Byte>();

	static
	{
		COLORS.put("path", 0xFFFFFF);//White
		COLORS.put("wall", 0x000000);//Black
		COLORS.put("solution path", 0x00C000);//Light green
		COLORS.put("start", 0xFFFF00);//Yellow
		COLORS.put("end", 0x4DFF66);//Bright green
		COLORS.put("visited", 0xFF00FF);//Purple
		COLORS.put("closed", 0xFF0000);//Red

		putSpecial("path");
		putSpecial("wall");
		putSpecial("start");
		putSpecial("end");
	}

	/**
	 * @param string Adds a color to the list of special colors
	 */
	private static void putSpecial(String string)
	{
		Integer color = COLORS.get(string);
		if (color == null) throw new RuntimeException(string);
		int colorInt = color.intValue();
		byte colorByte = (byte) ((((colorInt >> 22) & 0b11) << 4) | (((colorInt >> 14) & 0b11) << 2) | (((colorInt >> 6) & 0b11) << 0));
		SPECIAL_COLORS.add(colorByte);
	}

	private MazeSolver solver;

	public MasterRenderer(MazeSolver solver)
	{
		super();
		this.solver = solver;
	}

	/**
	 * Called each frame to draw the maze
	 * @param maze The maze to draw
	 */
	public void render(Maze maze)
	{
		//Tell Open GL the area that we want to draw on (the entire canvas)
		glViewport(0, 0, solver.getCanvas().getWidth(), solver.getCanvas().getHeight());
		int width = maze.width, height = maze.height;// Cache width and height for faster access
		glBegin(GL_QUADS);// Tell Open GL that we want to render quads (squares)
		{
			for (int y = 0; y < height; y++)//Loop through each tile
			{
				for (int x = 0; x < width; x++)
				{
					byte value = maze.maze[x + y * width];//Grab the tile's id

					//Set the color based on the tile type
					if (value == Maze.PATH) color("path");

					else if (value == Maze.WALL) color("wall");

					else if (value == Maze.SOLUTION_PATH) color("solution path");

					else if (value == Maze.START || (x == maze.startX && y == maze.startY)) color("start");

					else if (value == Maze.END) color("end");

					else if (value == Maze.VISITED) color("visited");

					else if (value == Maze.CLOSED) color("closed");

					else//If the current tile doesn't match a known tile id, just interpret the color as the low 6 bits of the tile's id
					{//This code allows for rendering specific colors as long as the sign bit is one. For example 0b10111111 would be white
					 //0b10110011 would be purple etc.
						int r = ((value >> 4) & 0b11);//extract each channel from the id
						int g = ((value >> 2) & 0b11);
						int b = ((value >> 0) & 0b11);
						glColor3f(r / 4.0f, g / 4.0f, b / 4.0f);//Pass the color to Open GL
					}

					//Calculate the positions for the square on the screen
					float minx = map(x, 0, width, -1.0f, 1.0f), maxx = map(x + 1, 0, width, -1.0f, 1.0f);
					float miny = map(y, 0, height, 1.0f, -1.0f), maxy = map(y + 1, 0, height, 1.0f, -1.0f);
					glVertex2f(minx, miny);
					glVertex2f(minx, maxy);
					glVertex2f(maxx, maxy);
					glVertex2f(maxx, miny);
				}
			}
		}
		glEnd();//Tell Open GL that we are done specifying objects to draw
		glFinish();//Wait until all operations on the GPU are complete
		GLUtil.checkForErrors("render");//Print out any Open GL errors (if any)

	}

	/**
	 * Sets the color for Open GL to draw with to the color specified by the name in {@link MasterRenderer#COLORS}
	 * @param string The color to use
	 */
	private void color(String string)
	{
		Integer color = COLORS.get(string);
		if (color == null) throw new RuntimeException(string);
		int colorInt = color.intValue();
		//Extract each channel's value and pass it to Open GL
		glColor3f(((colorInt >> 16) & 0xFF) / 256.0f, ((colorInt >> 8) & 0xFF) / 256.0f, ((colorInt >> 0) & 0xFF) / 256.0f);
	}

	//Helper math functions
	
	/**
	 * Maps a value between a min and a max to a different min and max
	 */
	public static float map(float value, float sourceMin, float sourceMax, float destMin, float destMax)
	{
		float n = normalize(sourceMin, sourceMax, value);
		return lerp(destMin, destMax, n);
	}

	//Reverse linear interpolation
	public static float normalize(float min, float max, float value)
	{
		return (value - min) / (max - min);
	}

	//Linear interpolation
	public static float lerp(float a, float b, float f)
	{
		return a + f * (b - a);
	}

}

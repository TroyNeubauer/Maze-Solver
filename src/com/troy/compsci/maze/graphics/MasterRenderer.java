package com.troy.compsci.maze.graphics;

import static org.lwjgl.opengl.GL11.*;

import com.troy.compsci.maze.*;

public class MasterRenderer
{

	private MazeSolver solver;

	public MasterRenderer(MazeSolver solver)
	{
		super();
		this.solver = solver;
	}

	public void render(Maze maze)
	{
		glViewport(0, 0, solver.getCanvas().getWidth(), solver.getCanvas().getHeight());
		int width = maze.width, height = maze.height;
		glBegin(GL_QUADS);
		{
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					byte value = maze.maze[x + y * width];
					if (value == Maze.PATH)
					{
						glColor3f(1, 1, 1);//White for paths
					}
					else if (value == Maze.WALL)
					{
						glColor3f(0, 0, 0);//Black for walls
					}
					else if (value == Maze.SOLUTION_PATH)
					{
						glColor3f(0, 0.75f, 0);// darker green for the path to the solution
					}
					else if (value == Maze.START || (x == maze.startX && y == maze.startY))
					{
						glColor3f(1, 1, 0);//yellow for start
					}
					else if (value == Maze.END)
					{
						glColor3f(0.3f, 1, 0.4f);//bright green for the end
					}
					else if (value == Maze.VISITED)
					{
						glColor3f(1, 0, 1);//Purple for visited
					}
					else if (value == Maze.CLOSED)
					{
						glColor3f(1, 0, 0);//Red for closed
					}
					else
					{
						throw new RuntimeException("Unknown value " + value);
					}
					float minx = map(x, 0, width, -1.0f, 1.0f), maxx = map(x + 1, 0, width, -1.0f, 1.0f);
					float miny = map(y, 0, height, 1.0f, -1.0f), maxy = map(y + 1, 0, height, 1.0f, -1.0f);
					glVertex2f(minx, miny);
					glVertex2f(minx, maxy);
					glVertex2f(maxx, maxy);
					glVertex2f(maxx, miny);
				}
			}
		}
		glEnd();
		glFinish();
		GLUtil.checkForErrors("render");

	}

	public static float map(float value, float sourceMin, float sourceMax, float destMin, float destMax)
	{
		float n = normalize(sourceMin, sourceMax, value);
		return lerp(destMin, destMax, n);
	}

	public static float normalize(float min, float max, float value)
	{
		return (value - min) / (max - min);
	}

	public static float lerp(float a, float b, float f)
	{
		return a + f * (b - a);
	}

}

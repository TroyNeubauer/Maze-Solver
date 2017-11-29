package com.troy.compsci.maze.graphics;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.*;
import java.nio.*;
import java.util.concurrent.atomic.*;

import javax.management.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.troy.compsci.maze.*;

public class MasterRenderer implements Runnable
{
	private long windowHandle;
	private Thread thread;
	private Maze maze;
	private AtomicBoolean finished = new AtomicBoolean(false);
	private WindowOptions options;

	public MasterRenderer(long windowHandle)
	{
		super();
		this.windowHandle = windowHandle;
	}

	public MasterRenderer()
	{
		this.thread = new Thread(this, "Open GL Rendering-Thread");
		thread.start();
	}

	@Override
	public void run()
	{
		if (!glfwInit())
		{
			System.err.println("Unable to Initalize GLFW!");
			System.exit(1);//End with error exit code
		}

		while (maze == null)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

		windowHandle = glfwCreateWindow(options.width, options.height, "N Queens Animation", options.fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
		glfwSetWindowPos(windowHandle, (Main.DISPLAY_WIDTH - options.width) / 2, (Main.DISPLAY_HEIGHT - options.height) / 2);

		glfwMakeContextCurrent(windowHandle);//Make the Open GL context current on the current thread
		GL.createCapabilities(false);//Tell LWJGL to make the Open GL bindings available for use

		GLFWErrorCallback.createPrint(System.err).set();//Tell GLFW to print errors to System.err

		glfwShowWindow(windowHandle);
		maze.solve();
		while (!glfwWindowShouldClose(windowHandle))
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);//Clear the color buffer and depth buffer for the next frame

			render(maze);//Render the board

			glfwSwapBuffers(windowHandle);//Swap the buffers to display the rendering that has been done
			glfwPollEvents();//Poll for window events (clicking X, moving, resizing, etc.)
		}
		glfwDestroyWindow(windowHandle);//Destroy the window
		glfwTerminate();
		maze.stop();
		finished.set(true);
	}

	public void setInfo(Maze maze, WindowOptions options)
	{
		this.maze = maze;
		this.options = options;
	}

	private IntBuffer winWidth = BufferUtils.createIntBuffer(1), winHeight = BufferUtils.createIntBuffer(1);

	public void render(Maze board)
	{
		glfwGetWindowSize(windowHandle, winWidth, winHeight);
		glViewport(0, 0, winWidth.get(0), winHeight.get(0));
		int width = maze.width, height = maze.height;
		glBegin(GL_QUADS);
		{
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					byte value = board.maze[x + y * width];
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

	public void waitForFinish()
	{
		while (!finished.get())
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
			}
		}
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

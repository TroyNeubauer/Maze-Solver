package com.troy.compsci.maze;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

import com.troy.compsci.maze.algorithm.*;
import com.troy.compsci.maze.gen.*;
import com.troy.compsci.maze.graphics.*;

/**
 * @author Troy Neubauer
 *
 */
public class MazeSolver extends JFrame
{

	public boolean needsRepaint;
	private Maze maze = MazeCreator.defaultLauMaze();
	private AWTGLCanvas canvas;
	private MasterRenderer renderer;
	private final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, (int) TimeUnit.SECONDS.toMicros(1), 0);
	private final JLabel sliderValue = new JLabel("not set yet");
	private final JComboBox<SolverType> solverTypeBox = new JComboBox<SolverType>();
	private final BooleanButon running = new BooleanButon("Stop", "Start", false);
	private final JButton reset = new JButton("Reset");
	
	private CreateNewMazeFrame createNewMaze;

	private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);

	private static final double STEEPNESS = 3.75;

	private JButton generateNewMaze = new JButton("Create New Maze");

	public MazeSolver()
	{
		super("Troy's Maze solver - November 2017");
		try {
			File file = new File("./test.maze");
			MazeEncoding.writeMaze(maze,file);
			maze = MazeEncoding.readMaze(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			canvas = new AWTGLCanvas()
			{
				@Override
				protected void paintGL()
				{
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);//Clear the color buffer and depth buffer for the next frame
					renderer.render(maze);//Render the board
					try
					{
						swapBuffers();
					}
					catch (LWJGLException e)
					{
						e.printStackTrace();
					}
				}
			};
		}
		catch (LWJGLException e2)
		{
			e2.printStackTrace();
			System.exit(1);
		}
		for (SolverType mode : SolverType.values())
		{
			solverTypeBox.addItem(mode);
		}
		renderer = new MasterRenderer(this);
		canvas.setPreferredSize(new Dimension(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth() * 3
				/ 4, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight() * 3 / 4));
		this.setVisible(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI();
		this.setSliderValue();
		slider.addChangeListener((e) ->
		{
			setSliderValue();
		});
		running.addActionListener((e) ->
		{
			if (running.getState())//If were not running
			{
				running.setState(false);
				maze.stop();
			}
			else
			{
				running.setState(true);
				startMaze();
			}
		});
		reset.addActionListener((e) -> {
			maze.reset();
			needsRepaint = true;
		});
		generateNewMaze.addActionListener((e) -> {
			if(createNewMaze == null) createNewMaze = new CreateNewMazeFrame(this);
			createNewMaze.setVisible(true);
		});
		while (true)
		{
			if (needsRepaint || (maze != null && maze.working.get()))
			{
				canvas.validate();
				canvas.repaint();
			}
			else
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e1)
				{
				}
			}
		}
	}

	private void createUI()
	{
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		this.add(new BottomPanel(), BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
	}

	private void startMaze()
	{
		MazeAlgorithm algorithm = null;
		Class<? extends MazeAlgorithm> algorithmClass = null;

		try
		{
			algorithmClass = ((SolverType) solverTypeBox.getSelectedItem()).algorithmClass;
			algorithm = algorithmClass.newInstance();
			algorithm.solver = this;
			algorithm.maze = maze;
		}
		catch (ClassCastException error)
		{
			System.err.println("non algorithm class selected");
			error.printStackTrace();
			System.exit(1);
		}
		catch (InstantiationException | IllegalAccessException e1)
		{
			System.err.println("Unable to create instance of algorithm class: " + algorithmClass);
			e1.printStackTrace();
			System.exit(1);
		}

		maze.setAlgorithm(algorithm);
		maze.solve();
	}
	
	/**
	 * @param maze the maze to set
	 */
	public void setMaze(Maze maze)
	{
		this.maze = maze;
	}
	
	/**
	 * @return the maze
	 */
	public Maze getMaze()
	{
		return maze;
	}

	public class BottomPanel extends JPanel
	{

		public BottomPanel()
		{
			super(new GridBagLayout());
			GridBagConstraints g = new GridBagConstraints();
			g.insets = new Insets(15, 15, 15, 15);
			g.gridx = 0;
			g.gridy = 0;
			this.add(slider, g);
			g.gridx = 0;
			g.gridy = 1;
			g.gridwidth = 2;
			this.add(sliderValue, g);
			g.gridx = 1;
			g.gridy = 0;
			g.gridwidth = 1;
			this.add(solverTypeBox, g);
			g.gridx++;
			this.add(generateNewMaze, g);
			g.gridx++;
			this.add(reset, g);
			g.gridx++;
			this.add(running, g);
		}
	}

	private void setSliderValue()
	{
		double value = getSlowDownMicroSeconds() / 1000.0;
		this.sliderValue.setText(FORMAT.format(value) + " miliseconds delay per iteration ");
	}

	public void stop()
	{
		maze.stop();
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	/**
	 * @return
	 */
	public long getSlowDownMicroSeconds()
	{
		return (int) (Math.pow(((double) ((slider.getMaximum() - slider.getMinimum()) - slider.getValue()) / slider.getMaximum()), (STEEPNESS)) * slider.getMaximum());
	}

	/**
	 * Called by the worker thread when solving the maze is finished
	 */
	public void mazeDone()
	{
		running.setState(false);// Change the button to say "Start"
	}

	public static enum SolverType
	{
		STACK_SINGLE_STREADED("Stack - single threaded", StackAlgorithm.class), STACK_MULTI_STREADED("Stack - multi threaded",
				StackMultiThreadedAlgorithm.class), RECURSIVE("Recursive", RecursiveAlgorithm.class), A_STAR_ALGORITHM("A* Algorithm",
						AStarAlgorithm.class), DIJKSTRAS_ALGORITHM("Dijkstra's Algorithm", DijkstrasAlgorithm.class);
		String name;
		Class<? extends MazeAlgorithm> algorithmClass;

		SolverType(String name, Class<? extends MazeAlgorithm> algorithmClass)
		{
			this.name = name;
			this.algorithmClass = algorithmClass;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}

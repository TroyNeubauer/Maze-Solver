package com.troy.compsci.maze;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

import org.apache.commons.lang3.exception.*;
import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.troy.compsci.maze.algorithm.*;
import com.troy.compsci.maze.gen.*;
import com.troy.compsci.maze.graphics.*;

/**
 * The core class that represents the window and is the center for the entire application
 */
public class MazeSolver extends JFrame
{
	//Can be set from other classes to trigger the maze to be repainted
	public boolean needsRepaint;
	private Maze maze = MazeCreator.defaultLauMaze();
	//The canvas that the maze is drawn on
	private AWTGLCanvas canvas;
	private MasterRenderer renderer;
	//The slider for controlling the speed
	private final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, (int) TimeUnit.SECONDS.toMicros(1), 0);
	//A label showing the current value of the slider
	private final JLabel sliderValue = new JLabel("not set yet");
	//The box containing all the algorithms to choose from
	private final JComboBox<SolverType> solverTypeBox = new JComboBox<SolverType>();
	//Used for starting and stopping the solving of the maze
	private final BooleanButon running = new BooleanButon("Stop", "Start", false);
	private final JButton reset = new JButton("Reset"), saveMaze = new JButton("Save Maze");

	private boolean canEdit = true;//format:off
	enum SetPositionsMode{NONE, SETTING_START, SETTING_END}//format:on

	private SetPositionsMode setPositionsMode = SetPositionsMode.NONE;
	private JButton setPositions = new JButton("Set Positions");

	//The frame for creating a new maze
	private CreateNewMazeFrame createNewMaze;

	private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);

	//How steep should the slow down curve be
	private static final double STEEPNESS = 3.75;

	private JButton generateNewMaze = new JButton("Create New Maze");

	public MazeSolver()
	{
		super("Troy's Maze solver - November 2017");
		try
		{
			canvas = new AWTGLCanvas()
			{
				@Override
				protected void paintGL()//Called by LWJGL when we need to repaint
				{
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);//Clear the color buffer and depth buffer for the next frame
					renderer.render(maze);//Render the board
					try
					{
						swapBuffers();//Swap the front and back buffers to show the user what we just drew
					}
					catch (LWJGLException e)
					{
						e.printStackTrace();
					}
				}
			};
			canvas.addMouseMotionListener(new MouseMotionAdapter()
			{
				@Override
				public void mouseDragged(MouseEvent e)
				{
					int tileX = e.getX() * maze.width / canvas.getWidth();
					int tileY = e.getY() * maze.height / canvas.getHeight();
					if (canEdit && !(tileX == maze.startX && tileY == maze.startY || tileX == maze.endX && tileY == maze.endY))
					{
						maze.setTileId(tileX, tileY, SwingUtilities.isLeftMouseButton(e) ? Maze.PATH : Maze.WALL);
						needsRepaint = true;
					}
				}
			});
			canvas.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					int tileX = e.getX() * maze.width / canvas.getWidth();
					int tileY = e.getY() * maze.height / canvas.getHeight();
					if (canEdit && !(tileX == maze.startX && tileY == maze.startY || tileX == maze.endX && tileY == maze.endY))
					{
						byte id = maze.getTileId(tileX, tileY);
						maze.setTileId(tileX, tileY, (id == Maze.WALL) ? Maze.PATH : Maze.WALL);
					}
					if (setPositionsMode == SetPositionsMode.SETTING_START)
					{
						maze.setTileId(maze.startX, maze.startY, Maze.PATH);
						maze.startX = tileX;
						maze.startY = tileY;
						maze.setTileId(tileX, tileY, Maze.START);
						setPositionsMode = SetPositionsMode.SETTING_END;
					}
					else if (setPositionsMode == SetPositionsMode.SETTING_END)
					{
						maze.setTileId(maze.endX, maze.endY, Maze.PATH);
						maze.endX = tileX;
						maze.endY = tileY;
						maze.setTileId(tileX, tileY, Maze.END);
						canEdit = true;
						setPositionsMode = SetPositionsMode.NONE;
						running.setEnabled(true);
						generateNewMaze.setEnabled(true);
						reset.setEnabled(true);
						saveMaze.setEnabled(true);

					}
					needsRepaint = true;
				}
			});
		}
		catch (LWJGLException e2)
		{
			e2.printStackTrace();
			System.exit(1);
		}
		for (SolverType mode : SolverType.values())
		{
			solverTypeBox.addItem(mode);//Add all the solver types to the combo box
		}
		renderer = new MasterRenderer(this);
		canvas.setPreferredSize(new Dimension(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth() * 3
				/ 4, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight() * 3 / 4));
		this.setVisible(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI();
		slider.addChangeListener((e) ->
		{
			setSliderValue();//Update the label showing the slider's calue when someone changes it
		});
		this.setSliderValue();
		running.addActionListener((e) ->
		{//Called when the user pressed the start/stop button
			if (running.getState())//If we are solving the maze
			{
				maze.stop();//Stop the maze
			}
			else//Otherwise
			{
				startMaze();//Start solving the maze
			}
			running.toggle();
		});
		saveMaze.addActionListener((e) ->
		{
			JFileChooser ch = new JFileChooser();
			ch.setFileFilter(new MazeFileFilter());
			int result = ch.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					MazeEncoding.writeMaze(maze, ch.getSelectedFile());
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, ExceptionUtils.getStackTrace(e1), "Unable to save maze!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		reset.addActionListener((e) ->
		{
			maze.reset();
			needsRepaint = true;
		});
		generateNewMaze.addActionListener((e) ->
		{
			if (createNewMaze == null) createNewMaze = new CreateNewMazeFrame(this);
			createNewMaze.setVisible(true);//Show the other window
		});
		setPositions.addActionListener((e) ->
		{
			maze.stop();
			maze.reset();
			canEdit = false;
			setPositionsMode = SetPositionsMode.SETTING_START;
			running.setEnabled(false);
			generateNewMaze.setEnabled(false);
			reset.setEnabled(false);
			saveMaze.setEnabled(false);
		});
		while (true)
		{//Loop to repaint
			if (needsRepaint || (maze != null && maze.working.get()))
			{
				canvas.validate();
				canvas.repaint();//This will call out paintGL method, thus repainting the maze
			}
			else
			{
				try
				{
					Thread.sleep(1);//Sleep a little bit to lower CPU usage
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
	{//Called when the user pressed start
		MazeAlgorithm algorithm = null;
		Class<? extends MazeAlgorithm> algorithmClass = null;

		try
		{
			algorithmClass = ((SolverType) solverTypeBox.getSelectedItem()).algorithmClass;
			algorithm = algorithmClass.newInstance();// Create the new algorithm object using reflection so we don't have to use any switch or if statements
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

		maze.setAlgorithm(algorithm);//Tell the maze which algorithm to use
		maze.solve();//Start solving the maze
	}

	/**
	 * @param maze the maze to set
	 */
	public void setMaze(Maze maze)
	{
		this.maze = maze;
		this.setSliderValue();
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
			g.gridx++;
			this.add(saveMaze, g);
			g.gridx++;
			this.add(setPositions, g);
		}
	}

	private void setSliderValue()
	{
		long micros = getSlowDownMicroSeconds();
		double value = micros / 1000.0;
		this.sliderValue.setText(FORMAT.format(value) + " miliseconds delay per iteration ");
		if (maze != null) maze.slowDownMicroSeconds = micros;
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
		return (int) (Math.pow(((double) ((slider.getMaximum() - slider.getMinimum()) - slider.getValue()) / slider.getMaximum()), (STEEPNESS))
				* slider.getMaximum());
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

package com.troy.compsci.maze;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import javax.swing.*;

import com.troy.compsci.maze.graphics.*;
import com.troy.compsci.maze.algorithm.*;

public class SwingOptionsFrame extends JFrame
{
	private AtomicBoolean finished = new AtomicBoolean(false);
	private JCheckBox fullscreenBox = new JCheckBox("Fullscreen", false);
	private JTextField windowWidthField = new JTextField(Integer.toString((int) (Main.DISPLAY_WIDTH * 0.75)), 5),
			windowHeightField = new JTextField(Integer.toString((int) (Main.DISPLAY_HEIGHT * 0.75)), 5);

	private JSlider slowDownMSSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 500, 50);
	
	private JTextField mazeWidthField = new JTextField(80 + "", 5), mazeHeightField = new JTextField(50 + "", 5);
	private JTextField startPositionField = new JTextField("0, 0", 7), endPositionField = new JTextField("0, 0", 7);

	private JButton startButton = new JButton("Start!");
	private JComboBox<MazeType> mazeTypeBox = new JComboBox<MazeType>();
	private JComboBox<SolverType> solverTypeBox = new JComboBox<SolverType>();
	private JCheckBox autogeneratePositionsBox = new JCheckBox("Auto-gen positions", true);
	
	private JLabel msLabel = new JLabel();

	private boolean fullscreen, autogeneratePositions;
	private int windowWidth, windowHeight, mazeWidth, mazeHeight;
	private int startX, startY, endX, endY;

	public SwingOptionsFrame() throws HeadlessException
	{
		super("Maze Solver Options - Troy Neubauer");
		for (MazeType mode : MazeType.values())
		{
			mazeTypeBox.addItem(mode);
		}

		for (SolverType mode : SolverType.values())
		{
			solverTypeBox.addItem(mode);
		}
		
		slowDownMSSlider.setPaintTicks(true);
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setContentPane(new Panel());
		this.pack();
		this.setSize(Math.round(this.getWidth() * 1.8f), Math.round(this.getHeight() * 1.8f));
		super.setVisible(true);
	}

	private class Panel extends JPanel
	{
		public Panel()
		{
			super(new GridBagLayout());
			GridBagConstraints goc = new GridBagConstraints();

			JPanel graphicsOptions = new JPanel(new GridBagLayout());
			graphicsOptions.setBorder(BorderFactory.createTitledBorder("Graphics Options"));

			goc.fill = GridBagConstraints.NONE;
			goc.gridx = 0;
			goc.gridy = 0;
			goc.weightx = 1.0;
			goc.weighty = 1.0;
			goc.gridwidth = 2;
			graphicsOptions.add(fullscreenBox, goc);
			
			goc.gridwidth = 1;
			goc.gridy++;
			graphicsOptions.add(new JLabel("Window Width"), goc);
			goc.gridx++;
			graphicsOptions.add(windowWidthField, goc);
			
			goc.gridx = 0;
			goc.gridy++;
			graphicsOptions.add(new JLabel("Window Height"), goc);
			goc.gridx++;
			graphicsOptions.add(windowHeightField, goc);
			
			goc.gridwidth = 2;
			goc.gridx = 0;
			goc.gridy++;
			graphicsOptions.add(new JLabel("Slow down each iteration"), goc);
			
			goc.gridy++;
			graphicsOptions.add(slowDownMSSlider, goc);
			
			goc.gridy++;
			graphicsOptions.add(msLabel, goc);

			JPanel gameOptions = new JPanel(new GridBagLayout());
			gameOptions.setBorder(BorderFactory.createTitledBorder("Maze Options"));

			GridBagConstraints goc2 = new GridBagConstraints();
			goc2.fill = GridBagConstraints.NONE;
			goc2.gridx = 0;
			goc2.gridy = 0;
			goc2.weightx = 1.0;
			goc2.weighty = 1.0;
			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Maze Type"), goc2);
			goc2.gridx++;
			gameOptions.add(mazeTypeBox, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Solver Type"), goc2);
			goc2.gridx++;
			gameOptions.add(solverTypeBox, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Maze Width"), goc2);
			goc2.gridx++;
			gameOptions.add(mazeWidthField, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Maze Height"), goc2);
			goc2.gridx++;
			gameOptions.add(mazeHeightField, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Start Position (x, y)"), goc2);
			goc2.gridx++;
			gameOptions.add(endPositionField, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("End Position (x, y)"), goc2);
			goc2.gridx++;
			gameOptions.add(startPositionField, goc2);

			goc2.gridx = 0;
			goc2.gridy++;
			gameOptions.add(new JLabel("Auto Generate Positions"), goc2);
			goc2.gridx++;
			gameOptions.add(autogeneratePositionsBox, goc2);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;
			this.add(graphicsOptions, c);
			c.gridx++;
			this.add(gameOptions, c);
			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy++;
			c.weighty = 0.1;
			this.add(startButton, c);
			addListeners();
		}

		private void addListeners()
		{

			slowDownMSSlider.addChangeListener((e) -> {
				msLabel.setText(slowDownMSSlider.getValue() + " Miliseconds");
			});
			slowDownMSSlider.setValue(0);
			
			autogeneratePositionsBox.addActionListener((e) ->
			{
				startPositionField.setEnabled(!autogeneratePositionsBox.isSelected());
				endPositionField.setEnabled(!autogeneratePositionsBox.isSelected());
			});
			autogeneratePositionsBox.setSelected(true);
			startPositionField.setEnabled(false);
			endPositionField.setEnabled(false);

			fullscreenBox.addActionListener((e) ->
			{
				fullscreen = fullscreenBox.isSelected();
				if (fullscreen)
				{
					windowWidthField.setEnabled(false);
					windowHeightField.setEnabled(false);
				}
				else
				{
					windowWidthField.setEnabled(true);
					windowHeightField.setEnabled(true);
				}
			});

			mazeTypeBox.addActionListener((e) ->
			{
				if (mazeTypeBox.getSelectedItem().equals(MazeType.MR_LAU_HARDCODED_MAZE))
				{
					mazeWidthField.setEnabled(false);
					mazeHeightField.setEnabled(false);
				}
				else
				{
					mazeWidthField.setEnabled(true);
					mazeHeightField.setEnabled(true);
				}
				if (mazeTypeBox.getSelectedItem().equals(MazeType.RANDOM_MAZE))
				{
					autogeneratePositionsBox.setSelected(true);
					autogeneratePositionsBox.setEnabled(false);
				}
				else
				{
					autogeneratePositionsBox.setEnabled(true);
				}
			});
			mazeTypeBox.setSelectedIndex(0);//to trigger the listener so that the input fields are in the currect state
			startButton.addActionListener((e) ->
			{
				try
				{
					autogeneratePositions = autogeneratePositionsBox.isSelected();
					windowWidth = Integer.parseInt(windowWidthField.getText().isEmpty() ? "0" : windowWidthField.getText());
					windowHeight = Integer.parseInt(windowHeightField.getText().isEmpty() ? "0" : windowHeightField.getText());
					if (mazeTypeBox.getSelectedItem().equals(MazeType.MR_LAU_HARDCODED_MAZE))
					{
						mazeWidth = 13;
						mazeHeight = 8;
					}
					else
					{
						mazeWidth = Integer.parseInt(mazeWidthField.getText().isEmpty() ? "0" : mazeWidthField.getText());
						mazeHeight = Integer.parseInt(mazeHeightField.getText().isEmpty() ? "0" : mazeHeightField.getText());
					}
					try
					{
						String[] start = startPositionField.getText().replaceAll(" ", "").split(",");
						String[] end = endPositionField.getText().replaceAll(" ", "").split(",");
						startX = mazeWidth - Integer.parseInt(start[0]) - 1;
						startY = Integer.parseInt(start[1]);
						endX = mazeWidth - Integer.parseInt(end[0]) - 1;
						endY = Integer.parseInt(end[1]);
						if (startX >= mazeWidth || startY >= mazeHeight || endX >= mazeWidth || endY >= mazeHeight || startX < 0 || startY < 0 || endX < 0
								|| endY < 0) { throw new Exception();// to trigger the catch block
						}
						finished.set(true);//Tell the listening thread that we are ready
					}
					catch (Exception c)
					{
						JOptionPane.showMessageDialog(this, "Invalid positions!\nMake sure to \"(x, y)\", not \"(row, col)\"\n"
								+ "Also remember to keep coordinates positive and nor exceding the bounds [width:" + mazeWidth + ", height:" + mazeHeight
								+ "]", "Unable To Start", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (Exception i)
				{
					JOptionPane.showMessageDialog(this, "Invalid arguments!", "Unable To Start", JOptionPane.ERROR_MESSAGE);
				}

			});

		}
	}

	/**
	 * @return The options that are used for the game
	 */
	public Maze waitForInformation()
	{
		while (!finished.get())
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{//Ignore
			}
		}
		finished.set(false);//So next time we have to wait for information again
		if (fullscreen)
		{
			windowWidth = Main.DISPLAY_WIDTH;
			windowHeight = Main.DISPLAY_HEIGHT;
		}
		MazeAlgorithm algorithm = null;
		try
		{
			algorithm = ((SolverType) solverTypeBox.getSelectedItem()).algorithmClass.newInstance();
		}
		catch (Exception e)
		{
			System.err.println("non algorithm class selected");
			e.printStackTrace();
			System.exit(1);
		}
		Maze maze;
		switch ((MazeType) mazeTypeBox.getSelectedItem())
		{
			case MR_LAU_HARDCODED_MAZE:
				maze = MazeCreator.defaultLauMaze(algorithm);
				if (autogeneratePositions)
				{
					startX = 0;
					startY = 0;
					endX = mazeWidth - 1;
					endY = mazeHeight - 1;
				}
				break;
			case EMPTY_MAZE:
				maze = MazeCreator.emptyMaze(algorithm, mazeWidth, mazeHeight);
				if (autogeneratePositions)
				{
					startX = 0;
					startY = 0;
					endX = mazeWidth - 1;
					endY = mazeHeight - 1;
				}
				break;
			case RANDOM_WALLS:
				maze = MazeCreator.randomWalls(algorithm, mazeWidth, mazeHeight);
				startX = 0;
				startY = 0;
				endX = mazeWidth - 1;
				endY = mazeHeight - 1;
				break;
			case RANDOM_MAZE:
				maze = MazeCreator.randomMaze(algorithm, mazeWidth, mazeHeight);
				startX = 0;
				startY = 0;
				endX = mazeWidth - 1;
				endY = mazeHeight - 1;
				while (maze.maze[startX + startY * maze.width] == Maze.WALL)
				{
					startX++;
					startY++;
				}
				while (endX >= 0 && endY >= 0 && (maze.maze[endX + endY * maze.width] == Maze.WALL))
				{
					endX--;
					endY--;
				}
				break;
			default:
				throw new InternalError("Unknown maze type!");
		}
		maze.setLocations(startX, startY, endX, endY);
		maze.slowDownMS = slowDownMSSlider.getValue();
		algorithm.maze = maze;
		return maze;
	}

	public WindowOptions getWindowOptions()
	{
		return new WindowOptions(windowWidth, windowHeight, fullscreen);
	}

	public static enum MazeType
	{
		MR_LAU_HARDCODED_MAZE("Basic Maze (credits to Mr. Lau)"), EMPTY_MAZE("Empty Maze (no walls)"), RANDOM_WALLS("Random Walls"), RANDOM_MAZE(
				"Random Maze");
		String name;

		MazeType(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
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

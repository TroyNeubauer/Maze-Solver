package com.troy.compsci.maze.gen;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import com.troy.compsci.maze.*;

public class CreateNewMazeFrame extends JFrame
{

	private JComboBox<MazeType> mazeTypeBox = new JComboBox<MazeType>();
	private JButton createMaze = new JButton("Create Maze");
	JPanel bigPanel = new JPanel(new BorderLayout()), center = new JPanel(new GridBagLayout());
	private MazeSolver solver;

	public CreateNewMazeFrame(MazeSolver solver)
	{
		super("Create a new Maze");

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.solver = solver;
		for (MazeType mode : MazeType.values())
		{
			mazeTypeBox.addItem(mode);
		}
		createMaze.addActionListener((e) -> tryGen());
		GridBagConstraints g = new GridBagConstraints();
		System.out.println(Arrays.toString(MazeCreatorPackage.PACKAGES));
		for(MazeCreatorPackage pack : MazeCreatorPackage.PACKAGES) {
			JPanel panel = new JPanel();
			pack.addComponets(center, g);
		}
		
		bigPanel.add(mazeTypeBox, BorderLayout.NORTH);
		bigPanel.add(center, BorderLayout.CENTER);
		bigPanel.add(createMaze, BorderLayout.SOUTH);
		this.setSize(900, 600);
		this.add(bigPanel);
	}

	public void tryGen()
	{
		MazeType type = (MazeType) mazeTypeBox.getSelectedItem();
		for (MazeCreatorPackage pack : type.packages)
		{
			String message = pack.checkRequiredValues();
			if (message != null)
			{
				System.out.println(message);
				JOptionPane.showMessageDialog(null, message, "Unable to create Maze", JOptionPane.ERROR_MESSAGE);
			}
		}
		try
		{
			Maze maze = type.create();
			if (maze != null)
			{
				solver.getMaze().stop();
				solver.setMaze(maze);
			}
		}
		catch (Exception e)
		{

		}

	}

}

package com.troy.compsci.maze.gen;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import com.troy.compsci.maze.*;

public class CreateNewMazeFrame extends JFrame
{

	private JComboBox<MazeType> mazeTypeBox = new JComboBox<MazeType>();
	private JButton createMaze = new JButton("Create Maze");
	private JPanel bigPanel = new JPanel(new BorderLayout()), center = new JPanel(new GridBagLayout());
	private MazeSolver solver;

	public CreateNewMazeFrame(MazeSolver solver)
	{
		super("Create a new Maze");
		setVisible(false);

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.solver = solver;
		for (MazeType mode : MazeType.values())
		{
			mazeTypeBox.addItem(mode);
		}
		createMaze.addActionListener((e) -> tryGen());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(20, 20, 20, 20);
		for (MazeCreatorPackage pack : MazeCreatorPackage.PACKAGES)
		{
			pack.setBorder(BorderFactory.createTitledBorder(pack.getName()));
			pack.addComponets(new GridBagConstraints());
			center.add(pack, g);
			g.gridy++;
		}
		bigPanel.add(mazeTypeBox, BorderLayout.NORTH);
		bigPanel.add(center, BorderLayout.CENTER);
		bigPanel.add(createMaze, BorderLayout.SOUTH);
		this.setSize(900, 600);
		this.add(bigPanel);
		this.pack();

		mazeTypeBox.addActionListener((e) ->
		{
			MazeType type = (MazeType) mazeTypeBox.getSelectedItem();
			List<MazeCreatorPackage> allPackages = new ArrayList<MazeCreatorPackage>();
			for (MazeCreatorPackage pack : MazeCreatorPackage.PACKAGES)
				allPackages.add(pack);
			
			for (MazeCreatorPackage pack : type.packages)
				allPackages.remove(allPackages.indexOf(pack));

			for (MazeCreatorPackage pack : allPackages)
				pack.disableAllComponents();

			for (MazeCreatorPackage pack : type.packages)
				pack.enableAllComponents();

		});
		mazeTypeBox.setSelectedIndex(0);
	}

	public void tryGen()
	{
		MazeType type = (MazeType) mazeTypeBox.getSelectedItem();
		for (MazeCreatorPackage pack : type.packages)
		{
			String message = pack.checkRequiredValues();
			if (message != null)
			{
				JOptionPane.showMessageDialog(null, message, "Unable to create Maze", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		try
		{
			Maze maze = type.create();
			if (maze != null)
			{
				solver.getMaze().stop();
				solver.setMaze(maze);
				this.setVisible(false);
				solver.needsRepaint = true;
				solver.requestFocus();
			}
		}
		catch (Exception e)
		{

		}

	}

}

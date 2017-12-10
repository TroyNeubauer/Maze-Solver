package com.troy.compsci.maze.gen;

import java.awt.*;
import java.io.*;

import javax.swing.*;

public class FileMazeCreatorPackage extends MazeCreatorPackage
{

	public FileMazeCreatorPackage()
	{
		super("From File");
	}

	public JTextField file = new JTextField(30);
	public JButton chooseFile = new JButton("Choose File");

	@Override
	public void addComponets(GridBagConstraints g)
	{
		g.gridy = 0;
		g.gridx = 0;
		g.gridwidth = GridBagConstraints.RELATIVE;
		g.gridheight = GridBagConstraints.RELATIVE;
		add(new JLabel("File: "), g);

		g.gridy = 0;
		g.gridx = 1;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.gridheight = GridBagConstraints.RELATIVE;
		add(file, g);

		g.gridy = 1;
		g.gridx = 0;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.gridheight = GridBagConstraints.REMAINDER;
		add(chooseFile, g);

		chooseFile.addActionListener((e) ->
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1)
			{
			}
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(chooseFile.getText()));
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file.setText(fc.getSelectedFile().getAbsolutePath());
			}
			try
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1)
			{
			}
		});
	}

	@Override
	public String checkRequiredValues()
	{
		if (file.getText() == null || file.getText().isEmpty()) return "A file must be specified!";
		if (!new File(file.getText()).exists()) return "The choosen file must exist!";
		return null;
	}
}

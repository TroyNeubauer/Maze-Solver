package com.troy.compsci.maze.gen;

import java.awt.*;
import java.io.*;

import javax.swing.*;

public class FileMazeCreatorPackage extends MazeCreatorPackage
{
	public JTextField file = new JTextField();
	private JButton chooseFile = new JButton("Choose File");

	@Override
	public void addComponets(JPanel panel, GridBagConstraints g)
	{
		panel.add(file, g);
		g.gridy++;
		panel.add(chooseFile, g);
		g.gridy++;

		chooseFile.addActionListener((e) ->
		{
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file.setText(fc.getSelectedFile().getAbsolutePath());
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

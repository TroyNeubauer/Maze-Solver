package com.troy.compsci.maze.mazetype;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.nio.charset.*;
import java.text.*;
import java.text.AttributedCharacterIterator.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.exception.*;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.gen.*;

public class FileMaze extends MazeType
{
	public FileMaze()
	{
		super(MazeCreatorPackage.FILE, MazeCreatorPackage.WIDTH_AND_HEIGHT);
		MazeCreatorPackage.FILE.file.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				updateSelection();
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				updateSelection();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				updateSelection();
			}
		});
	}

	//format:off
	private enum FileType {MAZE_FILE, IMAGE_FILE, TEXT_FILE, UNKNOWN}//format:on

	private FileType type = FileType.UNKNOWN;

	@Override
	public Maze create()
	{
		File file = new File(MazeCreatorPackage.FILE.file.getText());

		if (type == FileType.MAZE_FILE)
		{
			try
			{
				return MazeEncoding.readMaze(new File(MazeCreatorPackage.FILE.file.getText()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "The file " + new File(MazeCreatorPackage.FILE.file.getText()).getName()
						+ " is either corrupted or not a Troy Maze file! Error:\n"
						+ ExceptionUtils.getMessage(e), "Unable to create maze", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (type == FileType.IMAGE_FILE)
		{
			try
			{
				return MazeCreator.createImageMaze(new File(MazeCreatorPackage.FILE.file.getText()), MazeCreatorPackage.WIDTH_AND_HEIGHT.getWidth(), MazeCreatorPackage.WIDTH_AND_HEIGHT.getHeight(), MazeCreatorPackage.WALL_PERCENT.getValue());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Unable to create maze from image file: " + new File(MazeCreatorPackage.FILE.file.getText()).getName()
						+ "\n" + ExceptionUtils.getMessage(e), "Unable to create maze", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (type == FileType.TEXT_FILE)
		{
			try
			{
				String entireFile = FileUtils.readFileToString(file, (Charset) null);
				int width = (int) Math.sqrt(entireFile.length()) * 20;//So that the maze is a square
				BufferedImage image = renderTextToImage(new Font("timesnewroman", Font.PLAIN, 12), Color.BLACK, entireFile, width);
				System.out.println("width " + image.getWidth() + " height " + image.getHeight());
				byte[] mazeData = new byte[image.getWidth() * image.getHeight()];
				for (int y = 0; y < image.getHeight(); y++)
				{
					for (int x = 0; x < image.getWidth(); x++)
					{
						mazeData[x + y * image.getWidth()] = (image.getRGB(x, y) & 0xFF) < 0x80 ? Maze.WALL : Maze.PATH;
					}
				}
				return new Maze(mazeData, image.getWidth(), image.getHeight());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Unknown file: " + file
					+ "\nTroy's Maze program can maze files, images and text files but not this one!", "Unknown File", JOptionPane.ERROR_MESSAGE);
		}
		throw new RuntimeException();
	}

	private void setType()
	{
		String extension = FilenameUtils.getExtension(MazeCreatorPackage.FILE.file.getText());
		if (!extension.isEmpty())
		{
			if (extension.equalsIgnoreCase(MazeEncoding.EXTENSION)) type = FileType.MAZE_FILE;
			if (extension.equalsIgnoreCase("txt")) type = FileType.TEXT_FILE;
			for (String ext : ImageIO.getReaderFormatNames())
			{
				if (ext.equalsIgnoreCase(extension))
				{
					type = FileType.IMAGE_FILE;
					return;
				}
			}
		}
		else
		{
			type = FileType.UNKNOWN;
		}
	}

	@Override
	public void onSelect()
	{
		updateSelection();
		MazeCreatorPackage.FILE.chooseFile.addActionListener((e) ->
		{
			updateSelection();
		});
	}

	@Override
	public void onDeSelect()
	{

	}

	public void updateSelection()
	{
		setType();
		System.out.println("selected: " + type);
		if (type == FileType.IMAGE_FILE) MazeCreatorPackage.WALL_PERCENT.enableAllComponents();
		else MazeCreatorPackage.WALL_PERCENT.disableAllComponents();
	}

	/** 
	   * Renders a paragraph of text (line breaks ignored) to an image (created and returned). 
	   *
	   * @param font The font to use
	   * @param textColor The color of the text
	   * @param text The message
	   * @param width The width the text should be limited to
	   * @return An image with the text rendered into it
	   */
	public static BufferedImage renderTextToImage(Font font, Color textColor, String text, int width)
	{
		Hashtable<Attribute, Object> map = new Hashtable<Attribute, Object>();
		map.put(TextAttribute.FONT, font);
		AttributedString attributedString = new AttributedString(text, map);
		AttributedCharacterIterator paragraph = attributedString.getIterator();

		FontRenderContext frc = new FontRenderContext(null, false, false);
		int paragraphStart = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

		float drawPosY = 0;

		//First time around, just determine the height
		while (lineMeasurer.getPosition() < paragraphEnd)
		{
			TextLayout layout = lineMeasurer.nextLayout(width);

			// Move it down
			drawPosY += layout.getAscent() + layout.getDescent() + layout.getLeading();
		}

		BufferedImage image = createCompatibleImage(width, (int) drawPosY);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		graphics.setColor(textColor);

		drawPosY = 0;
		lineMeasurer.setPosition(paragraphStart);
		while (lineMeasurer.getPosition() < paragraphEnd)
		{
			TextLayout layout = lineMeasurer.nextLayout(width);

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			/* Compute pen x position.  If the paragraph is
			   right-to-left, we want to align the TextLayouts
			   to the right edge of the panel.
			 */
			float drawPosX;
			if (layout.isLeftToRight())
			{
				drawPosX = 0;
			}
			else
			{
				drawPosX = width - layout.getAdvance();
			}

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(graphics, drawPosX, drawPosY);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}

		graphics.dispose();
		return image;
	}

	/**
	 * Creates an image compatible with the current display
	 * 
	 * @return A BufferedImage with the appropriate color model
	 */
	public static BufferedImage createCompatibleImage(int width, int height)
	{
		GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		return configuration.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

}

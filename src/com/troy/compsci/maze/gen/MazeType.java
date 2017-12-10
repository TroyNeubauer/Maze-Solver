package com.troy.compsci.maze.gen;

import java.util.*;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.mazetype.*;

import io.github.lukehutch.fastclasspathscanner.*;

public abstract class MazeType
{
	public static final List<MazeType> values;

	static
	{
		//Scan for all sub clases and automatically put them in values
		List<String> classes = new FastClasspathScanner(EmptyMaze.class.getPackage().getName()).scan(1).getNamesOfSubclassesOf(MazeType.class);
		values = new ArrayList<MazeType>();
		for (String clazz : classes)
		{
			try
			{
				Class<? extends MazeType> clazzObj = (Class<? extends MazeType>) Class.forName(clazz);
				values.add(clazzObj.newInstance());
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public List<MazeCreatorPackage> packages = new ArrayList<MazeCreatorPackage>();
	private String name;

	public MazeType(MazeCreatorPackage... packages)
	{
		String rawName = this.getClass().getSimpleName();
		StringBuilder sb = new StringBuilder(rawName.length());
		boolean first = true;
		for (char c : rawName.toCharArray())
		{
			if (Character.isUpperCase(c) && !first)
			{
				sb.append(' ');
			}
			first = false;
			sb.append(c);
		}
		this.name = sb.toString();

		for (MazeCreatorPackage p : packages)
		{
			this.packages.add(p);
		}
	}

	@Override
	public String toString()
	{
		return name;
	}

	public abstract Maze create();

	public static List<MazeType> values()
	{
		return values;
	}

	/**
	 * Called when this maze type is selected
	 */
	public abstract void onSelect();

	/**
	 * 
	 */
	public abstract void onDeSelect();
}

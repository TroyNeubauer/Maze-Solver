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
		List<String> classes = new FastClasspathScanner(EmptyMaze.class.getPackage().getName()).scan(1).getNamesOfSubclassesOf(MazeType.class);
		System.out.println(Arrays.toString(classes.toArray()));
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

	List<MazeCreatorPackage> packages = new ArrayList<MazeCreatorPackage>();
	String name;

	public MazeType(MazeCreatorPackage... packages)
	{
		String rawName = this.getClass().getSimpleName(), name = "";
		boolean first = true;
		for (char c : rawName.toCharArray())
		{
			if (Character.isUpperCase(c) && !first)
			{
				name += ' ';
			}
			first = false;
			name += c;
		}

		this.name = name;
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
}

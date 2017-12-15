package com.troy.compsci.maze.gen;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public abstract class MazeCreatorPackage extends JPanel
{
	public static final MazeCreatorPackage[] PACKAGES;

	public static final FileMazeCreatorPackage FILE = new FileMazeCreatorPackage();

	public static final WAHMazeCreatorPackage WIDTH_AND_HEIGHT = new WAHMazeCreatorPackage();
	
	public static final WallPercentPackage WALL_PERCENT = new WallPercentPackage();

	static
	{
		int count = 0;
		for (Field field : MazeCreatorPackage.class.getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers()) && MazeCreatorPackage.class.isAssignableFrom(field.getType()) && field.getType() == WAHMazeCreatorPackage.class)
			{
				count++;
			}
		}
		PACKAGES = new MazeCreatorPackage[count];
		int i = 0;
		for (Field field : MazeCreatorPackage.class.getDeclaredFields())
		{
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers()) && MazeCreatorPackage.class.isAssignableFrom(field.getType()) && field.getType() == WAHMazeCreatorPackage.class)
			{
				try
				{
					PACKAGES[i++] = (MazeCreatorPackage) field.get(null);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private String name;

	public MazeCreatorPackage(String name)
	{
		super(new GridBagLayout());
		this.setPreferredSize(new Dimension(500, 350));
		this.name = name;
	}
	

	/**
	 * Adds all components that the user needs to interact with to {@code panel}
	 * @param g Constraints that should be used when adding 
	 */
	public abstract void addComponets(GridBagConstraints g);

	/**
	 * Checks that all values required by this package are valid
	 * @return If all values are valid {@code null}
	 * Otherwise a message describing why any invalid value is invalid
	 */
	public abstract String checkRequiredValues();

	public void disableAllComponents()
	{
		this.setEnabled(false);
		for (Component c : getAllComponets())
		{
			c.setEnabled(false);
		}
	}

	public void enableAllComponents()
	{
		this.setEnabled(true);
		for (Component c : getAllComponets())
		{
			c.setEnabled(true);
		}
	}

	private List<Component> getAllComponets()
	{
		return getAllComponets(this.getClass(), new ArrayList<Component>());
	}

	private List<Component> getAllComponets(Class<?> clazz, ArrayList<Component> list)
	{
		for (Field field : clazz.getDeclaredFields())
		{
			field.setAccessible(true);
			if (!Modifier.isStatic(field.getModifiers()) && Component.class.isAssignableFrom(field.getType()))
			{
				try
				{
					list.add((Component) field.get(this));
				}
				catch (IllegalArgumentException | IllegalAccessException ignore)
				{
				}
			}
		}
		return list;
	}

	/**
	 * @return The name of this package ()
	 */
	public String getName() {
		return name;
	}

}

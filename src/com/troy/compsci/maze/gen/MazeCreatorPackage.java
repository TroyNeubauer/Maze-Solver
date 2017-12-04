package com.troy.compsci.maze.gen;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public abstract class MazeCreatorPackage
{
	public static final MazeCreatorPackage[] PACKAGES;

	public static final FileMazeCreatorPackage FILE = new FileMazeCreatorPackage();

	public static final WAHMazeCreatorPackage WIDTH_AND_HEIGHT = new WAHMazeCreatorPackage();

	static
	{
		int count = 0;
		for (Field field : MazeCreatorPackage.class.getDeclaredFields())
		{
			System.out.println("f" + field.toString());
			if (Modifier.isStatic(field.getModifiers()) && MazeCreatorPackage.class.isAssignableFrom(field.getType()))
			{
				count++;
			}
		}
		PACKAGES = new MazeCreatorPackage[count];
		int i = 0;
		for (Field field : MazeCreatorPackage.class.getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers()) && MazeCreatorPackage.class.isAssignableFrom(field.getType()))
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

	/**
	 * Adds all components that the user needs to interact with to {@code panel}
	 * @param panel The panel to add all the components to
	 * @param g Constraints that should be used when adding 
	 */
	public abstract void addComponets(JPanel panel, GridBagConstraints g);

	/**
	 * Checks that all values required by this package are valid
	 * @return If all values are valid {@code null}
	 * Otherwise a message describing why any invalid value is invalid
	 */
	public abstract String checkRequiredValues();

	public void disableAllComponents()
	{
		for (Component c : getAllComponets())
		{
			c.setEnabled(false);
		}
	}

	public void enableAllComponents()
	{
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
		for (Field field : clazz.getFields())
		{
			if (!Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(Component.class))
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
		if (clazz.getSuperclass() != MazeCreatorPackage.class) { return getAllComponets(this.getClass().getSuperclass(), list); }
		return list;
	}

}

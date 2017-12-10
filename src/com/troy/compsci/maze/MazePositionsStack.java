package com.troy.compsci.maze;

import java.util.*;

/**
 * Represents a stack using the primitive type {@code int} so that no auto boxing is necessary 
 * because that yields poor performance.
 * @author Troy Neubauer
 *
 */
public class MazePositionsStack
{
	private int[] values = new int[100];
	private int size;

	public void push(int value)
	{
		ensureCapacity(size + 1);
		values[size++] = value;
	}
	
	public void push(int x, int y)
	{
		ensureCapacity(size + 2);
		values[size++] = x;
		values[size++] = y;
	}
	
	public void bigPush(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		ensureCapacity(size + 8);
		values[size++] = x1;
		values[size++] = y1;
		values[size++] = x2;
		values[size++] = y2;
		values[size++] = x3;
		values[size++] = y3;
		values[size++] = x4;
		values[size++] = y4;
	}

	public int pop()
	{
		return values[--size];
	}

	public int peep()
	{
		return values[size - 1];
	}

	private void ensureCapacity(int size)
	{
		this.size = this.size < 0 ? 0 : this.size;
		if (size >= values.length)
		{
			values = Arrays.copyOf(values, values.length * 3);
		}
	}
	
	public boolean isEmpty() {
		return size == 0;
	}

}

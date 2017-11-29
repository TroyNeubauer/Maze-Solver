package com.troy.compsci.maze.algorithm;

import java.util.*;

import com.troy.compsci.maze.*;

public class AStarAlgorithm extends MazeAlgorithm
{
	List<Node> openList = new LinkedList<Node>();

	@Override
	public boolean solve()
	{
		//cache values for faster access (no having to derefrence maze)
		int startX = maze.startX, startY = maze.startY, endX = maze.endX, endY = maze.endY, width = maze.width, height = maze.height;
		openList.add(new Node(startX, startY, null, 0, distance(startX, startY, endX, endY)));

		Node current;
		while (!openList.isEmpty())
		{
			current = getBest();//get the best node to work on
			int x = current.x, y = current.y;
			if (x == endX && y == endY)
			{
				current = current.parent;
				while (current != null)
				{
					maze.maze[current.x + current.y * maze.width] = Maze.SOLUTION_PATH;
					current = current.parent;
				}
				return true;//We have reached the goal. We win!
			}
			maze.maze[x + y * width] = Maze.CLOSED;//mark as closed because we are done with this one

			if (sleep()) return false;
			maze.steps++;

			Node neighbor;//try to create new neighbors
			for (int i = 0; i < 4; i++)
			{
				int nx = x + ((i == 0) ? 1 : (i == 2) ? -1 : 0);
				int ny = y + ((i == 1) ? 1 : (i == 3) ? -1 : 0);
				if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;//continue if we are out of bounds
				byte id = maze.maze[nx + ny * width];
				if (id == Maze.CLOSED || id == Maze.WALL) continue;
				if (id != Maze.END) maze.maze[nx + ny * width] = Maze.VISITED;
				double g = /*current.gCost + 1*/ distance(nx, ny, startX, startY);
				int index = containsNode(nx, ny);
				if (index == -1)
				{//if the new node isn't in the list
					double h = distance(nx, ny, endX, endY);
					neighbor = new Node(nx, ny, current, g, h);
				}
				else
				{//if it is in the list
					if (g >= (neighbor = openList.get(index)).gCost) continue;//continue is is currently has a shorter path
				}

				neighbor.parent = current;
				neighbor.gCost = g;
				neighbor.hCost = distance(nx, ny, endX, endY);
				neighbor.calculateFCost();
				if (index == -1) openList.add(neighbor);

			}
		}
		return false;
	}

	public Node getBest()
	{
		if (openList.size() == 0) return null;
		int index = 0;
		Node best = openList.get(index);
		for (int i = 1; i < openList.size(); i++)
		{
			Node node = openList.get(i);
			if (node.fCost < best.fCost)
			{
				best = node;
				index = i;
			}
			//System.out.println("looking at " + node);
		}
		//System.out.println("\tbest " + best);
		openList.remove(index);
		return best;
	}

	private int containsNode(int x, int y)
	{
		int size = openList.size();
		for (int i = 0; i < size; i++)
		{
			Node node = openList.get(i);
			if (node.x == x && node.y == y) return i;
		}
		return -1;
	}

	public class Node
	{
		public int x, y;
		public Node parent;
		public double fCost, gCost, hCost;

		public Node(int x, int y, Node parent, double gCost, double hCost)
		{
			this.x = x;
			this.y = y;
			this.parent = parent;
			this.gCost = gCost;
			this.hCost = hCost;
			calculateFCost();
		}

		/**
		 * 
		 */
		public void calculateFCost()
		{
			this.fCost = gCost * 0.9 + hCost;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(fCost);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(gCost);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(hCost);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((parent == null) ? 0 : parent.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Node other = (Node) obj;
			if (Double.doubleToLongBits(fCost) != Double.doubleToLongBits(other.fCost)) return false;
			if (Double.doubleToLongBits(gCost) != Double.doubleToLongBits(other.gCost)) return false;
			if (Double.doubleToLongBits(hCost) != Double.doubleToLongBits(other.hCost)) return false;
			if (parent == null)
			{
				if (other.parent != null) return false;
			}
			else if (!parent.equals(other.parent)) return false;
			if (x != other.x) return false;
			if (y != other.y) return false;
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "Node [x=" + x + ", y=" + y + ", parent=" + (parent == null ? "null" : "[" + parent.x + ", " + parent.y + "]") + ", fCost=" + fCost
					+ ", gCost=" + gCost + ", hCost=" + hCost + "]";
		}

	}
}

package com.troy.compsci.maze.algorithm;

import java.util.concurrent.atomic.*;

import com.troy.compsci.maze.*;
import com.troy.compsci.maze.graphics.*;

public class StackMultiThreadedAlgorithm extends MazeAlgorithm
{

	private final MazePositionsStack mainStack = new MazePositionsStack();
	private AtomicBoolean done = new AtomicBoolean(false);
	private AtomicInteger needsHelp = new AtomicInteger(0);

	@Override
	public boolean solve()
	{
		mainStack.push(maze.startX, maze.startY);
		boolean threadsWorking = true;
		double slowMS = maze.slowDownMicroSeconds / 1000.0;
		double multiplier;
		if(slowMS < 1) multiplier = 1;
		if(slowMS < 5) multiplier = 1.5;
		if(slowMS < 20) multiplier = 3;
		if(slowMS < 50) multiplier = 4;
		else multiplier = 8;
		WorkerThread[] workers = new WorkerThread[Math.max((int) (Runtime.getRuntime().availableProcessors() * multiplier), 1)];
		byte lastColor = 0;
		for (int i = 0; i < workers.length; i++)
		{
			workers[i] = new WorkerThread(new Worker(i));
			// Assign each thread a unique color

			// Add 5 to skip to the next color channel so that the colors for i=0 and i=1 are very different
			byte color = (byte) (lastColor + 5);
			while (MasterRenderer.SPECIAL_COLORS.contains(Byte.valueOf(color)) || color >= 0b00111111 || isTooDark(color))
			{
				if (color >= 0b00111111) color = 1;// revert back to 1 to avoid using the 2 high bits in the byte
				color++;//Move to the next color and try again
			}
			lastColor = color;
			color |= 0b10000000;// Make the sign bit a 1 so that the renderer doesn't recognize it and instead
			// just draws the lowest 6 bits.
			workers[i].worker.color = color;// Assign the color
			workers[i].start();//start the thread
		}
		while (true)
		{
			if (Thread.currentThread().isInterrupted() || done.get()) break;
			threadsWorking = false;
			for (WorkerThread thread : workers)
			{
				threadsWorking |= thread.worker.working.get();
			}
			synchronized (mainStack)
			{
				if (mainStack.isEmpty() && !threadsWorking) break;
			}
			try
			{
				sleep(1);
			}
			catch (Exception e)
			{
				break;
			}
		}

		for (WorkerThread worker : workers)
			worker.interrupt();

		if (Thread.currentThread().isInterrupted()) throw new StoppedByUserException();
		return done.get();
	}

	/**
	 * @param color
	 * @return
	 */
	private boolean isTooDark(byte color)
	{
		int sum = ((color >> 4) & 0b11) + ((color >> 2) & 0b11) + ((color >> 0) & 0b11);
		return sum <= 2;
	}

	private class Worker implements Runnable
	{
		private final MazePositionsStack personalStack = new MazePositionsStack();
		protected final AtomicBoolean working = new AtomicBoolean(true);
		protected int id;
		protected byte color;
		private int evalCount;

		public Worker(int id)
		{
			this.id = id;
		}

		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					if (done.get() || Thread.interrupted()) break;

					if (personalStack.isEmpty())
					{//If this worker's stack is empty we either need to wait for more data from the main stack (if its empty) or fill out stack with new data

						if (mainStack.isEmpty())
						{//Wait for another thread to push a location onto the stack
							working.set(false);
							int needsHelpValue;
							synchronized (needsHelp)
							{
								needsHelp.set(Math.max(needsHelp.get() + 1, 0));//Increment and prevent it being < 0
								needsHelpValue = needsHelp.get();
							}
							while (needsHelpValue == needsHelp.get())
							{
								sleep(1);
								//Just wait until another thread receives our cry for help and pushes a valid space into the mainStack for us to work on
							}
							// If we get here, another thread is in the process of pushing a tile onto the main stack for us so,
							// Wait for the other thread to finish pushing (because it holds the lock on needsHelp we can only acquire the lock after it releases the lock)
							synchronized (needsHelp) {
								
							}
						}
						else
						{//If we get here, out stack is empty, but mainStack has a location for us
							working.set(true);//Tell the manager that we aren't working since we have to wait for another thread to assign us a location
							synchronized (mainStack)//Get the lock on mainStack to we can pop
							{
								if (mainStack.isEmpty()) continue;//if another thread is sneaky, and pops the last location off the stack while we are acquiring the lock give up and try again
								
								int y = mainStack.pop();// Transfer the location to our stack
								int x = mainStack.pop();
								personalStack.push(x, y);
							}
						}
					}
					else// We have a location that we can visit 
					{
						working.set(true);// Tell the manager thread that we are working
						idle();// Sleep based on the slow down slider
						int y = personalStack.pop();//Grab the location that we are working on
						int x = personalStack.pop();
						byte tileId = maze.getTileId(x, y);
						if (tileId != Maze.PATH && tileId != Maze.START) continue;//If another thread modified the location while it was sitting in our stack,
						//Give up since the other thread would have already explored surrounding tiles when it marked the location as visited

						maze.setTileId(x, y, (byte) color);//Set the tile to be our color

						evalCount = 0;
						evaluate(x - 1, y);//Look at surrounding nodes
						evaluate(x, y + 1);
						evaluate(x + 1, y);
						evaluate(x, y - 1);

					}
				}
				working.set(false);//Tell the manager that we aren't working
				System.out.println("thread " + id + " ending");
			}
			catch (StoppedByUserException e)
			{
				//returns
			}
		}

		/**
		 * @param x
		 * @param y
		 */
		private void evaluate(int x, int y)
		{
			if (x == maze.endX && y == maze.endY)
			{
				done.set(true);
				System.out.println("DONE! ");
			}
			if (maze.getTileId(x, y) == Maze.PATH)
			{
				if ((evalCount >= 1 && needsHelp.get() > 0) || evalCount == 2)
				{
					synchronized (needsHelp)
					{
						needsHelp.set(Math.max(0, needsHelp.get() - 1));
						synchronized (mainStack)
						{
							mainStack.push(x, y);
						}
					}
				}
				else
				{
					personalStack.push(x, y);
				}
				evalCount++;
			}
		}

	}

	private class WorkerThread extends Thread
	{
		private Worker worker;

		public WorkerThread(Worker worker)
		{
			super(worker);
			this.worker = worker;
		}

	}

}

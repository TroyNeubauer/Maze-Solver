package com.troy.compsci.maze.graphics;

/**
 * Thrown when the user cancels something so that we can get to the cahch block easily
 *
 */
public class StoppedByUserException extends RuntimeException
{


	public StoppedByUserException()
	{
		super();
	}

	public StoppedByUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StoppedByUserException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StoppedByUserException(String message)
	{
		super(message);
	}

	public StoppedByUserException(Throwable cause)
	{
		super(cause);
	}

}

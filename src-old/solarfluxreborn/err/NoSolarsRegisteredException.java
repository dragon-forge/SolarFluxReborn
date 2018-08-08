package com.zeitheron.solarfluxreborn.err;

public class NoSolarsRegisteredException extends RuntimeException
{
	public NoSolarsRegisteredException(String cause, boolean THROW)
	{
		super(cause);
		if(THROW)
			throw this;
	}
	
	public NoSolarsRegisteredException(String cause)
	{
		super(cause);
		throw this;
	}
}
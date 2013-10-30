/*
 * File: CreateLog4jEvents.java
 * Project: com.hudren.woodpile
 * Created: Oct 21, 2013
 *
 * Copyright (c) 2013 Hudren Andromeda Connection. All rights reserved.
 */

package com.hudren.woodpile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * TODO Type description
 * 
 * @author jeff
 */
public class CreateLog4j2Events
{

	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 * TODO Method description for <code>main()</code>
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{
		ThreadContext.put( "application", "server1" );

		LOGGER.trace( "This is a trace message" );
		LOGGER.debug( "This is a debug message" );
		LOGGER.info( "This is an informative message from server1" );

		throwIt();

		// Give logger chance to send messages
		try
		{
			Thread.sleep( 100 );
		}
		catch ( InterruptedException e )
		{
		}
	}

	private static void throwIt()
	{
		try
		{
			throw new IllegalStateException( "This is a bad state" );
		}
		catch ( IllegalStateException e )
		{
			LOGGER.error( "Caught a bad state", e );
		}
	}

}

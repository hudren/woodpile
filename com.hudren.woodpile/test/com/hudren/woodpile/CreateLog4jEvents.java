/*
 * File: CreateLog4jEvents.java
 * Project: com.hudren.woodpile
 * Created: Oct 21, 2013
 *
 * Copyright (c) 2013-2017 Alphalon, LLC. All rights reserved.
 */

package com.hudren.woodpile;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * TODO Type description
 * 
 * @author jeff
 */
public class CreateLog4jEvents
{

	public static final Logger LOGGER = Logger.getLogger( CreateLog4jEvents.class );

	/**
	 * TODO Method description for <code>main()</code>
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{
		LOGGER.trace( "This is a trace message" );
		LOGGER.debug( "This is a debug message" );

		MDC.put( "server", "server1" );
		LOGGER.info( "This is an informative message from server1" );
		MDC.remove( "server" );

		LOGGER.info( "This is an informative message from application1" );

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

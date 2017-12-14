/*
 * File: CreateLogbackEvents.java
 * Project: com.hudren.woodpile
 * Created: Dec 14, 2017
 *
 * Copyright (c) 2017 Hudren Andromeda Connection. All rights reserved.
 */

package com.hudren.woodpile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Generates Logback events to test Woodpile.
 * 
 * @author jeff
 */
public class CreateLogbackEvents
{

	public static final Logger LOGGER = LoggerFactory.getLogger( CreateLogbackEvents.class );

	public static void main( String[] args )
	{
		MDC.put( "application", "hello" );
		MDC.put( "server", "server2" );

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

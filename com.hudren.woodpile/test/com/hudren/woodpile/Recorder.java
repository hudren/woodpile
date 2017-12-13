/*
 * Project: com.hudren.woodpile
 * File:    Recorder.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 21, 2006
 *
 * Copyright (c) 2006-2017 Alphalon, LLC. All rights reserved. 
 * 
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * 
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * 
 * You must not remove this notice, or any other, from this software.
 */

package com.hudren.woodpile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.SourceListener;
import com.hudren.woodpile.net.Receiver;

/**
 * TODO Recorder description
 * 
 * @author Jeff Hudren
 */
public class Recorder
	implements SourceListener
{

	/**
	 * The port used to capture and playback serialized objects.
	 */
	private static final int PORT = 4560;

	/**
	 * The port used to capture and playback xml events.
	 */
	private static final int XML_PORT = 4567;

	/**
	 * The remote host.
	 */
	private static final String REMOTE_HOST = "localhost";

	private final File file = new File( "test/com/hudren/woodpile/events.serial" );

	private List<LogEvent> events = new ArrayList<LogEvent>();

	/**
	 * TODO main description
	 * 
	 * @param args
	 */
	public static void main( final String[] args )
	{
		final Recorder recorder = new Recorder();

		try
		{
			if ( args.length == 1 )
			{
				if ( "capture".equals( args[ 0 ] ) )
				{
					recorder.capture();
				}
				else if ( "playback".equals( args[ 0 ] ) )
				{
					recorder.playback();
				}
				else
				{
					System.err.println( "Unknown arguments!" );
				}
			}
			else
			{
				System.err.println( "Missing arguments!" );
			}
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			LogManager.shutdown();
		}

	}

	private void capture() throws IOException
	{
		System.out.println( "Starting capture..." );

		final Receiver receiver = new Receiver( PORT, XML_PORT, this );
		receiver.start();

		// Wait until receiver closes
		synchronized ( this )
		{
			try
			{
				wait();
			}
			catch ( final InterruptedException e )
			{
				e.printStackTrace();
			}
		}

		// Write events to file
		final ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file ) );
		oos.writeObject( events );
		oos.close();

		System.out.println( "Captured " + events.size() + " events." );
	}

	@SuppressWarnings( "unchecked" )
	private void playback() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		System.out.println( "Starting playback..." );

		// Write events to file
		final ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
		events = (ArrayList<LogEvent>) ois.readObject();
		ois.close();

		final Logger logger = Logger.getLogger( Recorder.class );
		final Appender appender = new SocketAppender( REMOTE_HOST, PORT );
		logger.addAppender( appender );

		for ( final LogEvent event : events )
		{
			final LoggingEvent logEvent =
					new LoggingEvent( event.getLoggerName(), Logger.getLogger( event.getLoggerName() ), event.getTimeStamp(),
							event.getLevel(), event.getRenderedMessage(), null );

			logger.callAppenders( logEvent );
		}

		System.out.println( "Played back " + events.size() + " events." );
	}

	/**
	 * @see com.hudren.woodpile.model.SourceListener#addEvents(java.util.List)
	 */
	@Override
	public void addEvents( final List<LogEvent> events )
	{
		this.events.addAll( events );
	}

	/**
	 * @see com.hudren.woodpile.model.SourceListener#receiverClosed()
	 */
	@Override
	public void receiverClosed()
	{
		synchronized ( this )
		{
			notifyAll();
		}
	}

}

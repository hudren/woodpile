/*
 * File: ObjectReader.java
 * Project: com.hudren.woodpile
 * Created: Nov 3, 2013
 *
 * Copyright (c) 2013 Hudren Andromeda Connection. All rights reserved.
 */

package com.hudren.woodpile.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.log4j.spi.LoggingEvent;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO Type description
 *
 * @author jeff
 */
class SerializedLayoutReader
	extends AbstractReader
{

	public SerializedLayoutReader( final ReceiverManager manager, final Socket client, BlockingQueue<LogEvent> queue )
	{
		super( manager, client, queue );
	}

	@Override
	public void run()
	{
		try
		{
			final ObjectInputStream is = new ObjectInputStream( new BufferedInputStream( client.getInputStream() ) );
			final String host = client.getInetAddress().getHostName();

			LogEvent event;
			while ( !done )
			{
				event = null;

				try
				{
					// Read event from log
					final Object someEvent = is.readObject();
					if ( someEvent instanceof LoggingEvent )
						event = new LogEvent( host, (LoggingEvent) someEvent );

					else if ( someEvent instanceof org.apache.logging.log4j.core.LogEvent )
						event = new LogEvent( host, (org.apache.logging.log4j.core.LogEvent) someEvent );

					else if ( someEvent instanceof LogEvent )
						event = (LogEvent) someEvent;

					else if ( someEvent instanceof ILoggingEvent )
						event = new LogEvent( host, (ILoggingEvent) someEvent);

					// Queue known events
					if ( event != null )
						queue.add( event );
				}
				catch ( final ClassNotFoundException e )
				{
					e.printStackTrace();
				}
			}
		}
		catch ( final EOFException e )
		{
			System.out.println( "Client closed socket" );
		}
		catch ( final SocketException e )
		{
			e.printStackTrace();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}

		// Close connection
		try
		{
			client.close();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}

		manager.connectionClosed( this );
	}

}

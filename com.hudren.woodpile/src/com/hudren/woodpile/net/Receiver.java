/*
 * Project: com.hudren.woodpile
 * File:    Receiver.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
 *
 * Copyright (c) 2006-2013 Hudren Andromeda Connection. All rights reserved. 
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

package com.hudren.woodpile.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.spi.LoggingEvent;

import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.SourceListener;

/**
 * TODO Receiver description
 * 
 * @author Jeff Hudren
 */
public class Receiver
	extends Thread
{

	private ServerSocket server;

	private final List<SourceListener> listeners = new ArrayList<SourceListener>();

	private final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<LogEvent>();

	private volatile boolean done;

	private class Firer
		implements Runnable
	{

		@Override
		public void run()
		{
			while ( !done )
			{
				final List<LogEvent> events = new ArrayList<LogEvent>();

				try
				{
					final LogEvent event = queue.poll( 10, TimeUnit.SECONDS );
					if ( event != null )
					{
						events.add( event );
						queue.drainTo( events );

						if ( events.size() > 0 )
						{
							// Notify all listeners of received event
							synchronized ( listeners )
							{
								for ( final SourceListener listener : listeners )
								{
									listener.addEvents( events );
								}
							}
						}
					}
				}
				catch ( final InterruptedException e )
				{
					// Consume
				}
			}
		}
	};

	private class Reader
		implements Runnable
	{

		private final Socket client;

		Reader( final Socket client )
		{
			this.client = client;
		}

		@Override
		public void run()
		{
			try
			{
				final ObjectInputStream ois = new ObjectInputStream( client.getInputStream() );
				final String host = client.getInetAddress().getHostName();

				while ( !done )
				{
					LogEvent event = null;

					try
					{
						// Read event from log
						final Object someEvent = ois.readObject();
						if ( someEvent instanceof LoggingEvent )
							event = new LogEvent( host, (LoggingEvent) someEvent );

						else if ( someEvent instanceof org.apache.logging.log4j.core.LogEvent )
							event = new LogEvent( host, (org.apache.logging.log4j.core.LogEvent) someEvent );

						else if ( someEvent instanceof LogEvent )
							event = (LogEvent) someEvent;

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
				// e.printStackTrace();
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

			// Wait until queue is empty
			while ( !queue.isEmpty() )
			{
				try
				{
					Thread.sleep( 100 );
				}
				catch ( final InterruptedException e )
				{
					// consume
				}
			}

			// Notify listeners that connection closed
			synchronized ( listeners )
			{
				for ( final SourceListener listener : listeners )
				{
					listener.receiverClosed();
				}
			}
		}
	}

	public Receiver( final int port ) throws IOException
	{
		super( "Woodpile Receiver" );

		System.out.println( "Listening on port " + port );
		server = new ServerSocket( port );

		setDaemon( true );
	}

	public Receiver( final int port, final SourceListener listener ) throws IOException
	{
		this( port );

		listeners.add( listener );
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		final Thread firer = new Thread( new Firer(), "Woodpile Firer" );
		firer.setDaemon( true );
		firer.start();

		try
		{
			while ( !done )
			{
				final Socket client = server.accept();

				System.out.println( "Accepting new client: " + client.toString() );
				final Thread thread = new Thread( new Reader( client ), "Woodpile Reader" );
				thread.setDaemon( true );
				thread.start();
			}
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}

	public void addListener( final SourceListener listener )
	{
		synchronized ( listeners )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( final SourceListener listener )
	{
		synchronized ( listeners )
		{
			listeners.remove( listener );
		}
	}

	public void complete()
	{
		done = true;
	}

}

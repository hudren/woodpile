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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.SourceListener;

/**
 * TODO Receiver description
 * 
 * @author Jeff Hudren
 */
public class Receiver
	extends Thread
	implements ReceiverManager
{

	private final List<SourceListener> listeners = new ArrayList<SourceListener>();

	private final BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<LogEvent>();

	private volatile boolean done;

	private ObjectServer server;

	private XmlServer xmlServer;

	public Receiver( final int port, final int xmlPort ) throws IOException
	{
		super( "Woodpile Receiver" );

		server = new ObjectServer( this, queue, port );
		xmlServer = new XmlServer( this, queue, xmlPort );
	}

	public Receiver( final int port, final int xmlPort, final SourceListener listener ) throws IOException
	{
		this( port, xmlPort );

		listeners.add( listener );
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		while ( !done )
		{
			final ArrayList<LogEvent> events = new ArrayList<LogEvent>();

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

	public void complete()
	{
		done = true;
		interrupt();

		server.shutdown();
		xmlServer.shutdown();
	}

	@Override
	public void connectionClosed( AbstractReader reader )
	{
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

}

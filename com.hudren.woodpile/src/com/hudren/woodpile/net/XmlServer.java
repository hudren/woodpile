/*
 * File: XmlServer.java
 * Project: com.hudren.woodpile
 * Created: Nov 3, 2013
 *
 * Copyright (c) 2013-2017 Alphalon, LLC. All rights reserved.
 */

package com.hudren.woodpile.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO Type description
 * 
 * @author jeff
 */
public class XmlServer
	extends AbstractServer
{

	private ServerSocket server;

	public XmlServer( Receiver receiver, BlockingQueue<LogEvent> queue, int port ) throws IOException
	{
		super( receiver, queue );

		System.out.println( "Listening for xml on port " + port );
		server = new ServerSocket( port );

		setDaemon( true );
		start();
	}

	@Override
	public void run()
	{
		try
		{
			while ( !done )
			{
				final Socket client = server.accept();

				AbstractReader reader = new XMLLayoutReader( this, client, queue );
				readers.add( reader );

				System.out.println( "Accepting new client: " + client.toString() );
				final Thread thread = new Thread( reader, "Woodpile XML Reader" );
				thread.setDaemon( true );
				thread.start();
			}
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
	}
}

/*
 * File: AbstractServer.java
 * Project: com.hudren.woodpile
 * Created: Nov 3, 2013
 *
 * Copyright (c) 2013-2017 Alphalon, LLC. All rights reserved.
 */

package com.hudren.woodpile.net;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO Type description
 * 
 * @author jeff
 */
abstract class AbstractServer
	extends Thread
	implements ReceiverManager
{

	private Receiver receiver;

	protected BlockingQueue<LogEvent> queue;

	protected final HashSet<AbstractReader> readers = new HashSet<AbstractReader>();

	protected transient boolean done;

	public AbstractServer( Receiver receiver, BlockingQueue<LogEvent> queue )
	{
		this.receiver = receiver;
		this.queue = queue;
	}

	public void shutdown()
	{
		done = true;

		// Shutdown any threads
		for ( AbstractReader reader : readers )
			reader.shutdown();
	}

	@Override
	public void connectionClosed( AbstractReader reader )
	{
		readers.remove( reader );

		receiver.connectionClosed( reader );
	}

}

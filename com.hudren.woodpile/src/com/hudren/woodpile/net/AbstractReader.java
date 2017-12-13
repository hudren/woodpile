/*
 * File: AbstractReader.java
 * Project: com.hudren.woodpile
 * Created: Nov 3, 2013
 *
 * Copyright (c) 2013-2017 Alphalon, LLC. All rights reserved.
 */

package com.hudren.woodpile.net;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO Type description
 * 
 * @author jeff
 */
abstract class AbstractReader
	implements Runnable
{

	protected final ReceiverManager manager;

	protected final Socket client;

	protected final BlockingQueue<LogEvent> queue;

	protected transient boolean done;

	public AbstractReader( ReceiverManager manager, Socket client, BlockingQueue<LogEvent> queue )
	{
		this.manager = manager;
		this.client = client;
		this.queue = queue;
	}

	public void shutdown()
	{
		done = true;
	}
}

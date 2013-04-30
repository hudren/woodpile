/*
 * Project: com.hudren.woodpile
 * File:    Source.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.io.IOException;

import com.hudren.woodpile.net.Receiver;

/**
 * TODO Source description
 * 
 * @author Jeff Hudren
 */
public class Source
{

	private Receiver receiver;

	public Source( final int port ) throws IOException
	{
		this.receiver = new Receiver( port );
	}

	public void start()
	{
		receiver.start();
	}

	public void stop()
	{
		receiver.complete();
	}

	public void addListener( final SourceListener listener )
	{
		receiver.addListener( listener );
	}

	public void removeListener( final SourceListener listener )
	{
		receiver.removeListener( listener );
	}

}

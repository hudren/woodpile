/*
 * Project: com.hudren.woodpile
 * File:    Source.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
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

	public Source( final int port, final int xmlPort ) throws IOException
	{
		this.receiver = new Receiver( port, xmlPort );
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

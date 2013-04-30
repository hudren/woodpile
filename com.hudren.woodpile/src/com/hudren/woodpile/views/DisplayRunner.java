/*
 * Project: com.hudren.woodpile
 * File:    DisplayRunner.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.views;

import org.eclipse.swt.widgets.Display;

/**
 * TODO DisplayRunner description
 * 
 * @author Jeff Hudren
 */
public abstract class DisplayRunner
{

	public abstract void run();

	public void start()
	{

		if ( Display.getCurrent() != null )
		{
			run();
		}
		else
		{
			Display.getDefault().asyncExec( new Runnable()
			{

				public void run()
				{
					DisplayRunner.this.run();
				}
			} );
		}
	}

}

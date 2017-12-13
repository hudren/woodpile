/*
 * Project: com.hudren.woodpile
 * File:    ZoomFilter.java
 *
 * Author:  Jeff Hudren
 * Created: Jun 12, 2006
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

package com.hudren.woodpile.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO ZoomFilter description
 * 
 * @author Jeff Hudren
 */
public class ZoomFilter
	extends ViewerFilter
{

	private String loggerName;

	/**
     * 
     */
	public ZoomFilter()
	{
		super();
	}

	/**
	 * @param loggerName
	 */
	public ZoomFilter( final String loggerName )
	{
		super();

		this.loggerName = loggerName;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( final Viewer viewer, final Object parentElement, final Object element )
	{
		if ( loggerName != null && element instanceof LogEvent )
		{
			if ( ! ( (LogEvent) element ).getLoggerName().startsWith( loggerName ) )
				return false;
		}

		return true;
	}

	/**
	 * Getter for loggerName
	 * 
	 * @return loggerName
	 */
	public String getLoggerName()
	{
		return loggerName;
	}

	/**
	 * Setter for loggerName
	 * 
	 * @param loggerName loggerName
	 */
	public void setLoggerName( final String loggerName )
	{
		this.loggerName = loggerName;
	}

}

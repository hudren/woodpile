/*
 * Project: com.hudren.woodpile
 * File:    ZoomFilter.java
 *
 * Author:  Jeff Hudren
 * Created: Jun 12, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
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

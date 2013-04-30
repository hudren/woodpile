/*
 * Project: com.hudren.woodpile
 * File:    LevelFilter.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.views;

import org.apache.log4j.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO LevelFilter description
 * 
 * @author Jeff Hudren
 */
public class LevelFilter
	extends ViewerFilter
{

	private Level level;

	public LevelFilter( final Level level )
	{
		this.level = level;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( final Viewer viewer, final Object parentElement, final Object element )
	{
		if ( level != null && element instanceof LogEvent )
		{
			if ( ( (LogEvent) element ).getLevel().isGreaterOrEqual( level ) )
				return true;

			return false;
		}

		return true;
	}

	/**
	 * Getter for level
	 * 
	 * @return level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Setter for level
	 * 
	 * @param level level
	 */
	public void setLevel( final Level level )
	{
		this.level = level;
	}

}

/*
 * Project: com.hudren.woodpile
 * File:    LevelFilter.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
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

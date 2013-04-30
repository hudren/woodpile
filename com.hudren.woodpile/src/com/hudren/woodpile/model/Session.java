/*
 * Project: com.hudren.woodpile
 * File:    Session.java
 *
 * Author:  Jeff Hudren
 * Created: May 6, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.prefs.PreferenceConstants;

/**
 * This class represents a logging session.
 * 
 * @author Jeff Hudren
 */
public class Session
{

	private Log log;

	private int maximumEvents;

	private Level highestLevel = Level.ALL;

	private final List<LogEvent> events = new ArrayList<LogEvent>();

	private final List<SessionListener> listeners = new ArrayList<SessionListener>();

	private boolean active;

	private Date startTime;

	private transient int viewTopIndex;

	public Session( final Log log )
	{
		this.log = log;

		maximumEvents = WoodpilePlugin.getDefault().getPluginPreferences().getInt( PreferenceConstants.MAX_EVENTS );
		startTime = new Date();
	}

	public Object[] getEvents()
	{
		return events.toArray();
	}

	private List<LogEvent> filterEvents( final List<LogEvent> events )
	{
		final List<LogEvent> added = new ArrayList<LogEvent>();

		for ( final LogEvent event : events )
		{
			if ( getCategories().getContentSelection( event ) != ContentSelection.EXCLUDE )
				added.add( event );
		}

		return added;
	}

	/**
	 * TODO addEvents description
	 */
	public void addEvents( final List<LogEvent> newEvents )
	{
		final List<LogEvent> added = filterEvents( newEvents );
		if ( added.size() > 0 )
		{
			final List<LogEvent> removed = null;

			synchronized ( events )
			{
				// TODO implement this in a better way!
				// if ( maximumEvents > 0 && events.size() >= maximumEvents )
				// {
				// removed = new ArrayList<LogEvent>();
				//
				// while ( events.size() >= maximumEvents )
				// {
				// removed.add( events.remove( 0 ) );
				// }
				// }

				events.addAll( added );
				getCategories().addAll( added );

				// TODO implement this in a better way!
				if ( maximumEvents > 0 && events.size() >= maximumEvents )
					setActive( false );

				// Check for highest level
				boolean levelChanged = false;
				for ( final LogEvent event : added )
				{
					if ( event.getLevel().toInt() > highestLevel.toInt() )
					{
						highestLevel = event.getLevel();
						levelChanged = true;
					}
				}

				if ( levelChanged )
					fireSessionChanged();
			}

			fireEventsChanged( removed, added );
		}
	}

	/**
	 * Getter for log
	 * 
	 * @return log
	 */
	public Log getLog()
	{
		return log;
	}

	/**
	 * Getter for highestLevel
	 * 
	 * @return highestLevel
	 */
	public Level getHighestLevel()
	{
		return highestLevel;
	}

	/**
	 * Getter for tree
	 * 
	 * @return tree
	 */
	public CategoryTree getCategories()
	{
		return getLog().getCategories();
	}

	public void deleteAll()
	{
		try
		{
			synchronized ( events )
			{
				events.clear();
			}
		}
		finally
		{
			fireLogCleared();
		}
	}

	public void restart()
	{
		if ( events.size() > 0 )
			log.newActiveSession( this );
	}

	public void addListener( final SessionListener listener )
	{
		synchronized ( listeners )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( final SessionListener listener )
	{
		synchronized ( listeners )
		{
			listeners.remove( listener );
		}
	}

	private void fireEventsChanged( final List<LogEvent> removed, final List<LogEvent> added )
	{
		synchronized ( listeners )
		{
			for ( final SessionListener listener : listeners )
			{
				listener.eventsChanged( this, removed, added );
			}
		}
	}

	private void fireLogCleared()
	{
		synchronized ( listeners )
		{
			for ( final SessionListener listener : listeners )
			{
				listener.sessionCleared( this );
			}
		}
	}

	private void fireSessionChanged()
	{
		synchronized ( listeners )
		{
			for ( final SessionListener listener : listeners )
			{
				listener.sessionChanged( this );
			}
		}
	}

	/**
	 * Getter for active
	 * 
	 * @return active
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 * Setter for active
	 * 
	 * @param active active
	 */
	public void setActive( final boolean active )
	{
		if ( this.active != active )
		{
			this.active = active;

			fireSessionChanged();
		}
	}

	/**
	 * Getter for startTime
	 * 
	 * @return startTime
	 */
	public Date getStartTime()
	{
		return startTime;
	}

	/**
	 * Setter for startTime
	 * 
	 * @param startTime startTime
	 */
	void setStartTime( final Date startTime )
	{
		this.startTime = startTime;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Session: " + DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( startTime );
	}

	/**
	 * Getter for viewTopIndex
	 * 
	 * @return viewTopIndex
	 */
	public int getViewTopIndex()
	{
		return viewTopIndex;
	}

	/**
	 * Setter for viewTopIndex
	 * 
	 * @param viewTopIndex viewTopIndex
	 */
	public void setViewTopIndex( final int viewTopIndex )
	{
		this.viewTopIndex = viewTopIndex;
	}

}

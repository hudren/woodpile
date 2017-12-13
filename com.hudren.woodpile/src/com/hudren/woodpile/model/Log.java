/*
 * Project: com.hudren.woodpile
 * File:    Log.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO Log description
 * 
 * @author Jeff Hudren
 */
public class Log
	implements SourceListener, Comparable<Log>
{

	private String name;

	// private File file;

	private boolean active;

	private final CategoryTree categories = new CategoryTree();

	private Session activeSession;

	private final List<Session> sessions = new ArrayList<Session>();

	private final List<LogListener> listeners = new ArrayList<LogListener>();

	/**
     * 
     */
	public Log()
	{
		this( false );
	}

	/**
     * 
     */
	public Log( final boolean start )
	{
		this( "Log", start );
	}

	public Log( final String name, final boolean start )
	{
		super();

		this.active = true;
		this.name = name;

		if ( start )
			rollLog();
	}

	public Session newActiveSession( final Session oldSession )
	{
		if ( oldSession != null )
			oldSession.setActive( false );

		if ( activeSession != null )
			activeSession.setActive( false );

		activeSession = new Session( this );
		activeSession.setActive( true );

		sessions.add( activeSession );
		for ( final LogListener listener : listeners )
			listener.sessionAdded( activeSession, oldSession );

		return activeSession;
	}

	/**
	 * Rolls the log by creating a new session.
	 */
	public Session rollLog()
	{
		return newActiveSession( activeSession );
	}

	public void addSession( final Session session )
	{
		sessions.add( session );

		for ( final LogListener listener : listeners )
		{
			listener.sessionAdded( session, null );
		}
	}

	public void removeSession( final Session session )
	{
		if ( sessions.remove( session ) )
		{
			for ( final LogListener listener : listeners )
			{
				listener.sessionRemoved( session );
			}
		}
	}

	public void addListener( final LogListener listener )
	{
		synchronized ( listeners )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( final LogListener listener )
	{
		synchronized ( listeners )
		{
			listeners.remove( listener );
		}
	}

	public Session getActiveSession()
	{
		if ( activeSession == null )
			rollLog();

		return activeSession;
	}

	public CategoryTree getCategories()
	{
		return categories;
	}

	public List<Session> getSessions()
	{
		return sessions;
	}

	/**
	 * @see com.hudren.woodpile.model.SourceListener#addEvents(java.util.List)
	 */
	@Override
	public void addEvents( final List<LogEvent> events )
	{
		if ( activeSession != null )
		{
			if ( !activeSession.isActive() )
				rollLog();

			activeSession.addEvents( events );
		}
	}

	/**
	 * @see com.hudren.woodpile.model.SourceListener#receiverClosed()
	 */
	@Override
	public void receiverClosed()
	{
		if ( activeSession != null )
			activeSession.setActive( false );
	}

	/**
	 * Getter for name
	 * 
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Setter for name
	 * 
	 * @param name name
	 */
	public void setName( final String name )
	{
		this.name = name;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( final Log o )
	{
		return name.compareTo( o.getName() );
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
		this.active = active;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Log: " + getName();
	}

	/**
	 * TODO deleteInactiveSessions description
	 */
	public void deleteInactiveSessions()
	{
		final Iterator<Session> it = sessions.iterator();
		while ( it.hasNext() )
		{
			final Session session = it.next();

			if ( !session.isActive() )
			{
				it.remove();

				session.deleteAll();

				for ( final LogListener listener : listeners )
				{
					listener.sessionRemoved( session );
				}
			}
		}
	}

	/**
	 * TODO deleteSession description
	 * 
	 * @param session
	 */
	public void deleteSession( final Session session )
	{
		sessions.remove( session );
		session.deleteAll();

		for ( final LogListener listener : listeners )
		{
			listener.sessionRemoved( session );
		}
	}

}

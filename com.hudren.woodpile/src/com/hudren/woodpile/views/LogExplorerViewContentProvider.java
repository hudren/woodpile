/*
 * Project: com.hudren.woodpile
 * File:    LogExplorerViewContentProvider.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
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

import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.LogListener;
import com.hudren.woodpile.model.Session;
import com.hudren.woodpile.model.SessionListener;

/**
 * TODO LogExplorerViewContentProvider description
 * 
 * @author Jeff Hudren
 */
public class LogExplorerViewContentProvider
	implements ITreeContentProvider, LogListener, SessionListener
{

	private LogExplorerView view;

	private TreeViewer viewer;

	private SortedSet<Log> logs;

	/**
     * 
     */
	public LogExplorerViewContentProvider()
	{
		super();
	}

	/**
	 * @param view
	 */
	public LogExplorerViewContentProvider( final LogExplorerView view )
	{
		super();

		this.view = view;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren( final Object parentElement )
	{
		if ( parentElement instanceof Log )
		{
			return ( (Log) parentElement ).getSessions().toArray();
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent( final Object element )
	{
		if ( element instanceof Session )
		{
			return ( (Session) element ).getLog();
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren( final Object element )
	{
		if ( element instanceof Log )
		{
			return ( (Log) element ).getSessions().size() > 0;
		}

		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements( final Object inputElement )
	{
		if ( inputElement instanceof SortedSet )
		{
			return ( (SortedSet<?>) inputElement ).toArray();
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput )
	{
		this.viewer = (TreeViewer) viewer;

		if ( oldInput == null || ! ( newInput instanceof SortedSet ) )
		{
			if ( logs != null )
			{
				for ( final Log log : logs )
				{
					log.removeListener( this );

					for ( final Session session : log.getSessions() )
					{
						session.removeListener( this );
					}
				}

				logs = null;
			}

			if ( newInput instanceof SortedSet )
			{
				logs = (SortedSet<Log>) newInput;

				for ( final Log log : logs )
				{
					log.addListener( this );

					for ( final Session session : log.getSessions() )
					{
						if ( session.isActive() )
							session.addListener( this );
					}
				}
			}
		}
	}

	/**
	 * @see com.hudren.woodpile.model.LogListener#sessionAdded(com.hudren.woodpile.model.Session,
	 *      com.hudren.woodpile.model.Session)
	 */
	@Override
	public void sessionAdded( final Session session, final Session deactivated )
	{
		new DisplayRunner()
		{

			@Override
			public void run()
			{
				final Object selection = viewer.getSelection();

				session.addListener( LogExplorerViewContentProvider.this );
				viewer.refresh();

				if ( selection instanceof TreeSelection && session != null )
				{
					final Object obj = ( (TreeSelection) selection ).getFirstElement();

					if ( obj == deactivated )
						view.setSelection( session );
				}
			}

		}.start();
	}

	/**
	 * @see com.hudren.woodpile.model.LogListener#sessionRemoved(com.hudren.woodpile.model.Session)
	 */
	@Override
	public void sessionRemoved( final Session session )
	{
		session.removeListener( this );

		new DisplayRunner()
		{

			@Override
			public void run()
			{
				viewer.refresh();
			}
		}.start();
	}

	/**
	 * @see com.hudren.woodpile.model.SessionListener#eventsChanged(com.hudren.woodpile.model.Session,
	 *      java.util.List, java.util.List)
	 */
	@Override
	public void eventsChanged( final Session session, final List<LogEvent> removed, final List<LogEvent> added )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.SessionListener#sessionCleared(com.hudren.woodpile.model.Session)
	 */
	@Override
	public void sessionCleared( final Session session )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.SessionListener#sessionChanged(com.hudren.woodpile.model.Session)
	 */
	@Override
	public void sessionChanged( final Session session )
	{
		new DisplayRunner()
		{

			@Override
			public void run()
			{
				viewer.refresh( session );
			}

		}.start();
	}

	/**
	 * TODO deleteInactiveSessions description
	 */
	public void deleteInactiveSessions()
	{
		for ( final Log log : logs )
		{
			log.deleteInactiveSessions();
		}
	}

	/**
	 * TODO deleteInactiveSession description
	 * 
	 * @param session
	 */
	public void deleteInactiveSession( final Session session )
	{
		session.getLog().deleteSession( session );
	}

}

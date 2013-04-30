/*
 * Project: com.hudren.woodpile
 * File:    SessionViewContentProvider.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.hudren.woodpile.model.Category;
import com.hudren.woodpile.model.CategoryTreeListener;
import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.LogListener;
import com.hudren.woodpile.model.Session;
import com.hudren.woodpile.model.SessionListener;

/**
 * TODO SessionViewContentProvider description
 * 
 * @author Jeff Hudren
 */
public class SessionViewContentProvider
	implements IStructuredContentProvider, LogListener, SessionListener, CategoryTreeListener
{

	private final SessionView view;

	private TableViewer viewer;

	private Session session;

	private final BlockingQueue<LogEvent> removedQueue = new LinkedBlockingQueue<LogEvent>();

	private final BlockingQueue<LogEvent> addedQueue = new LinkedBlockingQueue<LogEvent>();

	private volatile boolean updatingView;

	public SessionViewContentProvider( final SessionView view )
	{
		this.view = view;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements( final Object inputElement )
	{
		return session != null ? session.getEvents() : new Object[] {};
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged( final Viewer viewer, final Object oldInput, Object newInput )
	{
		this.viewer = (TableViewer) viewer;

		if ( newInput instanceof Log )
			newInput = ( (Log) newInput ).getActiveSession();

		if ( newInput instanceof Session )
		{
			if ( session != null )
			{
				session.getCategories().removeListener( this );
				session.getLog().removeListener( this );
				session.removeListener( this );
			}

			session = (Session) newInput;
			session.addListener( this );
			session.getLog().addListener( this );
			session.getCategories().addListener( this );
		}
	}

	/**
	 * TODO Method description for <code>eventsChanged()</code>
	 * 
	 * @param session
	 * @param removed
	 * @param added
	 * @see com.hudren.woodpile.model.SessionListener#eventsChanged(com.hudren.woodpile.model.Session,
	 *      java.util.List, java.util.List)
	 */
	public void eventsChanged( final Session session, final List<LogEvent> removed, final List<LogEvent> added )
	{
		if ( Display.getCurrent() != null )
			updateViewer( removed, added );
		else
			synchronized ( addedQueue )
			{
				if ( removed != null )
					removedQueue.addAll( removed );

				if ( added != null )
					addedQueue.addAll( added );

				if ( !updatingView )
				{
					updatingView = true;

					Display.getDefault().asyncExec( new Runnable()
					{

						public void run()
						{
							synchronized ( addedQueue )
							{
								final List<LogEvent> removed = new ArrayList<LogEvent>();
								final List<LogEvent> added = new ArrayList<LogEvent>();

								removedQueue.drainTo( removed );
								addedQueue.drainTo( added );

								updateViewer( removed, added );

								updatingView = false;
							}
						}
					} );
				}
			}
	}

	private void updateViewer( final List<LogEvent> removed, final List<LogEvent> added )
	{
		if ( removed != null )
			viewer.remove( removed.toArray() );

		viewer.add( added.toArray() );

		if ( view.isAutoScroll() )
			viewer.reveal( added.get( added.size() - 1 ) );

		( (IWorkbenchSiteProgressService) view.getSite().getAdapter( IWorkbenchSiteProgressService.class ) ).warnOfContentChange();

		if ( view.isAutoShow() )
			view.getSite().getPage().bringToTop( view );
	}

	/**
	 * TODO Method description for <code>sessionCleared()</code>
	 * 
	 * @param session
	 * @see com.hudren.woodpile.model.SessionListener#sessionCleared(com.hudren.woodpile.model.Session)
	 */
	public void sessionCleared( final Session session )
	{
		if ( session == this.session )
			new DisplayRunner()
			{

				@Override
				public void run()
				{
					clearViewer();
				}
			}.start();
	}

	/**
	 * @see com.hudren.woodpile.model.SessionListener#sessionChanged(com.hudren.woodpile.model.Session)
	 */
	public void sessionChanged( final Session session )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.LogListener#sessionAdded(com.hudren.woodpile.model.Session,
	 *      com.hudren.woodpile.model.Session)
	 */
	public void sessionAdded( final Session session, final Session deactivated )
	{
		if ( deactivated == this.session )
			new DisplayRunner()
			{

				@Override
				public void run()
				{
					viewer.setInput( session );
				}
			}.start();
	}

	/**
	 * @see com.hudren.woodpile.model.LogListener#sessionRemoved(com.hudren.woodpile.model.Session)
	 */
	public void sessionRemoved( final Session session )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#categoryFilterChanged(boolean)
	 */
	public void categoryFilterChanged( final boolean update )
	{
		view.categoryFilterChanged( update );

		if ( update )
			resetViewer();
		else
			refreshViewer();
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#nodeAdded(com.hudren.woodpile.model.Category,
	 *      com.hudren.woodpile.model.Category)
	 */
	public void nodeAdded( final Category parent, final Category child )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#nodeRemoved(com.hudren.woodpile.model.Category)
	 */
	public void nodeRemoved( final Category node )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#zoomFilterChanged(java.lang.String)
	 */
	public void zoomFilterChanged( final String name )
	{
		view.zoomFilterChanged( name );

		// TODO do this if filter results in only a few rows
		if ( name != null )
			resetViewer();
		else
			refreshViewer();
	}

	private void clearViewer()
	{
		viewer.setItemCount( 0 );
	}

	private void resetViewer()
	{
		viewer.setItemCount( 0 );
		viewer.add( session.getEvents() );
	}

	private void refreshViewer()
	{
		viewer.refresh();
	}

	/**
	 * Getter for session
	 * 
	 * @return session
	 */
	public Session getSession()
	{
		return session;
	}

}

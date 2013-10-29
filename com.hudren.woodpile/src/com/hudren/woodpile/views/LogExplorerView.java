/*
 * Project: com.hudren.woodpile
 * File:    LogExplorerView.java
 *
 * Author:  Jeff Hudren
 * Created: May 6, 2006
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.Session;

/**
 * TODO LogExplorerView description
 * 
 * @author Jeff Hudren
 */
public class LogExplorerView
	extends ViewPart
{

	public static final String ID = "com.hudren.woodpile.views.LogExplorerView";

	private TreeViewer viewer;

	private LogExplorerViewContentProvider contentProvider;

	private Action newLogAction;

	private Action deleteSessionAction;

	private Action deleteSessionsAction;

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl( final Composite parent )
	{
		createTreeViewer( parent );

		createActions();
		contributeToActionBars();

		getSite().setSelectionProvider( viewer );
	}

	private void createTreeViewer( final Composite parent )
	{
		viewer = new TreeViewer( parent, SWT.H_SCROLL | SWT.V_SCROLL );

		viewer.setContentProvider( contentProvider = new LogExplorerViewContentProvider( this ) );
		viewer.setLabelProvider( new LogExplorerViewLabelProvider( viewer ) );
		viewer.setInput( WoodpilePlugin.getDefault().getLogs() );
	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar( bars.getToolBarManager() );
	}

	private void fillLocalToolBar( final IToolBarManager manager )
	{
		// manager.add( newLogAction );

		// manager.add( new Separator() );
		manager.add( deleteSessionAction );
		manager.add( deleteSessionsAction );
	}

	private void createActions()
	{
		newLogAction = new Action( "New Log" )
		{

			@Override
			public void run()
			{
			}

		};
		newLogAction.setToolTipText( "Create a new log" );
		newLogAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/new_report.gif" ) );

		deleteSessionAction = new Action( "Delete" )
		{

			@Override
			public void run()
			{
				final ITreeSelection selection = (ITreeSelection) viewer.getSelection();

				if ( selection != null )
				{
					final Object obj = selection.getFirstElement();

					if ( obj instanceof Session )
						contentProvider.deleteInactiveSession( (Session) obj );
				}
			}

		};
		deleteSessionAction.setToolTipText( "Delete Inactive Session" );
		deleteSessionAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/participant_rem.gif" ) );

		deleteSessionsAction = new Action( "Delete All" )
		{

			@Override
			public void run()
			{
				Log log = null;
				final ITreeSelection selection = (ITreeSelection) viewer.getSelection();

				if ( selection != null )
				{
					final Object obj = selection.getFirstElement();

					if ( obj instanceof Session )
						log = ( (Session) obj ).getLog();
				}

				contentProvider.deleteInactiveSessions();

				if ( log != null )
				{
					final Session session = log.getActiveSession();
					if ( session != null )
						setSelection( session );
				}
			}

		};
		deleteSessionsAction.setToolTipText( "Delete All Inactive Sessions" );
		deleteSessionsAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/participant_remall.gif" ) );
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * TODO getCurrentLog description
	 */
	public Log getCurrentLog()
	{
		Object selection = viewer.getSelection();

		if ( selection instanceof Session )
			selection = ( (Session) selection ).getLog();

		if ( selection instanceof Log )
			return (Log) selection;

		return null;
	}

	public void setSelection( final Session session )
	{
		viewer.setSelection( new TreeSelection( new TreePath( new Object[] { session.getLog(), session } ) ), true );
	}

}

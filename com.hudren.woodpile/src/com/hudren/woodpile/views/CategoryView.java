/*
 * Project: com.hudren.woodpile
 * File:    CategoryView.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.model.Category;
import com.hudren.woodpile.model.CategoryTree;
import com.hudren.woodpile.model.ContentSelection;
import com.hudren.woodpile.model.FilterInstruction;
import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.VisibilitySelection;

/**
 * TODO CategoryView description
 * 
 * @author Jeff Hudren
 */
public class CategoryView
	extends ViewPart
	implements ISelectionChangedListener
{

	public static final String ID = "com.hudren.woodpile.views.CategoryView";

	private TreeViewer viewer;

	private CategoryViewContentProvider contentProvider;

	private Action showAllAction;
	private Action zoomAction;

	private Action resetIncludeAllAction;
	private Action resetShowAllAction;

	private Action includeAction;
	private Action excludeAction;

	private Action showAction;
	private Action hideAction;

	private Action expandAction;
	private Action collapseAllAction;

	private ISelectionListener pageSelectionListener;

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl( final Composite parent )
	{
		createTreeViewer( parent );
		createActions();

		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		hookPageSelection();
	}

	private void createTreeViewer( final Composite parent )
	{
		viewer = new TreeViewer( parent, SWT.H_SCROLL | SWT.V_SCROLL );

		viewer.setContentProvider( contentProvider = new CategoryViewContentProvider( this ) );
		viewer.setLabelProvider( new CategoryViewLabelProvider() );
		viewer.setInput( WoodpilePlugin.getDefault().getCurrentSession().getCategories() );

		viewer.addPostSelectionChangedListener( this );
	}

	private void hookPageSelection()
	{
		pageSelectionListener = new ISelectionListener()
		{

			@Override
			public void selectionChanged( final IWorkbenchPart part, final ISelection selection )
			{
				pageSelectionChanged( part, selection );
			}
		};

		getSite().getPage().addPostSelectionListener( pageSelectionListener );
	}

	/**
	 * TODO pageSelectionChanged description
	 * 
	 * @param part
	 * @param selection
	 */
	protected void pageSelectionChanged( final IWorkbenchPart part, final ISelection selection )
	{
		if ( part instanceof LogExplorerView )
		{
			final LogExplorerView explorer = (LogExplorerView) part;
			final Log log = explorer.getCurrentLog();

			if ( log != null )
				viewer.setInput( log.getCategories() );
		}
	}

	private Category getSelection()
	{
		Category node = null;

		final Object selection = ( (ITreeSelection) viewer.getSelection() ).getFirstElement();
		if ( selection instanceof Category )
			node = (Category) selection;

		return node;
	}

	private CategoryTree getTree()
	{
		return contentProvider.getTree();
	}

	/**
	 * TODO createActions description
	 */
	private void createActions()
	{
		showAllAction = new Action( "Show All", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				getTree().setShowAll( showAllAction.isChecked() );
			}

		};
		showAllAction.setToolTipText( "Show All" );
		showAllAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/dont_filter.gif" ) );

		zoomAction = new Action( "Zoom", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				if ( zoomAction.isChecked() )
				{
					final Category node = getSelection();

					if ( node != null )
						getTree().setZoomNode( node );
				}
				else
					getTree().setZoomNode( null );
			}

		};
		zoomAction.setToolTipText( "Zoom" );
		zoomAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/zoom.gif" ) );

		expandAction = new Action( "Expand", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				final Category node = getSelection();

				if ( node != null )
					viewer.expandToLevel( node, AbstractTreeViewer.ALL_LEVELS );
			}

		};
		expandAction.setToolTipText( "Expand" );
		expandAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/expand.gif" ) );

		collapseAllAction = new Action( "Collapse All", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				viewer.collapseAll();
			}

		};
		collapseAllAction.setToolTipText( "Collapse All" );
		collapseAllAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/collapse_all.gif" ) );

		resetIncludeAllAction = new Action( "Reset to Include All", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				getTree().resetIncludeAll();

				viewer.refresh( true );
			}

		};

		resetShowAllAction = new Action( "Reset to Show All", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				getTree().resetShowAll();

				viewer.refresh( true );
			}

		};

		includeAction = new Action( "Include", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Category node = getSelection();

				if ( node != null )
				{
					getTree().include( node );

					viewer.refresh( node );
				}
			}

		};

		excludeAction = new Action( "Exclude", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Category node = getSelection();

				if ( node != null )
				{
					getTree().exclude( node );

					viewer.refresh( node );
				}
			}

		};

		showAction = new Action( "Show", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Category node = getSelection();

				if ( node != null )
				{
					getTree().show( node );

					viewer.refresh( node );
				}
			}

		};

		hideAction = new Action( "Hide", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Category node = getSelection();

				if ( node != null )
				{
					getTree().hide( node );

					viewer.refresh( node );
				}
			}

		};
	}

	private void hookContextMenu()
	{
		final MenuManager menuMgr = new MenuManager( "#PopupMenu" );
		menuMgr.setRemoveAllWhenShown( true );
		menuMgr.addMenuListener( new IMenuListener()
		{

			@Override
			public void menuAboutToShow( final IMenuManager manager )
			{
				fillContextMenu( manager );
			}

		} );

		final Menu menu = menuMgr.createContextMenu( viewer.getControl() );
		viewer.getControl().setMenu( menu );
		getSite().registerContextMenu( menuMgr, viewer );
	}

	private void hookDoubleClickAction()
	{

	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown( bars.getMenuManager() );
		fillLocalToolBar( bars.getToolBarManager() );
	}

	private void fillLocalPullDown( final IMenuManager manager )
	{
		manager.add( resetIncludeAllAction );
		manager.add( resetShowAllAction );
	}

	private void fillContextMenu( final IMenuManager manager )
	{
		boolean isEmpty = viewer.getSelection().isEmpty();

		if ( !isEmpty )
		{
			final Category node = getSelection();

			manager.add( includeAction );
			manager.add( excludeAction );

			manager.add( new Separator() );
			manager.add( showAction );
			manager.add( hideAction );

			manager.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );

			final FilterInstruction instruction = node.getInstruction();
			if ( instruction != null )
			{
				final ContentSelection contentSelection = instruction.getContentSelection();
				includeAction.setChecked( contentSelection == ContentSelection.INCLUDE );
				excludeAction.setChecked( contentSelection == ContentSelection.EXCLUDE );

				final VisibilitySelection visibilitySelection = instruction.getVisibilitySelection();
				showAction.setChecked( visibilitySelection == VisibilitySelection.SHOW );
				hideAction.setChecked( visibilitySelection == VisibilitySelection.HIDE );
			}
			else
			{
				includeAction.setChecked( false );
				excludeAction.setChecked( false );

				showAction.setChecked( false );
				hideAction.setChecked( false );
			}
		}
	}

	private void fillLocalToolBar( final IToolBarManager manager )
	{
		manager.add( expandAction );
		manager.add( collapseAllAction );

		manager.add( new Separator() );
		manager.add( zoomAction );
		manager.add( showAllAction );
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged( final SelectionChangedEvent event )
	{
		if ( zoomAction.isChecked() )
		{
			final Category node = getSelection();

			if ( node != null )
				getTree().setZoomNode( node );
		}
	}

}

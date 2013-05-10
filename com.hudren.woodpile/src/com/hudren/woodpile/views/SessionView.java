/*
 * Project: com.hudren.woodpile
 * File:    SessionView.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.model.Session;

import static com.hudren.woodpile.prefs.PreferenceConstants.FIND_IGNORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.FIND_REGEX;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */
public class SessionView
	extends ViewPart
{

	public static final String ID = "com.hudren.woodpile.views.SessionView";

	private TableViewer viewer;

	private IMemento memento;

	private TableColumn timestampColumn;
	private TableColumn iconColumn;
	private TableColumn levelColumn;
	private TableColumn loggerColumn;
	private TableColumn messageColumn;
	private TableColumn throwableColumn;
	private TableColumn threadColumn;
	private TableColumn hostColumn;
	private TableColumn serverColumn;

	private final ColumnLayoutData columnLayouts[] = { new ColumnPixelData( 16, false, true ),
			new ColumnPixelData( 150, true, true ), new ColumnPixelData( 70, true, true ), new ColumnPixelData( 200, true, true ),
			new ColumnPixelData( 300, true, true ), new ColumnPixelData( 150, true, true ), new ColumnPixelData( 150, true, true ),
			new ColumnPixelData( 150, true, true ), new ColumnPixelData( 150, true, true ) };

	private static final String TAG_COLUMN = "column";
	private static final String TAG_NUMBER = "number";
	private static final String TAG_WIDTH = "width";
	private static final String TAG_FILTER_LEVEL = "filterLevel";
	private static final String TAG_AUTO_SHOW = "autoShow";
	private static final String TAG_SHOW_SEARCH = "showSearch";
	private static final String TAG_SEARCH = "search";
	private static final String TAG_TEXT = "text";

	private boolean autoScroll = true;
	private boolean autoShow;
	private boolean showSearch = true;

	private Action deleteLogAction;
	private Action scrollLockAction;
	private Action filterAction;
	private Action doubleClickAction;

	private Action fatalLevelAction;
	private Action errorLevelAction;
	private Action warningLevelAction;
	private Action infoLevelAction;
	private Action debugLevelAction;
	private Action traceLevelAction;
	private Action allLevelAction;
	private Action autoShowAction;
	private Action showSearchAction;
	private Action useRegexAction;
	private Action ignoreCaseAction;

	SessionViewContentProvider contentProvider;
	SessionViewLabelProvider labelProvider;
	CategoryFilter categoryFilter;
	ZoomFilter zoomFilter;
	LevelFilter levelFilter = new LevelFilter( Level.ALL );
	TextFilter textFilter;

	private Label searchTextLabel;
	private Combo searchTextCombo;
	private final int maxVisibleItemCount = 10;

	private Action searchAction;
	private Action clearSearchAction;
	private Action findNextAction;
	private Action findPreviousAction;

	private Map<Level, Action> filterLevelActions;

	private final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
	{

		@Override
		public void propertyChange( PropertyChangeEvent event )
		{
			if ( labelProvider != null )
			{
				if ( event.getProperty().startsWith( "color." ) )
				{
					labelProvider.createColors();

					SessionView.this.showBusy( true );
					viewer.refresh();
					SessionView.this.showBusy( false );
				}
			}
		}

	};

	private ISelectionListener pageSelectionListener;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl( final Composite parent )
	{
		createForm( parent );
		createActions();

		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		hookPageSelection();

		setFilterLevelMenuItemCheck( filterLevelActions.get( levelFilter.getLevel() ) );

		final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();
		prefs.addPropertyChangeListener( propertyChangeListener );
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		WoodpilePlugin.getDefault().getPluginPreferences().removePropertyChangeListener( propertyChangeListener );

		if ( labelProvider != null )
			labelProvider.dispose();

		super.dispose();
	}

	private void createForm( final Composite parent )
	{
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout( layout );

		searchTextLabel = new Label( parent, SWT.LEFT );
		searchTextLabel.setText( "Search:" );

		searchTextCombo = new Combo( parent, SWT.DROP_DOWN );
		searchTextCombo.setVisibleItemCount( 1 );
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		searchTextCombo.setLayoutData( gridData );

		if ( memento != null )
		{
			// restore search text
			final IMemento children[] = memento.getChildren( TAG_SEARCH );
			if ( children != null )
			{
				for ( final IMemento element : children )
				{
					final Integer val = element.getInteger( TAG_NUMBER );
					if ( val != null )
					{
						final int index = val.intValue();
						searchTextCombo.add( element.getString( TAG_TEXT ), index );
					}
				}

				searchTextCombo.setVisibleItemCount( Math.min( searchTextCombo.getItemCount(), maxVisibleItemCount ) );
			}
		}

		searchTextCombo.addModifyListener( new ModifyListener()
		{

			@Override
			public void modifyText( final ModifyEvent e )
			{
				enableActions();
			}

		} );

		searchTextCombo.addSelectionListener( new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected( final SelectionEvent e )
			{
				searchAction.run();
			}

			@Override
			public void widgetSelected( final SelectionEvent e )
			{
			}

		} );

		createTableViewer( parent );
		gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
		gridData.horizontalSpan = 2;
		viewer.getTable().setLayoutData( gridData );
	}

	private void createTableViewer( final Composite parent )
	{
		viewer = new TableViewer( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION );
		final Table table = viewer.getTable();
		final TableLayout layout = new TableLayout();
		table.setLayout( layout );

		if ( memento != null )
		{
			// restore columns width
			final IMemento children[] = memento.getChildren( TAG_COLUMN );
			if ( children != null )
			{
				for ( final IMemento element : children )
				{
					Integer val = element.getInteger( TAG_NUMBER );
					if ( val != null )
					{
						final int index = val.intValue();
						val = element.getInteger( TAG_WIDTH );
						if ( val != null )
						{
							columnLayouts[ index ] = new ColumnPixelData( val.intValue(), true );
						}
					}
				}
			}
		}

		int i = 0;
		iconColumn = new TableColumn( table, SWT.CENTER );

		iconColumn.pack();
		columnLayouts[ i ] = new ColumnPixelData( Math.max( 16, iconColumn.getWidth() ), false, true );

		iconColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		timestampColumn = new TableColumn( table, SWT.LEFT );
		timestampColumn.setText( "Time" );
		timestampColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		levelColumn = new TableColumn( table, SWT.LEFT );
		levelColumn.setText( "Level" );
		levelColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		loggerColumn = new TableColumn( table, SWT.LEFT );
		loggerColumn.setText( "Logger" );
		loggerColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		messageColumn = new TableColumn( table, SWT.LEFT );
		messageColumn.setText( "Message" );
		layout.addColumnData( columnLayouts[ i++ ] );

		throwableColumn = new TableColumn( table, SWT.LEFT );
		throwableColumn.setText( "Throwable" );
		throwableColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		threadColumn = new TableColumn( table, SWT.LEFT );
		threadColumn.setText( "Thread" );
		threadColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		hostColumn = new TableColumn( table, SWT.LEFT );
		hostColumn.setText( "Host" );
		hostColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		serverColumn = new TableColumn( table, SWT.LEFT );
		serverColumn.setText( "Server" );
		serverColumn.setResizable( columnLayouts[ i ].resizable );
		layout.addColumnData( columnLayouts[ i++ ] );

		table.setHeaderVisible( true );
		table.setLinesVisible( false );

		viewer.setContentProvider( contentProvider = new SessionViewContentProvider( this ) );
		viewer.setLabelProvider( labelProvider = new SessionViewLabelProvider() );
		viewer.addFilter( categoryFilter = new CategoryFilter( contentProvider ) );
		viewer.addFilter( zoomFilter = new ZoomFilter() );
		viewer.addFilter( levelFilter );
		viewer.setInput( WoodpilePlugin.getDefault().getCurrentSession() );

		getSite().setSelectionProvider( viewer );
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
				SessionView.this.fillContextMenu( manager );
			}

		} );

		final Menu menu = menuMgr.createContextMenu( viewer.getControl() );
		viewer.getControl().setMenu( menu );
		getSite().registerContextMenu( menuMgr, viewer );
	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown( bars.getMenuManager() );
		fillLocalToolBar( bars.getToolBarManager() );
	}

	private void fillLocalPullDown( final IMenuManager manager )
	{
		final String group = "Level";

		manager.add( new GroupMarker( group ) );
		manager.appendToGroup( group, fatalLevelAction );
		manager.appendToGroup( group, errorLevelAction );
		manager.appendToGroup( group, warningLevelAction );
		manager.appendToGroup( group, infoLevelAction );
		manager.appendToGroup( group, debugLevelAction );
		manager.appendToGroup( group, traceLevelAction );
		manager.appendToGroup( group, allLevelAction );

		manager.add( new Separator() );
		manager.add( showSearchAction );
		manager.add( useRegexAction );
		manager.add( ignoreCaseAction );

		manager.add( new Separator() );
		manager.add( autoShowAction );
	}

	private void fillContextMenu( final IMenuManager manager )
	{
	}

	private void fillLocalToolBar( final IToolBarManager manager )
	{
		final String group = "Search";

		manager.add( new GroupMarker( group ) );
		manager.appendToGroup( group, searchAction );
		manager.appendToGroup( group, clearSearchAction );
		manager.appendToGroup( group, findNextAction );
		manager.appendToGroup( group, findPreviousAction );
		manager.appendToGroup( group, new Separator() );

		manager.add( deleteLogAction );
		manager.add( scrollLockAction );
		manager.add( filterAction );
	}

	private void createActions()
	{
		final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();

		deleteLogAction = new Action( "Delete" )
		{

			@Override
			public void run()
			{
				WoodpilePlugin.getDefault().getCurrentSession().restart();
			}

		};
		deleteLogAction.setToolTipText( "Start New Session" );
		deleteLogAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/participant_rem.gif" ) );
		deleteLogAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/delete.gif" ) );

		scrollLockAction = new Action( "Scroll Lock", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				setAutoScroll( !isChecked() );
			}

		};
		scrollLockAction.setToolTipText( "Scroll Lock" );
		scrollLockAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/lock.gif" ) );

		filterAction = new Action( "Filter", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
			}

		};
		filterAction.setToolTipText( "Filter" );
		filterAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/filter_ps.gif" ) );
		filterAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/filter_ps.gif" ) );
		filterAction.setEnabled( false );

		doubleClickAction = new Action()
		{

			@Override
			public void run()
			{
				try
				{
					getSite().getPage().showView( LogEventView.ID );
				}
				catch ( final PartInitException e )
				{
					// Consume
				}
			}

		};

		filterLevelActions = new HashMap<Level, Action>();

		fatalLevelAction = new Action( "Fatal", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.FATAL );
			}

		};
		fatalLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/fatalerror_obj.gif" ) );
		filterLevelActions.put( Level.FATAL, fatalLevelAction );

		errorLevelAction = new Action( "Error", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.ERROR );
			}

		};
		errorLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/error_obj.gif" ) );
		filterLevelActions.put( Level.ERROR, errorLevelAction );

		warningLevelAction = new Action( "Warning", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.WARN );
			}

		};
		warningLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/warning_obj.gif" ) );
		filterLevelActions.put( Level.WARN, warningLevelAction );

		infoLevelAction = new Action( "Info", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.INFO );
			}

		};
		infoLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/information.gif" ) );
		filterLevelActions.put( Level.INFO, infoLevelAction );

		debugLevelAction = new Action( "Debug", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.DEBUG );
			}

		};
		debugLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/ldebug_obj.gif" ) );
		filterLevelActions.put( Level.DEBUG, debugLevelAction );

		traceLevelAction = new Action( "Trace", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.TRACE );
			}

		};
		traceLevelAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/trace.gif" ) );
		filterLevelActions.put( Level.TRACE, traceLevelAction );

		allLevelAction = new Action( "All", IAction.AS_RADIO_BUTTON )
		{

			@Override
			public void run()
			{
				setFilterLevel( Level.ALL );
			}

		};
		filterLevelActions.put( Level.ALL, allLevelAction );

		autoShowAction = new Action( "Show on new events", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				setAutoShow( !isAutoShow() );
			}

		};
		autoShowAction.setChecked( isAutoShow() );

		showSearchAction = new Action( "Show search filter", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				setShowSearch( !isShowSearch() );
			}

		};
		showSearchAction.setChecked( isShowSearch() );
		showSearchAction.setEnabled( false );

		useRegexAction = new Action( "Use regular expression", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();

				prefs.setValue( FIND_REGEX, !prefs.getBoolean( FIND_REGEX ) );
			}

		};
		useRegexAction.setChecked( prefs.getBoolean( FIND_REGEX ) );

		ignoreCaseAction = new Action( "Ignore case", IAction.AS_CHECK_BOX )
		{

			@Override
			public void run()
			{
				final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();

				prefs.setValue( FIND_IGNORE, !prefs.getBoolean( FIND_IGNORE ) );
			}

		};
		ignoreCaseAction.setChecked( prefs.getBoolean( FIND_IGNORE ) );

		searchAction = new Action( "Search", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				SessionView.this.showBusy( true );
				try
				{
					final String text = searchTextCombo.getText();
					if ( text != null && text.length() == 0 )
						resetFilterText();
					else
						setFilterText( text );
				}
				finally
				{
					SessionView.this.showBusy( false );
				}
			}

		};
		searchAction.setToolTipText( "Search" );
		searchAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/tsearch_obj.gif" ) );
		searchAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/tsearch_obj.gif" ) );

		clearSearchAction = new Action( "Clear Search", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				SessionView.this.showBusy( true );
				try
				{
					resetFilterText();
				}
				finally
				{
					SessionView.this.showBusy( false );
				}
			}

		};
		clearSearchAction.setToolTipText( "Clear Search" );
		clearSearchAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/clear.gif" ) );
		clearSearchAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/clear_co.gif" ) );

		findNextAction = new Action( "Find Next", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				findNext( searchTextCombo.getText() );
			}

		};
		findNextAction.setToolTipText( "Find Next" );
		findNextAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/search_next.gif" ) );
		findNextAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/search_next.gif" ) );

		findPreviousAction = new Action( "Find Previous", IAction.AS_PUSH_BUTTON )
		{

			@Override
			public void run()
			{
				findPrevious( searchTextCombo.getText() );
			}

		};
		findPreviousAction.setToolTipText( "Find Previous" );
		findPreviousAction.setImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/search_prev.gif" ) );
		findPreviousAction.setDisabledImageDescriptor( WoodpilePlugin.getImageDescriptor( "icons/disabled/search_prev.gif" ) );
	}

	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener( new IDoubleClickListener()
		{

			@Override
			public void doubleClick( final DoubleClickEvent event )
			{
				doubleClickAction.run();
			}

		} );
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/**
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 *      org.eclipse.ui.IMemento)
	 */
	@Override
	public void init( final IViewSite site, final IMemento memento ) throws PartInitException
	{
		super.init( site, memento );

		this.memento = memento;

		if ( memento != null )
		{
			String value = memento.getString( TAG_FILTER_LEVEL );
			if ( value != null )
				levelFilter.setLevel( Level.toLevel( value, Level.ALL ) );

			value = memento.getString( TAG_AUTO_SHOW );
			if ( value != null )
				autoShow = Boolean.valueOf( value );

			value = memento.getString( TAG_SHOW_SEARCH );
			if ( value != null )
				showSearch = Boolean.valueOf( value );
		}
	}

	/**
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState( final IMemento memento )
	{
		super.saveState( memento );

		// save columns width (copied from org.eclipse.ui.views.tasklist.TaskList)
		final Table table = viewer.getTable();
		final TableColumn columns[] = table.getColumns();
		// check whether it has ever been layed out
		// workaround for 1GDTU19: ITPUI:WIN2000 - Task list columns "collapsed" left
		boolean shouldSave = false;
		for ( int i = 0; i < columns.length; i++ )
		{
			if ( columnLayouts[ i ].resizable && columns[ i ].getWidth() != 0 )
			{
				shouldSave = true;
				break;
			}
		}

		if ( shouldSave )
		{
			for ( int i = 0; i < columns.length; i++ )
			{
				if ( columnLayouts[ i ].resizable )
				{
					final IMemento child = memento.createChild( TAG_COLUMN );
					child.putInteger( TAG_NUMBER, i );
					child.putInteger( TAG_WIDTH, columns[ i ].getWidth() );
				}
			}
		}

		memento.putString( TAG_FILTER_LEVEL, levelFilter.getLevel().toString() );
		memento.putString( TAG_AUTO_SHOW, Boolean.toString( isAutoShow() ) );
		memento.putString( TAG_SHOW_SEARCH, Boolean.toString( isShowSearch() ) );

		final String searchItems[] = searchTextCombo.getItems();
		for ( int i = 0; i < searchItems.length; i++ )
		{
			final IMemento child = memento.createChild( TAG_SEARCH );
			child.putInteger( TAG_NUMBER, i );
			child.putString( TAG_TEXT, searchItems[ i ] );
		}
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

	protected void pageSelectionChanged( final IWorkbenchPart part, final ISelection selection )
	{
		if ( part instanceof LogExplorerView )
		{
			if ( selection instanceof IStructuredSelection )
			{
				if ( !selection.isEmpty() )
				{
					final Iterator it = ( (IStructuredSelection) selection ).iterator();
					if ( it.hasNext() )
					{
						final Object obj = it.next();
						if ( obj instanceof Session )
						{
							final Object currentInput = viewer.getInput();

							if ( obj != currentInput )
							{
								if ( currentInput instanceof Session )
									( (Session) currentInput ).setViewTopIndex( viewer.getTable().getTopIndex() );

								viewer.setInput( obj );

								viewer.getTable().setTopIndex( ( (Session) obj ).getViewTopIndex() );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Getter for autoScroll
	 * 
	 * @return autoScroll
	 */
	public boolean isAutoScroll()
	{
		return autoScroll;
	}

	/**
	 * Getter for autoShow
	 * 
	 * @return autoShow
	 */
	public boolean isAutoShow()
	{
		return autoShow;
	}

	/**
	 * Setter for autoShow
	 * 
	 * @param autoShow autoShow
	 */
	public void setAutoShow( final boolean autoShow )
	{
		this.autoShow = autoShow;

		if ( autoShowAction != null )
			autoShowAction.setChecked( autoShow );
	}

	/**
	 * Setter for autoScroll
	 * 
	 * @param autoScroll autoScroll
	 */
	public void setAutoScroll( final boolean autoScroll )
	{
		this.autoScroll = autoScroll;
	}

	/**
	 * Getter for showSearch
	 * 
	 * @return showSearch
	 */
	public boolean isShowSearch()
	{
		return showSearch;
	}

	/**
	 * Setter for showSearch
	 * 
	 * @param showSearch showSearch
	 */
	public void setShowSearch( final boolean showSearch )
	{
		this.showSearch = showSearch;

		if ( showSearchAction != null )
			showSearchAction.setChecked( showSearch );

		showSearch();
		enableActions();
	}

	private void setFilterLevel( final Level level )
	{
		if ( level != levelFilter.getLevel() )
		{
			// Save preference
			final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();
			prefs.setValue( TAG_FILTER_LEVEL, level.toString() );

			// Update levelFilter
			showBusy( true );
			try
			{
				levelFilter.setLevel( level );
				viewer.refresh();
			}
			finally
			{
				showBusy( false );
				enableActions();
			}
		}
	}

	private void setFilterLevelMenuItemCheck( final Action action )
	{
		fatalLevelAction.setChecked( action == fatalLevelAction );
		errorLevelAction.setChecked( action == errorLevelAction );
		warningLevelAction.setChecked( action == warningLevelAction );
		infoLevelAction.setChecked( action == infoLevelAction );
		debugLevelAction.setChecked( action == debugLevelAction );
		traceLevelAction.setChecked( action == traceLevelAction );
		allLevelAction.setChecked( action == allLevelAction );
	}

	private void findNext( final String text )
	{
		final Table table = viewer.getTable();
		final TableItem[] items = table.getItems();
		int selection = table.getSelectionIndex() + 1;

		final TextFilter filter = createTextFilter();
		filter.setText( text );

		boolean found = false;
		while ( !found && selection < items.length )
		{
			if ( filter.select( viewer, null, items[ selection ].getData() ) )
				found = true;
			else
				selection++;
		}

		if ( found )
			viewer.setSelection( new StructuredSelection( items[ selection ].getData() ), true );

		updateFilterTextDropDown( text );
	}

	private void findPrevious( final String text )
	{
		final Table table = viewer.getTable();
		final TableItem[] items = table.getItems();
		int selection = table.getSelectionIndex() - 1;
		if ( selection < -1 )
			selection = items.length - 1;

		final TextFilter filter = createTextFilter();
		filter.setText( text );

		boolean found = false;
		while ( !found && selection > -1 )
		{
			if ( filter.select( viewer, null, items[ selection ].getData() ) )
				found = true;
			else
				selection--;
		}

		if ( found )
			viewer.setSelection( new StructuredSelection( items[ selection ].getData() ), true );

		updateFilterTextDropDown( text );
	}

	private TextFilter createTextFilter()
	{
		final TextFilter filter = new TextFilter();

		final Preferences prefs = WoodpilePlugin.getDefault().getPluginPreferences();
		filter.setRegex( prefs.getBoolean( FIND_REGEX ) );
		filter.setIgnoreCase( prefs.getBoolean( FIND_IGNORE ) );

		return filter;
	}

	private void setFilterText( final String text )
	{
		if ( textFilter == null )
		{
			textFilter = createTextFilter();
			viewer.addFilter( textFilter );
		}

		SessionView.this.showBusy( true );
		try
		{
			textFilter.setText( text );
			viewer.refresh();
		}
		finally
		{
			SessionView.this.showBusy( false );
			enableActions();
		}

		updateFilterTextDropDown( text );
	}

	private void updateFilterTextDropDown( final String text )
	{
		String[] items = searchTextCombo.getItems();

		if ( text == null || text.length() == 0 )
			return;

		if ( items.length > 0 && items[ 0 ].equals( text ) )
			return;

		boolean found = false;
		int i = 1;
		while ( !found && i < items.length )
		{
			if ( items[ i ].equals( text ) )
				found = true;
			else
				i++;
		}

		// Expand array
		if ( !found && items.length < 10 )
		{
			final String[] temp = new String[ items.length + 1 ];

			for ( int j = 0; j < items.length; j++ )
				temp[ j ] = items[ j ];

			items = temp;
		}

		// Shift items
		while ( i > 0 )
		{
			if ( i < items.length )
				items[ i ] = items[ i - 1 ];

			i--;
		}

		items[ 0 ] = text;
		searchTextCombo.setItems( items );
		searchTextCombo.select( 0 );
		searchTextCombo.setVisibleItemCount( Math.min( items.length, maxVisibleItemCount ) );
	}

	private void resetFilterText()
	{
		if ( textFilter != null )
		{
			SessionView.this.showBusy( true );
			try
			{
				viewer.removeFilter( textFilter );
				textFilter = null;
			}
			finally
			{
				SessionView.this.showBusy( false );
				enableActions();
			}
		}
	}

	private void showSearch()
	{
		searchTextLabel.setVisible( showSearch );
		searchTextCombo.setVisible( showSearch );

		// TODO Dynamically adjust view
	}

	private void enableActions()
	{
		searchAction.setEnabled( showSearch && searchTextCombo.getText().length() > 0 );
		clearSearchAction.setEnabled( showSearch && textFilter != null );
		findNextAction.setEnabled( showSearch && searchTextCombo.getText().length() > 0 );
		findPreviousAction.setEnabled( showSearch && searchTextCombo.getText().length() > 0 );
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#categoryFilterChanged(boolean)
	 */
	public void categoryFilterChanged( final boolean update )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#zoomFilterChanged(java.lang.String)
	 */
	public void zoomFilterChanged( final String name )
	{
		zoomFilter.setLoggerName( name );
	}

}

/*
 * Project: com.hudren.woodpile
 * File:    CategoryTree.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.RGB;

/**
 * TODO CategoryTree description
 * 
 * @author Jeff Hudren
 */
public class CategoryTree
	extends Category
{

	public final static String SEPARATOR = ".";

	private final Map<String, Category> loggers = new HashMap<String, Category>();

	private final List<CategoryTreeListener> listeners = new ArrayList<CategoryTreeListener>();

	private Category zoomNode;

	private boolean showAll;

	public Category addName( final String fullName )
	{
		Category child = null;

		if ( !loggers.containsKey( fullName ) )
		{
			Category node = this;
			final StringTokenizer toker = new StringTokenizer( fullName, SEPARATOR );

			while ( toker.hasMoreTokens() )
			{
				final String name = toker.nextToken();

				if ( !node.contains( name ) )
				{
					child = node.add( name );

					fireNodeAdded( node, child );
					node = child;
				}
				else
					node = node.get( name );
			}

			loggers.put( fullName, node );
		}

		return child;
	}

	public void add( final LogEvent event )
	{
		addName( event.getLoggerName() );
	}

	public void addAll( final Collection<LogEvent> events )
	{
		for ( final LogEvent event : events )
			add( event );
	}

	public void addListener( final CategoryTreeListener listener )
	{
		synchronized ( listeners )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( final CategoryTreeListener listener )
	{
		synchronized ( listeners )
		{
			listeners.remove( listener );
		}
	}

	private void fireNodeAdded( final Category parent, final Category child )
	{
		synchronized ( listeners )
		{
			for ( final CategoryTreeListener listener : listeners )
			{
				listener.nodeAdded( parent, child );
			}
		}
	}

	private void fireNodeRemoved( final Category child )
	{
		synchronized ( listeners )
		{
			for ( final CategoryTreeListener listener : listeners )
			{
				listener.nodeRemoved( child );
			}
		}
	}

	private void fireCategoryFilterChanged( final boolean update )
	{
		synchronized ( listeners )
		{
			for ( final CategoryTreeListener listener : listeners )
			{
				listener.categoryFilterChanged( update );
			}
		}
	}

	private void fireZoomFilterChanged()
	{
		synchronized ( listeners )
		{
			for ( final CategoryTreeListener listener : listeners )
			{
				listener.zoomFilterChanged( zoomNode != null ? zoomNode.getFullName() : null );
			}
		}
	}

	public void clear()
	{
		final SortedSet<Category> children = getChildren();
		if ( children != null )
		{
			for ( final Category child : children )
			{
				removeChild( child );

				fireNodeRemoved( child );
			}
		}

		loggers.clear();
	}

	public Category get( final LogEvent event )
	{
		return loggers.get( event.getLoggerName() );
	}

	public Category getSelfOrParent( final LogEvent event )
	{
		String name = event.getLoggerName();

		while ( name != null )
		{
			final Category node = loggers.get( name );
			if ( node != null )
				return node;

			final int pos = name.lastIndexOf( '.' );
			if ( pos > -1 )
				name = name.substring( 0, pos );
			else
				name = null;
		}

		return null;
	}

	public ContentSelection getContentSelection( final LogEvent event )
	{
		return getContentSelection( getSelfOrParent( event ) );
	}

	public ContentSelection getContentSelection( Category node )
	{
		while ( node != null )
		{
			final FilterInstruction instruction = node.getInstruction();
			if ( instruction != null )
			{
				final ContentSelection selection = instruction.getContentSelection();

				if ( selection != null )
					return selection;
			}

			node = node.getParent();
		}

		return null;
	}

	public VisibilitySelection getVisibilitySelection( final LogEvent event )
	{
		return getVisibilitySelection( getSelfOrParent( event ) );
	}

	public VisibilitySelection getVisibilitySelection( Category node )
	{
		while ( node != null )
		{
			final FilterInstruction instruction = node.getInstruction();
			if ( instruction != null )
			{
				final VisibilitySelection selection = instruction.getVisibilitySelection();

				if ( selection != null )
					return selection;
			}

			node = node.getParent();
		}

		return null;
	}

	public boolean isShown( final Category node )
	{
		if ( showAll )
			return true;

		if ( getVisibilitySelection( node ) != VisibilitySelection.HIDE )
		{
			if ( zoomNode != null && !node.getName().startsWith( zoomNode.getName() ) )
				return false;

			return true;
		}

		return false;
	}

	public boolean isFiltered( final Category node )
	{
		if ( showAll && zoomNode == null )
			return false;

		if ( getVisibilitySelection( node ) == VisibilitySelection.HIDE )
			return true;

		if ( zoomNode != null && !node.getName().startsWith( zoomNode.getName() ) )
			return true;

		return false;
	}

	public boolean isAutoForeground( final Category node )
	{
		return false;
	}

	public RGB getForegroundColor( final Category node )
	{
		return null;
	}

	public RGB getAutoForegroundColor( final Category node )
	{
		return null;
	}

	public RGB getBackgroundColor( final Category node )
	{
		return null;
	}

	/**
	 * TODO resetAll description
	 */
	public void resetAll()
	{
		// TODO Auto-generated method stub

		fireCategoryFilterChanged( false );
	}

	/**
	 * TODO resetIncludeAll description
	 */
	public void resetIncludeAll()
	{
		resetContentSelection();
	}

	/**
	 * TODO resetShowAll description
	 */
	public void resetShowAll()
	{
		resetVisibilitySelection();

		fireCategoryFilterChanged( false );
	}

	/**
	 * TODO include description
	 * 
	 * @param node
	 */
	public void include( final Category node )
	{
		System.out.println( "CategoryTree: include " + node );

		FilterInstruction instruction = node.getInstruction();
		if ( instruction == null )
		{
			instruction = new FilterInstruction();
			node.setInstruction( instruction );
		}

		if ( instruction.getContentSelection() != ContentSelection.INCLUDE )
			instruction.setContentSelection( ContentSelection.INCLUDE );
		else
			instruction.setContentSelection( null );

		fireCategoryFilterChanged( false );
	}

	/**
	 * TODO exclude description
	 * 
	 * @param node
	 */
	public void exclude( final Category node )
	{
		System.out.println( "CategoryTree: exclude " + node );

		FilterInstruction instruction = node.getInstruction();
		if ( instruction == null )
		{
			instruction = new FilterInstruction();
			node.setInstruction( instruction );
		}

		if ( instruction.getContentSelection() != ContentSelection.EXCLUDE )
			instruction.setContentSelection( ContentSelection.EXCLUDE );
		else
			instruction.setContentSelection( null );

		fireCategoryFilterChanged( false );
	}

	/**
	 * TODO show description
	 * 
	 * @param node
	 */
	public void show( final Category node )
	{
		System.out.println( "CategoryTree: show " + node );

		FilterInstruction instruction = node.getInstruction();
		if ( instruction == null )
		{
			instruction = new FilterInstruction();
			node.setInstruction( instruction );
		}

		if ( instruction.getVisibilitySelection() != VisibilitySelection.SHOW )
			instruction.setVisibilitySelection( VisibilitySelection.SHOW );
		else
			instruction.setVisibilitySelection( null );

		fireCategoryFilterChanged( false );
	}

	/**
	 * TODO hide description
	 * 
	 * @param node
	 */
	public void hide( final Category node )
	{
		System.out.println( "CategoryTree: hide " + node );

		FilterInstruction instruction = node.getInstruction();
		if ( instruction == null )
		{
			instruction = new FilterInstruction();
			node.setInstruction( instruction );
		}

		if ( instruction.getVisibilitySelection() != VisibilitySelection.HIDE )
			instruction.setVisibilitySelection( VisibilitySelection.HIDE );
		else
			instruction.setVisibilitySelection( null );

		fireCategoryFilterChanged( true );
	}

	/**
	 * Getter for zoomNode
	 * 
	 * @return zoomNode
	 */
	public Category getZoomNode()
	{
		return zoomNode;
	}

	/**
	 * Setter for zoomNode
	 * 
	 * @param zoomNode zoomNode
	 */
	public void setZoomNode( final Category zoomNode )
	{
		if ( zoomNode != this.zoomNode )
		{
			this.zoomNode = zoomNode;

			fireZoomFilterChanged();
		}
	}

	/**
	 * Getter for showAll
	 * 
	 * @return showAll
	 */
	public boolean isShowAll()
	{
		return showAll;
	}

	/**
	 * Setter for showAll
	 * 
	 * @param showAll showAll
	 */
	public void setShowAll( final boolean showAll )
	{
		if ( showAll != this.showAll )
		{
			this.showAll = showAll;

			fireCategoryFilterChanged( false );
			fireZoomFilterChanged();
		}
	}

}

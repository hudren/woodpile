/*
 * Project: com.hudren.woodpile
 * File:    CategoryViewContentProvider.java
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

import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.hudren.woodpile.model.Category;
import com.hudren.woodpile.model.CategoryTree;
import com.hudren.woodpile.model.CategoryTreeListener;

/**
 * TODO CategoryViewContentProvider description
 * 
 * @author Jeff Hudren
 */
public class CategoryViewContentProvider
	implements ITreeContentProvider, CategoryTreeListener
{

	@SuppressWarnings( "unused" )
	private final CategoryView view;

	private TreeViewer viewer;

	private CategoryTree tree;

	CategoryViewContentProvider( final CategoryView view )
	{
		this.view = view;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements( final Object inputElement )
	{
		if ( inputElement instanceof CategoryTree )
		{
			final SortedSet children = ( (CategoryTree) inputElement ).getChildren();

			if ( children != null )
				return children.toArray();
		}

		return new Object[ 0 ];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren( final Object parentElement )
	{
		if ( parentElement instanceof Category )
		{
			final SortedSet children = ( (Category) parentElement ).getChildren();

			if ( children != null )
				return children.toArray();
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent( final Object element )
	{
		if ( element instanceof Category )
			return ( (Category) element ).getParent();
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren( final Object element )
	{
		if ( element instanceof Category )
			return ( (Category) element ).hasChildren();

		return false;
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
	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput )
	{
		this.viewer = (TreeViewer) viewer;

		if ( oldInput == tree || ! ( newInput instanceof CategoryTree ) )
			if ( tree != null )
			{
				tree.removeListener( this );
				tree = null;
			}

		if ( newInput instanceof CategoryTree )
		{
			tree = (CategoryTree) newInput;
			tree.addListener( this );

			// viewer.refresh();
		}
	}

	@Override
	public void nodeAdded( final Category parent, final Category child )
	{
		if ( Display.getCurrent() != null )
			updateViewerAdd( parent, child );
		else
			Display.getDefault().asyncExec( new Runnable()
			{

				@Override
				public void run()
				{
					updateViewerAdd( parent, child );
				}

			} );
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#nodeRemoved(com.hudren.woodpile.model.Category)
	 */
	@Override
	public void nodeRemoved( final Category node )
	{
		if ( Display.getCurrent() != null )
			updateViewerRemove( node );
		else
			Display.getDefault().asyncExec( new Runnable()
			{

				@Override
				public void run()
				{
					updateViewerRemove( node );
				}

			} );
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#categoryFilterChanged(boolean)
	 */
	@Override
	public void categoryFilterChanged( final boolean update )
	{
	}

	/**
	 * @see com.hudren.woodpile.model.CategoryTreeListener#zoomFilterChanged(java.lang.String)
	 */
	@Override
	public void zoomFilterChanged( final String name )
	{
	}

	private void updateViewerAdd( final Category parent, final Category child )
	{
		viewer.add( parent, child );
		viewer.refresh();
	}

	private void updateViewerRemove( final Category node )
	{
		viewer.remove( node );
		viewer.refresh();
	}

	/**
	 * Getter for tree
	 * 
	 * @return tree
	 */
	CategoryTree getTree()
	{
		return tree;
	}

}

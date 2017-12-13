/*
 * Project: com.hudren.woodpile
 * File:    CategoryFilter.java
 *
 * Author:  Jeff Hudren
 * Created: Jun 12, 2006
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

package com.hudren.woodpile.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.hudren.woodpile.model.CategoryTree;
import com.hudren.woodpile.model.LogEvent;
import com.hudren.woodpile.model.Session;
import com.hudren.woodpile.model.VisibilitySelection;

/**
 * TODO CategoryFilter description
 * 
 * @author Jeff Hudren
 */
public class CategoryFilter
	extends ViewerFilter
{

	private final SessionViewContentProvider contentProvider;

	public CategoryFilter( final SessionViewContentProvider contentProvider )
	{
		super();

		this.contentProvider = contentProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( final Viewer viewer, final Object parentElement, final Object element )
	{
		if ( element instanceof LogEvent )
		{
			final Session session = contentProvider.getSession();

			if ( session != null )
			{
				final CategoryTree tree = session.getCategories();

				if ( tree != null && tree.isShowAll()
						|| tree.getVisibilitySelection( (LogEvent) element ) != VisibilitySelection.HIDE )
					return true;
			}
		}

		return false;
	}

}

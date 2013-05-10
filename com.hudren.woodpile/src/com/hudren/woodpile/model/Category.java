/*
 * Project: com.hudren.woodpile
 * File:    Category.java
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

package com.hudren.woodpile.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * TODO Category description
 * 
 * @author Jeff Hudren
 */
public class Category
	implements Comparable<Category>
{

	private Category parent;
	private Map<String, Category> children;
	private SortedSet<Category> sortedChildren;

	private String name;

	protected FilterInstruction instruction;

	Category()
	{
		children = new TreeMap<String, Category>();
	}

	private Category( final Category parent, final String name )
	{
		this.parent = parent;
		this.name = name;

		parent.addChild( this );
	}

	public Category getParent()
	{
		return parent;
	}

	void addChild( final Category child )
	{
		if ( children == null )
			children = new TreeMap<String, Category>();

		children.put( child.getName(), child );
		sortedChildren = null;
	}

	void removeChild( final Category child )
	{
		if ( children != null )
		{
			if ( children.remove( child.getName() ) != null )
				sortedChildren = null;
		}
	}

	public boolean hasChildren()
	{
		return children != null;
	}

	public SortedSet<Category> getChildren()
	{
		if ( sortedChildren == null && children != null )
		{
			sortedChildren = new TreeSet<Category>();

			for ( final Entry<String, Category> child : children.entrySet() )
				sortedChildren.add( child.getValue() );
		}

		return sortedChildren;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		if ( parent != null )
		{
			final String parentName = parent.getFullName();

			if ( parentName != null )
				return parentName + CategoryTree.SEPARATOR + name;
		}

		return name;
	}

	/**
	 * Getter for instruction
	 * 
	 * @return instruction
	 */
	public FilterInstruction getInstruction()
	{
		return instruction;
	}

	/**
	 * Setter for instruction
	 * 
	 * @param instruction instruction
	 */
	public void setInstruction( final FilterInstruction instruction )
	{
		this.instruction = instruction;
	}

	/**
	 * TODO resetContentSelection description
	 */
	protected void resetContentSelection()
	{
		if ( instruction != null )
			instruction.setContentSelection( null );

		if ( hasChildren() )
		{
			for ( final Category child : getChildren() )
				child.resetContentSelection();
		}
	}

	/**
	 * TODO resetVisibilitySelection description
	 */
	protected void resetVisibilitySelection()
	{
		if ( instruction != null )
			instruction.setVisibilitySelection( null );

		if ( hasChildren() )
		{
			for ( final Category child : getChildren() )
				child.resetVisibilitySelection();
		}
	}

	public boolean contains( final String name )
	{
		if ( children != null )
			return children.containsKey( name );

		return false;
	}

	public Category add( final String name )
	{
		return new Category( this, name );
	}

	public Category get( final String name )
	{
		if ( children != null )
			return children.get( name );

		return null;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( final Category node )
	{
		return name.compareTo( node.getName() );
	}

	@Override
	public String toString()
	{
		return "Category: " + getFullName();
	}

}

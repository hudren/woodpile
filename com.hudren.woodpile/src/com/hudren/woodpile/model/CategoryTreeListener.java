/*
 * Project: com.hudren.woodpile
 * File:    CategoryTreeListener.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

/**
 * TODO CategoryTreeListener description
 * 
 * @author Jeff Hudren
 */
public interface CategoryTreeListener
{

	void nodeAdded( Category parent, Category child );

	void nodeRemoved( Category node );

	void categoryFilterChanged( boolean update );

	void zoomFilterChanged( String name );

}

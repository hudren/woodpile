/*
 * Project: com.hudren.woodpile
 * File:    FilterInstruction.java
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

package com.hudren.woodpile.model;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.eclipse.swt.graphics.RGB;

/**
 * TODO FilterInstruction description
 * 
 * @author Jeff Hudren
 */
public class FilterInstruction
	implements Serializable
{

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4025512520122228180L;

	private ContentSelection contentSelection;

	private Level contentLevel;

	private VisibilitySelection visibilitySelection;

	private Level visibilityLevel;

	private ColorSelection foregroundSelection;

	private RGB foregroundColor;

	private ColorSelection backgroundSelection;

	private RGB backgroundColor;

	/**
	 * Getter for backgroundColor
	 * 
	 * @return backgroundColor
	 */
	public RGB getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Setter for backgroundColor
	 * 
	 * @param backgroundColor backgroundColor
	 */
	public void setBackgroundColor( final RGB backgroundColor )
	{
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Getter for backgroundSelection
	 * 
	 * @return backgroundSelection
	 */
	public ColorSelection getBackgroundSelection()
	{
		return backgroundSelection;
	}

	/**
	 * Setter for backgroundSelection
	 * 
	 * @param backgroundSelection backgroundSelection
	 */
	public void setBackgroundSelection( final ColorSelection backgroundSelection )
	{
		this.backgroundSelection = backgroundSelection;
	}

	/**
	 * Getter for contentLevel
	 * 
	 * @return contentLevel
	 */
	public Level getContentLevel()
	{
		return contentLevel;
	}

	/**
	 * Setter for contentLevel
	 * 
	 * @param contentLevel contentLevel
	 */
	public void setContentLevel( final Level contentLevel )
	{
		this.contentLevel = contentLevel;
	}

	/**
	 * Getter for contentSelection
	 * 
	 * @return contentSelection
	 */
	public ContentSelection getContentSelection()
	{
		return contentSelection;
	}

	/**
	 * Setter for contentSelection
	 * 
	 * @param contentSelection contentSelection
	 */
	public void setContentSelection( final ContentSelection contentSelection )
	{
		this.contentSelection = contentSelection;
	}

	/**
	 * Getter for foregroundColor
	 * 
	 * @return foregroundColor
	 */
	public RGB getForegroundColor()
	{
		return foregroundColor;
	}

	/**
	 * Setter for foregroundColor
	 * 
	 * @param foregroundColor foregroundColor
	 */
	public void setForegroundColor( final RGB foregroundColor )
	{
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Getter for foregroundSelection
	 * 
	 * @return foregroundSelection
	 */
	public ColorSelection getForegroundSelection()
	{
		return foregroundSelection;
	}

	/**
	 * Setter for foregroundSelection
	 * 
	 * @param foregroundSelection foregroundSelection
	 */
	public void setForegroundSelection( final ColorSelection foregroundSelection )
	{
		this.foregroundSelection = foregroundSelection;
	}

	/**
	 * Getter for visibilityLevel
	 * 
	 * @return visibilityLevel
	 */
	public Level getVisibilityLevel()
	{
		return visibilityLevel;
	}

	/**
	 * Setter for visibilityLevel
	 * 
	 * @param visibilityLevel visibilityLevel
	 */
	public void setVisibilityLevel( final Level visibilityLevel )
	{
		this.visibilityLevel = visibilityLevel;
	}

	/**
	 * Getter for visibilitySelection
	 * 
	 * @return visibilitySelection
	 */
	public VisibilitySelection getVisibilitySelection()
	{
		return visibilitySelection;
	}

	/**
	 * Setter for visibilitySelection
	 * 
	 * @param visibilitySelection visibilitySelection
	 */
	public void setVisibilitySelection( final VisibilitySelection visibilitySelection )
	{
		this.visibilitySelection = visibilitySelection;
	}

}

/*
 * Project: com.hudren.woodpile
 * File:    LogExplorerViewLabelProvider.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
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

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.Session;

/**
 * TODO LogExplorerViewLabelProvider description
 * 
 * @author Jeff Hudren
 */
public class LogExplorerViewLabelProvider
	extends LabelProvider
	implements IFontProvider
{

	private final DateFormat df = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT );

	private final Map<Level, Image> icons = new HashMap<Level, Image>();

	private Image logImage;

	private Font boldFont;

	/**
     * 
     */
	public LogExplorerViewLabelProvider( final Viewer viewer )
	{
		super();

		createIcons();

		logImage = WoodpilePlugin.getDefault().getImage( "icons/log_session.gif" );

		final Font normalFont = viewer.getControl().getFont();
		final FontData[] fda = normalFont.getFontData();
		for ( final FontData df : fda )
		{
			df.setStyle( df.getStyle() | SWT.BOLD );
		}
		boldFont = new Font( null, fda );
	}

	void createIcons()
	{
		icons.put( Level.FATAL, WoodpilePlugin.getDefault().getImage( "icons/fatalerror_obj.gif" ) );
		icons.put( Level.ERROR, WoodpilePlugin.getDefault().getImage( "icons/error_obj.gif" ) );
		icons.put( Level.WARN, WoodpilePlugin.getDefault().getImage( "icons/warning_obj.gif" ) );
		icons.put( Level.INFO, WoodpilePlugin.getDefault().getImage( "icons/information.gif" ) );
		icons.put( Level.DEBUG, WoodpilePlugin.getDefault().getImage( "icons/ldebug_obj.gif" ) );
		icons.put( Level.TRACE, WoodpilePlugin.getDefault().getImage( "icons/trace.gif" ) );
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		if ( boldFont != null )
			boldFont.dispose();

		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText( final Object element )
	{
		if ( element instanceof Log )
			return ( (Log) element ).getName();

		if ( element instanceof Session )
			return df.format( ( (Session) element ).getStartTime() );

		return super.getText( element );
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage( final Object element )
	{
		if ( element instanceof Log )
			return logImage;

		if ( element instanceof Session )
			return icons.get( ( (Session) element ).getHighestLevel() );

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
	 */
	@Override
	public Font getFont( final Object element )
	{
		if ( element instanceof Log )
		{
			if ( ( (Log) element ).isActive() )
				return boldFont;
		}

		if ( element instanceof Session )
		{
			if ( ( (Session) element ).isActive() )
				return boldFont;
		}

		return null;
	}

}

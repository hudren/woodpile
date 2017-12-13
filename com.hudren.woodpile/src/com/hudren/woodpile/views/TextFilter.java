/*
 * Project: com.hudren.woodpile
 * File:    TextFilter.java
 *
 * Author:  Jeff Hudren
 * Created: May 13, 2006
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

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO TextFilter description
 * 
 * @author Jeff Hudren
 */
public class TextFilter
	extends ViewerFilter
{

	private boolean regex;
	private boolean ignoreCase;

	private String text;
	private String matchText;

	private Pattern pattern;

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select( final Viewer viewer, final Object parentElement, final Object element )
	{
		boolean match = false;

		if ( element instanceof LogEvent )
		{
			if ( text != null && text.length() > 0 )
			{
				final LogEvent event = (LogEvent) element;

				match = match( event.getLoggerName() );

				if ( !match )
					match = match( event.getRenderedMessage() );

				if ( !match )
					match = match( event.getThrowableStrRep() );
			}
			else
				match = true;
		}

		return match;
	}

	private boolean match( final String field )
	{
		if ( regex )
			return matchExpression( field );

		return matchString( field );
	}

	private boolean match( final String[] fields )
	{
		if ( regex )
			return matchExpression( fields );

		return matchString( fields );
	}

	private boolean matchString( String field )
	{
		boolean match = false;

		if ( field != null )
		{
			if ( ignoreCase )
				field = field.toLowerCase();

			match = field.contains( matchText );
		}

		return match;
	}

	private boolean matchString( final String[] fields )
	{
		boolean match = false;

		if ( fields != null )
		{
			int i = 0;
			while ( !match && i < fields.length )
			{
				String field = fields[ i++ ];

				if ( field != null )
				{
					if ( ignoreCase )
						field = field.toLowerCase();

					match = field.contains( matchText );
				}
			}
		}

		return match;
	}

	private boolean matchExpression( final String field )
	{
		boolean match = false;

		if ( field != null )
			match = pattern.matcher( field ).find();

		return match;
	}

	private boolean matchExpression( final String[] fields )
	{
		boolean match = false;

		if ( fields != null )
		{
			int i = 0;
			while ( !match && i < fields.length )
			{
				final String field = fields[ i++ ];

				if ( field != null )
					match = pattern.matcher( field ).find();
			}
		}

		return match;
	}

	/**
	 * Getter for regex
	 * 
	 * @return regex
	 */
	public boolean isRegex()
	{
		return regex;
	}

	/**
	 * Setter for regex
	 * 
	 * @param regex regex
	 */
	public void setRegex( final boolean regex )
	{
		this.regex = regex;

		calcRegex();
	}

	/**
	 * Getter for text
	 * 
	 * @return text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Setter for text
	 * 
	 * @param text text
	 */
	public void setText( final String text )
	{
		this.text = text;

		calculateMatchText();
		calcRegex();
	}

	/**
	 * Getter for ignoreCase
	 * 
	 * @return ignoreCase
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	/**
	 * Setter for ignoreCase
	 * 
	 * @param ignoreCase ignoreCase
	 */
	public void setIgnoreCase( final boolean ignoreCase )
	{
		this.ignoreCase = ignoreCase;

		calculateMatchText();
		calcRegex();
	}

	private void calculateMatchText()
	{
		if ( ignoreCase && text != null )
			matchText = text.toLowerCase();
		else
			matchText = text;
	}

	private void calcRegex()
	{
		if ( regex && text != null )
		{
			if ( ignoreCase )
				pattern = Pattern.compile( text );
			else
				pattern = Pattern.compile( text, Pattern.CASE_INSENSITIVE );
		}
		else
			pattern = null;
	}

}

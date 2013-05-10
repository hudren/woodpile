/*
 * Created on Jul 17, 2005
 *
 */

package com.hudren.woodpile.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Describes styled text content
 * 
 * @author barryf
 */
public class TextContent
{

	protected String content;

	/**
	 * @return Returns the content.
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content The content to set.
	 */
	public void setContent( final String content )
	{
		this.content = content;
	}

	/**
     * 
     */
	public TextContent( final Display display )
	{
		red = display.getSystemColor( SWT.COLOR_RED );
		green = display.getSystemColor( SWT.COLOR_GREEN );
		blue = display.getSystemColor( SWT.COLOR_BLUE );
		black = display.getSystemColor( SWT.COLOR_BLACK );
		white = display.getSystemColor( SWT.COLOR_WHITE );
	}

	protected int nesting = 0;

	protected List<StyleRange> styleRanges = new ArrayList<StyleRange>();

	boolean inItalics = false, inBold = false, inUnderline = false, inStrikeout = false;
	boolean inRed = false, inBlue = false, inGreen = false;

	protected Color black, white, red, green, blue;

	protected StyleRange curRange;
	protected int rangeStyle = SWT.NORMAL;

	protected void startRange( final int posn )
	{
		if ( curRange != null )
			endRange( posn );

		curRange = new StyleRange();
		curRange.start = posn;
	}

	protected void endRange( final int posn )
	{
		if ( curRange != null )
		{
			curRange.length = posn - curRange.start;

			if ( inItalics )
				rangeStyle |= SWT.ITALIC;
			else
				rangeStyle &= ~SWT.ITALIC;

			if ( inBold )
				rangeStyle |= SWT.BOLD;
			else
				rangeStyle &= ~SWT.BOLD;

			curRange.fontStyle = rangeStyle;
			curRange.underline = inUnderline;
			curRange.strikeout = inStrikeout;

			if ( inRed )
				curRange.foreground = red;
			else if ( inGreen )
				curRange.foreground = green;
			else if ( inBlue )
				curRange.foreground = blue;
			else
				curRange.foreground = black;

			if ( curRange.background == null )
				curRange.background = white;
			if ( curRange.length > 0 )
				styleRanges.add( curRange );
		}

		curRange = null;

		if ( nesting > 0 )
			startRange( posn );
	}

	public String toPlainText()
	{
		int posn = 0;
		final StringBuffer sb = new StringBuffer();

		for ( int i = 0; i < content.length(); )
		{
			char c = content.charAt( i );

			if ( c == '<' )
			{
				if ( "<>".equals( content.substring( i, i + 2 ) ) )
				{
					sb.append( '<' );
					i += 2;
					nesting++;
				}
				else if ( "<i>".equals( content.substring( i, i + 3 ) ) )
				{
					startRange( posn );
					inItalics = true;
					i += 3;
					nesting++;
				}
				else if ( "<b>".equals( content.substring( i, i + 3 ) ) )
				{
					startRange( posn );
					inBold = true;
					i += 3;
					nesting++;
				}
				else if ( "<u>".equals( content.substring( i, i + 3 ) ) )
				{
					startRange( posn );
					inUnderline = true;
					i += 3;
					nesting++;
				}
				else if ( "<so>".equals( content.substring( i, i + 4 ) ) )
				{
					startRange( posn );
					inStrikeout = true;
					i += 4;
					nesting++;
				}
				else if ( "<red>".equals( content.substring( i, i + 5 ) ) )
				{
					startRange( posn );
					inRed = true;
					i += 5;
					nesting++;
				}
				else if ( "<green>".equals( content.substring( i, i + 7 ) ) )
				{
					startRange( posn );
					inGreen = true;
					i += 7;
					nesting++;
				}
				else if ( "<blue>".equals( content.substring( i, i + 6 ) ) )
				{
					startRange( posn );
					inBlue = true;
					i += 6;
					nesting++;
				}
				else if ( "</i>".equals( content.substring( i, i + 4 ) ) )
				{
					endRange( posn );
					inItalics = false;
					i += 4;
					nesting--;
				}
				else if ( "</b>".equals( content.substring( i, i + 4 ) ) )
				{
					endRange( posn );
					inBold = false;
					i += 4;
					nesting--;
				}
				else if ( "</u>".equals( content.substring( i, i + 4 ) ) )
				{
					endRange( posn );
					inUnderline = false;
					i += 4;
					nesting--;
				}
				else if ( "</so>".equals( content.substring( i, i + 5 ) ) )
				{
					endRange( posn );
					inStrikeout = false;
					i += 5;
					nesting--;
				}
				else if ( "</red>".equals( content.substring( i, i + 6 ) ) )
				{
					endRange( posn );
					inRed = false;
					i += 6;
					nesting--;
				}
				else if ( "</green>".equals( content.substring( i, i + 8 ) ) )
				{
					endRange( posn );
					inGreen = false;
					i += 8;
					nesting--;
				}
				else if ( "</blue>".equals( content.substring( i, i + 7 ) ) )
				{
					endRange( posn );
					inBlue = false;
					i += 7;
					nesting--;
				}
				else
				{
					while ( i + 1 < content.length() )
					{
						if ( ( c = content.charAt( ++i ) ) == '>' )
							break;
					}
				}
			}
			else
			{
				sb.append( c );
				i++;
				posn++;
			}
		}

		endRange( posn );

		return sb.toString();
	}

	public StyleRange[] getStyleRanges()
	{
		return styleRanges.toArray( new StyleRange[ styleRanges.size() ] );
	}
}

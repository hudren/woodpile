/*
 * Project: com.hudren.woodpile
 * File:    LogEvent.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * TODO LogEvent description
 * 
 * @author Jeff Hudren
 */
public class LogEvent
	implements Serializable
{

	private static final String NL = System.getProperty( "line.separator" );

	private static final DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS z" );

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3759016573473947910L;

	private final long timeStamp;

	private final String loggerName;

	private final Level level;

	private final String renderedMessage;

	private final String threadName;

	private final String host;

	private final String server;

	private final String[] throwableStrRep;

	public LogEvent( final String host, final LoggingEvent event )
	{
		this.timeStamp = event.timeStamp;
		this.loggerName = event.getLoggerName();
		this.level = event.getLevel();
		this.renderedMessage = event.getRenderedMessage();
		this.threadName = event.getThreadName();
		this.host = host;
		this.server = null;
		this.throwableStrRep = event.getThrowableStrRep();
	}

	public LogEvent( final String host, final String server, final LoggingEvent event )
	{
		this.timeStamp = event.timeStamp;
		this.loggerName = event.getLoggerName();
		this.level = event.getLevel();
		this.renderedMessage = event.getRenderedMessage();
		this.threadName = event.getThreadName();
		this.host = host;
		this.server = server;
		this.throwableStrRep = event.getThrowableStrRep();
	}

	/**
	 * Getter for host
	 * 
	 * @return host
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Getter for server
	 * 
	 * @return server
	 */
	public String getServer()
	{
		return server;
	}

	/**
	 * Getter for level
	 * 
	 * @return level
	 */
	public Level getLevel()
	{
		return level;
	}

	/**
	 * Getter for levelName
	 * 
	 * @return level
	 */
	public String getLevelName()
	{
		return level.toString();
	}

	/**
	 * Getter for loggerName
	 * 
	 * @return loggerName
	 */
	public String getLoggerName()
	{
		return loggerName;
	}

	/**
	 * Getter for renderedMessage
	 * 
	 * @return renderedMessage
	 */
	public String getRenderedMessage()
	{
		return renderedMessage;
	}

	/**
	 * Getter for threadName
	 * 
	 * @return threadName
	 */
	public String getThreadName()
	{
		return threadName;
	}

	/**
	 * Getter for throwableStrRep
	 * 
	 * @return throwableStrRep
	 */
	public String[] getThrowableStrRep()
	{
		return throwableStrRep;
	}

	/**
	 * Getter for timeStamp
	 * 
	 * @return timeStamp
	 */
	public long getTimeStamp()
	{
		return timeStamp;
	}

	public String getStrRep()
	{
		final StringBuffer buffer = new StringBuffer();

		buffer.append( "<b>Level:</b>    " ).append( level ).append( NL );
		buffer.append( "<b>Logger:</b>   " ).append( loggerName ).append( NL );
		buffer.append( "<b>Time:</b>     " ).append( df.format( timeStamp ) ).append( NL );
		buffer.append( "<b>Thread:</b>   " ).append( threadName ).append( NL );
		buffer.append( "<b>Message:</b>  " ).append( renderedMessage );

		if ( throwableStrRep != null && throwableStrRep.length > 0 )
		{
			buffer.append( NL ).append( NL );
			buffer.append( "<red>" );

			for ( final String line : throwableStrRep )
			{
				buffer.append( line ).append( NL );
			}

			buffer.append( "</red>" ).append( NL );
		}

		return buffer.toString();
	}

}

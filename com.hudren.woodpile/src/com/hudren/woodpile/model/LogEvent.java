/*
 * Project: com.hudren.woodpile
 * File:    LogEvent.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.pattern.FileOfCallerConverter;
import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.pattern.MethodOfCallerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

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

	private static final ConcurrentHashMap<String, String> HOSTS = new ConcurrentHashMap<String, String>();

	/**
	 * Helper converters for Logback events.
	 */
	private static final ClassOfCallerConverter classNameConverter = new ClassOfCallerConverter();
	private static final MethodOfCallerConverter methodConverter = new MethodOfCallerConverter();
	private static final FileOfCallerConverter fileConverter = new FileOfCallerConverter();
	private static final LineOfCallerConverter lineConverter = new LineOfCallerConverter();

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

	private final String marker;

	private final String className;

	private final String methodName;

	private final String fileName;

	private final String lineNumber;

	private final String[] throwableStrRep;

	/**
	 * Constructs a LogEvent from a Log4j 1.2 LoggingEvent.
	 *
	 * @param host The host transmitting the log event
	 * @param event The Log4j logging event
	 */
	public LogEvent( final String host, final LoggingEvent event )
	{
		this.timeStamp = event.timeStamp;
		this.loggerName = event.getLoggerName();
		this.level = event.getLevel();
		this.renderedMessage = event.getRenderedMessage();
		this.threadName = event.getThreadName();
		this.host = lookupHost( host );
		this.server = getComponent( event );

		this.marker = standardMarker( null, renderedMessage );

		LocationInfo location = event.getLocationInformation();
		if ( location != null && !location.getClassName().equals( "?" ) )
		{
			this.className = location.getClassName();
			this.methodName = location.getMethodName();
			this.fileName = location.getFileName();
			this.lineNumber = location.getLineNumber();
		}
		else
		{
			this.className = null;
			this.methodName = null;
			this.fileName = null;
			this.lineNumber = null;
		}

		this.throwableStrRep = event.getThrowableStrRep();
	}

	/**
	 * Constructs a LogEvent from a Log4j2 2.0 LogEvent.
	 *
	 * @param host The host transmitting the log event
	 * @param event The Log4j2 log event
	 */
	public LogEvent( final String host, final org.apache.logging.log4j.core.LogEvent event )
	{
		Throwable thrown = event.getThrown();
		String[] rep = null;
		if ( thrown != null )
		{
			StackTraceElement[] stack = thrown.getStackTrace();
			rep = new String[ stack.length + 1 ];

			int i = 1;
			rep[ 0 ] = thrown.getClass().getName() + ": " + thrown.getLocalizedMessage();
			for ( StackTraceElement element : stack )
				rep[ i++ ] = "    at " + element.toString();
		}

		this.timeStamp = event.getTimeMillis();
		this.loggerName = event.getLoggerName();
		this.level = Level.toLevel( event.getLevel().name() );
		this.renderedMessage = event.getMessage().getFormattedMessage();
		this.threadName = event.getThreadName();
		this.host = lookupHost( host );
		this.server = getComponent( event.getContextData() );

		this.marker = standardMarker( event.getMarker().getName(), renderedMessage );

		StackTraceElement location = event.getSource();
		if ( location != null )
		{
			this.className = location.getClassName();
			this.methodName = location.getMethodName();
			this.fileName = location.getFileName();
			this.lineNumber = String.valueOf( location.getLineNumber() );
		}
		else
		{
			this.className = null;
			this.methodName = null;
			this.fileName = null;
			this.lineNumber = null;
		}

		this.throwableStrRep = rep;
	}

	/**
	 * Constructs a LogEvent from a map compatible with a Log4j2 2.0 log event.
	 *
	 * @param host The host transmitting the log event
	 * @param fields The Log4j2 log event fields
	 */
	public LogEvent( final String host, final Map<String, String> fields )
	{
		this.timeStamp = Long.valueOf( fields.get( "timeMillis" ) );
		this.loggerName = fields.get( "loggerName" );
		this.level = Level.toLevel( fields.get( "level" ).toUpperCase() );
		this.renderedMessage = fields.get( "message" );
		this.threadName = fields.get( "thread" );
		this.host = lookupHost( host );
		this.server = getComponent( fields );

		this.marker = standardMarker( null, renderedMessage );

		this.className = fields.get( "class" );
		this.methodName = fields.get( "method" );
		this.fileName = fields.get( "file" );
		this.lineNumber = fields.get( "line" );

		String throwable = fields.get( "throwable" );
		this.throwableStrRep = throwable != null ? throwable.split( "\n" ) : null;
	}

	/**
	 * Constructs a LogEvent from a Logback classic log event.
	 *
	 * @param host The host transmitting the log event
	 * @param fields The Logback log event.
	 */
	public LogEvent( final String host, final ILoggingEvent event )
	{
		timeStamp = event.getTimeStamp();
		loggerName = event.getLoggerName();
		level = getLevel( event );
		renderedMessage = event.getFormattedMessage();
		threadName = event.getThreadName();
		this.host = lookupHost( host );
		server = getComponent( event );

		marker = standardMarker( null, renderedMessage );

		if ( event.hasCallerData() )
		{
			className = classNameConverter.convert( event );
			methodName = methodConverter.convert( event );
			fileName = fileConverter.convert( event );
			lineNumber = lineConverter.convert( event );
		}
		else
		{
			className = null;
			methodName = null;
			fileName = null;
			lineNumber = null;
		}

		ArrayList<String> rep = null;
		IThrowableProxy thrown = event.getThrowableProxy();
		while ( thrown != null )
		{
			StackTraceElementProxy[] stack = thrown.getStackTraceElementProxyArray();

			String causedBy;
			if ( rep == null )
			{
				rep = new ArrayList<>();
				causedBy = "";
			}
			else
				causedBy = "Caused by: ";

			rep.add( causedBy + thrown.getClassName() + ": " + thrown.getMessage() );

			for ( StackTraceElementProxy element : stack )
				rep.add( "    " + element.toString() );

			thrown = thrown.getCause();
		}
		throwableStrRep = rep == null ? null : rep.toArray( new String[ rep.size() ] );
	}

	private static Level getLevel( final ILoggingEvent event )
	{
		Level level;
		switch ( event.getLevel().toInt() )
		{
			case ch.qos.logback.classic.Level.TRACE_INT:
				level = Level.TRACE;
				break;

			case ch.qos.logback.classic.Level.DEBUG_INT:
				level = Level.DEBUG;
				break;

			case ch.qos.logback.classic.Level.ERROR_INT:
				level = Level.ERROR;
				break;

			case ch.qos.logback.classic.Level.INFO_INT:
				level = Level.INFO;
				break;

			case ch.qos.logback.classic.Level.OFF_INT:
				level = Level.OFF;
				break;

			case ch.qos.logback.classic.Level.WARN_INT:
				level = Level.WARN;
				break;

			case ch.qos.logback.classic.Level.ALL_INT:
			default:
				level = Level.ALL;
				break;
		}
		return level;
	}

	private static String lookupHost( final String host )
	{
		String name = HOSTS.get( host );
		if ( name == null )
		{
			name = host;

			Pattern ipPattern = Pattern.compile( "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}" );
			if ( ipPattern.matcher( host ).matches() )
			{
				try
				{
					name = InetAddress.getByName( host ).getHostName();
				}
				catch ( UnknownHostException e )
				{
				}
			}

			HOSTS.putIfAbsent( host, name );
		}

		return name;
	}

	private static String getComponent( LoggingEvent event )
	{
		Object component = event.getMDC( "component" );
		if ( component == null )
			component = event.getMDC( "server" );
		if ( component == null )
			component = event.getMDC( "application" );

		return component != null ? component.toString() : null;
	}

	private String getComponent( ReadOnlyStringMap context )
	{
		String component = context.getValue( "component" );
		if ( component == null )
			component = context.getValue( "server" );
		if ( component == null )
			component = context.getValue( "application" );

		return component;
	}

	private static String getComponent( Map<String, String> context )
	{
		String component = context.get( "component" );
		if ( component == null )
			component = context.get( "server" );
		if ( component == null )
			component = context.get( "application" );

		return component;
	}

	private static String getComponent( final ILoggingEvent event )
	{
		Map<String, String> mdc = event.getMDCPropertyMap();
		if ( mdc != null )
			return getComponent( mdc );

		return null;
	}

	private String standardMarker( final String marker, String message )
	{
		if ( marker != null )
			return marker;

		if ( message != null )
		{
			// Normalize message
			if ( message.length() > 5 )
				message = message.substring( 0, 5 );
			message = message.toLowerCase();

			if ( message.startsWith( "enter" ) )
				return "ENTER";
			else if ( message.startsWith( "exit" ) )
				return "EXIT";
		}

		return null;
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
		final StringBuilder buffer = new StringBuilder();

		buffer.append( "<b>Level:</b>      " ).append( level ).append( NL );
		buffer.append( "<b>Time:</b>       " ).append( df.format( timeStamp ) ).append( NL );

		String location = null;
		if ( className != null && !"?".equals( className ) )
		{
			location = className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";

			if ( marker != null )
				location += " " + marker;
		}

		if ( location == null || !location.startsWith( loggerName ) )
		{
			buffer.append( "<b>Logger:</b>     " ).append( loggerName );

			if ( location == null && marker != null )
				buffer.append( " " ).append( marker );

			buffer.append( NL );
		}

		if ( location != null )
			buffer.append( "<b>Location:</b>   " ).append( location ).append( NL );

		if ( server != null )
		{
			buffer.append( "<b>Component:</b>  " ).append( server );

			if ( host != null && !"localhost".equals( host ) )
				buffer.append( " on " ).append( host );

			buffer.append( NL );
		}
		else if ( host != null && !"localhost".equals( host ) )
			buffer.append( "<b>Host:</b>       " ).append( host ).append( NL );

		buffer.append( "<b>Thread:</b>     " ).append( threadName ).append( NL );
		buffer.append( "<b>Message:</b>    " ).append( renderedMessage );

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

	public String getClassName()
	{
		return className;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getLineNumber()
	{
		return lineNumber;
	}

	public String getMarker()
	{
		return marker;
	}

}

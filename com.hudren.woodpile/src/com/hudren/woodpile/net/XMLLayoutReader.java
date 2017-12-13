/*
 * File: XMLReader.java
 * Project: com.hudren.woodpile
 * Created: Nov 3, 2013
 *
 * Copyright (c) 2013 Hudren Andromeda Connection. All rights reserved.
 */

package com.hudren.woodpile.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.hudren.woodpile.model.LogEvent;

/**
 * TODO Type description
 * 
 * @author jeff
 */
class XMLLayoutReader
	extends AbstractReader
{

	public XMLLayoutReader( ReceiverManager manager, Socket client, BlockingQueue<LogEvent> queue )
	{
		super( manager, client, queue );
	}

	/**
	 * TODO Method description for <code>run()</code>
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			final String host = client.getInetAddress().getHostName();

			final XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty( XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE );

			final BufferedInputStream is = new BufferedInputStream( client.getInputStream() );
			final XMLEventReader reader = factory.createXMLEventReader( is );

			while ( !done && reader.hasNext() )
			{
				while ( reader.hasNext() )
				{
					XMLEvent xmlEvent = reader.nextEvent();

					if ( xmlEvent.isStartElement() )
					{
						StartElement start = xmlEvent.asStartElement();
						if ( start.getName().getLocalPart().equals( "Event" ) )
						{
							// Read event from xml stream
							Map<String, String> fields = readEvent( reader, start );

							try
							{
								queue.add( new LogEvent( host, fields ) );
							}
							catch ( Exception e )
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		catch ( final EOFException e )
		{
			System.out.println( "Client closed socket" );
		}
		catch ( final SocketException e )
		{
			e.printStackTrace();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
		catch ( XMLStreamException e )
		{
			e.printStackTrace();
		}

		// Close connection
		try
		{
			client.close();
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}

		manager.connectionClosed( this );
	}

	private HashMap<String, String> readEvent( XMLEventReader reader, StartElement element ) throws XMLStreamException
	{
		HashMap<String, String> event = new HashMap<String, String>();

		// Grab element attributes as fields
		readAttributes( element, event );

		boolean done = false;
		while ( !done && reader.hasNext() )
		{
			XMLEvent xmlEvent = reader.nextEvent();

			if ( xmlEvent.isStartElement() )
			{
				StartElement start = xmlEvent.asStartElement();
				String name = start.getName().getLocalPart();

				if ( name.equals( "Message" ) )
					event.put( "message", readText( reader, start ) );

				else if ( name.equals( "Marker" ) )
					event.put( "marker", readText( reader, start ) );

				else if ( name.equals( "Throwable" ) )
					event.put( "throwable", readText( reader, start ) );

				else if ( name.equals( "LocationInfo" ) )
					readAttributes( start, event );

				else if ( name.equals( "Data" ) )
				{
					String key = start.getAttributeByName( QName.valueOf( "name" ) ).getValue();
					String value = start.getAttributeByName( QName.valueOf( "value" ) ).getValue();
					event.put( key, value );
				}
			}

			else if ( xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart().equals( "Event" ) )
				done = true;
		}

		return event;
	}

	@SuppressWarnings( "unchecked" )
	private void readAttributes( StartElement element, HashMap<String, String> event )
	{
		Iterator<Attribute> iter = element.getAttributes();
		while ( iter.hasNext() )
		{
			Attribute attr = iter.next();
			event.put( attr.getName().getLocalPart(), attr.getValue() );
		}
	}

	private String readText( XMLEventReader reader, StartElement element ) throws XMLStreamException
	{
		StringBuilder text = new StringBuilder();
		String tagName = element.getName().getLocalPart();

		boolean done = false;
		while ( !done && reader.hasNext() )
		{
			XMLEvent xmlEvent = reader.nextEvent();

			if ( xmlEvent.isCharacters() )
				text.append( xmlEvent.asCharacters().getData() );

			else if ( xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart().equals( tagName ) )
				done = true;
		}

		return text.toString();
	}
}

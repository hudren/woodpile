/*
 * Project: com.hudren.woodpile
 * File:    GlobalPreferencePage.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
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

package com.hudren.woodpile.prefs;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.TreeSet;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hudren.woodpile.WoodpilePlugin;

import static com.hudren.woodpile.prefs.PreferenceConstants.AUTO_STARTUP;
import static com.hudren.woodpile.prefs.PreferenceConstants.FIND_IGNORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.FIND_REGEX;
import static com.hudren.woodpile.prefs.PreferenceConstants.HISTORY_DAYS;
import static com.hudren.woodpile.prefs.PreferenceConstants.MAX_EVENTS;
import static com.hudren.woodpile.prefs.PreferenceConstants.PORT;
import static com.hudren.woodpile.prefs.PreferenceConstants.SIMPLE_NAME;
import static com.hudren.woodpile.prefs.PreferenceConstants.XML_PORT;

/**
 * TODO GlobalPreferencePage description
 * 
 * @author Jeff Hudren
 */
public class GlobalPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public GlobalPreferencePage()
	{
		super( GRID );
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init( final IWorkbench workbench )
	{
		setPreferenceStore( WoodpilePlugin.getDefault().getPreferenceStore() );

		TreeSet<String> ips = new TreeSet<String>();
		try
		{
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
			while ( nis.hasMoreElements() )
			{
				NetworkInterface ni = nis.nextElement();
				if ( ni.isUp() )
				{
					Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses();
					while ( enumIpAddr.hasMoreElements() )
					{
						String ip = enumIpAddr.nextElement().getHostAddress();

						if ( ip.contains( "." ) )
							ips.add( ip );
					}
				}
			}
		}
		catch ( SocketException e )
		{
			System.out.println( " (error retrieving network interface list)" );
		}

		if ( ips.size() > 1 )
			ips.remove( "127.0.0.1" );

		if ( !ips.isEmpty() )
		{
			String desc = "";
			for ( String ip : ips )
			{
				if ( desc.length() > 0 )
					desc += ", ";

				desc += ip;
			}

			if ( ips.size() > 1 )
				setDescription( "The IP addresses for this machine are " + desc );
			else
				setDescription( "The IP address of this machine is " + desc );
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors()
	{
		addField( new IntegerFieldEditor( PORT, "Receiver TCP port:", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( XML_PORT, "Receiver TCP port (XML):", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( MAX_EVENTS, "Maximum events in session:", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( HISTORY_DAYS, "Days to keep sessions:", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( AUTO_STARTUP, "Restart active logs on startup", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( SIMPLE_NAME, "Display simple name for loggers", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( FIND_REGEX, "Use regular expressions when searching log", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( FIND_IGNORE, "Ignore case when searching log", getFieldEditorParent() ) );
	}

}

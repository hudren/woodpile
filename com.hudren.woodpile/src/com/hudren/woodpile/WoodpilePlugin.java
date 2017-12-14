/*
 * Project: com.hudren.woodpile
 * File:    WoodpilePlugin.java
 *
 * Author:  Jeff Hudren
 * Created: May 6, 2006
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

package com.hudren.woodpile;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.hudren.woodpile.model.Log;
import com.hudren.woodpile.model.Session;
import com.hudren.woodpile.model.Source;
import com.hudren.woodpile.prefs.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class WoodpilePlugin
	extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.hudren.woodpile";

	// The shared instance
	private static WoodpilePlugin plugin;

	private static Source source;

	private final SortedSet<Log> logs = new TreeSet<Log>();
	private Log log;

	private final ImageCache imageCache = new ImageCache();

	/**
	 * The constructor
	 */
	public WoodpilePlugin()
	{
		plugin = this;
	}

	private synchronized void init()
	{
		if ( source == null )
		{
			try
			{
				// Get user preferences
				final IPreferenceStore prefs = WoodpilePlugin.getDefault().getPreferenceStore();
				final int port = prefs.getInt( PreferenceConstants.PORT );
				final int xmlPort = prefs.getInt( PreferenceConstants.XML_PORT );
				final boolean start = prefs.getBoolean( PreferenceConstants.AUTO_STARTUP );

				source = new Source( port, xmlPort );
				source.start();

				log = new Log( start );
				logs.add( log );

				source.addListener( log );
			}
			catch ( final IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start( final BundleContext context ) throws Exception
	{
		super.start( context );

		BasicConfigurator.configure();

		init();
	}

	@Override
	public void stop( final BundleContext context ) throws Exception
	{
		if ( source != null )
			source.stop();

		imageCache.dispose();

		super.stop( context );
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static WoodpilePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor( final String path )
	{
		return imageDescriptorFromPlugin( PLUGIN_ID, path );
	}

	public Image getImage( final ImageDescriptor descriptor )
	{
		return imageCache.getImage( descriptor );
	}

	public Image getImage( final String path )
	{
		return imageCache.getImage( imageDescriptorFromPlugin( PLUGIN_ID, path ) );
	}

	/**
	 * Getter for logs
	 * 
	 * @return logs
	 */
	public SortedSet<Log> getLogs()
	{
		return logs;
	}

	/**
	 * Getter for log
	 * 
	 * @return log
	 */
	public Log getCurrentLog()
	{
		return log;
	}

	/**
	 * Getter for session
	 * 
	 * @return session
	 */
	public Session getCurrentSession()
	{
		Session session = null;

		if ( log != null )
			session = log.getActiveSession();

		return session;
	}

	/**
	 * Getter for source
	 * 
	 * @return source
	 */
	public Source getSource()
	{
		return source;
	}

}

/*
 * Project: com.hudren.woodpile.startup
 * File:    Activator.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 3, 2006
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

package com.hudren.woodpile.startup;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

import com.hudren.woodpile.WoodpileStartup;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator
	extends Plugin
	implements IStartup
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.hudren.woodpile.startup";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
		plugin = this;
	}

	public void earlyStartup()
	{
		// Force loading of woodpile plugin?
		new WoodpileStartup();
	}

	@Override
	public void stop( BundleContext context ) throws Exception
	{
		plugin = null;
		super.stop( context );
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

}

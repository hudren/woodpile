/*
 * Project: com.hudren.woodpile
 * File:    GlobalPreferencePage.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.prefs;

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

		setPreferenceStore( WoodpilePlugin.getDefault().getPreferenceStore() );
		// setDescription( "General gettings for log capturing and viewing:" );
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init( final IWorkbench workbench )
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors()
	{
		addField( new BooleanFieldEditor( AUTO_STARTUP, "Restart active logs on startup", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( PORT, "Receiver port:", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( MAX_EVENTS, "Maximum events in session:", getFieldEditorParent() ) );
		addField( new IntegerFieldEditor( HISTORY_DAYS, "Days to keep sessions:", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( FIND_REGEX, "Use regular expressions when searching log", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( FIND_IGNORE, "Ignore case when searching log", getFieldEditorParent() ) );
	}

}
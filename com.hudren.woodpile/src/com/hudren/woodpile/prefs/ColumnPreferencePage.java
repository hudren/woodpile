/*
 * Project: com.hudren.woodpile
 * File:    ColumnPreferencePage.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hudren.woodpile.WoodpilePlugin;

import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_HOST;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_LEVEL;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_LOGGER;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_MESSAGE;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_SERVER;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_THREAD;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_THROWABLE;
import static com.hudren.woodpile.prefs.PreferenceConstants.VIEWER_COLUMN_TIME;

/**
 * TODO ColumnPreferencePage description
 * 
 * @author Jeff Hudren
 */
public class ColumnPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public ColumnPreferencePage()
	{
		super( GRID );

		setPreferenceStore( WoodpilePlugin.getDefault().getPreferenceStore() );
		setDescription( "Choose the columns to display in the Log view:" );
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
		addField( new BooleanFieldEditor( VIEWER_COLUMN_TIME, "Time", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_LEVEL, "Level", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_LOGGER, "Logger", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_MESSAGE, "Message", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_THROWABLE, "Throwable", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_THREAD, "Thread", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_HOST, "Host", getFieldEditorParent() ) );
		addField( new BooleanFieldEditor( VIEWER_COLUMN_SERVER, "Server", getFieldEditorParent() ) );
	}

}

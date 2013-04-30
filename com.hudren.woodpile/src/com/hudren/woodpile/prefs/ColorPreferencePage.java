/*
 * Project: com.hudren.woodpile
 * File:    ColorPreferencePage.java
 *
 * Author:  Jeff Hudren
 * Created: May 10, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.prefs;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hudren.woodpile.WoodpilePlugin;

import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_DEBUG_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_DEBUG_FORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_ERROR_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_ERROR_FORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_FATAL_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_FATAL_FORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_INFO_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_INFO_FORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_TRACE_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_TRACE_FORE;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_WARN_BACK;
import static com.hudren.woodpile.prefs.PreferenceConstants.COLOR_WARN_FORE;

/**
 * TODO ColorPreferencePage description
 * 
 * @author Jeff Hudren
 */
public class ColorPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public ColorPreferencePage()
	{
		super( GRID );

		setPreferenceStore( WoodpilePlugin.getDefault().getPreferenceStore() );
		setDescription( "Specify the colors used to highlight events based on level:" );
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
		addField( new ColorFieldEditor( COLOR_FATAL_FORE, "Fatal foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_FATAL_BACK, "Background:", getFieldEditorParent() ) );

		addField( new ColorFieldEditor( COLOR_ERROR_FORE, "Error foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_ERROR_BACK, "Background:", getFieldEditorParent() ) );

		addField( new ColorFieldEditor( COLOR_WARN_FORE, "Warning foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_WARN_BACK, "Background:", getFieldEditorParent() ) );

		addField( new ColorFieldEditor( COLOR_INFO_FORE, "Info foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_INFO_BACK, "Background:", getFieldEditorParent() ) );

		addField( new ColorFieldEditor( COLOR_DEBUG_FORE, "Debug foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_DEBUG_BACK, "Background:", getFieldEditorParent() ) );

		addField( new ColorFieldEditor( COLOR_TRACE_FORE, "Trace foreground:", getFieldEditorParent() ) );
		addField( new ColorFieldEditor( COLOR_TRACE_BACK, "Background:", getFieldEditorParent() ) );
	}

}

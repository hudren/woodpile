/*
 * Project: com.hudren.woodpile
 * File:    LogViewingPerspectiveFactory.java
 *
 * Author:  Jeff Hudren
 * Created: May 6, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.hudren.woodpile.views.CategoryView;
import com.hudren.woodpile.views.LogEventView;
import com.hudren.woodpile.views.LogExplorerView;
import com.hudren.woodpile.views.SessionView;

/**
 * TODO LogViewingPerspectiveFactory description
 * 
 * @author Jeff Hudren
 */
public class LogViewingPerspectiveFactory
	implements IPerspectiveFactory
{

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout( final IPageLayout layout )
	{
		final String editorArea = layout.getEditorArea();

		final IFolderLayout left = layout.createFolder( "left", IPageLayout.LEFT, 0.20f, editorArea );
		left.addView( LogExplorerView.ID );
		left.addPlaceholder( "org.eclipse.jdt.ui.PackageExplorer" );
		left.addPlaceholder( IPageLayout.ID_RES_NAV );

		layout.addView( CategoryView.ID, IPageLayout.BOTTOM, 0.50f, LogExplorerView.ID );

		layout.addView( SessionView.ID, IPageLayout.BOTTOM, 0.20f, editorArea );

		final IFolderLayout bottom = layout.createFolder( "bottom", IPageLayout.BOTTOM, 0.80f, SessionView.ID );
		bottom.addView( LogEventView.ID );

		layout.addShowViewShortcut( LogExplorerView.ID );
		layout.addShowViewShortcut( CategoryView.ID );
		layout.addShowViewShortcut( SessionView.ID );
		layout.addShowViewShortcut( LogEventView.ID );

		layout.setEditorAreaVisible( false );
	}

}

/*
 * Project: com.hudren.woodpile
 * File:    CategoryViewLabelProvider.java
 *
 * Author:  Jeff Hudren
 * Created: May 14, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.views;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.hudren.woodpile.WoodpilePlugin;
import com.hudren.woodpile.model.Category;
import com.hudren.woodpile.model.CategoryTree;
import com.hudren.woodpile.model.ContentSelection;
import com.hudren.woodpile.model.FilterInstruction;
import com.hudren.woodpile.model.VisibilitySelection;

/**
 * TODO CategoryViewLabelProvider description
 * 
 * @author Jeff Hudren
 */
public class CategoryViewLabelProvider
	extends LabelProvider
	implements ILightweightLabelDecorator
{

	private Image packageImage;
	private Image excludedPackageImage;
	private Image classImage;
	private Image textImage;

	CategoryViewLabelProvider()
	{
		super();

		packageImage = WoodpilePlugin.getImageDescriptor( "icons/package_obj.gif" ).createImage();
		classImage = WoodpilePlugin.getImageDescriptor( "icons/jcu_obj.gif" ).createImage();
		textImage = WoodpilePlugin.getImageDescriptor( "icons/normal_page.gif" ).createImage();
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		if ( packageImage != null )
			packageImage.dispose();

		if ( excludedPackageImage != null )
			excludedPackageImage.dispose();

		if ( classImage != null )
			classImage.dispose();

		if ( textImage != null )
			textImage.dispose();

		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText( final Object element )
	{
		if ( element instanceof Category )
		{
			final Category node = (Category) element;
			String text = node.getName();

			final FilterInstruction instruction = node.getInstruction();
			if ( instruction != null )
			{
				final ContentSelection cs = instruction.getContentSelection();
				final VisibilitySelection vs = instruction.getVisibilitySelection();

				if ( cs != null || vs != null )
				{
					text += " [";

					if ( cs == ContentSelection.INCLUDE )
						text += "included";
					else if ( cs == ContentSelection.EXCLUDE )
						text += "excluded";

					if ( vs != null )
					{
						if ( cs != null )
							text += ", ";

						if ( vs == VisibilitySelection.SHOW )
							text += "shown";
						else if ( vs == VisibilitySelection.HIDE )
							text += "hidden";
					}

					text += "]";
				}
			}

			return text;
		}

		return super.getText( element );
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage( final Object element )
	{
		Image image = super.getImage( element );

		if ( element instanceof Category )
		{
			final Category node = (Category) element;

			if ( node.hasChildren() )
			{
				image = packageImage;
			}
			else
			{
				final Category parent = node.getParent();

				if ( parent != null && ! ( parent instanceof CategoryTree ) )
					image = classImage;
				else
					image = textImage;
			}
		}

		return image;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate( final Object element, final IDecoration decoration )
	{
		if ( element instanceof Category )
		{
			decoration.addSuffix( " [decorated]" );
		}
	}

}

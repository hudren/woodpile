/*
 * Project: com.hudren.woodpile
 * File:    ImageCache.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * TODO ImageCache description
 * 
 * @author Jeff Hudren
 */
public class ImageCache
{

	private final Map<ImageDescriptor, Image> images = new HashMap<ImageDescriptor, Image>();

	public Image getImage( final ImageDescriptor descriptor )
	{
		if ( descriptor == null )
			return null;

		Image image = images.get( descriptor );
		if ( image == null )
		{
			image = descriptor.createImage();
			images.put( descriptor, image );
		}

		return image;
	}

	public void dispose()
	{
		for ( final Image image : images.values() )
			image.dispose();
	}

}

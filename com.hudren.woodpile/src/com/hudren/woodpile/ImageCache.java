/*
 * Project: com.hudren.woodpile
 * File:    ImageCache.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
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

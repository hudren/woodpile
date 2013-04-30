/*
 * Project: com.hudren.woodpile
 * File:    SourceListener.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.util.List;

/**
 * TODO SourceListener description
 * 
 * @author Jeff Hudren
 */
public interface SourceListener
{

	void addEvents( List<LogEvent> events );

	void receiverClosed();

}

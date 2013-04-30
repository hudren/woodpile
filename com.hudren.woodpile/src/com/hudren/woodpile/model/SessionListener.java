/*
 * Project: com.hudren.woodpile
 * File:    SessionListener.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

import java.util.List;

/**
 * TODO SessionListener description
 * 
 * @author Jeff Hudren
 */
public interface SessionListener
{

	void eventsChanged( Session session, List<LogEvent> removed, List<LogEvent> added );

	void sessionCleared( Session session );

	void sessionChanged( Session session );

}

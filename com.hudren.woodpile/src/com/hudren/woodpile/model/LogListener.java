/*
 * Project: com.hudren.woodpile
 * File:    LogListener.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
 *
 * Copyright (c) 2006 Hudren Andromeda Connection. All rights reserved. 
 */

package com.hudren.woodpile.model;

/**
 * TODO LogListener description
 * 
 * @author Jeff Hudren
 */
public interface LogListener
{

	void sessionAdded( Session session, Session deactivated );

	void sessionRemoved( Session session );

}

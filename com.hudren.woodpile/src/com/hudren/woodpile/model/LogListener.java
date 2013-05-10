/*
 * Project: com.hudren.woodpile
 * File:    LogListener.java
 *
 * Author:  Jeff Hudren
 * Created: Jul 16, 2006
 *
 * Copyright (c) 2006-2013 Hudren Andromeda Connection. All rights reserved. 
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

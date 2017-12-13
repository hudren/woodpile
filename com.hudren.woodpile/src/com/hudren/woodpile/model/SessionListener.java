/*
 * Project: com.hudren.woodpile
 * File:    SessionListener.java
 *
 * Author:  Jeff Hudren
 * Created: May 7, 2006
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

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

/**
 * Copyright 2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * 
 */
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;

/**
 * A class that represents an action which will fire a sequence of actions.
 * The action ids are added to the internal list. When this action is invoked,
 * the event will be dispatched to the actions in the internal list.
 * <p>
 * The action ids are represented by the value of the <code>Action.ACTION_COMMAND_KEY</code>
 * and must be managed by the <code>ActionManager</code>. When this action is 
 * invoked, then the actions are retrieved from the ActionManager in list order
 * and invoked.
 * 
 * @see ActionManager
 * @author Mark Davidson
 */
public class CompositeAction extends AbstractActionExt {

     /**
     * Keys for storing extended action attributes. May make public.
     */
    private static final String LIST_IDS = "action-list-ids";

    public CompositeAction() {
	this("CompositeAction");
    }

    public CompositeAction(String name) {
	super(name);
    }

    /**
     * @param name display name of the action
     * @param command the value of the action command key
     */
    public CompositeAction(String name, String command) {
	super(name, command);
    }

    public CompositeAction(String name, Icon icon) {
	super(name, icon);
    }

    /**
     * @param name display name of the action
     * @param command the value of the action command key
     * @param icon icon to display
     */
    public CompositeAction(String name, String command, Icon icon) {
	super(name, command, icon);
    }

    /**
     * Add an action id to the action list. This action will be invoked 
     * when this composite action is invoked.
     */
    public void addAction(String id) {
	List list = (List)getValue(LIST_IDS);
	if (list == null) {
	    list = new ArrayList();
	    putValue(LIST_IDS, list);
	}
	list.add(id);
    }

    /**
     * Returns a list of action ids which indicates that this is a composite
     * action. 
     * @return a valid list of action ids or null
     */
    public List getActionIDs() {
	return (List)getValue(LIST_IDS);
    }	

    /**
     * Callback for composite actions. This method will redispatch the 
     * ActionEvent to all the actions held in the list.
     */
    public void actionPerformed(ActionEvent evt) {
	ActionManager manager = ActionManager.getInstance();
	    
	Iterator iter = getActionIDs().iterator();
	while (iter.hasNext()) {
	    String id = (String)iter.next();
	    Action action = manager.getAction(id);
	    if (action != null) {
		action.actionPerformed(evt);
	    }
	}
    }

    /**
     * Callback for toggle actions.
     */
    public void itemStateChanged(ItemEvent evt) {
	ActionManager manager = ActionManager.getInstance();
	    
	Iterator iter = getActionIDs().iterator();
	while (iter.hasNext()) {
	    String id = (String)iter.next();
	    Action action = manager.getAction(id);
	    if (action != null && action instanceof AbstractActionExt) {
		((AbstractActionExt)action).itemStateChanged(evt);
	    }
	}
    }
}

/*
 * Created on 28.03.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ItemEvent;

public abstract class LinkAction extends AbstractActionExt {

    /**
     * temporaryly changed value to guarantee hyperlink is listening to
     * this instead of to linkmodel.
     */
    public static final String VISITED_KEY = "link_visited";
    private Object target;


    public LinkAction() {
        this(null);
    }
    
    public LinkAction(Object target) {
       this(target, false);
       setTarget(target);
    }

    public LinkAction(Object target, boolean visited) {
        setTarget(target);
        setVisited(visited);
    }

    /**
     * Set the visited property.
     * 
     * @param visited
     */
    public void setVisited(boolean visited) {
        putValue(VISITED_KEY, visited);
    }

    /**
     * 
     * @return visited state
     */
    public boolean isVisited() {
        Boolean visited = (Boolean) getValue(VISITED_KEY);
        return Boolean.TRUE.equals(visited) ? true : false;
    }

    
    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        Object oldTarget = getTarget();
        uninstallTarget();
        this.target = target;
        installTarget();
        firePropertyChange("target", oldTarget, getTarget());
    }

    /**
     * hook for subclasses to update internal state after
     * a new target has been set. <p>
     * 
     * Subclasses are free to decide the details. 
     * Here: 
     * <ul>
     * <li> the text property is set to target.toString or empty String if
     * the target is null
     * <li> visited is set to false.
     * </ul>
     */
    protected void installTarget() {
        setName(target != null ? target.toString() : "" );
        setVisited(false);
    }

    /**
     * hook for subclasses to cleanup before the old target
     * is overwritten. <p>
     * 
     * Subclasses are free to decide the details. 
     * Here: does nothing.
     */
    protected void uninstallTarget() {
        
    }
    
    public void itemStateChanged(ItemEvent e) {
        // do nothing
    }

    /**
     * Set the state property.
     * Overridden to to nothing.
     * PENDING: really?
     * @param state if true then this action will fire ItemEvents
     */
    @Override
    public void setStateAction(boolean state) {
    }

    

}

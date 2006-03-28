/*
 * Created on 28.03.2006
 *
 */
package org.jdesktop.swingx.action;

import java.awt.event.ItemEvent;

/**
 * Convenience implementation to simplify {@link JXHyperlink} configuration and 
 * provide minimal api as needed by a {@link LinkRenderer}. <p>
 * 
 * PENDING: generalize LinkRenderer to use LinkAction instead of LinkModelAction
 * PENDING: generify target.
 * 
 * @author Jeanette Winzenburg
 */
public abstract class LinkAction extends AbstractActionExt {

    /**
     * Key for the visited property value.
     */
    public static final String VISITED_KEY = "visited";
    /**
     * the object the actionPerformed can act on.
     */
    protected Object target;

    /**
     * Instantiates a LinkAction with null target. 
     * 
     */
    public LinkAction() {
        this(null);
    }
    
    /**
     * Instantiates a LinkAction with target. 
     * The visited property is initialized as defined by 
     * {@link LinkAction#installTarget()}
     * 
     * @param target the target this action should act on.
     */
    public LinkAction(Object target) {
       setTarget(target);
    }

    /**
     * Instantiates a LinkAction with target and visited. T
     * The initial value of visited is guaranteed to be 
     * the given parameter value, overruling the 
     * {@link LinkAction#installTarget()}
     * 
     * PENDING: hmm... better remove this constructor? 
     * Done for now - there are issue with the 
     * class invarant. 
     * 
     * @param target
     * @param visited
     */
//    public LinkAction(Object target, boolean visited) {
//        setTarget(target);
//        // JW: hmmm ... reverse method calls to guarantee target 
//        // properties take precedence if wanted?
//        // test!!
//        setVisited(visited);
//    }

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

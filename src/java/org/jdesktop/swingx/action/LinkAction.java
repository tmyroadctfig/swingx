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
 * PENDING: rename to AbstractLinkAction
 * 
 * @author Jeanette Winzenburg
 */
public abstract class LinkAction <T> extends AbstractActionExt {

    /**
     * Key for the visited property value.
     */
    public static final String VISITED_KEY = "visited";
    /**
     * the object the actionPerformed can act on.
     */
    protected T target;

    protected Class<?> targetClass;

    
    /**
     * Instantiates a LinkAction with null target. 
     * 
     */
    public LinkAction(Class<?> targetClass) {
        this(null, targetClass);
    }
    
    /**
     * Instantiates a LinkAction with a target of type targetClass. 
     * The visited property is initialized as defined by 
     * {@link LinkAction#installTarget()}
     * 
     * @param target the target this action should act on.
     * @param targetClass the type of target
     * @throws IllegalArgumentException if !isTargetable(target)
     */
    public LinkAction(T target, Class<?> targetClass) {
       this.targetClass = targetClass; 
       if (!isTargetable(target)) 
           throw new IllegalArgumentException("the target class is expected to be " + targetClass);
       setTarget(target);
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

    
    public T getTarget() {
        return target;
    }

    /**
     * PRE: isTargetable(target)
     * @param target
     */
    public void setTarget(T target) {
        T oldTarget = getTarget();
        uninstallTarget();
        this.target = target;
        installTarget();
        firePropertyChange("target", oldTarget, getTarget());
        
    }

    /**
     * decides if the given target is acceptable for setTarget.
     * <p>
     *  
     *  target == null is acceptable for all types.
     *  targetClass == null is the same as Object.class
     *  
     * @param target the target to set.
     * @return true if setTarget can cope with the object, 
     *  false otherwise.
     * 
     */
    public  boolean isTargetable(Object target) {
        // we accept everything
        if (targetClass == null) return true;
        if (target == null) return true;
        return targetClass.isAssignableFrom(target.getClass());
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

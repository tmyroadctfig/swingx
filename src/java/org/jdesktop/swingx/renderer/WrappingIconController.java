/*
 * Created on 08.01.2007
 *
 */
package org.jdesktop.swingx.renderer;

import javax.swing.BorderFactory;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Wrapping controller for usage in tree renderers. Handles the icon, delegates the value to 
 * the wrappee. <p>
 * 
 * PENDING: slight layout problem (one-pixel jumping on selection)<p>
 * PENDING: focus rect missing <p>
 * PENDING: ui specific focus rect variations not yet done <p>
 */
public class WrappingIconController extends 
    RenderingComponentController<WrappingIconPanel>  implements RolloverRenderer {

    private RenderingComponentController wrappee;

    public WrappingIconController() {
        this((RenderingComponentController) null);
    }
    
    public WrappingIconController(RenderingComponentController wrapper) {
        super();
        setWrappee(wrapper);
    }
   
    /**
     * @param converter
     */
    public WrappingIconController(ToStringConverter converter) {
        this(new RenderingLabelController(converter));
    }

    public void setWrappee(RenderingComponentController wrappee) {
        if (wrappee == null) {
            wrappee = new RenderingLabelController();
        }
        this.wrappee = wrappee;
        rendererComponent.setComponent(wrappee.getRendererComponent());
    }

    
    @Override
    public WrappingIconPanel getRendererComponent(CellContext context) {
        Object oldValue = adjustContextValue(context);
        wrappee.getRendererComponent(context);
        WrappingIconPanel panel = super.getRendererComponent(context);
        restoreContextValue(context, oldValue);
        return panel;
    }

    /**
     * 
     * @param context
     * @param oldValue
     */
    protected void restoreContextValue(CellContext context, Object oldValue) {
        context.value = oldValue;
    }

    /**
     * Replace the context's value with the userobject 
     * if it's a treenode. <p>
     * Subclasses may override but must guarantee to return the original 
     * value for restoring. 
     * 
     * @param context the context to adjust
     * @return the old context value
     */
    protected Object adjustContextValue(CellContext context) {
        Object oldValue = context.getValue();
        if (oldValue instanceof DefaultMutableTreeNode) {
            context.value = ((DefaultMutableTreeNode) oldValue).getUserObject();
        }
        return oldValue;
    }

    @Override
    protected void configureState(CellContext context) {
        rendererComponent.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected WrappingIconPanel createRendererComponent() {
        return new WrappingIconPanel();
    }

    @Override
    protected void format(CellContext context) {
        rendererComponent.setIcon(context.getIcon());
    }

    //----------------- implement RolloverController
    
    /**
     * {@inheritDoc}
     */
    public void doClick() {
        if (isEnabled()) {
            ((RolloverRenderer) wrappee).doClick(); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return (wrappee instanceof RolloverRenderer) && 
           ((RolloverRenderer) wrappee).isEnabled();
    }


    
}

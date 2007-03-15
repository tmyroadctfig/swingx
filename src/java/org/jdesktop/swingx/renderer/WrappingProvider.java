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
 * PENDING: ui specific focus rect variation (draw rect around icon) missing <p>
 * PENDING: custom icons missing
 */
public class WrappingProvider extends 
    ComponentProvider<WrappingIconPanel>  implements RolloverRenderer {

    protected ComponentProvider wrappee;

    public WrappingProvider() {
        this((ComponentProvider) null);
    }
    
    public WrappingProvider(ComponentProvider wrapper) {
        super();
        setWrappee(wrapper);
    }
   
    /**
     * @param converter
     */
    public WrappingProvider(StringValue converter) {
        this(new LabelProvider(converter));
    }

    public void setWrappee(ComponentProvider wrappee) {
        if (wrappee == null) {
            wrappee = new LabelProvider();
        }
        this.wrappee = wrappee;
        rendererComponent.setComponent(wrappee.rendererComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WrappingIconPanel getRendererComponent(CellContext context) {
        if (context != null) {
            Object oldValue = adjustContextValue(context);
            wrappee.getRendererComponent(context);
            WrappingIconPanel panel = super.getRendererComponent(context);
            restoreContextValue(context, oldValue);
            return panel;
        }
        return super.getRendererComponent(context);
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

//    /**
//     * @return
//     */
//    private boolean isBorderAroundIcon() {
//        return Boolean.TRUE.equals(UIManager.get("Tree.drawsFocusBorderAroundIcon"));
//    }

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

/*
 * Created on 08.01.2007
 *
 */
package org.jdesktop.swingx.renderer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.RolloverRenderer;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;


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
    
    /**
     * Instantiates a WrappingProvider with the given delegate
     * provider for the node content. If null, a default 
     * LabelProvider will be used.
     * 
     * @param delegate the provider to use as delegate
     */
    public WrappingProvider(ComponentProvider delegate) {
        super();
        setWrappee(delegate);
    }
   
    /**
     * Instantiates a WrappingProvider with default wrappee configured
     * with the given StringValue.
     * 
     * @param converter the StringValue to use in the wrappee.
     */
    public WrappingProvider(StringValue converter) {
        this(new LabelProvider(converter));
    }

    /**
     * Sets the given provider as delegate for the node content. 
     * If the delegate is null, a default LabelProvider is set.<p>
     * 
     *  PENDING: rename to setDelegate?
     *  
     * @param delegate the provider to use as delegate. 
     */
    public void setWrappee(ComponentProvider delegate) {
        if (delegate == null) {
            delegate = new LabelProvider();
        }
        this.wrappee = delegate;
        rendererComponent.setComponent(delegate.rendererComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WrappingIconPanel getRendererComponent(CellContext context) {
        if (context != null) {
            rendererComponent.setComponent(wrappee.rendererComponent);
            Object oldValue = adjustContextValue(context);
            WrappingIconPanel panel = super.getRendererComponent(context);
            wrappee.getRendererComponent(context);
            restoreContextValue(context, oldValue);
            return rendererComponent;
        }
        return super.getRendererComponent(context);
    }

    /**
     * Restores the context value to the old value.
     * 
     * @param context the CellContext to restore.
     * @param oldValue the value to restore the context to.
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
        } else if (oldValue instanceof AbstractMutableTreeTableNode) {
            AbstractMutableTreeTableNode node = (AbstractMutableTreeTableNode) oldValue;
            context.value = node.getUserObject();
            
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

    /**
     * {@inheritDoc} <p>
     * 
     * Here: implemented to set the icon.
     */
    @Override
    protected void format(CellContext context) {
        rendererComponent.setIcon(getValueAsIcon(context));
    }

    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to fallback to the default icons supplied by the 
     * context if super returns null.
     * 
     * PENDING: make fallback configurable - null icons might be
     *   valid.
     *   
     */
    @Override
    protected Icon getValueAsIcon(CellContext context) {
        Icon icon = super.getValueAsIcon(context);
        if (icon == null) {
            return context.getIcon();
        }
        return icon;
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

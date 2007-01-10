/*
 * Created on 08.01.2007
 *
 */
package org.jdesktop.swingx.renderer;

import javax.swing.BorderFactory;


/**
 * Wrapping controller for usage in tree renderers. Handles the icon, delegates the value to 
 * the wrappee. <p>
 * 
 * PENDING: slight layout problem (one-pixel jumping on selection)<p>
 * PENDING: focus rect missing <p>
 * PENDING: ui specific focus rect variations not yet done <p>
 */
public class WrappingIconController extends 
    RenderingComponentController<WrappingIconPanel>  {

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
        wrappee.getRendererComponent(context);
        return super.getRendererComponent(context);
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


    
}

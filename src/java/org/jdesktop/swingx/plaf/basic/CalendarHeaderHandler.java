/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;

/**
 * Provides and wires a component appropriate as a calendar navigation header.
 * 
 * 
 * @author Jeanette Winzenburg
 */
abstract class CalendarHeaderHandler {

    JXMonthView monthView;
    protected JComponent calendarHeader;
    private Icon monthDownImage;
    private Icon monthUpImage;
    
    public void install(JXMonthView monthView) {
        this.monthView = monthView;
        calendarHeader = createCalendarHeader();
        calendarHeader.setFont(getAsNotUIResource(createDerivedFont()));
        calendarHeader.setBackground(getAsNotUIResource(monthView.getMonthStringBackground()));
        // PENDING JW: remove here if rendererHandler takes over control completely
        // as is, some properties are duplicated
        monthDownImage = UIManager.getIcon("JXMonthView.monthDownFileName");
        monthUpImage = UIManager.getIcon("JXMonthView.monthUpFileName");
        installZoomActions();
    }
    
    
    public void uninstall(JXMonthView monthView) {
          monthView.remove(getHeaderComponent());
    }
    
    public JComponent getHeaderComponent(){
        return calendarHeader;
    }
    
    protected abstract JComponent createCalendarHeader();
    
    /**
     * 
     */
    private void installZoomActions() {
        ZoomOutAction zoomOutAction = new ZoomOutAction();
        zoomOutAction.setTarget(monthView);
        monthView.getActionMap().put("zoomOut", zoomOutAction);
        AbstractActionExt prev = new AbstractActionExt(null, monthDownImage) {

            public void actionPerformed(ActionEvent e) {
                previousMonth();
            }
            
        };
        monthView.getActionMap().put("scrollToPreviousMonth", prev);
        AbstractActionExt next = new AbstractActionExt(null, monthUpImage) {

            public void actionPerformed(ActionEvent e) {
                nextMonth();
            }
            
        };
        monthView.getActionMap().put("scrollToNextMonth", next);
    }

    private void nextMonth() {
        Date upperBound = monthView.getUpperBound();
        if (upperBound == null
                || upperBound.after(monthView.getLastDisplayedDay()) ){
            Calendar cal = monthView.getCalendar();
            cal.add(Calendar.MONTH, 1);
            monthView.setFirstDisplayedDay(cal.getTime());
        }
    }

    private void previousMonth() {
        Date lowerBound = monthView.getLowerBound();
        if (lowerBound == null
                || lowerBound.before(monthView.getFirstDisplayedDay())){
            Calendar cal = monthView.getCalendar();
            cal.add(Calendar.MONTH, -1);
            monthView.setFirstDisplayedDay(cal.getTime());
        }
    }

    /**
     * Returns a Font based on the param which is not of type UIResource. 
     * 
     * @param font the base font
     * @return a font not of type UIResource, may be null.
     */
    private Font getAsNotUIResource(Font font) {
        if (!(font instanceof UIResource)) return font;
        // PENDING JW: correct way to create another font instance?
       return font.deriveFont(font.getAttributes());
    }
    
    /**
     * Returns a Color based on the param which is not of type UIResource. 
     * 
     * @param color the base color
     * @return a color not of type UIResource, may be null.
     */
    private Color getAsNotUIResource(Color color) {
        if (!(color instanceof UIResource)) return color;
        // PENDING JW: correct way to create another color instance?
        float[] rgb = color.getRGBComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], rgb[3]);
    }


    /**
     * Quick fix for Issue #1046-swingx: header text not updated if zoomable.
     * 
     */
    protected static class ZoomOutAction extends AbstractHyperlinkAction<JXMonthView> {

        private PropertyChangeListener linkListener;
        // Formatters/state used by Providers. 
        /** Localized month strings used in title. */
        private String[] monthNames;
        private StringValue tsv ;

        public ZoomOutAction() {
            super();
            tsv = new StringValue() {
                
                public String getString(Object value) {
                    if (value instanceof Calendar) {
                        String month = monthNames[((Calendar) value)
                                                  .get(Calendar.MONTH)];
                        return month + " "
                        + ((Calendar) value).get(Calendar.YEAR); 
                    }
                    return StringValues.TO_STRING.getString(value);
                }
                
            };
        }
        
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            
        }

        
        /**
         * installs a propertyChangeListener on the target and
         * updates the visual properties from the target.
         */
        @Override
        protected void installTarget() {
            if (getTarget() != null) {
                getTarget().addPropertyChangeListener(getTargetListener());
            }
            updateLocale();
            updateFromTarget();
        }

        /**
         * 
         */
        private void updateLocale() {
            Locale current = getTarget() != null ? getTarget().getLocale() : Locale.getDefault();
            monthNames = new DateFormatSymbols(current).getMonths();
        }

        /**
         * removes the propertyChangeListener. <p>
         * 
         * Implementation NOTE: this does not clean-up internal state! There is
         * no need to because updateFromTarget handles both null and not-null
         * targets. Hmm...
         * 
         */
        @Override
        protected void uninstallTarget() {
            if (getTarget() == null) return;
            getTarget().removePropertyChangeListener(getTargetListener());
        }

        protected void updateFromTarget() {
            // this happens on construction with null target
            if (tsv == null) return;
            Calendar calendar = getTarget() != null ? getTarget().getCalendar() : null;
            setName(tsv.getString(calendar));
        }

        private PropertyChangeListener getTargetListener() {
            if (linkListener == null) {
             linkListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if ("firstDisplayedDay".equals(evt.getPropertyName())) {
                        updateFromTarget();
                    } else if ("locale".equals(evt.getPropertyName())) {
                        updateLocale();
                        updateFromTarget();
                    }
                }
                
            };
            }
            return linkListener;
        }

        
    }
    /**
     * Create a derived font used to when painting various pieces of the
     * month view component.  This method will be called whenever
     * the font on the component is set so a new derived font can be created.
     * @deprecated KEEP re-added usage in preliminary zoomable support
     *    no longer used in paint/layout with renderer.
     */
    @Deprecated
    protected Font createDerivedFont() {
        return monthView.getFont().deriveFont(Font.BOLD);
    }


}

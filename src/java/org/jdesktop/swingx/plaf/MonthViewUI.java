package org.jdesktop.swingx.plaf;

import java.util.Date;

import javax.swing.plaf.ComponentUI;

public abstract class MonthViewUI extends ComponentUI {

    
    /**
     * Return a the date at the specified x/y position.
     * The date represents a day in the calendar's coordinate system. 
     *
     * @param x X position
     * @param y Y position
     * @return The date at the given location or null if the the position
     *   doesn't contain a Day.
     */ 
    public abstract Date getDayAtLocation(int x, int y);

    


    /**
     * Returns the last possible date that can be displayed.
     * This is implemented by the UI since it is in control of layout
     * and may possibly yeild different results based on implementation. <p>
     * 
     * It's up to the UI to keep this property, based on internal state and
     * the firstDisplayed as controlled by the JXMonthView.
     * 
     * @return Date The date.
     */
    public abstract Date getLastDisplayedDay();
    
//------------------------- deprecated
    
    /**
     * Calculate the last possible date that can be displayed.
     * This is implemented by the UI since it is in control of layout
     * and may possibly yeild different results based on implementation.
     * 
     * @return long The date.
     * 
     * @deprecated use getLastDisplayedDate() instead
     * 
     */
    public abstract long calculateLastDisplayedDate();
    
    /**
     * Return a long representing the date at the specified x/y position.
     * It is expected that the date returned will have a valid day, month
     * and year.  All other fields such as hour, minute, second and milli-
     * second will be set to 0.
     * @param x X position
     * @param y Y position
     * @return long The date, -1 if position does not contain a date.
     * 
     * @deprecated use {@link #getDayAtLocation(int, int)}
     */
    public abstract long getDayAt(int x, int y);

    /**
     * Returns the last possible date that can be displayed.
     * This is implemented by the UI since it is in control of layout
     * and may possibly yeild different results based on implementation. <p>
     * 
     * It's up to the UI to keep this property, based on internal state and
     * the firstDisplayed as controlled by the JXMonthView.
     * 
     * @return long The date.
     * 
     * @deprecated use {@link #getLastDisplayedDay()}
     */
    public abstract long getLastDisplayedDate();

}

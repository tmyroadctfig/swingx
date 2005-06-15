/*
 * Created on 10.06.2005
 *
 */
package org.jdesktop.swingx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.regex.Pattern;

/**
 * @author Jeanette Winzenburg
 */
public class PatternModel {

    private String rawText;

    private boolean backwards;

    private Pattern pattern;

    private int foundIndex;

    private boolean caseSensitive;

//    private boolean enabled;
//
//    private boolean active;
//    private boolean highlight;

    private PropertyChangeSupport propertySupport;


    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        boolean old = isCaseSensitive();
        this.caseSensitive = caseSensitive;
        updatePattern(caseSensitive);
        firePropertyChange("caseSensitive", old, isCaseSensitive());
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getFoundIndex() {
        return foundIndex;
    }

    public void setFoundIndex(int foundIndex) {
        int old = getFoundIndex();
        this.foundIndex = foundIndex;
        firePropertyChange("foundIndex", old, getFoundIndex());
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String findText) {
        String old = getRawText();
        this.rawText = findText;
        updatePattern(createRegEx(findText));
        firePropertyChange("rawText", old, getRawText());
    }

    /**
     * returns a regEx for compilation into a pattern. Here: either a "contains"
     * (== partial find) or null if the input was empty.
     * 
     * @param searchString
     * @return null if the input was empty, or a regex according to the internal
     *         rules
     */
    private String createRegEx(String searchString) {
        if (isEmpty(searchString))
            return ".*";
        return ".*" + searchString + ".*";
    }

    private boolean isEmpty(String text) {
        return (text == null) || (text.length() == 0);
    }

    private void updatePattern(String regEx) {
        Pattern old = getPattern();
        if (isEmpty(regEx)) {
            pattern = null;
        } else if ((old == null) || (!old.pattern().equals(regEx))) {
            pattern = Pattern.compile(regEx, getFlags());
        }
        firePropertyChange("pattern", old, getPattern());
    }

    private int getFlags() {
        return isCaseSensitive() ? 0 : getCaseInsensitiveFlag();
    }

    private int getCaseInsensitiveFlag() {
        return Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    }

    private void updatePattern(boolean caseSensitive) {
        if (pattern == null)
            return;
        Pattern old = getPattern();
        int flags = old.flags();
        int flag = getCaseInsensitiveFlag();
        if ((caseSensitive) && ((flags & flag) != 0)) {
            pattern = Pattern.compile(pattern.pattern(), 0);
        } else if (!caseSensitive && ((flags & flag) == 0)) {
            pattern = Pattern.compile(pattern.pattern(), flag);
        }
        firePropertyChange("pattern", old, getPattern());
    }

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        boolean old = isBackwards();
        this.backwards = backwards;
        firePropertyChange("backwards", old, isBackwards());
    }

//    public boolean isActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        if (!isEnabled())
//            return;
//        boolean old = isActive();
//        this.active = active;
//        firePropertyChange("active", old, isActive());
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        boolean old = isEnabled();
//        this.enabled = enabled;
//        firePropertyChange("enabled", old, isEnabled());
//    }
//    public boolean isHighlight() {
//        return highlight;
//    }
//
//    public void setHighlight(boolean highlight) {
//        boolean old = isHighlight();
//        this.highlight = highlight;
//        firePropertyChange("highlight", old, isHighlight());
//    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (propertySupport == null) {
            propertySupport = new PropertyChangeSupport(this);
        }
        propertySupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertySupport == null)
            return;
        propertySupport.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String name, Object oldValue,
            Object newValue) {
        if (propertySupport == null)
            return;
        propertySupport.firePropertyChange(name, oldValue, newValue);
    }

}

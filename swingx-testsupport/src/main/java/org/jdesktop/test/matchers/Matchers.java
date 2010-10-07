package org.jdesktop.test.matchers;

import static org.hamcrest.CoreMatchers.any;

import java.beans.PropertyChangeEvent;

import org.hamcrest.Matcher;

public final class Matchers {
    public static Matcher<PropertyChangeEvent> anyProperty() {
        return any(PropertyChangeEvent.class);
    }
    
    public static Matcher<PropertyChangeEvent> propertyWithName(String propertyName) {
        return new PropertyChangeEventMatcher(propertyName, null, null);
    }
    
    public static Matcher<PropertyChangeEvent> property(String propertyName, Object oldValue, Object newValue) {
        return new PropertyChangeEventMatcher(propertyName, oldValue, newValue);
    }
}

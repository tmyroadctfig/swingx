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

    /**
     * This matcher returns {@code true} when the arguments are equivalent.
     * <p>
     * For purposes of this matcher equivalence is as follows:
     * <ol>
     * <li>the objects are equal</li>
     * <li>the objects contain the same contents, insofar as the {@link java.beans.BeanInfo class
     * info} for each object instance returns equivalent values</li>
     * </ol>
     * 
     * @param <T>
     * @param object
     * @return
     */
    public static <T> Matcher<T> equivalentTo(T object) {
        return new EquivalentMatcher<T>(object);
    }
}

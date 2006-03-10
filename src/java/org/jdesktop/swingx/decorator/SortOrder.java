/*
 * Created on 10.03.2006
 *
 */
package org.jdesktop.swingx.decorator;

public final class SortOrder {
    public static final SortOrder ASCENDING = new SortOrder("ascending");
    public static final SortOrder DESCENDING = new SortOrder("descending");
    public static final SortOrder UNSORTED = new SortOrder("unsorted");

    private final String name;
    private SortOrder(String name) {
        this.name = name;
    }
    
    public boolean isSorted() {
        return this != UNSORTED;
    }
    
    public boolean isAscending() {
        return this == ASCENDING;
    }
    
    public String toString() {
        return name;
    }

}

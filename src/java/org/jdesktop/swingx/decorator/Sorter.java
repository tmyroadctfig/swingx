/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.text.Collator;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Pluggable sorting filter.
 *
 * @author Ramesh Gupta
 */
public abstract class Sorter extends Filter {
    private boolean	ascending = true;
    private final   Collator    collator;   // RG: compute this once
    private Comparator comparator;

    public Sorter() {
        this(0, true);
    }

    public Sorter(int col, boolean ascending) {
        this(col, ascending, null);
    }

    public Sorter(int col, boolean ascending, Comparator comparator) {
        super(col);
        this.comparator = comparator;
        setAscending(ascending);
        collator = Collator.getInstance();
    }

    /**
     * set the Comparator to use when comparing values.
     * If not null every compare will be delegated to it.
     * If null the compare will follow the internal compare
     * (no contract, but implemented here as:
     * first check if the values are Comparable, if so
     * delegate, then compare the String representation)
     *
     * @param comparator
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
        refresh();
    }

    public Comparator getComparator() {
        return this.comparator;
    }

    /**
     * Adopts the row mappings of the specified sorter by cloning the mappings.
     *
     * @param oldSorter <code>Sorter</code> whose mappings are to be cloned
     */
	protected abstract void adopt(Sorter oldSorter);

    /**
     * Interposes this sorter between a filter pipeline and the component that
     * the pipeline is bound to, replacing oldSorter as the previously
     * interposed sorter. You should not have to call this method directly.
     * @todo Pass in just the ComponentAdapter, and add methods to that for
     * fetching the filter pipeline and old sorter, if any.
     *
     * @param filters
     * @param adapter
     * @param oldSorter
     */
    public void interpose(FilterPipeline filters, ComponentAdapter adapter,
                          Sorter oldSorter) {
        if (filters != null) {
            filters.setSorter(this);
        }
        adopt(oldSorter);
        assign(filters);
        assign(adapter);
        refresh(oldSorter == null);
    }


    public int compare(int row1, int row2) {
        int result = compare(row1, row2, getColumnIndex());
        return ascending ? result : -result;
    }

	/* Adapted from Phil Milne's TableSorter implementation.
        This implementation, however, is not coupled to TableModel in any way,
        and may be used with list models and other types of models easily. */

    private int compare(int row1, int row2, int col) {
        Object o1 = getInputValue(row1, col);
        Object o2 = getInputValue(row2, col);
        // If both values are null return 0
        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) { // Define null less than everything.
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        // JW: have to handle null first of all
        // Seemingly, Comparators are not required to handle null. Hmm...
        if (comparator != null) {
            return comparator.compare(o1, o2);
        }

        if (o1 instanceof Comparable) {
            Comparable c1 = (Comparable) o1;
            Comparable c2 = (Comparable) o2;
            return c1.compareTo(c2);
        }
        else if (o1 instanceof Boolean) {   // RG: Dead branch? A Boolean is also a Comparable!
            try {
                Boolean bool1 = (Boolean) o1;
                boolean b1 = bool1.booleanValue();
                Boolean bool2 = (Boolean) o2;
                boolean b2 = bool2.booleanValue();

                if (b1 == b2) {
                    return 0;
                }
                else if (b1) { // Define false < true
                    return 1;
                }
                else {
                    return -1;
                }
            }
            catch (ClassCastException ex) {
                System.out.println("Column class mismatch: " + o1.getClass() +
                                   " can't be compared to " + o2.getClass());
            }
        }
        else {
            return collator.compare(o1.toString(), o2.toString());
            //return o1.toString().compareTo(o2.toString());
        }

        return 0;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
        refresh();
    }

    public void toggle() {
        ascending = !ascending;
        refresh();
    }

}

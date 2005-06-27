/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Pluggable sorting filter.
 *
 * @author Ramesh Gupta
 */
public abstract class Sorter extends Filter {
    private boolean	ascending = true;
    // JW: need to be updated if default locale changed
    private Collator    collator;   // RG: compute this once
    private Locale currentLocale;
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
    }

    /** 
     * Subclasses must call this before filtering to guarantee the
     * correct collator!
     */
    protected void refreshCollator() {
        if (!Locale.getDefault().equals(currentLocale)) {
            currentLocale = Locale.getDefault();
            collator = Collator.getInstance();
        }
    }

    /**
     * exposed for testing only!
     * @return
     */
    protected Collator getCollator() {
        return collator;
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

        // make sure we use the collator for string compares
        if ((o1.getClass() == String.class) && (o2.getClass() == String.class)) {
            return collator.compare((String)o1, (String) o2);
        }
        
        if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
            Comparable c1 = (Comparable) o1;
            Comparable c2 = (Comparable) o2;
            try {
                return c1.compareTo(o2);
            } catch (ClassCastException ex) {
                // comparables with different types
            }
        }
        
        return collator.compare(o1.toString(), o2.toString());
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

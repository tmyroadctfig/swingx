/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

/**
 * Pluggable sorting filter.
 *
 * @author Ramesh Gupta
 */
public class ShuttleSorter extends Sorter {
    private int[]	toPrevious;

    public ShuttleSorter() {
        this(0, true);
    }

    public ShuttleSorter(int col, boolean ascending) {
        super(col, ascending);
    }

    protected void init() {
        toPrevious = new int[0];
    }

    /**
     * Adopts the row mappings of the specified sorter by cloning the mappings.
     *
     * @param oldSorter <code>Sorter</code> whose mappings are to be cloned
     */
    protected void adopt(Sorter oldSorter) {
        if (oldSorter != null) {
            toPrevious = (int[]) ((ShuttleSorter) oldSorter).toPrevious.clone();
            /** @todo shouldn't cast */
            fromPrevious = (int[]) ((ShuttleSorter) oldSorter).fromPrevious.clone();
            /** @todo shouldn't cast */
        }
    }

    /**
     * Resets the internal row mappings from this filter to the previous filter.
     */
    protected void reset() {
        int inputSize = getInputSize();
        toPrevious = new int[inputSize];
        fromPrevious = new int[inputSize];
        for (int i = 0; i < inputSize; i++) {
            toPrevious[i] = i;	// reset before sorting
        }
    }

    /**
     * Performs the sort.
     */
    protected void filter() {
        refreshCollator();
        sort((int[]) toPrevious.clone(), toPrevious, 0, toPrevious.length);
        // Generate inverse map for implementing convertRowIndexToView();
        for (int i = 0; i < toPrevious.length; i++) {
            fromPrevious[toPrevious[i]] = i;
        }
    }

    public int getSize() {
        return toPrevious.length;
    }

    protected int mapTowardModel(int row) {
        return toPrevious[row];
    }

// Adapted from Phil Milne's TableSorter implementation.
// This implementation, however, is not coupled to TableModel in any way,
// and may be used with list models and other types of models easily.

// This is a home-grown implementation which we have not had time
// to research - it may perform poorly in some circumstances. It
// requires twice the space of an in-place algorithm and makes
// NlogN assigments shuttling the values between the two
// arrays. The number of compares appears to vary between N-1 and
// NlogN depending on the initial order but the main reason for
// using it here is that, unlike qsort, it is stable.
    protected void sort(int from[], int to[], int low, int high) {
        if (high - low < 2) {
        //    System.out.println("low:"+low+"; high:"+high);
            return;
        }
        int middle = (low + high) >> 1;

        sort(to, from, low, middle);
        sort(to, from, middle, high);

        int p = low;
        int q = middle;

        /* This is an optional short-cut; at each recursive call,
         check to see if the elements in this subset are already
         ordered.  If so, no further comparisons are needed; the
         sub-array can just be copied.  The array must be copied rather
         than assigned otherwise sister calls in the recursion might
         get out of sinc.  When the number of elements is three they
         are partitioned so that the first set, [low, mid), has one
         element and and the second, [mid, high), has two. We skip the
         optimisation when the number of elements is three or less as
         the first compare in the normal merge will produce the same
         sequence of steps. This optimisation seems to be worthwhile
         for partially ordered lists but some analysis is needed to
         find out how the performance drops to Nlog(N) as the initial
         order diminishes - it may drop very quickly.  */

        if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }
            return;
        }

        // A normal merge.

        for (int i = low; i < high; i++) {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            }
            else {
                to[i] = from[q++];
            }
        }
    }
}

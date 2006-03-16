/*
 * Created on 15.03.2006
 *
 */
package org.jdesktop.swingx.decorator;

import java.util.Comparator;
import java.util.List;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

/**
 * Defines a sorting strategy for a table. Usually {@link JXTable} will sort in
 * the view using its default {@link Sorter} class, which is the default
 * implementation of this interface. To sort data externally, implement this
 * interface and set the {@link JXTableHeader}'s <code>RowSorter</code> to your
 * implementation of {@link SortController}, which will receive notification when the
 * table's headers are clicked on.
 *
 * <p>This interface is inspired by a Java 1.6 class of the same name. This
 * class manages a table's sort order and responds to changes in the requested
 * sort order by changing the underlying model.
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public interface SortController {

    /**
     * Toggle the specified column, if its already sorted reverse its
     * order. If it's not already sorted, sort this column.
     *
     * @param column the model index of the column to toggle
     */
    void toggleSortOrder(int column);

    /**
     * Toggle the specified column, if its already sorted reverse its
     * order. If it's not already sorted, sort this column.
     *
     * @param column the model index of the column to toggle
     * @param comparator the comparator to use
     */
    void toggleSortOrder(int column, Comparator comparator);
    
    /**
     * Set the sort order by column.
     */
    void setSortKeys(List<? extends SortKey> keys);

    /**
     * List the sort order by column.
     */
    List<? extends SortKey> getSortKeys();

    /**
     * Get the sort order of the specified column.
     * 
     * @return one of {@link SortOrder#ASCENDING},
     *     {@link SortOrder#DESCENDING} or {@link SortOrder#UNSORTED}.
     */
    SortOrder getSortOrder(int column);


}

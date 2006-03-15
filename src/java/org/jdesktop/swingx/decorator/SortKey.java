/*
 * Created on 15.03.2006
 *
 */
package org.jdesktop.swingx.decorator;

/**
 * A column and how its sorted.
 */
public final class SortKey {
    private final SortOrder sortOrder;
    private final int column;

    /**
     * @param sortOrder one of {@link SortOrder#ASCENDING},
     *     {@link SortOrder#DESCENDING} or {@link SortOrder#UNSORTED}.
     * @param column a column in terms of <strong>model</strong> index.
     */
    public SortKey(SortOrder sortOrder, int column) {
        if(sortOrder == null) throw new IllegalArgumentException();
        if(column < 0) throw new IllegalArgumentException();

        this.sortOrder = sortOrder;
        this.column = column;
    }

    /**
     * The sort order, ascending, descending or unsorted.
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * The sorting column in terms of <strong>model</strong> index.
     */
    public int getColumn() {
        return column;
    }

    /** {@inheritDoc} */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SortKey sortKey = (SortKey) o;

        if (column != sortKey.column) return false;
        if (sortOrder != null ? !sortOrder.equals(sortKey.sortOrder) : sortKey.sortOrder != null) return false;

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result;
        result = (sortOrder != null ? sortOrder.hashCode() : 0);
        result = 29 * result + column;
        return result;
    }
}

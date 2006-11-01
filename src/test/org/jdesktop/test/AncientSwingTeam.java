package org.jdesktop.test;

import javax.swing.table.AbstractTableModel;

/**
 * Standard Table with class-Infos. Taken from some old
 * SwingSet... 
 * Can remove/add rows.
 */
public class AncientSwingTeam extends AbstractTableModel {

    protected final String[] names = { "First Name", "Last Name", "Favorite Color",
            "No.", "Vegetarian" };

    protected final Object[][] data = {
            { "Mark", "Andrews", "Red", new Integer(2), new Boolean(true) },
            { "Tom", "Ball", "Blue", new Integer(99), new Boolean(false) },
            { "Alan", "Chung", "Green", new Integer(838), new Boolean(false) },
            { "Jeff", "Dinkins", "Turquois", new Integer(8), new Boolean(true) },
            { "Amy", "Fowler", "Yellow", new Integer(3), new Boolean(false) },
            { "Brian", "Gerhold", "Green", new Integer(0), new Boolean(false) },
            { "James", "Gosling", "Pink", new Integer(21), new Boolean(false) },
            { "David", "Karlton", "Red", new Integer(1), new Boolean(false) },
            { "Dave", "Kloba", "Yellow", new Integer(14), new Boolean(false) },
            { "Peter", "Korn", "Purple", new Integer(12), new Boolean(false) },
            { "Phil", "Milne", "Purple", new Integer(3), new Boolean(false) },
            { "Dave", "Moore", "Green", new Integer(88), new Boolean(false) },
            { "Hans", "Muller", "Maroon", new Integer(5), new Boolean(false) },

            { "Rick", "Levenson", "Blue", new Integer(2), new Boolean(false) },
            { "Tim", "Prinzing", "Blue", new Integer(22), new Boolean(false) },
            { "Chester", "Rose", "Black", new Integer(0), new Boolean(false) },
            { "Ray", "Ryan", "Gray", new Integer(77), new Boolean(false) },
            { "Georges", "Saab", "Red", new Integer(4), new Boolean(false) },
            { "Willie", "Walker", "Phthalo Blue", new Integer(4),
                    new Boolean(false) },

            { "Kathy", "Walrath", "Blue", new Integer(8), new Boolean(false) },
            { "Arnaud", "Weber", "Green", new Integer(44), new Boolean(false) } };

    protected int rowCount = data.length;

    public AncientSwingTeam() {

    }

     public AncientSwingTeam(int count) {
         rowCount = count;
     }

    public int getColumnCount() {
        return names.length;
    }

    public int getRowCount() {
        return rowCount;
    }

    /** reuses values internally */
    public Object getValueAt(int row, int col) {
        // following shows only every second value
        // if ((row + col) % 2 == 0) return null;
        return data[row % data.length][col];
    }

    public void setValueAt(Object value, int row, int col) {
        data[row % data.length][col] = value;
        fireTableCellUpdated(row, col);
    }

    // The default implementations of these methods in
    // AbstractTableModel would work, but we can refine them.
    public String getColumnName(int column) {
        return names[column];
    }

    /** returns class of column by asking class of value in first row. */
    public Class getColumnClass(int c) {
        Object value = null;
        if (getRowCount() > 0) {
            value = getValueAt(0, c);
        }
        if (value == null) {
            return Object.class;
        }
        return value.getClass();
    }

    /** everything is editable. */
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * insert length rows at rowIndex. PRE: rowIndex <= getRowCount()
     */
    public void insertRows(int rowIndex, int length) {
        rowCount += length;
        fireTableRowsInserted(rowIndex, rowIndex + length - 1);
    }

    /**
     * remove rows. NOTE: not tested
     */
    public void removeRows(int rowIndex, int length) {
        rowCount -= length;
        if (rowCount < 0) {
            length -= rowCount;
            rowCount = 0;
        }
        fireTableRowsDeleted(rowIndex, rowIndex + length - 1);
    }

} // end class SwingTeam

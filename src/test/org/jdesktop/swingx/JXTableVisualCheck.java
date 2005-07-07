/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * Split from old JXTableUnitTest - contains "interactive"
 * methods only.
 * 
 * @author Jeanette Winzenburg, Berlin
 */
public class JXTableVisualCheck extends JXTableUnitTest {
    
    /**
     * Issue #189, #214: Sorter fails if content is 
     * comparable with mixed types
     *
     */
    public void testDummy() {
    }   
    
    /**
     * example mixed sorting (Jens Elkner).
     *
     */
    public void interactiveTestSorterPatch() {
        Object[][] fourWheels = new Object[][]{
             new Object[] {"Car", new Car(180f)},
             new Object[] {"Porsche", new Porsche(170)}, 
             new Object[] {"Porsche", new Porsche(170)}, 
             new Object[] {"Porsche", new Porsche(170, false)}, 
             new Object[] {"Tractor", new Tractor(20)},
             new Object[] {"Tractor", new Tractor(10)},

        };
        DefaultTableModel model = new DefaultTableModel(fourWheels, new String[] {"Text", "Car"}) ;
        JXTable table = new JXTable(model);
        JFrame frame = wrapWithScrollingInFrame(table, "Sorter patch");
        frame.setVisible(true);
        
    
    }
    
    public class Car implements Comparable<Car> {
        float speed = 100;
        public Car(float speed) { this.speed = speed; }
        public int compareTo(Car o) {
            return speed < o.speed ? -1 : speed > o.speed ? 1 : 0;
        }
        public String toString() {
            return "Car - " + speed;
        }
    }
    public class Porsche extends Car {
        boolean hasBridgeStone = true;
        public Porsche(float speed) { super(speed); }
        public Porsche(float speed, boolean bridgeStone) { 
            this(speed); 
            hasBridgeStone = bridgeStone;
        }
        public int compareTo(Car o) {
            if (o instanceof Porsche) {
                return ((Porsche) o).hasBridgeStone ? 0 : 1; 
            }
            return super.compareTo(o);
        }
        public String toString() {
            return "Porsche - " + speed + (hasBridgeStone ? "+" : "");
        }
    }
    
    public class Tractor implements Comparable<Tractor> {
        float speed = 20;
        public Tractor(float speed) { this.speed = speed; }
        public int compareTo(Tractor o) {
            return speed < o.speed ? -1 : speed > o.speed ? 1 : 0;
        }
        public String toString() {
            return "Tractor - " + speed;
        }
    }

    
    
    /**
     * Issue #179: Sorter does not use collator if cell content is
     *  a String.
     *
     */
    public void interactiveTestLocaleSorter() {
        
        Object[][] rowData = new Object[][] {
                new Object[] { Boolean.TRUE, "aa" },
                new Object[] { Boolean.FALSE, "AB" },
                new Object[] { Boolean.FALSE, "AC" },
                new Object[] { Boolean.TRUE, "BA" },
                new Object[] { Boolean.FALSE, "BB" },
                new Object[] { Boolean.TRUE, "BC" } };
        String[] columnNames = new String[] { "Critical", "Task" };
        DefaultTableModel model =  new DefaultTableModel(rowData, columnNames);
//        {
//            public Class getColumnClass(int column) {
//                return column == 1 ? String.class : super.getColumnClass(column);
//            }
//        };
        final JXTable table = new JXTable(model);
        table.setSorter(1);
        JFrame frame = wrapWithScrollingInFrame(table, "locale sorting");
        frame.setVisible(true);
    }   
    
    /** 
     * Issue #??: Problems with filters and ColumnControl
     * 
     * - sporadic ArrayIndexOOB after sequence:
     * filter(column), sort(column), hide(column), setFilter(null)
     * 
     * - filtering invisible columns? Unclear state transitions.
     *
     */
    public void interactiveTestColumnControlAndFilters() {
        final JXTable table = new JXTable(sortableTableModel);
        // hmm bug regression with combos as editors - same in JTable
//        JComboBox box = new JComboBox(new Object[] {"one", "two", "three" });
//        box.setEditable(true);
//        table.getColumnExt(0).setCellEditor(new DefaultCellEditor(box));
        Action toggleFilter = new AbstractAction("Toggle Filter") {
            
            public void actionPerformed(ActionEvent e) {
                if (table.getFilters() != null) {
                    table.setFilters(null);
                } else {
                    Filter filter = new PatternFilter(".*e.*", 0, 0);
                    table.setFilters(new FilterPipeline(new Filter[] {filter}));

                }
                
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }
 
    /** 
     * Issue ??: Column control on changing column model.
     *
     */
    public void interactiveTestColumnControlColumnModel() {
        final JXTable table = new JXTable(10, 5);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Set ColumnModel") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnModel(new DefaultTableColumnModel());
                table.setModel(sortableTableModel);
                setEnabled(false);
            }
            
        };
        JFrame frame = wrapWithScrollingInFrame(table, "ColumnControl: set columnModel -> core default");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    /** 
     * Issue ??: Column control on changing column model.
     *
     */
    public void interactiveTestColumnControlColumnModelExt() {
        final JXTable table = new JXTable();
        table.setColumnModel( new DefaultTableColumnModel());
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Set ColumnModelExt") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnModel(new DefaultTableColumnModelExt());
                table.setModel(sortableTableModel);
                table.getColumnExt(0).setVisible(false);
                setEnabled(false);
            }
            
        };
        JFrame frame = wrapWithScrollingInFrame(table, "ColumnControl: set ColumnModel -> modelExt");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    /** 
     * Issue #11: Column control not showing with few rows.
     *
     */
    public void interactiveTestColumnControlFewRows() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl with few rows");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     * check behaviour outside scrollPane
     *
     */
    public void interactiveTestColumnControlWithoutScrollPane() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        toggleAction.putValue(Action.SHORT_DESCRIPTION, "does nothing visible - no scrollpane");
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        JFrame frame = wrapInFrame(table, "JXTable: Toggle ColumnControl outside ScrollPane");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     * check behaviour of moving into/out of scrollpane.
     *
     */
    public void interactiveTestToggleScrollPaneWithColumnControlOn() {
        final JXTable table = new JXTable();
        table.setModel(new DefaultTableModel(10, 5));
        table.setColumnControlVisible(true);
        final JFrame frame = wrapInFrame(table, "JXTable: Toggle ScrollPane with Columncontrol on");
        Action toggleAction = new AbstractAction("Toggle ScrollPane") {

            public void actionPerformed(ActionEvent e) {
                Container parent = table.getParent();
                boolean inScrollPane = parent instanceof JViewport;
                if (inScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) table.getParent().getParent();
                    frame.getContentPane().remove(scrollPane);
                    frame.getContentPane().add(table);
                } else {
                  parent.remove(table);
                  parent.add(new JScrollPane(table));
                }
                frame.pack();
                              
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /** 
     *  TableColumnExt: user friendly resizable  
     * 
     */
    public void interactiveTestColumnResizable() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        int totalColumnCount = table.getColumnCount();
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        JFrame frame = wrapWithScrollingInFrame(table, "JXTable: Column with Min=Max not resizable");
        Action action = new AbstractAction("Toggle MinMax of FirstName") {

            public void actionPerformed(ActionEvent e) {
                // user-friendly resizable flag
                if (priorityColumn.getMinWidth() == priorityColumn.getMaxWidth()) {
                    priorityColumn.setMinWidth(50);
                    priorityColumn.setMaxWidth(150);
                } else {
                    priorityColumn.setMinWidth(100);
                    priorityColumn.setMaxWidth(100);
                }
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
    }
//---------------------------------

    
    public void interactiveTestRolloverHighlight() {
        JXTable table = new JXTable(sortableTableModel);
        table.setRolloverEnabled(true);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] 
            {new RolloverHighlighter(Color.YELLOW, null)} ));
        JFrame frame = wrapWithScrollingInFrame(table, "rollover highlight");
        frame.setVisible(true);

    }

    /**
     * Issue #31 (swingx): clicking header must not sort if table !enabled.
     *
     */
    public void interactiveTestDisabledTableSorting() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Toggle Enabled") {

            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                
            }
            
        };
        JFrame frame = wrapWithScrollingInFrame(table, "Disabled tabled: no sorting");
        addAction(frame, toggleAction);
        frame.setVisible(true);  
    }


    
    /**
     * Issue #191: sorting and custom renderer
     * not reproducible ...
     *
     */
    public void interactiveTestCustomRendererSorting() {
        JXTable table = new JXTable(sortableTableModel);
        TableColumn column = table.getColumn("No.");
        TableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                value = "# " + value ;
                Component comp = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row,  col );
                return comp;
            }
        };
        column.setCellRenderer(renderer);
        JFrame frame = wrapWithScrollingInFrame(table, "RendererSortingTest");
        frame.setVisible(true);  // RG: Changed from deprecated method show();
    }

    /**
     */
    public void interactiveTestToggleSortingEnabled() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Action toggleSortableAction = new AbstractAction("Toggle Sortable") {

            public void actionPerformed(ActionEvent e) {
                table.setSortable(!table.isSortable());
                
            }
            
        };
        JFrame frame = wrapWithScrollingInFrame(table, "ToggleSortingEnabled Test");
        addAction(frame, toggleSortableAction);
        frame.setVisible(true);  // RG: Changed from deprecated method show();
        
    }
    public void interactiveTestTableSizing1() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnExt columns[] = new TableColumnExt[tableModel.getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumnExt(i);
            table.addColumn(columns[i]);
        }
        columns[0].setPrototypeValue(new Integer(0));
        columns[1].setPrototypeValue("Simple String Value");
        columns[2].setPrototypeValue(new Integer(1000));
        columns[3].setPrototypeValue(Boolean.TRUE);
        columns[4].setPrototypeValue(new Date(100));
        columns[5].setPrototypeValue(new Float(1.5));
        columns[6].setPrototypeValue(new LinkModel("Sun Micro", "_blank",
                                              tableModel.linkURL));
        columns[7].setPrototypeValue(new Integer(3023));
        columns[8].setPrototypeValue("John Doh");
        columns[9].setPrototypeValue("23434 Testcase St");
        columns[10].setPrototypeValue(new Integer(33333));
        columns[11].setPrototypeValue(Boolean.FALSE);

        table.setVisibleRowCount(12);

        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestEmptyTableSizing() {
        JXTable table = new JXTable(0, 5);
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(table, "Empty Table (0 rows)");
        frame.setVisible(true);
        
    }
    public void interactiveTestTableSizing2() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);

        TableColumnExt columns[] = new TableColumnExt[6];
        int viewIndex = 0;
        for (int i = columns.length - 1; i >= 0; i--) {
            columns[viewIndex] = new TableColumnExt(i);
            table.addColumn(columns[viewIndex++]);
        }
        columns[5].setHeaderValue("String Value");
        columns[5].setPrototypeValue("9999");
        columns[4].setHeaderValue("String Value");
        columns[4].setPrototypeValue("Simple String Value");
        columns[3].setHeaderValue("Int Value");
        columns[3].setPrototypeValue(new Integer(1000));
        columns[2].setHeaderValue("Bool");
        columns[2].setPrototypeValue(Boolean.FALSE);
        //columns[2].setSortable(false);
        columns[1].setHeaderValue("Date");
        columns[1].setPrototypeValue(new Date(0));
        //columns[1].setSortable(false);
        columns[0].setHeaderValue("Float");
        columns[0].setPrototypeValue(new Float(5.5));

        table.setRowHeight(24);
        table.setRowMargin(2);
        JFrame frame = wrapWithScrollingInFrame(table, "TableSizing2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setRowMargin(1);

        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true) // column 0, ascending
        }));

        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.
            linePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableAlternateRowHighlighter1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateRowHighlighter2() {
        JXTable table = new JXTable(tableModel);
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(1, false), // column 1, descending
        }));

        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.classicLinePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableAlternateRowHighlighter2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter1() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.notePadBackground,
        }));
        table.setBackground(new Color(0xFF, 0xFF, 0xCC)); // notepad
        table.setGridColor(Color.cyan.darker());
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setShowHorizontalLines(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true), // column 0, ascending
                                            new ShuttleSorter(1, true), // column 1, ascending
        }));

        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter1 col 0= asc, col 1 = asc");
        frame.setVisible(true);

    }
    
    public void interactiveTestTableSorter2() {
        JXTable table = new JXTable(sortableTableModel);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        table.setGridColor(Color.cyan.darker());
        table.setRowHeight(22);
        table.setRowMargin(1);
        table.setShowHorizontalLines(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(0, true), // column 0, ascending
                                            new ShuttleSorter(1, false), // column 1, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter2 col 0 = asc, col 1 = desc");
        frame.setVisible(true);
    }
    
    public void interactiveTestFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JXTable xtable = new JXTable(model);
//        BasicLookAndFeel lf;
        xtable.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        JFrame frame = wrapWithScrollingInFrame(xtable, table, "Unselected focused background: JXTable/JTable");
        frame.setVisible(true);
    }


    public void interactiveTestTableSorter3() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new Highlighter(Color.orange, null),
        }));
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(1, true), // column 1, ascending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter3 col 1 = asc, col 0 = desc");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter4() {
        JXTable table = new JXTable(sortableTableModel);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new Highlighter(Color.orange, null),
        }));
        table.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(0, false), // column 0, descending
                                            new ShuttleSorter(1, true), // column 1, ascending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter4 col 0 = des, col 1 = asc");
        frame.setVisible(true);
    }
    
    public void interactiveTestTablePatternFilter1() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("A.*", 0, 1)
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter2() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(2, 2));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter3() {
        JXTable table = new JXTable(tableModel);
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(1, false), // column 1, descending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter3 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter4() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(3, 3));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("A.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter4 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter5() {
        // **** IMPORTANT TEST CASE for interaction between ****
        // **** PatternFilter and PatternHighlighter!!! ****
        JXTable table = new JXTable(tableModel);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("S.*", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
                                            new ShuttleSorter(1, true), // column 1, ascending
                                            new ShuttleSorter(3, false), // column 3, descending
        }));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, "S.*", 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter5 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableViewProperties() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(table, "TableViewProperties Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternHighlighter() {
        JXTable table = new JXTable(tableModel);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        table.setRowHeight(0, 96);
        table.setShowGrid(true);
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, "A.*", 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternHighlighter Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableColumnProperties() {
        JXTable table = new JXTable();
        table.setModel(tableModel);

        table.getTableHeader().setBackground(Color.green);
        table.getTableHeader().setForeground(Color.magenta);
        table.getTableHeader().setFont(new Font("Serif", Font.PLAIN, 10));

        ColumnHeaderRenderer headerRenderer = ColumnHeaderRenderer.createColumnHeaderRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBackground(Color.blue);
        headerRenderer.setForeground(Color.yellow);
        headerRenderer.setIcon(new Icon() {
            public int getIconWidth() {
                return 12;
            }

            public int getIconHeight() {
                return 12;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(Color.red);
                g.fillOval(0, 0, 10, 10);
            }
        });
        headerRenderer.setIconTextGap(20);
        headerRenderer.setFont(new Font("Serif", Font.BOLD, 18));

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumnExt column = table.getColumnExt(i);
            if (i % 3 > 0) {
                column.setHeaderRenderer(headerRenderer);
            }
            if (i % 2 > 0) {
                TableCellRenderer cellRenderer =
                    table.getNewDefaultRenderer(table.getColumnClass(i));
                if (cellRenderer instanceof JLabel || cellRenderer instanceof AbstractButton) {
                    JComponent labelCellRenderer = (JComponent)cellRenderer;
                    labelCellRenderer.setBackground(Color.gray);
                    labelCellRenderer.setForeground(Color.red);
                    if (cellRenderer instanceof JLabel) {
                        ((JLabel) labelCellRenderer).setHorizontalAlignment(JLabel.CENTER);
                    } else {
                        ((AbstractButton) labelCellRenderer).setHorizontalAlignment(JLabel.CENTER);
                    }
                    column.setCellRenderer(cellRenderer);
                }
            }
        }

        JFrame frame = wrapWithScrollingInFrame(table, "TableColumnProperties Test");
        frame.setVisible(true);
    }


    public static void main(String args[]) {
        setSystemLF(false);
        JXTableVisualCheck test = new JXTableVisualCheck();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*ColumnControlColumnModel.*");
//            test.runInteractiveTests("interactive.*TableHeader.*");
        //    test.runInteractiveTests("interactive.*SorterP.*");
//            test.runInteractiveTests("interactive.*abled.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}

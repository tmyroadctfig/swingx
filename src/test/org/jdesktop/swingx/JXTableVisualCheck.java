/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable.NumberEditor;
import org.jdesktop.swingx.JXTableHeader.SortGestureRecognizer;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.action.LinkModelAction;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.SortController;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.test.AncientSwingTeam;

/**
 * Split from old JXTableUnitTest - contains "interactive"
 * methods only. <p>
 * 
 * PENDING: too many frames to fit all on screen - either split into different
 * tests or change positioning algo to start on top again if hidden. <p>
 * @author Jeanette Winzenburg
 */
public class JXTableVisualCheck extends JXTableUnitTest {
    private static final Logger LOG = Logger.getLogger(JXTableVisualCheck.class
            .getName());
    public static void main(String args[]) {
      JXTableVisualCheck test = new JXTableVisualCheck();
      try {
//        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*ColumnControl.*");
//          test.runInteractiveTests("interactive.*Header.*");
//          test.runInteractiveTests("interactive.*ColumnProp.*");
//          test.runInteractiveTests("interactive.*Multiple.*");
//          test.runInteractiveTests("interactive.*RToL.*");
//          test.runInteractiveTests("interactive.*Scrollable.*");
//          test.runInteractiveTests("interactive.*isable.*");
          
//          test.runInteractiveTests("interactive.*Policy.*");
//        test.runInteractiveTests("interactive.*Rollover.*");
//        test.runInteractiveTests("interactive.*Floating.*");
        test.runInteractiveTests("interactiveColumnHighlighting");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // super has LF specific tests...
        setSystemLF(true);
    }

    /**
     * Issue #675-swingx: esc doesn't reach rootpane.
     * 
     * Verify that the escape is intercepted only if editing.
     * BUT: (core behaviour) starts editing in table processKeyBinding. So every
     * second is not passed on.
     */
    public void interactiveDialogCancelOnEscape() {
        Action cancel = new AbstractActionExt("cancel") {

            public void actionPerformed(ActionEvent e) {
                LOG.info("performed: cancel action");
                
            }
            
        };
        final JButton field = new JButton(cancel);
        JXTable xTable = new JXTable(10, 3);
        JTable table = new JTable(xTable.getModel());
        JXFrame frame = wrapWithScrollingInFrame(xTable, table, "escape passed to rootpane (if editing)");
        frame.setCancelButton(field);
        frame.add(field, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    

    
    /**
     * Issue #508/547-swingx: clean up of pref scrollable.
     * Visual check: column init on model change.
     *
     */
     public void interactivePrefScrollable() {
        final DefaultTableModel tableModel = new DefaultTableModel(30, 7);
        final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
        final JXTable table = new JXTable(tableModel);
        table.setColumnControlVisible(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final JXFrame frame = showWithScrollingInFrame(table, "initial sizing");
        addMessage(frame, "initial size: " + table.getPreferredScrollableViewportSize());
        Action action = new AbstractActionExt("toggle model") {

            public void actionPerformed(ActionEvent e) {
                table.setModel(table.getModel() == tableModel ? ancientSwingTeam : tableModel);
                frame.pack();
            }
            
        };
        addAction(frame, action);
        frame.pack();
    }

     /**
     * Issue #508/547-swingx: clean up of pref scrollable.
      * Visual check: dynamic logical scroll sizes
      * Toggle visual row/column count.
      */
     public void interactivePrefScrollableDynamic() {
         final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
         final JXTable table = new JXTable(ancientSwingTeam);
         table.setColumnControlVisible(true);
         table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         final JXFrame frame = wrapWithScrollingInFrame(table, "Dynamic pref scrollable");
         Action action = new AbstractActionExt("vis row") {
             
             public void actionPerformed(ActionEvent e) {
                 int visRowCount = table.getVisibleRowCount() + 5;
                 if (visRowCount > 30) {
                     visRowCount = 10;
                 }
                 table.setVisibleRowCount(visRowCount);
                 frame.pack();
             }
             
         };
         addAction(frame, action);
         Action columnAction = new AbstractActionExt("vis column") {
             
             public void actionPerformed(ActionEvent e) {
                 int visColumnCount = table.getVisibleColumnCount();
                 if (visColumnCount > 8) {
                     visColumnCount = -1;
                 } else if (visColumnCount < 0 ) {
                     visColumnCount = 2;
                 } else {
                     visColumnCount += 2;
                 }
                 table.setVisibleColumnCount(visColumnCount);
                 frame.pack();
             }
             
         };
         addAction(frame, columnAction);
         frame.setVisible(true);
         frame.pack();
     }


    /**
     * Issue #445-swingx: sort icon not updated on programatic sorting.
     * The issue is the update (== repaint), so a non-visual test method
     * will always pass (the icon-border is set in getRendererComp).
     */
    public void interactiveHeaderUpdateOnSorting() {
        final JXTable table = new JXTable(createAscendingModel(0, 10));
        Action action = new AbstractActionExt("toggle sorter order") {

            public void actionPerformed(ActionEvent e) {
                table.toggleSortOrder(0);
                
            }
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "sort icon");
        addAction(frame, action);
        frame.setVisible(true);
    }
    

    /**
     * Issue #393-swingx: localized NumberEditor.
     * 
     * Playing ... looks working :-)
     *
     *  
     */
    public void interactiveFloatingPointEditor(){
        DefaultTableModel model = new DefaultTableModel(
                new String[] {"Double-core", "Double-ext", "Integer-core", "Integer-ext", "Object"}, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if ((columnIndex == 0) || (columnIndex == 1)) {
                    return Double.class;
                }
                if ((columnIndex == 2) || (columnIndex == 3)){
                    return Integer.class;
                }
                return Object.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setValueAt(10.2, 0, 0);
        table.setValueAt(10.2, 0, 1);
        table.setValueAt(10, 0, 2);
        table.setValueAt(10, 0, 3);
        
        NumberEditor numberEditor = new NumberEditor();
        table.getColumn(0).setCellEditor(numberEditor);
        table.getColumn(2).setCellEditor(numberEditor);
        showWithScrollingInFrame(table, "Extended NumberEditors (col 1/3)");
    }

    /**
     *  Issue #??-swingx: default number editor shows 3 digits only.
     *  
     *  Compare with plain JFromattedTextField and default NumberFormat - same. 
     *  To see, type a number with fractional digits > 3 in the first text field
     *  and press commit or transfer focus away. 
     */
    public void interactiveFloatingPointEditorDigits(){
        DefaultTableModel model = new DefaultTableModel(
                new String[] {"Double-default", "Double-customMaxDigits"}, 10) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if ((columnIndex == 0) || (columnIndex == 1)) {
                    return Double.class;
                }
                if ((columnIndex == 2) || (columnIndex == 3)){
                    return Integer.class;
                }
                return Object.class;
            }
            
        };
        final JXTable table = new JXTable(model);
        table.setSurrendersFocusOnKeystroke(true);
        table.setValueAt(10.2, 0, 0);
        table.setValueAt(10.2, 0, 1);
        NumberFormat moreFractionalDigits = NumberFormat.getInstance();
        moreFractionalDigits.setMaximumFractionDigits(20);
        NumberEditorExt numberEditor = new NumberEditorExt(moreFractionalDigits);
        table.getColumn(1).setCellEditor(numberEditor);
        JXFrame frame = showWithScrollingInFrame(table, "Extended NumberEditors (col 1/3)");
        Format format = NumberFormat.getInstance();
        final JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(10);
        final JFormattedTextField target = new JFormattedTextField(format);
        target.setColumns(10);
        field.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                target.setValue(field.getValue());
                LOG.info("value: " + field.getValue());
            }
            
        });
        FocusAdapter focusAdapter = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                LOG.info("field value: " + field.getValue());
                LOG.info("table value: " + table.getValueAt(0, 1));
            }
            
        };
        field.addFocusListener(focusAdapter);
        table.addFocusListener(focusAdapter);
        addStatusComponent(frame, field);
        addStatusComponent(frame, target);
    }
    /**
     * Issue #417-swingx: disable default find.
     *
     * Possible alternative to introducing disable api as suggested in the
     * issue report: disable the action? Move the action up the hierarchy to
     * the parent actionmap? Maybe JX specific parent?
     *  
     */
    public void interactiveDisableFind() {
        final JXTable table = new JXTable(sortableTableModel);
        Action findAction = new AbstractActionExt() {

            public void actionPerformed(ActionEvent e) {
                SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
                
            }
            
            @Override
            public boolean isEnabled() {
                return false;
            }
            
        };
        table.getActionMap().put("find", findAction);
        showWithScrollingInFrame(table, "disable finding");
    }
    
    /**
     * visually check if we can bind the CCB's action to a keystroke.
     * 
     * Working, but there's a visual glitch if opened by keystroke: 
     * the popup is not trailing aligned to the CCB. And the 
     * CCB must be visible, otherwise there's an IllegalStateException
     * because the popup tries to position itself relative to the CCB.
     *
     */
    public void interactiveKeybindingColumnControl() {
        JXTable table = new JXTable(sortableTableModel);
        // JW: currently the CCB must be visible
//        table.setColumnControlVisible(true);
        Action openCCPopup = ((AbstractButton) table.getColumnControl()).getAction();
        String actionKey = "popupColumnControl";
        table.getActionMap().put(actionKey, openCCPopup);
        KeyStroke keyStroke = KeyStroke.getKeyStroke("F2");
        table.getInputMap(JXTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, actionKey);
        showWithScrollingInFrame(table, "Press F2 to open column control");
    }
    /**
     * calculate reasonable table rowHeight withouth "white pixel" in editor.
     * Compare table and xtable
     */
    public void interactiveCompareRowHeight() {
        JXTable xtable = new JXTable(sortableTableModel);
        xtable.setShowGrid(false, false);
        JTable table = new JTable(sortableTableModel);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "compare default rowheight of xtable vs. table");
        frame.setVisible(true);
    }
    
    /**
     * visually check if terminateEditOnFocusLost, autoStartEdit
     * work as expected.
     *
     */
    public void interactiveToggleEditProperties() {
        final JXTable table = new JXTable(10, 2);
        JXFrame frame = wrapWithScrollingInFrame(table, new JButton("something to focus"), 
                "JXTable: toggle terminate/autoStart on left (right is dummy) ");
        Action toggleTerminate = new AbstractAction("toggleTerminate") {

            public void actionPerformed(ActionEvent e) {
                table.setTerminateEditOnFocusLost(!table.isTerminateEditOnFocusLost());
                
            }
            
        };
        addAction(frame, toggleTerminate);
        Action toggleAutoStart = new AbstractAction("toggleAutoStart") {

            public void actionPerformed(ActionEvent e) {
                table.setAutoStartEditOnKeyStroke(!table.isAutoStartEditOnKeyStroke());
                
            }
            
        };
        addAction(frame, toggleAutoStart);
        frame.setVisible(true);
        
    }
    
    /**
     * Example: how to implement a custom toggle sort cycle.
     * unsorted - ascending - descending - unsorted.
     */
    public void interactiveCustomToggleSortOrder() {
        FilterPipeline myFilterPipeline = new CustomToggleSortOrderFP();
        JXTable table = new JXTable(sortableTableModel);
        table.setFilters(myFilterPipeline);
        JXFrame frame = wrapWithScrollingInFrame(table, "Custom sort toggle");
        frame.setVisible(true);
        
    }
    /**
     * Example: how to implement a custom toggle sort cycle.
     * 
     */
    public class CustomToggleSortOrderFP extends FilterPipeline {

        public CustomToggleSortOrderFP() {
            super();
        }

        public CustomToggleSortOrderFP(Filter[] inList) {
            super(inList);
        }

        @Override
        protected SortController createDefaultSortController() {
            return new CustomSortController();
        }
        
        protected class CustomSortController extends SorterBasedSortController {

            @Override
            public void toggleSortOrder(int column, Comparator comparator) {
                Sorter currentSorter = getSorter();
                if ((currentSorter != null) && 
                     (currentSorter.getColumnIndex() == column) &&
                     !currentSorter.isAscending()) {
                    setSorter(null);
                } else {
                    super.toggleSortOrder(column, comparator);
                } 
            }
            
        }
    };
    


    /**
     * Respect not-sortable column.
     */
    public void interactiveColumnNotSortable() {
        final JXTable table = new JXTable(sortableTableModel) ;
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setSortable(false);
        JXFrame frame = wrapWithScrollingInFrame(table, "Always sorted");
        frame.setVisible(true);
        
    }
   

    /**
     * Expose sorted column. 
     * Example how to guarantee one column sorted at all times.
     */
    public void interactiveAlwaysSorted() {
        final JXTable table = new JXTable(sortableTableModel) {

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                super.columnRemoved(e);
                if (!hasVisibleSortedColumn()) {
                    toggleSortOrder(0);
                }
            }

            private boolean hasVisibleSortedColumn() {
                TableColumn column = getSortedColumn();
                if (column instanceof TableColumnExt) {
                    return ((TableColumnExt) column).isVisible();
                }
                // JW: this path is not tested, don't really expect
                // non-ext column types, though JXTable must 
                // cope with them
                return column != null;
            }

            
        };
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "Always sorted");
        frame.setVisible(true);
        
    }
   

    /**
     * Issue #271-swingx: make sort triggering mouseEvents
     * customizable.
     * 
     * added SortGestureRecognizer.
     *
     */
    public void interactiveSortGestureRecognizer() {
        final JXTable table = new JXTable(10, 2);
        JXFrame frame = wrapWithScrollingInFrame(table, "Sort Gesture customization");
        Action action = new AbstractAction("toggle default/custom recognizer") {
            boolean hasCustom;
            public void actionPerformed(ActionEvent e) {
                SortGestureRecognizer recognizer = null;
                if (!hasCustom) {
                    hasCustom = !hasCustom;
                    recognizer = new SortGestureRecognizer() {
                        /**
                         * allow double clicks to trigger a sort.
                         */
                        @Override
                        public boolean isSortOrderGesture(MouseEvent e) {
                            return e.getClickCount() <= 2;
                        }

                        /**
                         * Disable reset gesture.
                         */
                        @Override
                        public boolean isResetSortOrderGesture(MouseEvent e) {
                            return false;
                        }

                        /**
                         * ignore modifiers.
                         */
                        @Override
                        public boolean isToggleSortOrderGesture(MouseEvent e) {
                            return isSortOrderGesture(e);
                        }
                        
                        
                        
                    };
                }
                ((JXTableHeader) table.getTableHeader()).setSortGestureRecognizer(recognizer);
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    /**
     * Issue #281-swingx: header should be auto-repainted on changes to
     * header title, value.
     * 
     *
     */
    public void interactiveUpdateHeader() {
        final JXTable table = new JXTable(10, 2);
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            int count;
            public void actionPerformed(ActionEvent e) {
                table.getColumn(0).setHeaderValue("A" + count++);
                
            }
            
        };
        addAction(frame, action);
        action = new AbstractAction("update column title") {
            int count;
            public void actionPerformed(ActionEvent e) {
                table.getColumnExt(0).setTitle("A" + count++);
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    /**
     * Issue #281-swingx, Issue #334-swing: 
     * header should be auto-repainted on changes to
     * header title, value. Must update size if appropriate.
     * 
     * still open: core #4292511 - autowrap not really working
     *
     */
    public void interactiveUpdateHeaderAndSizeRequirements() {
        
        final String[] alternate = { 
                "simple", 
//                "<html><b>This is a test of a large label to see if it wraps </font></b>",
//                "simple", 
                 "<html><center>Line 1<br>Line 2</center></html>" 
                };
        final JXTable table = new JXTable(10, 2);
        
        JXFrame frame = wrapWithScrollingInFrame(table, "update header");
        Action action = new AbstractAction("update headervalue") {
            boolean first;
            public void actionPerformed(ActionEvent e) {
                table.getColumn(1).setHeaderValue(first ? alternate[0] : alternate[1]);
                first = !first;
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    /**
     * Issue #??-swingx: column auto-sizing support.
     *
     */
    public void interactiveTestExpandsToViewportWidth() {
        final JXTable table = new JXTable();
        ColumnFactory factory = new ColumnFactory() {
            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                 super.configureTableColumn(model, columnExt);
                 if (model.getColumnClass(columnExt.getModelIndex()) == Integer.class) {
                     // to see the effect: excess width is distributed relative
                     // to the difference between maxSize and prefSize
                     columnExt.setMaxWidth(200);
                 } else {
                 
                     columnExt.setMaxWidth(1024);
                 }
            }
            
        };
        table.setColumnFactory(factory);
        table.setColumnControlVisible(true);
        table.setModel(sortableTableModel);
        table.setHorizontalScrollEnabled(true);
        table.packAll();
        JXFrame frame = wrapWithScrollingInFrame(table, "expand to width");
        Action toggleModel = new AbstractAction("toggle model") {

            public void actionPerformed(ActionEvent e) {
                table.setModel(table.getModel() == sortableTableModel ? 
                        new DefaultTableModel(20, 4) : sortableTableModel);
                
            }
            
        };
        addAction(frame, toggleModel);
        frame.setSize(table.getPreferredSize().width - 50, 300);
        frame.setVisible(true);
        LOG.info("table: " + table.getWidth());
        LOG.info("Viewport: " + table.getParent().getWidth());
    }
    
    /**
     * Issue #256-swingx: viewport config.
     *
     */
    public void interactiveTestFillsViewportHeight() {
        final JXTable table = new JXTable(10, 2);
        table.setFillsViewportHeight(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "toggle viewport height");
        frame.setSize(500, table.getPreferredSize().height * 2);
        Action action = new AbstractAction("toggle fill") {

            public void actionPerformed(ActionEvent e) {
                table.setFillsViewportHeight(!table.getFillsViewportHeight());
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);

    }

    /** 
     * Issue ??: Anchor lost after receiving a structure changed.
     * Lead/anchor no longer automatically initialized - no visual clue
     * if table is focused. 
     *
     */
    public void interactiveTestToggleTableModelU6() {
        final DefaultTableModel tableModel = createAscendingModel(0, 20);
        final JTable table = new JTable(tableModel);
        // JW: need to explicitly set _both_ anchor and lead to >= 0
        // need to set anchor first
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(0);
        table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        Action toggleAction = new AbstractAction("Toggle TableModel") {

            public void actionPerformed(ActionEvent e) {
                TableModel model = table.getModel();
                table.setModel(model.equals(tableModel) ? sortableTableModel : tableModel);
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "JTable - anchor lost after structure changed");
        addAction(frame, toggleAction);
        frame.setVisible(true);
        frame.pack();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // sanity - focus is on table
                LOG.info("isFocused? " + table.hasFocus());
                LOG.info("who has focus? " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner());
            }
        });
    }

    /**
     * Issue #186-swingxProblem with lead/selection and buttons as editors:
     * - move focus (using arrow keys) to first editable boolean  
     * - press space to toggle boolean
     * - move focus to next row (same column)
     * - press space to toggle boolean
     * - move back to first row (same column)
     * - press space: boolean is toggled and (that's the problem) 
     *  lead selection is moved to next row.
     *  No problem in JTable.
     *
     */
    public void interactiveTestCompareTableBoolean() {
        JXTable xtable = new JXTable(createModelWithBooleans());
        JTable table = new JTable(createModelWithBooleans()); 
        JXFrame frame = wrapWithScrollingInFrame(xtable, table, "Compare boolean renderer JXTable <--> JTable");
        frame.setVisible(true);
    }

    private TableModel createModelWithBooleans() {
        String[] columnNames = { "text only", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
                model.addRow(new Object[] {"text only " + i, Boolean.TRUE, Boolean.TRUE });
        }
        return model;
    }


    /**
     * Issue #89-swingx: ColumnControl not updated with ComponentOrientation.
     *
     */
    public void interactiveRToLTableWithColumnControl() {
        final JXTable table = new JXTable(createAscendingModel(0, 20));
         JScrollPane pane = new JScrollPane(table);
//        table.setColumnControlVisible(true);
//        pane.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        final JXFrame frame = wrapInFrame(pane, "RToLScrollPane");
        addComponentOrientationToggle(frame);
        Action toggleColumnControl = new AbstractAction("toggle column control") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        addAction(frame, toggleColumnControl);
        frame.setVisible(true);
    }
    
    public void interactiveTestRowHeightAndSelection() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, table.getRowHeight() * 2);
        final int column = 0;
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel model = (ListSelectionModel) e.getSource();
                int selected = model.getMinSelectionIndex();
                if (selected < 0) return;
                System.out.println("from selection: " + table.getValueAt(selected, column));
            }
            
        });
        JXFrame frame = wrapWithScrollingInFrame(table, "Accessing values (indy rowheights)");
        Action updateCellAction = new AbstractAction("update cell value") {

            public void actionPerformed(ActionEvent e) {
                int anchorRow = table.getSelectionModel().getLeadSelectionIndex();
                int anchorCol = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                if ((anchorRow < 0) || (anchorCol < 0)) return;
                table.setValueAt("x" + table.getValueAt(anchorRow, anchorCol), anchorRow, anchorCol);
                
            }
            
        };
        addAction(frame, updateCellAction);
        frame.setVisible(true);
    }

    public void interactiveTestRowHeight() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setRowHeightEnabled(true);
        table.setRowHeight(0, table.getRowHeight() * 2);
        JXFrame frame = wrapWithScrollingInFrame(table, "Individual rowheight");
        Action temp = new AbstractAction("empty selection") {

            public void actionPerformed(ActionEvent e) {
                table.changeSelection(-1, -1, false, false);
                
            }
            
        };
        addAction(frame, temp);
        frame.setVisible(true);
    }
    

    /**
     * Issue #232-swingx: SelectionMapper not updated on changing selectionModel.
     * visually verify that the mapper keeps the selection after re-setting
     * table's selectionModel.
     * 
     * 
     *
     */
    public void interactiveSelectionMapperOnSelectionModelChange() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setSelectionModel(new DefaultListSelectionModel());
        JXFrame frame = wrapWithScrollingInFrame(table, "SelectionMapper: keep selection on change view model");
//        Action temp = new AbstractAction("toggle selectionModel") {
//
//            public void actionPerformed(ActionEvent e) {
//                table.setSelectionModel(new DefaultListSelectionModel());
//                
//            }
//            
//        };
//        addAction(frame, temp);
        frame.setVisible(true);
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
        @Override
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
        @Override
        public int compareTo(Car o) {
            if (o instanceof Porsche) {
                return ((Porsche) o).hasBridgeStone ? 0 : 1; 
            }
            return super.compareTo(o);
        }
        @Override
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
        @Override
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
        table.toggleSortOrder(1);
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
        Action toggleFilter = new AbstractAction("Toggle Filter col. 0") {
            boolean hasFilters;
            public void actionPerformed(ActionEvent e) {
                if (hasFilters) {
                    table.setFilters(null);
                } else {
                    Filter filter = new PatternFilter("e", 0, 0);
                    table.setFilters(new FilterPipeline(new Filter[] {filter}));

                }
                hasFilters = !hasFilters;
            }
            
        };
        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
        addAction(frame, toggleFilter);
        frame.setVisible(true);
    }
 
    /**
     * Issue #55-swingx: NPE on setModel if sorter in pipeline and new
     * model getColumnCount() < sorter.getColumnIndex().
     *
     */
    public void interactiveSetModelFilter() {
        final DefaultTableModel model = createAscendingModel(0, 20, 3, true);
        final AncientSwingTeam ancientSwingTeam = new AncientSwingTeam();
        final JXTable table = new JXTable(ancientSwingTeam);
        Sorter sorter = new ShuttleSorter(3, false);
        FilterPipeline pipeline = new FilterPipeline(new Filter[] {sorter});
        table.setFilters(pipeline);
        table.setRowHeight(0, 25);
        Action action = new AbstractAction("toggleModel") {

            public void actionPerformed(ActionEvent e) {
                if (table.getModel() == ancientSwingTeam) {
                    table.setModel(model);
                } else {
                    table.setModel(ancientSwingTeam);
                }
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "model update?");
        addAction(frame, action);
        frame.setVisible(true);
    }
    

    /** 
     * @KEEP this is about testing Mustang sorting.
     */
    public void interactiveTestColumnControlAndFiltersRowSorter() {
//        final JXTable table = new JXTable(sortableTableModel);
//        // hmm bug regression with combos as editors - same in JTable
////        JComboBox box = new JComboBox(new Object[] {"one", "two", "three" });
////        box.setEditable(true);
////        table.getColumnExt(0).setCellEditor(new DefaultCellEditor(box));
//        Action toggleFilter = new AbstractAction("Toggle RowFilter -contains e- ") {
//            boolean hasFilters;
//            public void actionPerformed(ActionEvent e) {
//                if (hasFilters) {
//                    table.setFilters(null);
//                } else {
//                    RowSorterFilter filter = new RowSorterFilter();
//                    filter.setRowFilter(RowFilter.regexFilter(".*e.*", 0));
//                    table.setFilters(new FilterPipeline(new Filter[] {filter}));
//
//                }
//                hasFilters = !hasFilters;
//            }
//            
//        };
//        toggleFilter.putValue(Action.SHORT_DESCRIPTION, "filtering first column - problem if invisible ");
//        table.setColumnControlVisible(true);
//        JFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl and Filters");
//        addAction(frame, toggleFilter);
//        frame.setVisible(true);
    }
 

    /** 
     * Issue #155-swingx: vertical scrollbar policy lost.
     *
     */
    public void interactiveTestColumnControlConserveVerticalScrollBarPolicyAlways() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        // initial state of column control visibility doesn't seem to matter
//      table.setColumnControlVisible(true);
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JXFrame frame = wrapInFrame(scrollPane1, "JXTable Vertical ScrollBar Policy");
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }


    /** 
     * Issue #155-swingx: vertical scrollbar policy lost.
     *
     */
    public void interactiveTestColumnControlConserveVerticalScrollBarPolicyNever() {
        final JXTable table = new JXTable();
        Action toggleAction = new AbstractAction("Toggle Control") {

            public void actionPerformed(ActionEvent e) {
                table.setColumnControlVisible(!table.isColumnControlVisible());
                
            }
            
        };
        table.setModel(new DefaultTableModel(10, 5));
        // initial state of column control visibility doesn't seem to matter
//        table.setColumnControlVisible(true);
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        JXFrame frame = wrapInFrame(scrollPane1, "JXTable Vertical ScrollBar Policy");
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
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable ColumnControl with few rows");
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
        JXFrame frame = wrapInFrame(table, "JXTable: Toggle ColumnControl outside ScrollPane");
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
        final JXFrame frame = wrapInFrame(table, "JXTable: Toggle ScrollPane with Columncontrol on");
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
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        JXFrame frame = wrapWithScrollingInFrame(table, "JXTable: Column with Min=Max not resizable");
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

    /**
     * quick check if multiple comparators per column work.
     * Basically yes, with a slight tweak: need to comment guarding
     * code in filterpipeline throwing exceptions.
     * 
     * So commented the body for now, need to enquire why the guard
     * was added in the first place.
     * 
     * @KEEP
     */
    public void interactiveMultipleComparatorsPerColumn() {
//        JXTable table = new JXTable(createSplittableValues());
//        Sorter sorter1 = new ShuttleSorter(0, false);
//        sorter1.setComparator(new ClassComparator(0));
//        Sorter sorter2 = new ShuttleSorter(0, true );
//        sorter2.setComparator(new ClassComparator(1));
//        
//        FilterPipeline pipeline = new FilterPipeline(new Filter[] { sorter1, sorter2 });
//        table.setFilters(pipeline);
//        JXFrame frame = wrapWithScrollingInFrame(table, "MultipleSorter per Column");
//        frame.setVisible(true);
        
    }
    
    /**
     * @KEEP - this is used in the commented method above.
     */
//    private TableModel createSplittableValues() {
//        String[] values = {"avalue:zvalue", "avalue:yvalue", "avalue:xvalue", 
//                "bvalue:zvalue", "bvalue:yvalue", "bvalue:xvalue", 
//                "cvalue:zvalue", "cvalue:yvalue", "cvalue:xvalue", 
//                };
//        DefaultTableModel model = new DefaultTableModel(values.length, 1);
//        for (int i = 0; i < values.length; i++) {
//            model.setValueAt(values[i], i, 0);
//        }
//    return model;
//}

    public class ClassComparator implements Comparator {
        
        List packageOrder;
        int sortIndex;
        
        public ClassComparator(int index) {
            this.sortIndex = index;
        }

        public int compare(Object o1, Object o2) {
            String[] value1 = String.valueOf(o1).split(":");
            String[] value2 = String.valueOf(o2).split(":");
            
            String part1 = value1.length > sortIndex ? value1[sortIndex] : "";
            String part2 = value2.length > sortIndex ? value2[sortIndex] : "";
            return part1.compareTo(part2);
        }
        
    }
    
    /**
     * Issue #31 (swingx): clicking header must not sort if table !enabled.
     *
     */
    public void interactiveTestDisabledTableSorting() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setEnabled(false);
        table.setColumnControlVisible(true);
        Action toggleAction = new AbstractAction("Toggle Enabled") {

            public void actionPerformed(ActionEvent e) {
                table.setEnabled(!table.isEnabled());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "Disabled tabled: no sorting");
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
            @Override
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
        frame.setVisible(true);  
    }

    /**
     */
    public void interactiveTestToggleSortable() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        Action toggleSortableAction = new AbstractAction("Toggle Sortable") {

            public void actionPerformed(ActionEvent e) {
                table.setSortable(!table.isSortable());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "ToggleSortingEnabled Test");
        addAction(frame, toggleSortableAction);
        frame.setVisible(true);  
        
    }
    public void interactiveTestTableSizing1() {
        JXTable table = new JXTable();
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(tableModel);
        installLinkRenderer(table);
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


    private void installLinkRenderer(JXTable table) {
        LinkModelAction action = new LinkModelAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("activated link: " + getTarget());
            }
             
        };
        TableCellRenderer linkRenderer = new DefaultTableRenderer(
                new HyperlinkProvider(action, LinkModel.class));
        table.setDefaultRenderer(LinkModel.class, linkRenderer);
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
        installLinkRenderer(table);

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



    public void interactiveTestTableSorter1() {
        JXTable table = new JXTable(sortableTableModel);
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
    
    @SuppressWarnings("deprecation")
    public void interactiveTestFocusedCellBackground() {
        TableModel model = new AncientSwingTeam() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        JXTable xtable = new JXTable(model);
        xtable.setBackground(HighlighterFactory.NOTEPAD);
        JTable table = new JTable(model);
        table.setBackground(new Color(0xF5, 0xFF, 0xF5)); // ledger
        JFrame frame = wrapWithScrollingInFrame(xtable, table, "Unselected focused background: JXTable/JTable");
        frame.setVisible(true);
    }


    public void interactiveTestTableSorter3() {
        JXTable table = new JXTable(sortableTableModel);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new ShuttleSorter(1, true), // column 1, ascending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter3 col 1 = asc, col 0 = desc");
        frame.setVisible(true);
    }

    public void interactiveTestTableSorter4() {
        JXTable table = new JXTable(sortableTableModel);
        table.setFilters(new FilterPipeline(new Filter[] {
                new ShuttleSorter(0, false), // column 0, descending
                                            
                new ShuttleSorter(1, true), // column 1, ascending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TableSorter4 col 0 = des, col 1 = asc");
        frame.setVisible(true);
    }
    
    public void interactiveTestTablePatternFilter1() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("^A", 0, 1)
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter1 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter2() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setIntercellSpacing(new Dimension(2, 2));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("^S", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter2 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter3() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("^S", 0, 1),
                                            new ShuttleSorter(1, false), // column 1, descending
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter3 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter4() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setIntercellSpacing(new Dimension(3, 3));
        table.setShowGrid(true);
        table.setFilters(new FilterPipeline(new Filter[] {
                                            new PatternFilter("^A", 0, 1),
                                            new ShuttleSorter(0, false), // column 0, descending
        }));
        JFrame frame = wrapWithScrollingInFrame(table, "TablePatternFilter4 Test");
        frame.setVisible(true);
    }

    public void interactiveTestTableViewProperties() {
        JXTable table = new JXTable(tableModel);
        installLinkRenderer(table);
        table.setIntercellSpacing(new Dimension(15, 15));
        table.setRowHeight(48);
        JFrame frame = wrapWithScrollingInFrame(table, "TableViewProperties Test");
        frame.setVisible(true);
    }


    public void interactiveTestTableColumnProperties() {
        JXTable table = new JXTable();
        table.setModel(tableModel);
        installLinkRenderer(table);

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

        JXFrame frame = wrapWithScrollingInFrame(table, "TableColumnProperties Test");
        addStatusMessage(frame, "column properties currently not working");
        frame.setVisible(true);
    }

    public void interactiveTestEditableHighlight() {
        JXTable table = new JXTable(tableModel);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.EDITABLE, Color.RED,
                Color.WHITE));
        JFrame frame = wrapWithScrollingInFrame(table, "Editability Highlighter Test");
        frame.setVisible(true);
    }
    
    public void interactiveColumnHighlighting() {
        final JXTable table = new JXTable(new AncientSwingTeam());
        
        table.getColumnExt("Favorite Color").setHighlighters(new AbstractHighlighter() {
            @Override
            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                Color color = (Color) adapter.getValue();
                
                if (renderer instanceof JComponent) {
                    ((JComponent) renderer).setBorder(BorderFactory.createLineBorder(color));
                }
                
                return renderer;
            }
        });
        
        JFrame frame = wrapWithScrollingInFrame(table, "Column Highlighter Test");
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("Toggle") {
            boolean state = false;
            
            public void actionPerformed(ActionEvent e) {
                if (state) {
                    table.getColumnExt("No.").setHighlighters(new Highlighter[0]);
                    state = false;
                } else {
                    table.getColumnExt("No.").addHighlighter(
                        new AbstractHighlighter(new HighlightPredicate() {
                            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                                return adapter.getValue().toString().contains("8");
                            }
                        }) {
                            @Override
                            protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
                                Font f = renderer.getFont().deriveFont(Font.ITALIC);
                                renderer.setFont(f);
                                
                                return renderer;
                            }
                        }
                    );
                    state = true;
                }
            }
        });
        frame.add(bar, BorderLayout.NORTH);
        frame.setVisible(true);
    }
    
    /**
     * dummy
     */
    public void testDummy() {
    }   

}

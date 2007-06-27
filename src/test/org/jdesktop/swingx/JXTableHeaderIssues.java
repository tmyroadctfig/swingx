/*
 * Created on 28.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.decorator.SortOrder;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;

public class JXTableHeaderIssues extends JXTableHeaderTest {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger
            .getLogger(JXTableHeaderIssues.class.getName());
    public static void main(String args[]) {
        JXTableHeaderIssues test = new JXTableHeaderIssues();
        setSystemLF(true);
        try {
//          test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Siz.*");
         //   test.runInteractiveTests("interactive.*Render.*");
            test.runInteractiveTests("interactive.*Dock.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        } 
    }

    /**
     * Quick proof-of-concept: JXTableHeader can go dirty and
     * suppress moving of "docked" column.
     *
     */
    public void interactiveDockColumn() {
        final JXTableHeader header = new JXTableHeader() {

            private int oldDistance;


            @Override
            public void setDraggedDistance(int distance) {
                oldDistance = getDraggedDistance();
                if (isDocked(getDraggedColumn())) {
                    distance = 0;
                } 
                if (isDraggedOverDocked(distance)) {
                    distance = 0;
                    setDraggedColumn(null);
                }
                super.setDraggedDistance(distance);
            }

            private boolean isDraggedOverDocked(int distance) {
                if (getDraggedColumn() == null) return false;
                int dragPosition = getDragX(distance);
                int columnUnder = columnAtPoint(new Point(dragPosition, 0));
                if (columnUnder >= 0) {
                    return  isDocked(getColumnModel().getColumn(columnUnder));
                }
                return false;
            }
            
            private int getDragX(int distance) {
                DefaultTableColumnModelExt columnModel = (DefaultTableColumnModelExt) getColumnModel();
                List<TableColumn> columns = columnModel.getColumns(false);
                int startX = 0;
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i) == getDraggedColumn()) {
                        // at the wrong column the very moment 
                        // before the dock neighbour is 
                        // actually replaced with the second next dock neighbour
                        // need to add the stop-column width as well
                        if (distance * oldDistance < 0) {
                            startX += columns.get(i).getWidth();
                        }
                         break;
                    }
                    startX += columns.get(i).getWidth();
                }
                return startX + distance;
            }

            
            private boolean isDocked(TableColumn column) {
                if (column instanceof TableColumnExt) {
                    return Boolean.TRUE.equals(((TableColumnExt) column).getClientProperty("docked"));
                }
                return false;
            }
        };
        final TableColumnModel cModel = new DefaultTableColumnModelExt() {

            @Override
            public void moveColumn(int columnIndex, int newIndex) {
                if (isDocked(columnIndex) || isDocked(newIndex)) return;
                super.moveColumn(columnIndex, newIndex);
            }

            private boolean isDocked(int columnIndex) {
                TableColumnExt column = getColumnExt(columnIndex);
                if (column != null) {
                    return Boolean.TRUE.equals(column.getClientProperty("docked"));
                }
                return false;
            }
            
        };
        JXTable table = new JXTable(10, 5) {

            @Override
            protected JTableHeader createDefaultTableHeader() {
                header.setColumnModel(columnModel);
                return header;
            }

            @Override
            protected TableColumnModel createDefaultColumnModel() {
                return cModel;
            }
            
            
        };
        table.getColumnExt(0).putClientProperty("docked", Boolean.TRUE);
        showWithScrollingInFrame(table, "block dragging");
    }

    /**
     * Issue #281-swingx, Issue #334-swing: 
     * header should be auto-repainted on changes to
     * header title, value. Must update size if appropriate.
     * 
     * still not solved: core #4292511 - autowrapping is weird,
     * even with the swingx fix 
     *
     */
    public void interactiveUpdateHeaderAndSizeRequirements() {
        
        final String[] alternate = { 
//                "simple", 
                "<html><b>This is a test of a large label to see if it wraps </font></b>",
                "simple", 
                //                "<html><center>Line 1<br>Line 2</center></html>" 
                };
        final JXTable table = new JXTable(10, 2);
        table.getColumn(0).setHeaderValue(alternate[0]);
        table.getColumn(1).setHeaderValue(alternate[1]);
        
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
     * Issue 337-swingx: header heigth depends on sort icon (for ocean only?) 
     * NOTE: this seems to be independent of the tweaks to xTableHeaders
     *   prefSize.
     */
    public void testSortedPreferredHeight() {
        JXTable table = new JXTable(10, 2);
        JXTableHeader tableHeader = (JXTableHeader) table.getTableHeader();
        Dimension dim = tableHeader.getPreferredSize();
        table.setSortOrder(0, SortOrder.ASCENDING);
        assertEquals("Header pref height must be unchanged if sorted",
                dim.height, tableHeader.getPreferredSize().height);
    }

}

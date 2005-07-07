/*
 * Created on 30.06.2005
 *
 */
package org.jdesktop.swingx.table;

import java.awt.Component;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * @author Jeanette Winzenburg
 */
public class ColumnControlButtonTest extends InteractiveTestCase {
    protected TableModel sortableTableModel;
    

    
    public void testColumnControlReleaseAction() {
        final JXTable table = new JXTable(sortableTableModel);
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        int listenerCount = priorityColumn.getPropertyChangeListeners().length;
        table.setColumnControlVisible(true);
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        assertEquals("numbers of listeners must be increased", listenerCount + 1, 
                priorityColumn.getPropertyChangeListeners().length);
        int totalColumnCount = table.getColumnCount();
        table.removeColumn(priorityColumn);
        assertEquals("number of columns reduced", totalColumnCount - 1, table.getColumnCount());
        assertEquals("all listeners must be removed", 0, 
                priorityColumn.getPropertyChangeListeners().length);
        
        
        //        assertNotNull("popup menu not null", columnControl.popupMenu);
//        int columnMenuItems = 0;
//        Component[] items = columnControl.popupMenu.getComponents();
//        for (int i = 0; i < items.length; i++) {
//            if (!(items[i] instanceof JMenuItem)) {
//                break;
//            }
//            columnMenuItems++;
//        }
//        // wrong assumption - has separator and actions!
//        assertEquals("menu items must be equal to columns", totalColumnCount, 
//                columnMenuItems);
//        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) columnControl.popupMenu
//            .getComponent(0);
//        // sanit assert
//        assertEquals(priorityColumn.getHeaderValue(), menuItem.getText());
//        assertEquals("selection of menu must be equal to column visibility", 
//                priorityColumn.isVisible(), menuItem.isSelected());
    }

   /** 
    * Issue #192: initially invisibility columns are hidden
    * but marked as visible in control.
    *
    * Issue #38 (swingx): initially invisble columns don't show up
    * in the column control list.
    * 
    * 
    */
   public void testColumnControlInvisibleColumns() {
       final JXTable table = new JXTable(sortableTableModel);
       // columns set to invisible before setting the columnControl
       // will not be inserted into the column control's list
//     table.getColumnExt("Last Name").setVisible(false);
       table.setColumnControlVisible(true);
       int totalColumnCount = table.getColumnCount();
       final TableColumnExt priorityColumn = table.getColumnExt("First Name");
       priorityColumn.setVisible(false);
       ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
       assertNotNull("popup menu not null", columnControl.popupMenu);
       int columnMenuItems = 0;
       Component[] items = columnControl.popupMenu.getComponents();
       for (int i = 0; i < items.length; i++) {
           if (!(items[i] instanceof JMenuItem)) {
               break;
           }
           columnMenuItems++;
       }
       // wrong assumption - has separator and actions!
       assertEquals("menu items must be equal to columns", totalColumnCount, 
               columnMenuItems);
       JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) columnControl.popupMenu
           .getComponent(0);
       // sanit assert
       assertEquals(priorityColumn.getHeaderValue(), menuItem.getText());
       assertEquals("selection of menu must be equal to column visibility", 
               priorityColumn.isVisible(), menuItem.isSelected());
   }


    /** 
     * Issue #192: initially invisibility columns are hidden
     * but marked as visible in control.
     *
     * Issue #38 (swingx): initially invisble columns don't show up
     * in the column control list.
     * 
     * 
     */
    public void interactiveTestColumnControlInvisibleColumns() {
        final JXTable table = new JXTable(sortableTableModel);
        // columns set to invisible before setting the columnControl
        // will not be inserted into the column control's list
//      table.getColumnExt("Last Name").setVisible(false);
        table.setColumnControlVisible(true);
        int totalColumnCount = table.getColumnCount();
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        priorityColumn.setVisible(false);
        JFrame frame = wrapWithScrollingInFrame(table, "JXTable (#192, #38-swingx) ColumnControl and Visibility of items");
        frame.setVisible(true);
    }


    
    public ColumnControlButtonTest() {
        super("ColumnControlButtonTest");
    }
    
    protected void setUp() throws Exception {
        super.setUp();
         sortableTableModel = new AncientSwingTeam();
     }

    public static void main(String args[]) {
       setSystemLF(false);
      ColumnControlButtonTest test = new ColumnControlButtonTest();
      try {
       // test.runInteractiveTests();
      //    test.runInteractiveTests("interactive.*Column.*");
//          test.runInteractiveTests("interactive.*TableHeader.*");
      //    test.runInteractiveTests("interactive.*SorterP.*");
          test.runInteractiveTests("interactive.*Column.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
}

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.ColumnControlButton.ColumnVisibilityAction;
import org.jdesktop.swingx.table.ColumnControlButton.DefaultControlPopup;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * @author Jeanette Winzenburg
 */
public class ColumnControlButtonTest extends InteractiveTestCase {
    protected TableModel sortableTableModel;

    /**
     * test that the actions synch's its own selected property with 
     * the column's visible property. <p>
     * 
     * Looks as if the non-synch of action.setSelected only shows 
     * if the ControlPopup doesn't create a menuitem via ActionFactory: the
     * listeners internally installed via ActionFactory probably take care?
     *  <p>
     * 
     * An analogous test in the incubator (in kleopatra/.../table) did fail
     * for a dialog based custom ControlPopup. For now, changed the visibility
     * action to explicitly update the tableColumn. All tests are passing,
     * but need to further evaluate.
     *
     */
    public void testColumnVisibilityAction() {
        JXTable table = new JXTable(10, 3);
        table.setColumnControlVisible(true);
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        ColumnVisibilityAction action = columnControl.getColumnVisibilityActions().get(0);
        TableColumnExt columnExt = table.getColumnExt(0);
        boolean visible = columnExt.isVisible();
        // sanity
        assertTrue(visible);
        assertEquals(columnExt.isVisible(), action.isSelected());
        action.setSelected(!visible);
        // hmmm... here it's working? unexpected
        // synch might be done by the listener's installed by ActionFactor.createMenuItem()?
        assertEquals(!visible, columnExt.isVisible());
    }
    
    /**
     * suspected: enabled not synched on init. 
     * But is (done in ccb.installTable()). 
     *
     */
    public void testInitialDisabled() {
        JXTable table = new JXTable(10, 3);
        table.setEnabled(false);
        table.setColumnControlVisible(true);
        assertEquals(table.isEnabled(), table.getColumnControl().isEnabled());
    }


    /**
     * guarantee that at least one column is always visible.
     *
     */
    public void testMinimumColumnCountOne() {
        JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setVisible(false);
        assertEquals(1, table.getColumnCount());
    }
    /**
     * Issue #153-swingx: ClassCastException if actionMap key is not a string.
     *
     */
    public void testNonStringActionKeys() {
        JXTable table = new JXTable();
        Action l = new AbstractAction("dummy") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        table.registerKeyboardAction(l , KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_FOCUSED);
        table.setColumnControlVisible(true);
        table.getColumnControl();
    }
    
    public void testColumnControlReleaseAction() {
        final JXTable table = new JXTable(sortableTableModel);
        final TableColumnExt priorityColumn = table.getColumnExt("First Name");
        int listenerCount = priorityColumn.getPropertyChangeListeners().length;
        table.setColumnControlVisible(true);
        // JW: the columnControlButton is created lazily, so we
        // have to access to test if listeners are registered.
        table.getColumnControl();
        assertEquals("numbers of listeners must be increased", listenerCount + 1, 
                priorityColumn.getPropertyChangeListeners().length);
        int totalColumnCount = table.getColumnCount();
        table.removeColumn(priorityColumn);
        assertEquals("number of columns reduced", totalColumnCount - 1, table.getColumnCount());
        assertEquals("all listeners must be removed", 0, 
                priorityColumn.getPropertyChangeListeners().length);
     }

    /**
     * Issue #212-swingx: 
     * 
     * Behaviour change: 
     * <ul>
     * <li>before - guarantee that exactly one column is always visible, 
     *    independent of source of visibiblity change
     * <li> now - this is true for user gesture induced invisible (through 
     *    columnControl, but not for programmatic hiding. It's up to 
     *    developers to not hide all. To alleviate the effects if they 
     *    hide all, the JXTableHeader and ColumnControl is always visible.
     * </ul>
     * This is testing the old behaviour (guarding against regression)
     * Here we directly set the second last visible column to invisible. This 
     * failed if a) column visibility is set after adding the table to a frame
     * and b) model.count = 2.
     *
     */
    public void testSetAllColumnsToInvisible() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 2);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        table.getColumnExt(0).setVisible(false);
        assertEquals(1, table.getColumnCount());
        table.getColumnExt(0).setVisible(false);
        assertEquals(0, table.getColumnCount());
    }
    /**
     * Issue #212-swingx: 
     * 
     * guarantee that exactly one column is always visible if 
     * visibility is toggled via the ColumnControl.
     * 
     * Here we deselect the menuitem.
     * 
     */
    public void testSetLastColumnMenuItemToUnselected() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        final JXTable table = new JXTable(10, 1);
        table.setColumnControlVisible(true);
        wrapWithScrollingInFrame(table, "");
        ColumnControlButton columnControl = (ColumnControlButton) table.getColumnControl();
        Component[] items = ((DefaultControlPopup) columnControl.getControlPopup()).getPopupMenu().getComponents();
        ((JMenuItem) items[0]).setSelected(false);
        assertEquals(1, table.getColumnCount());
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
       Component[] items = ((DefaultControlPopup) columnControl.getControlPopup()).getPopupMenu().getComponents();
       for (int i = 0; i < items.length; i++) {
           if (!(items[i] instanceof JMenuItem)) {
               break;
           }
           columnMenuItems++;
       }
       // wrong assumption - has separator and actions!
       assertEquals("menu items must be equal to columns", totalColumnCount, 
               columnMenuItems);
       JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) ((DefaultControlPopup) columnControl.getControlPopup()).getPopupMenu()
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
     * Visual check: first enable column control then  set column invisible. 
     * 
     */
    public void interactiveTestColumnControlInvisibleColumns() {
        final JXTable table = new JXTable(sortableTableModel);
        table.setColumnControlVisible(true);
        final TableColumnExt firstNameColumn = table.getColumnExt("First Name");
        firstNameColumn.setVisible(false);
        JFrame frame = wrapWithScrollingInFrame(table, "ColumnControl (#192, #38-swingx) first enable ColumnControl then column invisible");
        frame.setVisible(true);
    }


    /** 
     * Issue #192: initially invisibility columns are hidden
     * but marked as visible in control.
     *
     * Issue #38 (swingx): initially invisble columns don't show up
     * in the column control list.
     * 
     * Visual check: first set column invisible then enable column control.
     * 
     */
    public void interactiveTestColumnControlEarlyInvisibleColumns() {
        final JXTable table = new JXTable(sortableTableModel);
        table.getColumnExt("First Name").setVisible(false);
        table.setColumnControlVisible(true);
        JFrame frame = wrapWithScrollingInFrame(table, "ColumnControl (#192, #38-swingx) first column invisible, the enable columnControl");
        frame.setVisible(true);
    }


    /** 
     * Issue #212: programmatically toggle column vis does not work.
     * 
     * Visual check: compare toggle column visibility both via the columnControl 
     * and programmatically by button. While the columnControl prevents to hide
     * the very last visible column, developers have full control to do so 
     * programatically.
     * 
     * 
     */
    public void interactiveTestColumnControlSetModelToggleInvisibleColumns() {
        final JXTable table = new JXTable();
        table.setColumnControlVisible(true);
        JXFrame frame = wrapWithScrollingInFrame(table, "ColumnControl (#212-swingx) setModel and toggle first column invisible");
        frame.setVisible(true);
        table.setModel(new DefaultTableModel(10, 2));
        final TableColumnExt firstNameColumn = table.getColumnExt(1);
        Action action = new AbstractAction("Toggle first name visibility") {

            public void actionPerformed(ActionEvent e) {
                firstNameColumn.setVisible(!firstNameColumn.isVisible());
                
            }
            
        };
        addAction(frame, action);
    }

    /** 
     * 
     * 
     */
    public void interactiveTestLastVisibleColumn() {
        final JXTable table = new JXTable();
        table.setModel(new DefaultTableModel(10, 2));
        table.setColumnControlVisible(true);

        JFrame frame = wrapWithScrollingInFrame(table, "JXTable (#192, #38-swingx) ColumnControl and Visibility of items");
        table.getColumnExt(0).setVisible(false);
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
        test.runInteractiveTests();
      //    test.runInteractiveTests("interactive.*Column.*");
//          test.runInteractiveTests("interactive.*TableHeader.*");
      //    test.runInteractiveTests("interactive.*SorterP.*");
      //    test.runInteractiveTests("interactive.*Column.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }
    
}

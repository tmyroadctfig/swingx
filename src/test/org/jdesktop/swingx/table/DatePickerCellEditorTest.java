/*
 * Created on 17.07.2007
 *
 */
package org.jdesktop.swingx.table;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.test.CellEditorReport;

public class DatePickerCellEditorTest extends InteractiveTestCase {
    public static void main(String[] args) {
        setSystemLF(true);
        DatePickerCellEditorTest test = new DatePickerCellEditorTest();
        try {
            test.runInteractiveTests();
//          test.runInteractiveTests(".*Text.*");
//          test.runInteractiveTests(".*XLabel.*");
//          test.runInteractiveTests(".*Table.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
 

    /**
     * test fire stopped after accept in monthview.
     * 
     *
     */
    public void testDateEditorFireStopMonthAccept()  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        editor.addCellEditorListener(report);
        picker.getMonthView().commitSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }

    /**
     * test fire stopped after accept in monthview.
     * 
     *
     */
    public void testDateEditorFireStopMonthCancel()  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        editor.addCellEditorListener(report);
        picker.getMonthView().cancelSelection();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }

    /**
     * test fire stopped after commit picker.editor.
     * @throws ParseException 
     *
     */
    public void testDateEditorFireStopPickerCommit() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, null, false, -1, -1);
        editor.addCellEditorListener(report);
        picker.commitEdit();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }

    /**
     * test fire cancel after cancel picker.editor.
     * @throws ParseException 
     *
     */
    public void testDateEditorFireCancelPickerCancel() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        editor.addCellEditorListener(report);
        picker.cancelEdit();
//        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }
    /**
     * test fire stopped after stopCellEditing.
     * stop commits, triggers value change in formatted, triggers
     * BasicDatePickerUI to post action event which triggers
     * stop editing again ... that's twice!
     */
    public void testDateEditorFireStop() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.getTableCellEditorComponent(null, new Date(), false, -1, -1);
        editor.addCellEditorListener(report);
        editor.stopCellEditing();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStoppedEventCount());
    }
    
   /**
    * test fire cancel after cancelCellEditing
    *
    */ 
   public void testDateEditorFireCancel() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.getTableCellEditorComponent(null, null, false, -1, -1);
        editor.addCellEditorListener(report);
        editor.cancelCellEditing();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getCanceledEventCount());
    }
        
     /**
      * Editor must not fire on getXXComponent()
      *
      */
     public void testDateEditorNotFire() {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, null, false, -1, -1);
        assertEquals(0, report.getEventCount());
    }


    /**
     * Use formatting from sql date/time classes.
     *
     */
    public void interactiveDateTimeEditor() {
        Date date = new Date();
        DefaultTableModel model = new DefaultTableModel(1, 2) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return super.getColumnClass(columnIndex);
            }
            
        };
        model.setColumnIdentifiers(new Object[]{"Date", "editable combo"});
        model.setValueAt(date, 0, 0);
          model.setValueAt("selectedItem", 0, 1);
        JXTable table = new JXTable(model);
        // right align to see the difference to normal date renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer(
                new LabelProvider(SwingConstants.RIGHT));
        table.setDefaultRenderer(java.sql.Date.class, renderer);
        table.setDefaultEditor(Date.class, new DatePickerCellEditor(DateFormat.getDateInstance()));
        JComboBox box = new JComboBox(new String[] {"item1", "item2", "item3"});
        box.setEditable(true);
        table.getColumnExt(1).setCellEditor(new DefaultCellEditor(box));
        JXFrame frame = showWithScrollingInFrame(table, "normal/sql date formatting");
        // JXRootPane eats esc 
        frame.getRootPaneExt().getActionMap().remove("esc-action");
        JComboBox freeBox = new JComboBox(box.getModel());
        freeBox.setEditable(true);
        frame.add(freeBox, BorderLayout.SOUTH);
        frame.add(new JTextField("yet another thing to focus"), BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
    }

        


}

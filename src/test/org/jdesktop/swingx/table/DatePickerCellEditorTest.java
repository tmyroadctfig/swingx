/*
 * Created on 17.07.2007
 *
 */
package org.jdesktop.swingx.table;

import java.text.ParseException;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXDatePicker;
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
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        picker.getMonthView().commitSelection();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals(1, report.getEventCount());
                assertEquals(1, report.getStoppedEventCount());
                
            }
        });
    }

    /**
     * test fire stopped after accept in monthview.
     * 
     *
     */
    public void testDateEditorFireStopMonthCancel()  {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, new Date(), false, -1, -1);
        picker.getMonthView().cancelSelection();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals(1, report.getEventCount());
                assertEquals(1, report.getCanceledEventCount());
                
            }
        });
    }

    /**
     * test fire stopped after commit picker.editor.
     * @throws ParseException 
     *
     */
    public void testDateEditorFireStopPickerCommit() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor.getTableCellEditorComponent
            (null, null, false, -1, -1);
        picker.commitEdit();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals(1, report.getEventCount());
                assertEquals(1, report.getStoppedEventCount());
                
            }
        });
    }

    /**
     * test fire cancel after cancel picker.editor.
     * 
     * @throws ParseException
     * 
     */
    public void testDateEditorFireCancelPickerCancel() throws ParseException {
        DatePickerCellEditor editor = new DatePickerCellEditor();
        final CellEditorReport report = new CellEditorReport();
        editor.addCellEditorListener(report);
        JXDatePicker picker = (JXDatePicker) editor
                .getTableCellEditorComponent(null, new Date(), false, -1, -1);
        picker.cancelEdit();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                assertEquals(1, report.getEventCount());
                assertEquals(1, report.getCanceledEventCount());

            }
        });
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
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, new Date(), false, -1, -1);
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
        editor.addCellEditorListener(report);
        editor.getTableCellEditorComponent(null, null, false, -1, -1);
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

}

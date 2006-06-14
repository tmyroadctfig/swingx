/*
 * Created on 14.06.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * A quick example to excersize the experimental auto-resize if
 * isHorizontalScrollEnabled (AUTO_RESIZE_OFF).
 * 
 * 
 */
public class JXTableColumnSizing extends InteractiveTestCase {
    protected TableModel sortableTableModel;

    /**
     * Issue #214-swingx: column auto-sizing support.
     *
     */
    public void interactiveTestExpandsToViewportWidth() {
        final JXTable table = new JXTable();
        ColumnFactory factory = new ColumnFactory() {
            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                 super.configureTableColumn(model, columnExt);
                 if (model.getColumnClass(columnExt.getModelIndex()) == Integer.class) {
                     // to see an effect: excess width is distributed relative
                     // to the difference of  maxSize and prefSize
                     columnExt.setMaxWidth(200);
                 } else {
                     columnExt.setMaxWidth(1024);
                 }
            }
            
        };
        // use a custom factory
        table.setColumnFactory(factory);
        // enabled the experimental sizing control
        table.setExpandsToViewportWidthEnabled(true);
        // show horizontal scrollbar (if necessary)
        table.setHorizontalScrollEnabled(true);
        table.setColumnControlVisible(true);
        // set the model ...
        table.setModel(sortableTableModel);
        // ... and size all columns by content
        table.packAll();
        JXFrame frame = wrapWithScrollingInFrame(table, "expand to width");
        // toggles between two models: new models are automatically expanded to fill
        // the width
        Action toggleModel = new AbstractAction("toggle model") {

            public void actionPerformed(ActionEvent e) {
                table.setModel(table.getModel() == sortableTableModel ? 
                        new DefaultTableModel(20, 4) : sortableTableModel);
                
            }
            
        };
        addAction(frame, toggleModel);
        // explicitly expand to fill the width if it isn't 
        // f.i. after expanding frame size in !dockedOnExpand mode
        Action action = new AbstractAction("expand to width") {

            public void actionPerformed(ActionEvent e) {
                table.expandToViewportWidth();
                
            }
            
        };
        addAction(frame, action);
        
        // toggle the dockedOnExpand property:
        // if false table width will not change on expanding the frame
        // if on the table width will be "docked" (= increased if necessary) 
        // on expanding the frame, shrinking the frame has no effect
        Action dockAction = new AbstractActionExt("dockedOnExpand: " + 
                table.isDockedOnExpandWidth()) {

            public void actionPerformed(ActionEvent e) {
                table.setDockedOnExpandWidth(!table.isDockedOnExpandWidth());
                this.setName("dockedOnExpand: " + table.isDockedOnExpandWidth());
            }
            
        };
        addAction(frame, dockAction);
        
        frame.setVisible(true);

    }
    
    protected void setUp() throws Exception {
        sortableTableModel = new AncientSwingTeam();
    }

   public static void main(String args[]) {
       JXTableColumnSizing test = new JXTableColumnSizing();
       try {
         test.runInteractiveTests();
       } catch (Exception e) {
           System.err.println("exception when executing interactive tests:");
           e.printStackTrace();
       }
   }


}

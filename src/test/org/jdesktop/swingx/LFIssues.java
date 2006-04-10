/*
 * Created on 10.04.2006
 *
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.util.AncientSwingTeam;

public class LFIssues extends InteractiveTestCase {
    protected TableModel sortableTableModel;

    // flag used in setup to explicitly choose LF
    private boolean defaultToSystemLF;

    public static void main(String args[]) {
        LFIssues test = new LFIssues();
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


    public void testUpdateUILinkRenderer() {
        LinkRenderer comparison = new LinkRenderer();
        LinkRenderer linkRenderer = new LinkRenderer();
        JXTable table = new JXTable(2, 3);
        Component comparisonComponent = comparison.getTableCellEditorComponent(table, null, false, 0, 0);
        Font comparisonFont = comparisonComponent.getFont();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        setSystemLF(!defaultToSystemLF);
    }

    public void interactiveToggleLF() {
        LinkRenderer comparison = new LinkRenderer();
        LinkRenderer linkRenderer = new LinkRenderer();
        JXTable table = new JXTable(2, 3);
       
        Component comparisonComponent = comparison.getTableCellEditorComponent(table, null, false, 0, 0);
        Font comparisonFont = comparisonComponent.getFont();
        table.getColumnModel().getColumn(0).setCellRenderer(linkRenderer);
        final JXFrame frame = wrapWithScrollingInFrame(table, "toggle LF");
        Action action = new AbstractActionExt("ToggleLF") {
            boolean systemLF = defaultToSystemLF;
            public void actionPerformed(ActionEvent e) {
                systemLF = !systemLF;
                setSystemLF(systemLF);
                SwingUtilities.updateComponentTreeUI(frame);
                
            }
            
        };
        addAction(frame, action);
        frame.setVisible(true);
    }
    
    protected void setUp() throws Exception {
         sortableTableModel = new AncientSwingTeam();
         // make sure we have the same default for each test
         defaultToSystemLF = false;
         setSystemLF(defaultToSystemLF);
     }

}

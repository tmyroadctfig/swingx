/*
 * Created on 08.12.2010
 *
 */
package org.jdesktop.swingx.rollover;

import javax.swing.JOptionPane;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.test.AncientSwingTeam;

public class RolloverVisualCheck extends InteractiveTestCase {

    public static void main(String[] args) {
        RolloverVisualCheck test = new RolloverVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickList() {
        JXList table = new JXList(AncientSwingTeam.createNamedColorListModel());
        table.setEnabled(false);
        table.setRolloverEnabled(true);
        final JXFrame frame = showWithScrollingInFrame(table, "disable table must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultListRenderer renderer = new DefaultListRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setCellRenderer(renderer);
        addEnabledToggle(frame, table);
    }
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickTree() {
        JXTree table = new JXTree();
        table.setEnabled(false);
        table.setRolloverEnabled(true);
        final JXFrame frame = showWithScrollingInFrame(table, "disable table must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultTreeRenderer renderer = new DefaultTreeRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setCellRenderer(renderer);
        addEnabledToggle(frame, table);
    }
    
    
    /**
     * Issue #1374-swingx: Rollover click must be disabled if target disabled
     * 
     * Visual test for JXTable, adapted from reporter's test case
     */
    public void interactiveRolloverDisabledClickTable() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.setEditable(false);
        table.setEnabled(false);
        final JXFrame frame = showWithScrollingInFrame(table, "disable table must not trigger rollover renderer");
        // rollover-enabled default renderer
        DefaultTableRenderer renderer = new DefaultTableRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Click");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        addEnabledToggle(frame, table);
    }
    
    
}

/*
 * Created on 08.12.2010
 *
 */
package org.jdesktop.swingx.rollover;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.test.AncientSwingTeam;

public class RolloverVisualCheck extends InteractiveTestCase {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(RolloverVisualCheck.class.getName());

    public static void main(String[] args) {
        RolloverVisualCheck test = new RolloverVisualCheck();
        try {
            test.runInteractive("Drag");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Issue #456-swingx: Rollover highlighter not showing while dragging.
     * 
     * Example how to force rollover while dragging (NOT dnd - in that case the visuals
     * are controlled by the dnd mechanism): subclass JXList to install a custom 
     * RolloverProducer
     */
    public void interactiveEnforceRolloverWhileDragging() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel()) {

            /** 
             * @inherited <p>
             */
            @Override
            protected RolloverProducer createRolloverProducer() {
                ListRolloverProducer producer = new ListRolloverProducer() {

                    /** 
                     * @inherited <p>
                     */
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        updateRollover(e, ROLLOVER_KEY, false);
                    }
                    
                };
                return producer;
            }
            
        };
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));
        
        TransferHandler handler = new TransferHandler() {
            
            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        JXFrame frame = wrapWithScrollingInFrame(list, "force Rollover while dragging");
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #456-swingx: Rollover highlighter not showing while dragging.
     * 
     * The expected behaviour: 
     * - don't show the xRollover while dragging during a dnd,
     * - update the rollover to the released.
     */
    public void interactiveRolloverWhileDragging() {
        final JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));

        TransferHandler handler = new TransferHandler() {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        JXFrame frame = wrapWithScrollingInFrame(list, "rollover and drag-enabled (fake drop)");
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        show(frame);
    }
    
    /**
     * Issue #1387-swingx: Rollover click-on-release-after drag.
     * 
     * PENDING JW: what's the expected behaviour?
     */
    public void interactiveRolloverClickAfterDrag() {
        JXList list = new JXList(AncientSwingTeam.createNamedColorListModel());
        list.setVisibleRowCount(list.getElementCount());
        list.setRolloverEnabled(true);
        list.addHighlighter(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW, 
                Color.MAGENTA, null, Color.MAGENTA, null));

        TransferHandler handler = new TransferHandler() {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
            }
            
        };
        list.setTransferHandler(handler);
        final JXFrame frame = wrapWithScrollingInFrame(list, "release-after-drag must not trigger clicked");
        // rollover-enabled default renderer
        // Note JW: this implicitly changes the cursor to the hand-cursor
        DefaultListRenderer renderer = new DefaultListRenderer() {
            @Override
            public void doClick() {
                JOptionPane.showMessageDialog(frame, "Clicked");
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        list.setCellRenderer(renderer);
        JTextField textField = new JTextField("just something to drag ...", 40);
        textField.setDragEnabled(true);
        addStatusComponent(frame, textField);
        Action action = new AbstractAction("drag into me - am I called on release?") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("triggered..." + ((Component) e.getSource()).getName());
            }
        };
        JButton button = new JButton(action);
        button.setName("first");
        addStatusComponent(frame, button);
        JButton other = new JButton(action);
        other.setName("second");
        addStatusComponent(frame, other);
        show(frame);
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

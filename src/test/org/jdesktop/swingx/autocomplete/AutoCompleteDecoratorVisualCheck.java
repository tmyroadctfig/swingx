/*
 * $Id$
 *
 * Copyright 2008 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.autocomplete;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.test.AncientSwingTeam;

/**
 * @author Karl George Schaefer
 */
public class AutoCompleteDecoratorVisualCheck extends InteractiveTestCase {
    public static void main(String[] args) throws Exception {
        AutoCompleteDecoratorVisualCheck test = new AutoCompleteDecoratorVisualCheck();
        try {
            test.runInteractiveTests("interactiveForegroundColorCheck");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    public void interactiveCompletionAtTopTest() {
        JComboBox combo = new JComboBox(new String[] {
                "A1", "A2", "A3", "A4", "A5",
                "B1", "B2", "B3", "B4", "B5",
                "C1", "C2", "C3", "C4", "C5",
                "D1", "D2", "D3", "D4", "D5",
        });
        
        AutoCompleteDecorator.decorate(combo);
        
        JFrame frame = wrapInFrame(combo, "default decorator check");
        frame.pack();
        frame.setVisible(true);
    }
    
    public void interactiveCompletionOfEmptyCombo() {
        JComboBox combo = new JComboBox(new String[0]);
        
        AutoCompleteDecorator.decorate(combo);
        
        JFrame frame = wrapInFrame(combo, "empty combo");
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * SwingX 959: Ensure that {@code null} items are selectable.
     */
    public void interactiveCompletionOfNull() {
        JComboBox combo = new JComboBox(new String[] {
                "A1", "A2", null, "A4", "A5",
        });
        
        AutoCompleteDecorator.decorate(combo);
        
        JFrame frame = wrapInFrame(combo, "combo with null");
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Issue 1191: Ensure that a beep is not sounded when all of the items are removed.
     */
    public void interactiveEnsureNoBeepingForProgramaticRemoval() {
        final JComboBox combo = new JComboBox(new String[] {
                "A1", "A2", "A3", "A4", "A5",
        });
        
        AutoCompleteDecorator.decorate(combo);
        
        JFrame frame = wrapInFrame(combo, "combo with all items removed");
        frame.pack();
        frame.setVisible(true);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                combo.removeAllItems();
            }
        });
    }

    /**
     * Issue 1330: Ensure that a combo box will respond to the first key press. Had to work around
     * core (mis)behavior.
     */
    public void interactiveEnsureCellEditorRespondsToFirstKeyPress() {
        JXTable table = new JXTable(new AncientSwingTeam(5));
        table.setSurrendersFocusOnKeystroke(true);
        JXComboBox combo = new JXComboBox(new String[] {"Mark", "Tom", "Alan", "Jeff", "Amy"});
        AutoCompleteDecorator.decorate(combo);
        table.getColumn(0).setCellEditor(new ComboBoxCellEditor(combo));
        
        showInFrame(table, "Ensure Editor Responds to First Key Press");
    }
    
    /**
     * Issue 394: Mac is unable to perform keyboard navigation on decorated combo box.
     */
    public void interactiveKeyboardTest() {
        final JComboBox undecoratedBox = new JComboBox(new String[] {"ONE", "TWO", "THREE"});
        undecoratedBox.setEditable(true);
        final DefaultCellEditor undecoratedEditor = new DefaultCellEditor(undecoratedBox);

        final JComboBox decoratedBox = new JComboBox(new String[] {"ONE", "TWO", "THREE"});
        AutoCompleteDecorator.decorate(decoratedBox);
        final ComboBoxCellEditor decoratedEditor = new ComboBoxCellEditor(decoratedBox);

        final JXTable table = new JXTable() {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 0) {
                    return undecoratedEditor;
                }
                return decoratedEditor;
            }
        };
        table.setModel(new DefaultTableModel(new Object[][] { new Object[] { "ONE", "ONE" } },
                new Object[] { "1", "2" }));
        table.setSurrendersFocusOnKeystroke(true);

        showWithScrollingInFrame(table, "Keyboard Navigation Check");
    }

    /**
     * Issue 721: disabled foreground does not look the same for decorated combo box
     */
    public void interactiveForegroundColorCheck() {
        JPanel panel = new JPanel();
        final JComboBox comboNormal = new JComboBox(new String[] { "Normal Combo", "BB" });
        final JComboBox comboAuto = new JComboBox(new String[] { "AutoComplete", "BB" });

        comboNormal.setEnabled(false);
        comboAuto.setEnabled(false);
        
        AutoCompleteDecorator.decorate(comboAuto);
        comboAuto.setEditable(false);
        
        panel.add(comboNormal);
        panel.add(comboAuto);

        
        JXFrame frame = wrapInFrame(panel, "Disabled Foreground Check");
        addAction(frame, new AbstractAction("Enable Combos") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboAuto.isEnabled()) {
                    comboAuto.setEnabled(false);
                    comboNormal.setEnabled(false);
                    putValue(Action.NAME, "Enable Combos");
                } else {
                    comboAuto.setEnabled(true);
                    comboNormal.setEnabled(true);
                    putValue(Action.NAME, "Disable Combos");
                }
            }
        });
        show(frame);
    }

    /**
     * do nothing test - keep the testrunner happy.
     */
    public void testDummy() {
    }

}

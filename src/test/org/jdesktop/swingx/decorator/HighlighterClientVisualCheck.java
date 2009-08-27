/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class HighlighterClientVisualCheck extends InteractiveTestCase  {
    
    public static void main(String[] args) {
        HighlighterClientVisualCheck test = new HighlighterClientVisualCheck();
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Regression Issue ?? swingx: column highlighter change must update view.
     */
    public void interactiveColumnHighlighterChange() {
        final ColorHighlighter hl = new ColorHighlighter(HighlightPredicate.ODD, Color.RED, Color.BLACK);
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt(0).addHighlighter(hl);
        Action action = new AbstractAction("toggle column color") {

            public void actionPerformed(ActionEvent e) {
                Color old = hl.getBackground();
                hl.setBackground(old == Color.red ? Color.ORANGE : Color.RED);
               
            }
            
        };
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));
        JXFrame frame = wrapWithScrollingInFrame(table, "column highlighter update");
        addAction(frame, action);
        addMessage(frame, "toggle column color between orange/red must update immediately");
        show(frame);
    }


    
    /**
     * UI-dependent Column highlighter must updated on updateUI.
     */
    public void interactiveColumnHighlighterUpdateUI() {
        JXTable table = new JXTable(new AncientSwingTeam());
        table.getColumnExt(2).addHighlighter(new UIColorHighlighter());
        table.addHighlighter(new UIColorHighlighter(new ColumnHighlightPredicate(1)));
        showWithScrollingInFrame(table, "UpdateUI - table highlighter in second, column highlighter in third");
    }


}

/*
 * Created on 04.05.2005
 *
 */
package org.jdesktop.swingx;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.util.AncientSwingTeam;

/**
 * @author (C) 2004 Jeanette Winzenburg, Berlin
 * @version $Revision$ - $Date$
 */
public class DummyTable {
    public static void main(String[] args) {
        JXTable table = new JXTable(new AncientSwingTeam());
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(table));
        frame.pack();
        frame.show();
    }
}

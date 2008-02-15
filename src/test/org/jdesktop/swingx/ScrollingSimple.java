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
package org.jdesktop.swingx;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
import java.util.Date;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//Since we're adding a Swing component, we now need to
//extend JApplet. We need to be careful to access
//components only on the event-dispatching thread.
public class ScrollingSimple extends JApplet {

    JTextField field;
    private JXDatePicker datePicker;

    @Override
    public void init() {
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            System.err.println("createGUI didn't successfully complete");
        }

        addItem(false, "initializing... ");
    }

    private void createGUI() {        
        //Create the text field and make it uneditable.
        field = new JTextField();
        field.setEditable(false);

        //Set the layout manager so that the text field will be
        //as wide as possible.
        setLayout(new java.awt.GridLayout(1,0));
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //Add the text field to the applet.
        add(field);
        add(new JLabel("got it again - " + new Date()));
        datePicker = new JXDatePicker();
        add(datePicker);
    }

    @Override
    public void start() {
        addItem(false, "starting... ");
    }

    @Override
    public void stop() {
        addItem(false, "stopping... ");
    }

    @Override
    public void destroy() {
        addItem(false, "preparing for unloading...");
        cleanUp();
    }
    
    private void cleanUp() {
        //Execute a job on the event-dispatching thread:
        //taking the text field out of this applet.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    remove(field);
                    remove(datePicker);
                }
            });
        } catch (Exception e) {
            System.err.println("cleanUp didn't successfully complete");
        }
        field = null;
    }

    private void addItem(boolean alreadyInEDT, String newWord) {
        if (alreadyInEDT) {
            addItem(newWord);
        } else {
            final String word = newWord;
            //Execute a job on the event-dispatching thread:
            //invoking addItem(newWord).
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        addItem(word);
                    }
                });
            } catch (Exception e) {
                System.err.println("addItem didn't successfully complete");
            }
        }
    }
        
    //Invoke this method ONLY from the event-dispatching thread.
    private void addItem(String newWord) {
        String t = field.getText();
        System.out.println(newWord);
        field.setText(t + newWord);
    }
}
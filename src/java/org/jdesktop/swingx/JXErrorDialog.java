/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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

package org.jdesktop.swingx;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.*;

/**
 * Common Error Dialog.  Composed of a title, message, and details.

 * @author Richard Bair
 * @author Alexander Zuev
 */
public class JXErrorDialog extends JDialog {
    /**
     * Text representing expanding the details section of the dialog
     */
    private static final String DETAILS_EXPAND_TEXT = "Details >>";
    /**
     * Text representing contracting the details section of the dialog
     */
    private static final String DETAILS_CONTRACT_TEXT = "Details <<";
    /**
     * Text for the Ok button.
     */
    private static final String OK_BUTTON_TEXT = "Ok";
    /**
     * Icon for the error dialog (stop sign, etc)
     */
    private static final Icon icon = UIManager.getIcon("OptionPane.warningIcon");
    /**
     * Text for the reportError button
     */
    private static final String REPORT_BUTTON_TEXT = "Report...";

    /**
     * Error message label
     */
    private JLabel errorMessage;
    /**
     * details text area
     */
    private JTextArea details;
    /**
     * detail button
     */
    private JButton detailButton;
    /**
     * details scroll pane
     */
    private JScrollPane detailsScrollPane;
    /**
     * report an error button
     */
    private JButton reportButton;
    /**
     * Error reporting engine assigned for error reporting for all error dialogs
     */
    private static ErrorReporter reporter;
    /**
     * IncidentInfo that contains all the information prepared for
     * reporting.
     */
    private IncidentInfo incidentInfo;

    /**
     * Create a new ErrorDialog with the given Frame as the owner
     * @param owner
     */
    public JXErrorDialog(Frame owner) {
        super(owner, true);
        initGui();
    }

    /**
     * Create a new ErrorDialog with the given Dialog as the owner
     * @param owner
     */
    private JXErrorDialog(Dialog owner) {
        super(owner, true);
        initGui();
    }

    /**
     * initialize the gui.
     */
    private void initGui() {
        //initialize the gui
        GridBagLayout layout = new GridBagLayout();
        this.getContentPane().setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.insets = new Insets(22, 12, 11, 17);
        this.getContentPane().add(new JLabel(icon), gbc);

        errorMessage = new JLabel();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(12, 0, 0, 11);
        this.getContentPane().add(errorMessage, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(12, 0, 11, 5);
        JButton okButton = new JButton(OK_BUTTON_TEXT);
        this.getContentPane().add(okButton, gbc);

        reportButton = new JButton(new ReportAction());
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(12, 0, 11, 5);
        this.getContentPane().add(reportButton, gbc);
        reportButton.setVisible(false); // not visible by default

        detailButton = new JButton(DETAILS_EXPAND_TEXT);
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(12, 0, 11, 11);
        this.getContentPane().add(detailButton, gbc);

        details = new JTextArea(7, 60);
        detailsScrollPane = new JScrollPane(details);
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        details.setEditable(false);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(6, 11, 11, 11);
        this.getContentPane().add(detailsScrollPane, gbc);

        /*
         * Here i'm going to add invisible empty container to the bottom of the
         * content pane to fix minimal width of the dialog. It's quite a hack,
         * but i have not found anything better.
         */
        Dimension spPredictedSize = detailsScrollPane.getPreferredSize();
        Dimension newPanelSize =
                new Dimension(spPredictedSize.width+15, 0);
        Container widthHolder = new Container();
        widthHolder.setMinimumSize(newPanelSize);
        widthHolder.setPreferredSize(newPanelSize);
        widthHolder.setMaximumSize(newPanelSize);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 11, 11, 0);
        this.getContentPane().add(widthHolder, gbc);

        //make the buttons the same size
        int buttonLength = detailButton.getPreferredSize().width;
        int buttonHeight = detailButton.getPreferredSize().height;
        Dimension buttonSize = new Dimension(buttonLength, buttonHeight);
        okButton.setPreferredSize(buttonSize);
        reportButton.setPreferredSize(buttonSize);
        detailButton.setPreferredSize(buttonSize);

        //set the event handling
        okButton.addActionListener(new OkClickEvent());
        detailButton.addActionListener(new DetailsClickEvent());
    }

    /**
     * Set the details section of the error dialog.  If the details are either
     * null or an empty string, then hide the details button and hide the detail
     * scroll pane.  Otherwise, just set the details section.
     * @param details
     */
    private void setDetails(String details) {
        if (details == null || details.equals("")) {
            setDetailsVisible(false);
            detailButton.setVisible(false);
        } else {
            this.details.setText(details);
            setDetailsVisible(false);
            detailButton.setVisible(true);
        }
    }

    /**
     * Set the details section to be either visible or invisible.  Set the
     * text of the Details button accordingly.
     * @param b
     */
    private void setDetailsVisible(boolean b) {
        if (b) {
            details.setCaretPosition(0);
            detailsScrollPane.setVisible(true);
            detailButton.setText(DETAILS_CONTRACT_TEXT);
        } else {
            detailsScrollPane.setVisible(false);
            detailButton.setText(DETAILS_EXPAND_TEXT);
        }

        pack();
    }

    /**
     * Set the error message for the dialog box
     * @param errorMessage
     */
    private void setErrorMessage(String errorMessage) {
        this.errorMessage.setText(errorMessage);
    }

    /**
     * Sets the IncidentInfo for this dialog
     */
    private void setIncidentInfo(IncidentInfo info) {
        this.incidentInfo = info;
        this.reportButton.setVisible(getReporter() != null);
    }

    /**
     * Get curent dialog's IncidentInfo
     *
     * @return <code>IncidentInfo</code> assigned to this dialog
     */
    private IncidentInfo getIncidentInfo() {
        return incidentInfo;
    }

    /**
     * Listener for Ok button click events
     * @author Richard Bair
     */
    private final class OkClickEvent implements ActionListener {

        /* (non-Javadoc)
        * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
        */
        public void actionPerformed(ActionEvent e) {
            //close the window
            setVisible(false);
        }
    }

    /**
     * Listener for Details click events.  Alternates whether the details section
     * is visible or not.
     * @author Richard Bair
     */
    private final class DetailsClickEvent implements ActionListener {

        /* (non-Javadoc)
        * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
        */
        public void actionPerformed(ActionEvent e) {
            setDetailsVisible(!detailsScrollPane.isVisible());
        }
    }

    /**
     * Constructs and shows the error dialog for the given exception.  The exceptions message will be the
     * errorMessage, and the stacktrace will be the details.
     * @param owner
     * @param title
     * @param e
     */
    public static void showDialog(Window owner, String title, Throwable e) {
        IncidentInfo ii = new IncidentInfo(title, null, null, e);
        showDialog(owner, ii);
    }

    /**
     * Show the error dialog.
     * @param owner Owner of this error dialog.  This cannot be null.
     * @param title Title of the error dialog
     * @param errorMessage Message for the error dialog
     * @param details Details to be shown in the detail section of the dialog.  This can be null
     * if you do not want to display the details section of the dialog.
     */
    public static void showDialog(Window owner, String title, String errorMessage, String details) {
        IncidentInfo ii = new IncidentInfo(title, errorMessage, details);
        showDialog(owner, ii);
    }

    /**
     * Show the error dialog.
     * @param owner Owner of this error dialog.  This cannot be null.
     * @param info <code>IncidentInfo</code> that incorporates all the information about the error
     */
    public static void showDialog(Window owner, IncidentInfo info) {
        JXErrorDialog dlg;

        if (owner instanceof Dialog) {
            dlg = new JXErrorDialog((Dialog)owner);
        } else {
            dlg = new JXErrorDialog((Frame)owner);
        }
        dlg.setTitle(info.getHeader());
        dlg.setErrorMessage(info.getBasicErrorMessage());
        String details = info.getDetailedErrorMessage();
        if(details == null) {
            if(info.getErrorException() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                info.getErrorException().printStackTrace(pw);
                details = sw.toString();
            } else {
                details = "";
            }
        }
        dlg.setDetails(details);
        dlg.setIncidentInfo(info);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    /**
     * Returns the current reporting engine that will be used to report a problem if
     * user clicks on 'Report' button or null if no reporting engine set.
     *
     * @return reporting engine
     */
    public static ErrorReporter getReporter() {
        return reporter;
    }

    /**
     * Set reporting engine which will handle error reporting if user clicks 'report' button.
     *
     * @param rep <code>ErrorReporter</code> to be used or @null to turn reporting facility off
     */
    public static void setReporter(ErrorReporter rep) {
        reporter = rep;
    }

    /**
     * Action for report button
     */
    public class ReportAction extends AbstractAction {

        public boolean isEnabled() {
            return (getReporter() != null);
        }

        public void actionPerformed(ActionEvent e) {
            getReporter().reportIncident(getIncidentInfo());
        }

        public Object getValue(String key) {
            if(key == Action.NAME) {
                if(getReporter() != null && getReporter().getActionName() != null) {
                    return getReporter().getActionName();
                } else {
                    return REPORT_BUTTON_TEXT;
                }
            }
            return super.getValue(key);
        }
    }
}

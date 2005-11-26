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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * Common Error Dialog, suitable for representing information about 
 * errors and exceptions happened in application. The common usage of the 
 * <code>JXErrorDialog</code> is to show collected data about the incident and 
 * probably ask customer for a feedback. The data about the incident consists 
 * from the title which will be displayed in the dialog header, short 
 * description of the problem that will be immediately seen after dialog is 
 * became visible, full description of the problem which will be visible after
 * user clicks "Details" button and Throwable that contains stack trace and
 * another usable information that may be displayed in the dialog.<p>
 *
 * To ask user for feedback extend abstract class <code>ErrorReporter</code> and
 * set your reporter using <code>setReporter</code> method. Report button will 
 * be added to the dialog automatically.<br>
 * See {@link MailErrorReporter MailErrorReporter} documentation for the
 * example of error reporting usage.<p>

 * For example, to show simple <code>JXErrorDialog</code> call <br>
 * <code>JXErrorDialog.showDialog(null, "Application Error", 
 *   "The application encountered the unexpected error,
 *    please contact developers")</code>
 *
 * <p>Internationalization is handled via a resource bundle or via the UIManager
 * bidi orientation (usefull for right to left languages) is determined in the 
 * same way as the JOptionPane where the orientation of the parent component is
 * picked. So when showDialog(Component cmp, ...) is invoked the component 
 * orientation of the error dialog will match the component orientation of cmp.

 * @author Richard Bair
 * @author Alexander Zuev
 * @author Shai Almog
 */
public class JXErrorDialog extends JDialog {
    /**
     * Used as a prefix when pulling data out of UIManager for i18n
     */
    private static String CLASS_NAME;
    /**
     * Icon for the error dialog (stop sign, etc)
     */
    private static final Icon icon = UIManager.getIcon("OptionPane.warningIcon");
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
    private EqualSizeJButton detailButton;
    /**
     * details scroll pane
     */
    private JScrollPane detailsScrollPane;
    /**
     * report an error button
     */
    private EqualSizeJButton reportButton;
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
     * Creates initialize the UIManager with localized strings
     */
    static {
        // Popuplate UIDefaults with the localizable Strings we will use
        // in the Login panel.
        CLASS_NAME = JXErrorDialog.class.getCanonicalName();
        String lookup;
        ResourceBundle res = ResourceBundle.getBundle("org.jdesktop.swingx.plaf.resources.ErrorDialog");
        Enumeration<String> keys = res.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            lookup = CLASS_NAME + "." + key;
            if (UIManager.getString(lookup) == null) {
                UIManager.put(lookup, res.getString(key));
            }
        }
    }
    
    
    /**
     * Create a new ErrorDialog with the given Frame as the owner
     * @param owner Owner of this error dialog.
     */
    public JXErrorDialog(Frame owner) {
        super(owner, true);
        initGui();
    }

    /**
     * Create a new ErrorDialog with the given Dialog as the owner
     * @param owner Owner of this error dialog.
     */
    public JXErrorDialog(Dialog owner) {
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
        gbc.anchor = GridBagConstraints.LINE_START;
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
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(12, 0, 11, 5);
        EqualSizeJButton okButton = new EqualSizeJButton(UIManager.getString(CLASS_NAME + ".ok_button_text"));
        this.getContentPane().add(okButton, gbc);

        reportButton = new EqualSizeJButton(new ReportAction());
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(12, 0, 11, 5);
        this.getContentPane().add(reportButton, gbc);
        reportButton.setVisible(false); // not visible by default

        detailButton = new EqualSizeJButton(UIManager.getString(CLASS_NAME + ".details_expand_text"));
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
        EqualSizeJButton[] buttons = new EqualSizeJButton[] {
                detailButton, okButton, reportButton };
        okButton.setGroup(buttons);
        reportButton.setGroup(buttons);
        detailButton.setGroup(buttons);

        //set the event handling
        okButton.addActionListener(new OkClickEvent());
        detailButton.addActionListener(new DetailsClickEvent());
    }

    /**
     * Set the details section of the error dialog.  If the details are either
     * null or an empty string, then hide the details button and hide the detail
     * scroll pane.  Otherwise, just set the details section.
     * @param details  Details to be shown in the detail section of the dialog.  This can be null
     * if you do not want to display the details section of the dialog.
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
     * @param b if true details section will be visible
     */
    private void setDetailsVisible(boolean b) {
        if (b) {
            details.setCaretPosition(0);
            detailsScrollPane.setVisible(true);
            detailButton.setText(UIManager.getString(CLASS_NAME + ".details_contract_text"));
            detailsScrollPane.applyComponentOrientation(detailButton.getComponentOrientation());
            
            // workaround for bidi bug, if the text is not set "again" and the component orientation has changed
            // then the text won't be aligned correctly. To reproduce this (in JDK 1.5) show two dialogs in one
            // use LTOR orientation and in the second use RTOL orientation and press "details" in both.
            // Text in the text box should be aligned to right/left respectively, without this line this doesn't
            // occure I assume because bidi properties are tested when the text is set and are not updated later
            // on when setComponentOrientation is invoked.
            details.setText(details.getText());
        } else {
            detailsScrollPane.setVisible(false);
            detailButton.setText(UIManager.getString(CLASS_NAME + ".details_expand_text"));
        }

        pack();
        repaint();
    }

    /**
     * Set the error message for the dialog box
     * @param errorMessage Message for the error dialog
     */
    private void setErrorMessage(String errorMessage) {
        this.errorMessage.setText(errorMessage);
    }

    /**
     * Sets the IncidentInfo for this dialog
     *
     * @param info IncidentInfo that incorporates all the details about the error
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
            dispose();
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
     * @param owner Owner of this error dialog. Determines the Window in which the dialog
     *		is displayed; if the <code>owner</code> has
     *		no <code>Window</code>, a default <code>Frame</code> is used
     * @param title Title of the error dialog
     * @param e Exception that contains information about the error cause and stack trace
     */
    public static void showDialog(Component owner, String title, Throwable e) {
        IncidentInfo ii = new IncidentInfo(title, null, null, e);
        showDialog(owner, ii);
    }

    /**
     * Constructs and shows the error dialog for the given exception.  The exceptions message is specified,
     * and the stacktrace will be the details.
     * @param owner Owner of this error dialog. Determines the Window in which the dialog
     *		is displayed; if the <code>owner</code> has
     *		no <code>Window</code>, a default <code>Frame</code> is used
     * @param title Title of the error dialog
     * @param errorMessage Message for the error dialog
     * @param e Exception that contains information about the error cause and stack trace
     */
    public static void showDialog(Component owner, String title, String errorMessage, Throwable e) {
        IncidentInfo ii = new IncidentInfo(title, errorMessage, null, e);
        showDialog(owner, ii);
    }

    /**
     * Show the error dialog.
     * @param owner Owner of this error dialog. Determines the Window in which the dialog
     *		is displayed; if the <code>owner</code> has
     *		no <code>Window</code>, a default <code>Frame</code> is used
     * @param title Title of the error dialog
     * @param errorMessage Message for the error dialog
     * @param details Details to be shown in the detail section of the dialog.  This can be null
     * if you do not want to display the details section of the dialog.
     */
    public static void showDialog(Component owner, String title, String errorMessage, String details) {
        IncidentInfo ii = new IncidentInfo(title, errorMessage, details);
        showDialog(owner, ii);
    }

    /**
     * Show the error dialog.
     * @param owner Owner of this error dialog. Determines the Window in which the dialog
     *		is displayed; if the <code>owner</code> has
     *		no <code>Window</code>, a default <code>Frame</code> is used
     * @param info <code>IncidentInfo</code> that incorporates all the information about the error
     */
    public static void showDialog(Component owner, IncidentInfo info) {
        JXErrorDialog dlg;

        Window window = WindowUtils.findWindow(owner);

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
        dlg.applyComponentOrientation(owner.getComponentOrientation());
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.pack();
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    /**
     * Returns the current reporting engine that will be used to report a problem if
     * user clicks on 'Report' button or <code>null</code> if no reporting engine set.
     *
     * @return reporting engine
     */
    public static ErrorReporter getReporter() {
        return reporter;
    }

    /**
     * Set reporting engine which will handle error reporting if user clicks 'report' button.
     *
     * @param rep <code>ErrorReporter</code> to be used or <code>null</code> to turn reporting facility off.
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
                    return UIManager.getString(CLASS_NAME + ".report_button_text");
                }
            }
            return super.getValue(key);
        }
    }
    
    /**
     * This is a button that maintains the size of the largest button in the button
     * group by returning the largest size from the getPreferredSize method.
     * This is better than using setPreferredSize since this will work regardless
     * of changes to the text of the button and its language.
     */
    private static class EqualSizeJButton extends JButton {
        public EqualSizeJButton() {
        }

        public EqualSizeJButton(String text) {
            super(text);
        }
        
        public EqualSizeJButton(Action a) {
            super(a);
        }
        
        /**
         * Buttons whose size should be taken into consideration
         */
        private EqualSizeJButton[] group;
        
        public void setGroup(EqualSizeJButton[] group) {
            this.group = group;
        }

        /**
         * Returns the actual preferred size on a different instance of this button
         */
        private Dimension getRealPreferredSize() {
            return super.getPreferredSize();
        }
        
        /**
         * If the <code>preferredSize</code> has been set to a
         * non-<code>null</code> value just returns it.
         * If the UI delegate's <code>getPreferredSize</code>
         * method returns a non <code>null</code> value then return that;
         * otherwise defer to the component's layout manager.
         * 
         * @return the value of the <code>preferredSize</code> property
         * @see #setPreferredSize
         * @see ComponentUI
         */
        public Dimension getPreferredSize() {
            int width = 0;
            int height = 0;
            for(int iter = 0 ; iter < group.length ; iter++) {
                Dimension size = group[iter].getRealPreferredSize();
                width = Math.max(size.width, width);
                height = Math.max(size.height, height);
            }
            
            return new Dimension(width, height);
        }
        
    }
    
    public static void main(String[] args) {
        javax.swing.JFrame frm = new javax.swing.JFrame();
        showDialog(frm, "Application Error", 
            "The application encountered the unexpected error", "please contact developers");
        frm.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        showDialog(frm, "Application Error Hebrew", 
            "The application encountered the unexpected error", "please contact developers");
        System.exit(0);
    }
}

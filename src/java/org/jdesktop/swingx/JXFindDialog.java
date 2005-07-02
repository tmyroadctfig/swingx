/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class JXFindDialog extends JDialog {

    private Searchable searchable;

    private JTextField findText;
    private JCheckBox matchCheck;
    private JCheckBox wrapCheck;
    private JCheckBox backCheck;

    private Pattern pattern;
    private Matcher matcher;

    private boolean DEBUG = true;

    public JXFindDialog(Searchable searchable) {
        super((Frame)SwingUtilities.getWindowAncestor((Component)searchable),
              "Find in this component");
        this.searchable = searchable;

        GraphicsConfiguration gc =
            GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        Rectangle bounds = gc.getBounds();
        int x = bounds.x+bounds.width/3;
        int y = bounds.y+bounds.height/3;

        setLocation(x, y);

        initUI();
        pack();
    }

    /**
     * Set the debug flag. Mostly for testing and diagnostics.
     */
    public void setDebug(boolean debug) {
        this.DEBUG = debug;
    }

    private void initUI() {
        getContentPane().add(createFieldPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

        // Implementation of incremental search
        /*
          findText.getDocument().addDocumentListener(new DocumentListener() {
          public void insertUpdate(DocumentEvent evt) {
          doFind();
          }

          public void removeUpdate(DocumentEvent evt) {
          doFind(true);
          }

          public void changedUpdate(DocumentEvent evt) {
          }
          });
        */
    }

    /**
     * TODO: Strings should be removed from the UI
     */
    private JComponent createFieldPanel() {

        // Create components
        JLabel label = new JLabel("Find Text: ");
        label.setDisplayedMnemonicIndex(2);
        label.setLabelFor(findText);

        findText = new JTextField();
        matchCheck = new JCheckBox(new MatchAction());
        wrapCheck = new JCheckBox(new WrapAction());
        backCheck = new JCheckBox(new BackwardAction());

        Box lBox = Box.createVerticalBox();
        lBox.add(label);
        lBox.add(Box.createGlue());

        Box rBox = Box.createVerticalBox();
        rBox.add(findText);
        rBox.add(matchCheck);
        rBox.add(wrapCheck);
        rBox.add(backCheck);

        Box box = Box.createHorizontalBox();
        box.add(lBox);
        box.add(rBox);

        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return box;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        Action findAction = new FindAction();
        Action closeAction = new CloseAction();

        JButton findButton;
        panel.add(findButton = new JButton(findAction));
        panel.add(new JButton(closeAction));

        // Bind the ESC key to CloseAction and ENTER to FindAction
        String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
        String ENTER_ACTION_KEY = "ENTER_ACTION_KEY";

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(enterKey, ENTER_ACTION_KEY);
        inputMap.put(escapeKey, CANCEL_ACTION_KEY);

        getRootPane().setDefaultButton(findButton);

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(ENTER_ACTION_KEY, findAction);
        actionMap.put(CANCEL_ACTION_KEY, closeAction);

        return panel;
    }

    private int lastIndex = -1;

    /**
     * Action callback for Find action.
     */
    public void doFind() {
        doFind(getBackwardsFlag());
    }

    public void doFind(boolean backwards) {
        Pattern pattern = getPattern();
        // XXX
        //  System.out.println("doFind: " + findText.getText() + ", " + backwards);
        lastIndex = searchable.search(getPattern(), lastIndex, backwards);
        if (lastIndex == -1) {
            JOptionPane.showMessageDialog(this, "Value not found");
        }
    }

    /**
     * Action callback for Close action.
     */
    public void doClose() {
        JXFindDialog.this.dispose();
    }

    public boolean getMatchFlag() {
        return matchCheck.isSelected();
    }

    /**
     * Public method for testing.
     * <p>
     * TODO: The state should probably be encapsulated by a model rather
     * that within the UI components.
     */
    public void setMatchFlag(boolean flag) {
        matchCheck.setSelected(flag);
    }

    public boolean getWrapFlag() {
        return wrapCheck.isSelected();
    }

    public void setWrapFlag(boolean flag) {
        wrapCheck.setSelected(flag);
    }

    public boolean getBackwardsFlag() {
        return backCheck.isSelected();
    }

    public void setBackwardsFlag(boolean flag) {
        backCheck.setSelected(flag);
    }

    private Pattern getPattern() {
        String searchString = findText.getText();
        if (searchString.length() == 0) {
            return null;
        }
        if (pattern == null || !pattern.pattern().equals(searchString)) {
            // TODO: check to see if the existing pattern.flags() state matches
            // getMatchFlag
            pattern = Pattern.compile(searchString,
                                      getMatchFlag() ? 0 : Pattern.CASE_INSENSITIVE);
            // Start from the beginning.
            lastIndex = -1;
        }
        return pattern;
    }

    private class FindAction extends AbstractAction {
        public FindAction() {
            super("Find");
        }
        public void actionPerformed(ActionEvent evt) {
            doFind();
        }
    }

    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super("Close");
        }

        public void actionPerformed(ActionEvent evt) {
            if (DEBUG) {
                System.err.println(this.getValue(Action.NAME));
            }
            doClose();
        }
    }

    private abstract class CheckAction extends AbstractAction {

        public CheckAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if (DEBUG) {
                System.err.println(this.getValue(Action.NAME));
            }
        }
    }

    private class MatchAction extends CheckAction {
        public MatchAction() {
            super("Match upper/lower case");
            putValue(Action.MNEMONIC_KEY, new Integer('M'));
        }
    }

    private class WrapAction extends CheckAction {
        public WrapAction() {
            super("Wrap around");
            putValue(Action.MNEMONIC_KEY, new Integer('W'));
        }
    }

    private class BackwardAction extends CheckAction {
        public BackwardAction() {
            super("Search Backwards");
            putValue(Action.MNEMONIC_KEY, new Integer('B'));
        }
    }

}

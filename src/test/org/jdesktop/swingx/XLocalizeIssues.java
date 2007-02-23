/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Test to expose known issues around <code>Locale</code> setting.
 * 
 * Ideally, there would be at least one failing test method per open
 * Issue in the issue tracker. Plus additional failing test methods for
 * not fully specified or not yet decided upon features/behaviour.
 * 
 * @author Jeanette Winzenburg
 */
public class XLocalizeIssues extends InteractiveTestCase {
    private static final Logger LOG = Logger.getLogger(XLocalizeIssues.class
            .getName());
    private static final Locale DEFAULT_LOCALE = Locale.US;
    private static final Locale ALTERNATIVE_LOCALE = Locale.GERMAN;


    private Locale originalLocale;
    // test scope is static anyway...
    static {
        // force the addon to load
        LookAndFeelAddons.getAddon();
    }
    public static void main(String[] args) {
//      setSystemLF(true);
      XLocalizeIssues test = new XLocalizeIssues();
      try {
        test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*File.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }

    }

    @Override
    protected void setUp() throws Exception {
        originalLocale = Locale.getDefault();
        super.setUp();
    }
    
    

    @Override
    protected void tearDown() throws Exception {
        Locale.setDefault(originalLocale);
        super.tearDown();
    }

    /**
     * Issue #459-swingx: JXTable setLocale doesn't update localized column
     * control properties.
     * 
     * Pass/fail expectation:
     * <ul>
     * <li> fails always with jdk5 independent of LookAndFeelAddon resource
     * bundle registration.
     * <li> fails with jdk6 and LookAndFeelAddon copy resource bundle values.
     * <li> passes with jdk6 and LookAndFeelAddon addResourceBundle.
     * </ul>
     */
    public void testLocaleColumnControl() {
       assertLocaleActionUpdate(JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
       assertLocaleActionUpdate(JXTable.PACKALL_ACTION_COMMAND);
       assertLocaleActionUpdate(JXTable.PACKSELECTED_ACTION_COMMAND);
    }

    private void assertLocaleActionUpdate(String actionCommand) {
        JXTable table = new JXTable(10, 2);
        Action action = table.getActionMap().get(actionCommand);
        String name = (String) action.getValue(Action.NAME);
        String uiValue = UIManager.getString("JXTable." + actionCommand, table
                .getLocale());
        // sanity
        assertNotNull(uiValue);
        assertEquals(name, uiValue);
        Locale alternative = ALTERNATIVE_LOCALE;
        if (alternative.getLanguage().equals(table.getLocale().getLanguage())) {
            alternative = DEFAULT_LOCALE;
        }
        table.setLocale(alternative);
        String altUIValue = UIManager.getString("JXTable." + actionCommand,
                table.getLocale());
        // sanity
        assertNotNull(altUIValue);
        // sanity to track unexpected failure during refactoring
        assertFalse("new uiValue  must be different: " + uiValue + "/"
                + altUIValue, uiValue.equals(altUIValue));
        String altName = (String) action.getValue(Action.NAME);
        // here are the real asserts
        assertFalse("new action name must be different: " + name + "/"
                + altName, name.equals(altName));
        assertEquals(altName, altUIValue);
    }
    
    /**
     * Issue #466-swingx: locale-dependent values not accessible. This looks
     * like a side-effect of the first go on #159-swingx (not all values of
     * resourceBundle available).
     * <p>
     * 
     * Could be core problem (in jdk5, fixed in jdk6) around classloader and
     * ResourceBundle: <a
     * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4834404">
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4834404 </a>
     * 
     * 
     */
    public void testGetLocaleUIDefaults() {
        String key = "JXTable.column.packAll";
        Object alternativeValue = UIManager.get(key, ALTERNATIVE_LOCALE);
        // sanity - the value must be available
        assertNotNull(alternativeValue);
        Object defaultValue = UIManager.get(key, DEFAULT_LOCALE);
        // sanity - the value must be available
        assertNotNull(defaultValue);
        assertFalse("values must be different: " + defaultValue + "/" + alternativeValue, defaultValue.equals(alternativeValue));
    }
    
    /**
     * Issue #459-swingx: columnControl properties not updated on locale setting.
     *
     */
    public void interactiveLocaleColumnControl() {
        final JXTable table = new JXTable(10, 4);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setTitle(table.getLocale().getLanguage());
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = table.getLocale();
                table.setLocale(old == DEFAULT_LOCALE ? ALTERNATIVE_LOCALE : DEFAULT_LOCALE);
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(table, "toggle locale on table - column control not updated");
        addAction(frame, toggleLocale);
        frame.setVisible(true);
    }
    
    /**
     * similar to Issue #459-swingx: findPanel properties not updated on setLocale.
     *
     */
    public void interactiveLocaleFindPanel() {
        final JXTable table = new JXTable(10, 4);
        table.setColumnControlVisible(true);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table, "toggle locale on default - FindPanel");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == DEFAULT_LOCALE ? ALTERNATIVE_LOCALE : DEFAULT_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
                
            }
            
        };
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                JXFindPanel errorPane = SearchFactory.getInstance().getSharedFindPanel();
                
                // we are fine if the chooser is re-created in each call
                // _and_ the JComponent.defaultLocale is kept in synch with
                // with Locale.getDefault()
                // JFileChooser chooser = new JFileChooser();
                // otherwise we have to update the chooser's locale manually
                if (!Locale.getDefault().equals(errorPane.getLocale())) {
                    errorPane.setLocale(Locale.getDefault());
                    // need to explicitly trigger re-install to pick up the new
                    // locale-dependent state
                    // this throws java error - UIDefaults.getUI() failed ... ??
                    errorPane.updateUI();
                }
                SearchFactory.getInstance().showFindDialog(table, table.getSearchable());
            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }

    /**
     * similar to Issue #459-swingx: errorPane properties not updated on setLocale.<p>
     * 
     * Seem to have some unrelated open issues ...
     *
     */
    public void interactiveErrorPane() {
        final JXTable table = new JXTable(10, 3);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "ErrorPane and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == DEFAULT_LOCALE ? ALTERNATIVE_LOCALE
                        : DEFAULT_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());

            }

        };
        final JXErrorPane errorPane = new JXErrorPane();
        // work around issue #??-swingx: errorPane must cope with null errorInfo.
        errorPane.setErrorInfo(new ErrorInfo("title", "xxxx-yyy", null, null, null, null, null));
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                // we are fine if the chooser is re-created in each call
                // _and_ the JComponent.defaultLocale is kept in synch with
                // with Locale.getDefault()
                // JFileChooser chooser = new JFileChooser();
                // otherwise we have to update the chooser's locale manually
                if (!Locale.getDefault().equals(errorPane.getLocale())) {
                    errorPane.setLocale(Locale.getDefault());
                    // need to explicitly trigger re-install to pick up the new
                    // locale-dependent state
                    // this throws java error - UIDefaults.getUI() failed ... ??
                    errorPane.updateUI();
                }
                JXErrorPane.showDialog(frame, errorPane);
            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }

//------------------ core components for comparison.
    
    /**
     * Trying to understand how core Swing handles <code>Locale</code>
     * in <code>JFileChooser</code>.
     * <p>
     * 
     * Not very intuitive: the default locale for all J** is controlled by the
     * static JComponent.getDefaultLocale() which is automatically set the very
     * first time any component is instantiated to the Locale.getDefault().
     * Changing the Locale-defined default later on has no effect, must do so
     * explicitly
     * <p>
     * 
     * Changing a component's Locale after instantiating is not trivial as well:
     * need to explicitly call updateUI to trigger the ui-delegate into action.
     * <p>
     * 
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4423439">
     * Core bug JFileChooser </a>
     * 
     */
    public void interactiveFileChooser() {
        final JXTable table = new JXTable(10, 3);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "FileChooser and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == DEFAULT_LOCALE ? ALTERNATIVE_LOCALE
                        : DEFAULT_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                UIManager.getDefaults().setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());

            }

        };
        final JFileChooser chooser = new JFileChooser();
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                // we are fine if the chooser is re-created in each call
                // _and_ the JComponent.defaultLocale is kept in synch with
                // with Locale.getDefault()
                // JFileChooser chooser = new JFileChooser();
                // otherwise we have to update the chooser's locale manually
                if (!Locale.getDefault().equals(chooser.getLocale())) {
                    chooser.setLocale(Locale.getDefault());
                    // need to explicitly trigger re-install to pick up the new
                    // locale-dependent state
                    chooser.updateUI();
                }
                chooser.showOpenDialog(frame);
            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }
    /**
     * Trying to understand how core Swing handles <code>Locale</code>
     * switching in <code>JOptionPane</code>.
     * <p>
     * 
     * Not very intuitive: the default locale for all J** is controlled by the
     * static JComponent.getDefaultLocale() which is automatically set the very
     * first time any component is instantiated to the Locale.getDefault().
     * Changing the Locale-defined default later on has no effect.
     * <p>
     * 
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4884480 ">
     * Core bug JOptionPane</a>
     * 
     */
    public void interactiveOptionPane() {
        final JXTable table = new JXTable(10, 3);
        table.getColumnExt(0).setTitle(Locale.getDefault().getLanguage());
        final JXFrame frame = wrapWithScrollingInFrame(table,
                "JOptionPane and default locale?");
        Action toggleLocale = new AbstractActionExt("toggleLocale") {

            public void actionPerformed(ActionEvent e) {
                Locale old = Locale.getDefault();
                Locale.setDefault(old == DEFAULT_LOCALE ? ALTERNATIVE_LOCALE
                        : DEFAULT_LOCALE);
                // make sure newly created comps get the new locale by default
                // Note: this does not effect components which are already
                // created
                JComponent.setDefaultLocale(Locale.getDefault());
                table.getColumnExt(0).setTitle(
                        Locale.getDefault().getLanguage());

            }

        };
        Action open = new AbstractActionExt("open") {

            public void actionPerformed(ActionEvent e) {
                // title not localized ...
                JOptionPane.showConfirmDialog(frame, "abcdefghijklmnopqrstxyz - 123456789");

            }

        };
        addAction(frame, toggleLocale);
        addAction(frame, open);
        frame.setVisible(true);
    }
}

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.UIManager;

import org.jdesktop.swingx.plaf.JXTipOfTheDayAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.TipOfTheDayUI;
import org.jdesktop.swingx.tips.DefaultTipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel;
import org.jdesktop.swingx.tips.TipOfTheDayModel.Tip;

/**
 * Provides the "Tip of The Day" pane and dialog.<br>
 * Tips are retrieved from the {@link org.jdesktop.swingx.tips.TipOfTheDayModel}. In the
 * most common usage, a tip (as returned by
 * {@link org.jdesktop.swingx.tips.TipOfTheDayModel.Tip#getTip()}) is just a
 * <code>String</code>. However, the return type of this method is actually
 * <code>Object</code>. Its interpretation depends on its type:
 * <dl compact>
 * <dt>Component
 * <dd>The <code>Component</code> is displayed in the dialog.
 * <dt>Icon
 * <dd>The <code>Icon</code> is wrapped in a <code>JLabel</code> and
 * displayed in the dialog.
 * <dt>others
 * <dd>The object is converted to a <code>String</code> by calling its
 * <code>toString</code> method. The result is wrapped in a
 * <code>JEditorPane</code> or <code>JTextArea</code> and displayed.
 * </dl>
 * 
 * @author Frederic Lavigne
 */
public class JXTipOfTheDay extends JXPanel {

  public final static String uiClassID = "swingx/TipOfTheDayUI";

  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JXTipOfTheDayAddon());
  }

  /**
   * Key used to store the status of the "Show tip on startup" checkbox"
   */
  public static final String PREFERENCE_KEY = "ShowTipOnStartup";

  /**
   * Used when generating PropertyChangeEvents for the "currentTip" property
   */
  public static final String CURRENT_TIP_CHANGED_KEY = "currentTip";

  private TipOfTheDayModel model;
  private int currentTip = 0;

  /**
   * Constructs a new <code>JXTipOfTheDay</code> with an empty
   * TipOfTheDayModel
   */
  public JXTipOfTheDay() {
    this(new DefaultTipOfTheDayModel(new Tip[0]));
  }
  
  /**
   * Constructs a new <code>JXTipOfTheDay</code> showing tips from the given
   * TipOfTheDayModel.
   * 
   * @param model
   */
  public JXTipOfTheDay(TipOfTheDayModel model) {
    this.model = model;
    updateUI();
  }

  /**
   * Notification from the <code>UIManager</code> that the L&F has changed.
   * Replaces the current UI object with the latest version from the
   * <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    setUI((TipOfTheDayUI)LookAndFeelAddons.getUI(this, TipOfTheDayUI.class,
      UIManager.getUI(this)));
  }

  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui
   *          the <code>TipOfTheDayUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   *           implements the taskpane group's LookAndFeel.
   */
  public void setUI(TipOfTheDayUI ui) {
    super.setUI(ui);
  }

  /**
   * Gets the UI object which implements the L&F for this component.
   * 
   * @return the TipOfTheDayUI object that implements the TipOfTheDayUI L&F
   */
  public TipOfTheDayUI getUI() {
    return (TipOfTheDayUI)ui;
  }

  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string {@link #uiClassID}
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return uiClassID;
  }

  public TipOfTheDayModel getModel() {
    return model;
  }

  public void setModel(TipOfTheDayModel model) {
    if (model == null) {
      throw new IllegalArgumentException("model can not be null");
    }
    TipOfTheDayModel old = this.model;
    this.model = model;
    firePropertyChange("model", old, model);
  }

  public int getCurrentTip() {
    return currentTip;
  }

  /**
   * Sets the index of the tip to show
   * 
   * @param currentTip
   * @throw IllegalArgumentException if currentTip is not within the bounds [0,
   *        getModel().getTipCount()[.
   */
  public void setCurrentTip(int currentTip) {
    if (currentTip < 0 || currentTip >= getModel().getTipCount()) {
      throw new IllegalArgumentException(
      "Current tip must be within the bounds [0, " + getModel().getTipCount()
        + "[");
    }

    int oldTip = this.currentTip;
    this.currentTip = currentTip;
    firePropertyChange(CURRENT_TIP_CHANGED_KEY, oldTip, currentTip);
  }

  /**
   * Shows the next tip in the list. It cycles the tip list.
   */
  public void nextTip() {
    int count = getModel().getTipCount();
    if (count == 0) { return; }

    int nextTip = currentTip + 1;
    if (nextTip >= count) {
      nextTip = 0;
    }
    setCurrentTip(nextTip);
  }

  /**
   * Shows the previous tip in the list. It cycles the tip list.
   */
  public void previousTip() {
    int count = getModel().getTipCount();
    if (count == 0) { return; }

    int previousTip = currentTip - 1;
    if (previousTip < 0) {
      previousTip = count - 1;
    }
    setCurrentTip(previousTip);
  }

  /**
   * Pops up a "Tip of the day" dialog.
   * 
   * @param parentComponent
   * @exception HeadlessException
   *              if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  public void showDialog(Component parentComponent) throws HeadlessException {
    showDialog(parentComponent, (ShowOnStartupChoice)null);
  }

  /**
   * Pops up a "Tip of the day" dialog. Additionally, it saves the state of the
   * "Show tips on startup" checkbox in a key named "ShowTipOnStartup" in the
   * given Preferences.
   * 
   * @param parentComponent
   * @param showOnStartupPref
   * @exception HeadlessException
   *              if GraphicsEnvironment.isHeadless() returns true.
   * @throws IllegalArgumentException
   *           if showOnStartupPref is null
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @return true if the user chooses to see the tips again, false otherwise.
   */
  public boolean showDialog(Component parentComponent,
    Preferences showOnStartupPref) throws HeadlessException {
    return showDialog(parentComponent, showOnStartupPref, false);
  }
  
  /**
   * Pops up a "Tip of the day" dialog. Additionally, it saves the state of the
   * "Show tips on startup" checkbox in a key named "ShowTipOnStartup" in the
   * given Preferences.
   * 
   * @param parentComponent
   * @param showOnStartupPref
   * @param force
   *          if true, the dialog is displayed even if the Preferences is set to
   *          hide the dialog
   * @exception HeadlessException
   *              if GraphicsEnvironment.isHeadless() returns true.
   * @throws IllegalArgumentException
   *           if showOnStartupPref is null
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @return true if the user chooses to see the tips again, false
   *         otherwise.
   */
  public boolean showDialog(Component parentComponent,
    final Preferences showOnStartupPref, boolean force) throws HeadlessException {
    if (showOnStartupPref == null) { throw new IllegalArgumentException(
      "Preferences can not be null"); }

    ShowOnStartupChoice store = new ShowOnStartupChoice() {
      public boolean isShowingOnStartup() {
        return showOnStartupPref.getBoolean(PREFERENCE_KEY, true);
      }
      public void setShowingOnStartup(boolean showOnStartup) {
        // only save the choice if it is negative
        if (!showOnStartup) {
          showOnStartupPref.putBoolean(PREFERENCE_KEY, showOnStartup);
        }
      }
    };
    return showDialog(parentComponent, store, force);
  }

  /**
   * Pops up a "Tip of the day" dialog.
   * 
   * If <code>choice</code> is not null, the method first checks if
   * {@link ShowOnStartupChoice#isShowingOnStartup()} is true before showing the
   * dialog.
   * 
   * Additionally, it saves the state of the "Show tips on startup" checkbox
   * using the given {@link ShowOnStartupChoice} object.
   * 
   * @param parentComponent
   * @param choice
   * @exception HeadlessException
   *              if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @return true if the user chooses to not the tips again, false otherwise.
   */
  public boolean showDialog(Component parentComponent,
    ShowOnStartupChoice choice) {
    return showDialog(parentComponent, choice, false);
  }

  /**
   * Pops up a "Tip of the day" dialog.
   * 
   * If <code>choice</code> is not null, the method first checks if
   * <code>force</code> is true or if
   * {@link ShowOnStartupChoice#isShowingOnStartup()} is true before showing the
   * dialog.
   * 
   * Additionally, it saves the state of the "Show tips on startup" checkbox
   * using the given {@link ShowOnStartupChoice} object.
   * 
   * @param parentComponent
   * @param choice
   * @param force
   *          if true, the dialog is displayed even if
   *          {@link ShowOnStartupChoice#isShowingOnStartup()} is false
   * @exception HeadlessException
   *              if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @return true if the user chooses to see the tips again, false otherwise.
   */
  public boolean showDialog(Component parentComponent,
    ShowOnStartupChoice choice, boolean force) {    
    if (choice == null) {
      JDialog dialog = createDialog(parentComponent, choice);
      dialog.setVisible(true);
      dialog.dispose();
      return true;
    } else if (force || choice.isShowingOnStartup()) {
      JDialog dialog = createDialog(parentComponent, choice);
      dialog.setVisible(true);
      dialog.dispose();
      return choice.isShowingOnStartup();
    } else {
      return false;
    }
  }
  
  /**
   * @param showOnStartupPref
   * @return true if the key named "ShowTipOnStartup" is not set to false
   */
  public static boolean isShowingOnStartup(Preferences showOnStartupPref) {
    return showOnStartupPref.getBoolean(PREFERENCE_KEY, true);
  }

  /**
   * Removes the value set for "ShowTipOnStartup" in the given Preferences to
   * ensure the dialog shown by a later call to
   * {@link #showDialog(Component, Preferences)} will be visible to the user.
   * 
   * @param showOnStartupPref
   */
  public static void forceShowOnStartup(Preferences showOnStartupPref) {
    showOnStartupPref.remove(PREFERENCE_KEY);
  }

  /**
   * Calls
   * {@link TipOfTheDayUI#createDialog(Component, JXTipOfTheDay.ShowOnStartupChoice)}
   * 
   * @param parentComponent
   * @param choice
   * @return a JDialog to show this TipOfTheDay pane
   */
  protected JDialog createDialog(Component parentComponent,
    ShowOnStartupChoice choice) {
    return getUI().createDialog(parentComponent, choice);
  }

  /**
   * Used in conjunction with the
   * {@link JXTipOfTheDay#showDialog(Component, ShowOnStartupChoice)} to save the
   * "Show tips on startup" choice.
   */
  public static interface ShowOnStartupChoice {
    void setShowingOnStartup(boolean showOnStartup);
    boolean isShowingOnStartup();
  }

}

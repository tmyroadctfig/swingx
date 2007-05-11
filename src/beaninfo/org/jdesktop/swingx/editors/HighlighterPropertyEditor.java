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
package org.jdesktop.swingx.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
//import org.jdesktop.jdnc.incubator.rlopes.colorcombo.ColorComboBox;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.LegacyHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
//import org.jdesktop.swingx.expression.ExpressionHighlighter;
//import org.netbeans.modules.form.NamedPropertyEditor;
//import org.openide.windows.WindowManager;


/**
 *
 * @author rbair
 */
public class HighlighterPropertyEditor  extends PropertyEditorSupport { //implements NamedPropertyEditor {
    private CompoundHighlighter pipeline = new CompoundHighlighter();
    private Editor editor;

    /** Creates a new instance of HighlighterPropertyEditor */
    public HighlighterPropertyEditor() {
        editor = new Editor();
        editor.highlightersList.setModel(new AbstractListModel() {
            public Object getElementAt(int index) {
                return pipeline.getHighlighters()[index];
            }

            public int getSize() {
                return pipeline.getHighlighters().length;
            }
        });
        editor.highlightersList.getModel().addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                HighlighterPropertyEditor.this.firePropertyChange();
            }

            public void intervalAdded(ListDataEvent e) {
                HighlighterPropertyEditor.this.firePropertyChange();
            }

            public void intervalRemoved(ListDataEvent e) {
                HighlighterPropertyEditor.this.firePropertyChange();
            }
        });
    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public String getJavaInitializationString() {
        StringBuffer buffer = new StringBuffer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(300);
        XMLEncoder e = new XMLEncoder(baos);
        e.writeObject(pipeline.getHighlighters());
        e.close();
        Highlighter[] blar = (Highlighter[])new XMLDecoder(new ByteArrayInputStream(baos.toString().getBytes())).readObject();
        buffer.append("new org.jdesktop.swingx.decorator.CompoundHighlighter(\n");
        buffer.append("\t(org.jdesktop.swingx.decorator.LegacyHighlighter[])new java.beans.XMLDecoder(new java.io.ByteArrayInputStream(\"");
        buffer.append(escapeString(baos.toString()));
        buffer.append("\".getBytes())).readObject())");
        return buffer.toString();
    }
    
    private String escapeString(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\n", "");
    }

    public String getDisplayName() {
        return "LegacyHighlighter Editor";
    }

    public void setAsText(String text) throws IllegalArgumentException {
    }

    public void setValue(Object value) {
        //if the value is a CompoundHighlighter, hold on to this. The
        //editor is going to need it
        if (value instanceof CompoundHighlighter) {
            pipeline = new CompoundHighlighter(((CompoundHighlighter)value).getHighlighters());
        } else {
            pipeline = new CompoundHighlighter();
        }
        super.setValue(value);
    }

    public Object getValue() {
        return pipeline;
    }

    public String getAsText() {
        return pipeline.toString();
    }

    public java.awt.Component getCustomEditor() {
        return editor;
    }

    private class Editor extends JXPanel {
        private JXList highlightersList;
        private AlternateRowDetailPanel alternateRowDetailPanel = new AlternateRowDetailPanel();
        private ExpressionDetailPanel expressionDetailPanel = new ExpressionDetailPanel();
        private PatternDetailPanel patternDetailPanel = new PatternDetailPanel();
        
        public Editor() {
            setLayout(new BorderLayout());

            JSplitPane mainsp = new JSplitPane();
            mainsp.setBorder(BorderFactory.createEmptyBorder());
            JXTitledPanel tp = new JXTitledPanel("Highlighters");
            JXPanel buttonPanel = new JXPanel();
            buttonPanel.setOpaque(false);
            JButton btn = new JButton(new AddAction());
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            buttonPanel.add(btn);
            btn = new JButton(new DeleteAction());
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            buttonPanel.add(btn);
            tp.addRightDecoration(buttonPanel);
            highlightersList = new JXList();
            JScrollPane sp = new JScrollPane(highlightersList);
            sp.setBorder(BorderFactory.createEmptyBorder());
            tp.setContentContainer(sp);
            mainsp.setLeftComponent(tp);
            final JXTitledPanel details = new JXTitledPanel("LegacyHighlighter Details");
            mainsp.setRightComponent(details);
            add(mainsp);
            
            highlightersList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        //get the first selected value...
                        Highlighter h = (Highlighter)highlightersList.getSelectedValue();
                        //load the proper detail panel
                        Container c = details.getContentContainer();
                        HighlighterDetailPanel content = null;
                        if (c instanceof HighlighterDetailPanel) {
                            content = (HighlighterDetailPanel)c;
                            content.save();
                        }
                        if (h instanceof AlternateRowHighlighter) {
                            alternateRowDetailPanel.init((AlternateRowHighlighter)h);
                            content = alternateRowDetailPanel;
//                        } else if (h instanceof ExpressionHighlighter) {
//                            expressionDetailPanel.init((ExpressionHighlighter)h);
//                            content = expressionDetailPanel;
                        } else if (h instanceof PatternHighlighter) {
                            patternDetailPanel.init((PatternHighlighter)h);
                            content = patternDetailPanel;
                        }
                        details.setContentContainer(content == null ? new JXPanel() : content);
                        details.revalidate();
                        details.repaint();
                    }
                }
            });
        }
        
        private class DeleteAction extends AbstractAction {
            public DeleteAction() {
                super();
                super.putValue(AddAction.SMALL_ICON, new ImageIcon(HighlighterPropertyEditor.class.getResource("deleteHighlighter.gif")));
            }
            public void actionPerformed(ActionEvent ae) {
                //get the selected items
                Object[] values = highlightersList.getSelectedValues();
                for (int i=0; i<values.length; i++) {
                    pipeline.removeHighlighter((LegacyHighlighter)values[i]);
                }
                ListDataListener[] listeners = ((AbstractListModel)highlightersList.getModel()).getListDataListeners();
                ListDataEvent evt = new ListDataEvent(highlightersList, ListDataEvent.CONTENTS_CHANGED, 0, pipeline.getHighlighters().length-1);
                for (int i=0; i<listeners.length; i++) {
                    listeners[i].contentsChanged(evt);
                }
            }
        }
        
        private class AddAction extends AbstractAction {
            public AddAction() {
                super();
                super.putValue(AddAction.SMALL_ICON, new ImageIcon(HighlighterPropertyEditor.class.getResource("newHighlighter.gif")));
            }
            public void actionPerformed(ActionEvent ae) {
                //show a true popup
                final JDialog dlg = new JDialog(/*WindowManager.getDefault().getMainWindow()*/(JDialog)null, true);
                JXPanel cp = new JXPanel(new GridBagLayout());
                final JRadioButton newRB = new JRadioButton("New LegacyHighlighter");
                final JRadioButton formRB = new JRadioButton("From Form");
                ButtonGroup bg = new ButtonGroup();
                bg.add(newRB);
                bg.add(formRB);
                newRB.setSelected(true);
                cp.add(newRB, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 5, 11), 0, 0));
                JLabel label = new JLabel("Type: ");
                final JComboBox typeCB = new JComboBox(new String[]{"Alternate Row", "Expression", "Pattern"});
                label.setLabelFor(typeCB);
                cp.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 7, 3), 0, 0));
                cp.add(typeCB, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 7, 11), 0, 0));
                label = new JLabel("Name: ");
                final JTextField nameTF = new JTextField("<auto-generate>");
                label.setLabelFor(nameTF);
                cp.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 7, 3), 0, 0));
                cp.add(nameTF, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 7, 11), 0, 0));
                cp.add(formRB, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 12, 5, 11), 0, 0));
                label = new JLabel("Choose LegacyHighlighter: ");
                final JComboBox highlighterCB = new JComboBox(new Object[0]);
                cp.add(label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 24, 7, 3), 0, 0));
                cp.add(highlighterCB, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 17, 11), 0, 0));
                JButton okButton = new JButton(new AbstractAction("OK") {
                    public void actionPerformed(ActionEvent ae) {
                        if (newRB.isSelected()) {
                            String type = (String)typeCB.getSelectedItem();
                            LegacyHighlighter h = null;
                            if (type == null || type == "Alternate Row") {
                                h = new AlternateRowHighlighter();
//                            } else if (type == "Expression") {
//                                h = new ExpressionHighlighter();
                            } else if (type == "Pattern") {
                                h = new PatternHighlighter();
                            }
                            //TODO deal with the name
                            pipeline.addHighlighter(h);
                            ListDataListener[] listeners = ((AbstractListModel)highlightersList.getModel()).getListDataListeners();
                            int size = pipeline.getHighlighters().length;
                            ListDataEvent evt = new ListDataEvent(highlightersList, ListDataEvent.CONTENTS_CHANGED, size -1, size -1);
                            for (int i=0; i<listeners.length; i++) {
                                listeners[i].contentsChanged(evt);
                            }
                        }
                        dlg.setVisible(false);
                    }
                });
                JXPanel buttonPanel = new JXPanel(new GridBagLayout());
                buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                cp.add(buttonPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 12, 11, 11), 0, 0));
                dlg.setContentPane(cp);
                dlg.pack();
                //TODO need to to center...
                dlg.setVisible(true);
            }
        }
        
        private JComboBox createColorComboBox() {
//            try {
//                return new ColorComboBox();
//            } catch (Exception e) {
                return new JComboBox(new Color[]{Color.BLACK, Color.BLUE, 
                        Color.CYAN, Color.DARK_GRAY, Color.GRAY,
                        Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA,
                        Color.ORANGE, Color.PINK, Color.RED, Color.WHITE,
                        Color.YELLOW});
//            }
        }

        private class HighlighterDetailPanel extends JXPanel {
            private int rowCounter = 0;
            protected JComboBox backgroundCB;
            protected JComboBox selectedBackgroundCB;
            protected JComboBox foregroundCB;
            protected JComboBox selectedForegroundCB;
            private LegacyHighlighter h;
            
            public HighlighterDetailPanel() {
                setLayout(new GridBagLayout());
                backgroundCB = createColorComboBox();
                add("Background: ", backgroundCB, false);
                selectedBackgroundCB = createColorComboBox();
                add("Selected Background: ", selectedBackgroundCB, false);
                foregroundCB = createColorComboBox();
                add("Foreground: ", foregroundCB, false);
                selectedForegroundCB = createColorComboBox();
                add("Selected Foreground: ", selectedForegroundCB, false);
            }
            
            protected void add(String labelText, JComponent component, boolean isLast) {
                JLabel label = new JLabel(labelText);
                label.setLabelFor(component);
                Insets insets = rowCounter == 0 ? new Insets(12, 12, 5, 5) : new Insets(0, 12, 5, 5);
                add(label, new GridBagConstraints(0, rowCounter, 1, 1, 0.0, 0.0, isLast ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
                insets = rowCounter == 0 ? new Insets(12, 0, 5, 11) : new Insets(0, 0, isLast ? 11 : 5, 11);
                add(component, new GridBagConstraints(1, rowCounter, 1, 1, 1.0, isLast ? 1.0 : 0.0, isLast ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
                rowCounter++;
            }
            
            protected void init(LegacyHighlighter h) {
                this.h = h;
                backgroundCB.setSelectedItem(h.getBackground());
                selectedBackgroundCB.setSelectedItem(h.getSelectedBackground());
                foregroundCB.setSelectedItem(h.getForeground());
                selectedForegroundCB.setSelectedItem(h.getSelectedForeground());
            }
            
            public void save() {
                this.h.setBackground((Color)backgroundCB.getSelectedItem());
                this.h.setSelectedBackground((Color)selectedBackgroundCB.getSelectedItem());
                this.h.setForeground((Color)foregroundCB.getSelectedItem());
                this.h.setSelectedForeground((Color)selectedForegroundCB.getSelectedItem());
                ListDataListener[] listeners = ((AbstractListModel)highlightersList.getModel()).getListDataListeners();
                int size = pipeline.getHighlighters().length;
                ListDataEvent evt = new ListDataEvent(highlightersList, ListDataEvent.CONTENTS_CHANGED, size -1, size-1);
                for (int i=0; i<listeners.length; i++) {
                    listeners[i].contentsChanged(evt);
                }
            }
        }
        
        private class AlternateRowDetailPanel extends HighlighterDetailPanel {
            private JComboBox evenRowCB;
            private JComboBox oddRowCB;
            private AlternateRowHighlighter h;
            
            public AlternateRowDetailPanel() {
                evenRowCB = createColorComboBox();
                add("Even Row Background: ", evenRowCB, false);
                oddRowCB = createColorComboBox();
                add("Odd Row Background: ", oddRowCB, true);
            }
            
            public void init(AlternateRowHighlighter h) {
                super.init(h);
                this.h = h;
                evenRowCB.setSelectedItem(h.getEvenRowBackground());
                oddRowCB.setSelectedItem(h.getOddRowBackground());
            }
            
            public void save() {
                this.h.setEvenRowBackground((Color)evenRowCB.getSelectedItem());
                this.h.setOddRowBackground((Color)oddRowCB.getSelectedItem());
                super.save();
            }
        }
        
        private class ExpressionDetailPanel extends HighlighterDetailPanel {
            private JTextField bgExpressionTF;
            private JTextField fgExpressionTF;
//            private ExpressionHighlighter h;
            
            public ExpressionDetailPanel() {
                bgExpressionTF = new JTextField();
                add("Background Expression: ", bgExpressionTF, false);
                fgExpressionTF = new JTextField();
                add("Foreground Expression: ", fgExpressionTF, true);
            }
            
            public void init(/*Expression*/LegacyHighlighter h) {
                super.init(h);
//                this.h = h;
//                bgExpressionTF.setText(h.getBackgroundExpression());
//                fgExpressionTF.setText(h.getForegroundExpression());
            }
            
            public void save() {
//                this.h.setBackgroundExpression(bgExpressionTF.getText());
//                this.h.setForegroundExpression(fgExpressionTF.getText());
                super.save();
            }
        }
        
        private class PatternDetailPanel extends HighlighterDetailPanel {
            private JTextField patternTF;
//            private JCheckBox matchFlagsCB;
            private PatternHighlighter h;
            
            public PatternDetailPanel() {
                patternTF = new JTextField();
                add("Pattern: ", patternTF, true);
//                matchFlagsCB = new JCheckBox("Match Flags?");
//                add("", matchFlagsCB, true);
            }
            
            public void init(PatternHighlighter h) {
                super.init(h);
                this.h = h;
                patternTF.setText(h.getPattern().pattern());
//                matchFlagsCB.setSelected(...);
            }
            
            public void save() {
                this.h.setPattern(patternTF.getText(), 0); //TODO
                super.save();
            }
        }
        
    }
}
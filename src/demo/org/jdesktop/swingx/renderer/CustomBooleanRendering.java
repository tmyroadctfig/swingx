/*
 * Created on 19.04.2007
 *
 */
package org.jdesktop.swingx.renderer;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.EqualsHighlightPredicate;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * Replace default boolean rendering with custom icons.
 *  
 */
public class CustomBooleanRendering {

    /**
     * Replace the default boolean rendering with a custom
     * ComponentProvider.
     * 
     * @see org.javadesktop.swingx.renderer.ComponentProvider
     */
    private void configureRendering(JXTable table) {
        LabelProvider provider = new LabelProvider(JLabel.CENTER) {
            Icon yesIcon = getIcon("resources/green-orb.png");
            Icon noIcon = getIcon("resources/exit.png");
            @Override
            protected void format(CellContext context) {
                if (Boolean.TRUE.equals(context.getValue())) {
                    rendererComponent.setIcon(yesIcon);
                } else if (Boolean.FALSE.equals(context.getValue())) {
                    rendererComponent.setIcon(noIcon);
                } else {
                    rendererComponent.setIcon(null);
                }
                rendererComponent.setText(null);
            }
            
        }; 
        table.setDefaultRenderer(Boolean.class, new DefaultTableRenderer(provider));
    }
    /**
     * Replace the default boolean rendering with a Highlighter.
     * Note: this is just for fun! Typically, the "what" to render
     * is the task of a ComponentProvider.
     * 
     * @see org.javadesktop.swingx.decorator.Highlighter
     * @see org.javadesktop.swingx.decorator.HighlightPredicate
     */
    private void configureFunRendering(JXTable table) {
        HighlightPredicate truePredicate = new EqualsHighlightPredicate(Boolean.TRUE);
        HighlightPredicate falsePredicate = new EqualsHighlightPredicate(Boolean.FALSE);
        Highlighter yesHighlighter = new PainterHighlighter(getPainter("resources/green-orb.png"), truePredicate); 
        Highlighter noHighlighter = new PainterHighlighter(getPainter("resources/exit.png"), falsePredicate);
        table.setHighlighters(yesHighlighter, noHighlighter);
        
        table.setDefaultRenderer(Boolean.class, 
                new DefaultTableRenderer(StringValue.EMPTY));
    }


    /**
     * @return
     */
    private Component createContent() {
        JXTable table = new JXTable(new DemoTableModel());
        configureTable(table);
        configureRendering(table);
        JXTable fun = new JXTable(new DemoTableModel());
        configureTable(fun);
        configureFunRendering(fun);
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Custom Provider", new JScrollPane(table));
        pane.addTab("Boolean Highlighter", new JScrollPane(fun));
        pane.setToolTipTextAt(1, "Just for fun!");
        return pane;
    }




    /**
     * @param table
     */
    private void configureTable(JXTable table) {
        table.setColumnControlVisible(true);
        table.setVisibleRowCount(table.getRowCount());
        table.packColumn(2, -1);
    }

    private Painter getPainter(String resource) {
        Painter yesPainter = null;
        try {
            yesPainter = new ImagePainter(ImageIO
                    .read(CustomBooleanRendering.class
                            .getResource(resource)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yesPainter;
    }

    private Icon getIcon(String resource) {
        Icon icon = null;
        URL url = getClass().getResource(resource);
        icon = new ImageIcon(url);
        return icon;
    }
    /**
     * Adhoc model.
     */
    public static class DemoTableModel extends AbstractTableModel {
        Class[] columnClasses = { String.class, String.class, String.class,
                Integer.class, Boolean.class
        };
        
        String[] columnNames = { "First Name", "Last Name", "Sport",
                "# of Years", "Vegetarian" };

        Object[][] data = {
                { "Mary", "Campione", "Snowboarding", new Integer(5),
                        new Boolean(false) },
                { "Alison", "Huml", "Rowing", new Integer(3), new Boolean(true) },
                { "Kathy", "Walrath", "Knitting", new Integer(2),
                        new Boolean(false) },
                { "Sharon", "Zakhour", "Speed reading", new Integer(20),
                        new Boolean(true) },
                { "Philip", "Milne", "Pool", new Integer(10),
                        new Boolean(false) } };

        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        public Class<?> getColumnClass(int column) {
            return columnClasses[column];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public Object getValueAt(int row, int column) {
            return data[row][column];
        }

    }

    public static void main(String[] args) {
        final JXFrame frame = new JXFrame("SwingX :: Custom Boolean Rendering", true);
        frame.add(new CustomBooleanRendering().createContent());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        });        
    }
    


} 
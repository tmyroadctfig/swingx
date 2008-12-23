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
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.EqualsHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.OrHighlightPredicate;
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
     * content-icon mapping.
     * 
     * @see org.javadesktop.swingx.renderer.IconValue
     * @see org.javadesktop.swingx.renderer.StringValue
     */
    private void configureRendering(JXTable table) {
        final Icon yesIcon = getIcon("resources/green-orb.png");
        final Icon noIcon = getIcon("resources/exit.png");
        final Icon undecided = getIcon("resources/silver-star.gif");
        IconValue iv = new IconValue() {

            public Icon getIcon(Object value) {
                if (Boolean.TRUE.equals(value)) {
                    return yesIcon;
                } else if (Boolean.FALSE.equals(value)) {
                    return noIcon;
                } 
                return undecided;
            }
            
        };
        table.setDefaultRenderer(Boolean.class, 
                new DefaultTableRenderer(new MappedValue(StringValues.EMPTY, iv), JLabel.CENTER));
    }
    
    /**
     * Replace the default boolean rendering with a Highlighter.
     * Note: this is just for fun! Typically, the "what" to render
     * is the task of a ComponentProvider.
     * 
     * @see org.javadesktop.swingx.decorator.Highlighter
     * @see org.javadesktop.swingx.decorator.HighlightPredicate
     */
    private void configureFunRendering(JXTable table, int... booleanColumns) {
        HighlightPredicate truePredicate = new EqualsHighlightPredicate(Boolean.TRUE);
        HighlightPredicate falsePredicate = new EqualsHighlightPredicate(Boolean.FALSE);
        HighlightPredicate undecidedPredicate = new AndHighlightPredicate(
            new ColumnHighlightPredicate(booleanColumns),
            new NotHighlightPredicate(
                new OrHighlightPredicate(truePredicate, falsePredicate)));
        Highlighter yesHighlighter = new PainterHighlighter(truePredicate, getPainter("resources/green-orb.png")); 
        Highlighter noHighlighter = new PainterHighlighter(falsePredicate, getPainter("resources/exit.png"));
        Highlighter undecidedHighlighter = new PainterHighlighter(undecidedPredicate, getPainter("resources/silver-star.gif"));
        table.setHighlighters(yesHighlighter, noHighlighter, undecidedHighlighter);
        
        table.setDefaultRenderer(Boolean.class, 
                new DefaultTableRenderer(StringValues.EMPTY));
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
        configureFunRendering(fun, fun.getColumnCount() - 1);
        JTabbedPane pane = new JTabbedPane();
        pane.addTab("Custom Content Mapping", new JScrollPane(table));
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
                        null } };

        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
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
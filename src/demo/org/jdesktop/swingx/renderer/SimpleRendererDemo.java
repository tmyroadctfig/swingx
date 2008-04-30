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
package org.jdesktop.swingx.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.SearchFactory;
import org.jdesktop.swingx.JXList.DelegatingRenderer;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.TypeHighlightPredicate;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.util.WindowUtils;

/**
 * A simple example about how to configure SwingX renderers.
 * 
 * @author Jeanette Winzenburg
 */
public class SimpleRendererDemo {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SimpleRendererDemo.class
            .getName());
    private String dataSource = "resources/contributors.txt";
    private List<Contributor> contributors;
    private ListModel listModel;
    private TableModel tableModel;
    private DefaultMutableTreeNode rootNode;

    public SimpleRendererDemo() {
        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchFactory.getInstance().setUseFindBar(true);
    }

    /**
     * Configure the given collection components with the same
     * rendering representation.
     * 
     * Note: this method is extracted for emphasis only :-)
     */
    private void configureRendering(JXTable table, JXList list, JXTree tree) {
        StringValue stringValue = new StringValue() {
            
            public String getString(Object value) {
                if (!(value instanceof Contributor)) return TO_STRING.getString(value);
                Contributor contributor = (Contributor) value;
                return contributor.lastName + ", " + contributor.firstName;
            }
            
        };
        table.setDefaultRenderer(Contributor.class, new DefaultTableRenderer(stringValue));
        list.setCellRenderer(new DefaultListRenderer(stringValue));
        tree.setCellRenderer(new DefaultTreeRenderer(stringValue));
        
    }

    private void configureHighlighting(JXTable table, JXList list, JXTree tree) {
        CompoundHighlighter stars = new CompoundHighlighter(
                new TypeHighlightPredicate(Contributor.class));
        stars.addHighlighter(new BorderHighlighter(BorderFactory.createEmptyBorder(0, 20, 0, 0)));
        stars.addHighlighter(getRangeHighlighter("silver-star.gif", 50, 80));
        stars.addHighlighter(getRangeHighlighter("gold-star.gif", 80, 100));
        list.addHighlighter(stars);
        
        // choose a random first name to highlight
        int rndIndex = new Double(Math.random() * contributors.size()).intValue();
        String match = contributors.get(rndIndex).firstName;
        System.out.println(match);
        HighlightPredicate predicate = new PatternPredicate(Pattern.compile(match), 0);
        Highlighter foreground = new ColorHighlighter(predicate, 
                HighlighterFactory.NOTEPAD, Color.MAGENTA);
        table.addHighlighter(foreground);
        list.addHighlighter(foreground);
        tree.addHighlighter(foreground);
    }


    private PainterHighlighter getRangeHighlighter(String gifName, int start,
            int end) {
        HighlightPredicate meritPredicate = getRangePredicate(start, end);
        ImagePainter bronze = getImagePainter(gifName);
        PainterHighlighter painterHighlighter = new PainterHighlighter(meritPredicate, bronze);
        return painterHighlighter;
    }

    private HighlightPredicate getRangePredicate(final int start, final int end) {
        HighlightPredicate meritPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int merit = ((Contributor) adapter.getValue()).merits;
                return (merit >= start) && (merit < end);
            }
            
        };
        return meritPredicate;
    }


    /**
     * Configures the components after the meta-data are set. In this simple example,
     * it's equivalent to having set the models.
     * 
     * @param table
     */
    private void configureComponents(JXTable table, JXList list, JXTree tree) {
        table.setColumnControlVisible(true);
        table.getColumnExt(1).setToolTipText("Randomly generated - run again if you are disatisfied");
        table.packColumn(1, 10);
        table.getColumnExt(1).setMaxWidth(table.getColumnExt(1).getPreferredWidth());
    }
    

    /**
     * @return the component to show.
     */
    private Component createContent() {
        // create
        JXTable table = new JXTable();
        JXList list = new JXList();
        JXTree tree = new JXTree();
        // add
        table.setModel(tableModel);
        list.setModel(listModel);
        tree.setModel(new DefaultTreeModel(rootNode));
        // configure
        configureRendering(table, list, tree);
        configureHighlighting(table, list, tree);
        configureComponents(table, list, tree);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("JXTable", new JScrollPane(table));
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(new JScrollPane(list));
        splitPane.setRightComponent(new JScrollPane(tree));
//        splitPane.setDividerLocation(250);
        tabbedPane.addTab("JXList/JXTree", splitPane);
        // testing black/white list with bullets (from java1_2007)
        JXList bulletedList = new JXList(listModel);
        configureList(bulletedList, ((DelegatingRenderer) list.getCellRenderer()).getDelegateRenderer());
        tabbedPane.add("Black List", new JScrollPane(bulletedList));
        return tabbedPane;
    }

    
    /**
     * @param listCellRenderer 
     * @param bulletedList
     */
    private void configureList(JXList list, ListCellRenderer renderer) {
        list.setBackground(Color.BLACK);
        list.setLayoutOrientation(JXList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(0);
        list.setCellRenderer(renderer);
        list.addHighlighter(new ColorHighlighter(null, Color.WHITE));
        Highlighter hl = new AbstractHighlighter() {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
               ((JLabel) component).setText("\u2022 " + ((JLabel) component).getText());
                return component;
            }

            @Override
            protected boolean canHighlight(Component component,
                    ComponentAdapter adapter) {
                return component instanceof JLabel;
            }
            
            
        };
        list.addHighlighter(hl);
    }

    /**
     * @param string
     * @return
     */
    private ImagePainter getImagePainter(String string) {
        ImagePainter imagePainter = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass()
                    .getResource("resources/" + string));
            BufferedImage mod = 
              GraphicsUtilities.createCompatibleTranslucentImage(
                      image.getWidth(), 
                      image.getHeight());
            Graphics2D g = mod.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            g.dispose();
            imagePainter = new ImagePainter(mod);
            imagePainter.setHorizontalAlignment(HorizontalAlignment.LEFT);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imagePainter;
    }


    /**
     * Create and fill a list of contributors from a resource and 
     * wrap view models around.
     * @throws IOException 
     * 
     */
    private void initData() throws IOException {
        contributors = new ArrayList<Contributor>();
        // fill the list from the resources
        readDataSource(contributors);
        // wrap a listModel around
        listModel = new AbstractListModel() {

            public Object getElementAt(int index) {
                if (index == 0) {
                    return "-- Contributors --";
                }
                return contributors.get(index - 1);
            }

            public int getSize() {
                return contributors.size() + 1;
            }
            
        };
        // wrap a TableModel around
        tableModel = new AbstractTableModel() {

            public int getColumnCount() {
                return 2;
            }

            public int getRowCount() {
                return contributors.size();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return contributors.get(rowIndex);
                case 1:
                    return contributors.get(rowIndex).merits;
                }
                return null;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return Contributor.class;
                case 1:
                    return Number.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public String getColumnName(int column) {
                switch (column) {
                case 0:
                    return "Contributor";
                case 1:
                    return "Merits";
                }
                return super.getColumnName(column);
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }
            
            
        };
        // fill DefaultTreeNodes with the elements 
        rootNode = new DefaultMutableTreeNode("Contributors");
        for (int i = 0; i < contributors.size(); i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(contributors.get(i));
            rootNode.add(node);
        }
        
    }
    
    private void readDataSource(List<Contributor> list) throws IOException {
        InputStream is = getClass().getResourceAsStream(dataSource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                list.add(new Contributor(line));
            }
        } finally {
            // do our best to close
            reader.close();
        }
    }
    
    public static class Contributor implements Comparable {
        private String firstName;
        private String lastName;
        @SuppressWarnings("unused")
        private String userID;
        private int merits;
        
        public Contributor(String rawData) {
            setData(rawData);
            merits = new Double(Math.random() * 100).intValue();
        }

        /**
         * @param rawData
         */
        private void setData(String rawData) {
            if (rawData == null) {
                lastName = " <unknown> ";
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(rawData);
            try {
               firstName = tokenizer.nextToken();
               lastName = tokenizer.nextToken();
               userID = tokenizer.nextToken();
            } catch (Exception ex) {
                // don't care ...
            }
            
        }

        public int compareTo(Object o) {
            if (!(o instanceof Contributor)) return -1;
            return lastName.compareTo(((Contributor) o).lastName);
        }
    }

    //---------------------------Main

    public static void main(String[] args) {
//        initLF();
        final JXFrame frame = new JXFrame("SwingX :: Simple Renderer Demo", true);
        frame.add(new SimpleRendererDemo().createContent());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setSize(600, 400);
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        });        
    }

    /**
     * 
     */
    private static void initLF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}

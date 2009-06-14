/*
 * PainterDemoSet.java
 *
 * Created on October 18, 2006, 8:20 PM
 */

package org.jdesktop.swingx.painter.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painter.effects.GlowPathEffect;
import org.jdesktop.swingx.painter.effects.InnerGlowPathEffect;
import org.jdesktop.swingx.painter.effects.InnerShadowPathEffect;
import org.jdesktop.swingx.painter.effects.NeonBorderEffect;
import org.jdesktop.swingx.painter.effects.ShadowPathEffect;
import org.jdesktop.swingx.util.ShapeUtils;

import com.jhlabs.image.ShadowFilter;

/**
 *
 * @author  joshy
 */
public class PainterDemoSet extends javax.swing.JFrame {
    private static final MultipleGradientPaint gradient = new LinearGradientPaint(
            new Point2D.Double(0,0), new Point2D.Double(100,0),
            new float[] { 0f, 0.5f, 1f },
            new Color[] { Color.BLACK, Color.BLUE, Color.LIGHT_GRAY});
    
    /** Creates new form PainterDemoSet */
    public PainterDemoSet() {
        initComponents();
        painterList.setModel(new DefaultListModel());
        painterPanel.setLayout(new BorderLayout());
        CompoundPainter comp;
        
        //Color[] colors = { Color.BLACK, Color.BLUE, Color.WHITE};
        //float[] floats = { 0f, 0.5f, 1f};
        
        
        // a rectangle filled with a gradient
        //RectanglePainter stdrect = new RectanglePainter(0,0,0,0);
        //stdrect.setFillPaint(gradient);
        //stdrect.setBorderPaint(Color.BLACK);
        //stdrect.setBorderWidth(4f);
        //comp = new CompoundPainter(stdrect, new TextPainter("This is some stuff"));
        //addDemo(new JXButton("Cool Text Yo"), comp, "button with gradient and text");
        
        imageDemos();
        ShapePainter star;
        shapeDemos();
        
        
        textDemos(gradient);
        
        MattePainter gray = new MattePainter(Color.GRAY);
        rectangleDemos();
        transformDemos();
        addGlossDemos();
        addPinstripeDemos();
        addGradientDemos();
        addPainterSetAPIDemos();
        listDemos(gradient);
        tableDemos();
        miscDemos(gradient);
        genericsDemos();
        try {
            loadCitations();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    Map citeMap = new HashMap();
    private void loadCitations() throws Exception {
        p("doing citations");
        URL url = this.getClass().getResource("PainterDemoSet.java");
        Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
        //File file = new File("src/beaninfo/org/jdesktop/swingx/painter/demo/PainterDemoSet.java");
        //FileReader reader = new FileReader(file);
        //Scanner scanner = new Scanner(reader);
        scanner.useDelimiter(".*\\$startcite.*");
        while(scanner.hasNext()) {
            String cite = scanner.next();
            //p("cite = " + cite);
            if(cite.contains("$name-")) {
                //p("contains");
                String[] ret = regexSearch(cite,"\\$name-(.*?)-(.*)\\$endcite");
                //p("got: " + ret[1]);
                citeMap.put(ret[1], ret[2]);
                /*
                Pattern pat = Pattern.compile("\\$name-(.*)-(.*)\\$endcite",Pattern.DOTALL);
                Matcher matcher = pat.matcher(cite);
                matcher.find();
                for(int i=0; i<=matcher.groupCount(); i++) {
                    //p("got: " + matcher.group(i));
                }
                citeMap.put(matcher.group(1),matcher.group(2));
                 */
                //p("added citation: " + matcher.group(1) +  " = " + matcher.group(2));
            }
        }
    }
    
    private String[] regexSearch(String source, String pattern) {
        Pattern pat = Pattern.compile(pattern,Pattern.DOTALL);
        Matcher matcher = pat.matcher(source);
        matcher.find();
        String[] list = new String[matcher.groupCount()+1];
        for(int i=0; i<=matcher.groupCount(); i++) {
            list[i] = matcher.group(i);
        }
        return list;
    }
    
    
    
    private void miscDemos(final MultipleGradientPaint gradient) {
        addDemo(new JPanel(), "---- Misc Demos");
        Font font2 = new Font("SansSerif", Font.BOLD, 80);
        ShapePainter star;
        
        
        
        // a generated triangle with a gradient
        //$startcite
        //$name-misc01-
        ShapePainter triangle = new ShapePainter(ShapeUtils.generatePolygon(3,50,30));
        triangle.setFillPaint(gradient);
        addDemo(new JXPanel(),triangle,"Triangle w/ gradient","misc01");
        //$endcite
        
        
        //$startcite
        //$name-misc02-
        // a text painter using the neon border path effect
        TextPainter coollogo = new TextPainter("Neon");
        coollogo.setFont(new Font("SansSerif",Font.BOLD,100));
        coollogo.setFillPaint(Color.BLACK);
        NeonBorderEffect neon1 = new NeonBorderEffect(Color.BLACK, Color.RED.brighter(), 10);
        neon1.setBorderPosition(NeonBorderEffect.BorderPosition.Centered);
        coollogo.setAreaEffects(neon1);
        addDemo("A Cool Logo","misc02",new MattePainter(Color.BLACK),coollogo);
        //$endcite
        
        
        
        //$startcite
        //$name-misc03-
        // create a coming soon badge
        star = new ShapePainter(ShapeUtils.generatePolygon(30,50,45,true),Color.RED);
        star.setStyle(ShapePainter.Style.FILLED);
        star.setBorderPaint(Color.BLUE);
        ShadowPathEffect starShadow = new ShadowPathEffect();
        starShadow.setOffset(new Point(1,1));
        starShadow.setEffectWidth(5);
        star.setAreaEffects(starShadow);
        addDemo(new JXPanel(), new CompoundPainter(
                new MattePainter(Color.GRAY),
                star,
                new TextPainter("Coming Soon!",
                new Font("SansSerif", Font.PLAIN, 12), Color.WHITE)
                ),
                "Coming Soon Badge","misc03");
        //$endcite
        
        //$startcite
        //$name-misc-flex
        // create a pinstripe painter similar to the flex progress bar
        RectanglePainter rp = new RectanglePainter();
        //$endcite
    }
    
    private void tableDemos() {
        addDemo(new JPanel(), "---- Table Renderer Demos");
        //$startcite
        //$name-table01-
        // itunes like table demo
        JTable musicTable = createJTableWithData(false);
        //musicTable.setIntercellSpacing(new Dimension(0,0));
        musicTable.setGridColor(Color.GRAY.darker());
        // broken for now
        /*
        PainterTableCellRenderer tableRenderer = new PainterTableCellRenderer();
        MultipleGradientPaint shading = new LinearGradientPaint(
                new Point(0,0), new Point(0,10),
                new float[] {0f, 1f} ,
                new Color[] {Color.GRAY, Color.GRAY.darker()});
        tableRenderer.setBackgroundPainter(new MattePainter(shading));
        shading = new LinearGradientPaint(
                new Point(0,0), new Point(0,10),
                new float[] {0f, 1f} ,
                new Color[] {Color.BLUE, Color.BLUE.darker()});
        
        TextPainter selectionText = new TextPainter();
        selectionText.setFillPaint(Color.BLACK);
        selectionText.setHorizontalAlignment(TextPainter.HorizontalAlignment.LEFT);
        tableRenderer.setSelectionBackgroundPainter(new MattePainter(shading));
        
        musicTable.setDefaultRenderer(Object.class, tableRenderer);
        
        PainterTableCellRenderer headerRenderer = new PainterTableCellRenderer();
        CompoundPainter headerPainter = new CompoundPainter(
                new MattePainter(new Color(200,200,200)),
                new PinstripePainter(new Color(220,220,220),45,5,8),
                new RectanglePainter(new Color(200,200,200,0), Color.BLACK));
        headerRenderer.setBackgroundPainter(headerPainter);
        
        
        for(int i=0; i<musicTable.getColumnCount(); i++) {
            TableColumn col = musicTable.getColumn(musicTable.getColumnName(i));
            col.setHeaderRenderer(headerRenderer);
        }
        //musicTable.setEnabled(false);
        
        musicTable.setRowSelectionAllowed(true);
        musicTable.setColumnSelectionAllowed(false);
        addDemo(new JScrollPane(musicTable), "JTable with custom renderer","table01");
        //$endcite*/
    }
    
    private void transformDemos() {
        Font font2 = new Font("SansSerif", Font.BOLD, 80);
        addDemo(new JPanel(), "---- Affine Transform Demos");
        CompoundPainter comp;
        RectanglePainter rectnorm;
        
        
        // affine transforms
        //$startcite
        //$name-trans01-
        TextPainter normText = new TextPainter("Text", font2);
        comp = new CompoundPainter(normText);
        addDemo(new JXPanel(), comp, "Normal Text","trans01");
        //$endcite
        
        
        //$startcite
        //$name-trans02-
        TextPainter rotText = new TextPainter("Text", font2);
        comp = new CompoundPainter(rotText);
        comp.setTransform(AffineTransform.getRotateInstance(-Math.PI*2/8,100,100));
        addDemo(new JXPanel(), comp, "Rotated Text","trans02");
        //$endcite
        
        
        //$startcite
        //$name-asdf-
        TextPainter shearText = new TextPainter("Text", font2);
        comp = new CompoundPainter(shearText);
        comp.setTransform(AffineTransform.getShearInstance(-0.2,0));
        addDemo(new JXPanel(), comp, "Sheared Text","asdf");
        //$endcite
        
        
        //$startcite
        //$name-trans04-
        TextPainter scaleText = new TextPainter("Text", font2);
        comp = new CompoundPainter(scaleText);
        comp.setTransform(AffineTransform.getScaleInstance(2,2));
        addDemo(new JXPanel(), comp, "Scaled Text","trans04");
        //$endcite
        
        
        //$startcite
        //$name-trans05-
        rotText = new TextPainter("Text", font2);
        rectnorm = new RectanglePainter(30,30,30,30,30,30,true,Color.RED,4f,Color.RED.darker());
        comp = new CompoundPainter(rectnorm,rotText);
        comp.setTransform(AffineTransform.getRotateInstance(-Math.PI*2/8,100,100));
        addDemo(new JXPanel(), comp, "Rotated Text w/ effects on rect","trans05");
        //$endcite
    }
    
    private void listDemos(final MultipleGradientPaint gradient) {
        addDemo(new JPanel(), "---- List Renderer Demos");
        
        /* broken until the renderer classes make it out of the incubator
        //$startcite
        //$name-list01-
        //a list with painter effects
        PainterListCellRenderer renderer = new PainterListCellRenderer();
        
        // the list background
        RectanglePainter listRect = new RectanglePainter(1,1,1,1,10,10,true, Color.RED.brighter(), 1, Color.RED.darker());
        CompoundPainter listNormalBg = new CompoundPainter(
                new MattePainter(Color.WHITE),
                listRect);
        renderer.setBackgroundPainter(listNormalBg);
        
        // the list foreground
        TextPainter listText = new TextPainter();
        listText.setFillPaint(Color.WHITE);
        listText.setHorizontalAlignment(TextPainter.HorizontalAlignment.LEFT);
        listText.setInsets(new Insets(0,8,0,0));
        
        renderer.setForegroundPainter(listText);
        
        // the list selection
        Painter selectionPainter = new CompoundPainter(
                new MattePainter(gradient),
                listText);
        renderer.setSelectionPainter(selectionPainter);
        
        // set it on the list
        JList list = createJListWithData();
        list.setCellRenderer(renderer);
        addDemo(list,"JList with red bg + gradient selection","list01");
        //$endcite
        
        //$startcite
        //$name-list02-
        // another list with normal even odds, plus reuse previous selection painter
        PainterListCellRenderer evenodd = new PainterListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                // initialize the renderer (which is 'this')
                super.getListCellRendererComponent(list, value,
                        index, isSelected, cellHasFocus);
                setOpaque(true);
                if(index % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(Color.LIGHT_GRAY);
                }
                return this;
            }
            
        };
        evenodd.setSelectionPainter(selectionPainter);
        list = createJListWithData();
        list.setCellRenderer(evenodd);
        addDemo(list,"JList with even/odd + gradient selection","list02");
        //$endcite*/
    }
    
    private void rectangleDemos() {
        MattePainter gray = new MattePainter(Color.GRAY);
        addDemo(new JPanel(), "---- Rectangle Painter Demos");
        
        RectanglePainter rectnorm = null;
        
        // rect w/ 0 insets
        //$startcite
        //$name-rect01-
        rectnorm = createStandardRectPainter();
        rectnorm.setInsets(new Insets(0,0,0,0));
        addDemo(new JXPanel(),new CompoundPainter(gray,rectnorm),"Rectangle, green on gray, 0px insets","rect01");
        //$endcite
        
        
        //$startcite
        //$name-rect02-
        // normal rectangle
        rectnorm = createStandardRectPainter();
        addDemo(new JXPanel(),new CompoundPainter(gray,rectnorm),"Rectangle, green on gray, 20px insets","rect02");
        //$endcite
        
        
        //$startcite
        //$name-rect03-
        // rect w/ 20px top insets
        rectnorm = createStandardRectPainter();
        rectnorm.setInsets(new Insets(50,0,0,0));
        addDemo(new JXPanel(),new CompoundPainter(gray,rectnorm),"Rectangle 50px top insets","rect03");
        //$endcite
        
        
        //$startcite
        //$name-rect04-
        // do the fixed size demos
        rectnorm = create50pxRectPainter();
        addDemo("Rectangle, 50x50, default aligned (center)", "rect04", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect05-
        // rect 50x50 left aligned
        rectnorm = create50pxRectPainter();
        rectnorm.setHorizontalAlignment(RectanglePainter.HorizontalAlignment.LEFT);
        addDemo("Rectangle, 50x50, left aligned", "rect05", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect06-
        // rect 50x50 top aligned
        rectnorm = create50pxRectPainter();
        rectnorm.setVerticalAlignment(RectanglePainter.VerticalAlignment.TOP);
        addDemo("Rectangle, 50x50, top aligned", "rect06", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect07-
        // rect 50x50 top aligned w/ horiz stretch
        rectnorm = create50pxRectPainter();
        rectnorm.setVerticalAlignment(RectanglePainter.VerticalAlignment.TOP);
        rectnorm.setFillHorizontal(true);
        addDemo("Rectangle, 50x50, top aligned w/ horiz stretch", "rect07", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect08-
        // rect 50x50 top aligned w/ vert stretch
        rectnorm = create50pxRectPainter();
        rectnorm.setVerticalAlignment(RectanglePainter.VerticalAlignment.TOP);
        rectnorm.setFillVertical(true);
        addDemo("Rectangle, 50x50, top aligned w/ vert stretch", "rect08", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect09-
        // rect 50x50 top aligned w/ vert & horiz stretch
        rectnorm = create50pxRectPainter();
        rectnorm.setVerticalAlignment(RectanglePainter.VerticalAlignment.TOP);
        rectnorm.setFillHorizontal(true);
        rectnorm.setFillVertical(true);
        addDemo("Rectangle, 50x50, top aligned w/ horiz & vert stretch", "rect09", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect10-
        // rect 50x50 center aligned w/ vert & horiz stretch
        rectnorm = create50pxRectPainter();
        rectnorm.setFillVertical(true);
        rectnorm.setFillHorizontal(true);
        addDemo("Rectangle, 50x50, center aligned w/ horiz & vert stretch", "rect10", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect11-
        // rect 50x50 center aligned w/ vert & horiz stretch & 20px insets
        rectnorm = create50pxRectPainter();
        rectnorm.setFillVertical(true);
        rectnorm.setFillHorizontal(true);
        rectnorm.setInsets(new Insets(20,20,20,20));
        addDemo("Rectangle, 50x50, w/ horiz & vert stretch & 20px insets", "rect11", gray, rectnorm);
        //$endcite
        
        
        //$startcite
        //$name-rect12-
        // rectangle with shadow
        RectanglePainter rectshad = createStandardRectPainter();
        ShadowPathEffect rectShadEffect = new ShadowPathEffect();
        //rectShadEffect.setOffset(new Point(10,10));
        rectshad.setAreaEffects(rectShadEffect);
        addDemo(new JXPanel(),new CompoundPainter(gray,rectshad),"Rectangle with shadow", "rect12" );
        //$endcite
        
        
        
        //$startcite
        //$name-rect13-
        // rectangle with glow
        RectanglePainter rectglow = createStandardRectPainter();
        rectglow.setAreaEffects(new GlowPathEffect());
        addDemo(new JXPanel(),new CompoundPainter(gray,rectglow),"Rectangle with glow", "rect13");
        //$endcite
        
        
        //$startcite
        //$name-rect14-
        // rectangle with inner shadow
        RectanglePainter rectinshad = new RectanglePainter(20,20,20,20, 30,30, true,
                Color.GREEN, 3, Color.GREEN.darker());
        InnerShadowPathEffect rectinshadEffect = new InnerShadowPathEffect();
        rectinshad.setAreaEffects(rectinshadEffect);
        addDemo(new JXPanel(),new CompoundPainter(new MattePainter(Color.GRAY),
                rectinshad),"Rectangle with inner shadow", "rect14");
        //$endcite
        
        
        //$startcite
        //$name-rect15-
        // rectangle with inner glow
        RectanglePainter rectinglow = new RectanglePainter(20,20,20,20, 30,30, true,
                Color.GREEN, 3, Color.GREEN.darker());
        InnerGlowPathEffect rectinglowEffect = new InnerGlowPathEffect();
        rectinglow.setAreaEffects(rectinglowEffect);
        addDemo(new JXPanel(),new CompoundPainter(new MattePainter(Color.GRAY),
                rectinglow),"Rectangle with inner glow", "rect15" );
        //$endcite
        
        
        //$startcite
        //$name-rect16-
        // rectangle with cool border
        RectanglePainter rectneon = new RectanglePainter(20,20,20,20, 30,30, true,
                Color.GREEN, 3, Color.GREEN.darker());
        rectneon.setStyle(RectanglePainter.Style.FILLED);
        rectneon.setAreaEffects(new NeonBorderEffect(Color.WHITE, Color.ORANGE, 20));
        addDemo(new JXPanel(),new CompoundPainter(new MattePainter(Color.GRAY),
                rectneon),"Rectangle with neon border", "rect16" );
        //$endcite
        
        
        //$startcite
        //$name-rect17-
        // rectangle w/ pink neon border
        rectneon = createStandardRectPainter();
        rectneon.setFillPaint(Color.BLACK);
        rectneon.setStyle(RectanglePainter.Style.FILLED);
        rectneon.setAreaEffects(new NeonBorderEffect(new Color(255,100,100), new Color(255,255,255), 30));
        addDemo("Rectangle w/ pink neon border", "rect17", new MattePainter(Color.BLACK), rectneon);
        //$endcite
        
        
        RectanglePainter rect = null;
        //$startcite
        //$name-rect18-
        // red rectangle w/ 3px black border
        rect = new RectanglePainter(5,5,5,5,10,10,true,Color.RED,3,Color.BLACK);
        addDemo("Red Rectangle w/ 3px black border", "rect18", new MattePainter(Color.WHITE), rect);
        //$endcite
        //$startcite
        //$name-rect19-
        // red rectangle w/ 2px black border
        rect = new RectanglePainter(5,5,5,5,10,10,true,Color.RED,2,Color.BLACK);
        addDemo("Red Rectangle w/ 2px black border", "rect19", new MattePainter(Color.WHITE), rect);
        //$endcite
        //$startcite
        //$name-rect20-
        // red rectangle w/ 1px black border
        rect = new RectanglePainter(5,5,5,5,10,10,true,Color.RED,1,Color.BLACK);
        addDemo("Red Rectangle w/ 1px black border", "rect20", new MattePainter(Color.WHITE), rect);
        //$endcite
        //$startcite
        //$name-rect21-
        // red rectangle w/ 0px black border
        rect = new RectanglePainter(5,5,5,5,10,10,true,Color.RED,0,Color.BLACK);
        addDemo("Red Rectangle w/ 0px black border", "rect21", new MattePainter(Color.WHITE), rect);
        //$endcite
        
        rect = new RectanglePainter(Color.BLACK,Color.RED);
        rect.setInsets(new Insets(0,0,0,0));
        rect.setStyle(RectanglePainter.Style.BOTH);
        rect.setBorderWidth(1f);
        addDemo("Plain rect for sizing bugs","rect22",new MattePainter(Color.GREEN),rect);
        
        rect = new RectanglePainter(Color.BLACK,Color.RED);
        rect.setRoundHeight(10);
        rect.setRoundWidth(10);
        rect.setRounded(true);
        rect.setInsets(new Insets(0,0,0,0));
        rect.setStyle(RectanglePainter.Style.BOTH);
        rect.setBorderWidth(1f);
        addDemo("Plain round rect for sizing bugs","rect23",new MattePainter(Color.GREEN),rect);
    }
    
    private void textDemos(final MultipleGradientPaint gradient) {
        addDemo(new JPanel(), "---- Text Demos");
        CompoundPainter comp;
        
        // normal text
        Font font = new Font("SansSerif", Font.BOLD, 80);
        
        
        //$startcite
        //$name-text01-
        TextPainter textnorm = new TextPainter("Neon", font, Color.RED);
        comp = new CompoundPainter(new MattePainter(Color.GRAY),textnorm);
        addDemo(new JXPanel(),comp,"Text with no effects","text01");
        //$endcite
        
        //$startcite
        //$name-text02-
        // text AA on
        MattePainter gray = new MattePainter(Color.GRAY);
        TextPainter text = new TextPainter("Neon",font,Color.BLACK);
        text.setAntialiasing(true);//TextPainter.Antialiasing.On);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text AA on","text02");
        //$endcite
        
        
        
        //$startcite
        //$name-text03-
        // text AA off
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setAntialiasing(false);//TextPainter.Antialiasing.Off);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text AA off","text03");
        //$endcite
        
        
        
        //$startcite
        //$name-text04-
        // text left aligned
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setHorizontalAlignment(TextPainter.HorizontalAlignment.LEFT);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text Left aligned","text04");
        //$endcite
        
        
        //$startcite
        //$name-text05-
        // text right aligned
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setHorizontalAlignment(TextPainter.HorizontalAlignment.RIGHT);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text Right aligned","text05");
        //$endcite
        
        
        
        //$startcite
        //$name-text06-
        // text top aligned
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setVerticalAlignment(TextPainter.VerticalAlignment.TOP);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text Top aligned","text06");
        //$endcite
        
        
        
        //$startcite
        //$name-text07-
        // text bottom aligned
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setVerticalAlignment(TextPainter.VerticalAlignment.BOTTOM);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text Bottom aligned","text07");
        //$endcite
        
        
        //$startcite
        //$name-text08-
        // text bottom aligned with insets
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setVerticalAlignment(TextPainter.VerticalAlignment.BOTTOM);
        text.setInsets(new Insets(0,0,20,0));
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text Bottom aligned with 20px inset","text08");
        //$endcite
        
        
        //$startcite
        //$name-text09-
        // text with gradient
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setFillPaint(gradient);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text with gradient","text09");
        //$endcite
        
        
        //$startcite
        //$name-text10-
        // text with snapped gradient
        text = new TextPainter("Neon",font,Color.BLACK);
        text.setFillPaint(gradient);
        text.setPaintStretched(true);
        addDemo(new JXPanel(), new CompoundPainter(gray,text),"Text with snapped gradient","text10");
        //$endcite
        
        
        //$startcite
        //$name-text11-
        // text w/ dropshadow
        TextPainter textshadow = new TextPainter("Neon", font, Color.RED);
        ShadowPathEffect shadow = new ShadowPathEffect();
        //shadow.setOffset(new Point(3,3));
        textshadow.setAreaEffects(shadow);
        comp = new CompoundPainter(new MattePainter(Color.GRAY),textshadow);
        addDemo(new JXPanel(),comp,"Text with shadow","text11");
        //$endcite
        
        
        //$startcite
        //$name-text12-
        // text w/ glow effet
        TextPainter textglow = new TextPainter("Neon", font, Color.RED);
        GlowPathEffect glow = new GlowPathEffect();
        //glow.setOffset(new Point(0,0));
        textglow.setAreaEffects(glow);
        comp = new CompoundPainter(new MattePainter(Color.GRAY),textglow);
        addDemo(new JXPanel(),comp,"Text with glow","text12");
        //$endcite
        
        
        //$startcite
        //$name-text13-
        // text w/ inner shadow effect
        TextPainter textinshad = new TextPainter("Neon", font, Color.RED);
        textinshad.setAreaEffects(new InnerShadowPathEffect());
        comp = new CompoundPainter(new MattePainter(Color.GRAY),textinshad);
        addDemo(new JXPanel(), comp, "Text with inner shadow","text13");
        //$endcite
        
        
        //$startcite
        //$name-text14-
        // text w/ inner glow effect
        TextPainter textinglow = new TextPainter("Neon", font, Color.RED);
        textinglow.setAreaEffects(new InnerGlowPathEffect());
        comp = new CompoundPainter(new MattePainter(Color.GRAY),textinglow);
        addDemo(new JXPanel(), comp, "Text with inner glow","text14");
        //$endcite
    }
    
    private void shapeDemos() {
        addDemo(new JPanel(), "---- Shape Demos");
        Shape starShape = ShapeUtils.generatePolygon(5,30,15,true);
        // build a star shape with 5 points and 30 degree angles
        ShapePainter star = null;
        
        //$startcite
        //$name-star01-
        // star filled
        star = new ShapePainter(starShape, Color.RED);
        star.setStyle(ShapePainter.Style.FILLED);
        addDemo(new JXPanel(),star,"Star style = filled","star01");
        //$endcite
        
        
        
        //$startcite
        //$name-star02-
        // star outline
        star = new ShapePainter(starShape, Color.RED);
        star.setStyle(ShapePainter.Style.OUTLINE);
        addDemo(new JXPanel(),star,"Star style = outline","star02");
        //$endcite
        
        
        //$startcite
        //$name-star03-
        // star both
        star = new ShapePainter(starShape, Color.RED);
        star.setStyle(ShapePainter.Style.BOTH);
        addDemo(new JXPanel(),star,"Star style = both","star03");
        //$endcite
        
        
        //$startcite
        //$name-star04-
        // star both
        star = new ShapePainter(starShape, Color.RED);
        star.setStyle(ShapePainter.Style.BOTH);
        star.setBorderWidth(6f);
        addDemo(new JXPanel(),star,"Star border width = 5","star04");
        //$endcite
        
        //$startcite
        //$name-star05-
        // left/top aligned star
        star = new ShapePainter(starShape, Color.RED);
        star.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        star.setVerticalAlignment(ShapePainter.VerticalAlignment.TOP);
        addDemo(new JXPanel(),star,"Star, left & top aligned","star05");
        //$endcite
        
        //$startcite
        //$name-star06-
        // left/bottom aligned star
        star = new ShapePainter(starShape, Color.RED);
        star.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        star.setVerticalAlignment(ShapePainter.VerticalAlignment.BOTTOM);
        addDemo(new JXPanel(),star,"Star, left & bottom aligned","star06");
        //$endcite
        
        
        //$startcite
        //$name-star07-
        // left/top aligned with insets
        star = new ShapePainter(starShape, Color.RED);
        star.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        star.setVerticalAlignment(ShapePainter.VerticalAlignment.TOP);
        star.setInsets(new Insets(50,50,50,50));
        addDemo(new JXPanel(),star,"Star, left & top aligned, 50px insets","star07");
        //$endcite
        
        
        
        //$startcite
        //$name-star08-
        // left aligned only with left insets
        star = new ShapePainter(starShape, Color.RED);
        star.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        star.setInsets(new Insets(0,50,0,0));
        addDemo(new JXPanel(),star,"Star, left aligned, 50px left insets","star08");
        //$endcite
        
        
        
        //$startcite
        //$name-star09-
        // left aligned only with left top insets
        star = new ShapePainter(starShape, Color.RED);
        star.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        star.setInsets(new Insets(50,50,0,0));
        addDemo(new JXPanel(),star,"Star, left aligned, 50px left & top insets","star09");
        //$endcite
        
        //$startcite
        //$name-star10-
        // the same star, but with a drop shadow
        star = new ShapePainter(starShape, Color.RED);
        star.setStyle(ShapePainter.Style.FILLED);
        star.setAreaEffects(new ShadowPathEffect());
        addDemo(new JXPanel(), star, "Star with drop shadow","star10");
        //$endcite
    }
    
    private void addGlossDemos() {
        addDemo(new JPanel(), "---- Gloss Demos");
        
        
        //$startcite
        //$name-gloss01-
        RectanglePainter rect = new RectanglePainter(20,20,20,20, 20,20);
        rect.setFillPaint(Color.RED);
        rect.setBorderPaint(Color.RED.darker());
        rect.setStyle(RectanglePainter.Style.BOTH);
        rect.setBorderWidth(5);
        rect.setAntialiasing(true);//RectanglePainter.Antialiasing.On);
        addDemo("Gloss on rectangle","gloss01",new MattePainter(Color.BLACK), rect,new GlossPainter());
        //$endcite
        
        //$startcite
        //$name-gloss02-
        rect = new RectanglePainter(20,20,20,20, 20,20, true, Color.RED, 5f, Color.RED.darker());
        //rect.setClipPreserved(true);
        rect.setAntialiasing(true);//RectanglePainter.Antialiasing.On);
        addDemo("broken: Gloss clipped on rectangle","gloss02",new MattePainter(Color.BLACK), rect,new GlossPainter());
        //$endcite
        
    }
    
    private void addPinstripeDemos() {
        addDemo(new JPanel(), "---- Pinstripe Demos");
        
        //$startcite
        //$name-pinstripe1-
        MattePainter black = new MattePainter(Color.BLACK);
        RectanglePainter rect = new RectanglePainter(20,20,20,20, 20,20, true, Color.RED, 5f, Color.RED.darker());
        rect.setAntialiasing(true);//RectanglePainter.Antialiasing.On);
        PinstripePainter pin = new PinstripePainter(Color.WHITE, 45, 1, 10);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        addDemo("45deg white pinstripe on black", "pinstripe1", black, pin);
        //$endcite
        
        
        //$startcite
        //$name-pinstripe2-
        pin = new PinstripePainter(Color.WHITE, 0, 1, 10);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        addDemo("vertical white pinstripe on black", "pinstripe2", black, pin);
        //$endcite
        
        
        //$startcite
        //$name-pinstripe3-
        pin = new PinstripePainter(Color.WHITE, 90, 1, 10);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        addDemo("horizontal white pinstripe on black", "pinstripe3",  black, pin);
        //$endcite
        
        
        //$startcite
        //$name-pinstripe4-
        pin = new PinstripePainter(Color.WHITE, 45, 3, 10);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        addDemo("3px wide white pinstripe on black","pinstripe4", black, pin);
        //$endcite
        
        
        
        
        //$startcite
        //$name-pinstripe5-
        pin = new PinstripePainter(Color.WHITE, 45, 10, 2);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        addDemo("10px wide pinstripe w/ 2px spacing on black",  "pinstripe5", black, pin);
        //$endcite
        
        
        //$startcite
        //$name-pinstripe6-
        pin = new PinstripePainter(Color.WHITE, 45, 3, 15);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        pin.setPaint(new GradientPaint(new Point(0,0), Color.WHITE, new Point(10,10), Color.BLACK));
        addDemo("pinstripe w/ 10px gradient ",  "pinstripe6", black, pin);
        //$endcite
        
        //$startcite
        //$name-pinstripe7-
        pin = new PinstripePainter(Color.WHITE, 45, 3, 15);
        pin.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        pin.setPaint(new GradientPaint(new Point(0,0), Color.WHITE, new Point(200,200), Color.BLACK));
        
        addDemo("pinstripe w/ 200px gradient ",  "pinstripe7", black, pin);
        //$endcite
        
    }
    
    private void addPainterSetAPIDemos() {
        addDemo(new JPanel(), "---- PainterSet API tests");
        
        //$startcite
        //$name-painterset1-
        JXPanel panel = new JXPanel();
        panel.setBackgroundPainter(new MattePainter(Color.GREEN));
        addDemo(panel,"panel w/ green matte background","painterset1");
        //$endcite
        
        //$startcite
        //$name-painterset2-
        panel = new JXPanel();
        panel.setBackgroundPainter(new PinstripePainter(Color.BLUE));
        addDemo(panel,"panel w/ blue pinstripe foreground","painterset2");
        //$endcite
        
        //$startcite
        //$name-painterset3-
        panel = new JXPanel();
        panel.setBackgroundPainter(new CompoundPainter(
                new MattePainter(Color.GREEN),
                new PinstripePainter(Color.BLUE)));
        addDemo(panel,"panel w/ blue pinstripe fg, green matte bg","painterset3");
        //$endcite
        
        //$startcite
        //$name-painterset4-
        panel = new JXPanel();
        panel.setBackgroundPainter(new CompoundPainter(
                new MattePainter(Color.GREEN),
                new RectanglePainter(new Insets(20,20,20,20),
                50,50,10,10,true,Color.RED,5,Color.RED.darker()),
                new PinstripePainter(Color.BLUE)));
        addDemo(panel,"panel, blue stripe fg, green bg, red rect comp","painterset4");
        //$endcite
        
        //$startcite
        //$name-painterset5-
        panel = new JXPanel();
        panel.setBackgroundPainter(new PinstripePainter(Color.BLUE,0));
        panel.setBackgroundPainter(new PinstripePainter(Color.RED,90));
        addDemo(panel,"red fg replaces blue fg","painterset5");
        //$endcite
        
        //$startcite
        //$name-painterset6-
        panel = new JXPanel();
        AbstractPainter pt = new TextPainter("Some Text");
        panel.setBackgroundPainter(new CompoundPainter(new RectanglePainter(20,20,5,Color.BLUE) ,pt));
        
        // pull out all of the painters and wrap them in a compound painter
        Painter compPainter = panel.getBackgroundPainter();
        CompoundPainter comp = new CompoundPainter(compPainter);
        comp.setTransform(AffineTransform.getRotateInstance(Math.PI*2/16,100,100));
        panel.setBackgroundPainter(comp);
        addDemo(panel,"Broken?: rotate entire set of painters","painterset6");
        //$endcite
        
        //$startcite
        //$name-painterset7-
        JXLabel label = new JXLabel("A JLabel");
        addDemo(label,"normal label","painterset7");
        //$endcite
        
        //$startcite
        //$name-painterset8-
        label = new JXLabel("A JLabel");
        label.setForegroundPainter(new MattePainter(Color.RED));
        addDemo(label,"normal label w/ red bg","painterset8");
        //$endcite
        
        //$startcite
        //$name-painterset9-
        label = new JXLabel("An invalid JLabel");
        Shape star = ShapeUtils.generatePolygon(20, 30,25,true);
        ShapePainter shapePainter = new ShapePainter(star,new Color(255,0,0,200));
        shapePainter.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        shapePainter.setVerticalAlignment(ShapePainter.VerticalAlignment.CENTER);
        label.setForegroundPainter(shapePainter);
        addDemo(label,"label + shape painter in bg layer","painterset9");
        //$endcite
        
        //$startcite
        //$name-painterset10-
        label = new JXLabel("An invalid JLabel");
        star = ShapeUtils.generatePolygon(20, 30,25,true);
        shapePainter = new ShapePainter(star, new Color(255,0,0,200));
        shapePainter.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        shapePainter.setVerticalAlignment(ShapePainter.VerticalAlignment.CENTER);
        label.setForegroundPainter(shapePainter);
        addDemo(label,"BROKEN! label + shape painter in validation layer","painterset10");
        //$endcite
        
        //$startcite
        //$name-painterset11-
        label = new JXLabel("An invalid JLabel");
        star = ShapeUtils.generatePolygon(20, 30,25,true);
        shapePainter = new ShapePainter(star, new Color(255,0,0,200));
        shapePainter.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        shapePainter.setVerticalAlignment(ShapePainter.VerticalAlignment.CENTER);
        //label.setPainter(JXComponent.VALIDATION_LAYER, shapePainter);
        TextPainter tp = new TextPainter("!!!",Color.GREEN);
        //label.getPainters().get(JXComponent.VALIDATION_LAYER).add(tp);
        //label.setPainter(JXComponent.VALIDATION_LAYER, tp);
        addDemo(label,"BROKEN! label, 2 validation using addPainter","painterset11");
        //$endcite
        
        //$startcite
        //$name-painterset12-
        label = new JXLabel("An invalid JLabel");
        star = ShapeUtils.generatePolygon(20, 30,25,true);
        shapePainter = new ShapePainter(star, new Color(255,0,0,200));
        shapePainter.setHorizontalAlignment(ShapePainter.HorizontalAlignment.LEFT);
        shapePainter.setVerticalAlignment(ShapePainter.VerticalAlignment.CENTER);
        //label.setPainter(JXComponent.VALIDATION_LAYER,shapePainter);
        tp = new TextPainter("!!!",Color.GREEN);
        //label.setPainter(JXComponent.VALIDATION_LAYER, tp);
        addDemo(label,"BROKEN! label, 2 validation using setPainter","painterset12");
        //$endcite
        
        
        //$startcite
        //$name-painterset13-
        label = new JXLabel("An normal label");
        label.setFont(label.getFont().deriveFont(36f));
        Painter ptr = label.getForegroundPainter();
        if(ptr instanceof AbstractPainter) {
            ((AbstractPainter)ptr).setFilters(new ShadowFilter());
        }
        addDemo(label, "label w/ image effect","painterset13");
        //$endcite
        
    }
    
    
    private void imageDemos() {
        addDemo(new JPanel(), "---- ImagePainter Demos");
        
        //$startcite
        //$name-image02-
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            addDemo("small image, default alignment (center)","image02",ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //$endcite
        
        
        //$startcite
        //$name-image01-
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("moon.jpg"));
            ImagePainter ip = new ImagePainter(img);
            addDemo("big image, default alignment (center)","image01",ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //$endcite
        
        
        //bottom aligned
        //$startcite
        //$name-image03-
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("moon.jpg"));
            ImagePainter ip = new ImagePainter(img);
            ip.setVerticalAlignment(ImagePainter.VerticalAlignment.BOTTOM);
            addDemo("bottom aligned","image03",ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //$endcite
        
        
        //right aligned
        //$startcite
        //$name-image04-
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("moon.jpg"));
            ImagePainter ip = new ImagePainter(img);
            ip.setHorizontalAlignment(ImagePainter.HorizontalAlignment.RIGHT);
            addDemo("right aligned","image04",ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //$endcite
        
        
        //top aligned with horizontal repeat
        //$startcite
        //$name-image05-
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setVerticalAlignment(ImagePainter.VerticalAlignment.TOP);
            ip.setHorizontalRepeat(true);
            addDemo("top aligned, horizontal repeat","image05",ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //$endcite
        
        
        
        //top aligned with horizontal repeat downset by 10px
        //top aligned with horizontal repeat
        try {
            //$startcite
            //$name-image06-
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setVerticalAlignment(ImagePainter.VerticalAlignment.TOP);
            ip.setHorizontalRepeat(true);
            ip.setInsets(new Insets(20,0,0,0));
            addDemo("top aligned, horizontal repeat, top = 20px","image06",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        // set a border on the image
        try {
            //$startcite
            //$name-image08-
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setBorderPaint(Color.BLACK);
            ip.setBorderWidth(3);
            addDemo("image with black border","image08",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // set a colored background on an image w/ transparent parts
        try {
            //$startcite
            //$name-image09-
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("a-glyph.png"));
            ImagePainter ip = new ImagePainter(img);
            addDemo("An image of 'A' with transparent parts","image09",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // set a colored background on an image w/ transparent parts
        try {
            //$startcite
            //$name-image10-
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("a-glyph.png"));
            ImagePainter ip = new ImagePainter(img);
            ip.setFillPaint(Color.RED);
            addDemo("red background visible through transparent parts","image10",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        
        // set a dropshadow path effect on the image
        try {
            //$startcite
            //$name-image07-
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("border.gif"));
            ImagePainter ip = new ImagePainter(img);
            ip.setAreaEffects(new ShadowPathEffect());
            addDemo("image with shadow path effect","image07",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        
        // set an image using a remote url
        try {
            //$startcite
            //$name-image11-
            ImagePainter ip = new ImagePainter();
            ip.setImageString("http://java.sun.com/developer/techDocs/hi/repository/graphicsRepository/toolbarButtonGraphics/general/Delete24.gif");
            addDemo("image loaded from remote URL","image11",ip);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // load an image painter using persisted xml
        try {
            //$startcite
            //$name-image12-
            Painter ptr = PainterUtil.loadPainter(new File("/Users/joshy/Desktop/blah.xml"));
            addDemo("Painter set loaded from xml file with remote URL image","image12",ptr);
            //$endcite
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    
    private RectanglePainter create50pxRectPainter() {
        RectanglePainter rectnorm;
        
        // rectangle positioning
        // rect 50x50 default aligned (center)
        rectnorm = new RectanglePainter(50, 50, 30, Color.GREEN);
        rectnorm.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        rectnorm.setBorderPaint(Color.GREEN.darker());
        //rectnorm.setStyle(AbstractPainter.Style.FILLED);
        rectnorm.setBorderWidth(3);
        return rectnorm;
    }
    
    private RectanglePainter createStandardRectPainter() {
        RectanglePainter rectnorm;
        // rect w/ 20px top insets
        rectnorm = new RectanglePainter(20,20,20,20, 30,30, true,
                Color.GREEN, 3, Color.GREEN.darker());
        rectnorm.setAntialiasing(true);//AbstractPainter.Antialiasing.On);
        return rectnorm;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        citationText = new javax.swing.JTextArea();
        painterPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        painterList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jSplitPane1.setDividerLocation(240);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane2.setDividerLocation(150);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setContinuousLayout(true);
        citationText.setColumns(20);
        citationText.setRows(5);
        jScrollPane2.setViewportView(citationText);

        jSplitPane2.setBottomComponent(jScrollPane2);

        painterPanel.setLayout(new java.awt.BorderLayout());

        painterPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane2.setTopComponent(painterPanel);

        jSplitPane1.setRightComponent(jSplitPane2);

        painterList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        painterList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                painterListValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(painterList);

        jSplitPane1.setLeftComponent(jScrollPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void painterListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_painterListValueChanged
        Demo demo = (Demo)painterList.getSelectedValue();
        painterPanel.removeAll();
        painterPanel.add(demo.component,"Center");
        //demo.component.repaint();
        citationText.setText((String) citeMap.get(demo.citeid));
        painterPanel.revalidate();
        painterPanel.repaint();
    }//GEN-LAST:event_painterListValueChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PainterDemoSet().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea citationText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JList painterList;
    private javax.swing.JPanel painterPanel;
    // End of variables declaration//GEN-END:variables
    
    private void addDemo(JComponent component, Painter painter, String string) {
        addDemo(component, painter, string, "");
    }
    private void addDemo(JComponent component, Painter painter, String string, String citename) {
        ((DefaultListModel)painterList.getModel()).addElement(new Demo(component, string, citename));
        //painterPanel.removeAll();
        //painterPanel.add(component, "Center");
        if(component instanceof JXPanel) {
            ((JXPanel)component).setBackgroundPainter(painter);
        }
        if(component instanceof JXButton) {
            ((JXButton)component).setForegroundPainter(painter);
        }
        //painterPanel.repaint();
    }
    
    private void addDemo(JComponent component, String string) {
        ((DefaultListModel)painterList.getModel()).addElement(new Demo(component, string, ""));
    }
    private void addDemo(JComponent component, String string, String citename) {
        ((DefaultListModel)painterList.getModel()).addElement(new Demo(component, string, citename));
    }
    private void addDemo(String text, Painter ... painters) {
        addDemo(new JXPanel(),new CompoundPainter(painters),text);
    }
    private void addDemo(String text, String citename, Painter ... painters) {
        addDemo(new JXPanel(),new CompoundPainter(painters),text, citename);
    }
    
    private JList createJListWithData() {
        String[] data = { "Item 1", "Item 2", "Item 3", "Item 4" };
        return new JList(data);
    }
    
    private JTable createJTableWithData(final boolean editable) {
        String[] columns = { "Song", "Artist", "Album"};
        String[][] data = {
            { "Love Me Do", "The Beatles", "With the Beatles"},
            { "Evil Woman", "ELO", "Classics" },
            { "Crash", "Dave Mathews Band", "Crash" }
        };
        return new JTable(new DefaultTableModel(data,columns) {
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        });
    }
    
    private void p(String string) {
        System.out.println(string);
    }
    
    
    
    
    
    private class Demo {
        public JComponent component;
        public String title;
        public String citeid;
        public Demo(JComponent component, String title) {
            this.component = component;
            this.title = title;
        }
        public Demo(JComponent component, String title, String citeid) {
            this.component = component;
            this.title = title;
            this.citeid = citeid;
        }
        public String toString() {
            return this.title;
        }
    }
    
    
    private void genericsDemos() {
        addDemo(new JPanel(), "---- Generics Demos");
        
        // normal text
        Font font = new Font("SansSerif", Font.BOLD, 80);
        
        
        //$startcite
        //$name-generics01-
        TextPainter textnorm = new TextPainter("Neon", font, Color.RED);
        JXPanel panel = new JXPanel();
        panel.setBackgroundPainter(new Painter<JXLabel>() {
            public void paint(Graphics2D g, JXLabel object, int width, int height) {
                System.out.println("painting a label: " + object);
            }
        });
        addDemo(panel,"Text with no effects","generics01");
        //$endcite
        
    }

    private void addGradientDemos() {
        addDemo(new JPanel(), "---- Gradient Demos ----");
        MattePainter matte = null;
        Paint paint;
        
        
        //$startcite
        //$name-grad01- // show a normal gradient, horiz
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(80,30), Color.GREEN);
        matte = new MattePainter(paint);
        addDemo("single stop, horiz","grad01",matte);
        //$endcite
        
        //$startcite
        //$name-grad02- // show a normal gradient snapped horiz
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(80,30), Color.GREEN);
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("single stop, horiz snapped","grad02",matte);
        //$endcite
        
        
        //$startcite
        //$name-grad11- // show a normal gradient, vert
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(30,80), Color.GREEN);
        matte = new MattePainter(paint);
        addDemo("single stop, vert","grad11",matte);
        //$endcite
        
        //$startcite
        //$name-grad12- // show a normal gradient snapped vert
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(30,80), Color.GREEN);
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("single stop, vert, snapped","grad12",matte);
        //$endcite
        
        //$startcite
        //$name-grad13- // show a normal gradient, diag
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(80,80), Color.GREEN);
        matte = new MattePainter(paint);
        addDemo("single stop, diag","grad13",matte);
        //$endcite
        
        //$startcite
        //$name-grad14- // show a normal gradient snapped diag
        paint = new GradientPaint(new Point(30,30),Color.RED,
                new Point(80,80), Color.GREEN);
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("single stop, diag, snapped","grad14",matte);
        //$endcite
        
        
        
        //$startcite
        //$name-grad07- // show a multi stop gradient, horiz
        paint = new LinearGradientPaint(new Point(30,30), new Point(80,30), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        addDemo("multi stop horiz","grad07",matte);
        //$endcite
        
        //$startcite
        //$name-grad08- // show a multi stop gradient, horiz snapped 
        paint = new LinearGradientPaint(new Point(30,30), new Point(80,30), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("multi stop horiz, snapped","grad08",matte);
        //$endcite
        
        //$startcite
        //$name-grad09- // show a multi stop gradient, vert
        paint = new LinearGradientPaint(new Point(30,30), new Point(30,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        addDemo("multi stop vert","grad09",matte);
        //$endcite
        
        //$startcite
        //$name-grad10- // show a multi stop gradient, vert snapped 
        paint = new LinearGradientPaint(new Point(30,30), new Point(30,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("multi stop vert, snapped","grad10",matte);
        //$endcite
        
        
        //$startcite
        //$name-grad05- // show a multi stop gradient, diag
        paint = new LinearGradientPaint(new Point(30,30), new Point(80,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        addDemo("multi stop diag","grad05",matte);
        //$endcite
        
        //$startcite
        //$name-grad06- // show a multi stop gradient, snapped, diag
        paint = new LinearGradientPaint(new Point(30,30), new Point(80,80), 
                new float[] {0f, 0.5f, 1f},
                new Color[] {Color.RED, Color.GREEN, Color.BLUE} );
        matte = new MattePainter(paint);
        matte.setPaintStretched(true);
        addDemo("multi stop diag, snapped","grad06",matte);
        //$endcite
        
    }
}

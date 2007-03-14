/*
 * URLPainterTest.java
 *
 * Created on December 18, 2006, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.demo;

import java.io.File;
import java.net.URL;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 *
 * @author joshy
 */
public class URLPainterTest {
    
    /** Creates a new instance of URLPainterTest */
    public static void main(String ... args) throws Exception {
        ImagePainter ip;
        p("loading from an absolute xml url & absolute image url");
        ip = (ImagePainter) PainterUtil.loadPainter(
                new File("/Users/joshy/projects/current/swingx/"+
                "src/beaninfo/org/jdesktop/swingx/painter/demo/URLPainterTest1.xml").toURL());
        p("image = " + ip.getImage());
        
        p("-----------");
        p("loading from a classpath xml url & absolute image url");
        ip = (ImagePainter) PainterUtil.loadPainter(
                URLPainterTest.class.getResource("URLPainterTest1.xml"));
        p("image = " + ip.getImage());
        
        p("-----------");
        p("loading from an absolute xml url & relative image url");
        ip = (ImagePainter) PainterUtil.loadPainter(
                new File("/Users/joshy/projects/current/swingx/"+
                "src/beaninfo/org/jdesktop/swingx/painter/demo/URLPainterTest2.xml").toURL());
        p("image = " + ip.getImage());
        
        p("-----------");
        p("loading from a classpath xml url & relative image url");
        ip = (ImagePainter) PainterUtil.loadPainter(
                URLPainterTest.class.getResource("URLPainterTest2.xml"));
        p("image = " + ip.getImage());
        
    }

    private static void p(String string) {
        System.out.println(string);
    }
    
}

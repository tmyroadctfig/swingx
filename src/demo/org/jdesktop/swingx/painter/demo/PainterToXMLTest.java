/*
 * PainterToXMLTest.java
 *
 * Created on December 18, 2006, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.demo;

import java.io.File;

import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 *
 * @author joshy
 */
public class PainterToXMLTest {
    
    public static void main(String ... args) throws Exception {
        p("testing saving an image painter with absolute image urls");
        ImagePainter ip = new ImagePainter();
        ip.setImageString("file:/Users/joshy/Pictures/cooltikis.jpg");
        File outfile = new File("/Users/joshy/Desktop/test.xml");
        PainterUtil.savePainterToFile(ip, outfile, outfile.getParentFile().toURI().toURL());
        p("testing saving an image painter with relative image URLs");
        outfile = new File("/Users/joshy/Pictures/deleteme.xml");
        PainterUtil.savePainterToFile(ip, outfile, outfile.getParentFile().toURI().toURL());
    }

    private static void p(String string) {
        System.out.println(string);
    }
}

package org.jdesktop.swingx.plaf.basic;

import static org.junit.Assert.*;

import java.applet.Applet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.jdesktop.swingx.JXErrorPane;
import org.junit.Before;
import org.junit.Test;

public class BasicErrorPaneUITest {

    private BasicErrorPaneUI ui;

    @Before
    public void setup() {
        JXErrorPane pane = new JXErrorPane();
        assertTrue(pane.getUI().getClass().getName().equals(BasicErrorPaneUI.class.getName()));
        ui = (BasicErrorPaneUI) pane.getUI();
    }

    @Test
    public void testGetErrorFrameNull() {
        assertNotNull(ui.getErrorFrame(null));
    }
    @Test
    public void testGetErrorFrameFrame() {
        assertNotNull(ui.getErrorFrame(new JFrame()));
    }
    @Test
    public void testGetErrorFrameDialog() {
        assertNotNull(ui.getErrorFrame(new JDialog()));
    }
    @Test
    public void testGetErrorFrameContentPane() {
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorFrame(frame.getContentPane()));
    }
    @Test
    public void testGetErrorFrameJPanel() {
        assertNotNull(ui.getErrorFrame(new JPanel()));
    }

    @Test
    public void testGetErrorFrameApplet() {
        assertNotNull(ui.getErrorFrame(new Applet()));
    }

    @Test
    public void testGetErrorFrameJWindow() {
        assertNotNull(ui.getErrorFrame(new JWindow()));
    }

    @Test
    public void testGetErrorDialogNull() {
        assertNotNull(ui.getErrorDialog(null));
    }
    @Test
    public void testGetErrorDialogFrame() {
        assertNotNull(ui.getErrorDialog(new JFrame()));
    }
    @Test
    public void testGetErrorDialogDialog() {
        assertNotNull(ui.getErrorDialog(new JDialog()));
    }
    @Test
    public void testGetErrorDialogContentPane() {
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorDialog(frame.getContentPane()));
    }
    @Test
    public void testGetErrorDialogJPanel() {
        assertNotNull(ui.getErrorDialog(new JPanel()));
    }

    @Test
    public void testGetErrorDialogApplet() {
        assertNotNull(ui.getErrorDialog(new Applet()));
    }

    /**
     * swingx-854 - NPE when owner is JWindow
     */
    @Test
    public void testGetErrorDialogJWindow() {
        assertNotNull(ui.getErrorDialog(new JWindow()));
    }

    @Test
    public void testGetErrorInternalFrameNull() {
        assertNotNull(ui.getErrorInternalFrame(null));
    }
    @Test
    public void testGetErrorInternalFrameFrame() {
        assertNotNull(ui.getErrorInternalFrame(new JFrame()));
    }
    @Test
    public void testGetErrorInternalFrameDialog() {
        assertNotNull(ui.getErrorInternalFrame(new JDialog()));
    }
    @Test
    public void testGetErrorInternalFrameContentPane() {
        JFrame frame = new JFrame();
        assertNotNull(ui.getErrorInternalFrame(frame.getContentPane()));
    }
    @Test
    public void testGetErrorInternalFrameJPanel() {
        assertNotNull(ui.getErrorInternalFrame(new JPanel()));
    }

    @Test
    public void testGetErrorInternalFrameApplet() {
        assertNotNull(ui.getErrorInternalFrame(new Applet()));
    }

    @Test
    public void testGetErrorInternalFrameJWindow() {
        assertNotNull(ui.getErrorInternalFrame(new JWindow()));
    }

}

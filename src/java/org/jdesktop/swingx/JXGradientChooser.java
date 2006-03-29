/*
 * GradientPicker.java
 *
 * Created on January 13, 2006, 3:17 PM
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.jdesktop.swingx.color.*;
import org.jdesktop.swingx.multislider.Thumb;

/**
 *
 * @author  jm158417
 */
public class JXGradientChooser extends JXPanel {
    
    
    public JButton add_thumb_button;
    public JSlider alpha_slider;
    public JSpinner alpha_value;
    public ButtonGroup buttonGroup1;
    public JButton change_color;
    public JSpinner color_location_field;
    public JTextField color_value;
    public JButton delete_thumb_button;
    public JPanel gradientPreview;
    public JPanel gradient_selector;
    public JLabel jLabel1;
    public JLabel jLabel2;
    public JLabel jLabel3;
    public JLabel jLabel4;
    public JLabel jLabel5;
    public JLabel jLabel6;
    public JLabel jLabel7;
    public JLabel jLabel8;
    public JPanel jPanel1;
    public JPanel jPanel2;
    public JPanel jPanel3;
    public JPanel jPanel4;
    public JSpinner jSpinner1;
    public JButton minus_button;
    public JRadioButton no_cycle;
    public JButton plus_button;
    public JRadioButton reflected;
    public JRadioButton repeated;
    public JCheckBox reversed;
    public JComboBox style_list;
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        final JFrame frame = new JFrame("Gradient Picker");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
        
        JButton button = new JButton("Select Gradient");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color[] colors = { Color.blue, Color.black};
                float[] vals = {0.0f,1.0f};
                LinearGradientPaint paint = new LinearGradientPaint(0f,0f,10f,0f,vals,colors);
                MultipleGradientPaint grad = JXGradientChooser.showDialog(frame,"Pick a Gradient",paint);
                System.out.println("got: " + JXGradientChooser.toString(grad));
            }
        });
        frame.add(button);    
        frame.pack();
        frame.setVisible(true);
    }
    
    Paint checker_texture = null;
    private JXMultiThumbSlider<Color> slider = null;
    
    /** Creates new form GradientPicker */
    public JXGradientChooser() {
	initComponents();
	
	SpinnerNumberModel alpha_model = new SpinnerNumberModel(100,0,100,1);
	alpha_value.setModel(alpha_model);
	SpinnerNumberModel location_model = new SpinnerNumberModel(100,0,100,1);
	color_location_field.setModel(location_model);
	
	checker_texture = ColorUtil.getCheckerPaint();
	
	gradient_selector.setLayout(new BorderLayout());
	setSlider(new JXMultiThumbSlider<Color>());
	
	getSlider().setOpaque(false);
	getSlider().setPreferredSize(new Dimension(100,35));
	getSlider().getModel().setMinimumValue(0f);
	getSlider().getModel().setMaximumValue(1.0f);
	
	getSlider().getModel().addThumb(0,Color.black);
	getSlider().getModel().addThumb(0.5f,Color.red);
	getSlider().getModel().addThumb(1.0f,Color.white);
	
	getSlider().setThumbRenderer(new GradientThumbRenderer(this, getSlider()));
	getSlider().setTrackRenderer(new GradientTrackRenderer(this));
	getSlider().addMultiThumbListener(new StopListener(getSlider()));
	
	gradient_selector.add(getSlider(), "Center");
	
	change_color.addActionListener(new ChangeColorListener(getSlider()));
	alpha_slider.addChangeListener(new ChangeAlphaListener(getSlider()));
	ActionListener add_list = new AddThumbListener(getSlider());
	add_thumb_button.addActionListener(add_list);
	plus_button.addActionListener(add_list);
	ActionListener del_list = new DeleteThumbListener(getSlider());
	delete_thumb_button.addActionListener(del_list);
	minus_button.addActionListener(del_list);
	((GradientPreviewPanel)gradientPreview).picker = this;
        
        plus_button.putClientProperty("JButton.buttonType","toolbar");
        minus_button.putClientProperty("JButton.buttonType","toolbar");
	
	reflected.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		gradientPreview.repaint();
	    }
	});
	reversed.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		gradientPreview.repaint();
	    }
	});
	repeated.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		gradientPreview.repaint();
	    }
	});
	no_cycle.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		gradientPreview.repaint();
	    }
	});
	style_list.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		gradientPreview.repaint();
	    }
	});
	
	style_list.setModel(new DefaultComboBoxModel(GradientStyle.values()));
	
    }
    
    
    public MultipleGradientPaint getGradient() {
	// get the list of colors
	List<Thumb<Color>> stops = getSlider().getModel().getSortedThumbs();
	int len = stops.size();
	
	// set up the data for the gradient
	float[] fractions = new float[len];
	Color[] colors = new Color[len];
	int i = 0;
	for(Thumb<Color> thumb : stops) {
	    colors[i] = (Color)thumb.getObject();
	    fractions[i] = thumb.getPosition();
	    i++;
	}
	// fill in the gradient
	Point2D start = new Point2D.Float(0,0);
	Point2D end = new Point2D.Float(getWidth(),0);
	MultipleGradientPaint paint = new org.apache.batik.ext.awt.LinearGradientPaint(
		(float)start.getX(),
		(float)start.getY(),
		(float)end.getX(),
		(float)end.getY(),
		fractions,colors);
	return paint;
    }
    
    public void setGradient(MultipleGradientPaint mgrad) {
	while(getSlider().getModel().getThumbCount() > 0) {
	    getSlider().getModel().removeThumb(0);
	}
	float[] fracts = mgrad.getFractions();
	Color[] colors = mgrad.getColors();
	for(int i=0; i<fracts.length; i++) {
	    getSlider().getModel().addThumb(fracts[i],colors[i]);
	}
	repaint();
    }
    
    private void updateFromStop(Thumb<Color> thumb) {
	if(thumb == null) {
	    updateFromStop(-1,-1,Color.black);
	} else {
	    updateFromStop(1,thumb.getPosition(),thumb.getObject());
	}
    }
    
    private void updateFromStop(int thumb, float position, Color color) {
	if(thumb == -1) {
	    color_location_field.setEnabled(false);
	    alpha_value.setEnabled(false);
	    alpha_slider.setEnabled(false);
	    color_value.setEnabled(false);
	    change_color.setEnabled(false);
	    change_color.setBackground(Color.black);
	    delete_thumb_button.setEnabled(false);
	    minus_button.setEnabled(false);
	} else {
	    color_location_field.setEnabled(true);
	    alpha_value.setEnabled(true);
	    alpha_slider.setEnabled(true);
	    color_value.setEnabled(true);
	    change_color.setEnabled(true);
	    color_location_field.setValue((int)(100*position));
	    color_value.setText(Integer.toHexString(color.getRGB()).substring(2));
	    alpha_value.setValue((int)(color.getAlpha()*100/255));
	    alpha_slider.setValue(color.getAlpha()*100/255);
	    change_color.setBackground(color);
	    delete_thumb_button.setEnabled(true);
	    minus_button.setEnabled(true);
	}
        updateDeleteButtons();
	((GradientPreviewPanel)gradientPreview).repaint();
    }
    private void updateDeleteButtons() {
	if(getSlider().getModel().getThumbCount() <= 2) {
	    delete_thumb_button.setEnabled(false);
	    minus_button.setEnabled(false);
	}
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {/*
        buttonGroup1 = new ButtonGroup();
        jSpinner1 = new JSpinner();
        jPanel2 = new JPanel();
        jPanel3 = new JPanel();
        jPanel1 = new JPanel();
        gradientPreview = new GradientPreviewPanel();
        jLabel7 = new JLabel();
        style_list = new JComboBox();
        reversed = new JCheckBox();
        reflected = new JRadioButton();
        repeated = new JRadioButton();
        no_cycle = new JRadioButton();
        jLabel8 = new JLabel();
        jPanel4 = new JPanel();
        plus_button = new JButton();
        minus_button = new JButton();
        gradient_selector = new JPanel();
        add_thumb_button = new JButton();
        delete_thumb_button = new JButton();
        jLabel4 = new JLabel();
        jLabel3 = new JLabel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel6 = new JLabel();
        jLabel5 = new JLabel();
        color_value = new JTextField();
        color_location_field = new JSpinner();
        alpha_value = new JSpinner();
        alpha_slider = new JSlider();
        change_color = new ColorSelectionButton();

        jPanel2.setBorder(BorderFactory.createTitledBorder("Stops"));
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 371, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 131, Short.MAX_VALUE)
        );
        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 113, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );

        jPanel1.setBorder(BorderFactory.createTitledBorder("Preview"));
        gradientPreview.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        org.jdesktop.layout.GroupLayout gradientPreviewLayout = new org.jdesktop.layout.GroupLayout(gradientPreview);
        gradientPreview.setLayout(gradientPreviewLayout);
        gradientPreviewLayout.setHorizontalGroup(
            gradientPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 125, Short.MAX_VALUE)
        );
        gradientPreviewLayout.setVerticalGroup(
            gradientPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 123, Short.MAX_VALUE)
        );

        jLabel7.setText("Style:");

        style_list.setModel(new DefaultComboBoxModel(new String[] { "Linear", "Radial" }));

        reversed.setText("Reverse");
        reversed.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reversed.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(reflected);
        reflected.setText("Reflect");
        reflected.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reflected.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(repeated);
        repeated.setText("Repeat");
        repeated.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        repeated.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(no_cycle);
        no_cycle.setSelected(true);
        no_cycle.setText("None");
        no_cycle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        no_cycle.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel8.setText("Type:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel7)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(no_cycle)
                    .add(style_list, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(repeated)
                    .add(reversed)
                    .add(reflected))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gradientPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, gradientPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(style_list, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(4, 4, 4)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(no_cycle)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(repeated)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(reflected)
                                .add(22, 22, 22)
                                .add(reversed))
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel4.setBorder(BorderFactory.createTitledBorder("Gradient"));
        plus_button.setText("+");

        minus_button.setText("-");
        minus_button.setEnabled(false);

        org.jdesktop.layout.GroupLayout gradient_selectorLayout = new org.jdesktop.layout.GroupLayout(gradient_selector);
        gradient_selector.setLayout(gradient_selectorLayout);
        gradient_selectorLayout.setHorizontalGroup(
            gradient_selectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 206, Short.MAX_VALUE)
        );
        gradient_selectorLayout.setVerticalGroup(
            gradient_selectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 38, Short.MAX_VALUE)
        );

        add_thumb_button.setText("Add");

        delete_thumb_button.setText("Delete");
        delete_thumb_button.setEnabled(false);

        jLabel4.setText("Location:");

        jLabel3.setText("Color:");

        jLabel1.setText("Opacity:");

        jLabel2.setText("%");

        jLabel6.setText("%");

        jLabel5.setText("#");

        color_value.setText("000");
        color_value.setEnabled(false);

        color_location_field.setEnabled(false);
        color_location_field.addChangeListener(new event.ChangeListener() {
            public void stateChanged(event.ChangeEvent evt) {
                mystatechanged(evt);
            }
        });

        alpha_value.setEnabled(false);
        alpha_value.addChangeListener(new event.ChangeListener() {
            public void stateChanged(event.ChangeEvent evt) {
                alpha_valueStateChanged(evt);
            }
        });

        alpha_slider.setEnabled(false);

        change_color.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(gradient_selector, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(plus_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, Short.MAX_VALUE)
                            .add(minus_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(color_value, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(color_location_field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                    .add(alpha_value))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(alpha_slider, 0, 0, Short.MAX_VALUE)
                                    .add(change_color)))
                            .add(add_thumb_button)))
                    .add(delete_thumb_button))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {minus_button, plus_button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(plus_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                        .add(minus_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(gradient_selector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(delete_thumb_button)
                    .add(add_thumb_button))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(color_value, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(change_color))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jLabel6)
                            .add(color_location_field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(10, 10, 10)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jLabel1)
                            .add(alpha_value, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(alpha_slider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {minus_button, plus_button}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );*/
    }

    private void alpha_valueStateChanged(ChangeEvent evt) {
	if(getSlider().getSelectedIndex() >= 0) {
	    Thumb<Color> thumb = getSlider().getModel().getThumbAt(getSlider().getSelectedIndex());
	    int alpha = (Integer)alpha_value.getValue();
	    int alpha_hex = alpha*255/100;
	    Color new_color = ColorUtil.setAlpha(thumb.getObject(),alpha_hex);
	    thumb.setObject(new_color);
	    alpha_slider.setValue(alpha);
	    getSlider().repaint();
	}
    }

    private void mystatechanged(ChangeEvent evt) {
	if(getSlider().getSelectedIndex() >= 0) {
	    Thumb thumb = getSlider().getModel().getThumbAt(getSlider().getSelectedIndex());
	    thumb.setPosition((float)((Integer)color_location_field.getValue())/100);
	    getSlider().recalc();
	    getSlider().repaint();
	    updateFromStop(thumb);
	}
    }
    
    
    
    private class ChangeColorListener implements ActionListener {
	
	private JXMultiThumbSlider slider;
	
	public ChangeColorListener(JXMultiThumbSlider slider) {
	    super();
	    this.slider = slider;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
	    selectColorForThumb();
	}
    }
    
    private void selectColorForThumb() {
	int index = getSlider().getSelectedIndex();
	if (index >= 0) {
	    Color color = (Color)getSlider().getModel().getThumbAt(index).getObject();
	    color = JColorChooser.showDialog(getSlider(), "Select A Color", color);
	    if (color != null) {
		getSlider().getModel().getThumbAt(index).setObject(color);
		updateFromStop(index, getSlider().getModel().getThumbAt(index).getPosition(), color);
		getSlider().repaint();
		((GradientPreviewPanel)gradientPreview).repaint();
	    }
	}
    }
    
    private class ChangeAlphaListener implements ChangeListener {
	
	private JXMultiThumbSlider slider;
	
	public ChangeAlphaListener(JXMultiThumbSlider slider) {
	    super();
	    this.slider = slider;
	}
	
	public void stateChanged(ChangeEvent changeEvent) {
	    
	    int index = slider.getSelectedIndex();
	    if (index >= 0) {
		// get the alpha
		int alpha = alpha_slider.getValue();
		// update the thumb
		Thumb<Color> thumb = slider.getModel().getThumbAt(index);
		Color col = (Color) thumb.getObject();
		col = ColorUtil.setAlpha(col, alpha*255/100);
		thumb.setObject(col);
		
		// update everything else
		alpha_value.setValue(alpha);
		slider.repaint();
		gradientPreview.repaint();
	    }
	    
	    
	}
    }
    
    private class AddThumbListener implements ActionListener {
	
	private JXMultiThumbSlider slider;
	
	public AddThumbListener(JXMultiThumbSlider slider) {
	    super();
	    this.slider = slider;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
	    float pos = 0.2f;
	    Color color = Color.black;
	    for (int i = 0; i < slider.getModel().getThumbCount(); i++) {
		float pos2 = slider.getModel().getThumbAt(i).getPosition();
		if (pos2 < pos) {
		    continue;
		}
		slider.getModel().insertThumb(pos, color, i);
		updateFromStop(i,pos,color);
		break;
	    }
	    
	}
    }
    
    private class DeleteThumbListener implements ActionListener {
	
	JXMultiThumbSlider slider;
	
	public DeleteThumbListener(JXMultiThumbSlider slider) {
	    super();
	    this.slider = slider;
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
	    int index = slider.getSelectedIndex();
	    if (index >= 0) {
		slider.getModel().removeThumb(index);
		updateFromStop(-1,-1,null);
	    }
	}
    }
    
    private class StopListener implements JXMultiThumbSlider.ThumbListener {
	
	private JXMultiThumbSlider slider;
	
	public StopListener(JXMultiThumbSlider slider) {
	    super();
	    this.slider = slider;
	}
	
	public void thumbMoved(int thumb, float pos) {
	    Color color = (Color)slider.getModel().getThumbAt(thumb).getObject();
	    updateFromStop(thumb,pos,color);
	    updateDeleteButtons();
	}
	
	public void thumbSelected(int thumb) {
	    if(thumb == -1) {
		updateFromStop(-1,-1,Color.black);
		return;
	    }
	    float pos = slider.getModel().getThumbAt(thumb).getPosition();
	    Color color = (Color)slider.getModel().getThumbAt(thumb).getObject();
	    updateFromStop(thumb,pos,color);
	    updateDeleteButtons();
	}
	public void mousePressed(MouseEvent e) {
	    if(e.getClickCount() > 1) {
	        selectColorForThumb();
	    }
	}
    }
    
    /**
     * This static utility method <b>cannot</b> be called from the
     * ETD, or your application will lock up. Call it from a separate
     * thread or create a new Thread with a Runnable.
     */
    public static MultipleGradientPaint showDialog(Component comp, String title, MultipleGradientPaint mgrad) {
	Component root = SwingUtilities.getRoot(comp);
	final JDialog dialog = new JDialog((JFrame)root,title,true);
	final JXGradientChooser picker = new JXGradientChooser();
	if(mgrad != null) {
	    picker.setGradient(mgrad);
	}
	dialog.add(picker);
	
	
	JPanel panel = new JPanel();
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
		dialog.setVisible(false);
	    }
	});
	JButton okay = new JButton("Okay");
	okay.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent actionEvent) {
		dialog.setVisible(false);
	    }
	});
	
	
	panel.add(cancel);
	panel.add(okay,"East");;
	dialog.add(panel,"South");
	
	dialog.pack();
	dialog.setVisible(true);
	
	return picker.getGradient();
    }
    
    public static String toString(MultipleGradientPaint paint) {
	StringBuffer buffer = new StringBuffer();
	buffer.append(paint.getClass().getName());
	Color[] colors = paint.getColors();
	float[] values = paint.getFractions();
	buffer.append("[");
	for(int i=0; i<colors.length; i++) {
	    buffer.append("#"+Integer.toHexString(colors[i].getRGB()));
	    buffer.append(":");
	    buffer.append(values[i]);
	    buffer.append(", ");
	}
	buffer.append("]");
	return buffer.toString();
    }
    
    public enum GradientStyle { Linear, Radial };

    public JXMultiThumbSlider<Color> getSlider() {
        return slider;
    }

    public void setSlider(JXMultiThumbSlider<Color> slider) {
        this.slider = slider;
    }
}



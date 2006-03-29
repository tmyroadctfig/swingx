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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.color.*;
import org.jdesktop.swingx.multislider.Thumb;

/**
 * A specialized JXPanel that allows the user to construct and choose a Gradient.
 * The returned values will be one of: GradientPaint, LinearGradientPaint, RadialGradientPaint.
 *
 * @author  jm158417
 */
public class JXGradientChooser extends JXPanel {
    public enum GradientStyle { Linear, Radial };

    /**
     * The multi-thumb slider to use for the gradient stops
     */
    private JXMultiThumbSlider<Color> slider;
    public JButton deleteThumbButton;
    public JButton addThumbButton;

    public JTextField colorField;
    public ColorSelectionButton changeColorButton;
    public JSpinner colorLocationSpinner;
    public JSpinner alphaSpinner;
    public JSlider alphaSlider;
    
    public JComboBox styleCombo;
    public GradientPreviewPanel gradientPreview;
    
    public JRadioButton noCycleRadio;
    public JRadioButton reflectedRadio;
    public JRadioButton repeatedRadio;
    public JCheckBox reversedCheck;
    
    Paint checker_texture = null;
    
    /** Creates new form GradientPicker */
    public JXGradientChooser() {
	initComponents();
    }
    
    
    public MultipleGradientPaint getGradient() {
	// get the list of colors
	List<Thumb<Color>> stops = slider.getModel().getSortedThumbs();
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
	while(slider.getModel().getThumbCount() > 0) {
	    slider.getModel().removeThumb(0);
	}
	float[] fracts = mgrad.getFractions();
	Color[] colors = mgrad.getColors();
	for(int i=0; i<fracts.length; i++) {
	    slider.getModel().addThumb(fracts[i],colors[i]);
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
	    colorLocationSpinner.setEnabled(false);
	    alphaSpinner.setEnabled(false);
	    alphaSlider.setEnabled(false);
	    colorField.setEnabled(false);
	    changeColorButton.setEnabled(false);
	    changeColorButton.setBackground(Color.black);
	    deleteThumbButton.setEnabled(false);
	} else {
	    colorLocationSpinner.setEnabled(true);
	    alphaSpinner.setEnabled(true);
	    alphaSlider.setEnabled(true);
	    colorField.setEnabled(true);
	    changeColorButton.setEnabled(true);
	    colorLocationSpinner.setValue((int)(100*position));
	    colorField.setText(Integer.toHexString(color.getRGB()).substring(2));
	    alphaSpinner.setValue((int)(color.getAlpha()*100/255));
	    alphaSlider.setValue(color.getAlpha()*100/255);
	    changeColorButton.setBackground(color);
	    deleteThumbButton.setEnabled(true);
	}
        updateDeleteButtons();
	((GradientPreviewPanel)gradientPreview).repaint();
    }
    private void updateDeleteButtons() {
	if(slider.getModel().getThumbCount() <= 2) {
	    deleteThumbButton.setEnabled(false);
	}
    }
    
    public JXMultiThumbSlider<Color> getSlider() {
        return slider;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        //create the actions and load them in the action map
        AddThumbAction addThumbAction = new AddThumbAction();
        DeleteThumbAction deleteThumbAction = new DeleteThumbAction();
        deleteThumbAction.setEnabled(false); //disabled to begin with
        ChangeColorAction changeColorAction = new ChangeColorAction();
        changeColorAction.setEnabled(false);
        //TODO Add to the action map with proper keys, etc
        ActionMap actions = getActionMap();
        actions.put("add-thumb", addThumbAction);
        actions.put("delete-thumb", deleteThumbAction);
        actions.put("change-color", changeColorAction);
        
        //configure the panel
        JXPanel topPanel = new JXPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Gradient"));

        ///////////////// Build and Configure the slider panel/////////////////
        JXPanel sliderPanel = new JXPanel(new GridBagLayout());
        slider = new JXMultiThumbSlider<Color>();
        addThumbButton = new JButton(addThumbAction);
        deleteThumbButton = new JButton(deleteThumbAction);
        
        sliderPanel.add(slider, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
        sliderPanel.add(addThumbButton, new GridBagConstraints(0, 1, 1, 1, .5, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 3), 0, 0));
        sliderPanel.add(deleteThumbButton, new GridBagConstraints(1, 1, 1, 1, .5, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0));
        
        topPanel.add(sliderPanel, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(7, 7, 12, 7), 0, 0));
        ///////////////////////////////////////////////////////////////////////
        
        //////////////// Build and Configure the top panel ////////////////////
        JLabel label = new JLabel("Color: #");
        label.setHorizontalAlignment(SwingConstants.TRAILING);
        colorField = new JTextField();
        colorField.setText("000");
        colorField.setEnabled(false);
        label.setLabelFor(colorField);
        changeColorButton = new ColorSelectionButton();
        changeColorButton.setAction(changeColorAction);
        changeColorButton.setText("");
        topPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(colorField, new GridBagConstraints(1, 1, 1, 1, .5, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(changeColorButton, new GridBagConstraints(2, 1, 1, 1, .5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        
        label = new JLabel("Location: %");
        label.setHorizontalAlignment(SwingConstants.TRAILING);
        colorLocationSpinner = new JSpinner();
        colorLocationSpinner.setEnabled(false);
        colorLocationSpinner.addChangeListener(new ChangeLocationListener());
        label.setLabelFor(colorLocationSpinner);
        topPanel.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(colorLocationSpinner, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        
        ChangeAlphaListener changeAlphaListener = new ChangeAlphaListener();
        label = new JLabel("Opacity: %");
        label.setHorizontalAlignment(SwingConstants.TRAILING);
        alphaSpinner = new JSpinner();
        alphaSpinner.setEnabled(false);
        alphaSpinner.addChangeListener(changeAlphaListener);
        label.setLabelFor(alphaSpinner);
        alphaSlider = new JSlider();
        alphaSlider.setEnabled(false);
        alphaSlider.addChangeListener(changeAlphaListener);
        topPanel.add(label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(alphaSpinner, new GridBagConstraints(1, 3, 1, 1, .5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(alphaSlider, new GridBagConstraints(2, 3, 1, 1, .5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        
        add(topPanel, BorderLayout.NORTH);
        /////////////////////////////////////////////////////////////////////
        

        JXPanel previewPanel = new JXPanel(new GridBagLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        
        RepaintOnEventListener repaintListener = new RepaintOnEventListener();
        label = new JLabel("Style:");
        styleCombo = new JComboBox();
	styleCombo.addItemListener(repaintListener);
	styleCombo.setModel(new DefaultComboBoxModel(GradientStyle.values()));
        label.setLabelFor(styleCombo);
        previewPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        previewPanel.add(styleCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        label = new JLabel("Type");
        previewPanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        ButtonGroup typeGroup = new ButtonGroup();
        noCycleRadio = new JRadioButton("None");
        noCycleRadio.setSelected(true);
        noCycleRadio.addActionListener(repaintListener);
        typeGroup.add(noCycleRadio);
        previewPanel.add(noCycleRadio, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        reflectedRadio = new JRadioButton("Reflect");
        reflectedRadio.addActionListener(repaintListener);
        typeGroup.add(reflectedRadio);
        previewPanel.add(reflectedRadio, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        repeatedRadio = new JRadioButton("Repeat");
        repeatedRadio.addActionListener(repaintListener);
        typeGroup.add(repeatedRadio);
        previewPanel.add(repeatedRadio, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        reversedCheck = new JCheckBox("Reverse");
        reversedCheck.addActionListener(repaintListener);
        previewPanel.add(reversedCheck, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        gradientPreview = new GradientPreviewPanel();
        gradientPreview.picker = this; //wow, nasty
        previewPanel.add(gradientPreview, new GridBagConstraints(2, 0, 1, 5, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        add(previewPanel);

        
        ///To still refactor below::
	SpinnerNumberModel alpha_model = new SpinnerNumberModel(100,0,100,1);
	alphaSpinner.setModel(alpha_model);
	SpinnerNumberModel location_model = new SpinnerNumberModel(100,0,100,1);
	colorLocationSpinner.setModel(location_model);
	
	checker_texture = ColorUtil.getCheckerPaint();
	
	slider.setOpaque(false);
	slider.setPreferredSize(new Dimension(100,35));
	slider.getModel().setMinimumValue(0f);
	slider.getModel().setMaximumValue(1.0f);
	
	slider.getModel().addThumb(0,Color.black);
	slider.getModel().addThumb(0.5f,Color.red);
	slider.getModel().addThumb(1.0f,Color.white);
	
	slider.setThumbRenderer(new GradientThumbRenderer(this, slider));
	slider.setTrackRenderer(new GradientTrackRenderer(this));
	slider.addMultiThumbListener(new StopListener(slider));
//	
////	gradient_selector.add(slider, "Center");
    }

    private final class ChangeLocationListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            if(slider.getSelectedIndex() >= 0) {
                Thumb thumb = slider.getModel().getThumbAt(slider.getSelectedIndex());
                thumb.setPosition((float)((Integer)colorLocationSpinner.getValue())/100);
                slider.recalc();
                slider.repaint();
                updateFromStop(thumb);
            }
        }
    }
    
    private final class ChangeAlphaListener implements ChangeListener {
	public void stateChanged(ChangeEvent changeEvent) {
            if(slider.getSelectedIndex() >= 0) {
                Thumb<Color> thumb = slider.getModel().getThumbAt(slider.getSelectedIndex());
                int alpha = changeEvent.getSource() == alphaSpinner ?
                    (Integer)alphaSpinner.getValue()
                    : alphaSlider.getValue();
                
		Color col = (Color)thumb.getObject();
                col = ColorUtil.setAlpha(col, alpha*255/100);
                thumb.setObject(col);

                if (changeEvent.getSource() == alphaSpinner) {
                    alphaSlider.setValue(alpha);
                } else {
                    alphaSpinner.setValue(alpha);
                }
		slider.repaint();
		gradientPreview.repaint();
	    }
	}
    }
    private final class ChangeColorAction extends AbstractActionExt {
	public ChangeColorAction() {
	    super("Change Color");
	}
	
	public void actionPerformed(ActionEvent actionEvent) {
	    selectColorForThumb();
	}
    }
    private final class AddThumbAction extends AbstractActionExt {
	public AddThumbAction() {
	    super("Add");
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
    private final class DeleteThumbAction extends AbstractActionExt {
	public DeleteThumbAction() {
	    super("Delete");
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
    
    private final class RepaintOnEventListener implements ActionListener, ItemListener {
        public void actionPerformed(ActionEvent e) {
            gradientPreview.repaint();
        }

        public void itemStateChanged(ItemEvent e) {
            gradientPreview.repaint();
        }
    }
    
    private void selectColorForThumb() {
	int index = slider.getSelectedIndex();
	if (index >= 0) {
	    Color color = (Color)slider.getModel().getThumbAt(index).getObject();
	    color = JColorChooser.showDialog(slider, "Select A Color", color);
	    if (color != null) {
		slider.getModel().getThumbAt(index).setObject(color);
		updateFromStop(index, slider.getModel().getThumbAt(index).getPosition(), color);
		slider.repaint();
		((GradientPreviewPanel)gradientPreview).repaint();
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
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        final JFrame frame = new JFrame("Gradient Picker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    
}



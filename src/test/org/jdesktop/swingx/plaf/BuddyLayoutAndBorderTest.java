package org.jdesktop.swingx.plaf;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jdesktop.swingx.prompt.BuddySupport;
import org.junit.Before;
import org.junit.Test;

public class BuddyLayoutAndBorderTest {
	private BuddyLayoutAndBorder blab;
	private JTextField textField;

	@Before
	public void setup() {
		textField = new JTextField();
		blab = new BuddyLayoutAndBorder();
		blab.install(textField);
	}

	@Test
	public void testBorder() throws Exception {
		Border newBorder = BorderFactory.createEmptyBorder();
		textField.setBorder(newBorder);
		assertNotSame("Border should have been wrapped.", newBorder, textField.getBorder());
	}

	@Test
	public void testUninstall() throws Exception {
		blab.uninstall();
		Border newBorder = BorderFactory.createEmptyBorder();
		textField.setBorder(newBorder);
		assertSame("Border should NOT have been wrapped.", newBorder, textField.getBorder());
	}

	@Test
	public void testPreferredWidth() throws Exception {
		JButton btn = new JButton("hey");
		int txtWidth = textField.getPreferredSize().width;
//		int btnWidth = btn.getPreferredSize().width;

		assertSame(txtWidth, blab.preferredLayoutSize(textField).width);

		BuddySupport.addLeft(btn, textField);

		assertSame(String.format("preferred layout size should be %d", txtWidth),
				txtWidth, blab.preferredLayoutSize(textField).width);

		btn.setVisible(false);
		assertSame(txtWidth, blab.preferredLayoutSize(textField).width);
	}

	@Test
	public void testBorderInsets() throws Exception {
		JButton btn = new JButton("hey");
		int left = blab.getBorderInsets(textField).left;
		int btnWidth = btn.getPreferredSize().width;

		BuddySupport.addLeft(btn, textField);
		assertSame(btn, BuddySupport.getLeft(textField).get(0));

		assertSame(left + btnWidth, blab.getBorderInsets(textField).left);

		btn.setVisible(false);
		assertSame(left, blab.getBorderInsets(textField).left);
	}
}

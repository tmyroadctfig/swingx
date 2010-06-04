package org.jdesktop.swingx.plaf;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.TextUI;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.search.NativeSearchFieldSupport;

/**
 * {@link PromptTextUI} implementation for rendering prompts on
 * {@link JTextField}s and uses a {@link JTextField} as a prompt component.
 * 
 * @author Peter Weishapl <petw@gmx.net>
 * 
 */
public class PromptTextFieldUI extends PromptTextUI {
	/**
	 * Creates a new {@link PromptTextFieldUI}.
	 * 
	 * @param delegate
	 */
	public PromptTextFieldUI(TextUI delegate) {
		super(delegate);
	}

	/**
	 * Overrides {@link #getPromptComponent(JTextComponent)} to additionally
	 * update {@link JTextField} specific properties.
	 */
    @Override
	public JTextComponent getPromptComponent(JTextComponent txt) {
		LabelField lbl = (LabelField) super.getPromptComponent(txt);
		JTextField txtField = (JTextField) txt;

		lbl.setHorizontalAlignment(txtField.getHorizontalAlignment());
		lbl.setColumns(txtField.getColumns());

		// Make search field in Leopard paint focused border.
        lbl.hasFocus = txtField.hasFocus()
                && NativeSearchFieldSupport.isNativeSearchField(txtField);

		// leopard client properties. see
		// http://developer.apple.com/technotes/tn2007/tn2196.html#JTEXTFIELD_VARIANT
        NativeSearchFieldSupport.setSearchField(lbl, NativeSearchFieldSupport
                .isSearchField(txtField));
        NativeSearchFieldSupport.setFindPopupMenu(lbl, NativeSearchFieldSupport
                .getFindPopupMenu(txtField));
		
        // here we need to copy the border again for Mac OS X, because the above
        // calls may have replaced it.
        Border b = txt.getBorder();
        
        if (b == null) {
            lbl.setBorder(txt.getBorder());
        } else {
            Insets insets = b.getBorderInsets(txt);
            lbl.setBorder(
                    createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        }
//		lbl.setBorder(txtField.getBorder());

        // buddy support: not needed, because BuddyLayoutAndBorder queries
        // original text field
        // BuddySupport.setOuterMargin(lbl,
        // BuddySupport.getOuterMargin(txtField));
		// BuddySupport.setLeft(lbl, BuddySupport.getLeft(txtField));
		// BuddySupport.setRight(lbl, BuddySupport.getRight(txtField));

		return lbl;
	}

	/**
	 * Returns a shared {@link JTextField}.
	 */
    @Override
	protected JTextComponent createPromptComponent() {
	    return new LabelField();
	}

	private static final class LabelField extends JTextField {
		boolean hasFocus;

		@Override
		public boolean hasFocus() {
			return hasFocus;
		}
	}
}

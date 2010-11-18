/*
 * Created on 18.11.2010
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HighlightPredicateIssues extends HighlightPredicateTest {

    
    /**
     * Issue #??-swingx: must respect insets
     */
    @Test
    public void testIsTextTruncatedRespectsBorder() {
        allColored.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Insets insets = allColored.getBorder().getBorderInsets(allColored);
        Dimension preferredSize = allColored.getPreferredSize();
        preferredSize.width -= insets.left + insets.right;
        preferredSize.height -= insets.top + insets.bottom;
        allColored.setSize(preferredSize);
        ComponentAdapter adapter = createComponentAdapter(allColored, true, true);
        assertTrue(HighlightPredicate.IS_TEXT_TRUNCATED.isHighlighted(allColored, adapter));
    }

    @Test
    public void testDummy() {
        // keep runner happy if we have solved all issues
    }
}

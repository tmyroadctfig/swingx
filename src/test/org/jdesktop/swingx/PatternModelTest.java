/*
 * Created on 18.08.2005
 *
 */
package org.jdesktop.swingx;

import junit.framework.TestCase;

public class PatternModelTest extends TestCase {

    final static String startAnchor = "^";
    final static String endAnchor = "$";
    final static String middleStartAnchor = "some" + startAnchor + "one";
    final static String middleEndAnchor = "some" + endAnchor + "one";
    
    /**
     * test if rawtext is treated as literal.
     *
     */
    public void testRawMiddleAnchorTokens() {
        PatternModel patternModel = new PatternModel();
        patternModel.setRawText(startAnchor);
        String literalAnchor = "some" + startAnchor +"one";
        assertTrue("must find literal containing startAnchor " + literalAnchor, patternModel.getPattern().matcher(literalAnchor).find());
        String literal = "someone";
        assertFalse("must reject literal not containing startAnchor " + literal, 
                patternModel.getPattern().matcher(literal).find());
    }
    
    /**
     * test if rawtext is treated as literal.
     *
     */
    public void testRawStartAnchor() {
        PatternModel patternModel = new PatternModel();
        String anchored = startAnchor + "hap";
        patternModel.setRawText(anchored);
        String literalAnchor = startAnchor + "happy";
        assertTrue("must find literal containing startAnchor " + literalAnchor, patternModel.getPattern().matcher(literalAnchor).find());
        String literal = "happy";
        assertFalse("must reject literal not containing startAnchor " + literal, 
                patternModel.getPattern().matcher(literal).find());
    }
    
   
}

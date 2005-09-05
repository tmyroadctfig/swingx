/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.util.List;

import junit.framework.TestCase;

import org.jdesktop.swingx.util.PropertyChangeReport;

/**
 * Testing PatternModel.
 * 
 * @author Jeanette Winzenburg
 */
public class PatternModelTest extends TestCase {

    final static String startAnchor = "^";
    final static String endAnchor = "$";
    final static String middleStartAnchor = "some" + startAnchor + "one";
    final static String middleEndAnchor = "some" + endAnchor + "one";
    private PropertyChangeReport propertyReport;
 
    public void testCaseSensitive() {
        PatternModel model = new PatternModel();
        model.setRawText("tab");
        assertTrue("must find not case sensitive by default", 
                model.getPattern().matcher("JTABLE").find());
        model.addPropertyChangeListener(propertyReport);
        model.setCaseSensitive(true);
        assertTrue("changing case sensitive must fire casesensitive property", 
                propertyReport.hasEvents("caseSensitive"));
        assertTrue("changing case sensitive must fire pattern property", 
                propertyReport.hasEvents("pattern"));
        
    }
    public void testAvailableMatchRules() {
        PatternModel model = new PatternModel();
        List rules = model.getMatchRules();
        assertNotNull("rules must not be null", rules);
    }
    
    public void testRegexCreator() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        model.setRegexCreatorKey(PatternModel.REGEX_UNCHANGED);
        assertEquals("search string mode must be", PatternModel.REGEX_UNCHANGED, model.getRegexCreatorKey());
        assertTrue(propertyReport.hasEvents("regexCreatorKey"));
        
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_ANCHORED);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_WILDCARD);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_EXPLICIT);
    }
    
    public void testMatchRule() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        // default searchStringMode
        assertEquals("search string mode must be", 
                PatternModel.REGEX_MATCH_RULES, model.getRegexCreatorKey());
        // default searchCategory
        assertEquals("search category must be ", 
                PatternModel.MATCH_RULE_CONTAINS, model.getMatchRule());
        // change category and test if property change is fired
        model.setMatchRule(PatternModel.MATCH_RULE_EQUALS);
        assertTrue("model must have fired " + "matchRule ", propertyReport.hasEvents("matchRule"));
    }
    
    public void testChangeMatchRule() {
        PatternModel model = new PatternModel();
        String contained = "t";
        model.setRawText(contained);
        String match = "x" + contained + "x";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.addPropertyChangeListener(propertyReport);
        model.setMatchRule(PatternModel.MATCH_RULE_EQUALS);
        assertTrue("model must have fire pattern change", propertyReport.hasEvents("pattern"));
        assertFalse("pattern must reject " + match, model.getPattern().matcher(match).find());
        model.setMatchRule(PatternModel.MATCH_RULE_STARTSWITH);
        match = "txx";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.setMatchRule(PatternModel.MATCH_RULE_ENDSWITH);
        match = "xxt";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
    }
    
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

    @Override
    protected void setUp()  {
        propertyReport = new PropertyChangeReport();
    }
    
    
}

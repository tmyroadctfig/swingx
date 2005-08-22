/*
 * Created on 18.08.2005
 *
 */
package org.jdesktop.swingx;

import org.jdesktop.swingx.util.PropertyChangeReport;

import junit.framework.TestCase;

public class PatternModelTest extends TestCase {

    final static String startAnchor = "^";
    final static String endAnchor = "$";
    final static String middleStartAnchor = "some" + startAnchor + "one";
    final static String middleEndAnchor = "some" + endAnchor + "one";
    private PropertyChangeReport propertyReport;
 
    public void testSetSearchStringMode() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        model.setSearchStringMode(PatternModel.SEARCH_STRING_REGEX);
        assertEquals("search string mode must be", PatternModel.SEARCH_STRING_REGEX, model.getSearchStringMode());
        assertTrue(propertyReport.hasEvents("searchStringMode"));
        
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_ANCHORED);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_WILDCARD);
//        model.setSearchStringMode(PatternModel.SEARCH_STRING_EXPLICIT);
    }
    
    public void testSearchCategory() {
        PatternModel model = new PatternModel();
        model.addPropertyChangeListener(propertyReport);
        // default searchStringMode
        assertEquals("search string mode must be", 
                PatternModel.SEARCH_STRING_EXPLICIT, model.getSearchStringMode());
        // default searchCategory
        assertEquals("search category must be ", 
                PatternModel.SEARCH_CATEGORY_CONTAINS, model.getSearchCategory());
        // change category and test if property change is fired
        model.setSearchCategory(PatternModel.SEARCH_CATEGORY_EQUALS);
        assertTrue("model must have fired " + "searchCategory ", propertyReport.hasEvents("searchCategory"));
    }
    
    public void testChangeSearchCategory() {
        PatternModel model = new PatternModel();
        String contained = "t";
        model.setRawText(contained);
        String match = "x" + contained + "x";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.addPropertyChangeListener(propertyReport);
        model.setSearchCategory(PatternModel.SEARCH_CATEGORY_EQUALS);
        assertTrue("model must have fire pattern change", propertyReport.hasEvents("pattern"));
        assertFalse("pattern must reject " + match, model.getPattern().matcher(match).find());
        model.setSearchCategory(PatternModel.SEARCH_CATEGORY_STARTSWITH);
        match = "txx";
        assertTrue("pattern must find " + match, model.getPattern().matcher(match).find());
        model.setSearchCategory(PatternModel.SEARCH_CATEGORY_ENDSWITH);
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

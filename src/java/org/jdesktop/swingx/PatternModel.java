/*
 * Created on 10.06.2005
 *
 */
package org.jdesktop.swingx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Holds a compiled regular expression.
 * <p>
 * 
 * JW: Work-in-progress - Anchors will be factored into AnchoredSearchMode 
 * <b>Anchors</b> By default, the scope of the pattern relative to strings
 * being tested are unanchored, ie, the pattern will match any part of the
 * tested string. Traditionally, special characters ('^' and '$') are used to
 * describe patterns that match the beginning (or end) of a string. If those
 * characters are included in the pattern, the regular expression will honor
 * them. However, for ease of use, two properties are included in this model
 * that will determine how the pattern will be evaluated when these characters
 * are omitted.
 * <p>
 * The <b>StartAnchored</b> property determines if the pattern must match from
 * the beginning of tested strings, or if the pattern can appear anywhere in the
 * tested string. Likewise, the <b>EndAnchored</b> property determines if the
 * pattern must match to the end of the tested string, or if the end of the
 * pattern can appear anywhere in the tested string. The default values (false
 * in both cases) correspond to the common database 'LIKE' operation, where the
 * pattern is considered to be a match if any part of the tested string matches
 * the pattern.
 * 
 * @author Jeanette Winzenburg
 * @author David Hall
 */
public class PatternModel {

    /**
     * The prefix marker to find component related properties in the
     * resourcebundle.
     */
    public static final String SEARCH_PREFIX = "Search.";

    public static final String REGEX_UNCHANGED = "regex";

    public static final String REGEX_ANCHORED = "anchored";

    public static final String REGEX_WILDCARD = "wildcard";

    public static final String REGEX_MATCH_RULES = "explicit";

    public static final String MATCH_RULE_CONTAINS = "contains";

    public static final String MATCH_RULE_EQUALS = "equals";

    public static final String MATCH_RULE_ENDSWITH = "endsWith";

    public static final String MATCH_RULE_STARTSWITH = "startsWith";

    private String rawText;

    private boolean backwards;

    private Pattern pattern;

    private int foundIndex;

    private boolean caseSensitive;

//    private boolean startAnchored;
//
//    private boolean endAnchored;

    // private boolean enabled;
    //
    // private boolean active;
    // private boolean highlight;

    private PropertyChangeSupport propertySupport;

    private String regexCreatorKey;

    private RegexCreator regexCreator;

    private boolean wrapping;


//---------------------- misc. properties not directly related to Pattern.
    
    public int getFoundIndex() {
        return foundIndex;
    }

    public void setFoundIndex(int foundIndex) {
        int old = getFoundIndex();
        this.foundIndex = foundIndex;
        firePropertyChange("foundIndex", old, getFoundIndex());
    }
    
    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        boolean old = isBackwards();
        this.backwards = backwards;
        firePropertyChange("backwards", old, isBackwards());
    }

    public boolean isWrapping() {
        return wrapping;
    }
    
    public void setWrapping(boolean wrapping) {
        boolean old = isWrapping();
        this.wrapping = wrapping;
        firePropertyChange("wrapping", old, isWrapping());
    }
    
    // public boolean isActive() {
    // return active;
    // }
    //
    // public void setActive(boolean active) {
    // if (!isEnabled())
    // return;
    // boolean old = isActive();
    // this.active = active;
    // firePropertyChange("active", old, isActive());
    // }
    //
    // public boolean isEnabled() {
    // return enabled;
    // }
    //
    // public void setEnabled(boolean enabled) {
    // boolean old = isEnabled();
    // this.enabled = enabled;
    // firePropertyChange("enabled", old, isEnabled());
    // }
    // public boolean isHighlight() {
    // return highlight;
    // }
    //
    // public void setHighlight(boolean highlight) {
    // boolean old = isHighlight();
    // this.highlight = highlight;
    // firePropertyChange("highlight", old, isHighlight());
    // }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        boolean old = isCaseSensitive();
        this.caseSensitive = caseSensitive;
        updatePattern(caseSensitive);
        firePropertyChange("caseSensitive", old, isCaseSensitive());
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String findText) {
        String old = getRawText();
        this.rawText = findText;
        updatePattern(createRegEx(findText));
        firePropertyChange("rawText", old, getRawText());
    }

    /**
     * returns a regEx for compilation into a pattern. Here: either a "contains"
     * (== partial find) or null if the input was empty.
     * 
     * @param searchString
     * @return null if the input was empty, or a regex according to the internal
     *         rules
     */
    private String createRegEx(String searchString) {
        if (isEmpty(searchString))
            return ".*";
        return getRegexCreator().createRegEx(searchString);
    }

    /**
     * 
     * @param s
     * @return
     */

    private boolean isEmpty(String text) {
        return (text == null) || (text.length() == 0);
    }

    private void updatePattern(String regEx) {
        Pattern old = getPattern();
        if (isEmpty(regEx)) {
            pattern = null;
        } else if ((old == null) || (!old.pattern().equals(regEx))) {
            pattern = Pattern.compile(regEx, getFlags());
        }
        firePropertyChange("pattern", old, getPattern());
    }

    private int getFlags() {
        return isCaseSensitive() ? 0 : getCaseInsensitiveFlag();
    }

    private int getCaseInsensitiveFlag() {
        return Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    }

    private void updatePattern(boolean caseSensitive) {
        if (pattern == null)
            return;
        Pattern old = getPattern();
        int flags = old.flags();
        int flag = getCaseInsensitiveFlag();
        if ((caseSensitive) && ((flags & flag) != 0)) {
            pattern = Pattern.compile(pattern.pattern(), 0);
        } else if (!caseSensitive && ((flags & flag) == 0)) {
            pattern = Pattern.compile(pattern.pattern(), flag);
        }
        firePropertyChange("pattern", old, getPattern());
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (propertySupport == null) {
            propertySupport = new PropertyChangeSupport(this);
        }
        propertySupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertySupport == null)
            return;
        propertySupport.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String name, Object oldValue,
            Object newValue) {
        if (propertySupport == null)
            return;
        propertySupport.firePropertyChange(name, oldValue, newValue);
    }

    /**
     * Responsible for converting a "raw text" into a valid 
     * regular expression in the context of a set of rules.
     * 
     */
    public static class RegexCreator {
        protected String matchRule;
        private List rules;

        public String getMatchRule() {
            if (matchRule == null) {
                matchRule = getDefaultMatchRule();
            }
            return matchRule;
        }

        public boolean isAutoDetect() {
            return false;
        }
        
        public String createRegEx(String searchString) {
            if (MATCH_RULE_CONTAINS.equals(getMatchRule())) {
                return createContainedRegEx(searchString);
            }
            if (MATCH_RULE_EQUALS.equals(getMatchRule())) {
                return createEqualsRegEx(searchString);
            }
            if (MATCH_RULE_STARTSWITH.equals(getMatchRule())){
                return createStartsAnchoredRegEx(searchString);
            }
            if (MATCH_RULE_ENDSWITH.equals(getMatchRule())) {
                return createEndAnchoredRegEx(searchString);
            }
            return searchString;
        }

        protected String createEndAnchoredRegEx(String searchString) {
            return Pattern.quote(searchString) + "$";
        }

        protected String createStartsAnchoredRegEx(String searchString) {
            return "^" + Pattern.quote(searchString);
        }

        protected String createEqualsRegEx(String searchString) {
            return "^" + Pattern.quote(searchString) + "$";
        }

        protected String createContainedRegEx(String searchString) {
            return Pattern.quote(searchString);
        }

        public void setMatchRule(String category) {
            this.matchRule = category;
        }
        
        protected String getDefaultMatchRule() {
            return MATCH_RULE_CONTAINS;
        }

        public List getMatchRules() {
            if (rules == null) {
                rules = createAndInitRules();
            }
            return rules;
        }

        private List createAndInitRules() {
            if (!supportsRules()) return Collections.EMPTY_LIST;
            List<String> list = new ArrayList<String>();
            list.add(MATCH_RULE_CONTAINS);
            list.add(MATCH_RULE_EQUALS);
            list.add(MATCH_RULE_STARTSWITH);
            list.add(MATCH_RULE_ENDSWITH);
            return list;
        }

        private boolean supportsRules() {
            return true;
        }
    }

 
    /**
     * Support for anchored input.
     * 
     * PENDING: NOT TESTED - simply moved!
     * Need to define requirements...
     * 
     */
    public static class AnchoredSearchMode extends RegexCreator {
        
        public boolean isAutoDetect() {
            return true;
        }
        
        public String createRegEx(String searchExp) {
          if (isAutoDetect()) {
              StringBuffer buf = new StringBuffer(searchExp.length() + 4);
              if (!hasStartAnchor(searchExp)) {
                  if (isStartAnchored()) {
                      buf.append("^");
                  } 
              }
      
              //PENDING: doesn't escape contained regex metacharacters...
              buf.append(searchExp);
      
              if (!hasEndAnchor(searchExp)) {
                  if (isEndAnchored()) {
                      buf.append("$");
                  } 
              }
      
              return buf.toString();
          }
          return super.createRegEx(searchExp);
        }

        private boolean hasStartAnchor(String str) {
            return str.startsWith("^");
        }

        private boolean hasEndAnchor(String str) {
            int len = str.length();
            if ((str.charAt(len - 1)) != '$')
                return false;

            // the string "$" is anchored
            if (len == 1)
                return true;

            // scan backwards along the string: if there's an odd number
            // of backslashes, then the last escapes the dollar and the
            // pattern is not anchored. if there's an even number, then
            // the dollar is unescaped and the pattern is anchored.
            for (int n = len - 2; n >= 0; --n)
                if (str.charAt(n) != '\\')
                    return (len - n) % 2 == 0;

            // The string is of the form "\+$". If the length is an odd
            // number (ie, an even number of '\' and a '$') the pattern is
            // anchored
            return len % 2 != 0;
        }


//      /**
//      * returns true if the pattern must match from the beginning of the string,
//      * or false if the pattern can match anywhere in a string.
//      */
     public boolean isStartAnchored() {
         return MATCH_RULE_EQUALS.equals(getMatchRule()) ||
             MATCH_RULE_STARTSWITH.equals(getMatchRule());
     }
 //
//     /**
//      * sets the default interpretation of the pattern for strings it will later
//      * be given. Setting this value to true will force the pattern to match from
//      * the beginning of tested strings. Setting this value to false will allow
//      * the pattern to match any part of a tested string.
//      */
//     public void setStartAnchored(boolean startAnchored) {
//         boolean old = isStartAnchored();
//         this.startAnchored = startAnchored;
//         updatePattern(createRegEx(getRawText()));
//         firePropertyChange("startAnchored", old, isStartAnchored());
//     }
 //
     /**
      * returns true if the pattern must match from the beginning of the string,
      * or false if the pattern can match anywhere in a string.
      */
     public boolean isEndAnchored() {
         return MATCH_RULE_EQUALS.equals(getMatchRule()) ||
             MATCH_RULE_ENDSWITH.equals(getMatchRule());
     }
 //
//     /**
//      * sets the default interpretation of the pattern for strings it will later
//      * be given. Setting this value to true will force the pattern to match the
//      * end of tested strings. Setting this value to false will allow the pattern
//      * to match any part of a tested string.
//      */
//     public void setEndAnchored(boolean endAnchored) {
//         boolean old = isEndAnchored();
//         this.endAnchored = endAnchored;
//         updatePattern(createRegEx(getRawText()));
//         firePropertyChange("endAnchored", old, isEndAnchored());
//     }
 //
//     public boolean isStartEndAnchored() {
//         return isEndAnchored() && isStartAnchored();
//     }
//     
//     /**
//      * sets the default interpretation of the pattern for strings it will later
//      * be given. Setting this value to true will force the pattern to match the
//      * end of tested strings. Setting this value to false will allow the pattern
//      * to match any part of a tested string.
//      */
//     public void setStartEndAnchored(boolean endAnchored) {
//         boolean old = isStartEndAnchored();
//         this.endAnchored = endAnchored;
//         this.startAnchored = endAnchored;
//         updatePattern(createRegEx(getRawText()));
//         firePropertyChange("StartEndAnchored", old, isStartEndAnchored());
//     }
    }
    /**
     * 
     * @param mode
     */
    public void setRegexCreatorKey(String mode) {
        if (getRegexCreatorKey().equals(mode)) return;
        String old = getRegexCreatorKey();
        regexCreatorKey = mode;
        firePropertyChange("regexCreatorKey", old, getRegexCreatorKey());
        
    }

    public String getRegexCreatorKey() {
        if (regexCreatorKey == null) {
            regexCreatorKey = getDefaultRegexCreatorKey();
        }
        return regexCreatorKey;
    }

    private String getDefaultRegexCreatorKey() {
        return REGEX_MATCH_RULES;
    }

    public void setMatchRule(String category) {
        if (getMatchRule().equals(category)) {
            return;
        }
        String old = getMatchRule();
        getRegexCreator().setMatchRule(category);
        updatePattern(createRegEx(getRawText()));
        firePropertyChange("matchRule", old, getMatchRule());
        
    }

    public String getMatchRule() {
        return getRegexCreator().getMatchRule();
    }

    private RegexCreator getRegexCreator() {
        if (regexCreator == null) {
            regexCreator = new RegexCreator();
        }
        return regexCreator;
    }

    public List getMatchRules() {
        return getRegexCreator().getMatchRules();
    }


    
}

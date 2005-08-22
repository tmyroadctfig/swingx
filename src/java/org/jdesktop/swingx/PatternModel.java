/*
 * Created on 10.06.2005
 *
 */
package org.jdesktop.swingx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

    public static final String SEARCH_STRING_REGEX = "regex";

    public static final String SEARCH_STRING_ANCHORED = "anchored";

    public static final String SEARCH_STRING_WILDCARD = "wildcard";

    public static final String SEARCH_STRING_EXPLICIT = "explicit";

    public static final String SEARCH_CATEGORY_CONTAINS = "contains";

    public static final String SEARCH_CATEGORY_EQUALS = "equals";

    public static final String SEARCH_CATEGORY_ENDSWITH = "endsWith";

    public static final String SEARCH_CATEGORY_STARTSWITH = "startsWith";

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

    private String searchStringMode;

    private SearchMode searchMode;


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

    public int getFoundIndex() {
        return foundIndex;
    }

    public void setFoundIndex(int foundIndex) {
        int old = getFoundIndex();
        this.foundIndex = foundIndex;
        firePropertyChange("foundIndex", old, getFoundIndex());
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
        return getSearchMode().createRegEx(searchString);
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

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        boolean old = isBackwards();
        this.backwards = backwards;
        firePropertyChange("backwards", old, isBackwards());
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

    public class SearchMode {
        private String searchCategory;

        public String getSearchCategory() {
            if (searchCategory == null) {
                searchCategory = getDefaultSearchCategory();
            }
            return searchCategory;
        }

        public boolean isAutoDetect() {
            return false;
        }
        
        public String createRegEx(String searchString) {
            if (SEARCH_CATEGORY_CONTAINS.equals(getSearchCategory())) {
                return createContainedRegEx(searchString);
            }
            if (SEARCH_CATEGORY_EQUALS.equals(getSearchCategory())) {
                return createEqualsRegEx(searchString);
            }
            if (SEARCH_CATEGORY_STARTSWITH.equals(getSearchCategory())){
                return createStartsAnchoredRegEx(searchString);
            }
            if (SEARCH_CATEGORY_ENDSWITH.equals(getSearchCategory())) {
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

        public void setSearchCategory(String category) {
            this.searchCategory = category;
        }
        
        protected String getDefaultSearchCategory() {
            return SEARCH_CATEGORY_CONTAINS;
        }
    }

 
    public class AnchoredSearchMode extends SearchMode {
        
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
      
              buf.append(createContainedRegEx(searchExp));
      
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
         return SEARCH_CATEGORY_STARTSWITH.equals(getSearchCategory());
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
         return SEARCH_CATEGORY_ENDSWITH.equals(getSearchCategory());
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
    public void setSearchStringMode(String mode) {
        if (getSearchStringMode().equals(mode)) return;
        String old = getSearchStringMode();
        searchStringMode = mode;
        firePropertyChange("searchStringMode", old, getSearchStringMode());
        
    }

    public String getSearchStringMode() {
        if (searchStringMode == null) {
            searchStringMode = getDefaultSearchStringMode();
        }
        return searchStringMode;
    }

    private String getDefaultSearchStringMode() {
        return SEARCH_STRING_EXPLICIT;
    }

    public void setSearchCategory(String category) {
        if (getSearchCategory().equals(category)) {
            return;
        }
        String old = getSearchCategory();
        getSearchMode().setSearchCategory(category);
        updatePattern(createRegEx(getRawText()));
        firePropertyChange("searchCategory", old, getSearchCategory());
        
    }

    public String getSearchCategory() {
        return getSearchMode().getSearchCategory();
    }

    private SearchMode getSearchMode() {
        if (searchMode == null) {
            searchMode = new SearchMode();
        }
        return searchMode;
    }


    
}

/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.util.regex.Pattern;

/**
 * Searchable
 *
 * @author Ramesh Gupta
 */
public interface Searchable {

    /**
     * Search from the beginning of a document
     */
    public int search(String searchString);
    public int search(String searchString, int startIndex);
    public int search(Pattern pattern);

    /**
     * Search for the pattern from the start index.
     * @param pattern Pattern for search
     * @param startIndex starting index of search. If -1 then start from the beginning
     * @return index of matched pattern. -1 if a match cannot be found.
     */
    public int search(Pattern pattern, int startIndex);

    /**
     * Search for the pattern from the start index.
     * @param pattern Pattern for search
     * @param startIndex starting index of search. If -1 then start from the beginning
     * @param backward indicates the direction if true then search is backwards
     * @return index of matched pattern. -1 if a match cannot be found.
     */
    public int search(Pattern pattern, int startIndex, boolean backward);
}

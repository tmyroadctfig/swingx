/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.decorator;

import java.util.regex.Pattern;

/**
 * Implemented by classes that work with {@link java.util.regex.Pattern} objects.

 * @author Ramesh Gupta
 */
public interface PatternMatcher {
    public Pattern getPattern();
    public void setPattern(Pattern pattern);
}
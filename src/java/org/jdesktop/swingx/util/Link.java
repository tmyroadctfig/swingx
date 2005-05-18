/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An object which represents an URL link in a table cell, tree cell or list
 * item.
 * 
 * @author Mark Davidson
 */
public class Link implements Comparable {

    private String text; // display text

    private URL url; // url of the link

    private String target; // url target frame

    private boolean visited = false;

    public Link(String text, String target, URL url) {
        setText(text);
        setTarget(target);
        setURL(url);
    }

    /**
     * @param text
     *            text to that a renderer would display
     * @param target
     *            the target that a URL should load into.
     * @param template
     *            a string that represents a URL with
     * @{N} place holders for string substitution
     * @param args
     *            an array of strings which will be used for substitition
     */
    public Link(String text, String target, String template, String[] args) {
        setText(text);
        setTarget(target);
        setURL(createURL(template, args));
    }

    /**
     * Set the display text.
     */
    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        if (text != null) {
            return text;
        } else {
            return getURL().toString();
        }
    }

    /**
     * Set the url.
     */
    public void setURL(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("URL for link cannot be null");
        }
        this.url = url;
    }

    public URL getURL() {
        return url;
    }

    /**
     * Create a URL from a template string that has place holders and an array
     * of strings which will be substituted into the place holders. The place
     * holders are represented as
     * 
     * @{N} where N = { 1..n }
     *      <p>
     *      For example, if the template contains a string like:
     *      http://bugz.sfbay/cgi-bin/showbug?cat=@{1}&sub_cat=@{2} and a two
     *      arg array contains: java, classes_swing The resulting URL will be:
     *      http://bugz.sfbay/cgi-bin/showbug?cat=java&sub_cat=classes_swing
     *      <p>
     * @param template
     *            a url string that contains the placeholders
     * @param args
     *            an array of strings that will be substituted
     */
    private URL createURL(String template, String[] args) {
        URL url = null;
        try {
            String urlStr = template;
            for (int i = 0; i < args.length; i++) {
                urlStr = urlStr.replaceAll("@\\{" + (i + 1) + "\\}", args[i]);
            }
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            //
        }
        return url;
    }

    /**
     * Set the target that the URL should load into. This can be a uri
     * representing another control or the name of a window or special targets.
     * See: http://www.w3c.org/TR/html401/present/frames.html#adef-target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Return the target for the URL.
     * 
     * @return value of the target. If null then "_blank" will be returned.
     */
    public String getTarget() {
        if (target != null) {
            return target;
        } else {
            return "_blank";
        }
    }

    /**
     * Sets a flag to indicate if the link has been visited. The state of this
     * flag can be used to render the color of the link.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getVisited() {
        return visited;
    }

    // Comparable interface for sorting.
    public int compareTo(Object obj) {
        if (obj == null) {
            return 1;
        }
        if (obj == this) {
            return 0;
        }
        return text.compareTo(((Link) obj).text);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof Link) {
            Link other = (Link) obj;
            if (!getText().equals(other.getText())) {
                return false;
            }

            if (!getTarget().equals(other.getTarget())) {
                return false;
            }

            if (!getURL().equals(other.getURL())) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = 7;

        result = 37 * result + ((getText() == null) ? 0 : getText().hashCode());
        result = 37 * result
                + ((getTarget() == null) ? 1 : getTarget().hashCode());
        result = 37 * result + ((getURL() == null) ? 2 : getURL().hashCode());

        return result;
    }

    public String toString() {

        StringBuffer buffer = new StringBuffer("[");
        // RG: Fix for J2SE 5.0; Can't cascade append() calls because
        // return type in StringBuffer and AbstractStringBuilder are different
        buffer.append("url=");
        buffer.append(url);
        buffer.append(", target=");
        buffer.append(target);
        buffer.append(", text=");
        buffer.append(text);
        buffer.append("]");

        return buffer.toString();
    }
}

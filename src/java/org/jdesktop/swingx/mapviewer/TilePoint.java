/*
 * TilePoint.java
 *
 * Created on April 1, 2006, 12:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

/**
 *
 * @author rbair
 */
public class TilePoint {
    private int x;
    private int y;
    
    /** Creates a new instance of TilePoint */
    public TilePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public TilePoint(TilePoint tilePoint, int x, int y) {
        this.x = tilePoint.getX() + x;
        this.y = tilePoint.getY() + y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof TilePoint) {
            TilePoint t = (TilePoint)obj;
            return t.x == x && t.y == y;
        }
        return false;
    }
    
    public int hashCode() {
        return ((Integer)x).hashCode() + ((Integer)y).hashCode();
    }
}

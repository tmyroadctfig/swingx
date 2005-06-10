/*
 * Created on 10.06.2005
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Point;

import org.jdesktop.swingx.RolloverProducer;

/**
 * @author Jeanette Winzenburg
 */
public class RolloverHighlighter extends ConditionalHighlighter {

    public RolloverHighlighter(Color cellBackground, Color cellForeground) {
        super(cellBackground, cellForeground, -1, -1);
    }

    protected boolean test(ComponentAdapter adapter) {
        Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
        return p != null &&  p.y == adapter.row;
    }

}

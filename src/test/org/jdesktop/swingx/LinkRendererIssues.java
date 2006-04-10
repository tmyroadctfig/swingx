/*
 * Created on 29.03.2006
 *
 */
package org.jdesktop.swingx;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.action.LinkModelAction;

public class LinkRendererIssues extends LinkRendererTest {

    /**
     * renderers must cope with type mismatch.
     *
     */
    public void testMixedValueTypes() {
        LinkModelAction action = new LinkModelAction(new EditorPaneLinkVisitor());
        LinkRenderer renderer = new LinkRenderer(action, LinkModel.class);
        renderer.getTableCellRendererComponent(null, "stringonly", false, false, -1, -1);

    }

    /**
     * sanity: can cope with subclasses.
     * a side-effect: renderers should cope with null table.
     */
    public void testSubclassedValueTypes() {
        LinkModelAction action = new LinkModelAction(new EditorPaneLinkVisitor());
        LinkRenderer renderer = new LinkRenderer(action, LinkModel.class);
        LinkModel link = new LinkModel() {
            
        };
        renderer.getTableCellRendererComponent(null, link, false, false, -1, -1);

    }
}

/*
 * $Id$
 *
 * Copyright 2009 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx.hyperlink;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * A implementation wrapping <code>Desktop</code> actions BROWSE and MAIL, that is
 * URI-related. 
 * 
 * @author Jeanette Winzenburg
 */
public class HyperlinkAction extends AbstractHyperlinkAction<URI> {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(HyperlinkAction.class
            .getName());
    
    private Action desktopAction;
    private URIVisitor visitor;
    
    /**
     * Factory method to create and return a HyperlinkAction for the given uri. Tries
     * to guess the appropriate type from the uri. If uri is not null and has a
     * scheme of mailto, create one of type Mail. In all other cases, creates one
     * for BROWSE.
     * 
     * @param uri to uri to create a HyperlinkAction for, maybe null.
     * @return a HyperlinkAction for the given URI.
     * @throws HeadlessException if {@link
     * GraphicsEnvironment#isHeadless()} returns {@code true}
     * @throws UnsupportedOperationException if the current platform doesn't support
     *   Desktop
     */
    public static HyperlinkAction createHyperlinkAction(URI uri) {
        Action type = isMailURI(uri) ? Action.MAIL : Action.BROWSE;
        return createHyperlinkAction(uri, type);
    }
    
    /**
     * Creates and returns a HyperlinkAction with the given target and action type.
     * @param uri the target uri, maybe null.
     * @param desktopAction the type of desktop action this class should perform, must be
     *    BROWSE or MAIL
     * @return a HyperlinkAction
     * @throws HeadlessException if {@link
     * GraphicsEnvironment#isHeadless()} returns {@code true}
     * @throws UnsupportedOperationException if the current platform doesn't support
     *   Desktop
     * @throws IllegalArgumentException if unsupported action type 
     */
    public static HyperlinkAction createHyperlinkAction(URI uri, Action type) {
        return new HyperlinkAction(uri, type);
    }

    /**
     * @param uri
     * @return
     */
    private static boolean isMailURI(URI uri) {
        return uri != null && "mailto".equalsIgnoreCase(uri.getScheme());
    }

    /** 
     * Instantiates a HyperlinkAction with action type BROWSE.
     * 
     * @throws HeadlessException if {@link
     * GraphicsEnvironment#isHeadless()} returns {@code true}
     * @throws UnsupportedOperationException if the current platform doesn't support
     *   Desktop
     * @throws IllegalArgumentException if unsupported action type 
     */
    public HyperlinkAction() {
        this(Action.BROWSE);
    }
    
    /**
     * Instantiates a HyperlinkAction with the given action type.
     * 
     * @param desktopAction the type of desktop action this class should perform, must be
     *    BROWSE or MAIL
     * @throws HeadlessException if {@link
     * GraphicsEnvironment#isHeadless()} returns {@code true}
     * @throws UnsupportedOperationException if the current platform doesn't support
     *   Desktop
     * @throws IllegalArgumentException if unsupported action type 
     */ 
    public HyperlinkAction(Action desktopAction) {
        this(null, desktopAction);
    }

    /**
     * 
     * @param uri the target uri, maybe null.
     * @param desktopAction the type of desktop action this class should perform, must be
     *    BROWSE or MAIL
     * @throws HeadlessException if {@link
     * GraphicsEnvironment#isHeadless()} returns {@code true}
     * @throws UnsupportedOperationException if the current platform doesn't support
     *   Desktop
     * @throws IllegalArgumentException if unsupported action type 
     */
    public HyperlinkAction(URI uri, Action desktopAction) {
        super();
        if (!Desktop.isDesktopSupported()) {
            throw new UnsupportedOperationException("Desktop API is not " +
                                                    "supported on the current platform");
        }
        if (desktopAction != Desktop.Action.BROWSE && desktopAction != Desktop.Action.MAIL) {
           throw new IllegalArgumentException("Illegal action type: " + desktopAction + 
                   ". Must be BROWSE or MAIL");
        }
        this.desktopAction = desktopAction;
        getURIVisitor();
        setTarget(uri);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        getURIVisitor().visit(getTarget());
    }
    
    /**
     * @return
     */
    public Action getDesktopAction() {
        return desktopAction;
    }
    
    
    @Override
    protected void installTarget() {
        // doohh ... this is called from super's constructor before we are 
        // fully initialized
        if (visitor == null) return;
        super.installTarget();
        updateEnabled();
    }
    
    /**
     * 
     */
    private void updateEnabled() {
        setEnabled(getURIVisitor().isEnabled(getTarget()));
    }

    /**
     * @return
     */
    private URIVisitor getURIVisitor() {
        if (visitor == null) {
            visitor = createURIVisitor();
        }
        return visitor;
    }

    /**
     * @return
     */
    private URIVisitor createURIVisitor() {
        return getDesktopAction() == Action.BROWSE ?
                new BrowseVisitor() : new MailVisitor();
    }

    private abstract class URIVisitor {
        protected boolean desktopSupported = Desktop.isDesktopSupported();
        
        public boolean isEnabled(URI uri) {
            return desktopSupported && isActionSupported();
        }
        

        public void visit(URI uri) {
            if (!isEnabled(uri)) return;
            try {
                doVisit(uri);
            } catch (IOException e) {
                LOG.fine("cant visit Desktop " + e);
            }
        }
        
        /**
         * @return
         */
        protected abstract boolean isActionSupported();
        protected abstract void doVisit(URI uri) throws IOException;
    }
    
    private class BrowseVisitor extends URIVisitor {

        @Override
        protected void doVisit(URI uri) throws IOException {
            Desktop.getDesktop().browse(uri);
        }

        @Override
        protected boolean isActionSupported() {
            return Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        }

        @Override
        public boolean isEnabled(URI uri) {
            return uri != null && super.isEnabled(uri);
        }
        
        
    }
    
    private class MailVisitor extends URIVisitor {

        @Override
        protected void doVisit(URI uri) throws IOException {
            if (uri == null) {
                Desktop.getDesktop().mail();
            } else {
                Desktop.getDesktop().mail(uri);
            }
        }

        @Override
        protected boolean isActionSupported() {
            return Desktop.getDesktop().isSupported(Desktop.Action.MAIL);
        }
        
    }
}

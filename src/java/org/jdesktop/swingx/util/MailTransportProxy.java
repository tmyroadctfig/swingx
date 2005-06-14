package org.jdesktop.swingx.util;

import java.util.List;

/*
* $Id$
*
* Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
* Santa Clara, California 95054, U.S.A. All rights reserved.
*/

/**
 * This is a proxy interface to allow usage of the JDIC mail transport for error logging
 * without adding dependensies on the JDIC itaelf.
 *
 * @author Alexander Zuev
 * @version 1.0
 */
public interface MailTransportProxy {
    /**
     * Compose and send message
     * @param toAddr List of addresses to whom to send this mesage
     * @param ccAddr List of addresses to whom to carbon-copy this message
     * @param subject Message subject
     * @param body Message main text
     * @param attach Pathis to files that needs to be send in attachment with this message
     */
    public void mailMessage(List<String> toAddr, List<String> ccAddr,
                            String subject, String body, List<String> attach) throws Error;
}

/*
* $Id$
*
* Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
* Santa Clara, California 95054, U.S.A. All rights reserved.
*/

/**
 * This reporter initializes and uses default mail user agent to send information
 * to predefined mail address. Note, that to use this report facility you need to
 * install Java Desktop Integration Components (JDIC) Library.
 *
 * One can obtain it on the
 * <a href="http://jdic.dev.java.net/">Official JDIC site</a>
 *
 * @author Alexander Zuev
 * @version 1.0
 */
package org.jdesktop.swingx;

import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;

/*import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;
import org.jdesktop.jdic.desktop.Message; */


public class MailErrorReporter extends ErrorReporter {
    private String mailAddr;
    private ArrayList<String> toList = new ArrayList<String>();

    public MailErrorReporter(String address) {
        super();
        this.mailAddr = address;
        toList.add(this.mailAddr);
    }

    /**
     * Get the mail address to which send error report
     *
     * @return mail address
     */
    public String getMailAddr() {
        return mailAddr;
    }

    /**
     * Set the address to which we will send mail
     *
     * @param mailAddr
     */
    public void setMailAddr(String mailAddr) {
        toList.remove(this.mailAddr);
        this.mailAddr = mailAddr;
        toList.add(this.mailAddr);
    }

    /**
     * Report given incident by popping up system default mail user agent with prepared message
     *
     * @param info <code>IncidentInfo</code> which incorporates all the information on error
     */
    public void reportIncident(IncidentInfo info) {
/*        Message msg = new Message();
        msg.setToAddrs(toList);
        msg.setSubject(info.getHeader());
        msg.setBody(getMessageBody(info));
        try {
            Desktop.mail(msg);
        } catch (DesktopException e) {} */
    }

    /**
     * This method is used to extract text message from the provided <code>IncidentInfo</code>.
     * Override this method to change text formatting or contents.
     *
     * @param incident - Incapsulates all the information about error
     * @return String to be used as a body message in report.
     */
    public String getMessageBody(IncidentInfo incident) {
        String body = incident.getBasicErrorMessage();
        if(incident.getDetailedErrorMessage() != null) {
            body.concat("\n"+incident.getDetailedErrorMessage());
        }
        if(incident.getErrorException() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            incident.getErrorException().printStackTrace(pw);
            body = body + "\n ----- " + sw.toString();
        }
        return body;
    }
}

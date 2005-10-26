/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
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
*/

package org.jdesktop.swingx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.util.MailTransportProxy;

/**
 * This reporter initializes and uses default mail user agent to send information
 * to predefined mail address. To send error message one needs to configure an MailTransportProxy.
 *
 * For example, here is how to use it with <a href="http://jdic.dev.java.net/">JDIC library</a>
 * <pre>

 import org.jdesktop.swingx.util.MailTransportProxy;
 import org.jdesktop.swingx.*;
 import org.jdesktop.jdic.desktop.Message;
 import org.jdesktop.jdic.desktop.Desktop;
 import org.jdesktop.jdic.desktop.DesktopException;

 public class TestApp {
     public static class MyMailTransport implements MailTransportProxy {
         public void mailMessage(java.util.List<String> toAddr,
                                 java.util.List<String> ccAddr,
                                 String subject, String body,
                                 java.util.List<String> attach) throws Error {
             Error result = null;

             Message msg = new Message();
             msg.setToAddrs(toAddr);
             msg.setCcAddrs(ccAddr);
             msg.setSubject(subject);
             msg.setBody(body);
             try {
                 msg.setAttachments(attach);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 Desktop.mail(msg);
             } catch (DesktopException e) {
                 result = new Error(e);
                 result.setStackTrace(Thread.currentThread().getStackTrace());
                 throw result;
             }
         }
     }
     public static void main(String args[]) {
         JFrame jf = new JFrame("Main frame");
     ... In the program body ...
         String errorDetails = "The filter factory can't accept this value";
         MailErrorReporter reporter = new MailErrorReporter("someone@the.net");
         reporter.setMailTransportProxy(new MyMailTransport());
         JXErrorDialog.setReporter(reporter);
         JXErrorDialog.showDialog(jf, "Filter Error", new RuntimeException(errorDetails));
     }
 } </pre>
 *
 * @author Alexander Zuev
 * @version 1.0
 */
public class MailErrorReporter extends ErrorReporter {
    private String mailAddr;
    private List<String> toList = new ArrayList<String>();
    private MailTransportProxy mailTransportProxy;

    /**
     * Constructs new MailErrorReporter with the given address assigned as destination
     * address for error report.
     *
     * @param address
     */
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

    public void setMailTransportProxy(MailTransportProxy mailTransportProxy) {
        this.mailTransportProxy = mailTransportProxy;
    }

    /**
     * Report given incident by popping up system default mail user agent with prepared message
     *
     * @param info <code>IncidentInfo</code> which incorporates all the information on error
     */
    public void reportIncident(IncidentInfo info) {
        if(mailTransportProxy != null) {
            try {
                mailTransportProxy.mailMessage(toList, null, info.getHeader(),
                                               getMessageBody(info), getAttachments(info));
            } catch(Error e) {} // Do nothing
        }
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

    /**
     * This method is used to extract list of paths to files that we want to send
     * as attachment with the current incident report mwssage.
     *
     * @param incident - Incapsulates all the information about error
     * @return List of Strings containing pathis to files
     */
    public List<String> getAttachments(IncidentInfo incident) {
        return null;
    }
}

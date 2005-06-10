/*
* $Id$
*
* Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
* Santa Clara, California 95054, U.S.A. All rights reserved.
*/

/**
 * This is abstract class that incapsulates all the information needed
 * to report a problem to the automated report/processing system.
 *
 * @author Alexander Zuev
 * @version 1.0
 */
package org.jdesktop.swingx;

public class IncidentInfo {
    /**
     * Short string that will be used as a error header
     */
    private String header;
    /**
     * Basic message that describes incident
     */
    private String basicErrorMessage;
    /**
     * Message that will fully describe the incident with all the
     * available details
     */
    private String detailedErrorMessage;
    /**
     * Optional Throwable that will be used
     */
    private Throwable errorException;

    /**
     * Main constructor that adds all the information to IncidentInfo
     * @param header
     * @param basicErrorMessage
     * @param detailedErrorMesage
     * @param errorException
     */
    public IncidentInfo(String header, String basicErrorMessage,
                        String detailedErrorMesage, Throwable errorException) {
        this.header = header;
        if(basicErrorMessage != null) {
            this.basicErrorMessage = basicErrorMessage;
        } else {
            if(errorException != null) {
                this.basicErrorMessage = errorException.getLocalizedMessage();
            } else {
                this.basicErrorMessage = "";
            }
        }
        this.detailedErrorMessage = detailedErrorMesage;
        this.errorException = errorException;
    }

    public IncidentInfo(String header, String basicErrorMessage, String detailedErrorMessage) {
        this(header, basicErrorMessage, detailedErrorMessage, null);
    }

    public IncidentInfo(String header, Throwable errorException) {
        this(header, null, null, errorException);
    }

    /**
     * Get the current header string
     *
     * @return header string
     */
    public String getHeader() {
        return header;
    }

    /**
     * Set the current header string
     *
     * @param header
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Get the basic error description
     *
     * @return basic error description
     */
    public String getBasicErrorMessage() {
        return basicErrorMessage;
    }

    /**
     * Set the current basic error description
     *
     * @param basicErrorMessage
     */
    public void setBasicErrorMessage(String basicErrorMessage) {
        this.basicErrorMessage = basicErrorMessage;
    }

    /**
     * Get the detailed error description
     *
     * @return detailed description
     */
    public String getDetailedErrorMessage() {
        return detailedErrorMessage;
    }

    /**
     * Set the detailed description for this error
     *
     * @param detailedErrorMessage
     */
    public void setDetailedErrorMessage(String detailedErrorMessage) {
        this.detailedErrorMessage = detailedErrorMessage;
    }

    /**
     * Get an exception that contains some additional information about the
     * error if provided.
     *
     * @return exception or null if no exception provided
     */
    public Throwable getErrorException() {
        return errorException;
    }

    /**
     * Set the exception that may contain additional information about the
     * error.
     *
     * @param errorException
     */
    public void setErrorException(Throwable errorException) {
        this.errorException = errorException;
    }
}
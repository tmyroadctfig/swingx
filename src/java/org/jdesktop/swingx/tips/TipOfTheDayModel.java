/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.tips;

import org.jdesktop.swingx.JXTipOfTheDay;

/**
 * A model for {@link org.jdesktop.swingx.JXTipOfTheDay}.<br>
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public interface TipOfTheDayModel {

  /**
   * @return the number of tips in this model
   */
  int getTipCount();

  /**
   * @param index
   * @return the tip at <code>index</code>
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (index &lt; 0 || index &gt;=
   *           getTipCount()).
   */
  Tip getTipAt(int index);

  /**
   * A tip.<br>
   */
  interface Tip {

    /**
     * @return very short (optional) description for the tip
     */
    String getTipName();

    /**
     * The tip object to show. See {@link JXTipOfTheDay} for supported object
     * types.
     * 
     * @return the tip to display
     */
    Object getTip();
  }

}

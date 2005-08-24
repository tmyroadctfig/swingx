/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.tips;

import org.jdesktop.swingx.tips.TipOfTheDayModel.Tip;

public class DefaultTip implements Tip {

  private String name;
  private Object tip;

  public DefaultTip() {   
  }
  
  public DefaultTip(String name, Object tip) {
    this.name = name;
    this.tip = tip;
  }

  public Object getTip() {
    return tip;
  }

  public void setTip(Object tip) {
    this.tip = tip;
  }

  public String getTipName() {
    return name;
  }

  public void setTipName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return getTipName();
  }
  
}
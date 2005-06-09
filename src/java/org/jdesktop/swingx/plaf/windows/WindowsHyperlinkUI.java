/**
 * @PROJECT.FULLNAME@ @VERSION@ License.
 *
 * Copyright @YEAR@ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdesktop.swingx.plaf.windows;

import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI;

/**
 * Extends BasicHyperlinkUI and paints the text with an offset when mouse
 * pressed.<br>
 */
public class WindowsHyperlinkUI extends BasicHyperlinkUI {

  public static ComponentUI createUI(JComponent c) {
    return new WindowsHyperlinkUI();
  }
  
  protected void paintButtonPressed(Graphics g, AbstractButton b) {
    setTextShiftOffset();
  }
  
}

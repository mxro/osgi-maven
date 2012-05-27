/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.ToolButton;

public class ToolButtonHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return component instanceof ToolButton;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    pe.stopEvent();
    if (pe.isShiftKey()) {
      focusPreviousWidget(component);
    } else {
      focusNextWidget(component);
    }
  }

}

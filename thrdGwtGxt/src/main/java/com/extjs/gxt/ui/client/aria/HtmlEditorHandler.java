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
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;

public class HtmlEditorHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    if (component.getParent() instanceof ToolBar && component.getParent().getParent() instanceof HtmlEditor && pe.getKeyCode() == KeyCodes.KEY_TAB) {
      return true;
    }
    return false;
  }
  
  @Override
  public void onTab(Component component, PreviewEvent pe) {
    pe.stopEvent();
    Component c = (Component)component.getParent().getParent();
    c.el().selectNode("iframe").focus();
  }
}

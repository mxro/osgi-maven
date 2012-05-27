/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.toolbar;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Fills the toolbar width, pushing any newly added items to the right.
 */
public class FillToolItem extends Component {

  /**
   * Creates a new fill item.
   */
  public FillToolItem() {
    getAriaSupport().setPresentation(true);
  }
  
  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
  }

}

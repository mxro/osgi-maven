/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.core.El;

/**
 * <code>ClickRepeater</code> event type.
 */
public class ClickRepeaterEvent extends BaseEvent {

  private El el;

  public ClickRepeaterEvent(Object source, El el) {
    super(source);
    this.el = el;
  }

  public El getEl() {
    return el;
  }

  public void setEl(El el) {
    this.el = el;
  }

}

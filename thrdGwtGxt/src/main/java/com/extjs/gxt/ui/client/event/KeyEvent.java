/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.Event;

public class KeyEvent extends ComponentEvent {

  public KeyEvent(Component component, Event event) {
    super(component, event);
  }

  public KeyEvent(Component component) {
    super(component);
  }

}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;


/**
 * Key listener.
 */
public class KeyListener implements Listener<ComponentEvent> {

  /**
   * Fires on key down.
   * 
   * @param event the component event
   */
  public void componentKeyDown(ComponentEvent event) {

  }

  /**
   * Fires on key press.
   * 
   * @param event the component event
   */
  public void componentKeyPress(ComponentEvent event) {

  }

  /**
   * Fires on key up.
   * 
   * @param event the component event
   */
  public void componentKeyUp(ComponentEvent event) {

  }

  public void handleEvent(ComponentEvent e) {
    EventType type = e.getType();
    if (type == Events.KeyPress) {
      componentKeyPress(e);
    } else if (type == Events.KeyUp){
      componentKeyUp(e);
    } else if (type == Events.KeyDown) {
      componentKeyDown(e);
    }
  }

}

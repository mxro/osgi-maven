/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.WindowManager;

public class WindowManagerEvent extends BaseEvent {

  /**
   * The window manager.
   * 
   * @deprecated use methods
   */
  public WindowManager manager;

  /**
   * The window.
   * 
   * @deprecated use methods
   */
  public Window window;

  public WindowManagerEvent(WindowManager manager) {
    super(manager);
    this.manager = manager;
  }

  public WindowManagerEvent(WindowManager manager, Window window) {
    this(manager);
    this.window = window;
  }

  public WindowManager getManager() {
    return manager;
  }

  public void setManager(WindowManager manager) {
    this.manager = manager;
  }

  public Window getWindow() {
    return window;
  }

  public void setWindow(Window window) {
    this.window = window;
  }

}

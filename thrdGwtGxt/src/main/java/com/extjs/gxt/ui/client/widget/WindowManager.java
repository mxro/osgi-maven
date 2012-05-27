/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.WindowManagerEvent;

/**
 * An object that represents a group of {@link Window} instances and provides
 * z-order management and window activation behavior.
 */
public class WindowManager extends BaseObservable {

  private static WindowManager instance;

  /**
   * Returns the singleton instance.
   * 
   * @return the window manager
   */
  public static WindowManager get() {
    if (instance == null) instance = new WindowManager();
    return instance;
  }

  private Stack<Window> accessList;
  private Comparator<Window> comparator;
  private Window front;
  private List<Window> windows;

  public WindowManager() {
    accessList = new Stack<Window>();
    windows = new ArrayList<Window>();
    comparator = new Comparator<Window>() {
      public int compare(Window w1, Window w2) {
        Long d1 = (Long) w1.getData("_gxtdate");
        Long d2 = (Long) w2.getData("_gxtdate");
        return d1 == null || d1 < d2 ? -1 : 1;
      }
    };

  }

  /**
   * Activates the next window in the access stack.
   * 
   * @param window the reference window
   * @return true if the next window exists
   */
  public boolean activateNext(Window window) {
    int count = windows.size();
    if (count > 1) {
      int idx = windows.indexOf(window);
      if (idx == count - 1) {
        return false;
      }
      setActiveWin(windows.get(++idx));
      return true;
    }
    return false;
  }

  /**
   * Activates the previous window in the access stack.
   * 
   * @param window the reference window
   * @return true if a previous window exists
   */
  public boolean activatePrevious(Window window) {
    int count = windows.size();
    if (count > 1) {
      int idx = windows.indexOf(window);
      if (idx == 0) {
        return false;
      }
      setActiveWin(windows.get(--idx));
    }
    return false;
  }

  /**
   * Brings the specified window to the front of any other active windows.
   * 
   * @param window the window return True if the dialog was brought to the
   *          front, else false if it was already in front
   */
  public boolean bringToFront(Window window) {
    if (window != front) {
      window.setData("_gxtdate", System.currentTimeMillis());
      orderWindows(false);
      return true;
    } else {
      window.focus();
    }

    return false;
  }

  /**
   * Gets a registered window by id.
   * 
   * @param id the window id
   * @return the window
   */
  public Window get(String id) {
    return (Window) ComponentManager.get().get(id);
  }

  /**
   * Gets the currently-active window in the group.
   * 
   * @return the active window
   */
  public Window getActive() {
    return front;
  }

  /**
   * Returns the ordered windows.
   * 
   * @return the windows
   */
  public Stack<Window> getStack() {
    return accessList;
  }

  /**
   * Returns the visible windows.
   * 
   * @return the windows
   */
  public List<Window> getWindows() {
    return windows;
  }

  /**
   * Hides all windows that are registered to this WindowManager.
   */
  public void hideAll() {
    for (int i = accessList.size() - 1; i >= 0; --i) {
      accessList.get(i).hide();
    }
  }

  public void register(Window window) {
    accessList.push(window);
    windows.add(window);
    window.setData("_gxtdate", System.currentTimeMillis());
    fireEvent(Events.Register, new WindowManagerEvent(this, window));
  }

  /**
   * Sends the specified window to the back of other active windows.
   * 
   * @param window the window
   * @return the window
   */
  public Window sendToBack(Window window) {
    window.setData("_gxtdate", System.currentTimeMillis());
    orderWindows(true);
    return window;
  }

  public void unregister(Window window) {
    if (front == window) {
      front = null;
    }
    accessList.remove(window);
    windows.remove(window);
    activateLast();
    fireEvent(Events.Unregister, new WindowManagerEvent(this, window));
  }

  protected void activateLast() {
    for (int i = accessList.size() - 1; i >= 0; --i) {
      Window w = (Window) accessList.get(i);
      if (w.isVisible()) {
        setActiveWin(w);
        return;
      }
    }
    setActiveWin(null);
  }

  protected void orderWindows(boolean reverse) {
    if (accessList.size() > 0) {
      Collections.sort(accessList, comparator);
      if (reverse) {
        Collections.reverse(accessList);
      }
      for (int i = 0; i < accessList.size(); i++) {
        Window w = (Window) accessList.get(i);
        w.setZIndex(XDOM.getTopZIndex(10));
      }
      activateLast();
    }
  }

  protected void setActiveWin(Window window) {
    if (window != front) {
      if (front != null) {
        front.setActive(false);
      }
      front = window;
      if (window != null) {
        window.setActive(true);
        window.focus();
      }
    }
  }
}

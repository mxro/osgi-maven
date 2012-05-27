/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the <code>ChangeEventSource</code> interface.
 */
public class ChangeEventSupport implements ChangeEventSource {

  protected List<ChangeListener> listeners;
  protected boolean silent;

  public void addChangeListener(ChangeListener... listener) {
    if (listeners == null) {
      listeners = new ArrayList<ChangeListener>();
    }
    for (int i = 0; i < listener.length; i++) {
      listeners.add(listener[i]);
    }
  }

  public boolean isSilent() {
    return silent;
  }

  public void notify(ChangeEvent event) {
    if (!silent && listeners != null) {
      //make a copy of it because of ConcurrentModificationException
      List<ChangeListener> l = new ArrayList<ChangeListener>(listeners);
      for (int i = 0, len = l.size(); i< len; i++) {
        ChangeListener listener = l.get(i);
        listener.modelChanged(event);
      }
    }
  }

  public void removeChangeListener(ChangeListener... listener) {
    if (listeners != null) {
      for (int i = 0; i < listener.length; i++) {
        listeners.remove(listener[i]);
      }
    }
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  public void removeChangeListeners() {
    if (listeners != null) {
      listeners.clear();
    }
  }

}

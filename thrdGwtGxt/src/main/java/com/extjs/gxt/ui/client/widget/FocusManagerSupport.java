/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.event.BaseObservable;

/**
 * Component support class for Focus Manager.
 */
public class FocusManagerSupport extends BaseObservable {

  private boolean ignore;
  private String nextId;
  private String previousId;
  private Component c;
  
  FocusManagerSupport(Component c) {

  }
  
  /**
   * Returns the target component.
   * 
   * @return the target component
   */
  public Component getComponent() {
    return c;
  }

  /**
   * Returns the next component id.
   * 
   * @return the next component id
   */
  public String getNextId() {
    return nextId;
  }

  /**
   * Returns the previous component id.
   * 
   * @return the previous component id
   */
  public String getPreviousId() {
    return previousId;
  }

  /**
   * Returns true if the component will be ignored by the ARIA and FocusManager
   * API.
   * 
   * @return true if component is being ignored
   */
  public boolean isIgnore() {
    return ignore;
  }

  /**
   * True to mark this component to be ignored by the ARIA and FocusManager API
   * (defaults to false). Typically set to true for any containers that should
   * not be navigable to.
   * 
   * @param ignore true to ignore
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * The id of the component to navigate to when TAB is pressed (defaults to
   * null). When set, the focus manager will override its default behavior to
   * determine the next focusable widget.
   * 
   * @param nextId the next component id
   */
  public void setNextId(String nextId) {
    this.nextId = nextId;
  }

  /**
   * The id of the component to navigate to when SHIFT-TAB is pressed (defaults
   * to null). When set, the focus manager will override its default behavior to
   * determine the previous focusable widget.
   * 
   * @param previousId the previous component id
   */
  public void setPreviousId(String previousId) {
    this.previousId = previousId;
  }
}

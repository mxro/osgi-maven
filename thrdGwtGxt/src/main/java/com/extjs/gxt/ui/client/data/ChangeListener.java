/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Interface for objects that listen for model change events.
 * 
 * @see ChangeEvent
 * @see BaseModel
 */
public interface ChangeListener {

  /**
   * Fired when the model's state has changed.
   * 
   * @param event an event describing the change
   */
  public void modelChanged(ChangeEvent event);

}

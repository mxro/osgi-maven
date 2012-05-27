/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * A <code>ListLoadConfig</code> which adds a parent property.
 * 
 * @see RemoteSortTreeLoader
 */
public interface RemoteSortTreeLoadConfig extends ListLoadConfig {

  /**
   * Returns the parent.
   * 
   * @return the parent
   */
  public ModelData getParent();

  /**
   * Sets the parent.
   * 
   * @param parent the parent
   */
  public void setParent(ModelData parent);

}

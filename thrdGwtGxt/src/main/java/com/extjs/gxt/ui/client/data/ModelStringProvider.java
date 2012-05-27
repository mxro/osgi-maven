/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Interface for objects that can translate a model's typed values to strings.
 */
public interface ModelStringProvider<M extends ModelData> {

  /**
   * Returns the string value for the property.
   * 
   * @param model the model instance
   * @param property the property name
   * @return the string value
   */
  public String getStringValue(M model, String property);

}

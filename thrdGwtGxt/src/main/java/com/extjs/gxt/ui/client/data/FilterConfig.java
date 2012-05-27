/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Filter config object for <code>GridFilters</code>
 */
public interface FilterConfig {

  /**
   * Returns the comparison value. This value will be set by numeric ('lt',
   * 'gt', 'eq') and date ('after', 'before', 'on') filter types.
   * 
   * @return the comparison value
   */
  public String getComparison();

  /**
   * Returns the model property name.
   * 
   * @return the property name
   */
  public String getField();

  public String getType();

  /**
   * Returns the filter value.
   * 
   * @return the filter value
   */
  public Object getValue();

  /**
   * Determines if the model will be filtered by the given filter.
   * 
   * @param model the model
   * @param test the filter config value
   * @param value
   * @return true if filtered
   */
  public boolean isFiltered(ModelData model, Object test, String compariosn, Object value);

  /**
   * Sets the comparison value. This value will be set by numeric ('lt', 'gt',
   * 'eq') and date ('after', 'before', 'on') filter types.
   * 
   * @param comparison the comparison
   */
  public void setComparison(String comparison);

  /**
   * Sets the model property name the filter is bound to.
   * 
   * @param field the property name
   */
  public void setField(String field);

  /**
   * Sets the type of filter ('string', 'numeric', 'date', 'boolean', 'list').
   * 
   * @param type the filter type
   */
  public void setType(String type);

  /**
   * Sets the value. The type of the value will be determined by the type of
   * filter.
   * 
   * @param value the value
   */
  public void setValue(Object value);

}

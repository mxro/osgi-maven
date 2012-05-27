/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

public class BaseBooleanFilterConfig extends BaseFilterConfig {

  public BaseBooleanFilterConfig() {
    super();
  }

  public BaseBooleanFilterConfig(String type, Object value) {
    super(type, value);
  }

  public BaseBooleanFilterConfig(String type, String comparison, Object value) {
    super(type, comparison, value);
  }

  public boolean isFiltered(ModelData model, Object test, String comparison, Object value) {
    if (value == null) {
      return true;
    }
    boolean t = (Boolean)test;
    boolean v;
    if (value instanceof String) {
      v = Boolean.parseBoolean((String)value);
    } else {
      v = (Boolean)value;
    }
    return t != v;
  }

}

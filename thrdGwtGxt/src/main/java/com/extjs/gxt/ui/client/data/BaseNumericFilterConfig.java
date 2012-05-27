/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

public class BaseNumericFilterConfig extends BaseFilterConfig {

  public BaseNumericFilterConfig() {
    super();
  }

  public BaseNumericFilterConfig(String type, Object value) {
    super(type, value);
  }

  public BaseNumericFilterConfig(String type, String comparison, Object value) {
    super(type, comparison, value);
  }

  public boolean isFiltered(ModelData model, Object test, String comparison, Object value) {
    if (value == null) {
      return false;
    }
    double t = (Double) test;
    Double v = (Double) value;

    if ("gt".equals(comparison)) {
      return t > v;
    } else if ("lt".equals(comparison)) {
      return t < v;
    } else if ("eq".equals(comparison)) {
      return t == v;
    }
    return false;
  }

}

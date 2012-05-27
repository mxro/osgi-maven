/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.Date;

import com.extjs.gxt.ui.client.util.DateWrapper;

public class BaseDateFilterConfig extends BaseFilterConfig {

  public BaseDateFilterConfig() {
    super();
  }

  public BaseDateFilterConfig(String type, Object value) {
    super(type, value);
  }

  public BaseDateFilterConfig(String type, String comparison, Object value) {
    super(type, comparison, value);
  }

  public boolean isFiltered(ModelData model, Object test, String comparison, Object value) {
    Date t = (Date) test;
    Date v = (Date) value;
    if (value == null) {
      return false;
    }
    if ("after".equals(comparison)) {
      return v.before(t);
    } else if ("before".equals(comparison)) {
      return v.after(t);
    } else if ("on".equals(comparison)) {
      t = new DateWrapper(t).resetTime().asDate();
      v = new DateWrapper(v).resetTime().asDate();
      return !v.equals(t);
    }
    return true;
  }

}

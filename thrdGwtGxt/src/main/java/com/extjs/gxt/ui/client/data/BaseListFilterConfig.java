/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.List;

public class BaseListFilterConfig extends BaseFilterConfig {

  public BaseListFilterConfig() {
    super();
  }

  public BaseListFilterConfig(String type, Object value) {
    super(type, value);
  }

  public BaseListFilterConfig(String type, String comparison, Object value) {
    super(type, comparison, value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public boolean isFiltered(ModelData model, Object test, String comparison, Object value) {
    if (value == null) {
      return true;
    }
    List<String> matches = (List)test;
    if (matches.contains(value)) {
      return false;
    }
    return true;
  }

}

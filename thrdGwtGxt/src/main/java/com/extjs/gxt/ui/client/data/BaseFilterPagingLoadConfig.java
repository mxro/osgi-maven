/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.List;

public class BaseFilterPagingLoadConfig extends BasePagingLoadConfig implements FilterPagingLoadConfig {

  List<FilterConfig> ignore;
  
  public BaseFilterPagingLoadConfig() {
    super();
  }

  public BaseFilterPagingLoadConfig(int offset, int limit) {
    super(offset, limit);
  }

  public List<FilterConfig> getFilterConfigs() {
    return get("filters");
  }

  public void setFilterConfigs(List<FilterConfig> configs) {
    set("filters", configs);
  }

}

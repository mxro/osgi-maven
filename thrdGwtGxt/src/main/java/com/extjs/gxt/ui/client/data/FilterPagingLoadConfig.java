/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;

/**
 * A <code>PagingLoadConfig</code> which adds support for filter criteria.
 * 
 * @see GridFilters
 */
public interface FilterPagingLoadConfig extends PagingLoadConfig {

  /**
   * Returns the filter configs.
   * 
   * @return the filter configs
   */
  public List<FilterConfig> getFilterConfigs();

  /**
   * Sets the filter configs.
   * 
   * @param configs the filter configs
   */
  public void setFilterConfigs(List<FilterConfig> configs);

}

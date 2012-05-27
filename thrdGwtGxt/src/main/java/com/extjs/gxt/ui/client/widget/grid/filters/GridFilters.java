/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid.filters;

import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;

/**
 * <p>
 * GridFilter is a plugin for grids that allow for a slightly more robust
 * representation of filtering than what is provided by the default store.
 * </p>
 * <p>
 * Filtering is adjusted by the user using the grid's column header menu (this
 * menu can be disabled through configuration). Through this menu users can
 * configure, enable, and disable filters for each column.
 * </p>
 */
public class GridFilters extends AbstractGridFilters {

  @Override
  public boolean isLocal() {
    return super.isLocal();
  }

  @Override
  public void setLocal(boolean local) {
    super.setLocal(local);
  }

  @Override
  protected Loader<?> getLoader(Store<ModelData> store) {
    if (store instanceof ListStore<?>) {
      return ((ListStore<?>) store).getLoader();
    }
    return null;
  }

  @Override
  protected Store<ModelData> getStore() {
    return grid.getStore();
  }
}
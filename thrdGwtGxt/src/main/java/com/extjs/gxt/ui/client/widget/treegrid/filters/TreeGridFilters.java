/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid.filters;

import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.filters.AbstractGridFilters;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

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
public class TreeGridFilters extends AbstractGridFilters {
  public TreeGridFilters() {
    // TreeGridFilters only support local filtering, see TreeGrid.setExpanded
    setLocal(true);
  }

  @Override
  public void init(Component component) {
    assert component instanceof TreeGrid<?> : "TreeGridFilters can only be used with a TreeGrid.";
    super.init(component);
  }

  @Override
  protected Loader<?> getLoader(Store<ModelData> store) {
    // we do not support remote filtering on TreeGridFilter
    return null;
  }

  @Override
  protected Store<ModelData> getStore() {
    return ((TreeGrid<ModelData>) grid).getTreeStore();
  }

}

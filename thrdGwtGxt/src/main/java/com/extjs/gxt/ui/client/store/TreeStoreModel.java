/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.store;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * "Wraps" a model and provides parent-child relationships to the model.
 */
public class TreeStoreModel extends BaseTreeModel {

  public TreeStoreModel(ModelData model) {
    set("model", model);
  }

  /**
   * Returns the actual model.
   * 
   * @return the model
   */
  public ModelData getModel() {
    return (ModelData)get("model");
  }

}

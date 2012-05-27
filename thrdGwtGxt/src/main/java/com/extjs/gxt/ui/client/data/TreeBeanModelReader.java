/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>DataReader</code> for beans using a <code>BeanModelFactory</code>.
 * Valid return types are a list of beans . Beans must be of the same type and
 * beans must implement {@link BeanModelTag} or have a companion
 * {@link BeanModelMarker} marker class.
 */
public class TreeBeanModelReader implements DataReader<List<ModelData>> {

  private boolean factoryForEachBean;

  /**
   * Return if a BeanModelFactory is created for each bean or not.
   * 
   * @return true if a BeanModelFactory is created for each bean or not
   */
  public boolean isFactoryForEachBean() {
    return factoryForEachBean;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<ModelData> read(Object loadConfig, Object data) {
    if (data instanceof List) {
      List<Object> beans = (List) data;
      if (beans.size() > 0) {
        if (factoryForEachBean) {
          List models = new ArrayList(beans.size());
          for (Object o : beans) {
            BeanModelFactory factory = BeanModelLookup.get().getFactory(o.getClass());
            assert factory != null : "No BeanModelFactory found for " + o.getClass();
            models.add(factory.createModel(o));
          }
          return models;
        } else {
          BeanModelFactory factory = BeanModelLookup.get().getFactory(beans.get(0).getClass());
          assert factory != null : "No BeanModelFactory found for " + beans.get(0).getClass();
          return (List) factory.createModel(beans);
        }
      }
      return (List) beans;

    }

    assert false : "Error converting data";

    return null;
  }

  /**
   * Set to true to create a BeanModelFactory for each bean in the list
   * (defaults to false).
   * 
   * @param factoryForEachBean true to enable
   */
  public void setFactoryForEachBean(boolean factoryForEachBean) {
    this.factoryForEachBean = factoryForEachBean;
  }

}

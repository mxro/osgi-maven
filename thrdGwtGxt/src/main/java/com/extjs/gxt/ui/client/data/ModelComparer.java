/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.data;

/**
 * Compares the model instances for equality.
 * 
 * @param <M> the model type
 */
public interface ModelComparer<M extends ModelData> {

  /**
   * Returns true if and model instances represent the same entity.
   * 
   * @param m1 model one
   * @param m2 model two
   * @return true if the models are equal
   */
  public boolean equals(M m1, M m2);

}

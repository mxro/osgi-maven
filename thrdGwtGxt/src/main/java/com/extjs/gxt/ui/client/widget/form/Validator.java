/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

/**
 * Interface for validating a field's value.
 */
public interface Validator {

  /**
   * Validates the fields value.
   * 
   * @param field the field
   * @param value the value to validate
   * @return <code>null</code> if validation passes, otherwise the error message
   */
  public String validate(Field<?> field, String value);

}

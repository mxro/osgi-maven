/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.user.client.Event;

@SuppressWarnings({"unchecked", "rawtypes"})
public class FieldSetEvent extends ContainerEvent {

  private FieldSet fieldSet;

  public FieldSetEvent(FieldSet fieldSet) {
    super(fieldSet);
    this.fieldSet = fieldSet;
  }

  public FieldSetEvent(FieldSet fieldSet, Event event) {
    this(fieldSet);
    this.event = event;
  }

  /**
   * Returns the source field set.
   * 
   * @return the field set
   */
  public FieldSet getFieldSet() {
    return fieldSet;
  }

  /**
   * Sets the source field set.
   * 
   * @param fieldSet the field set
   */
  public void setFieldSet(FieldSet fieldSet) {
    this.fieldSet = fieldSet;
  }

}

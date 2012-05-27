/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.Event;

public class HtmlEditorEvent extends FieldEvent {

  public HtmlEditorEvent(Field<?> field, Event event) {
    super(field, event);
  }

  public HtmlEditorEvent(Field<?> field) {
    super(field);
  }

}

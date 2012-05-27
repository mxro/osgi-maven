/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * Single radio field. Same as Checkbox, but provided as a convenience for
 * automatically setting the input type. Radio grouping is handled automatically
 * by the browser if you give each radio in a group the same name.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>Field Focus</dd>
 * <dd>Field Blur</dd>
 * <dd>Field Change</dd>
 * <dd>Field Invalid</dd>
 * <dd>Field Valid</dd>
 * <dd>Field KeyPress</dd>
 * <dd>Field SpecialKey</dd>
 * </dl>
 */
public class Radio extends CheckBox {

  protected RadioGroup group;

  /**
   * Returns the radios container group.
   * 
   * @return the group
   */
  public RadioGroup getGroup() {
    return group;
  }

  @Override
  public void setName(String name) {
    this.name = name;
    if (afterRender) {
      replaceInputElement(DOM.createInputRadio(name));
      if (isAttached()) {
        alignElements();
      }
    }
  }

  @Override
  public void setValue(Boolean value) {
    if (value == null) {
      value = new Boolean(false);
    }
    focusValue = value;
    if (value && group != null) {
      group.onRadioSelected(this);
    }
    super.setValue(value);
  }

  @Override
  protected void fireChangeEvent(Object oldValue, Object value) {
    super.fireChangeEvent(oldValue, value);
    if (value != null && value instanceof Boolean && ((Boolean) value).booleanValue()
        && !Util.equalWithNull(oldValue, value) && group != null) {
      FieldEvent e = new FieldEvent(group);
      e.setOldValue(oldValue);
      e.setValue(value);
      group.fireEvent(Events.Change, e);
    }
  }

  @Override
  protected void onClick(ComponentEvent be) {
    // if we click the boxLabel, the browser fires an own click event
    // automatically, so we ignore one of it
    if (boxLabelEl != null && boxLabelEl.dom.isOrHasChild(be.getTarget())) {
      return;
    }
    if (readOnly) {
      be.stopEvent();
      return;
    }
    setValue(true);
  }

  protected void replaceInputElement(Element elem) {
    InputElement newInputElem = InputElement.as(elem);

    int tabIndex = getTabIndex();
    boolean checked = getValue();
    boolean enabled = isEnabled();
    String uid = input.getId();
    String accessKey = InputElement.as(input.dom).getAccessKey();
    int sunkEvents = Event.getEventsSunk(input.dom);
    String styleName = input.getStyleName();
    String valueAttribute = getValueAttribute();

    getElement().replaceChild(newInputElem, input.dom);

    Event.sinkEvents(elem, 0);
    input = new El((Element) Element.as(newInputElem));
    input.makePositionable();

    Event.sinkEvents(input.dom, sunkEvents);

    input.setId(uid);
    if (!"".equals(accessKey)) {
      InputElement.as(input.dom).setAccessKey(accessKey);
    }
    setTabIndex(tabIndex);
    setValueAttribute(valueAttribute);
    setValue(checked);
    setEnabled(enabled);

    input.setStyleName(styleName);
  }

}

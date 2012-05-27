/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.user.client.Element;

/**
 * A group of Radio's.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Change</b> : FieldEvent(field, value, oldValue)<br>
 * <div>Fires after a child radio is selected.</div>
 * <ul>
 * <li>field : this the group, not the radio</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class RadioGroup extends MultiField<Radio> {

  /**
   * TextField Messages.
   */
  public class RadioGroupMessages extends FieldMessages {
    private String selectionRequired = GXT.MESSAGES.textField_blankText();

    public String getSelectionRequired() {
      return selectionRequired;
    }

    public void setSelectionRequired(String selectionRequired) {
      this.selectionRequired = selectionRequired;
    }
  }

  private static int autoId = 0;
  private String groupName;
  private boolean selectionRequired;

  /**
   * Creates a new radio group.
   */
  public RadioGroup() {
    this("gxt.RadioGroup." + (autoId++));
  }

  /**
   * Creates a new radio group.
   * 
   * @param name the group name
   */
  public RadioGroup(String name) {
    this.groupName = name;
    messages = new RadioGroupMessages();
    setSpacing(5);
  }

  public void add(Radio radio) {
    super.add(radio);
    radio.group = this;
    radio.setName(groupName);
  }

  @Override
  public RadioGroupMessages getMessages() {
    return (RadioGroupMessages) messages;
  }

  /**
   * Returns the selected radio.
   */
  @Override
  public Radio getValue() {
    for (int i = 0; i < getAll().size(); i++) {
      Radio r = (Radio) getAll().get(i);
      if (r.getValue()) {
        return r;
      }
    }
    return null;
  }

  /**
   * Returns true if a selection is required.
   * 
   * @return the selection required state
   */
  public boolean isSelectionRequired() {
    return selectionRequired;
  }

  @Override
  public boolean isValid(boolean preventMark) {
    if (selectionRequired) {
      boolean sel = false;
      for (int i = 0; i < getAll().size(); i++) {
        Radio r = (Radio) getAll().get(i);
        if (r.getValue()) {
          sel = true;
        }
      }

      if (!sel) {
        boolean restore = this.preventMark;
        this.preventMark = preventMark;
        markInvalid(getMessages().getSelectionRequired());
        this.preventMark = restore;
        return false;
      }
    }
    for (int i = 0; i < getAll().size(); i++) {
      Radio radio = (Radio) getAll().get(i);
      if (!radio.isValid(preventMark)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void markInvalid(String msg) {
    if (!GXT.isAriaEnabled()) {
      super.markInvalid(msg);
    }
  }

  /**
   * Sets whether a selection is required when validating the group (defaults to
   * false).
   * 
   * @param selectionRequired true to require a selection
   */
  public void setSelectionRequired(boolean selectionRequired) {
    this.selectionRequired = selectionRequired;
  }

  @Override
  public void setValue(Radio value) {
    if (value != null) {
      value.setValue(true);
    }
  }

  protected void onRadioSelected(Radio radio) {
    for (int i = 0; i < getAll().size(); i++) {
      Radio r = (Radio) getAll().get(i);
      if (r != radio) {
        r.setValue(false);
      }
    }
    clearInvalid();
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    getElement().removeAttribute("tabindex");

    if (GXT.isAriaEnabled()) {
      setAriaRole("radiogroup");
      StringBuffer sb = new StringBuffer();
      for (Field<?> f : fields) {
        String id = f.getId();
        if (f instanceof Radio) {
          id = f.el().selectNode("INPUT").getId();
        }
        sb.append(id + " ");
      }
      getElement().setAttribute("aria-owns", sb.toString());
    }
  }

  @Override
  protected void setAriaState(String stateName, String stateValue) {

  }

}

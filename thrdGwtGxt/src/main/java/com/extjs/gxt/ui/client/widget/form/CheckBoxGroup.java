/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.user.client.Element;

/**
 * A group of CheckBox's.
 */
public class CheckBoxGroup extends MultiField<CheckBox> {

  /**
   * Creates a new check box group.
   */
  public CheckBoxGroup() {
    setSpacing(5);
    getFocusSupport().setIgnore(true);
  }

  /**
   * Returns the first checked check box.
   */
  @Override
  public CheckBox getValue() {
    for (Field<?> f : fields) {
      if (f instanceof CheckBox) {
        CheckBox check = (CheckBox) f;
        if (check.getValue()) {
          return check;
        }
      }
    }
    return null;
  }

  /**
   * Returns a list of all selected check boxes.
   * 
   * @return the list
   */
  public List<CheckBox> getValues() {
    List<CheckBox> values = new ArrayList<CheckBox>();
    for (Field<?> f : fields) {
      if (f instanceof CheckBox) {
        CheckBox check = (CheckBox) f;
        if (check.getValue()) {
          values.add(check);
        }
      }
    }
    return values;
  }
  
  @Override
  public void markInvalid(String msg) {
    if (!GXT.isAriaEnabled()) {
      super.markInvalid(msg);
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    getElement().removeAttribute("tabindex");

    if (GXT.isAriaEnabled()) {
      setAriaRole("group");
      String text = GXT.MESSAGES.checkBoxGroup_text(getFieldLabel());
      for (Field<?> f : fields) {
        if (f instanceof CheckBox) {
          CheckBox check = (CheckBox) f;
          check.getAriaSupport().setLabel(text);
        }
      }
    }
  }

}

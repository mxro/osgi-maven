/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * Adds a menu item that contains a checkbox by default, but can also be part of
 * a radio group.
 * 
 * A horizontal row of buttons.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeCheckChange</b> : MenuEvent(menu, item)<br>
 * <div>Fires before the item is checked or unchecked. Listeners can cancel the
 * action by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>item : this</li>
 * <li>menu : the parent menu</li>
 * <li>checked : the check state</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>CheckChange</b> : MenuEvent(menu, item)<br>
 * <div>Fires after the item is checked or unchecked.</div>
 * <ul>
 * <li>item : this</li>
 * <li>menu : the parent menu</li>
 * <li>checked : the check state</li>
 * </ul>
 * </dd> 
 * </dl>
 */
public class CheckMenuItem extends MenuItem {

  private String groupStyle = "x-menu-group-item";
  private boolean checked;
  private String group, groupTitle;

  /**
   * Creates a new check menu item.
   */
  public CheckMenuItem() {
    hideOnClick = true;
    itemStyle = "x-menu-item x-menu-check-item";
    canActivate = true;
  }

  /**
   * Creates a new check menu item.
   * 
   * @param text the text
   */
  public CheckMenuItem(String text) {
    this();
    setText(text);
  }

  /**
   * Returns the ARIA group title.
   * 
   * @return the group title
   */
  public String getAriaGroupTitle() {
    return groupTitle;
  }

  /**
   * Returns the group name.
   * 
   * @return the name
   */
  public String getGroup() {
    return group;
  }

  /**
   * Returns the group style.
   * 
   * @return the group style
   */
  public String getGroupStyle() {
    return groupStyle;
  }

  /**
   * Returns true if the item is checked.
   * 
   * @return the checked state
   */
  public boolean isChecked() {
    return checked;
  }

  /**
   * Sets the title attribute on the group container element. Only applies to
   * radio check items when ARIA is enabled.
   * 
   * @param title the title
   */
  public void setAriaGroupTitle(String title) {
    this.groupTitle = title;
  }

  /**
   * Set the checked state of this item.
   * 
   * @param checked the new checked state
   */
  public void setChecked(boolean checked) {
    setChecked(checked, false);
  }

  /**
   * Set the checked state of this item.
   * 
   * @param state the new checked state
   * @param supressEvent true to prevent the CheckChange event from firing
   */
  public void setChecked(boolean state, boolean supressEvent) {
    if (!rendered) {
      this.checked = state;
      return;
    }
    MenuEvent me = new MenuEvent(parentMenu);
    me.setItem(this);
    me.setChecked(state);
    if (supressEvent || fireEvent(Events.BeforeCheckChange, me)) {

      if (getGroup() == null) {
        setIcon(state ? GXT.IMAGES.checked() : GXT.IMAGES.unchecked());
        el().setStyleName("x-menu-checked", state);
      } else {
        el().addStyleName("x-menu-item-radio");
        setIcon(state ? GXT.IMAGES.group_checked() : null);
        el().setStyleName("x-menu-radio-sel", state);
      }
      checked = state;

      if (GXT.isAriaEnabled()) {
        Accessibility.setState(getElement(), "aria-checked", state ? "true" : "false");
      }
      if (!supressEvent) {
        fireEvent(Events.CheckChange, me);
      }
    }
  }

  /**
   * All check items with the same group name will automatically be grouped into
   * a single-select radio button group (defaults to null).
   * 
   * @param group the group
   */
  public void setGroup(String group) {
    this.group = group;
  }

  /**
   * The default CSS class to use for radio group check items (defaults to
   * "x-menu-group-item").
   * 
   * @param groupStyle the group style
   */
  public void setGroupStyle(String groupStyle) {
    if (rendered) {
      el().removeStyleName(this.groupStyle);
      el().addStyleName(groupStyle);
      if (GXT.isAriaEnabled()) {
        Accessibility.setRole(getElement(), "menuitemradio");
      }
    }
    this.groupStyle = groupStyle;
  }

  protected void onClick(ComponentEvent ce) {
    if (!disabled && getGroup() == null) {
      setChecked(!checked);
    }
    if (!disabled && !checked && getGroup() != null) {
      setChecked(!checked);
      onRadioClick(ce);
    }
    super.onClick(ce);
  }

  protected void onRadioClick(ComponentEvent ce) {
    if (parentMenu != null) {
      for (Component item : parentMenu.getItems()) {
        if (item instanceof CheckMenuItem) {
          CheckMenuItem check = (CheckMenuItem) item;
          if (check != this && check.isChecked() && Util.equalWithNull(group, check.getGroup())) {
            check.setChecked(false);
          }
        }
      }
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    setChecked(checked, true);
    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), "menuitemcheckbox");
    }
    if (getGroup() != null) {
      setGroupStyle(groupStyle);
    }
  }

}

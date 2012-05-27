/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Allows any widget to be placed in a menu. As of 2.0, any component can be
 * added directly to a Menu therefore reducing the need for this class.
 * AdapterMenuItem allows arbitrary widgets to be added to Menu and also
 * configured as Items.
 * 
 * <p />
 * For example, when adding a ComboBox to a Menu, the Menu will be closed when a
 * item from the combo drop down list is clicked if the combo is added directly
 * to the Menu. To stop the menu from being closed, an AdapterMenuItem can be
 * used so that @link {@link #setHideOnClick(boolean)} can be called.
 */
public class AdapterMenuItem extends Item {

  /**
   * The wrapped widget.
   */
  protected Widget widget;

  protected boolean manageFocus = false;

  private boolean needsIndent = true;

  /**
   * Creates a new adapter.
   * 
   * @param widget the widget to be adapted
   */
  public AdapterMenuItem(Widget widget) {
    assert widget != null : "Widget may not be null";
    widget.removeFromParent();
    this.widget = widget;
    setParent(this, widget);
  }

  @Override
  public Element getElement() {
    // we need this because of lazy rendering
    return widget.getElement();
  }

  /**
   * Returns the wrapped widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  @Override
  public boolean isAttached() {
    if (widget != null) {
      return widget.isAttached();
    }
    return false;
  }

  /**
   * Returns true if the widget will be indented.
   * 
   * @return true if indented
   */
  public boolean isNeedsIndent() {
    return needsIndent;
  }

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the composite itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    widget.onBrowserEvent(event);
  }

  /**
   * True to indent the widget to account for the icon space (defaults to true).
   * 
   * @param needsIndent true to indent
   */
  public void setNeedsIndent(boolean needsIndent) {
    this.needsIndent = needsIndent;
  }

  @Override
  protected void onAttach() {
    ComponentHelper.doAttach(widget);
    DOM.setEventListener(getElement(), this);
    onLoad();
  }

  @Override
  protected void onClick(ComponentEvent be) {
    System.out.println("sdfdsf");
    if (widget instanceof Component) {
      ((Component) widget).focus();
    }
  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
    } finally {
      ComponentHelper.doDetach(widget);
    }
    onDetachHelper();
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    if (widget instanceof Component) {
      ((Component) widget).disable();
    }
  }

  /**
   * Returns true if the adapter manages focus for the wrapped widget.
   * 
   * @return true if focus being managed
   */
  public boolean isManageFocus() {
    return manageFocus;
  }

  /**
   * True to move focus to wrapped widget when the enter key is pressed and
   * remove focus of wrapped widget when escape is pressed (defaults to false).
   * 
   * @param manageFocus true to manage focus
   */
  public void setManageFocus(boolean manageFocus) {
    this.manageFocus = manageFocus;
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (widget instanceof Component) {
      ((Component) widget).enable();
    }
  }

  @Override
  protected boolean onEscape() {
    if (manageFocus) {
      focus();
      return false;
    }
    return super.onEscape();
  }

  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    if (widget instanceof Component) {
      ((Component) widget).render(target, index);
    }
    setElement(widget.getElement(), target, index);
  }

  private native void setParent(Widget parent, Widget child) /*-{
    child.@com.google.gwt.user.client.ui.Widget::parent = parent;
  }-*/;

}

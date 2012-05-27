/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.Container;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * A horizontal menu bar.
 */
public class MenuBar extends Container<MenuBarItem> {

  protected MenuBarItem active;
  private boolean autoSelect = true;
  private Listener<MenuEvent> listener;

  public MenuBar() {
    baseStyle = "x-menubar";
    enableLayout = true;
    attachChildren = false;

    listener = new Listener<MenuEvent>() {

      public void handleEvent(MenuEvent be) {
        EventType type = be.getType();
        if (type == Events.Hide) {
          autoSelect = false;
          focus();
          autoSelect = true;
          if (active != null) active.expanded = false;
        } else if (type == Events.AutoHide) {

          autoSelect = false;
          focus();
          autoSelect = true;
          if (active != null) active.expanded = false;
        } else if (type == Events.Maximize) {
          int index = indexOf(active);
          index = index != getItemCount() - 1 ? index + 1 : 0;
          MenuBarItem item = getItem(index);
          setActiveItem(item, true);
        } else if (type == Events.Minimize) {
          int index = indexOf(active);
          index = index > 0 ? index - 1 : getItemCount() - 1;
          MenuBarItem item = getItem(index);
          setActiveItem(item, true);
        }
      }
    };

  }

  @Override
  public boolean add(MenuBarItem item) {
    return super.add(item);
  }

  @Override
  public boolean insert(MenuBarItem item, int index) {
    return super.insert(item, index);
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);

    int type = ce.getEventTypeInt();
    switch (type) {
      case Event.ONMOUSEOVER:
        onMouseOver(ce);
        break;
      case Event.ONMOUSEOUT:
        onMouseOut(ce);
        break;
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONFOCUS:
        if (autoSelect && active == null && getItemCount() > 0) {
          setActiveItem(getItem(0), false);
        }
        break;
      case Event.ONBLUR:
        if (active != null && !active.expanded) {
          onDeactivate(active);
        }
        break;
    }
  }

  @Override
  public boolean remove(MenuBarItem item) {
    return super.remove(item);
  }

  /**
   * Sets the active item.
   * 
   * @param item the item to activate
   * @param expand true to expand the item's menu
   */
  public void setActiveItem(final MenuBarItem item, boolean expand) {
    if (active != item) {
      if (active != null) {
        onDeactivate(active);
      }
      onActivate(item);

      if (GXT.isFocusManagerEnabled()) {
        FocusFrame.get().frame(active);
      }

      if (expand) {
        expand(item, true);
      }
    }
  }

  /**
   * Toggles the given item.
   * 
   * @param item the item to toggle
   */
  public void toggle(MenuBarItem item) {
    if (item == active) {
      if (item.expanded) {
        collapse(item);
      } else {
        expand(item, false);
      }
    } else {
      setActiveItem(item, true);
    }
  }

  protected void collapse(MenuBarItem item) {
    item.menu.hide();
    item.expanded = false;
  }

  protected void expand(MenuBarItem item, boolean selectFirst) {
    item.menu.setFocusOnShow(false);
    item.menu.show(item.getElement(), "tl-bl", new int[] {0, 1});
    item.expanded = true;
    if (item.menu.getItemCount() > 0 && selectFirst) {
      item.menu.setActiveItem(item.menu.getItem(0), false);
    }
  }

  protected void onActivate(MenuBarItem item) {
    active = item;
    Accessibility.setState(getElement(), "aria-activedescendant", item.getId());
    item.addStyleName(item.getBaseStyle() + "-active");
    item.addStyleName(item.getBaseStyle() + "-over");
  }

  protected void onDeactivate(MenuBarItem item) {
    if (item.expanded) {
      item.menu.hide();
      item.expanded = false;
    }

    item.removeStyleName(item.getBaseStyle() + "-active");
    item.removeStyleName(item.getBaseStyle() + "-over");
    if (GXT.isFocusManagerEnabled()) {
      FocusFrame.get().unframe();
    }
    if (active == item) {
      active = null;
    }
  }

  @Override
  protected void onInsert(final MenuBarItem item, int index) {
    super.onInsert(item, index);

    item.menu.addListener(Events.Hide, listener);
    item.menu.addListener(Events.AutoHide, listener);
    item.menu.addListener(Events.Maximize, listener);
    item.menu.addListener(Events.Minimize, listener);
  }

  @Override
  protected void onRemove(MenuBarItem item) {
    super.onRemove(item);
    item.menu.removeListener(Events.Hide, listener);
    item.menu.removeListener(Events.AutoHide, listener);
    item.menu.removeListener(Events.Maximize, listener);
    item.menu.removeListener(Events.Minimize, listener);
  }

  protected void onKeyPress(ComponentEvent ce) {
    switch (ce.getKeyCode()) {
      case KeyCodes.KEY_DOWN:
        onDown(ce);
        break;
      case KeyCodes.KEY_LEFT:
        onLeft(ce);
        break;
      case KeyCodes.KEY_RIGHT:
        onRight(ce);
        break;
    }
  }

  protected void onLeft(ComponentEvent ce) {
    if (active != null && getItemCount() > 1) {
      int idx = indexOf(active);
      idx = idx != 0 ? idx - 1 : getItemCount() - 1;
      MenuBarItem item = getItem(idx);
      setActiveItem(item, item.expanded);
    }
  }

  protected void onMouseOut(ComponentEvent ce) {
    EventTarget eT = ce.getEvent().getRelatedEventTarget();
    if ((eT == null || (Element.is(eT) && findItem((Element) Element.as(eT)) == null)) && active != null && !active.expanded) {
      onDeactivate(active);
    }
  }

  protected void onMouseOver(ComponentEvent ce) {
    MenuBarItem item = findItem(ce.getTarget());
    if (item != null && item != active) {
      setActiveItem(item, active != null && active.expanded);
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    setElement(DOM.createDiv(), target, index);

    el().setTabIndex(-1);
    el().setElementAttribute("hideFocus", "true");

    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), Accessibility.ROLE_MENUBAR);
      Accessibility.setState(getElement(), "aria-hidden", "false");
    }

    new KeyNav<ComponentEvent>(this) {
      @Override
      public void onKeyPress(ComponentEvent ce) {
        MenuBar.this.onKeyPress(ce);
      }
    };

    layout();

    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONFOCUS | Event.ONBLUR);
  }

  @Override
  protected void onResize(int width, int height) {

  }

  protected void onRight(ComponentEvent ce) {
    if (active != null && getItemCount() > 1) {
      int idx = indexOf(active);
      idx = idx != getItemCount() - 1 ? idx + 1 : 0;
      MenuBarItem item = getItem(idx);
      setActiveItem(item, item.expanded);
    }
  }

  protected void onClick(ComponentEvent ce) {
    ce.stopEvent();
    MenuBarItem item = findItem(ce.getTarget());
    if (item != null) {
      toggle(item);
    }
  }

  protected void onDown(ComponentEvent ce) {
    if (active != null && getItemCount() > 0) {
      ce.stopEvent();
      if (active.expanded) {
        active.menu.focus();
        active.menu.setActiveItem(active.menu.getItem(0), false);
      } else {
        expand(active, true);
      }
    }
  }
}

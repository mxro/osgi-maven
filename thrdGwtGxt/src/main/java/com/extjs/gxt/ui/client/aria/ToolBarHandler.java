/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.ToolBarLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

public class ToolBarHandler extends FocusHandler {

  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    if (component.getParent() instanceof ToolBar) {
      return true;
    }
    return false;
  }

  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) return;

    boolean paging = component.getParent() instanceof PagingToolBar;
    if (paging) {
      PagingToolBar bar = (PagingToolBar) component.getParent();
      int idx = bar.indexOf(component);
      int fa = firstActive(bar);
      int la = lastActive(bar);
      if ((pe.isShiftKey() && idx > fa) || (!pe.isShiftKey() && idx < (la - 1))) {
        super.onTab(component, pe);
        return;
      }
    }
    Widget parent = component.getParent();
    if (pe.isShiftKey()) {
      if (focusPreviousWidget(parent)) {
        pe.stopEvent();
      }
    } else {
      if (focusNextWidget(parent)) {
        pe.stopEvent();
      }
    }
  }

  @Override
  public void onRight(Component component, PreviewEvent pe) {
    if (component.getParent() instanceof PagingToolBar) {
      return;
    }
    Component c = (Component) findNextWidget(component);
    if (c != null && c.getData("gxt-overflow") != null) {
      pe.stopEvent();
      ToolBar bar = (ToolBar) component.getParent();
      ToolBarLayout layout = bar.getLayout();
      Button more = layout.getMoreButton();
      more.focus();
      return;
    }

    if (!focusNextWidget(component)) {
      component.focus();
    }
  }

  @Override
  public void onLeft(Component component, PreviewEvent pe) {
    if (component.getParent() instanceof PagingToolBar) {
      return;
    }
    ToolBar bar = (ToolBar)component.getParent();
    ToolBarLayout layout = bar.getLayout();
    Button more = layout.getMoreButton();
    if (component == more) {
      for (int i = bar.getItemCount() - 1; i >= 0; i--) {
        Component c = bar.getItem(i);
        if (!isIgnore(c) && c.getData("gxt-overflow") == null) {
          focusWidget(c, false);
          return;
        }
      }
    }
    if (!focusPreviousWidget(component)) {
      component.focus();
    }
  }

  @Override
  public void onEscape(Component component, PreviewEvent pe) {
    stepOut(component.getParent());
  }

}

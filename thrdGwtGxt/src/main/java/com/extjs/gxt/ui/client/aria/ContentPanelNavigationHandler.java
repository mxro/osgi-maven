/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.CollapsePanel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentPanelNavigationHandler implements NavigationHandler {

  public List<Widget> getOrderedWidgets(Widget widget) {
    ContentPanel panel = null;
    if (widget instanceof HorizontalPanel) {
      panel = (ContentPanel)widget.getParent().getParent();
    } else if (widget.getParent() instanceof ContentPanel){
      panel = (ContentPanel) widget.getParent();
    } else {
      panel = (ContentPanel) widget;
    }

    List<Widget> widgets = new ArrayList<Widget>();

    Header header = panel.getHeader();
    for (int i = 0, len = header.getTools().size(); i < len; i++) {
      widgets.add(header.getTool(i));
    }

    if (panel.getTopComponent() != null) {
      widgets.add(panel.getTopComponent());
    }

    for (int i = 0, len = panel.getItemCount(); i < len; i++) {
      widgets.add(panel.getItem(i));
    }

    if (panel.getBottomComponent() != null) {
      widgets.add(panel.getBottomComponent());
    }

    if (panel.getButtonBar() != null && panel.getButtonBar().getItemCount() > 0 ) {
      widgets.add(panel.getButtonBar());
    }
    
    if (panel instanceof CollapsePanel) {
      widgets.add(((CollapsePanel)panel).getCollapseButton());
    }
    return widgets;
  }

  public boolean canHandleTabKey(Component comp) {
    if (comp instanceof HorizontalPanel) {
      return comp.getParent() instanceof Header;
    }
    return comp instanceof ContentPanel;
  }

}

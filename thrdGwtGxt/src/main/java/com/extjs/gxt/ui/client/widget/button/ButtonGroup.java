/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.button;

import com.extjs.gxt.ui.client.util.TextMetrics;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

/**
 * A specialized content panel for showing groups of buttons, typically used
 * with ToolBar.
 * 
 * @see ToolBar
 */
public class ButtonGroup extends ContentPanel {

  /**
   * Creates a new button group.
   * 
   * @param columns the number of columns
   */
  public ButtonGroup(int columns) {
    super();
    baseStyle = "x-btn-group";
    frame = true;
    setLayout(new TableLayout(columns));
  }

  @Override
  protected void onLayoutExcecuted(Layout layout) {
    super.onLayoutExcecuted(layout);
    int bodyWidth = body.getFrameWidth("lr") + body.firstChild().getWidth();
    if (bodyWidth > 0) { // display none
      TextMetrics.get().bind(head.el().selectNode("span"));
      int frameWidth = getFrameWidth();
      int tw = TextMetrics.get().getWidth(getHeading()) + frameWidth;
      if (tw > bodyWidth) {
        bodyWidth = tw;
      }
      body.setWidth(bodyWidth, true);
      el().setWidth(bodyWidth + frameWidth, true);
    }
  }

  @Override
  protected void onRender(Element parent, int pos) {
    super.onRender(parent, pos);
    if (getHeading() == null || (getHeading() != null && getHeading().equals("&#160;"))) {
      addStyleName("x-btn-group-notitle");
    }
  }
}

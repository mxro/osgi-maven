/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Layout;

/**
 * This layout positions and sizes the container's children in columns
 * horizontally. Each component may specify its width in pixels or as percentage
 * of the parent remaining width.
 * 
 * <p />
 * Child Widgets are:
 * <ul>
 * <li><b>Sized</b> : Yes - the width of a widget is adjusted by the ColumnData
 * hint</li>
 * <li><b>Positioned</b> : Yes - widgets are placed in columns</li>
 * </ul>
 * 
 * <p />
 * Code snippet:
 * 
 * <code><pre>
   LayoutContainer container = new LayoutContainer();
   container.setLayout(new ColumnLayout());
   container.add(new Button("100px"), new ColumnData(100));
   container.add(new Button("30%"), new ColumnData(.3));
   container.add(new Button("50px"), new ColumnData(50));
 * </pre></code>
 * 
 * @see ColumnData
 */
public class ColumnLayout extends Layout {

  protected El innerCt;
  protected boolean adjustForScroll = false;

  /**
   * Creates a new column layout.
   */
  public ColumnLayout() {
    setExtraStyle("x-column");
    monitorResize = true;
  }

  /**
   * Returns true if adjust for scroll is enabled.
   * 
   * @return the adjust for scroll state
   */
  public boolean isAdjustForScroll() {
    return adjustForScroll;
  }

  /**
   * True to adjust the container width calculations to account for the scroll
   * bar (defaults to false).
   * 
   * @param adjustForScroll the adjust for scroll state
   */
  public void setAdjustForScroll(boolean adjustForScroll) {
    this.adjustForScroll = adjustForScroll;
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    if (innerCt == null) {
      container.addStyleName("x-column-layout-ct");
      innerCt = target.createChild("<div class='x-column-inner'></div>");
      innerCt.createChild("<div class='x-clear'></div>");
    }

    renderAll(container, innerCt);

    Size size = target.getStyleSize();

    int w = size.width - (adjustForScroll ? XDOM.getScrollBarWidth() : 0);
    int pw = w;

    int count = container.getItemCount();

    // some columns can be percentages while others are fixed
    // so we need to make 2 passes
    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);

      ColumnData layoutData = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof ColumnData) {
        layoutData = (ColumnData) d;
      } else {
        layoutData = new ColumnData();
      }

      if (layoutData.getWidth() > 1) {
        pw -= layoutData.getWidth();
      }

      pw -= getSideMargins(c);
    }

    pw = pw < 0 ? 0 : pw;

    for (int i = 0; i < count; i++) {
      Component c = container.getItem(i);

      ColumnData layoutData = null;
      LayoutData d = getLayoutData(c);
      if (d != null && d instanceof ColumnData) {
        layoutData = (ColumnData) d;
      } else {
        layoutData = new ColumnData();
      }

      int width = -1;
      if (layoutData.getWidth() > 0 && layoutData.getWidth() <= 1) {
        width = (int) (layoutData.getWidth() * pw);
      } else {
        width = (int) layoutData.getWidth();
      }
      setSize(c, width, -1);
    }
  }
}

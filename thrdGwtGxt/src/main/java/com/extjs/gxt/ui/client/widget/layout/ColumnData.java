/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.Style;

public class ColumnData extends LayoutData {

  private double width = Style.DEFAULT;

  public ColumnData() {

  }

  public ColumnData(double width) {
    this.width = width;
  }

  /**
   * Sets the width of the column.
   * 
   * @param width the width, values <= 1 treated a percentages.
   */
  public void setWidth(double width) {
    this.width = width;
  }

  /**
   * Returns the width.
   * 
   * @return the wdith
   */
  public double getWidth() {
    return width;
  }

}

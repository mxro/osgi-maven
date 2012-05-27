/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

/**
 * Represents 4-side margins.
 */
public class Margins extends Region {

  /**
   * Creates a new margins instance with 0 values for all sides.
   */
  public Margins() {
    this(0);
  }

  /**
   * Creates a new margins instance.
   * 
   * @param margin the margin value for all 4 sides.
   */
  public Margins(int margin) {
    this(margin, margin, margin, margin);
  }

  /**
   * Creates a new margin instance.
   * 
   * @param top the top margin
   * @param right the right margin
   * @param bottom the bottom margin
   * @param left the left margin
   */
  public Margins(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

}

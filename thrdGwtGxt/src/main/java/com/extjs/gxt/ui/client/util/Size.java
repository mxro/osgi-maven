/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

/**
 * Instances of this class represent a rectangle's size.
 */
public class Size {
  /**
   * The width.
   */
  public int width;

  /**
   * The height.
   */
  public int height;

  /**
   * Creates a new size instance.
   * 
   * @param width the width
   * @param height the height
   */
  public Size(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public String toString() {
    return "height: " + height + ", width: " + width;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Size) {
      Size s = (Size) obj;
      if (width == s.width && height == s.height) {
        return true;
      }
      return false;
    }
    return super.equals(obj);
  }

}

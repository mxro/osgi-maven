/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.util.IconHelper;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Interface for objects that support icons. To create
 * <code>AbstractImagePrototypes</code> from image paths and CSS style names,
 * see @link {@link IconHelper}.
 * 
 * @see IconHelper
 */
public interface IconSupport {
  /**
   * Returns the icon.
   * 
   * @return the icon
   */
  public AbstractImagePrototype getIcon();

  /**
   * Sets the icon.
   * 
   * @param icon the icon
   */
  public void setIcon(AbstractImagePrototype icon);

  /**
   * Sets the icon style.
   * 
   * @param icon a CSS style name
   */
  public void setIconStyle(String icon);
}

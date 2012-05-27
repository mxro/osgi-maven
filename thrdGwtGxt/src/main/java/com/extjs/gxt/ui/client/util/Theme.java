/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

import java.util.Map;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.image.gray.GrayImages;
import com.google.gwt.core.client.GWT;

/**
 * A UI theme. Themes should be registered via the @link {@link ThemeManager} as
 * soon as the application module is loaded (onModuleLoad).
 * 
 * @see Theme#BLUE
 * @see Theme#GRAY
 */
public class Theme extends BaseModelData {

  /**
   * Default GXT blue theme.
   */
  public static Theme BLUE;

  /**
   * GXT gray theme (default path is 'gxt/css/gxt-gray.css').
   */
  public static Theme GRAY;

  static {
    BLUE = new Theme("blue", GXT.MESSAGES.themeSelector_blueTheme(), "gxt-all.css");
    GRAY = new Theme("gray", GXT.MESSAGES.themeSelector_grayTheme(), "gxt/css/gxt-gray.css")  {
      @Override
      public void init() {
        super.init();
        GXT.IMAGES = GWT.create(GrayImages.class);
      }
    };
  }

  protected Theme() {

  }

  /**
   * Creates a new theme.
   * 
   * @param id the theme id
   * @param name the theme name
   * @param file the CSS file
   */
  public Theme(String id, String name, String file) {
    set("id", id);
    set("name", name);
    set("file", file);
  }

  /**
   * Returns the theme id.
   * 
   * @return the theme id
   */
  public String getId() {
    return this.<String> get("id");
  }

  /**
   * Returns the theme name.
   * 
   * @return the theme name
   */
  public String getName() {
    return this.<String> get("name");
  }

  /**
   * Returns the theme's CSS file.
   * 
   * @return the file including the path
   */
  public String getFile() {
    return this.<String> get("file");
  }

  public Map<String, Object> asMap() {
    Map<String, Object> map = new FastMap<Object>();
    map.put("id", getId());
    map.put("file", getFile());
    return map;
  }

  /**
   * Well be called when theme is initialized. Subclasses should override to
   * perform any theme specific initialization.
   */
  public void init() {

  }

}

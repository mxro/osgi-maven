/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the libraries themes. The BLUE and GRAY themes are registered when
 * the application is initialized.
 */
public class ThemeManager {

  private static List<Theme> themes = new ArrayList<Theme>();

  static {
    register(Theme.BLUE);
    register(Theme.GRAY);
  }

  /**
   * Returns the theme with the given id.
   * 
   * @param id the theme id
   * @return the matching theme or null
   */
  public static Theme findTheme(String id) {
    for (Theme t : themes) {
      if (t.getId().equals(id)) {
        return t;
      }
    }
    return null;
  }

  /**
   * Returns the theme at the given index.
   * 
   * @param index the index
   * @return the theme
   */
  public static Theme getTheme(int index) {
    return themes.get(index);
  }

  /**
   * Returns all registered themes.
   * 
   * @return the themes
   */
  public static List<Theme> getThemes() {
    return new ArrayList<Theme>(themes);
  }

  /**
   * Registers a theme.
   * 
   * @param theme the theme to register.
   */
  public static void register(Theme theme) {
    themes.add(theme);
  }

  /**
   * Unregisters a theme.
   * 
   * @param theme the theme to unregister
   */
  public static void unregister(Theme theme) {
    themes.remove(theme);
  }
}

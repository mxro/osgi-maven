/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.themes.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface ThemeNames extends Messages {

  public static ThemeNames NAMES = (ThemeNames) GWT.create(ThemeNames.class);

  public String slate();
  
  public String access();

}

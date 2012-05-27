/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core;

import com.google.gwt.user.client.Element;

public interface Markup {
  public String getHtml();

  public Element getRootElement();

  public Element select(String selector);
}

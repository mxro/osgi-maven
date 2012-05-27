/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.image.gray;

import com.extjs.gxt.ui.client.image.XImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

@SuppressWarnings("deprecation")
public interface GrayImages extends XImages {

  @Resource("page-first.gif")
  AbstractImagePrototype paging_toolbar_first();

  @Resource("page-last.gif")
  AbstractImagePrototype paging_toolbar_last();

  @Resource("page-next.gif")
  AbstractImagePrototype paging_toolbar_next();
  
  @Resource("page-prev.gif")
  AbstractImagePrototype paging_toolbar_prev();

  @Resource("refresh.gif")
  AbstractImagePrototype paging_toolbar_refresh();
}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.themes.client.access.image;

import com.extjs.gxt.ui.client.image.XImages;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

@SuppressWarnings("deprecation")
public interface AccessImages extends XImages {
  
  @Resource("tree-collapsed.png")
  AbstractImagePrototype tree_collapsed();

  @Resource("tree-collapsed.png")
  AbstractImagePrototype tree_collapsed_over();

  @Resource("tree-expanded.png")
  AbstractImagePrototype tree_expanded();

  @Resource("tree-expanded.png")
  AbstractImagePrototype tree_expanded_over();
  
  @Resource("more.gif")
  AbstractImagePrototype toolbar_more();
  
  @Resource("tb-bold.gif")
  AbstractImagePrototype editor_bold();

  @Resource("tb-font-color.gif")
  AbstractImagePrototype editor_font_color();

  @Resource("tb-font-decrease.gif")
  AbstractImagePrototype editor_font_decrease();

  @Resource("tb-font-highlight.gif")
  AbstractImagePrototype editor_font_highlight();

  @Resource("tb-font-increase.gif")
  AbstractImagePrototype editor_font_increase();

  @Resource("tb-italic.gif")
  AbstractImagePrototype editor_italic();

  @Resource("tb-justify-center.gif")
  AbstractImagePrototype editor_justify_center();

  @Resource("tb-justify-left.gif")
  AbstractImagePrototype editor_justify_left();

  @Resource("tb-justify-right.gif")
  AbstractImagePrototype editor_justify_right();
  
  @Resource("tb-underline.gif")
  AbstractImagePrototype editor_underline();
  
  @Resource("group-checked.gif")
  AbstractImagePrototype group_checked();
  
  @Resource("page-prev.gif")
  AbstractImagePrototype paging_toolbar_prev();

  @Resource("page-prev-disabled.gif")
  AbstractImagePrototype paging_toolbar_prev_disabled();

  @Resource("page-next.gif")
  AbstractImagePrototype paging_toolbar_next();

  @Resource("page-next-disabled.gif")
  AbstractImagePrototype paging_toolbar_next_disabled();

  @Resource("page-first.gif")
  AbstractImagePrototype paging_toolbar_first();

  @Resource("page-first-disabled.gif")
  AbstractImagePrototype paging_toolbar_first_disabled();

  @Resource("page-last.gif")
  AbstractImagePrototype paging_toolbar_last();

  @Resource("page-last-disabled.gif")
  AbstractImagePrototype paging_toolbar_last_disabled();
  
  @Resource("refresh.gif")
  AbstractImagePrototype paging_toolbar_refresh();
}

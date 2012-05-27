/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * The collapse state of a content panel.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>ContentPanel BeforeExpand</dd>
 * <dd>ContentPanel Expand</dd>
 * <dd>ContentPanel BeforeCollapse</dd>
 * <dd>ContentPanel Collapse</dd>
 * <dd>ContentPanel BeforeClose</dd>
 * <dd>ContentPanel Close</dd>
 * <dd>LayoutContainer AfterLayout</dd>
 * <dd>ScrollContainer Scroll</dd>
 * <dd>Container BeforeAdd</dd>
 * <dd>Container Add</dd>
 * <dd>Container BeforeRemove</dd>
 * <dd>Container Remove</dd>
 * <dd>BoxComponent Move</dd>
 * <dd>BoxComponent Resize</dd>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 */
public class CollapsePanel extends ContentPanel {

  protected int[] adj;
  protected String align;
  protected ToolButton expandButton;
  protected ContentPanel panel;
  protected BorderLayoutData parentData;
  protected Popup popup;
  protected LayoutRegion region;
  private boolean expanded;
  private El headerEl;

  /**
   * Creates a new collapse panel.
   * 
   * @param panel the parent content panel
   * @param data the border layout data
   */
  public CollapsePanel(ContentPanel panel, BorderLayoutData data) {
    this.panel = panel;
    this.parentData = data;
    this.region = data.getRegion();
    this.collapse();
    setDeferHeight(false);
  }

  public ToolButton getCollapseButton() {
    return expandButton;
  }

  /**
   * Returns the panel's content panel.
   * 
   * @return the content panel
   */
  public ContentPanel getContentPanel() {
    return panel;
  }

  public int getFrameHeight() {
    return 0;
  }

  public int getFrameWidth() {
    return 0;
  }

  @Override
  public El getLayoutTarget() {
    return el();
  }

  public boolean isExpanded() {
    return expanded;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    if (!ce.within(expandButton.getElement())) {
      if (ce.getType().getEventCode() == Event.ONCLICK) {
        setExpanded(!expanded);
      }
    }
    switch (ce.getType().getEventCode()) {
      case Event.ONMOUSEOVER:
        addStyleName("x-layout-collapsed-over");
        break;
      case Event.ONMOUSEOUT:
        removeStyleName("x-layout-collapsed-over");
        break;
    }
  }

  public void setExpanded(boolean expanded) {
    ContentPanel panel = (ContentPanel) getData("panel");
    if (!this.expanded && expanded) {
      onShowPanel(panel);
    } else if (this.expanded && !expanded) {
      onHidePanel(panel);
    }
  }

  protected void afterHidePanel(ContentPanel panel) {

  }

  protected void afterShowPanel(ContentPanel panel) {

  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(expandButton);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(expandButton);
  }

  @Override
  protected Size getFrameSize() {
    return new Size(0, 0);
  }

  @Override
  protected void onExpand() {
    panel.getHeader().show();
  }

  protected void onExpandButton(BaseEvent be) {

  }

  protected void onHidePanel(ContentPanel panel) {
    this.expanded = false;
    if (popup != null) {
      panel.body.removeStyleName("x-panel-popup-body");
      panel.getHeader().show();
      popup.hide();
      panel.setStyleAttribute("margin", "0px");
      afterHidePanel(panel);

      SplitBar bar = (SplitBar) panel.getData("splitBar");
      if (bar != null) {
        bar.enable();
      }
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
    String text = null;

    LayoutRegion r = parentData.getRegion();

    if (r == LayoutRegion.NORTH || r == LayoutRegion.SOUTH) {
      text = panel.getHeader().getText();
    }
    headerEl = el().createChild(
        "<div class=\"x-panel-header\"><span class=\"x-panel-header-text\">"
            + (Util.isEmptyString(text) ? "&#160;" : text) + "</span></div>");

    String icon = null;
    adj = new int[] {0, 0};
    switch (parentData.getRegion()) {
      case WEST:
        icon = "right";
        align = "tl-tr";
        adj = new int[] {0, 25};
        break;
      case EAST:
        icon = "left";
        align = "tr-tl";
        adj = new int[] {0, 25};
        break;
      case NORTH:
        icon = "down";
        align = "tl-bl";
        break;
      case SOUTH:
        icon = "up";
        align = "bl-tl";
        break;
    }

    if (r == LayoutRegion.NORTH || region == LayoutRegion.SOUTH) {
      headerEl.setStyleAttribute("background", "none");
    } else {
      el().selectNode("span").hide();
    }

    headerEl.setStyleAttribute("cursor", "default");

    setStyleName("x-layout-collapsed");
    expandButton = new ToolButton("x-tool-" + icon);
    expandButton.render(headerEl.dom, 0);
    if (r == LayoutRegion.NORTH || r == LayoutRegion.SOUTH) {
      expandButton.setStyleAttribute("marginTop", "-2px");
    }

    expandButton.addListener(Events.Select, new Listener<ComponentEvent>() {
      public void handleEvent(ComponentEvent ce) {
        if (expanded) {
          setExpanded(false);
        }
        onExpandButton(ce);
      }
    });

    if (parentData.isFloatable()) {
      sinkEvents(Event.ONCLICK);
    }
    el().setVisibility(true);

    if (GXT.isFocusManagerEnabled()) {
      el().setTabIndex(0);
      el().setElementAttribute("hideFocus", "true");
    }

    sinkEvents(Event.MOUSEEVENTS | Event.FOCUSEVENTS);
  }

  protected void onShowPanel(final ContentPanel panel) {
    this.expanded = true;
    Rectangle box = getBounds(false);

    SplitBar bar = (SplitBar) panel.getData("splitBar");
    if (bar != null) {
      bar.disable();
    }
    if (popup == null) {
      popup = new Popup() {
        protected boolean onAutoHide(Event event) {
          setExpanded(false);
          return false;
        }
      };

      popup.getIgnoreList().add(expandButton.getElement());
      popup.getIgnoreList().add(getElement());
      popup.getIgnoreList().add(panel.getElement());
      popup.setStyleName("x-layout-popup");
      popup.setLayout(new FitLayout());
      popup.setShadow(true);
    }
    panel.setPosition(0, 0);
    panel.setBorders(false);
    panel.getHeader().hide();

    if (panel.isRendered()) {
      panel.body.addStyleName("x-panel-popup-body");
    } else {
      panel.addListener(Events.Render, new Listener<ComponentEvent>() {
        public void handleEvent(ComponentEvent be) {
          panel.body.addStyleName("x-panel-popup-body");
          panel.removeListener(Events.Render, this);
        }
      });
    }

    int w = 0;
    int h = 0;

    switch (region) {
      case WEST:
      case EAST:
        w = (int) parentData.getSize();
        h = box.height - 25;
        break;
      case NORTH:
      case SOUTH:
        w = box.width;
        h = (int) parentData.getSize();
    }

    // needed to enforce correct sizing
    panel.removeFromParent();
    popup.setSize(w, h);
    popup.show(getElement(), align, adj);
    popup.add(panel);
    popup.layout();

    afterShowPanel(panel);
  }
}

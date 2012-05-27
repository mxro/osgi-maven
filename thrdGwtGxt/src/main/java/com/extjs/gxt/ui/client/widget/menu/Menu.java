/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ClickRepeaterEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.ClickRepeater;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.layout.MenuLayout;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A menu component.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeShow</b> : MenuEvent(container)<br>
 * <div>Fires before this menu is displayed. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>container : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Show</b> : MenuEvent(container)<br>
 * <div>Fires after this menu is displayed.</div>
 * <ul>
 * <li>container : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeHide</b> : MenuEvent(container)<br>
 * <div>Fired before the menu is hidden. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>container : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Hide</b> : MenuEvent(container)<br>
 * <div>Fires after this menu is hidden.</div>
 * <ul>
 * <li>container : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeAdd</b> : MenuEvent(container, item, index)<br>
 * <div>Fires before a item is added or inserted. Listeners can cancel the
 * action by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>container : this</li>
 * <li>item : the item being added</li>
 * <li>index : the index at which the item will be added</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeRemove</b> : MenuEvent(container, item)<br>
 * <div>Fires before a item is removed. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>container : this</li>
 * <li>item : the item being removed</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Add</b> : MenuEvent(container, item, index)<br>
 * <div>Fires after a item has been added or inserted.</div>
 * <ul>
 * <li>container : this</li>
 * <li>item : the item that was added</li>
 * <li>index : the index at which the item will be added</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Remove</b> : MenuEvent(container, item)<br>
 * <div>Fires after a item has been removed.</div>
 * <ul>
 * <li>container : this</li>
 * <li>item : the item being removed</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
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
public class Menu extends Container<Component> {

  protected KeyNav<ComponentEvent> keyNav;
  protected Item parentItem;
  protected BaseEventPreview eventPreview;
  protected boolean plain;
  protected boolean showSeparator = true;
  protected El ul;
  protected Item activeItem;

  private String subMenuAlign = "tl-tr?";
  private String defaultAlign = "tl-bl?";

  private int minWidth = 120;
  private boolean showing;
  private boolean constrainViewport = true;
  private boolean focusOnShow = true;
  private int maxHeight = Style.DEFAULT;
  private boolean enableScrolling = true;
  private int scrollIncrement = 24;
  private int scrollerHeight = 8;
  private int activeMax;

  /**
   * Creates a new menu.
   */
  public Menu() {
    baseStyle = "x-menu";
    shim = true;
    monitorWindowResize = true;
    setShadow(true);
    setLayoutOnChange(true);

    enableLayout = true;
    setLayout(new MenuLayout());
    eventPreview = new BaseEventPreview() {

      @Override
      protected boolean onPreview(PreviewEvent pe) {
        Menu.this.onAutoHide(pe);
        return super.onPreview(pe);
      }

      @Override
      protected void onPreviewKeyPress(PreviewEvent pe) {
        super.onPreviewKeyPress(pe);
        onEscape(pe);
      }
    };
  }

  /**
   * Adds a item to the menu.
   * 
   * @param item the new item
   */
  @Override
  public boolean add(Component item) {
    return super.add(item);
  }

  /**
   * Returns the default alignment.
   * 
   * @return the default align
   */
  public String getDefaultAlign() {
    return defaultAlign;
  }

  @Override
  public El getLayoutTarget() {
    return ul;
  }

  /**
   * Returns the max height of the menu or -1 if not set.
   * 
   * @return the max height in pixels
   */
  public int getMaxHeight() {
    return maxHeight;
  }

  /**
   * Returns the menu's minimum width.
   * 
   * @return the width
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Returns the menu's parent item.
   * 
   * @return the parent item
   */
  public Item getParentItem() {
    return parentItem;
  }

  /**
   * Returns the sub menu alignment.
   * 
   * @return the alignment
   */
  public String getSubMenuAlign() {
    return subMenuAlign;
  }

  /**
   * Hides the menu.
   */
  public void hide() {
    hide(false);
  }

  /**
   * Hides this menu and optionally all parent menus
   * 
   * @param deep true to close all parent menus
   * @return this
   */
  public Menu hide(boolean deep) {
    if (showing) {
      MenuEvent me = new MenuEvent(this);
      if (fireEvent(Events.BeforeHide, me)) {
        if (activeItem != null) {
          activeItem.deactivate();
          activeItem = null;
        }
        onHide();
        RootPanel.get().remove(this);
        eventPreview.remove();
        showing = false;
        hidden = true;
        fireEvent(Events.Hide, me);
        if (deep && parentItem != null) {
          parentItem.parentMenu.hide(true);
        }
      }
    }
    return this;
  }

  /**
   * Inserts an item into the menu.
   * 
   * @param item the item to insert
   * @param index the insert location
   */
  @Override
  public boolean insert(Component item, int index) {
    if (item instanceof Item) {
      ((Item) item).parentMenu = this;
    }
    return super.insert(item, index);
  }

  /**
   * Returns true if constrain to viewport is enabled.
   * 
   * @return the constrain to viewport state
   */
  public boolean isConstrainViewport() {
    return constrainViewport;
  }

  /**
   * Returns true if vertical scrolling is enabled.
   * 
   * @return true for scrolling
   */
  public boolean isEnableScrolling() {
    return enableScrolling;
  }

  /**
   * Returns true if the menu will be focused when displayed.
   * 
   * @return true if focused
   */
  public boolean isFocusOnShow() {
    return focusOnShow;
  }

  @Override
  public boolean isVisible() {
    return showing;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    switch (ce.getEventTypeInt()) {
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONMOUSEMOVE:
        onMouseMove(ce);
        break;
      case Event.ONMOUSEOUT:
        onMouseOut(ce);
        break;
      case Event.ONMOUSEOVER:
        onMouseOver(ce);
        break;
      case Event.ONMOUSEWHEEL:
        if (enableScrolling) {
          scrollMenu(ce.getEvent().getMouseWheelVelocityY() < 0);
        }
    }
    El t = ce.getTargetEl();
    if (enableScrolling && t.is(".x-menu-scroller")) {
      switch (ce.getEventTypeInt()) {
        case Event.ONMOUSEOVER:
          // deactiveActiveItem();
          onScrollerIn(t);
          break;
        case Event.ONMOUSEOUT:
          onScrollerOut(t);
          break;
      }
    }
  }

  /**
   * Removes a item from the menu.
   * 
   * @param item the menu to remove
   */
  @Override
  public boolean remove(Component item) {
    if (activeItem == item) {
      deactiveActiveItem();
    }
    boolean success = super.remove(item);
    if (success && item instanceof Item) {
      ((Item) item).parentMenu = null;
    }
    return success;
  }

  /**
   * Sets the active item. The component must be of type <code>Item</code> to be
   * activated. All other types are ignored.
   * 
   * @param c the component to set active
   * @param autoExpand true to auto expand the item
   */
  public void setActiveItem(Component c, boolean autoExpand) {
    if (c == null) {
      deactiveActiveItem();
      return;
    }
    if (c instanceof Item) {
      Item item = (Item) c;
      if (item != activeItem) {
        deactiveActiveItem();

        this.activeItem = item;
        item.activate(autoExpand);
        item.el().scrollIntoView(ul.dom, false);
        focus();

        if (GXT.isFocusManagerEnabled()) {
          FocusFrame.get().frame(item);
          Accessibility.setState(getElement(), "aria-activedescendant", item.getId());
        }

      } else if (autoExpand) {
        item.expandMenu(autoExpand);
      }
    }
  }

  /**
   * Sets whether the menu should be constrained to the viewport when shown.
   * Only applies when using {@link #showAt(int, int)}.
   * 
   * @param constrainViewport true to constrain
   */
  public void setConstrainViewport(boolean constrainViewport) {
    this.constrainViewport = constrainViewport;
  }

  /**
   * Sets the default {@link El#alignTo} anchor position value for this menu
   * relative to its element of origin (defaults to "tl-bl?").
   * 
   * @param defaultAlign the default align
   */
  public void setDefaultAlign(String defaultAlign) {
    this.defaultAlign = defaultAlign;
  }

  /**
   * True to enable vertical scrolling of the children in the menu (defaults to
   * true).
   * 
   * @param enableScrolling true to for scrolling
   */
  public void setEnableScrolling(boolean enableScrolling) {
    this.enableScrolling = enableScrolling;
  }

  /**
   * True to set the focus on the menu when it is displayed.
   * 
   * @param focusOnShow true to focus
   */
  public void setFocusOnShow(boolean focusOnShow) {
    this.focusOnShow = focusOnShow;
  }

  /**
   * Sets the max height of the menu (defaults to -1). Only applies when
   * {@link #setEnableScrolling(boolean)} is set to true.
   * 
   * @param maxHeight the max height
   */
  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  /**
   * Sets he minimum width of the menu in pixels (defaults to 120).
   * 
   * @param minWidth the min width
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }

  /**
   * The {@link El#alignTo} anchor position value to use for submenus of this
   * menu (defaults to "tl-tr-?").
   * 
   * @param subMenuAlign the sub alignment
   */
  public void setSubMenuAlign(String subMenuAlign) {
    this.subMenuAlign = subMenuAlign;
  }

  /**
   * Displays this menu relative to another element.
   * 
   * @param elem the element to align to
   * @param pos the {@link El#alignTo} anchor position to use in aligning to the
   *          element (defaults to defaultAlign)
   */
  public void show(Element elem, String pos) {
    show(elem, pos, new int[] {0, 0});
  }

  /**
   * Displays this menu relative to another element.
   * 
   * @param elem the element to align to
   * @param pos the {@link El#alignTo} anchor position to use in aligning to the
   *          element (defaults to defaultAlign)
   * @param offsets the menu align offsets
   */
  public void show(Element elem, String pos, int[] offsets) {
    MenuEvent me = new MenuEvent(this);
    if (fireEvent(Events.BeforeShow, me)) {
      RootPanel.get().add(this);

      el().makePositionable(true);

      onShow();
      el().updateZIndex(0);

      showing = true;
      doAutoSize();

      el().alignTo(elem, pos, offsets);

      if (enableScrolling) {
        constrainScroll(el().getY());
      }
      el().show();

      eventPreview.add();

      if (focusOnShow) {
        focus();
      }

      fireEvent(Events.Show, me);
    }
  }

  /**
   * Displays this menu relative to the widget using the default alignment.
   * 
   * @param widget the align widget
   */
  public void show(Widget widget) {
    show(widget.getElement(), defaultAlign);
  }

  /**
   * Displays this menu at a specific xy position.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void showAt(int x, int y) {
    MenuEvent me = new MenuEvent(this);
    if (fireEvent(Events.BeforeShow, me)) {
      RootPanel.get().add(this);

      el().makePositionable(true);

      onShow();
      el().updateZIndex(0);

      showing = true;
      doAutoSize();

      if (constrainViewport) {
        Point p = el().adjustForConstraints(new Point(x, y));
        x = p.x;
        y = p.y;
      }
      setPagePosition(x + XDOM.getBodyScrollLeft(), y + XDOM.getBodyScrollTop());
      if (enableScrolling) {
        constrainScroll(y);
      }

      el().show();
      eventPreview.add();

      if (focusOnShow) {
        focus();
      }

      fireEvent(Events.Show, me);
    }
  }

  @Override
  protected void afterRender() {
    super.afterRender();

    keyNav = new KeyNav<ComponentEvent>(this) {
      public void onDown(ComponentEvent ce) {
        onKeyDown(ce);
      }

      public void onEnter(ComponentEvent be) {
        if (activeItem != null) {
          be.cancelBubble();
          activeItem.onClick(be);
        }
      }

      public void onLeft(ComponentEvent be) {
        hide();
        if (parentItem != null) {
          parentItem.parentMenu.focus();
          if (GXT.isFocusManagerEnabled()) {
            FocusFrame.get().frame(parentItem);
          }
        } else {
          Menu menu = Menu.this;
          while (menu.parentItem != null) {
            menu = menu.parentItem.parentMenu;
          }
          menu.fireEvent(Events.Minimize);
        }
      }

      public void onRight(ComponentEvent be) {
        if (activeItem != null) {
          activeItem.expandMenu(true);
        }
        if (activeItem instanceof MenuItem) {
          MenuItem mi = (MenuItem) activeItem;
          if (mi.subMenu != null && mi.subMenu.isVisible()) {
            return;
          }
        }
        Menu menu = Menu.this;
        while (menu.parentItem != null) {
          menu = menu.parentItem.parentMenu;
        }
        menu.fireEvent(Events.Maximize);
      }

      public void onUp(ComponentEvent ce) {
        onKeyUp(ce);
      }
    };
  }

  protected void constrainScroll(int y) {
    int full = ul.setHeight("auto").getHeight();

    int max = maxHeight != Style.DEFAULT ? maxHeight : (XDOM.getViewHeight(false) - y);
    if (full > max && max > 0) {
      activeMax = max - 10 - scrollerHeight * 2;
      ul.setHeight(activeMax, true);
      createScrollers();
    } else {
      ul.setHeight(full, true);
      NodeList<Element> nodes = el().select(".x-menu-scroller");
      for (int i = 0; i < nodes.getLength(); i++) {
        fly(nodes.getItem(i)).hide();
      }
    }
    ul.setScrollTop(0);
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new MenuEvent(this);
  }

  @Override
  protected ContainerEvent<Menu, Component> createContainerEvent(Component item) {
    return new MenuEvent(this, item);
  }

  protected void createScrollers() {
    if (el().select(".x-menu-scroller").getLength() == 0) {
      Listener<ClickRepeaterEvent> listener = new Listener<ClickRepeaterEvent>() {
        public void handleEvent(ClickRepeaterEvent be) {
          onScroll(be);
        }
      };

      El scroller;

      scroller = new El(DOM.createDiv());
      scroller.addStyleName("x-menu-scroller", "x-menu-scroller-top");
      scroller.setInnerHtml("&nbsp;");
      ClickRepeater cr = new ClickRepeater(scroller);
      cr.doAttach();
      cr.addListener(Events.OnClick, listener);
      addAttachable(cr);

      el().insertFirst(scroller.dom);

      scroller = new El(DOM.createDiv());
      scroller.addStyleName("x-menu-scroller", "x-menu-scroller-bottom");
      scroller.setInnerHtml("&nbsp;");
      cr = new ClickRepeater(scroller);
      cr.doAttach();
      cr.addListener(Events.OnClick, listener);
      addAttachable(cr);

      el().appendChild(scroller.dom);
    }
  }

  protected void deactiveActiveItem() {
    if (activeItem != null) {
      activeItem.deactivate();
      activeItem = null;
    }
    if (GXT.isFocusManagerEnabled()) {
      FocusFrame.get().unframe();
      Accessibility.setState(getElement(), "aria-activedescendant", "");
    }
  }

  protected void doAutoSize() {
    if (showing && width == null) {
      int width = getLayoutTarget().getWidth() + el().getFrameWidth("lr");
      el().setWidth(Math.max(width, minWidth), true);
    }
  }

  protected boolean onAutoHide(PreviewEvent pe) {
    if ((pe.getEventTypeInt() == Event.ONMOUSEDOWN || pe.getEventTypeInt() == Event.ONMOUSEWHEEL
        || pe.getEventTypeInt() == Event.ONSCROLL || pe.getEventTypeInt() == Event.ONKEYPRESS)
        && !(pe.within(getElement()) || (fly(pe.getTarget()).findParent(".x-ignore", -1) != null))) {
      MenuEvent me = new MenuEvent(this);
      me.setEvent(pe.getEvent());
      if (fireEvent(Events.AutoHide, me)) {
        hide(true);
        return true;
      }
    }
    return false;
  }

  protected void onClick(ComponentEvent ce) {
    Component item = findItem(ce.getTarget());
    if (item != null && item instanceof Item) {
      ((Item) item).onClick(ce);
    }
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (eventPreview != null) {
      eventPreview.remove();
    }
  }

  protected void onEscape(PreviewEvent pe) {
    if (pe.getKeyCode() == KeyCodes.KEY_ESCAPE) {
      if (activeItem != null && !activeItem.onEscape()) {
        return;
      }
      hide(false);
    }
  }

  @Override
  protected void onHide() {
    super.onHide();
    deactiveActiveItem();
  }

  @Override
  protected void onInsert(Component item, int index) {
    super.onInsert(item, index);
    if (rendered && GXT.isAriaEnabled() && item instanceof CheckMenuItem) {
      handleRadioGroups();
    }
  }

  protected void onKeyDown(ComponentEvent ce) {
    ce.stopEvent();
    if (tryActivate(indexOf(activeItem) + 1, 1) == null) {
      tryActivate(0, 1);
    }
  }

  protected void onKeyUp(ComponentEvent ce) {
    ce.stopEvent();
    if (tryActivate(indexOf(activeItem) - 1, -1) == null) {
      tryActivate(getItemCount() - 1, -1);
    }
  }

  @Override
  protected void onLayoutExcecuted(Layout layout) {
    super.onLayoutExcecuted(layout);
    doAutoSize();
  }

  protected void onMouseMove(ComponentEvent ce) {
    Component c = findItem(ce.getTarget());
    if (c != null && c instanceof Item) {
      Item item = (Item) c;
      if (activeItem != item && item.canActivate && item.isEnabled()) {
        setActiveItem(item, true);
      }
    }
  }

  protected void onMouseOut(ComponentEvent ce) {
    EventTarget to = ce.getEvent().getRelatedEventTarget();
    if (activeItem != null
        && (to == null || (Element.is(to) && !DOM.isOrHasChild(activeItem.getElement(), (Element) Element.as(to))))
        && activeItem.shouldDeactivate(ce)) {
      deactiveActiveItem();
    }
  }

  protected void onMouseOver(ComponentEvent ce) {
    EventTarget from = ce.getEvent().getRelatedEventTarget();
    if (from == null || (Element.is(from) && !DOM.isOrHasChild(getElement(), (Element) Element.as(from)))) {
      Component c = findItem(ce.getTarget());
      if (c != null && c instanceof Item) {
        Item item = (Item) c;
        if (activeItem != item && item.canActivate && item.isEnabled()) {
          setActiveItem(item, true);
        }
      }
    }
  }

  @Override
  protected void onRemove(Component item) {
    super.onRemove(item);
    if (rendered && GXT.isAriaEnabled() && item instanceof CheckMenuItem) {
      handleRadioGroups();
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
    el().makePositionable(true);
    super.onRender(target, index);

    ul = new El(DOM.createDiv());
    ul.addStyleName(baseStyle + "-list");

    getElement().appendChild(ul.dom);

    // add menu to ignore list
    eventPreview.getIgnoreList().add(getElement());

    el().setTabIndex(0);
    el().setElementAttribute("hideFocus", "true");

    el().addStyleName("x-ignore");
    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), "menu");
      Accessibility.setRole(ul.dom, "presentation");
      handleRadioGroups();
    }

    if (plain) {
      addStyleName("x-menu-plain");
    }
    if (!showSeparator) {
      addStyleName("x-menu-nosep");
    }

    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.KEYEVENTS | Event.ONMOUSEWHEEL);
  }

  protected void onScroll(ClickRepeaterEvent ce) {
    El target = ce.getEl();
    boolean top = target.is(".x-menu-scroller-top");
    scrollMenu(top);

    if (top ? ul.getScrollTop() <= 0 : ul.getScrollTop() + activeMax >= ul.dom.getPropertyInt("scrollHeight")) {
      onScrollerOut(target);
    }
  }

  protected void onScrollerIn(El t) {
    boolean top = t.is(".x-menu-scroller-top");
    if (top ? ul.getScrollTop() > 0 : ul.getScrollTop() + activeMax < ul.dom.getPropertyInt("scrollHeight")) {
      t.addStyleName("x-menu-item-active", "x-menu-scroller-active");
    }
  }

  protected void onScrollerOut(El t) {
    t.removeStyleName("x-menu-item-active", "x-menu-scroller-active");
  }

  @Override
  protected void onWindowResize(int width, int height) {
    hide(true);
  }

  protected void scrollMenu(boolean top) {
    ul.setScrollTop(ul.getScrollTop() + scrollIncrement * (top ? -1 : 1));
  }

  protected Item tryActivate(int start, int step) {
    for (int i = start, len = getItemCount(); i >= 0 && i < len; i += step) {
      Component c = getItem(i);
      if (c instanceof Item) {
        Item item = (Item) c;
        if (item.canActivate && item.isEnabled()) {
          setActiveItem(item, false);
          return item;
        }
      }
    }
    return null;
  }

  protected void clearGroups() {
    NodeList<Element> groups = el().select(".x-menu-radio-group");
    for (int i = 0; i < groups.getLength(); i++) {
      Element e = groups.getItem(i);
      El.fly(e).removeFromParent();
    }
  }

  private El getGroup(String groupName) {
    El g = el().selectNode("#" + getId() + "-" + groupName);
    if (g == null) {
      g = new El(DOM.createDiv());
      g.makePositionable(true);
      g.dom.setAttribute("role", "group");
      g.addStyleName(HideMode.OFFSETS.value());
      g.addStyleName("x-menu-radio-group");
      g.setId(getId() + "-" + groupName);
      el().appendChild(g.dom);
    }
    return g;
  }

  protected void handleRadioGroups() {
    clearGroups();
    for (int i = 0; i < getItemCount(); i++) {
      Object obj = getItem(i);
      if (obj instanceof CheckMenuItem) {
        CheckMenuItem check = (CheckMenuItem) obj;
        if (check.getGroup() != null) {
          El g = getGroup(check.getGroup());
          Accessibility.setState(g.dom, "aria-owns", g.dom.getAttribute("aria-owns") + " " + check.getId());
          if (check.getAriaGroupTitle() != null) {
            g.dom.setTitle(check.getAriaGroupTitle());
          }
        }
      }
    }
  }
}

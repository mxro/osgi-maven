/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.IconSupport;
import com.extjs.gxt.ui.client.widget.Layer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for all menu items that require menu-related functionality (like
 * sub-menus) and are not static display items. Item extends the base
 * functionality of {@link Item} by adding menu-specific activation and click
 * handling.
 */
public class MenuItem extends Item implements IconSupport {

  protected Menu subMenu;
  protected String itemStyle = "x-menu-item";
  protected AbstractImagePrototype icon;
  protected String text;
  protected Widget widget;

  /**
   * Creates a new item.
   */
  public MenuItem() {
    canActivate = true;
  }

  /**
   * Creates a new item with the given text.
   * 
   * @param text the item's text
   */
  public MenuItem(String text) {
    this();
    this.text = text;
  }

  /**
   * Creates a new item.
   * 
   * @param text the item's text
   * @param icon the item's icon
   */
  public MenuItem(String text, AbstractImagePrototype icon) {
    this(text);
    setIcon(icon);
  }

  /**
   * Creates a new item.
   * 
   * @param text the item's text
   * @param icon the item's icon
   * @param listener the selection listener
   */
  public MenuItem(String text, AbstractImagePrototype icon, SelectionListener<? extends MenuEvent> listener) {
    this(text, icon);
    addSelectionListener(listener);
  }

  /**
   * Creates a new item.
   * 
   * @param text the item text
   * @param listener the selection listener
   */
  public MenuItem(String text, SelectionListener<? extends MenuEvent> listener) {
    this(text);
    addSelectionListener(listener);
  }

  /**
   * Expands the item's sub menu.
   */
  public void expandMenu() {
    if (isEnabled() && subMenu != null) {
      subMenu.setFocusOnShow(true);
      subMenu.show(el().dom, "tl-tr?");
    }
  }

  /**
   * Returns the item's icon style.
   * 
   * @return the icon style
   */
  public AbstractImagePrototype getIcon() {
    return icon;
  }

  /**
   * Returns the item's sub menu.
   * 
   * @return the sub menu
   */
  public Menu getSubMenu() {
    return subMenu;
  }

  /**
   * Returns the item's text.
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the item's widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * Sets the item's icon style. The style name should match a CSS style that
   * specifies a background image using the following format:
   * 
   * <pre>
   * &lt;code&gt;
   * .my-icon {
   *    background: url(images/icons/my-icon.png) no-repeat center left !important;
   * }
   * &lt;/code&gt;
   * </pre>
   * 
   * @param icon the icon
   */
  public void setIcon(AbstractImagePrototype icon) {
    this.icon = icon;
    if (rendered) {
      El oldIcon = el().selectNode(".x-menu-item-icon");
      if (oldIcon != null) {
        oldIcon.remove();
      }
      if (icon != null) {
        Element e = icon.createElement().cast();
        El.fly(e).addStyleName("x-menu-item-icon");
        el().insertChild(e, 0);
      }
    }
    this.icon = icon;
  }

  public void setIconStyle(String icon) {
    setIcon(IconHelper.create(icon));
  }

  /**
   * Sets the item's sub menu.
   * 
   * @param menu the sub menu
   */
  public void setSubMenu(Menu menu) {
    this.subMenu = menu;
    menu.parentItem = this;
  }

  /**
   * Sets the item's text.
   * 
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
    if (rendered) {
      el().update(Util.isEmptyString(text) ? "&#160;" : text);
      setIcon(icon);
    }
  }

  /**
   * Sets the item's widget.
   * 
   * @param widget the widget
   */
  public void setWidget(Widget widget) {
    this.widget = widget;
    if (rendered) {
      if (widget instanceof Component) {
        Component c = (Component) widget;
        if (!c.isRendered()) {
          c.render(getElement());
          setIcon(icon);
          return;
        }
      }
      getElement().appendChild(widget.getElement());
      setIcon(icon);
    }
  }

  @Override
  protected void activate(boolean autoExpand) {
    super.activate(autoExpand);
    if (autoExpand && subMenu != null) {
      expandMenu();
    }
  }

  @Override
  protected void afterRender() {
    super.afterRender();
    if (text != null) setText(text);
  }

  @Override
  protected void deactivate() {
    super.deactivate();
    if (subMenu != null && subMenu.isVisible()) {
      subMenu.hide();
    }
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    if (widget != null) {
      ComponentHelper.doAttach(widget);
    }
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    if (widget != null) {
      ComponentHelper.doDetach(widget);
    }
  }

  @Override
  protected void expandMenu(boolean autoActivate) {
    if (!disabled && subMenu != null) {
      if (!subMenu.isVisible()) {
        expandMenu();
        subMenu.tryActivate(0, 1);
      }
    }
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    if (widget != null && widget instanceof Component) {
      ((Component) widget).disable();
    }
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (widget != null && widget instanceof Component) {
      ((Component) widget).enable();
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    setElement(DOM.createAnchor(), target, index);

    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), Accessibility.ROLE_MENUITEM);
    } else {
      getElement().setPropertyString("href", "#");
    }

    String s = itemStyle + (subMenu != null ? " x-menu-item-arrow" : "");
    addStyleName(s);

    if (widget != null) {
      setWidget(widget);
    } else {
      setText(text);
    }

    if (subMenu != null) {
      Accessibility.setState(getElement(), "aria-haspopup", "true");
    }
  }

  @Override
  protected boolean shouldDeactivate(ComponentEvent ce) {
    if (super.shouldDeactivate(ce)) {
      if (subMenu != null && subMenu.isVisible()) {
        Point xy = ce.getXY();
        xy.x += XDOM.getBodyScrollLeft();
        xy.y += XDOM.getBodyScrollTop();

        Rectangle rec = subMenu.el().getBounds();
        if ((subMenu.el() instanceof Layer)) {
          Layer l = (Layer) subMenu.el();
          if (l.isShim() && l.isShadow()) {
            return !rec.contains(xy) && !l.getShadow().getBounds().contains(xy)
                && !l.getShim().getBounds().contains(xy);
          } else if (l.isShadow()) {
            return !rec.contains(xy) && !l.getShadow().getBounds().contains(xy);
          } else if (l.isShim()) {
            return !rec.contains(xy) && !l.getShim().getBounds().contains(xy);
          }
        }

        return !rec.contains(xy);
      }
    }
    return true;
  }
}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid.filters;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseNumericFilterConfig;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Custom implementation of <code>Menu</code> that has preconfigured items for
 * LESSTHAN, GREATERTHAN, and EQUALs.
 * 
 */
public class RangeMenu extends Menu {

  enum RangeItem {
    EQUAL("eq"), GREATERTHAN("gt"), LESSTHAN("lt");

    private final String key;

    private RangeItem(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

  protected NumberField lt, gt, eq;

  private NumericFilter filter;
  private List<RangeItem> rangeItems = new ArrayList<RangeItem>();
  private DelayedTask updateTask = new DelayedTask(new Listener<BaseEvent>() {
    public void handleEvent(BaseEvent be) {
      fireUpdate();
    }
  });

  public RangeMenu(NumericFilter filter) {
    this.filter = filter;
    addListener(Events.BeforeHide, new Listener<MenuEvent>() {
      public void handleEvent(MenuEvent be) {
        // blur the field because of empty text
        if (lt != null && lt.isRendered()) {
          lt.el().firstChild().blur();
          blurField(lt);
        }
        if (gt != null && gt.isRendered()) {
          gt.el().firstChild().blur();
          blurField(gt);
        }
        if (eq != null && eq.isRendered()) {
          eq.el().firstChild().blur();
          blurField(eq);
        }
      }
    });
  }

  /**
   * Returns the menu's range items.
   * 
   * @return the range items
   */
  public List<RangeItem> getRangeItems() {
    return rangeItems;
  }

  /**
   * Returns the menu's value.
   * 
   * @return the value
   */
  public List<FilterConfig> getValue() {
    List<FilterConfig> configs = new ArrayList<FilterConfig>();
    if (eq != null && eq.getValue() != null && eq.isValid()) {
      FilterConfig config = new BaseNumericFilterConfig("numeric", "eq", eq.getValue());
      configs.add(config);
    }
    if (lt != null && lt.getValue() != null && lt.isValid()) {
      FilterConfig config = new BaseNumericFilterConfig("numeric", "lt", lt.getValue());
      configs.add(config);
    }

    if (gt != null && gt.getValue() != null && gt.isValid()) {
      FilterConfig config = new BaseNumericFilterConfig("numeric", "gt", gt.getValue());
      configs.add(config);
    }
    return configs;
  }

  public void setEmptyText(String emptyText) {
    if (lt != null) {
      lt.setEmptyText(emptyText);
    }
    if (gt != null) {
      gt.setEmptyText(emptyText);
    }
    if (eq != null) {
      eq.setEmptyText(emptyText);
    }
  }

  public void setFieldWidth(int width) {
    if (lt != null) {
      lt.setWidth(width);
    }
    if (gt != null) {
      gt.setWidth(width);
    }
    if (eq != null) {
      eq.setWidth(width);
    }

  }

  /**
   * Sets the menu's range items (defaults to EQUAL, GREATERTHAN, LESSTHAN).
   * 
   * @param rangeItems the range items
   */
  public void setRangeItems(List<RangeItem> rangeItems) {
    this.rangeItems = rangeItems;
    removeAll();
    AbstractImagePrototype icon = null;
    for (RangeItem item : rangeItems) {
      NumberField field = new NumberField() {
        @Override
        protected void onKeyUp(FieldEvent fe) {
          super.onKeyUp(fe);
          RangeMenu.this.onFilterKeyUp(fe);
        }
      };
      field.setEmptyText(filter.getMessages().getEmptyText());
      field.setWidth(filter.getWidth());

      switch (item) {
        case LESSTHAN:
          icon = GXT.IMAGES.grid_filter_lessThan();
          lt = field;
          break;
        case GREATERTHAN:
          icon = GXT.IMAGES.grid_filter_greaterThan();
          gt = field;
          break;
        case EQUAL:
          icon = GXT.IMAGES.grid_filter_equal();
          eq = field;
          break;
      }

      MenuItem menuItem = new MenuItem();
      menuItem.setCanActivate(false);
      menuItem.setHideOnClick(false);
      menuItem.setIcon(icon);

      menuItem.setWidget(field);

      add(menuItem);
    }
    layout();
  }

  /**
   * Sets the menu's values
   * 
   * @param values the values
   */
  public void setValue(List<FilterConfig> values) {
    for (FilterConfig config : values) {
      String c = config.getComparison();
      if ("eq".equals(c)) {
        eq.setValue((Number) config.getValue());
      } else if ("lt".equals(c)) {
        lt.setValue((Number) config.getValue());
      } else if ("gt".equals(c)) {
        gt.setValue((Number) config.getValue());
      }
    }
  }

  protected void onFilterKeyUp(FieldEvent fe) {
    int kc = fe.getKeyCode();
    if (kc == KeyCodes.KEY_ENTER && fe.getField().isValid()) {
      fe.stopEvent();
      hide(true);
      return;
    }
    if (fe.getField() == eq) {
      if (lt != null && lt.getValue() != null) {
        lt.setValue(null);
      }
      if (gt != null && gt.getValue() != null) {
        gt.setValue(null);
      }
    } else {
      eq.setValue(null);
    }
    updateTask.delay(filter.getUpdateBuffer());
  }

  private native void blurField(Field<?> f) /*-{
    f.@com.extjs.gxt.ui.client.widget.form.Field::onBlur(Lcom/extjs/gxt/ui/client/event/ComponentEvent;)(null)
  }-*/;

  protected void fireUpdate() {
    filter.fireUpdate();
  }
}

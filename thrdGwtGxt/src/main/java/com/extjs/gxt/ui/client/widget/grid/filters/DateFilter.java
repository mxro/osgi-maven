/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid.filters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseDateFilterConfig;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.filters.RangeMenu.RangeItem;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;

/**
 * Date filter type to displays a before, after, and on date picker.
 */
public class DateFilter extends Filter {

  public static class DateFilterMessages extends FilterMessages {
    private String afterText = GXT.MESSAGES.dateFilter_afterText();
    private String beforeText = GXT.MESSAGES.dateFilter_beforeText();
    private String onText = GXT.MESSAGES.dateFilter_onText();

    /**
     * @return the afterText
     */
    public String getAfterText() {
      return afterText;
    }

    /**
     * @return the beforeText
     */
    public String getBeforeText() {
      return beforeText;
    }

    /**
     * @return the onText
     */
    public String getOnText() {
      return onText;
    }

    /**
     * @param afterText the afterText to set
     */
    public void setAfterText(String afterText) {
      this.afterText = afterText;
    }

    /**
     * @param beforeText the beforeText to set
     */
    public void setBeforeText(String beforeText) {
      this.beforeText = beforeText;
    }

    /**
     * @param onText the onText to set
     */
    public void setOnText(String onText) {
      this.onText = onText;
    }
  }

  private CheckMenuItem beforeItem, afterItem, onItem;
  private DateMenu beforeMenu, afterMenu, onMenu;
  private Date maxDate;
  private Listener<MenuEvent> menuListener = new Listener<MenuEvent>() {
    public void handleEvent(MenuEvent be) {
      if (be.getType() == Events.CheckChange) {
        onCheckChange(be);
      } else if (be.getType() == Events.Select) {
        onMenuSelect(be);
      }
    }
  };
  private Date minDate;
  private List<RangeItem> rangeItems = new ArrayList<RangeItem>();

  /**
   * Creates a new date filter.
   * 
   * @param dataIndex the date index the filter is mapped to
   */
  public DateFilter(String dataIndex) {
    super(dataIndex);
    rangeItems.add(RangeItem.LESSTHAN);
    rangeItems.add(RangeItem.GREATERTHAN);
    rangeItems.add(RangeItem.EQUAL);

    menu = new Menu();
    beforeItem = new CheckMenuItem();
    beforeItem.addListener(Events.CheckChange, menuListener);

    beforeMenu = new DateMenu();
    beforeMenu.addListener(Events.Select, menuListener);
    beforeItem.setSubMenu(beforeMenu);
    menu.add(beforeItem);

    afterItem = new CheckMenuItem();
    afterItem.addListener(Events.CheckChange, menuListener);
    afterMenu = new DateMenu();
    afterMenu.addListener(Events.Select, menuListener);
    afterItem.setSubMenu(afterMenu);
    menu.add(afterItem);

    menu.add(new SeparatorMenuItem());

    onItem = new CheckMenuItem();
    onItem.addListener(Events.CheckChange, menuListener);
    onMenu = new DateMenu();
    onMenu.addListener(Events.Select, menuListener);
    onItem.setSubMenu(onMenu);
    menu.add(onItem);

    setMessages(new DateFilterMessages());
  }

  /**
   * Returns the max date.
   * 
   * @return the max date
   */
  public Date getMaxDate() {
    return maxDate;
  }

  @Override
  public DateFilterMessages getMessages() {
    return (DateFilterMessages) super.getMessages();
  }

  /**
   * Returns the minimum date.
   * 
   * @return the minimum date
   */
  public Date getMinDate() {
    return minDate;
  }

  @Override
  public List<FilterConfig> getSerialArgs() {
    List<FilterConfig> configs = new ArrayList<FilterConfig>();
    if (beforeItem != null && beforeItem.isChecked()) {
      FilterConfig c = new BaseDateFilterConfig("date", "before", beforeMenu.getDate());
      configs.add(c);
    }
    if (afterItem != null && afterItem.isChecked()) {
      FilterConfig c = new BaseDateFilterConfig("date", "after", afterMenu.getDate());
      configs.add(c);
    }
    if (onItem != null && onItem.isChecked()) {
      FilterConfig c = new BaseDateFilterConfig("date", "on", onMenu.getDate());
      configs.add(c);
    }
    return configs;
  }

  @Override
  public Object getValue() {
    return getSerialArgs();
  }

  @Override
  public boolean isActivatable() {
    if (beforeItem != null && beforeItem.isChecked()) {
      return true;
    }
    if (afterItem != null && afterItem.isChecked()) {
      return true;
    }
    if (onItem != null && onItem.isChecked()) {
      return true;
    }
    return false;
  }

  /**
   * Sets the max date as passed to the date picker.
   * 
   * @param maxDate the max date
   */
  public void setMaxDate(Date maxDate) {
    this.maxDate = maxDate;
  }

  @Override
  public void setMessages(FilterMessages messages) {
    super.setMessages(messages);
    if (onItem != null) {
      onItem.setText(getMessages().getOnText());
    }
    if (afterItem != null) {
      afterItem.setText(getMessages().getAfterText());
    }
    if (beforeItem != null) {
      beforeItem.setText(getMessages().getBeforeText());
    }
  }

  /**
   * Set's the minimum date as passed to the date picker.
   * 
   * @param minDate the minimum date
   */
  public void setMinDate(Date minDate) {
    this.minDate = minDate;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void setValue(Object value) {
    List<FilterConfig> values = (List) value;
    for (FilterConfig config : values) {
      String comp = config.getComparison();
      if ("before".equals(comp)) {
        beforeItem.setChecked(true);
        beforeMenu.setDate((Date) config.getValue());
      } else if ("after".equals(comp)) {
        afterItem.setChecked(true);
        afterMenu.setDate((Date) config.getValue());
      } else if ("on".equals(comp)) {
        onItem.setChecked(true);
        onMenu.setDate((Date) config.getValue());
      }
    }
  }

  @Override
  public boolean validateModel(ModelData model) {
    Date d = getModelValue(model);

    long time = d == null ? 0l : new DateWrapper(d).clearTime().getTime();
    if (beforeItem.isChecked() && beforeMenu.getDate() != null) {
      long pvalue = new DateWrapper(beforeMenu.getDate()).clearTime().getTime();
      if (d == null || pvalue <= time) {
        return false;
      }
    }
    if (afterItem.isChecked() && afterMenu.getDate() != null) {
      long pvalue = new DateWrapper(afterMenu.getDate()).clearTime().getTime();
      if (d == null || pvalue >= time) {
        return false;
      }
    }
    if (onItem.isChecked() && onMenu.getDate() != null) {
      long pvalue = new DateWrapper(onMenu.getDate()).resetTime().getTime();
      if (d == null || pvalue != (d == null ? 0l : new DateWrapper(d).resetTime().getTime())) {
        return false;
      }
    }
    return true;
  }

  protected void onCheckChange(MenuEvent be) {
    updateMenuState(be.getItem(), be.isChecked());
    fireUpdate();
  }

  protected void onMenuSelect(MenuEvent be) {
    DateMenu menu = (DateMenu) be.getMenu();
    if (menu == beforeMenu) {
      updateMenuState(beforeItem, true);
    } else if (menu == afterMenu) {
      updateMenuState(afterItem, true);
    } else if (menu == onMenu) {
      updateMenuState(onItem, true);
    }
    menu.hide(true);
    fireUpdate();
  }

  protected void updateMenuState(Component item, boolean isChecked) {

    if (item == onItem) {
      onItem.setChecked(isChecked, true);
      if (isChecked) {
        beforeItem.setChecked(false, true);
        afterItem.setChecked(false, true);
      }
    } else if (item == afterItem) {
      afterItem.setChecked(isChecked, true);
      if (isChecked) {
        beforeItem.setChecked(beforeMenu.getDate() != null && beforeMenu.getDate().after(afterMenu.getDate()), true);
        onItem.setChecked(false, true);
      }
    } else if (item == beforeItem) {
      beforeItem.setChecked(isChecked, true);
      if (isChecked) {
        onItem.setChecked(false, true);
        afterItem.setChecked(afterMenu.getDate() != null && afterMenu.getDate().before(beforeMenu.getDate()), true);
      }
    }

  }

}

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
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.filters.RangeMenu.RangeItem;

public class NumericFilter extends Filter {
  public static class NumericFilterMessages extends FilterMessages {
    private String emptyText = GXT.MESSAGES.numericFilter_emptyText();

    /**
     * Returns the field's empty text.
     * 
     * @return the empty text
     */
    public String getEmptyText() {
      return emptyText;
    }

    /**
     * Sets the field empty text (defaults to 'Enter filter text...').
     * 
     * @param emptyText the empty text
     */
    public void setEmptyText(String emptyText) {
      this.emptyText = emptyText;
    }
  }

  private List<RangeItem> rangeItems = new ArrayList<RangeItem>();

  private RangeMenu rangeMenu;
  private int width = 125;

  public NumericFilter(String dataIndex) {
    super(dataIndex);
    setMessages(new NumericFilterMessages());
    rangeItems.add(RangeItem.LESSTHAN);
    rangeItems.add(RangeItem.GREATERTHAN);
    rangeItems.add(RangeItem.EQUAL);

    menu = new RangeMenu(this);
    rangeMenu = (RangeMenu) menu;
    rangeMenu.setRangeItems(rangeItems);
    setWidth(getWidth());
  }

  @Override
  public NumericFilterMessages getMessages() {
    return (NumericFilterMessages) super.getMessages();
  }

  @Override
  public List<FilterConfig> getSerialArgs() {
    return getValue();
  }

  @Override
  public List<FilterConfig> getValue() {
    return rangeMenu.getValue();
  }

  /**
   * Returns the field width.
   * 
   * @return the field width
   */
  public int getWidth() {
    return width;
  }

  @Override
  public boolean isActivatable() {
    if (rangeMenu.eq != null && rangeMenu.eq.getValue() != null) {
      return true;
    }
    if (rangeMenu.lt != null && rangeMenu.lt.getValue() != null) {
      return true;
    }
    if (rangeMenu.gt != null && rangeMenu.gt.getValue() != null) {
      return true;
    }
    return false;
  }

  @Override
  public void setMessages(FilterMessages messages) {
    super.setMessages(messages);
    if (rangeMenu != null) {
      rangeMenu.setEmptyText(getMessages().getEmptyText());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void setValue(Object value) {
    rangeMenu.setValue((List) value);
    fireUpdate();
  }

  /**
   * Sets the field width (defaults to 125).
   * 
   * @param width the field width
   */
  public void setWidth(int width) {
    this.width = width;
    rangeMenu.setFieldWidth(width);
  }

  @Override
  public boolean validateModel(ModelData model) {
    Number val = getModelValue(model);
    if (rangeMenu.eq != null && rangeMenu.eq.getValue() != null) {
      if (val == null || rangeMenu.eq.getValue().doubleValue() != val.doubleValue()) {
        return false;
      }
    }

    if (rangeMenu.lt != null && rangeMenu.lt.getValue() != null) {
      if (val == null || rangeMenu.lt.getValue().doubleValue() <= val.doubleValue()) {
        return false;
      }
    }

    if (rangeMenu.gt != null && rangeMenu.gt.getValue() != null) {
      if (val == null || rangeMenu.gt.getValue().doubleValue() >= val.doubleValue()) {
        return false;
      }
    }
    return true;
  }

}

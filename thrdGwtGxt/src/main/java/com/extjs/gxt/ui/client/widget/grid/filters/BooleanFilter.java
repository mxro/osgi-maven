/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid.filters;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseBooleanFilterConfig;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;

/**
 * Boolean filter that displays yes / no radio items.
 */
public class BooleanFilter extends Filter {
  public static class BooleanFilterMessages extends FilterMessages {
    private String noText = GXT.MESSAGES.booleanFilter_noText();
    private String yesText = GXT.MESSAGES.booleanFilter_yesText();

    /**
     * @return the noText
     */
    public String getNoText() {
      return noText;
    }

    /**
     * @return the yesText
     */
    public String getYesText() {
      return yesText;
    }

    /**
     * @param noText the noText to set
     */
    public void setNoText(String noText) {
      this.noText = noText;
    }

    /**
     * @param yesText the yesText to set
     */
    public void setYesText(String yesText) {
      this.yesText = yesText;
    }
  }

  private Listener<MenuEvent> listener = new Listener<MenuEvent>() {
    public void handleEvent(MenuEvent be) {
      fireUpdate();
    }
  };

  private CheckMenuItem yesItem, noItem;

  /**
   * Creates a new boolean filter index.
   * 
   * @param dataIndex the data index the filter is mapped to
   */
  public BooleanFilter(String dataIndex) {
    super(dataIndex);
    yesItem = new CheckMenuItem();
    yesItem.setGroup(XDOM.getUniqueId());
    yesItem.addListener(Events.CheckChange, listener);
    yesItem.setChecked(false);

    noItem = new CheckMenuItem();
    noItem.setGroup(yesItem.getGroup());
    noItem.addListener(Events.CheckChange, listener);
    noItem.setChecked(true);

    menu.add(yesItem);
    menu.add(noItem);

    setMessages(new BooleanFilterMessages());
  }

  @Override
  public BooleanFilterMessages getMessages() {
    return (BooleanFilterMessages) super.getMessages();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<FilterConfig> getSerialArgs() {
    FilterConfig config = new BaseBooleanFilterConfig();
    config.setType("boolean");
    config.setValue(getValue());
    return Util.createList(config);
  }

  @Override
  public Object getValue() {
    return Boolean.valueOf(yesItem.isChecked());
  }

  @Override
  public void setMessages(FilterMessages messages) {
    super.setMessages(messages);
    if (yesItem != null) {
      yesItem.setText(getMessages().getYesText());
    }
    if (noItem != null) {
      noItem.setText(getMessages().getNoText());
    }
  }

  @Override
  public void setValue(Object value) {
    boolean yes = false;
    if (value instanceof Boolean) {
      yes = ((Boolean) value).booleanValue();
    } else if (value instanceof String) {
      yes = Boolean.valueOf((String) value);
    }
    yesItem.setChecked(yes, false);
    noItem.setChecked(!yes, false);
    fireUpdate();
  }

  @Override
  public boolean validateModel(ModelData model) {
    Boolean val = getModelValue(model);
    return getValue().equals(val == null ? Boolean.FALSE : val);
  }

}

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
import com.extjs.gxt.ui.client.data.BaseFilterConfig;
import com.extjs.gxt.ui.client.data.BaseStringFilterConfig;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;

public class StringFilter extends Filter {

  public static class StringFilterMessages extends FilterMessages {
    private String emptyText = GXT.MESSAGES.stringFilter_emptyText();

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

  private TextField<String> field;

  private DelayedTask updateTask = new DelayedTask(new Listener<BaseEvent>() {
    public void handleEvent(BaseEvent be) {
      fireUpdate();
    }
  });
  private int width = 125;

  public StringFilter(String dataIndex) {
    super(dataIndex);

    field = new TextField<String>() {
      @Override
      protected void onKeyUp(FieldEvent fe) {
        super.onKeyUp(fe);
        onFieldKeyUp(fe);
      }
    };
    setWidth(getWidth());
    menu.add(field);
    menu.addListener(Events.BeforeHide, new Listener<MenuEvent>() {
      public void handleEvent(MenuEvent be) {
        // blur the field because of empty text
        field.el().firstChild().blur();
        blurField(field);
      }
    });
    setMessages(new StringFilterMessages());
  }

  @Override
  public StringFilterMessages getMessages() {
    return (StringFilterMessages) super.getMessages();
  }

  @Override
  public List<FilterConfig> getSerialArgs() {
    List<FilterConfig> configs = new ArrayList<FilterConfig>();
    BaseFilterConfig c = new BaseStringFilterConfig("string", getValue());
    configs.add(c);
    return configs;
  }

  @Override
  public Object getValue() {
    return field.getValue();
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
    return field.getValue() != null && field.getValue().length() > 0;
  }

  @Override
  public void setMessages(FilterMessages messages) {
    super.setMessages(messages);
    if (field != null) {
      field.setEmptyText(getMessages().getEmptyText());
    }
  }

  @Override
  public void setValue(Object value) {
    field.setValue((String) value);
    fireUpdate();
  }

  /**
   * Sets the field width (defaults to 125).
   * 
   * @param width the field width
   */
  public void setWidth(int width) {
    this.width = width;
    field.setWidth(width);
  }

  @Override
  public boolean validateModel(ModelData model) {
    String val = getModelValue(model);
    Object value = getValue();
    String v = value == null ? "" : value.toString();
    if (v.length() == 0 && (val == null || val.length() == 0)) {
      return true;
    } else if (val == null) {
      return false;
    } else {
      return val.toLowerCase().indexOf(v.toLowerCase()) > -1;
    }
  }

  protected void onFieldKeyUp(FieldEvent fe) {
    int key = fe.getKeyCode();
    if (key == KeyCodes.KEY_ENTER && field.isValid()) {
      fe.stopEvent();
      menu.hide(true);
      return;
    }
    updateTask.delay(getUpdateBuffer());
  }

  private native void blurField(Field<?> f) /*-{
    f.@com.extjs.gxt.ui.client.widget.form.Field::onBlur(Lcom/extjs/gxt/ui/client/event/ComponentEvent;)(null)
  }-*/;

}

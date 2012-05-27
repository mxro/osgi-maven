/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid.filters;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FilterEvent;
import com.extjs.gxt.ui.client.widget.menu.Menu;

/**
 * Abstract base class for filter implementations.
 */
public abstract class Filter extends BaseObservable {

  public static class FilterMessages {

  }

  protected String dataIndex;

  protected Menu menu;

  private boolean active = false;

  private FilterMessages msgs;
  private int updateBuffer = 500;

  /**
   * Creates a new filter instance.
   * 
   * @param dataIndex the data index the filter is mapped to
   */
  public Filter(String dataIndex) {
    this.dataIndex = dataIndex;
    menu = new Menu();
  }

  /**
   * Returns the filter's data index.
   * 
   * @return the data index
   */
  public String getDataIndex() {
    return dataIndex;
  }

  /**
   * Returns the filter's menu.
   * 
   * @return the menu
   */
  public Menu getMenu() {
    return menu;
  }

  public FilterMessages getMessages() {
    return msgs;
  }

  /**
   * Template method to be implemented by all subclasses that is to get and
   * return serialized filter data for transmission to the server.
   */
  public abstract List<FilterConfig> getSerialArgs();

  /**
   * Returns the update buffer.
   * 
   * @return the update buffer in milliseconds
   */
  public int getUpdateBuffer() {
    return updateBuffer;
  }

  /**
   * Template method to be implemented by all subclasses that is to get and
   * return the value of the filter.
   */
  public abstract Object getValue();

  /**
   * Template method to be implemented by all subclasses that is to return
   * <code>true</code> if the filter has enough configuration information to be
   * activated.
   * 
   * @return true if if the filter has enough configuration information to be
   *         activated
   */
  public boolean isActivatable() {
    return true;
  };

  /**
   * Returns true if the filter is active.
   * 
   * @return the active state
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns the serialized filter data for transmission to the server and fires
   * the 'Serialize' event.
   * 
   * @return the key value pairs representing the current configuration of the
   *         filter
   */
  public List<FilterConfig> serialize() {
    List<FilterConfig> args = getSerialArgs();
    fireEvent(Events.Serialize, new FilterEvent(this, args));
    return args;
  }

  /**
   * Sets the status of the filter and fires the appropriate events. You can
   * only set it to active if the filter is activatable.
   * 
   * @param active the new filter state
   * @param supressEvent true to prevent events from being fired
   */
  public void setActive(boolean active, boolean supressEvent) {
    active = active && isActivatable();
    if (this.active != active) {
      this.active = active;
      if (!supressEvent) {
        fireEvent(active ? Events.Activate : Events.Deactivate, new FilterEvent(this));
      }
    }
  }

  public void setMessages(FilterMessages messages) {
    msgs = messages;
  }

  /**
   * Number of milliseconds to wait after user interaction to fire an update
   * (defaults to 500).
   * 
   * @param updateBuffer the update buffer in milliseconds
   */
  public void setUpdateBuffer(int updateBuffer) {
    this.updateBuffer = updateBuffer;
  }

  /**
   * Template method to be implemented by all subclasses that is to set the
   * value of the filter and fire the 'Update' event.
   * 
   * @param value the filter value
   */
  public abstract void setValue(Object value);

  /**
   * Template method to be implemented by all subclasses that is to validates
   * the provided Model against the filters configuration. Defaults to
   * <tt>return true</tt>.
   * 
   * @param model the model
   * @return true if valid
   */
  public boolean validateModel(ModelData model) {
    return true;
  }

  protected void fireUpdate() {
    if (active) {
      fireEvent(Events.Update, new FilterEvent(this));
    }
    setActive(isActivatable(), false);
  }

  @SuppressWarnings("unchecked")
  protected <X> X getModelValue(ModelData model) {
    return (X) model.get(dataIndex);
  }

}

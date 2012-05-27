/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import java.util.Date;

import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.DatePicker;

/**
 * A Menu for choosing a date.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Select</b> : MenuEvent(menu, date)<br>
 * <div>Fires when a date is selected.</div>
 * <ul>
 * <li>menu : this</li>
 * <li>date : the selected date</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class DateMenu extends Menu {

  /**
   * The internal date picker.
   */
  protected DatePicker picker;

  public DateMenu() {
    picker = new DatePicker();
    picker.addListener(Events.Select, new Listener<DatePickerEvent>() {
      public void handleEvent(DatePickerEvent be) {
        onPickerSelect(be);
      }
    });
    add(picker);
    addStyleName("x-date-menu");
    setAutoHeight(true);
    plain = true;
    showSeparator = false;
    setEnableScrolling(false);
  }

  @Override
  public void focus() {
    super.focus();
    picker.el().focus();
  }

  /**
   * Returns the selected date.
   * 
   * @return the date
   */
  public Date getDate() {
    return picker.getValue();
  }

  /**
   * Returns the date picker.
   * 
   * @return the date picker
   */
  public DatePicker getDatePicker() {
    return picker;
  }

  /**
   * Sets the menu's date.
   * 
   * @param date the date
   */
  public void setDate(Date date) {
    picker.setValue(date);
  }

  protected void onPickerSelect(DatePickerEvent be) {
    MenuEvent e = new MenuEvent(this);
    e.setDate(be.getDate());
    fireEvent(Events.Select, e);
  }
}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.Date;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.CompositeElement;
import com.extjs.gxt.ui.client.core.CompositeFunction;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FxEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.DateTimeConstants;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Simple date picker.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Select</b> : DatePickerEvent(datePicker, date)<br>
 * <div>Fires when a date is selected.</div>
 * <ul>
 * <li>datePicker : this</li>
 * <li>date : the selected date</li>
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
@SuppressWarnings("deprecation")
public class DatePicker extends BoxComponent {

  /**
   * DatePicker messages.
   */
  public class DatePickerMessages {
    private String todayText = GXT.MESSAGES.datePicker_todayText();
    private String okText = GXT.MESSAGES.datePicker_okText();
    private String cancelText = GXT.MESSAGES.datePicker_cancelText();
    private String todayTip = GXT.MESSAGES.datePicker_todayTip(DateTimeFormat.getShortDateFormat().format(new Date()));
    private String minText = GXT.MESSAGES.datePicker_minText();
    private String maxText = GXT.MESSAGES.datePicker_maxText();
    private String prevText = GXT.MESSAGES.datePicker_prevText();
    private String nextText = GXT.MESSAGES.datePicker_nextText();
    private String monthYearText = GXT.MESSAGES.datePicker_monthYearText();

    /**
     * Sets the text to display on the cancel button.
     * 
     * @return the cancel button text
     */
    public String getCancelText() {
      return cancelText;
    }

    /**
     * Returns the max text.
     * 
     * @return the max text
     */
    public String getMaxText() {
      return maxText;
    }

    /**
     * Returns the min text.
     * 
     * @return the min text
     */
    public String getMinText() {
      return minText;
    }

    /**
     * Returns the month year text.
     * 
     * @return the month year text
     */
    public String getMonthYearText() {
      return monthYearText;
    }

    /**
     * Returns the next text.
     * 
     * @return the next text
     */
    public String getNextText() {
      return nextText;
    }

    /**
     * Returns the ok text.
     * 
     * @return the ok text
     */
    public String getOkText() {
      return okText;
    }

    /**
     * Returns the prev text.
     * 
     * @return the prev text
     */
    public String getPrevText() {
      return prevText;
    }

    /**
     * Returns the today text.
     * 
     * @return the today text
     */
    public String getTodayText() {
      return todayText;
    }

    /**
     * Returns the today tip.
     * 
     * @return the tip
     */
    public String getTodayTip() {
      return todayTip;
    }

    /**
     * Sets the cance text (default to "Cancel").
     * 
     * @param cancelText the cancel text
     */
    public void setCancelText(String cancelText) {
      this.cancelText = cancelText;
    }

    /**
     * Sets the error text to display if the maxDate validation fails (defaults
     * to "This date is after the maximum date").
     * 
     * @param maxText the max error text
     */
    public void setMaxText(String maxText) {
      this.maxText = maxText;
    }

    /**
     * Sets the error text to display if the minDate validation fails (defaults
     * to "This date is before the minimum date").
     * 
     * @param minText the minimum error text
     */
    public void setMinText(String minText) {
      this.minText = minText;
    }

    /**
     * Sets the header month selector tooltip (defaults to 'Choose a month
     * (Control+Up/Down to move years)').
     * 
     * @param monthYearText the month year text
     */
    public void setMonthYearText(String monthYearText) {
      this.monthYearText = monthYearText;
    }

    /**
     * Sets the next month navigation button tooltip (defaults to 'Next Month
     * (Control+Right)').
     * 
     * @param nextText the next text
     */
    public void setNextText(String nextText) {
      this.nextText = nextText;
    }

    /**
     * Sets the text to display on the ok button.
     * 
     * @param okText the ok text
     */
    public void setOkText(String okText) {
      this.okText = okText;
    }

    /**
     * Sets the previous month navigation button tooltip (defaults to 'Previous
     * Month (Control+Left)').
     * 
     * @param prevText the previous text
     */
    public void setPrevText(String prevText) {
      this.prevText = prevText;
    }

    /**
     * Sets the text to display on the button that selects the current date
     * (defaults to "Today").
     * 
     * @param todayText the today text
     */
    public void setTodayText(String todayText) {
      this.todayText = todayText;
    }

    /**
     * Sets the tooltip to display for the button that selects the current date
     * (defaults to "{current date} (Spacebar)").
     * 
     * @param todayTip the today tool tip
     */
    public void setTodayTip(String todayTip) {
      this.todayTip = todayTip;
    }

  }

  class Header extends Component {

    protected void doAttachChildren() {
      ComponentHelper.doAttach(monthBtn);
      prevBtn.onAttach();
      nextBtn.onAttach();
    }

    protected void doDetachChildren() {
      ComponentHelper.doDetach(monthBtn);
      prevBtn.onDetach();
      nextBtn.onDetach();
    }

    @Override
    protected void onRender(Element target, int index) {
      StringBuffer sb = new StringBuffer();
      sb.append("<table width=100% cellpadding=0 cellspacing=0><tr>");
      sb.append("<td class=x-date-left></td><td class=x-date-middle align=center></td>");
      sb.append("<td class=x-date-right></td></tr></table>");

      setElement(XDOM.create(sb.toString()));
      el().insertInto(target, index);

      monthBtn = new Button("&#160;", new SelectionListener<ButtonEvent>() {
        public void componentSelected(ButtonEvent ce) {
          showMonthPicker();
        }
      }) {
        @Override
        protected void autoWidth() {
          if (!GXT.isAriaEnabled()) {
            super.autoWidth();
          }
        }
      };

      monthBtn.setToolTip(getMessages().getMonthYearText());
      monthBtn.render(el().selectNode(".x-date-middle").dom);
      monthBtn.el().child("em").addStyleName("x-btn-arrow");

      prevBtn = new IconButton("x-date-left-icon", new SelectionListener<IconButtonEvent>() {
        public void componentSelected(IconButtonEvent ce) {
          showPrevMonth();
        }
      });
      prevBtn.setToolTip(messages.getPrevText());
      prevBtn.render(el().selectNode(".x-date-left").dom);

      nextBtn = new IconButton("x-date-right-icon", new SelectionListener<IconButtonEvent>() {
        public void componentSelected(IconButtonEvent ce) {
          showNextMonth();
        }
      });
      nextBtn.setToolTip(messages.getNextText());
      nextBtn.render(el().selectNode(".x-date-right").dom);
    }

  }

  protected Button todayBtn;

  private int firstDOW;
  private Date minDate;
  private Date maxDate;
  private int startDay = Integer.MIN_VALUE;
  private long today;
  private int mpyear;
  private Grid days, grid;
  private Component header;
  private com.google.gwt.user.client.ui.HorizontalPanel footer;
  private DateWrapper activeDate, value;
  private int mpSelMonth, mpSelYear;
  private Button monthBtn;
  private Element[] cells;
  private Element[] textNodes;
  private IconButton prevBtn, nextBtn;
  private CompositeElement mpMonths, mpYears;
  private El monthPicker;
  private DateTimeConstants constants;
  private DatePickerMessages messages;
  private El gridWrapper;

  /**
   * Creates a new date picker.
   */
  public DatePicker() {
    baseStyle = "x-date-picker";
    messages = new DatePickerMessages();
    constants = LocaleInfo.getCurrentLocale().getDateTimeConstants();
  }

  @Override
  public void focus() {
    super.focus();
    update(activeDate);
  }

  /**
   * Returns the field's maximum allowed date.
   * 
   * @return the max date
   */
  public Date getMaxDate() {
    return maxDate;
  }

  /**
   * Returns the data picker messages.
   * 
   * @return the date picker messages
   */
  public DatePickerMessages getMessages() {
    return messages;
  }

  /**
   * Returns the picker's minimum data.
   * 
   * @return the minimum date
   */
  public Date getMinDate() {
    return minDate;
  }

  /**
   * Returns the picker's start day.
   * 
   * @return the start day
   */
  public int getStartDay() {
    return startDay;
  }

  /**
   * Gets the current selected value of the date field.
   * 
   * @return the date
   */
  public Date getValue() {
    return value != null ? value.asDate() : null;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    switch (ce.getEventTypeInt()) {
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONMOUSEOVER:
        onMouseOver(ce);
        break;
      case Event.ONMOUSEOUT:
        onMouseOut(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
    }
  }

  /**
   * Sets the picker's maximum allowed date.
   * 
   * @param maxDate the max date
   */
  public void setMaxDate(Date maxDate) {
    if (maxDate != null) {
      maxDate = new DateWrapper(maxDate).resetTime().asDate();
    }
    this.maxDate = maxDate;
    if (rendered) {
      update(value);
    }
  }

  /**
   * Sets the data picker messages.
   * 
   * @param messages the date picker messages
   */
  public void setMessages(DatePickerMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets the picker's minimum allowed date.
   * 
   * @param minDate the minimum date
   */
  public void setMinDate(Date minDate) {
    if (minDate != null) {
      minDate = new DateWrapper(minDate).resetTime().asDate();
    }
    this.minDate = minDate;
    if (rendered) {
      update(value);
    }
  }

  /**
   * Sets the picker's start day
   * 
   * @param startDay the start day
   */
  public void setStartDay(int startDay) {
    this.startDay = startDay;
  }

  /**
   * Sets the value of the date field.
   * 
   * @param date the date
   */
  public void setValue(Date date) {
    setValue(date, false);
  }

  /**
   * Sets the value of the date field.
   * 
   * @param date the date
   * @param supressEvent true to suppress the select event
   */
  public void setValue(Date date, boolean supressEvent) {
    this.value = new DateWrapper(date).resetTime();
    if (rendered) {
      update(value);
    }
    if (!supressEvent) {
      DatePickerEvent de = new DatePickerEvent(this);
      de.setDate(date);
      fireEvent(Events.Select, de);
    }
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    header.onAttach();
    ComponentHelper.doAttach(footer);
    ComponentHelper.doAttach(grid);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    header.onDetach();
    ComponentHelper.doDetach(footer);
    ComponentHelper.doDetach(grid);
    monthPicker.setVisible(false);
  }

  protected void onClick(ComponentEvent be) {
    be.preventDefault();
    El target = be.getTargetEl();
    El pn = null;
    String cls = target.getStyleName();
    if (cls.equals("x-date-left-a")) {
      showPrevMonth();
    } else if (cls.equals("x-date-right-a")) {
      showNextMonth();
    }
    if ((pn = target.findParent("td.x-date-mp-month", 2)) != null) {
      mpMonths.removeStyleName("x-date-mp-sel");
      El elem = target.findParent("td.x-date-mp-month", 2);
      elem.addStyleName("x-date-mp-sel");
      mpSelMonth = pn.dom.getPropertyInt("xmonth");
    } else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null) {
      mpYears.removeStyleName("x-date-mp-sel");
      El elem = target.findParent("td.x-date-mp-year", 2);
      elem.addStyleName("x-date-mp-sel");
      mpSelYear = pn.dom.getPropertyInt("xyear");
    } else if (target.is("button.x-date-mp-ok")) {
      DateWrapper d = new DateWrapper(mpSelYear, mpSelMonth, 1);
      update(d);
      hideMonthPicker();
    } else if (target.is("button.x-date-mp-cancel")) {
      hideMonthPicker();
    } else if (target.is("a.x-date-mp-prev")) {
      updateMPYear(mpyear - 10);
    } else if (target.is("a.x-date-mp-next")) {
      updateMPYear(mpyear + 10);
    }

    if (GXT.isSafari) {
      focus();
    }
  }

  protected void onDayClick(ComponentEvent ce) {
    ce.preventDefault();
    El target = ce.getTargetEl();
    El e = target.findParent("a", 5);
    if (e != null) {
      String dt = e.dom.getPropertyString("dateValue");
      if (dt != null) {
        handleDateClick(e, dt);
        return;
      }
    }
  }

  protected void onFocus(ComponentEvent ce) {
    if (GXT.isFocusManagerEnabled()) {
      FocusFrame.get().frame(this);
    }
    update(activeDate);
  }

  protected void onKeyDown(ComponentEvent ce) {
    if (ce.isControlKey()) {
      showPreviousYear();
    } else {
      update(activeDate.addDays(7));
    }
  }

  protected void onKeyEnd(ComponentEvent ce) {
    if (ce.isShiftKey()) {
      setValue(new DateWrapper(activeDate.getFullYear(), 11, 31).asDate());
    } else {
      setValue(activeDate.getLastDateOfMonth().asDate());
    }
  }

  protected void onKeyEnter(ComponentEvent ce) {
    ce.stopEvent();
    setValue(activeDate.asDate());
  }

  protected void onKeyHome(ComponentEvent ce) {
    if (ce.isShiftKey()) {
      setValue(new DateWrapper(activeDate.getFullYear(), 0, 1).asDate());
    } else {
      setValue(activeDate.getFirstDayOfMonth().asDate());
    }
  }

  protected void onKeyLeft(ComponentEvent ce) {
    ce.stopEvent();
    if (ce.isControlKey()) {
      showPrevMonth();
    } else {
      update(activeDate.addDays(-1));
    }
  }

  protected void onKeyPageDown(ComponentEvent ce) {
    if (ce.isShiftKey()) {
      setValue(activeDate.addYears(1).asDate());
    } else {
      setValue(activeDate.addMonths(1).asDate());
    }
  }

  protected void onKeyPageUp(ComponentEvent ce) {
    if (ce.isShiftKey()) {
      setValue(activeDate.addYears(-1).asDate());
    } else {
      setValue(activeDate.addMonths(-1).asDate());
    }
  }

  protected void onKeyRight(ComponentEvent ce) {
    ce.stopEvent();
    if (ce.isControlKey()) {
      showNextMonth();
    } else {
      update(activeDate.addDays(1));
    }
  }

  protected void onKeyUp(ComponentEvent ce) {
    ce.stopEvent();
    if (ce.isControlKey()) {
      showNextYear();
    } else {
      update(activeDate.addDays(-7));
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
    disableTextSelection(true);

    header = new Header();
    header.render(getElement());

    days = new Grid(1, 7);
    days.setStyleName("x-date-days");
    days.setCellPadding(0);
    days.setCellSpacing(0);
    days.setBorderWidth(0);

    String[] dn = constants.narrowWeekdays();
    String[] longdn = constants.weekdays();
    
    firstDOW = startDay != Integer.MIN_VALUE ? startDay : Integer.parseInt(constants.firstDayOfTheWeek()) - 1;

    days.setHTML(0, 0, "<span>" + dn[(0 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 1, "<span>" + dn[(1 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 2, "<span>" + dn[(2 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 3, "<span>" + dn[(3 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 4, "<span>" + dn[(4 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 5, "<span>" + dn[(5 + firstDOW) % 7] + "</span>");
    days.setHTML(0, 6, "<span>" + dn[(6 + firstDOW) % 7] + "</span>");

    days.getRowFormatter().getElement(0).setAttribute("role", "row");

    for (int i = 0; i < 7; i++) {
      days.getCellFormatter().getElement(0, i).setAttribute("role", "columnheader");
      days.getCellFormatter().getElement(0, i).setAttribute("aria-label", longdn[i]);
    }

    grid = new Grid(6, 7);
    grid.setStyleName("x-date-inner");
    grid.setCellSpacing(0);
    grid.setCellPadding(0);
    grid.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        Event evt = DOM.eventGetCurrentEvent();
        ComponentEvent be = new ComponentEvent(DatePicker.this, evt);
        onDayClick(be);
      }
    });
    String s = GXT.isAriaEnabled() ? "<a role=gridcell tabindex=0><span role=presentation></span></a>"
        : "<a href=#><span></span></a>";
    for (int row = 0; row < 6; row++) {
      if (GXT.isAriaEnabled()) {
        grid.getRowFormatter().getElement(row).setAttribute("role", "row");
      }
      for (int col = 0; col < 7; col++) {
        grid.setHTML(row, col, s);
        if (GXT.isAriaEnabled()) {
          grid.getCellFormatter().getElement(row, col).setAttribute("role", "presentation");
        }
      }
    }

    footer = new com.google.gwt.user.client.ui.HorizontalPanel();
    footer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    footer.setWidth("100%");

    todayBtn = new Button(messages.getTodayText(), new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        selectToday();
      }
    });
    if (messages.getTodayTip() != null) {
      todayBtn.setToolTip(messages.getTodayTip());
    }
    footer.add(todayBtn);
    todayBtn.getElement().getParentElement().setClassName("x-date-bottom");

    monthPicker = new El(DOM.createDiv());
    monthPicker.dom.setClassName("x-date-mp");

    gridWrapper = new El((Element) Document.get().createElement("DIV"));
    gridWrapper.dom.setAttribute("role", "grid");

    gridWrapper.appendChild(days.getElement());
    gridWrapper.appendChild(grid.getElement());

    getElement().appendChild(header.getElement());
    getElement().appendChild(gridWrapper.dom);
    getElement().appendChild(footer.getElement());
    getElement().appendChild(monthPicker.dom);

    El btntext = monthBtn.el().selectNode("button");
    btntext.setId(XDOM.getUniqueId());
    gridWrapper.dom.setAttribute("aria-labelledby", btntext.getId());

    setWidth(177);

    cells = Util.toElementArray(el().select("table.x-date-inner tbody td"));
    textNodes = Util.toElementArray(el().select("table.x-date-inner tbody span"));

    activeDate = value != null ? value : new DateWrapper();
    update(activeDate);

    if (GXT.isAriaEnabled()) {
      String[] tags = new String[] {"table", "tbody", "tr", "td"};
      for (int i = 0; i < tags.length; i++) {
        NodeList<Element> elems = el().select(tags[i]);
        for (int j = 0; j < elems.getLength(); j++) {
          if (elems.getItem(j).getAttribute("role").equals("")) {
            elems.getItem(j).setAttribute("role", "presentation");
          }
          if (i == 3) {
            elems.getItem(j).setId(XDOM.getUniqueId());
          }
        }
      }
    }

    el().makePositionable();
    el().setTabIndex(0);
    el().setElementAttribute("hideFocus", "true");
    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONFOCUS);

    new KeyNav<ComponentEvent>(this) {

      @Override
      public void onDown(ComponentEvent ce) {
        onKeyDown(ce);
      }

      @Override
      public void onEnd(ComponentEvent ce) {
        onKeyEnd(ce);
      }

      @Override
      public void onEnter(ComponentEvent ce) {
        onKeyEnter(ce);
      }

      @Override
      public void onHome(ComponentEvent ce) {
        onKeyHome(ce);
      }

      @Override
      public void onKeyPress(ComponentEvent ce) {
        // space bar pressed
        if (ce.getKeyCode() == 32) {
          selectToday();
        }
      }

      @Override
      public void onLeft(ComponentEvent ce) {
        onKeyLeft(ce);
      }

      @Override
      public void onPageDown(ComponentEvent ce) {
        onKeyPageDown(ce);
      }

      @Override
      public void onPageUp(ComponentEvent ce) {
        onKeyPageUp(ce);
      }

      @Override
      public void onRight(ComponentEvent ce) {
        onKeyRight(ce);
      }

      @Override
      public void onUp(ComponentEvent ce) {
        onKeyUp(ce);
      }

    };
  }

  protected void createMonthPicker() {
    StringBuffer buf = new StringBuffer();
    buf.append("<table border=0 cellspacing=0>");
    String[] monthNames = constants.shortMonths();
    for (int i = 0; i < 6; i++) {
      buf.append("<tr><td class=x-date-mp-month><a href=#>");
      buf.append(monthNames[i]);
      buf.append("</a></td>");
      buf.append("<td class='x-date-mp-month x-date-mp-sep'><a href=#>");
      buf.append(monthNames[i + 6]);
      buf.append("</a></td>");
      if (i == 0) {
        buf.append("<td class=x-date-mp-ybtn align=center><a class=x-date-mp-prev href=#></a></td><td class='x-date-mp-ybtn' align=center><a class='x-date-mp-next'></a></td></tr>");
      } else {
        buf.append("<td class='x-date-mp-year'><a href='#'></a></td><td class='x-date-mp-year'><a href='#'></a></td></tr>");
      }
    }
    buf.append("<tr class=x-date-mp-btns><td colspan='4'><button type='button' class='x-date-mp-ok'>");
    buf.append(messages.getOkText());
    buf.append("</button><button type=button class=x-date-mp-cancel>");
    buf.append(messages.getCancelText());
    buf.append("</button></td></tr></table>");

    monthPicker.update(buf.toString());

    mpMonths = new CompositeElement(Util.toElementArray(monthPicker.select("td.x-date-mp-month")));
    mpYears = new CompositeElement(Util.toElementArray(monthPicker.select("td.x-date-mp-year")));

    mpMonths.each(new CompositeFunction() {

      public void doFunction(Element elem, CompositeElement ce, int index) {
        index += 1;
        if (index % 2 == 0) {
          elem.setPropertyInt("xmonth", (int) (5 + (Math.round(index * .5))));
        } else {
          elem.setPropertyInt("xmonth", (int) (Math.round((index - 1) * .5)));
        }
      }

    });

  }

  protected void handleDateClick(El target, String dt) {
    String[] tokens = dt.split(",");
    int year = Integer.parseInt(tokens[0]);
    int month = Integer.parseInt(tokens[1]);
    int day = Integer.parseInt(tokens[2]);
    Date d = new DateWrapper(year, month, day).asDate();
    if (d != null && !target.getParent().hasStyleName("x-date-disabled")) {
      setValue(d);
    }
  }

  protected void hideMonthPicker() {
    monthPicker.slideOut(Direction.UP, new FxConfig(300, new Listener<FxEvent>() {
      public void handleEvent(FxEvent ce) {
        monthPicker.setVisible(false);
      }
    }));
  }

  protected void onMouseOut(ComponentEvent ce) {
    El target = ce.getTarget("td.x-date-active", 3);
    if (target == null) {
      target = ce.getTarget("td.x-date-nextday", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-prevday", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-mp-month", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-mp-year", 3);
    }
    if (target != null) {
      target.removeStyleName("x-date-active-hover");
    }

  }

  protected void onMouseOver(ComponentEvent ce) {
    El target = ce.getTarget("td.x-date-active", 3);
    if (target == null) {
      target = ce.getTarget("td.x-date-nextday", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-prevday", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-mp-month", 3);
    }
    if (target == null) {
      target = ce.getTarget("td.x-date-mp-year", 3);
    }
    if (target != null) {
      target.addStyleName("x-date-active-hover");
    }

  }

  protected void selectToday() {
    setValue(new DateWrapper().asDate());
  }

  protected void setCellStyle(Element cell, Date d, long sel, long min, long max) {
    long t = d.getTime();

    DateWrapper w = new DateWrapper(d);
    int year = w.getFullYear();
    int month = w.getMonth();
    int day = w.getDate();

    String dd = year + "," + month + "," + day;

    cell.getFirstChildElement().setPropertyString("dateValue", dd);

    if (t == today) {
      fly(cell).addStyleName("x-date-today");
      if (GXT.isAriaEnabled()) {
        cell.setAttribute("title", "Today");
      }
    }
    if (t == sel) {
      fly(cell).addStyleName("x-date-selected");
      if (GXT.isAriaEnabled()) {
        cell.getFirstChildElement().setAttribute("aria-selected", "true");
        if (isAttached()) {
          El.fly(cell.getFirstChildElement()).focus();
        }
      }
    } else {
      fly(cell).removeStyleName("x-date-selected");
      if (GXT.isAriaEnabled()) {
        cell.getFirstChildElement().setAttribute("aria-selected", "false");
      }
    }
    if (t > max || t < min) {
      fly(cell).addStyleName("x-date-disabled");
      cell.setTitle(messages.getMaxText());
    }
  }

  protected void showMonthPicker() {
    createMonthPicker();

    Size s = el().getSize(true);
    s.height -= 2;
    monthPicker.setTop(1);
    monthPicker.setSize(s.width, s.height);
    monthPicker.firstChild().setSize(s.width, s.height, true);

    mpSelMonth = (activeDate != null ? activeDate : value).getMonth();

    updateMPMonth(mpSelMonth);
    mpSelYear = (activeDate != null ? activeDate : value).getFullYear();
    updateMPYear(mpSelYear);

    monthPicker.enableDisplayMode("block");
    monthPicker.makePositionable(true);
    monthPicker.slideIn(Direction.DOWN, FxConfig.NONE);

  }

  protected void showNextMonth() {
    update(activeDate.addMonths(+1));
  }

  protected void showNextYear() {
    update(activeDate.addYears(1));
  }

  protected void showPreviousYear() {
    update(activeDate.addYears(-1));
  }

  protected void showPrevMonth() {
    update(activeDate.addMonths(-1));
  }

  protected void update(DateWrapper date) {
    DateWrapper vd = activeDate;
    activeDate = date;
    if (vd != null && el() != null) {
      int days = date.getDaysInMonth();
      DateWrapper firstOfMonth = date.getFirstDayOfMonth();
      int startingPos = firstOfMonth.getDayInWeek() - firstDOW;

      if (startingPos <= startDay) {
        startingPos += 7;
      }

      // go to previous month.
      DateWrapper pm = activeDate.addMonths(-1);
      int prevStart = pm.getDaysInMonth() - startingPos;

      days += startingPos;

      DateWrapper d = new DateWrapper(pm.getFullYear(), pm.getMonth(), prevStart).resetTime();
      today = new DateWrapper().resetTime().getTime();
      long sel = value != null ? value.resetTime().getTime() : Long.MIN_VALUE;
      long min = minDate != null ? new DateWrapper(minDate).getTime() : Long.MIN_VALUE;
      long max = maxDate != null ? new DateWrapper(maxDate).getTime() : Long.MAX_VALUE;

      int i = 0;
      for (; i < startingPos; i++) {
        fly(textNodes[i]).update("" + ++prevStart);
        d = d.addDays(1);
        cells[i].setClassName("x-date-prevday");
        if (GXT.isAriaEnabled()) {
          cells[i].setAttribute("aria-disabled", "true");
        }
        setCellStyle(cells[i], d.asDate(), sel, min, max);
      }
      for (; i < days; i++) {
        int intDay = i - startingPos + 1;
        fly(textNodes[i]).update("" + intDay);
        d = d.addDays(1);
        cells[i].setClassName("x-date-active");
        setCellStyle(cells[i], d.asDate(), sel, min, max);
      }
      int extraDays = 0;
      for (; i < 42; i++) {
        fly(textNodes[i]).update("" + ++extraDays);
        d = d.addDays(1);
        cells[i].setClassName("x-date-nextday");
        if (GXT.isAriaEnabled()) {
          cells[i].setAttribute("aria-disabled", "true");
        }
        setCellStyle(cells[i], d.asDate(), sel, min, max);
      }

      int month = activeDate.getMonth();

      String t = constants.standaloneMonths()[month] + " " + activeDate.getFullYear();
      monthBtn.setText(t);
    }
  }

  protected void updateMPMonth(int month) {
    for (int i = 0; i < mpMonths.getCount(); i++) {
      Element elem = mpMonths.item(i);
      int xmonth = elem.getPropertyInt("xmonth");
      fly(elem).setStyleName("x-date-mp-sel", xmonth == month);
    }
  }

  protected void updateMPYear(int year) {
    mpyear = year;
    for (int i = 1; i <= 10; i++) {
      El td = new El(mpYears.item(i - 1));
      int y2;
      if (i % 2 == 0) {
        y2 = (int) (year + (Math.round(i * .5)));
      } else {
        y2 = (int) (year - (5 - Math.round(i * .5)));
      }
      td.firstChild().update("" + y2);
      td.dom.setPropertyInt("xyear", y2);
      td.setStyleName("x-date-mp-sel", y2 == mpSelYear);
    }

  }
}

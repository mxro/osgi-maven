/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.button;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * A simple css styled button with 3 states: normal, over, and disabled.
 * 
 * <p />
 * Note: To change the icon style after construction use
 * {@link #changeStyle(String)}.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Select</b> : IconButtonEvent(iconButton, event)<br>
 * <div>Fires after the item is selected.</div>
 * <ul>
 * <li>iconButton : this</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class IconButton extends BoxComponent {

  protected String style;
  protected boolean cancelBubble = true;

  /**
   * Creates a new icon button. When using the default constructor,
   * {@link #changeStyle(String)} must be called to initialize the button.
   */
  @Deprecated
  public IconButton() {
    this("");
  }

  /**
   * Creates a new icon button. The 'over' style and 'disabled' style names
   * determined by adding '-over' and '-disabled' to the base style name.
   * 
   * @param style the base style
   */
  public IconButton(String style) {
    this.style = style;
  }

  /**
   * Creates a new icon button. The 'over' style and 'disabled' style names
   * determined by adding '-over' and '-disabled' to the base style name.
   * 
   * @param style the base style
   * @param listener the click listener
   */
  public IconButton(String style, SelectionListener<IconButtonEvent> listener) {
    this(style);
    addSelectionListener(listener);
  }

  /**
   * @param listener
   */
  public void addSelectionListener(SelectionListener<IconButtonEvent> listener) {
    addListener(Events.Select, listener);
  }

  /**
   * Changes the icon style.
   * 
   * @param style the new icon style
   */
  public void changeStyle(String style) {
    removeStyleName(this.style);
    removeStyleName(this.style + "-over");
    removeStyleName(this.style + "-disabled");
    addStyleName(style);
    this.style = style;
  }

  public void onComponentEvent(ComponentEvent ce) {
    switch (ce.getEventTypeInt()) {
      case Event.ONMOUSEOVER:
        addStyleName(style + "-over");
        break;
      case Event.ONMOUSEOUT:
        removeStyleName(style + "-over");
        break;
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
      case Event.ONBLUR:
        onBlur(ce);
        break;
      case Event.ONKEYUP:
        onKeyPress(ce);
        break;
    }
  }

  /**
   * Removes a previously added listener.
   * 
   * @param listener the listener to be removed
   */
  public void removeSelectionListener(SelectionListener<IconButtonEvent> listener) {
    removeListener(Events.Select, listener);
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new IconButtonEvent(this, event);
  }

  protected void onBlur(ComponentEvent ce) {
    if (GXT.isFocusManagerEnabled()) {
      FocusFrame.get().unframe();
    }
  }

  protected void onClick(ComponentEvent ce) {
    if (cancelBubble) {
      ce.cancelBubble();
    }
    removeStyleName(style + "-over");
    fireEvent(Events.Select, ce);
  }

  protected void onDisable() {
    addStyleName(style + "-disabled");
  }

  protected void onEnable() {
    removeStyleName(style + "-disabled");
  }

  protected void onFocus(ComponentEvent ce) {
    if (GXT.isFocusManagerEnabled() && !GXT.isIE) {
      FocusFrame.get().frame(this);
    }
  }

  protected void onKeyPress(ComponentEvent ce) {
    int code = ce.getKeyCode();
    if (code == KeyCodes.KEY_ENTER || code == 32) {
      onClick(ce);
    }
  }

  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
    addStyleName("x-icon-btn");
    addStyleName("x-nodrag");
    addStyleName(style);
    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.ONKEYUP);
    super.onRender(target, index);

    if (GXT.isHighContrastMode) {
      getElement().setInnerHTML("<i>&nbsp;</i>");
    }

    if (GXT.isFocusManagerEnabled()) {
      el().setTabIndex(0);
      Accessibility.setRole(getElement(), Accessibility.ROLE_BUTTON);
    }
  }

}

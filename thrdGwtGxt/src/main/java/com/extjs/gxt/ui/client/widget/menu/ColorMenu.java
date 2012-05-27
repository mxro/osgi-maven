/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.menu;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ColorPalette;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
@SuppressWarnings("deprecation")
public class ColorMenu extends Menu {

  protected ColorPalette palette;

  public ColorMenu() {
    super();
    palette = new ColorPalette();
    palette.addListener(Events.Select, new Listener<BaseEvent>() {
      public void handleEvent(BaseEvent be) {
        hide(true);
      }
    });
    add(palette);
    setAutoHeight(true);
    plain = true;
    showSeparator = false;
    setEnableScrolling(false);
  }
  
  /**
   * Returns the selected color.
   * 
   * @return the color
   */
  
  public String getColor() {
    return palette.getValue();
  }

  /**
   * Returns the color palette.
   * 
   * @return the color palette
   */
  public ColorPalette getColorPalette() {
    return palette;
  }

  protected void onClick(ComponentEvent ce) {
    hide(true);
  }
  
  @Override
  protected void onShow() {
    super.onShow();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        palette.focus();
      }
    });
  }
}

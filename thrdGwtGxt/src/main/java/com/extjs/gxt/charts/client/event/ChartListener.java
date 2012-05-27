/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.charts.client.event;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * Chart listener.
 */
public abstract class ChartListener implements Listener<ChartEvent> {

  public void handleEvent(ChartEvent ce) {
    chartClick(ce);
  }

  public void chartClick(ChartEvent ce) {
  }

}

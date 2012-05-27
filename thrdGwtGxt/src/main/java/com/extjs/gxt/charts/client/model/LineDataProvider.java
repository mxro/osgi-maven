/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.charts.client.model;

import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * <code>DataProvider</code> implementation for line charts.
 */
public class LineDataProvider extends PieDataProvider {

  public LineDataProvider(String valueProperty) {
    super(valueProperty);
  }

  public LineDataProvider(String valueProperty, String labelProperty, String textProperty) {
    super(valueProperty, labelProperty, textProperty);
  }

  public LineDataProvider(String valueProperty, String labelProperty) {
    super(valueProperty, labelProperty);
  }

  @Override
  public void populateData(ChartConfig config) {
    LineChart chart = (LineChart) config;
    chart.getValues().clear();

    XAxis xAxis = null;
    if (labelProperty != null || labelProvider != null) {
      xAxis = chart.getModel().getXAxis();
      if (xAxis == null) {
        xAxis = new XAxis();
        chart.getModel().setXAxis(xAxis);
      }
      xAxis.getLabels().getLabels().clear();
    }

    boolean first = true;
    for (ModelData m : store.getModels()) {
      Number n = getValue(m);
      if (n == null) {
        chart.addNullValue();
      } else {
        chart.addValues(n);
        maxYValue = first ? n.doubleValue() : Math.max(maxYValue, n.doubleValue());
        minYValue = first ? n.doubleValue() : Math.min(minYValue, n.doubleValue());
        first = false;
      }
      if (xAxis != null) {
        xAxis.addLabels(getLabel(m));
      }
    }
  }
}

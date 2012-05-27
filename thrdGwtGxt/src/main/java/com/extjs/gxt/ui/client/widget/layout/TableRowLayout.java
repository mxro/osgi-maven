/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.layout;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * A single row TableLayout.
 * 
 * @see TableData
 */
public class TableRowLayout extends TableLayout {

  private Element row;

  @Override
  protected Element getNextCell(Component component) {
    TableData data = (TableData) getLayoutData(component);
    if (data == null) {
      data = new TableData();
      setLayoutData(component, data);
    }

    if (component.getData("width") != null) {
      data.setWidth((String) component.getData("width"));
    }

    El td = new El(DOM.createTD());
    td.dom.setAttribute("role", "presentation");

    if (data.horizontalAlign != null) {
      td.dom.setPropertyString("align", data.horizontalAlign.name());
    } else if (cellHorizontalAlign != null) {
      td.dom.setPropertyString("align", cellHorizontalAlign.name());
    }

    if (data.verticalAlign != null) {
      td.dom.setPropertyString("vAlign", data.verticalAlign.name());
    } else if (cellVerticalAlign != null) {
      td.dom.setPropertyString("vAlign", cellVerticalAlign.name());
    }

    if (data.getPadding() > 0) {
      td.dom.getStyle().setPropertyPx("padding", data.getPadding());
    } else if (cellPadding > 0) {
      td.dom.getStyle().setPropertyPx("padding", cellPadding);
    }

    if (data.getStyleName() != null) {
      td.dom.setClassName(data.getStyleName());
    }

    if (data.getHeight() != null) {
      td.dom.setPropertyString("height", data.getHeight());
    }
    if (data.getWidth() != null) {
      td.dom.setPropertyString("width", data.getWidth());
    }

    if (data.getStyle() != null) {
      td.applyStyles(data.getStyle());
    }
    DOM.appendChild(row, td.dom);

    return td.dom;
  }

  @Override
  protected void onLayout(Container<?> container, El target) {
    currentColumn = 0;
    currentRow = 0;

    target.removeChildren();

    table = DOM.createTable().cast();
    table.setAttribute("role", "presentation");
    
    if (getTableStyle() != null) {
      fly(table).applyStyles(getTableStyle());
    }

    if (getCellPadding() != -1) {
      table.setCellPadding(getCellPadding());
    }
    if (cellSpacing != -1) {
      table.setCellSpacing(cellSpacing);
    }

    if (getBorder() > 0) {
      table.setBorder(getBorder());
    }

    if (getWidth() != null) {
      table.setWidth(getWidth());
    }

    if (getHeight() != null) {
      table.setAttribute("height", getHeight());
    }

    tbody = DOM.createTBody();
    table.appendChild(tbody);

    row = DOM.createTR();
    row.setAttribute("role", "presentation");
    DOM.appendChild(tbody, row);

    if (getInsertSpacer()) {
      Element td = DOM.createTD();
      td.setAttribute("role", "presentation");
      fly(td).setWidth("100%");
      DOM.appendChild(row, td);
    }

    target.dom.appendChild(table);
    renderAll(container, target);
    
    table.getFirstChildElement().setAttribute("role", "presentation");
  }

  @Override
  protected void renderComponent(Component c, int index, El target) {
    if (!c.isRendered()) {
      c.render(getNextCell(c));
    } else {
      DOM.appendChild(getNextCell(c), c.getElement());
    }
  }

}

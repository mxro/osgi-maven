/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.custom;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.dnd.ScrollSupport;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.PortalEvent;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ScrollContainer;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Portal container of Portlets. It is required that
 * {@link #setColumnWidth(int, double)} be called for each column prior to
 * rendering.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>ValidateDrop</b> : PortalEvent(portal, startColumn, startRow, column,
 * row)<br>
 * <div>Fires before a dragged portlet can be inserted into a new
 * location.</div>
 * <ul>
 * <li>portal : this</li>
 * <li>portlet : the portlet being dragged</li>
 * <li>startColumn : the start column</li>
 * <li>startRow : the start row</li>
 * <li>column : the new column</li>
 * <li>row : the new row</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Drop</b> : PortalEvent(portal, startColumn, startRow, column, row)<br>
 * <div>Fires after a portlet has been moved.</div>
 * <ul>
 * <li>portal : this</li>
 * <li>portlet : the portlet being dropped</li>
 * <li>startColumn : the start column</li>
 * <li>startRow : the start row</li>
 * <li>column : the new column</li>
 * <li>row : the new row</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>Container BeforeAdd</dd>
 * <dd>Container Add</dd>
 * <dd>Container BeforeRemove</dd>
 * <dd>Container Remove</dd>
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
public class Portal extends ScrollContainer<LayoutContainer> {

  private Portlet active;
  private boolean autoScroll = true;
  private ColumnLayout cl;
  private El dummy;
  private int insertCol = -1, insertRow = -1;
  private DragListener listener;
  private int numColumns;
  private ScrollSupport scrollSupport;
  private int spacing = 10;
  private int startCol, startRow;
  private List<Integer> startColumns;

  /**
   * Creates a new portal container.
   * 
   * @param numColumns the number of columns
   */
  public Portal(int numColumns) {
    this.numColumns = numColumns;
    cl = new ColumnLayout();
    cl.setAdjustForScroll(true);
    setLayout(cl);
    setScrollMode(Scroll.AUTO);
    baseStyle = "x-portal";
    enableLayout = true;

    for (int i = 0; i < numColumns; i++) {
      LayoutContainer l = new LayoutContainer();
      l.addStyleName("x-portal x-portal-column");
      l.setStyleAttribute("minHeight", "20px");
      l.setStyleAttribute("padding", spacing + "px 0 0 " + spacing + "px");
      l.setLayout(new RowLayout());
      l.setLayoutOnChange(true);
      add(l);
    }

    listener = createDragListener();
  }

  /**
   * Adds a portlet to the portal.
   * 
   * @param portlet the portlet to add
   * @param column the column to insert into
   */
  public void add(Portlet portlet, int column) {
    insert(portlet, getItem(column).getItemCount(), column);
  }

  /**
   * Returns the column of the given porlet.
   * 
   * @param portlet the portlet
   * @return the column or -1 if not found
   */
  public int getPortletColumn(Portlet portlet) {
    Widget c = portlet.getParent();
    if (c != null && c instanceof LayoutContainer) {
      return indexOf((LayoutContainer) c);
    }
    return -1;
  }

  /**
   * Returns the index of the column for the given portlet.
   * 
   * @param portlet the portlet
   * @return the index or -1 if not found
   */
  public int getPortletIndex(Portlet portlet) {
    Widget c =  portlet.getParent();
    if (c != null && c instanceof LayoutContainer) {
      return ((LayoutContainer)c).indexOf(portlet);
    }
    return -1;
  }

  /**
   * Returns the scroll support instance.
   * 
   * @return the scroll support
   */
  public ScrollSupport getScrollSupport() {
    if (scrollSupport == null) {
      scrollSupport = new ScrollSupport();
    }
    return scrollSupport;
  }

  /**
   * Returns the spacing between portlets.
   * 
   * @return the spacing the spacing in pixels
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Inserts a portlet.
   * 
   * @param portlet the portlet to add
   * @param index the insert index
   * @param column the column to insert into
   */
  public void insert(Portlet portlet, int index, int column) {
    Draggable d = portlet.getData("gxt.draggable");
    if (d == null) {
      d = new Draggable(portlet, portlet.getHeader());
      portlet.setData("gxt.draggable", d);
    }
    d.setUseProxy(true);
    d.removeDragListener(listener);
    d.addDragListener(listener);
    d.setMoveAfterProxyDrag(false);
    d.setSizeProxyToSource(true);
    d.setEnabled(!portlet.isPinned());
    getItem(column).insert(portlet, index, new RowData(1, -1));
    getItem(column).layout();
  }

  /**
   * Returns true if auto scroll is enabled (defaults to true).
   * 
   * @return true if auto scroll enabled
   */
  public boolean isAutoScroll() {
    return autoScroll;
  }

  /**
   * Removes a portlet from the portal.
   * 
   * @param portlet the porlet to remove
   * @param column the column
   */
  public void remove(Portlet portlet, int column) {
    Draggable d = portlet.getData("gxt.draggable");
    if (d != null) {
      d.release();
    }
    portlet.setData("gxt.draggable", null);

    getItem(column).remove(portlet);
  }

  /**
   * True to adjust the layout for a vertical scroll bar (defaults to true).
   * 
   * @param adjust true to adjust
   */
  public void setAdjustForScroll(boolean adjust) {
    cl.setAdjustForScroll(adjust);
  }

  /**
   * True to automatically scroll the portal container when the user hovers over
   * the top and bottom of the container (defaults to true).
   * 
   * @see ScrollSupport
   * 
   * @param autoScroll true to enable auto scroll
   */
  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  /**
   * Sets the column's width.
   * 
   * @param colIndex the column index
   * @param width the column width
   */
  public void setColumnWidth(int colIndex, double width) {
    ComponentHelper.setLayoutData(getItem(colIndex), new ColumnData(width));
  }

  /**
   * Sets the spacing between portlets (defaults to 10).
   * 
   * @param spacing the spacing in pixels
   */
  public void setSpacing(int spacing) {
    this.spacing = spacing;
    for (LayoutContainer l : getItems()) {
      l.setStyleAttribute("padding", spacing + "px 0 0 " + spacing + "px");
    }
  }

  protected DragListener createDragListener() {
    return new DragListener() {

      @Override
      public void dragCancel(DragEvent de) {
        onDragCancel(de);
      }

      @Override
      public void dragEnd(DragEvent de) {
        onDragEnd(de);
      }

      @Override
      public void dragLeave(DragEvent de) {
        onDragLeave(de);
      }

      @Override
      public void dragMove(DragEvent de) {
        onDragMove(de);
      }

      @Override
      public void dragStart(DragEvent de) {
        onDragStart(de);
      }
    };
  }

  protected void onDragCancel(DragEvent event) {
    active.setVisible(true);
    active = null;
    insertCol = -1;
    insertRow = -1;
    dummy.removeFromParent();
    if (autoScroll) {
      scrollSupport.stop();
    }
  }

  protected void onDragEnd(DragEvent de) {
    dummy.removeFromParent();

    if (insertCol != -1 && insertRow != -1) {
      if (startCol == insertCol && insertRow > startRow) {
        insertRow--;
      }
      active.setVisible(true);
      active.removeFromParent();
      getItem(insertCol).insert(active, insertRow);
      active.repaint();
      
      fireEvent(Events.Drop, new PortalEvent(this, active, startCol, startRow, insertCol, insertRow));
    }
    active.setVisible(true);
    active = null;
    insertCol = -1;
    insertRow = -1;
    if (autoScroll) {
      scrollSupport.stop();
    }
  }

  protected void onDragLeave(DragEvent de) {
    if (autoScroll) {
      scrollSupport.stop();
    }
  }

  protected void onDragMove(DragEvent de) {
    int col = getColumn(de.getClientX());

    int row = getRowPosition(col, de.getClientY());
    int adjustRow = row;
    if (startCol == col && row > startRow) {
      adjustRow--;
    }
    if (col != insertCol || row != insertRow) {
      PortalEvent pe = new PortalEvent(this, active, startCol, startRow, col, adjustRow);
      if (fireEvent(Events.ValidateDrop, pe)) {
        addInsert(col, row);
      } else {
        insertCol = startCol;
        insertRow = startRow;
      }
    }
  }

  protected void onDragStart(DragEvent de) {
    active = (Portlet) de.getComponent();

    if (dummy == null) {
      dummy = new El("<div class='x-portal-insert' style='margin-bottom: 10px'><div></div></div>");
      dummy.setStyleName("x-portal-insert");
    }

    dummy.setStyleAttribute("padding", active.el().getStyleAttribute("padding"));

    int h = active.el().getHeight() - active.el().getFrameWidth("tb");
    dummy.firstChild().setHeight(h);

    startColumns = new ArrayList<Integer>();
    for (int i = 0; i < numColumns; i++) {
      LayoutContainer con = getItem(i);
      int x = con.getAbsoluteLeft();
      startColumns.add(x);
    }
    startCol = getColumn(de.getX());
    startRow = getRow(startCol, de.getY());
    active.setVisible(false);
    addInsert(startCol, startRow);

    if (autoScroll) {
      scrollSupport.start();
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);

    if (scrollSupport == null) {
      scrollSupport = new ScrollSupport(el());
    } else if (scrollSupport.getScrollElement() == null) {
      scrollSupport.setScrollElement(el());
    }
  }

  protected void addInsert(int col, int row) {
    insertCol = col;
    insertRow = row;
    
    LayoutContainer lc = getItem(insertCol);

    dummy.removeFromParent();
    dummy.insertInto(lc.el().dom, row);
  }

  private int getColumn(int x) {
    x += XDOM.getBodyScrollLeft();
    for (int i = startColumns.size() - 1; i >= 0; i--) {
      if (x > startColumns.get(i)) {
        return i;
      }
    }
    return 0;
  }

  private int getRow(int col, int y) {
    y += XDOM.getBodyScrollTop();
    LayoutContainer con = getItem(col);
    int count = con.getItemCount();

    for (int i = 0; i < count; i++) {
      Component c = con.getItem(i);
      int b = c.getAbsoluteTop();
      int t = b + c.getOffsetHeight();

      if (y < t) {
        return i;
      }
    }

    return 0;
  }

  private int getRowPosition(int col, int y) {
    y += XDOM.getBodyScrollTop();
    LayoutContainer con = getItem(col);
    List<Component> list = new ArrayList<Component>(con.getItems());
    int count = list.size();

    for (int i = 0; i < count; i++) {
      Component c = list.get(i);

      int b = c.getAbsoluteTop();
      int t = b + c.getOffsetHeight();
      int m = b + (c.getOffsetHeight() / 2);
      if (y < t) {
        if (y < m) {
          return i;
        } else {
          return i + 1;
        }
      }
    }
    return count;
  }
}

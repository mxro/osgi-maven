/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import java.util.Arrays;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ColumnModelEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.grid.ColumnHeader.Head;
import com.extjs.gxt.ui.client.widget.selection.AbstractStoreSelectionModel;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

/**
 * Grid selection model.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>AbstractStoreSelectionModel BeforeSelect</dd>
 * <dd>AbstractStoreSelectionModel SelectionChange</dd>
 * </dl>
 */
@SuppressWarnings("rawtypes")
public class GridSelectionModel<M extends ModelData> extends AbstractStoreSelectionModel<M> implements
    Listener<BaseEvent> {

  public static class Callback {

    private GridSelectionModel sm;

    public Callback(GridSelectionModel sm) {
      this.sm = sm;
    }

    public boolean isSelectable(int row, int cell, boolean acceptsNav) {
      return sm.isSelectable(row, cell, acceptsNav);
    }
  }

  public static class Cell {
    public int cell;
    public int row;

    public Cell(int row, int cell) {
      this.row = row;
      this.cell = cell;
    }

  }

  protected boolean enableNavKeys = true;
  protected Grid<M> grid;
  protected boolean grouped = false;
  protected GroupingView groupingView;
  protected KeyNav<GridEvent<M>> keyNav = new KeyNav<GridEvent<M>>() {

    @Override
    public void onDown(GridEvent<M> e) {
      onKeyDown(e);
    }

    @Override
    public void onKeyPress(GridEvent<M> ce) {
      GridSelectionModel.this.onKeyPress(ce);
    }

    @Override
    public void onLeft(GridEvent<M> ce) {
      onKeyLeft(ce);
    }

    @Override
    public void onRight(GridEvent<M> ce) {
      onKeyRight(ce);
    }

    @Override
    public void onUp(GridEvent<M> e) {
      onKeyUp(e);
    }

  };
  protected ListStore<M> listStore;
  protected Element selectedGroup;
  protected Head selectedHeader;

  private Callback callback = new Callback(this);
  private boolean moveEditorOnEnter;

  @SuppressWarnings("unchecked")
  @Override
  public void bind(Store store) {
    super.bind(store);
    if (store instanceof ListStore) {
      listStore = (ListStore<M>) store;
    } else {
      listStore = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void bindGrid(Grid grid) {
    if (this.grid != null) {
      this.grid.removeListener(Events.RowMouseDown, this);
      this.grid.removeListener(Events.RowClick, this);
      this.grid.removeListener(Events.ContextMenu, this);
      this.grid.removeListener(Events.ViewReady, this);
      this.grid.getView().removeListener(Events.RowUpdated, this);
      this.grid.getView().removeListener(Events.Refresh, this);
      this.grid.getColumnModel().removeListener(Events.HiddenChange, this);
      keyNav.bind(null);
      bind(null);
    }
    this.grid = grid;
    if (grid != null) {
      grid.addListener(Events.RowMouseDown, this);
      grid.addListener(Events.RowClick, this);
      grid.addListener(Events.ContextMenu, this);
      grid.addListener(Events.ViewReady, this);
      grid.getView().addListener(Events.RowUpdated, this);
      grid.getView().addListener(Events.Refresh, this);
      grid.getColumnModel().addListener(Events.HiddenChange, this);
      keyNav.bind(grid);
      bind(grid.getStore());
      grouped = grid.getView() instanceof GroupingView;
      if (grouped) groupingView = (GroupingView) grid.getView();
    }
  }

  @SuppressWarnings("unchecked")
  public void handleEvent(BaseEvent e) {
    EventType type = e.getType();
    if (type == Events.RowMouseDown) {
      handleMouseDown((GridEvent) e);
    } else if (type == Events.RowClick) {
      handleMouseClick((GridEvent) e);
    } else if (type == Events.RowUpdated) {
      onRowUpdated((GridEvent) e);
    } else if (type == Events.Refresh || type == Events.ViewReady) {
      refresh();
      if (getLastFocused() != null) {
        grid.getView().onHighlightRow(listStore.indexOf(getLastFocused()), true);
      }
    } else if (type == Events.HiddenChange) {
      handleColumnHidden((ColumnModelEvent) e);
    }
  }

  /**
   * Returns true of the editor moves on enter.
   * 
   * @return true if editor moves on enter
   */
  public boolean isMoveEditorOnEnter() {
    return moveEditorOnEnter;
  }

  public void onEditorKey(DomEvent e) {
    int k = e.getKeyCode();
    Cell newCell = null;
    CellEditor editor = grid.editSupport.getActiveEditor();
    switch (k) {
      case KeyCodes.KEY_ENTER:
      case KeyCodes.KEY_TAB:
        e.stopEvent();
        if (editor != null) {
          editor.completeEdit();
        }
        if ((k == KeyCodes.KEY_ENTER && moveEditorOnEnter) || k == KeyCodes.KEY_TAB) {
          if (e.isShiftKey()) {
            newCell = grid.walkCells(editor.row, editor.col - 1, -1, callback, true);
          } else {
            newCell = grid.walkCells(editor.row, editor.col + 1, 1, callback, true);
          }
        }
        break;
      case KeyCodes.KEY_ESCAPE:
        if (editor != null) {
          editor.cancelEdit();
        }
        break;
    }
    if (newCell != null) {
      grid.editSupport.startEditing(newCell.row, newCell.cell);
    } else {
      if (k == KeyCodes.KEY_ENTER || k == KeyCodes.KEY_TAB || k == KeyCodes.KEY_ESCAPE) {
        grid.getView().focusCell(editor.row, editor.col, false);
      }
    }
  }

  /**
   * Selects the next row.
   * 
   * @param keepexisting true to keep existing selections
   */
  public void selectNext(boolean keepexisting) {
    if (hasNext()) {
      int idx = listStore.indexOf(lastSelected) + 1;
      select(idx, keepexisting);
      grid.getView().focusRow(idx);
    }
  }

  /**
   * Selects the previous row.
   * 
   * @param keepexisting true to keep existing selections
   */
  public void selectPrevious(boolean keepexisting) {
    if (hasPrevious()) {
      int idx = listStore.indexOf(lastSelected) - 1;
      select(idx, keepexisting);
      grid.getView().focusRow(idx);
    }
  }

  /**
   * Set this to true to move the editor to the next editable cell on pressing
   * enter.
   * 
   * @param moveEditorOnEnter true to move the editor on pressing enter.
   */
  public void setMoveEditorOnEnter(boolean moveEditorOnEnter) {
    this.moveEditorOnEnter = moveEditorOnEnter;
  }

  protected void handleColumnHidden(ColumnModelEvent e) {
    ColumnHeader header = grid.getView().getHeader();
    if (header != null) {
      int col = e.getColIndex();
      Head h = header.getHead(col);
      if (h == selectedHeader) {
        selectedHeader = null;
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void handleMouseClick(GridEvent<M> e) {
    if (isLocked() || isInput(e.getTarget())) {
      return;
    }
    if (e.getRowIndex() == -1) {
      deselectAll();
      return;
    }
    if (selectionMode == SelectionMode.MULTI) {
      M sel = listStore.getAt(e.getRowIndex());
      if (e.isControlKey() && isSelected(sel)) {
        doDeselect(Arrays.asList(sel), false);
      } else if (e.isControlKey()) {
        doSelect(Arrays.asList(sel), true, false);
        grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
      } else if (isSelected(sel) && !e.isShiftKey() && !e.isControlKey() && selected.size() > 1) {
        doSelect(Arrays.asList(sel), false, false);
        grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
      }
    }

  }

  @SuppressWarnings("unchecked")
  protected void handleMouseDown(GridEvent<M> e) {
    if (e.getRowIndex() == -1 || isLocked() || isInput(e.getTarget())) {
      return;
    }
    if (e.isRightClick()) {
      if (selectionMode != SelectionMode.SINGLE && isSelected(listStore.getAt(e.getRowIndex()))) {
        return;
      }
      select(e.getRowIndex(), false);
      grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
    } else {
      M sel = listStore.getAt(e.getRowIndex());
      if (selectionMode == SelectionMode.SIMPLE) {
        if (!isSelected(sel)) {
          select(sel, true);
          grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
        }

      } else if (selectionMode == SelectionMode.SINGLE) {
        if (e.isControlKey() && isSelected(sel)) {
          deselect(sel);
        } else if (!isSelected(sel)) {
          select(sel, false);
          grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
        }
      } else if (!e.isControlKey()) {
        if (e.isShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int index = e.getRowIndex();
          select(last, index, e.isControlKey());
          grid.getView().focusCell(index, e.getColIndex(), false);
        } else if (!isSelected(sel)) {
          doSelect(Arrays.asList(sel), false, false);
          grid.getView().focusCell(e.getRowIndex(), e.getColIndex(), false);
        }
      }
    }
  }

  protected boolean hasNext() {
    return lastSelected != null && listStore.indexOf(lastSelected) < (listStore.getCount() - 1);
  }

  protected boolean hasPrevious() {
    return lastSelected != null && listStore.indexOf(lastSelected) > 0;
  }

  protected boolean isInput(Element target) {
    String tag = target.getTagName();
    return "input".equalsIgnoreCase(tag) || "textarea".equalsIgnoreCase(tag);
  }

  protected boolean isSelectable(int row, int cell, boolean acceptsNav) {
    if (acceptsNav) {
      return !grid.getColumnModel().isHidden(cell) && grid.getColumnModel().isCellEditable(cell);
    } else {
      return !grid.getColumnModel().isHidden(cell);
    }
  }

  protected void onKeyDown(GridEvent<M> e) {
    if (GXT.isFocusManagerEnabled()) {
      if (selectedGroup == null && (selectedHeader != null || selected.size() == 0)) {
        e.cancelBubble();
        if (e.isAltKey()) {
          grid.getView().getHeader().showColumnMenu(selectedHeader.column);
          return;
        }
        if (selectedHeader != null) {
          selectedHeader.deactivate();
        }
        select(0, false);
        return;
      }
      if (grouped) {
        GroupingView view = (GroupingView) grid.getView();
        NodeList<Element> groups = view.getGroups().cast();
        int gc = view.getGroups().getLength();

        if (selectedGroup != null) {
          int gindex = indexOf(groups, selectedGroup);
          if (!view.isExpanded(selectedGroup)) {
            if (gindex < gc - 1) {
              view.onGroupSelect(selectedGroup, false);
              selectedGroup = groups.getItem(gindex + 1);
              view.onGroupSelect(selectedGroup, true);
            }
            return;
          }
          view.onGroupSelect(selectedGroup, false);
          Element r = view.getGroupRow(selectedGroup, 0).cast();

          selectedGroup = null;

          if (r != null) {
            int idx = view.findRowIndex(r);
            select(idx, false);
            view.focusRow(idx);
            return;
          }
        }
        if (lastSelected != null) {
          Element row = view.getRow(lastSelected).cast();
          Element group = view.findGroup(row).cast();

          int totalGroups = groups.getLength();
          int groupIndex = indexOf(groups, group);

          NodeList<Element> groupRows = group.getChildNodes().getItem(1).getChildNodes().cast();

          int rowsInGroup = group.getChildNodes().getItem(1).getChildNodes().getLength();
          int rowInGroupIndex = indexOf(groupRows, row);
          if (rowInGroupIndex == rowsInGroup - 1) {
            if (groupIndex < totalGroups - 1) {
              deselectAll();
              selectedGroup = groups.getItem(groupIndex + 1);
              view.onGroupSelect(selectedGroup, true);
              return;

            }
          }
        }
      }
    }
    if (!e.isControlKey() && selected.size() == 0 && getLastFocused() == null) {
      select(0, false);
    } else {
      int idx = listStore.indexOf(getLastFocused());
      if (idx >= 0) {
        if (e.isControlKey() || (e.isShiftKey() && isSelected(listStore.getAt(idx + 1)))) {
          if (!e.isControlKey()) {
            deselect(idx);
          }

          M lF = listStore.getAt(idx + 1);
          if (lF != null) {
            setLastFocused(lF);
            grid.getView().focusCell(idx + 1, 0, false);
          }

        } else {
          if (e.isShiftKey() && lastSelected != getLastFocused()) {
            select(listStore.indexOf(lastSelected), idx + 1, true);
            grid.getView().focusCell(idx + 1, 0, false);
          } else {
            if (idx + 1 < listStore.getCount()) {
              selectNext(e.isShiftKey());
              grid.getView().focusCell(idx + 1, 0, false);
            }
          }
        }
      }
    }

    e.preventDefault();
  }

  protected void onKeyLeft(GridEvent<M> ce) {
    if (GXT.isFocusManagerEnabled() && selectedHeader != null) {
      ColumnHeader ch = grid.getView().getHeader();
      int idx = ch.indexOf(selectedHeader) - 1;
      ColumnConfig config = grid.getColumnModel().getColumn(idx);
      while (config != null) {
        if (!config.isHidden()) {
          Head h = getHead(idx, false);
          selectedHeader = h;
          grid.getView().getHeader().selectHeader(idx);
          break;
        } else {
          idx--;
          config = grid.getColumnModel().getColumn(idx);
        }
      }
    }
    if (GXT.isFocusManagerEnabled() && selectedGroup != null) {
      groupingView.toggleGroup(selectedGroup, false);
    }
  }

  @SuppressWarnings("unchecked")
  protected void onKeyPress(GridEvent<M> e) {
    int kc = e.getKeyCode();
    if (GXT.isFocusManagerEnabled()) {
      if (selectedHeader != null) {
        if (kc == KeyCodes.KEY_ENTER) {
          grid.getView().onHeaderClick((Grid) grid, grid.getColumnModel().indexOf(selectedHeader.config));
          return;
        } else if (kc == 32) {
          ColumnHeader ch = grid.getView().getHeader();
          int idx = ch.indexOf(selectedHeader);
          String id = grid.getColumnModel().getColumnId(idx);
          if (id == null || !id.equals("checker")) {
            grid.getView().getHeader().showColumnMenu(idx);
          }
          return;
        }
      }
    }
    if (lastSelected != null && enableNavKeys) {
      if (kc == KeyCodes.KEY_PAGEUP) {
        e.stopEvent();
        select(0, false);
        grid.getView().focusRow(0);
      } else if (kc == KeyCodes.KEY_PAGEDOWN) {
        e.stopEvent();
        int idx = listStore.indexOf(listStore.getAt(listStore.getCount() - 1));
        select(idx, false);
        grid.getView().focusRow(idx);
      }
    }
    // if space bar is pressed
    if (e.getKeyCode() == 32) {
      if (getLastFocused() != null) {
        if (e.isShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int i = listStore.indexOf(getLastFocused());
          select(last, i, e.isControlKey());
          grid.getView().focusCell(i, 0, false);
        } else {
          if (isSelected(getLastFocused())) {
            deselect(getLastFocused());
          } else {
            select(getLastFocused(), true);
            grid.getView().focusCell(listStore.indexOf(getLastFocused()), 0, false);
          }
        }
      }
    }
  }

  protected void onKeyRight(GridEvent<M> ce) {
    if (GXT.isFocusManagerEnabled() && selectedHeader != null) {
      ColumnHeader ch = grid.getView().getHeader();
      int idx = ch.indexOf(selectedHeader) + 1;
      ColumnConfig config = grid.getColumnModel().getColumn(idx);
      while (config != null) {
        if (!config.isHidden()) {
          Head h = getHead(idx, false);
          selectedHeader = h;
          grid.getView().getHeader().selectHeader(idx);
          break;
        } else {
          idx++;
          config = grid.getColumnModel().getColumn(idx);
        }
      }
    }
    if (GXT.isAriaEnabled() && selectedGroup != null) {
      groupingView.toggleGroup(selectedGroup, true);
    }
  }

  protected void onKeyUp(GridEvent<M> e) {
    if (GXT.isFocusManagerEnabled()) {
      if (selectedHeader != null) {
        return;
      }
      if (listStore.indexOf(lastSelected) == 0 && !grouped) {
        deselectAll();
        ColumnHeader header = grid.getView().getHeader();
        Head h = getHead(0, false);
        if (h != null) {
          selectedHeader = h;
          header.selectHeader(header.indexOf(h));
        }
      }
      if (grouped) {
        GroupingView view = (GroupingView) grid.getView();
        NodeList<Element> groups = view.getGroups().cast();
        if (selectedGroup != null) {
          int gindex = indexOf(groups, selectedGroup);

          if (gindex == 0) {
            deselectAll();
            ColumnHeader header = grid.getView().getHeader();
            Head h = getHead(0, false);
            if (h != null) {
              selectedHeader = h;
              header.selectHeader(header.indexOf(h));
            }
          }

          view.onGroupSelect(selectedGroup, false);
          selectedGroup = null;

          if (gindex > 0) {
            selectedGroup = groups.getItem(gindex - 1);
            if (view.isExpanded(selectedGroup)) {
              int grows = view.getGroupRowCount(selectedGroup);
              Element r = view.getGroupRow(selectedGroup, grows - 1).cast();
              selectedGroup = null;
              select(view.findRowIndex(r), false);
              view.focusRow(view.findRowIndex(r));

            } else {
              view.onGroupSelect(selectedGroup, true);
            }
            return;
          }
        }
        if (lastSelected != null) {
          Element row = view.getRow(lastSelected).cast();
          Element group = view.findGroup(row).cast();
          if (row == view.getGroupRow(group, 0)) {
            deselectAll();
            selectedGroup = group;
            view.onGroupSelect(selectedGroup, true);
            return;
          }
        }
      }
    }
    int idx = listStore.indexOf(getLastFocused());
    if (idx >= 0) {
      if (e.isControlKey() || (e.isShiftKey() && isSelected(listStore.getAt(idx - 1)))) {
        if (!e.isControlKey()) {
          deselect(idx);
        }

        M lF = listStore.getAt(idx - 1);
        if (lF != null) {
          setLastFocused(lF);
          grid.getView().focusCell(idx - 1, 0, false);
        }

      } else {

        if (e.isShiftKey() && lastSelected != getLastFocused()) {
          select(listStore.indexOf(lastSelected), idx - 1, true);
          grid.getView().focusCell(idx - 1, 0, false);
        } else {
          if (idx > 0) {
            selectPrevious(e.isShiftKey());
            grid.getView().focusCell(idx - 1, 0, false);
          }
        }

      }
    }

    e.preventDefault();
  }

  @Override
  protected void onLastFocusChanged(M oldFocused, M newFocused) {
    int i;
    if (oldFocused != null) {
      i = listStore.indexOf(oldFocused);
      if (i >= 0) {
        grid.getView().onHighlightRow(i, false);
      }
    }
    if (newFocused != null) {
      i = listStore.indexOf(newFocused);
      if (i >= 0) {
        grid.getView().onHighlightRow(i, true);
      }
    }
  }

  protected void onRowUpdated(GridEvent<M> ge) {
    if (isSelected(ge.getModel())) {
      onSelectChange(ge.getModel(), true);
    }
    if (getLastFocused() == ge.getModel()) {
      setLastFocused(getLastFocused());
    }
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    int idx = listStore.indexOf(model);
    if (idx != -1) {
      if (GXT.isFocusManagerEnabled() && selectedHeader != null) {
        selectedHeader = null;
        FocusFrame.get().frame(grid);
      }
      if (select) {
        grid.getView().onRowSelect(idx);
      } else {
        grid.getView().onRowDeselect(idx);
      }
    }
  }

  private Head getHead(int index, boolean back) {
    ColumnHeader header = grid.getView().getHeader();
    int cols = grid.getColumnModel().getColumnCount();

    if (!back) {
      for (int i = index; i < cols; i++) {
        ColumnConfig config = grid.getColumnModel().getColumn(i);
        if (!config.isHidden() && !config.ariaIgnore) {
          return header.getHead(i);
        }
      }
    } else {
      for (int i = index; i > -1; i--) {
        ColumnConfig config = grid.getColumnModel().getColumn(i);
        if (!config.isHidden() && !config.ariaIgnore) {
          return header.getHead(i);
        }
      }
    }
    return null;
  }

  private int indexOf(NodeList<Element> elems, Element elem) {
    for (int i = 0; i < elems.getLength(); i++) {
      if (elems.getItem(i) == elem) {
        return i;
      }
    }
    return -1;
  }
}

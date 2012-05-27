/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.Arrays;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.selection.AbstractStoreSelectionModel;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

/**
 * ListView selection model.
 */
public class ListViewSelectionModel<M extends ModelData> extends AbstractStoreSelectionModel<M> implements
    Listener<ListViewEvent<M>> {

  protected boolean enableNavKeys = true;
  protected KeyNav<ComponentEvent> keyNav = new KeyNav<ComponentEvent>() {

    @Override
    public void onDown(ComponentEvent e) {
      if (isVertical) {
        onKeyDown(e);
      }
    }

    @Override
    public void onKeyPress(ComponentEvent ce) {
      ListViewSelectionModel.this.onKeyPress(ce);
    }

    @Override
    public void onLeft(ComponentEvent e) {
      if (!isVertical) {
        onKeyUp(e);
      }
    }

    @Override
    public void onRight(ComponentEvent e) {
      if (!isVertical) {
        onKeyDown(e);
      }
    }

    @Override
    public void onUp(ComponentEvent e) {
      if (isVertical) {
        onKeyUp(e);
      }
    }

  };
  protected ListStore<M> listStore;
  protected ListView<M> listView;
  private boolean isVertical = true;

  /**
   * Binds the list view to the selection model.
   * 
   * @param listView the list view
   */
  public void bindList(ListView<M> listView) {
    if (this.listView != null) {
      this.listView.removeListener(Events.OnMouseDown, this);
      this.listView.removeListener(Events.OnClick, this);
      this.listView.removeListener(Events.RowUpdated, this);
      this.listView.removeListener(Events.Refresh, this);
      this.listView.removeListener(Events.Render, this);
      keyNav.bind(null);
      this.listStore = null;
      bind(null);
    }
    this.listView = listView;
    if (listView != null) {
      listView.addListener(Events.OnMouseDown, this);
      listView.addListener(Events.OnClick, this);
      listView.addListener(Events.Refresh, this);
      listView.addListener(Events.RowUpdated, this);
      listView.addListener(Events.Render, this);
      keyNav.bind(listView);
      bind(listView.getStore());
      this.listStore = listView.getStore();
    }
  }

  public void handleEvent(ListViewEvent<M> e) {
    EventType type = e.getType();
    if (type == Events.OnMouseDown) {
      handleMouseDown(e);
    } else if (type == Events.OnClick) {
      handleMouseClick(e);
    } else if (type == Events.RowUpdated) {
      onRowUpdated(e);
    } else if (type == Events.Refresh || type == Events.Render) {
      refresh();
      if (getLastFocused() != null) {
        listView.onHighlightRow(listStore.indexOf(getLastFocused()), true);
      }
    }
  }

  /**
   * Returns true if up and down arrow keys are used for navigation. Else left
   * and right arrow keys are used.
   * 
   * @return the isVertical
   */
  public boolean isVertical() {
    return isVertical;
  }

  /**
   * Sets if up and down arrow keys or left and right arrow keys should be used
   * (defaults to true).
   * 
   * @param isVertical the isVertical to set
   */
  public void setVertical(boolean isVertical) {
    this.isVertical = isVertical;
  }

  @SuppressWarnings("unchecked")
  protected void handleMouseClick(ListViewEvent<M> e) {
    if (isLocked() || isInput(e.getTarget())) {
      return;
    }
    if (e.getIndex() == -1) {
      deselectAll();
      return;
    }
    if (selectionMode == SelectionMode.MULTI) {
      M sel = listStore.getAt(e.getIndex());
      if (e.isControlKey() && isSelected(sel)) {
        doDeselect(Arrays.asList(sel), false);
      } else if (e.isControlKey()) {
        doSelect(Arrays.asList(sel), true, false);
        listView.focusItem(e.getIndex());
      } else if (isSelected(sel) && !e.isShiftKey() && !e.isControlKey() && selected.size() > 1) {
        doSelect(Arrays.asList(sel), false, false);
        listView.focusItem(e.getIndex());
      }
    }

  }

  @SuppressWarnings("unchecked")
  protected void handleMouseDown(ListViewEvent<M> e) {
    if (e.getIndex() == -1 || isLocked() || isInput(e.getTarget())) {
      return;
    }
    if (e.isRightClick()) {
      if (selectionMode != SelectionMode.SINGLE && isSelected(listStore.getAt(e.getIndex()))) {
        return;
      }
      select(e.getIndex(), false);
      listView.focusItem(e.getIndex());
    } else {
      M sel = listStore.getAt(e.getIndex());
      if (selectionMode == SelectionMode.SIMPLE) {
        if (!isSelected(sel)) {
          select(sel, true);
          listView.focusItem(e.getIndex());
        }

      } else if (selectionMode == SelectionMode.SINGLE) {
        if (e.isControlKey() && isSelected(sel)) {
          deselect(sel);
        } else if (!isSelected(sel)) {
          select(sel, false);
          listView.focusItem(e.getIndex());
        }
      } else if (!e.isControlKey()) {
        if (e.isShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int index = e.getIndex();
          select(last, index, e.isControlKey());
          listView.focusItem(last);
        } else if (!isSelected(sel)) {
          doSelect(Arrays.asList(sel), false, false);
          listView.focusItem(e.getIndex());
        }
      }
    }
  }

  protected boolean isInput(Element target) {
    String tag = target.getTagName();
    return "INPUT".equals(tag) || "TEXTAREA".equals(tag);
  }

  protected void onKeyDown(ComponentEvent e) {
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
            listView.focusItem(idx + 1);
          }

        } else {
          if (e.isShiftKey() && lastSelected != getLastFocused()) {
            select(listStore.indexOf(lastSelected), idx + 1, true);
            listView.focusItem(idx + 1);
          } else {
            if (idx + 1 < listStore.getCount()) {
              select(idx + 1, e.isShiftKey());
              listView.focusItem(idx + 1);
            }

          }

        }
      }
    }

    e.preventDefault();
  }

  protected void onKeyPress(ComponentEvent e) {
    if (lastSelected != null && enableNavKeys) {
      int kc = e.getKeyCode();
      if (kc == KeyCodes.KEY_PAGEUP || kc == KeyCodes.KEY_HOME) {
        e.stopEvent();
        select(0, false);
        listView.focusItem(0);
      } else if (kc == KeyCodes.KEY_PAGEDOWN || kc == KeyCodes.KEY_END) {
        e.stopEvent();
        int idx = listStore.indexOf(listStore.getAt(listStore.getCount() - 1));
        select(idx, false);
        listView.focusItem(idx);
      }
    }
    // if space bar is pressed
    if (e.getKeyCode() == 32) {
      if (getLastFocused() != null) {
        if (e.isShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int i = listStore.indexOf(getLastFocused());
          select(last, i, e.isControlKey());
          listView.focusItem(i);
        } else {
          if (isSelected(getLastFocused())) {
            deselect(getLastFocused());
          } else {
            select(getLastFocused(), true);
            listView.focusItem(listStore.indexOf(getLastFocused()));
          }
        }
      }
    }
  }

  protected void onKeyUp(ComponentEvent e) {
    int idx = listStore.indexOf(getLastFocused());
    if (idx >= 0) {
      if (e.isControlKey() || (e.isShiftKey() && isSelected(listStore.getAt(idx - 1)))) {
        if (!e.isControlKey()) {
          deselect(idx);
        }

        M lF = listStore.getAt(idx - 1);
        if (lF != null) {
          setLastFocused(lF);
          listView.focusItem(idx - 1);
        }

      } else {

        if (e.isShiftKey() && lastSelected != getLastFocused()) {
          select(listStore.indexOf(lastSelected), idx - 1, true);
          listView.focusItem(idx - 1);
        } else {
          if (idx > 0) {
            select(idx - 1, e.isShiftKey());
            listView.focusItem(idx - 1);
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
        listView.onHighlightRow(i, false);
      }
    }
    if (newFocused != null) {
      i = listStore.indexOf(newFocused);
      if (i >= 0) {
        listView.onHighlightRow(i, true);
      }
    }
  }

  protected void onRowUpdated(ListViewEvent<M> ge) {
    if (isSelected(ge.getModel())) {
      onSelectChange(ge.getModel(), true);
    }
    if (getLastFocused() == ge.getModel()) {
      setLastFocused(getLastFocused());
    }
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    listView.onSelectChange(model, select);
  }

}

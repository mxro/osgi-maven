/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnHeader.Head;
import com.google.gwt.user.client.Event;

/**
 * A grid selection model and component plugin. To use, add the column config to
 * the column model using {@link #getColumn()} and add this object to the grids
 * plugin.
 * 
 * <p>
 * This selection mode defaults to SelectionMode.MULTI and also supports
 * SelectionMode.SIMPLE. With SIMPLE, the control and shift keys do not need to
 * be pressed for multiple selections.
 * 
 * @param <M> the model data type
 */
public class CheckBoxSelectionModel<M extends ModelData> extends GridSelectionModel<M> implements ComponentPlugin {

  protected ColumnConfig config;
  protected String headerCheckTitle = "Select All";

  public CheckBoxSelectionModel() {
    super();
    config = newColumnConfig();
    config.setId("checker");
    config.setWidth(20);
    config.setSortable(false);
    config.setResizable(false);
    config.setFixed(true);
    config.setMenuDisabled(true);
    config.setDataIndex("");
    config.setRenderer(new GridCellRenderer<M>() {
      public String render(M model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<M> store,
          Grid<M> grid) {
        config.cellAttr = "rowspan='2'";
        return "<div class='x-grid3-row-checker'>&#160;</div>";
      }
    });
  }

  /**
   * Returns the column config.
   * 
   * @return the column config
   */
  public ColumnConfig getColumn() {
    return config;
  }

  @SuppressWarnings("unchecked")
  public void init(Component component) {
    this.grid = (Grid<M>) component;
    Listener<GridEvent<M>> listener = new Listener<GridEvent<M>>() {
      public void handleEvent(GridEvent<M> e) {
        if (e.getType() == Events.HeaderClick) {
          onHeaderClick(e);
        } else if (e.getType() == Events.ViewReady) {
          updateHeaderCheckBox();

          Head h = grid.getView().getHeader().getHead(grid.getColumnModel().indexOf(config));
          if (h != null) {
            h.getElement().removeAttribute("aria-haspopup");
            if (GXT.isAriaEnabled()) {
              h.getElement().setTitle(headerCheckTitle);
            }
          }

        } else if (e.getEventTypeInt() == Event.ONKEYPRESS) {
          if (selectedHeader != null && e.getKeyCode() == 32) {
            int idx = grid.getView().getHeader().indexOf(selectedHeader);
            if (grid.getColumnModel().getColumn(idx) == config) {
              boolean isChecked = selectedHeader.el().getParent().hasStyleName("x-grid3-hd-checker-on");
              Head h = selectedHeader;
              h.getElement().getFirstChildElement().setAttribute("aria-selected", isChecked ? "true" : "false");
              if (isChecked) {
                setChecked(false);
                deselectAll();
              } else {
                setChecked(true);
                selectAll();
              }
              selectedHeader = h;
              grid.getView().getHeader().selectHeader(selectedHeader.column);
            }
          }
        }
      }
    };
    grid.addListener(Events.HeaderClick, listener);
    grid.addListener(Events.ViewReady, listener);
    grid.addListener(Events.OnKeyPress, listener);
  }

  @Override
  protected void handleMouseClick(GridEvent<M> e) {
    if (e.getTarget().getClassName().equals("x-grid3-row-checker")) {
      return;
    }
    super.handleMouseClick(e);
  }

  @Override
  protected void handleMouseDown(GridEvent<M> e) {
    if (e.getEvent().getButton() == Event.BUTTON_LEFT && e.getTarget().getClassName().equals("x-grid3-row-checker")) {
      M m = listStore.getAt(e.getRowIndex());
      if (m != null) {
        if (isSelected(m)) {
          deselect(m);
        } else {
          select(m, true);
        }
      }
    } else {
      super.handleMouseDown(e);
    }
  }

  protected ColumnConfig newColumnConfig() {
    return new ColumnConfig();
  }

  @Override
  protected void onAdd(List<? extends M> models) {
    super.onAdd(models);
    updateHeaderCheckBox();
  }

  @Override
  protected void onClear(StoreEvent<M> se) {
    super.onClear(se);
    setChecked(false);
  }

  protected void onHeaderClick(GridEvent<M> e) {
    ColumnConfig c = grid.getColumnModel().getColumn(e.getColIndex());
    if (c == config) {
      El hd = e.getTargetEl().getParent();
      boolean isChecked = hd.hasStyleName("x-grid3-hd-checker-on");
      hd.dom.getFirstChildElement().setAttribute("aria-selected", isChecked ? "false" : "true");
      if (isChecked) {
        setChecked(false);
        deselectAll();
      } else {
        setChecked(true);
        selectAll();
      }
    }
  }

  @Override
  protected void onRemove(M model) {
    super.onRemove(model);
    updateHeaderCheckBox();
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    super.onSelectChange(model, select);
    updateHeaderCheckBox();
  }

  protected void setChecked(boolean checked) {
    if (grid.isViewReady()) {
      El hd = grid.getView().innerHd.child("div.x-grid3-hd-checker");
      if (hd != null) {
        hd.getParent().setStyleName("x-grid3-hd-checker-on", checked);
      }
    }
  }

  protected void updateHeaderCheckBox() {
    setChecked(getSelection().size() == listStore.getCount());
  }

}

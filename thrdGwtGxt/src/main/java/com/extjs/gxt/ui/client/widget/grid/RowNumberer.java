/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.google.gwt.dom.client.Element;

/**
 * A <code>ColumnConfig</code> that provides an automatic row numbering column.
 * 
 * <p />
 * By default, the row numbers will not be updated when models are added and
 * removed from the store after the grid has been rendered. The row numbers can
 * be recalculated by calling refresh(false) on the GridView.
 * 
 * <p />
 * RowNumberer is a ComponentPlugin and can be registered with the Grid, when
 * this is done, refresh(false) will automatically called when the store is
 * updated.
 * 
 * Code snippet:
 * 
 * <pre>
 * RowNumberer r = new RowNumberer();
 * 
 * List&lt;ColumnConfig&gt; configs = new ArrayList&lt;ColumnConfig&gt;();
 * configs.add(r);
 * 
 * Grid&lt;Stock&gt; grid = new Grid&lt;Stock&gt;(store, cm);
 * 
 * // add row numberer as grid plugin to have row numbers updated when
 * // store is modified (add, remove, and filtered)
 * grid.addPlugin(r);
 * </pre>
 */
public class RowNumberer extends ColumnConfig implements ComponentPlugin {
  private Grid<?> grid;
  private DelayedTask task;

  public RowNumberer() {
    setHeader("");
    setWidth(23);
    setSortable(false);
    setResizable(false);
    setFixed(true);
    setMenuDisabled(true);
    setDataIndex("");
    setId("numberer");

    setRenderer(new GridCellRenderer<ModelData>() {

      public String render(ModelData model, String property, ColumnData d, int rowIndex, int colIndex,
          ListStore<ModelData> store, Grid<ModelData> grid) {
        d.cellAttr = "rowspan='2'";
        return String.valueOf(rowIndex + 1);
      }
    });
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void init(Component component) {
    grid = (Grid) component;
    grid.getStore().addStoreListener(new StoreListener() {
      public void storeAdd(StoreEvent se) {
        doRefresh();
      }

      public void storeFilter(StoreEvent se) {
        doRefresh();
      }

      public void storeRemove(StoreEvent se) {
        doRefresh();
      }

    });

    task = new DelayedTask(new Listener<BaseEvent>() {
      public void handleEvent(BaseEvent be) {
        int columnIndex = grid.getColumnModel().getIndexById(RowNumberer.this.getId());
        ColumnData columnData = grid.getView().getColumnData().get(columnIndex);
        ListStore<ModelData> store = (ListStore<ModelData>) grid.getStore();
        String property = RowNumberer.this.getDataIndex();
        for (int i = 0; i < grid.getStore().getCount(); i++) {
          Element cell = grid.getView().getCell(i, columnIndex);
          if (cell != null) {
            cell.getFirstChildElement().setInnerHTML(
                RowNumberer.this.getRenderer().render((ModelData) grid.getStore().getAt(i), property, columnData, i,
                    columnIndex, store, (Grid<ModelData>) grid).toString());
          }
        }
      }
    });

  }

  protected void doRefresh() {
    task.delay(50);
  }

}

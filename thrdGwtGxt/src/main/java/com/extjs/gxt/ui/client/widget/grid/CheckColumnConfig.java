/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;

/**
 * A <code>ColumnConfig</code> implementation that renders a checkbox in each
 * cell.
 * 
 * <p />
 * CheckColumnConfig is a <code>ComponentPlugin</code> and must be added to the
 * Grid's list of plugins (see @link {@link Grid#addPlugin(ComponentPlugin)}).
 * 
 * <p />
 * Disabled support code snippet:
 * 
 * <pre>
    CheckColumnConfig checkColumn = new CheckColumnConfig("indoor", "Indoor?", 55) {
      protected String getCheckState(ModelData model, String property, int rowIndex,
          int colIndex) {
        return "-disabled";
      }
    };
 * </pre>
 */
public class CheckColumnConfig extends ColumnConfig implements ComponentPlugin {

  protected Grid<ModelData> grid;

  /**
   * Creates a new check column config.
   */
  public CheckColumnConfig() {
    super();
    init();
    ariaIgnore = true;
  }

  /**
   * Creates a new check column config.
   * 
   * @param id the column id
   * @param name the column name
   * @param width the column width
   */
  public CheckColumnConfig(String id, String name, int width) {
    super(id, name, width);
    init();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void init(Component component) {
    this.grid = (Grid) component;
    grid.addListener(Events.CellMouseDown, new Listener<GridEvent>() {
      public void handleEvent(GridEvent e) {
        onMouseDown(e);
      }
    });
  }

  /**
   * Returns the css style name which contains a background image representing
   * the checkbox. This implementation returns "-on" or "" based on a boolean
   * model property. "-disabled" can be returned to render a disabled checkbox.
   * 
   * @param model the model
   * @param property the model property
   * @param rowIndex the row index
   * @param colIndex the cell index
   * @return the css style name
   */
  protected String getCheckState(ModelData model, String property, int rowIndex, int colIndex) {
    Boolean v = model.get(property);
    String on = (v != null && v) ? "-on" : "";
    return on;
  }

  protected void init() {
    setRenderer(new GridCellRenderer<ModelData>() {
      public String render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
          ListStore<ModelData> store, Grid<ModelData> grid) {
        return onRender(model, property, config, rowIndex, colIndex, store);
      }
    });
  }

  /**
   * Called when the cell is clicked.
   * 
   * @param ge the grid event
   */
  protected void onMouseDown(GridEvent<ModelData> ge) {
    El el = ge.getTargetEl();
    if (el != null && el.hasStyleName("x-grid3-cc-" + getId()) && !el.hasStyleName("x-grid3-check-col-disabled")) {
      ge.stopEvent();
      ModelData m = ge.getModel();
      Record r = grid.getView().ds.getRecord(m);
      Boolean b = (Boolean) m.get(getDataIndex());
      r.set(getDataIndex(), b == null ? true : !b);
    }
  }

  /**
   * Called to render each check cell.
   * 
   * @param model the model
   * @param property the model property
   * @param config the config object
   * @param rowIndex the row index
   * @param colIndex the column index
   * @param store the list store
   * @return the rendered HTML
   */
  protected String onRender(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
      ListStore<ModelData> store) {
    config.css = "x-grid3-check-col-td";
    String checked = getCheckState(model, property, rowIndex, colIndex);
    if (GXT.isAriaEnabled()) {
      config.cellAttr = "aria-checked=" + (checked.equals("-on") ? "true" : "false");
    }
    return "<div class='x-grid3-check-col" + " x-grid3-check-col" + checked + " x-grid3-cc-" + getId()
        + "'>&#160;</div>";
  }

}

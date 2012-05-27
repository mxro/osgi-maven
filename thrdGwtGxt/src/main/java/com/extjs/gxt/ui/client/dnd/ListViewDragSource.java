/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.user.client.Element;

/**
 * A <code>DragSource</code> implementation for ListViews. Supports both single
 * and multi-selections. The selected model instances will be set as the data to
 * be dragged.
 */
public class ListViewDragSource extends DragSource {

  protected ListView<ModelData> listView;

  /**
   * Creates a new list view drag source instance.
   * 
   * @param listView the source list view
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public ListViewDragSource(ListView listView) {
    super(listView);
    this.listView = listView;
    setStatusText("{0} items selected");
    
    listView.addListener(Events.OnMouseDown, new Listener<ComponentEvent>() {
      public void handleEvent(ComponentEvent be) {
        be.cancelBubble();
        ListViewDragSource.this.listView.focus();
      }
    });
  }

  /**
   * Returns the sources list view.
   * 
   * @return the list view
   */
  public ListView<ModelData> getListView() {
    return listView;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected void onDragDrop(DNDEvent e) {
    if (e.getOperation() == Operation.MOVE) {
      Object data = e.getData();
      if (data instanceof ModelData) {
        listView.getStore().remove((ModelData) data);
      } else if (data instanceof List) {
        List<ModelData> sel = (List) data;
        for (ModelData m : sel) {
          listView.getStore().remove(m);
        }
      }
    }
  }

  @Override
  protected void onDragStart(DNDEvent e) {
    Element r = listView.findElement((Element) e.getDragEvent().getStartElement());
    if (r == null) {
      e.setCancelled(true);
      return;
    }

    List<ModelData> sel = listView.getSelectionModel().getSelectedItems();
    if (sel.size() > 0) {
      e.setCancelled(false);
      e.setData(sel);

      if (getStatusText() == null) {
        e.getStatus().update(GXT.MESSAGES.grid_ddText(sel.size()));
      } else {
        e.getStatus().update(Format.substitute(getStatusText(), sel.size()));
      }
    }
  }

}

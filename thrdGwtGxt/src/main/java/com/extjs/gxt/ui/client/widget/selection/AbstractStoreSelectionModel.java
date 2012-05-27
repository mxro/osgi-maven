/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;

/**
 * Abstract base class for store based selection models.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>SelectionChange</b> : SelectionEvent(source, selection)<br>
 * <div>Fires after the selection changes.</div>
 * <ul>
 * <li>source : this</li>
 * <li>selection : the selected items</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeSelect</b> : SelectionEvent(source, model)<br>
 * <div>Fires before a row is selected. Listeners can cancel the action by
 * calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>source : this</li>
 * <li>model : the selected item</li>
 * <li>index : the row index</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * @param <M> the model type contained within the store
 */
@SuppressWarnings("unchecked")
public abstract class AbstractStoreSelectionModel<M extends ModelData> extends BaseObservable implements
    StoreSelectionModel<M>, SelectionProvider<M> {

  protected M lastSelected;
  protected boolean locked;
  protected List<M> selected = new ArrayList<M>();
  protected SelectionMode selectionMode = SelectionMode.MULTI;
  protected Store<M> store;

  protected StoreListener<M> storeListener = new StoreListener<M>() {
    @Override
    public void storeAdd(StoreEvent<M> se) {
      onAdd(se.getModels());
    }

    @Override
    public void storeClear(StoreEvent<M> se) {
      onClear(se);
    }

    @Override
    public void storeRemove(StoreEvent<M> se) {
      onRemove(se.getModel());
    }

    @Override
    public void storeUpdate(StoreEvent<M> se) {
      onUpdate(se.getModel());
    }

  };

  private M lastFocused;

  public void addSelectionChangedListener(SelectionChangedListener<M> listener) {
    addListener(Events.SelectionChange, listener);
  }

  @SuppressWarnings("rawtypes")
  public void bind(Store store) {
    if (this.store != null) {
      this.store.removeStoreListener(storeListener);
    }
    this.store = store;
    if (store != null) {
      store.addStoreListener(storeListener);
    }
  }

  @SuppressWarnings("rawtypes")
  public void deselect(int index) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore) store;
      M m = ls.getAt(index);
      if (m != null) {
        doDeselect(Arrays.asList(m), false);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  public void deselect(int start, int end) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore) store;
      List<M> list = new ArrayList<M>();
      for (int i = start; i < end; i++) {
        M m = ls.getAt(i);
        if (m != null) {
          list.add(m);
        }
      }
      doDeselect(list, false);
    }
  }

  public void deselect(List<M> items) {
    doDeselect(items, false);
  }

  public void deselect(M... items) {
    deselect(Arrays.asList(items));
  }

  public void deselect(M item) {
    deselect(Arrays.asList(item));
  }

  public void deselectAll() {
    doDeselect(new ArrayList<M>(selected), false);
  }

  public M getSelectedItem() {
    return lastSelected;
  }

  public List<M> getSelectedItems() {
    return new ArrayList<M>(selected);
  }

  public List<M> getSelection() {
    return getSelectedItems();
  }

  public SelectionMode getSelectionMode() {
    return selectionMode;
  }

  /**
   * Returns true if the selection model is locked.
   * 
   * @return the locked state
   */
  public boolean isLocked() {
    return locked;
  }

  public boolean isSelected(M item) {
    for (M m : selected) {
      if (store.equals(item, m)) {
        return true;
      }
    }
    return false;
  }

  public void refresh() {
    List<M> sel = new ArrayList<M>();
    boolean change = false;
    for (M m : selected) {
      M storeModel = store.findModel(m);
      if (storeModel != null) {
        sel.add(storeModel);
      }
    }
    if (sel.size() != selected.size()) {
      change = true;
    }
    selected.clear();
    lastSelected = null;
    setLastFocused(null);
    doSelect(sel, false, true);
    if (change) {
      fireSelectionChange();
    }
  }

  public void removeSelectionListener(SelectionChangedListener<M> listener) {
    removeListener(Events.SelectionChange, listener);
  }

  public void select(boolean keepExisting, M... items) {
    select(Arrays.asList(items), keepExisting);
  }

  public void select(int index, boolean keepExisting) {
    select(index, index, keepExisting);
  }

  @SuppressWarnings("rawtypes")
  public void select(int start, int end, boolean keepExisting) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore) store;
      List<M> sel = new ArrayList<M>();
      if (start <= end) {
        for (int i = start; i <= end; i++) {
          sel.add(ls.getAt(i));
        }
      } else {
        for (int i = start; i >= end; i--) {
          sel.add(ls.getAt(i));
        }
      }
      doSelect(sel, keepExisting, false);
    }
  }

  public void select(List<M> items, boolean keepExisting) {
    doSelect(items, keepExisting, false);
  }

  public void select(M item, boolean keepExisting) {
    select(Arrays.asList(item), keepExisting);
  }

  public void selectAll() {
    select(store.getModels(), false);
  }

  /**
   * True to lock the selection model. When locked, all selection changes are
   * disabled.
   * 
   * @param locked true to lock
   */
  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public void setSelection(List<M> selection) {
    select(selection, false);
  }

  public void setSelectionMode(SelectionMode selectionMode) {
    this.selectionMode = selectionMode;
  }

  protected void doDeselect(List<M> models, boolean supressEvent) {
    if (locked) return;
    boolean change = false;
    for (M m : models) {
      if (selected.remove(m)) {
        if (lastSelected == m) {
          lastSelected = selected.size() > 0 ? selected.get(selected.size() - 1) : null;
        }
        onSelectChange(m, false);
        change = true;
      }
    }
    if (!supressEvent && change) {
      fireSelectionChange();
    }
  }

  @SuppressWarnings("rawtypes")
  protected void doMultiSelect(List<M> models, boolean keepExisting, boolean supressEvent) {
    if (locked) return;
    boolean change = false;
    if (!keepExisting && selected.size() > 0) {
      change = true;
      doDeselect(new ArrayList<M>(selected), true);
    }
    for (M m : models) {
      SelectionEvent<M> e = new SelectionEvent<M>(this, m);
      boolean isListStore = false;
      if (store instanceof ListStore) {
        isListStore = true;
        ListStore<M> ls = (ListStore) store;
        e.setIndex(ls.indexOf(m));
      }
      if ((keepExisting && isSelected(m)) || (isListStore && e.getIndex() == -1)
          || (!supressEvent && !fireEvent(Events.BeforeSelect, e))) {
        continue;

      }
      change = true;
      lastSelected = m;

      selected.add(m);
      setLastFocused(lastSelected);
      onSelectChange(m, true);

    }

    if (change && !supressEvent) {
      fireSelectionChange();
    }
  }

  protected void doSelect(List<M> models, boolean keepExisting, boolean supressEvent) {
    if (locked) return;
    if (selectionMode == SelectionMode.SINGLE) {
      M m = models.size() > 0 ? models.get(0) : null;
      if (m != null) {
        doSingleSelect(m, supressEvent);
      }
    } else {
      doMultiSelect(models, keepExisting, supressEvent);
    }
  }

  @SuppressWarnings("rawtypes")
  protected void doSingleSelect(M model, boolean supressEvent) {
    if (locked) return;

    SelectionEvent<M> e = new SelectionEvent<M>(this, model);

    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore) store;
      e.setIndex(ls.indexOf(model));
    }

    if (e.getIndex() == -1 || isSelected(model) || !fireEvent(Events.BeforeSelect, e)) {
      return;
    }

    boolean change = false;
    if (selected.size() > 0 && !isSelected(model)) {
      doDeselect(Arrays.asList(lastSelected), true);
      change = true;
    }
    if (selected.size() == 0) {
      change = true;
    }
    selected.add(model);
    lastSelected = model;
    onSelectChange(model, true);
    setLastFocused(lastSelected);
    if (change && !supressEvent) {
      fireSelectionChange();
    }
  }

  @SuppressWarnings("rawtypes")
  protected void fireSelectionChange() {
    fireEvent(Events.SelectionChange, new SelectionChangedEvent(this, new ArrayList(selected)));
  }

  protected M getLastFocused() {
    return lastFocused;
  }

  protected void onAdd(List<? extends M> models) {

  }

  protected void onClear(StoreEvent<M> se) {
    int oldSize = selected.size();
    selected.clear();
    lastSelected = null;
    setLastFocused(null);
    if (oldSize > 0) fireSelectionChange();
  }

  protected void onLastFocusChanged(M oldFocused, M newFocused) {

  }

  protected void onRemove(M model) {
    if (locked) return;
    if (selected.remove(model)) {
      if (lastSelected == model) {
        lastSelected = null;
      }
      if (getLastFocused() == model) {
        setLastFocused(null);
      }
      fireSelectionChange();
    }
  }

  protected abstract void onSelectChange(M model, boolean select);

  protected void onUpdate(M model) {
    if (locked) return;
    for (int i = 0; i < selected.size(); i++) {
      M m = selected.get(i);
      if (store.equals(model, m)) {
        if (m != model) {
          selected.remove(m);
          selected.add(i, model);
        }
        if (lastSelected == m) {
          lastSelected = model;
        }
        break;
      }
    }
    if (model != getLastFocused() && store.equals(model, getLastFocused())) {
      lastFocused = model;
    }
  }

  protected void setLastFocused(M lastFocused) {
    M lF = this.lastFocused;
    this.lastFocused = lastFocused;

    onLastFocusChanged(lF, lastFocused);
  }

}

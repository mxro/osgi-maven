/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LiveGridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

/**
 * LiveGridView for displaying large amount of data. Data is loaded on demand as
 * the user scrolls the grid.
 */
public class LiveGridView extends GridView {

  protected El liveScroller;
  protected ListStore<ModelData> liveStore;
  protected int liveStoreOffset = 0;
  protected int totalCount = 0;
  protected int viewIndex;

  private int cacheSize = 200;
  private boolean isLoading;
  // to prevent flickering
  private boolean isMasked;
  private StoreListener<ModelData> liveStoreListener;
  private int loadDelay = 200;
  private PagingLoader<PagingLoadResult<ModelData>> loader;
  private int loaderOffset;
  private DelayedTask loaderTask;
  private double prefetchFactor = .2;
  private int rowHeight = 20;
  private int viewIndexReload = -1;

  /**
   * Returns the numbers of rows that should be cached.
   * 
   * @return the cache size
   */
  public int getCacheSize() {
    int c = -1;
    if (grid.isViewReady()) {
      c = getVisibleRowCount();
    }
    return Math.max(c, cacheSize);
  }

  /**
   * Returns the amount of time before loading is done.
   * 
   * @return the load delay in milliseconds
   */
  public int getLoadDelay() {
    return loadDelay;
  }

  /**
   * Returns the prefetchFactor.
   * 
   * @return the prefetchFactor
   */
  public double getPrefetchFactor() {
    return prefetchFactor;
  }

  /**
   * Returns the height of one row.
   * 
   * @return the height of one row
   */
  public int getRowHeight() {
    return rowHeight;
  }

  public int getVisibleRowCount() {
    int rh = getCalculatedRowHeight();
    int visibleHeight = getLiveScrollerHeight();
    return (int) ((visibleHeight < 1) ? 0 : Math.floor((double) visibleHeight / rh));
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void handleComponentEvent(GridEvent ge) {
    super.handleComponentEvent(ge);
    int type = ge.getEventTypeInt();
    Element target = ge.getTarget();
    if ((type == Event.ONSCROLL && liveScroller.dom.isOrHasChild(target))
        || (type == Event.ONMOUSEWHEEL && mainBody.dom.isOrHasChild(target))) {
      ge.stopEvent();
      if (type == Event.ONMOUSEWHEEL) {
        int v = ge.getEvent().getMouseWheelVelocityY() * getCalculatedRowHeight();
        liveScroller.setScrollTop(liveScroller.getScrollTop() + v);
      } else {
        updateRows((int) Math.ceil((double) liveScroller.getScrollTop() / getCalculatedRowHeight()), false);
      }
    }
  }

  /**
   * Refreshed the view. Reloads the store based on the current settings
   */
  public void refresh() {
    loadLiveStore(liveStoreOffset);
  }

  @Override
  public void refresh(boolean headerToo) {
    super.refresh(headerToo);
    if (headerToo) {
      positionLiveScroller();
    }
    if (!preventScrollToTopOnRefresh) {
      // we scrolled to the top
      updateRows(0, false);
    }
  }

  @Override
  public void scrollToTop() {
    liveScroller.setScrollTop(0);
  }

  /**
   * Sets the amount of rows that should be cached (default to 200). The cache
   * size is the number of rows that are retrieved each time a data request is
   * made. The cache size should always be greater than the number of visible
   * rows of the grid. The number of visible rows will vary depending on the
   * grid height and the height of each row. If the set cache size is smaller
   * than the number of visible rows of the grid than it gets set to the number
   * of visible rows of the grid.
   * 
   * @param cacheSize the new cache size
   */
  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  /**
   * Sets the amount of time before loading is done (defaults to 200).
   * 
   * @param loadDelay the new load delay in milliseconds
   */
  public void setLoadDelay(int loadDelay) {
    this.loadDelay = loadDelay;
  }

  /**
   * Sets the pre-fetch factor (defaults to .2). The pre-fetch factor is used to
   * determine when new data should be fetched as the user scrolls the grid. The
   * factor is used with the cache size.
   * 
   * <p />
   * For example, if the cache size is 1000 with a pre-fetch of .20, the grid
   * will request new data when the 800th (1000 * .20) row of the grid becomes
   * visible.
   * 
   * @param prefetchFactor the pre-fetch factor
   */
  public void setPrefetchFactor(double prefetchFactor) {
    this.prefetchFactor = prefetchFactor;
  }

  /**
   * Sets the height of one row (defaults to 20). <code>LiveGridView</code> will
   * only work with fixed row heights with all rows being the same height.
   * Changing this value will not physically resize the row heights, rather, the
   * specified height will be used internally for calculations.
   * 
   * @param rowHeight the new row height.
   */
  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
  }

  @Override
  protected void afterRender() {
    mainBody.setInnerHtml(renderRows(0, -1));
    renderWidgets(0, -1);
    processRows(0, true);
    applyEmptyText();
    refresh();
  }

  @Override
  protected void calculateVBar(boolean force) {
    if (force) {
      layout();
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected GridEvent<?> createComponentEvent(Event event) {
    LiveGridEvent l = new LiveGridEvent(grid, event);
    l.setLiveStoreOffset(liveStoreOffset);
    l.setViewIndex(viewIndex);
    l.setTotalCount(totalCount);
    return l;
  }

  protected void doLoad() {
    loader.load(loaderOffset, getCacheSize());
  }

  protected int getCalculatedRowHeight() {
    return rowHeight + borderWidth;
  }

  protected int getLiveScrollerHeight() {
    return liveScroller.getHeight(true);
  }

  protected int getLiveStoreCalculatedIndex(int index) {
    int calcIndex = index - (getCacheSize() / 2) + getVisibleRowCount();
    calcIndex = Math.min(totalCount - getCacheSize(), calcIndex);
    calcIndex = Math.min(index, calcIndex);
    return Math.max(0, calcIndex);
  }

  @Override
  protected int getScrollAdjust() {
    return scrollOffset;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected void initData(ListStore ds, ColumnModel cm) {
    if (liveStoreListener == null) {
      liveStoreListener = new StoreListener<ModelData>() {

        public void storeDataChanged(StoreEvent<ModelData> se) {
          liveStoreOffset = loader.getOffset();

          if (totalCount != loader.getTotalCount()) {
            totalCount = loader.getTotalCount();
            int height = totalCount * getCalculatedRowHeight();
            // 1000000 as browser maxheight hack
            int count = height / 1000000;
            int h = 0;

            StringBuilder sb = new StringBuilder();

            if (count > 0) {
              h = height / count;

              for (int i = 0; i < count; i++) {
                sb.append("<div style=\"height:");
                sb.append(h);
                sb.append("px;\">&nbsp;</div>");
              }
            }
            int diff = height - count * h;
            if (diff != 0) {
              sb.append("<div style=\"height:");
              sb.append(diff);
              sb.append("px;\"></div>");
            }
            liveScroller.setInnerHtml(sb.toString());

          }
          if (totalCount > 0 && viewIndexReload != -1 && !isCached(viewIndexReload)) {
            loadLiveStore(getLiveStoreCalculatedIndex(viewIndexReload));
          } else {
            viewIndexReload = -1;
            updateRows(viewIndex, true);
            isLoading = false;
            if (isMasked) {
              isMasked = false;
              scroller.unmask();
            }
          }

        }

        public void storeUpdate(StoreEvent<ModelData> se) {
          LiveGridView.this.ds.update(se.getModel());
        }
      };
    }
    if (liveStore != null) {
      liveStore.removeStoreListener(liveStoreListener);
    }
    liveStore = ds;
    super.initData(new ListStore() {
      @Override
      public boolean equals(ModelData model1, ModelData model2) {
        return LiveGridView.this.liveStore.equals(model1, model2);
      }

      @Override
      public ModelKeyProvider getKeyProvider() {
        return LiveGridView.this.liveStore.getKeyProvider();
      }

      @Override
      public Record getRecord(ModelData model) {
        return LiveGridView.this.liveStore.getRecord(model);
      }

      @Override
      public boolean hasRecord(ModelData model) {
        return LiveGridView.this.liveStore.hasRecord(model);
      }

      @Override
      public void sort(String field, SortDir sortDir) {
        LiveGridView.this.liveStore.sort(field, sortDir);
        sortInfo = liveStore.getSortState();
      }
    }, cm);

    loader = (PagingLoader) liveStore.getLoader();
    liveStore.addStoreListener(liveStoreListener);
    grid.getSelectionModel().bind(this.ds);
  }

  protected boolean isCached(int index) {
    if ((liveStore.getCount() == 0) || (index < liveStoreOffset)
        || (index > (liveStoreOffset + getCacheSize() - getVisibleRowCount()))) {
      return false;
    }
    return true;
  }

  protected boolean isHorizontalScrollBarShowing() {
    return cm.getTotalWidth() > scroller.getStyleWidth();
  }

  protected boolean loadLiveStore(int offset) {
    if (loaderTask == null) {
      loaderTask = new DelayedTask(new Listener<BaseEvent>() {
        public void handleEvent(BaseEvent be) {
          doLoad();
        }
      });
    }
    loaderOffset = offset;
    loaderTask.delay(loadDelay);
    if (isLoading) {
      return true;
    } else {
      isLoading = true;
      return false;
    }
  }

  @Override
  protected void notifyShow() {
    super.notifyShow();
    updateRows(viewIndex, true);
  }

  @Override
  protected void onColumnWidthChange(int column, int width) {
    super.onColumnWidthChange(column, width);
    updateRows(viewIndex, false);
  }

  @Override
  protected void onRemove(ListStore<ModelData> ds, ModelData m, int index, boolean isUpdate) {
    super.onRemove(ds, m, index, isUpdate);
    if (!isUpdate && liveStore.hasRecord(m)) {
      liveStore.getRecord(m).reject(false);
    }
  }

  @Override
  protected void renderUI() {
    super.renderUI();
    scroller.setStyleAttribute("overflowY", "hidden");
    liveScroller = grid.el().insertFirst("<div class=\"x-livegrid-scroller\"></div>");

    positionLiveScroller();

    liveScroller.addEventsSunk(Event.ONSCROLL);
    mainBody.addEventsSunk(Event.ONMOUSEWHEEL);
  }

  @Override
  protected void resize() {
    int oldCount = getVisibleRowCount();
    super.resize();
    if (mainBody != null) {
      int h = grid.getHeight(true) - mainHd.getHeight(true);
      if (isHorizontalScrollBarShowing()) {
        h -= XDOM.getScrollBarWidth();
      }
      if (footer != null) {
        h -= footer.getHeight();
      }
      liveScroller.setHeight(h, true);
      scroller.setWidth(grid.getWidth() - getScrollAdjust(), true);

      if (oldCount != getVisibleRowCount()) {
        updateRows(viewIndex, true);
      }
    }
  }

  protected boolean shouldCache(int index) {
    int cz = getCacheSize();
    int i = (int) (cz * prefetchFactor);
    double low = liveStoreOffset + i;
    double high = liveStoreOffset + cz - getVisibleRowCount() - i;
    if ((index < low && liveStoreOffset > 0) || (index > high && liveStoreOffset != totalCount - cz)) {
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  protected void updateRows(int newIndex, boolean reload) {
    int rowCount = getVisibleRowCount();

    newIndex = Math.min(newIndex, Math.max(0, totalCount - rowCount));

    int diff = newIndex - viewIndex;
    int delta = Math.abs(diff);

    // nothing has changed and we are not forcing a reload
    if (delta == 0 && !reload) {
      return;
    }

    viewIndex = newIndex;
    int liveStoreIndex = Math.max(0, viewIndex - liveStoreOffset);

    // load data if not already cached
    if (!isCached(viewIndex)) {
      if (!isMasked && grid.isLoadMask()) {
        scroller.mask(GXT.MESSAGES.loadMask_msg());
        isMasked = true;
      }
      if (loadLiveStore(getLiveStoreCalculatedIndex(viewIndex))) {
        viewIndexReload = viewIndex;
      }
      return;
    }

    // do pre caching
    if (shouldCache(viewIndex) && !isLoading) {
      loadLiveStore(getLiveStoreCalculatedIndex(viewIndex));
    }

    int rc = getVisibleRowCount();
    if (delta > rc - 1) {
      reload = true;
    }

    if (reload) {
      delta = diff = rc;
      boolean p = preventScrollToTopOnRefresh;
      preventScrollToTopOnRefresh = true;
      ds.removeAll();
      preventScrollToTopOnRefresh = p;
    }

    if (delta == 0) {
      return;
    }

    int count = ds.getCount();
    if (diff > 0) {
      // rolling forward
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(0);
      }
      count = ds.getCount();
      ds.add(liveStore.getRange(liveStoreIndex + count, liveStoreIndex + count + delta - 1));
    } else {
      // rolling back
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(count - c - 1);
      }

      ds.insert(liveStore.getRange(liveStoreIndex, liveStoreIndex + delta - 1), 0);
    }

    LiveGridEvent<ModelData> event = (LiveGridEvent<ModelData>) createComponentEvent(null);
    fireEvent(Events.LiveGridViewUpdate, event);
  }

  protected void positionLiveScroller() {
    liveScroller.setTop(mainHd.getHeight());
  }
}

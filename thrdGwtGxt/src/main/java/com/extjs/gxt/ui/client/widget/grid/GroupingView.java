/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.js.JsArray;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * <code>GridView</code> that groups data based on a <code>GroupingStore</code>.
 */
public class GroupingView extends GridView {

  public class GroupingViewImages extends GridViewImages {
    private AbstractImagePrototype groupBy = GXT.IMAGES.grid_groupBy();

    public AbstractImagePrototype getGroupBy() {
      return groupBy;
    }

    public void setGroupBy(AbstractImagePrototype groupBy) {
      this.groupBy = groupBy;
    }
  }

  protected boolean enableGrouping;
  protected boolean isUpdating;

  protected Map<String, String> map = new FastMap<String>();
  private int counter = 0;
  private boolean enableGroupingMenu = true;
  private boolean enableNoGroups = true;
  private GroupingStore<ModelData> groupingStore;
  private GridGroupRenderer groupRenderer;
  private String lastGroupField;
  private boolean showGroupedColumn = true;
  private boolean showGroupName;
  private boolean startCollapsed;
  private Map<String, Boolean> state = new FastMap<Boolean>();

  /**
   * Collapses all groups.
   */
  public void collapseAllGroups() {
    toggleAllGroups(false);
  }

  /**
   * Expands all groups.
   */
  public void expandAllGroups() {
    toggleAllGroups(true);
  }

  /**
   * Returns the group renderer.
   * 
   * @return the group renderer
   */
  public GridGroupRenderer getGroupRenderer() {
    return groupRenderer;
  }

  /**
   * Returns the group elements.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public NodeList<Element> getGroups() {
    if (!enableGrouping) {
      return new JsArray().getJsObject().cast();
    }
    return (NodeList) mainBody.dom.getChildNodes();
  }

  @Override
  public GroupingViewImages getImages() {
    if (images == null) {
      images = new GroupingViewImages();
    }
    return (GroupingViewImages) images;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void initData(ListStore ds, ColumnModel cm) {
    super.initData(ds, cm);
    groupingStore = (GroupingStore) ds;
  }

  /**
   * Returns true if the grouping menu is enabled.
   * 
   * @return the enable grouping state
   */
  public boolean isEnableGroupingMenu() {
    return enableGroupingMenu;
  }

  /**
   * Returns true if the user can turn off grouping.
   * 
   * @return the enable no groups state
   */
  public boolean isEnableNoGroups() {
    return enableNoGroups;
  }

  /**
   * Returns true if the group is expanded.
   * 
   * @param group the group
   * @return true if expanded
   */
  public boolean isExpanded(Element group) {
    return group.getClassName().indexOf("x-grid-group-collapsed") == -1;
  }

  /**
   * Returns true if the grouped column is visible.
   * 
   * @return the show grouped column
   */
  public boolean isShowGroupedColumn() {
    return showGroupedColumn;
  }

  /**
   * Returns true if start collapsed is enabled.
   * 
   * @return the start collapsed state
   */
  public boolean isStartCollapsed() {
    return startCollapsed;
  }

  /**
   * True to enable the grouping entry in the header context menu (defaults to
   * true).
   * 
   * @param enableGroupingMenu true to enable
   */
  public void setEnableGroupingMenu(boolean enableGroupingMenu) {
    this.enableGroupingMenu = enableGroupingMenu;
  }

  /**
   * True to allow the user to turn off grouping by adding a check item to the
   * header context menu (defaults to true).
   * 
   * @param enableNoGroups true to enable turning off grouping
   */
  public void setEnableNoGroups(boolean enableNoGroups) {
    this.enableNoGroups = enableNoGroups;
  }

  /**
   * Sets the group renderer.
   * 
   * @param groupRenderer the group renderer
   */
  public void setGroupRenderer(GridGroupRenderer groupRenderer) {
    this.groupRenderer = groupRenderer;
  }

  /**
   * Sets whether the grouped column is visible (defaults to true).
   * 
   * @param showGroupedColumn true to show the grouped column
   */
  public void setShowGroupedColumn(boolean showGroupedColumn) {
    this.showGroupedColumn = showGroupedColumn;
  }

  /**
   * Sets whether the groups should start collapsed (defaults to false).
   * 
   * @param startCollapsed true to start collapsed
   */
  public void setStartCollapsed(boolean startCollapsed) {
    this.startCollapsed = startCollapsed;
  }

  /**
   * Toggles all groups.
   * 
   * @param expanded true to expand
   */
  public void toggleAllGroups(boolean expanded) {
    NodeList<Element> groups = getGroups();
    for (int i = 0, len = groups.getLength(); i < len; i++) {
      toggleGroup(groups.getItem(i), expanded);
    }
  }

  @Override
  protected Menu createContextMenu(final int colIndex) {
    Menu menu = super.createContextMenu(colIndex);

    if (menu != null && enableGroupingMenu && cm.isGroupable(colIndex)) {
      MenuItem groupBy = new MenuItem(GXT.MESSAGES.groupingView_groupByText());
      groupBy.setIcon(getImages().getGroupBy());
      groupBy.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
          onGroupByClick(ce, colIndex);
        }

      });
      menu.add(new SeparatorMenuItem());
      menu.add(groupBy);
    }

    if (menu != null && enableGroupingMenu && enableGrouping && enableNoGroups) {
      final CheckMenuItem showInGroups = new CheckMenuItem(GXT.MESSAGES.groupingView_showGroupsText());
      showInGroups.setChecked(true);
      showInGroups.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
          onShowGroupsClick(ce, showInGroups.isChecked());
        }
      });
      menu.add(showInGroups);
    }
    return menu;
  }

  protected void doGroupEnd(StringBuilder buf, GroupColumnData g, List<ColumnData> cs, int colCount) {
    buf.append(templates.endGroup());
  }

  protected void doGroupStart(StringBuilder buf, GroupColumnData g, List<ColumnData> cs, int colCount) {
    buf.append(templates.startGroup(g.groupId, g.css, g.style, g.group.toString()));
  }

  @Override
  protected String doRender(List<ColumnData> cs, List<ModelData> rows, int startRow, int colCount, boolean stripe) {
    if (rows.size() < 1) {
      return "";
    }

    String groupField = getGroupField();
    int colIndex = cm.findColumnIndex(groupField);

    enableGrouping = groupField != null;

    if (!enableGrouping || isUpdating) {
      return super.doRender(cs, rows, startRow, colCount, stripe);
    }

    String gstyle = "width:" + getTotalWidth() + "px;";
    String gidPrefix = grid.getId();

    ColumnConfig cfg = cm.getColumn(colIndex);

    String prefix = showGroupName ? cfg.getHeader() + ": " : "";

    GroupColumnData curGroup = null;
    String gid = null;

    List<GroupColumnData> groups = new ArrayList<GroupColumnData>();

    for (int j = 0; j < rows.size(); j++) {
      ModelData model = (ModelData) rows.get(j);

      int rowIndex = (j + startRow);

      // the value for the group field
      Object gvalue = model.get(groupField);

      // the rendered group value
      String g = getGroup(gvalue, model, rowIndex, colIndex, ds);

      if (curGroup == null || !curGroup.group.equals(g)) {
        gid = getGroupId(gidPrefix, groupField, g);

        boolean isCollapsed = state.get(gid) != null ? !state.get(gid) : startCollapsed;
        String gcls = isCollapsed ? "x-grid-group-collapsed" : "";

        curGroup = new GroupColumnData();
        curGroup.group = g;
        curGroup.field = groupField;
        curGroup.gvalue = gvalue;
        curGroup.text = prefix + g;
        curGroup.groupId = gid;
        curGroup.startRow = rowIndex;
        curGroup.style = gstyle;
        curGroup.css = gcls;
        curGroup.models.add(model);
        groups.add(curGroup);

      } else {
        curGroup.models.add(model);
      }
      // model.set("_groupId", gid);

    }

    for (GroupColumnData group : groups) {
      if (groupRenderer != null) {
        String g = groupRenderer.render(group);
        if (g == null || g.equals("")) {
          g = "&nbsp;";
        }
        group.group = g;
      }
    }

    StringBuilder buf = new StringBuilder();

    for (int i = 0, len = groups.size(); i < len; i++) {
      GroupColumnData g = groups.get(i);
      doGroupStart(buf, g, cs, colCount);
      buf.append(super.doRender(cs, g.models, g.startRow, colCount, stripe));
      doGroupEnd(buf, g, cs, colCount);
    }

    return buf.toString();
  }

  protected Element findGroup(Element el) {
    return fly(el).findParentElement(".x-grid-group", 10);
  }

  protected String getGroup(Object value, ModelData m, int rowIndex, int colIndex, ListStore<ModelData> ds) {
    return value == null ? "" : value.toString();
  }

  protected String getGroupField() {
    return groupingStore.getGroupState();
  }

  protected String getGroupId(String gidPrefix, String groupField, String group) {
    String s = gidPrefix + "-gp-" + groupField + "-" + group;
    String r = map.get(s);
    if (r == null) {
      r = gidPrefix + "-gp-groupid-" + String.valueOf(counter++);
      map.put(s, r);
    }

    return r;
  }

  protected Element getGroupRow(Element group, int rowIndex) {
    return getGroupRows(group).getItem(rowIndex);
  }

  protected int getGroupRowCount(Element group) {
    return group.getChildNodes().getItem(1).getChildNodes().getLength();
  }

  protected NodeList<Element> getGroupRows(Element group) {
    return group.getChildNodes().getItem(1).getChildNodes().cast();
  }

  @Override
  protected NodeList<Element> getRows() {
    if (!enableGrouping) {
      return super.getRows();
    }
    if (!hasRows()) {
      return new JsArray().getJsObject().cast();
    }

    NodeList<Element> gs = getGroups();
    JsArray rows = new JsArray();
    for (int i = 0, len = gs.getLength(); i < len; i++) {
      NodeList<Element> g = gs.getItem(i).getChildNodes().getItem(1).getChildNodes().cast();
      for (int j = 0, len2 = g.getLength(); j < len2; j++) {
        rows.add(g.getItem(j));
      }
    }
    return rows.getJsObject().cast();
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void init(Grid grid) {
    super.init(grid);
    grid.getAriaSupport().setRole("treegrid");
  }

  @Override
  protected void initTemplates() {
    super.initTemplates();

    GridSelectionModel<ModelData> sm = grid.getSelectionModel();
    sm.addListener(Events.BeforeSelect, new Listener<SelectionEvent<ModelData>>() {
      public void handleEvent(SelectionEvent<ModelData> be) {
        onBeforeRowSelect(be);
      }
    });
  }

  @Override
  protected void onAdd(ListStore<ModelData> store, List<ModelData> models, int index) {
    if (enableGrouping) {
      Point ss = getScrollState();
      refresh(false);
      restoreScroll(ss);
    } else {
      super.onAdd(store, models, index);
    }
  }

  protected void onGroupSelect(Element group, boolean select) {
    El.fly(group).firstChild().setStyleName("x-grid3-group-selected", select);
    grid.getAriaSupport().setState("aria-activedescendant", group.getFirstChildElement().getId());
  }

  @Override
  protected void onMouseDown(GridEvent<ModelData> ge) {
    super.onMouseDown(ge);
    El hd = ge.getTarget(".x-grid-group-hd", 10);
    if (hd != null) {
      ge.stopEvent();
      toggleGroup(hd.dom.getParentElement(), isGroupExpanded(hd.dom.getParentElement()));
    }
  }

  @Override
  protected void onRemove(ListStore<ModelData> ds, ModelData m, int index, boolean isUpdate) {
    super.onRemove(ds, m, index, isUpdate);
    String groupField = getGroupField();
    if (enableGrouping) {
      String id = getGroupId(grid.getId(), groupField, getGroup(m.get(groupField), m, index,
          cm.findColumnIndex(groupField), ds));
      Element g = XDOM.getElementById(id);
      if (g != null && !g.getChildNodes().getItem(1).hasChildNodes()) {
        fly(g).removeFromParent();
        removeGroupId(id);
      }
    }
    // apply empty text
  }

  protected void onShowGroupsClick(MenuEvent be, boolean checked) {
    if (checked) {
      onGroupByClick(be, activeHdIndex);
    } else {
      groupingStore.clearGrouping();
    }
  }

  @Override
  protected void refreshRow(int row) {
    isUpdating = true;
    super.refreshRow(row);
    isUpdating = false;
  }

  @Override
  protected String renderRows(int startRow, int endRow) {
    String groupField = getGroupField();
    boolean eg = groupField != null;
    if (!showGroupedColumn) {
      int colIndex = cm.findColumnIndex(groupField);
      if (!eg && lastGroupField != null) {
        mainBody.update("");
        cm.setHidden(cm.findColumnIndex(lastGroupField), false);
        lastGroupField = null;
      } else if (eg && (lastGroupField == null || lastGroupField == groupField)) {
        lastGroupField = groupField;
        cm.setHidden(colIndex, true);
      } else if (eg && lastGroupField != null && !groupField.equals(lastGroupField)) {
        mainBody.update("");
        int oldIndex = cm.findColumnIndex(lastGroupField);
        cm.setHidden(oldIndex, false);
        lastGroupField = groupField;
        cm.setHidden(colIndex, true);
      }
    }
    return super.renderRows(startRow, endRow);
  }

  @Override
  protected void templateOnAllColumnWidthsUpdated(List<Integer> ws, int tw) {
    super.templateOnAllColumnWidthsUpdated(ws, tw);
    updateGroupWidths();
  }

  @Override
  protected void templateOnColumnHiddenUpdated(int col, boolean hidden, int tw) {
    super.templateOnColumnHiddenUpdated(col, hidden, tw);
    updateGroupWidths();
  }

  @Override
  protected void templateOnColumnWidthUpdated(int col, int w, int tw) {
    super.templateOnColumnWidthUpdated(col, w, tw);
    updateGroupWidths();
  }

  protected void toggleGroup(Element g, boolean expanded) {
    if (grid.editSupport != null) {
      grid.editSupport.stopEditing();
    }
    state.put(fly(g).getId(), expanded);
    fly(g).setStyleName("x-grid-group-collapsed", !expanded);
    g.getFirstChildElement().setAttribute("aria-expanded", expanded ? "true" : "false");
    calculateVBar(false);
  }

  private boolean isGroupExpanded(Element g) {
    return fly(g).hasStyleName("x-grid-group-collapsed");
  }

  protected void onBeforeRowSelect(SelectionEvent<ModelData> se) {
    if (!enableGrouping) {
      return;
    }
    Element row = getRow(se.getIndex());
    if (row != null) {
      Element g = findGroup(row);
      toggleGroup(g, true);
    }
  }

  protected void onGroupByClick(MenuEvent me, int colIndex) {
    groupingStore.groupBy(cm.getDataIndex(colIndex));
  }

  protected void removeGroupId(String id) {
    for (Entry<String, String> e : map.entrySet()) {
      if (e.getValue().equals(id)) {
        map.remove(e.getKey());
        return;
      }
    }
  }

  protected void updateGroupWidths() {
    if (!enableGrouping || ds.getCount() < 1) {
      return;
    }
    String tw = Math.max(cm.getTotalWidth(), el.dom.getOffsetWidth() - getScrollAdjust()) + "px";
    NodeList<Element> gs = getGroups();
    for (int i = 0, len = gs.getLength(); i < len; i++) {
      Element e = gs.getItem(i).getFirstChild().cast();
      e.getStyle().setProperty("width", tw);
    }
  }

}

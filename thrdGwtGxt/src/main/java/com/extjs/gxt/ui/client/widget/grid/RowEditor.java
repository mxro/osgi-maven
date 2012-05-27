/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.grid;

import java.util.Map;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ColumnModelEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * This RowEditor should be used as a plugin to {@link Grid}. It displays an
 * editor for all cells in a row.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeEdit</b> : RowEditorEvent(rowEditor, rowIndex)<br>
 * <div>Fires before row editing is triggered. Listeners can cancel the action
 * by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>rowEditor : this</li>
 * <li>rowIndex : the row index of the row about to be edited</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>ValidateEdit</b> : RowEditorEvent(rowEditor, rowIndex, changes)<br>
 * <div>Fires right before the model is updated. Listeners can cancel the action
 * by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>rowEditor : this</li>
 * <li>rowIndex : the row index of the row about to be edited</li>
 * <li>changes : a map of property name and new values</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>AfterEdit</b> : RowEditorEvent(rowEditor, rowIndex, changes)<br>
 * <div>Fires after a row has been edited.</div>
 * <ul>
 * <li>rowEditor : this</li>
 * <li>rowIndex : the row index of the row that was edited</li>
 * <li>changes : a map of property name and new values</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * @param <M> the model type
 */
@SuppressWarnings("deprecation")
public class RowEditor<M extends ModelData> extends ContentPanel implements ComponentPlugin {
  public class RowEditorMessages {

    private String cancelText = GXT.MESSAGES.rowEditor_cancelText();
    private String dirtyText = GXT.MESSAGES.rowEditor_dirtyText();
    private String errorTipTitleText = GXT.MESSAGES.rowEditor_tipTitleText();
    private String saveText = GXT.MESSAGES.rowEditor_saveText();

    /**
     * Returns the buttons cancel text.
     * 
     * @return the text
     */
    public String getCancelText() {
      return cancelText;
    }

    /**
     * Returns the tool tip dirty text.
     * 
     * @return the dirtyText
     */
    public String getDirtyText() {
      return dirtyText;
    }

    /**
     * Returns the error tool tip title.
     * 
     * @return the errorTipTitleText
     */
    public String getErrorTipTitleText() {
      return errorTipTitleText;
    }

    /**
     * Returns the buttons save text.
     * 
     * @return the text
     */
    public String getSaveText() {
      return saveText;
    }

    /**
     * Sets the buttons cancel text
     * 
     * @param cancelText the cancel text
     */
    public void setCancelText(String cancelText) {
      this.cancelText = cancelText;
    }

    /**
     * Sets the tool tip dirty text.
     * 
     * @param dirtyText the dirtyText to set
     */
    public void setDirtyText(String dirtyText) {
      this.dirtyText = dirtyText;
    }

    /**
     * Sets the error tool tip title.
     * 
     * @param errorTipTitleText the errorTipTitleText to set
     */
    public void setErrorTipTitleText(String errorTipTitleText) {
      this.errorTipTitleText = errorTipTitleText;
    }

    /**
     * Sets the buttons save text
     * 
     * @param saveText the save text
     */
    public void setSaveText(String saveText) {
      this.saveText = saveText;
    }

  }

  protected ContentPanel btns;
  protected Grid<M> grid;
  protected RowEditorMessages messages;
  protected boolean renderButtons = true;
  protected int rowIndex;

  protected Button saveBtn, cancelBtn;
  private boolean bound;
  private int buttonPad = 3;
  private ClicksToEdit clicksToEdit = ClicksToEdit.ONE;
  private boolean editing;
  private boolean errorSummary = true;
  private int frameWidth = 5;
  private boolean initialized;
  private boolean lastValid;
  private Listener<GridEvent<M>> listener;
  private int monitorPoll = 200;
  private Timer monitorTimer;
  private boolean monitorValid = true;
  private Record record;
  private ToolTip tooltip;

  public RowEditor() {
    super();
    setFooter(true);
    setLayout(new HBoxLayout());
    addStyleName("x-small-editor");
    baseStyle = "x-row-editor";
    messages = new RowEditorMessages();
  }

  /**
   * Returns the clicks to edit.
   * 
   * @return the clicks to edit
   */
  public ClicksToEdit getClicksToEdit() {
    return clicksToEdit;
  }

  /**
   * Returns the roweditors's messages.
   * 
   * @return the messages
   */
  public RowEditorMessages getMessages() {
    return messages;
  }

  /**
   * Returns the interval in ms in that the roweditor is validated
   * 
   * @return the interval in ms in that the roweditor is validated
   */
  public int getMonitorPoll() {
    return monitorPoll;
  }

  @SuppressWarnings("unchecked")
  public void init(Component component) {
    grid = (Grid<M>) component;
    grid.disableTextSelection(false);

    listener = new Listener<GridEvent<M>>() {

      public void handleEvent(GridEvent<M> be) {
        if (be.getType() == Events.RowDoubleClick) {
          onRowDblClick(be);
        } else if (be.getType() == Events.RowClick) {
          onRowClick(be);
        } else if (be.getType() == Events.OnKeyDown) {
          onGridKey(be);
        } else if (be.getType() == Events.ColumnResize || be.getType() == Events.Resize) {
          verifyLayout(false);
        } else if (be.getType() == Events.BodyScroll) {
          positionButtons();
        } else if (be.getType() == Events.Detach) {
          stopEditing(false);
        } else if (be.getType() == Events.Reconfigure && initialized) {
          stopEditing(false);
          removeAll();
          initialized = false;
        }

      }

    };

    grid.addListener(Events.RowDoubleClick, listener);
    grid.addListener(Events.Resize, listener);
    grid.addListener(Events.RowClick, listener);
    grid.addListener(Events.OnKeyDown, listener);
    grid.addListener(Events.ColumnResize, listener);
    grid.addListener(Events.BodyScroll, listener);
    grid.addListener(Events.Detach, listener);
    grid.addListener(Events.Reconfigure, listener);
    grid.getColumnModel().addListener(Events.HiddenChange, new Listener<ColumnModelEvent>() {
      public void handleEvent(ColumnModelEvent be) {
        verifyLayout(false);
      }
    });
    grid.getColumnModel().addListener(Events.ColumnMove, new Listener<ColumnModelEvent>() {
      public void handleEvent(ColumnModelEvent be) {
        if (initialized) {
          stopEditing(false);
          removeAll();
          initialized = false;
        }
      }
    });
    grid.getView().addListener(Events.Refresh, new Listener<BaseEvent>() {
      public void handleEvent(BaseEvent be) {
        stopEditing(false);
      }
    });
  }

  /**
   * Returns true of the RowEditor is active and editing.
   * 
   * @return true if the RowEditor is active
   */
  public boolean isEditing() {
    return editing;
  }

  /**
   * Returns true if a tooltip with an error summary is shown.
   * 
   * @return true if a tooltip with an error summary is shown
   */
  public boolean isErrorSummary() {
    return errorSummary;
  }

  /**
   * Returns true if this roweditor is monitored.
   * 
   * @return true if the roweditor is monitored
   */
  public boolean isMonitorValid() {
    return monitorValid;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    if (ce.getEventTypeInt() == KeyNav.getKeyEvent().getEventCode()) {
      if (ce.getKeyCode() == KeyCodes.KEY_ENTER) {
        onEnter(ce);
      } else if (ce.getKeyCode() == KeyCodes.KEY_ESCAPE) {
        onEscape(ce);
      } else if (ce.getKeyCode() == KeyCodes.KEY_TAB) {
        onTab(ce);
      }
    }
  }

  /**
   * Sets the number of clicks to edit (defaults to ONE).
   * 
   * @param clicksToEdit the clicks to edit
   */
  public void setClicksToEdit(ClicksToEdit clicksToEdit) {
    this.clicksToEdit = clicksToEdit;
  }

  /**
   * True to show a tooltip with an error summary (defaults to true)
   * 
   * @param errorSummary true to show an error summary.
   */
  public void setErrorSummary(boolean errorSummary) {
    this.errorSummary = errorSummary;
  }

  /**
   * Sets the roweditors's messages.
   * 
   * @param messages the messages
   */
  public void setMessages(RowEditorMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets the polling interval in ms in that the roweditor validation is done
   * (defaults to 200)
   * 
   * @param monitorPoll the polling interval in ms in that validation is done
   */
  public void setMonitorPoll(int monitorPoll) {
    this.monitorPoll = monitorPoll;
  }

  /**
   * True to monitor the valid status of this roweditor (defaults to true)
   * 
   * @param monitorValid true to monitor this roweditor
   */
  public void setMonitorValid(boolean monitorValid) {
    this.monitorValid = monitorValid;
  }

  /**
   * Start editing of a specific row.
   * 
   * @param rowIndex the index of the row to edit.
   * @param doFocus true to focus the field
   */
  @SuppressWarnings("unchecked")
  public void startEditing(int rowIndex, boolean doFocus) {
    if (disabled) {
      return;
    }
    if (editing && isDirty()) {
      showTooltip(getMessages().getDirtyText());
      return;
    }
    hideTooltip();
    M model = (M) grid.getStore().getAt(rowIndex);
    Record r = getRecord(model);
    RowEditorEvent ree = new RowEditorEvent(this, rowIndex);
    ree.setRecord(r);

    Element row = (Element) grid.getView().getRow(rowIndex);

    if (row == null || model == null || !fireEvent(Events.BeforeEdit, ree)) {
      return;
    }

    editing = true;
    record = r;

    this.rowIndex = rowIndex;

    if (!isRendered()) {
      render((Element) grid.getView().getEditorParent());
    }
    ComponentHelper.doAttach(this);

    if (!initialized) {
      initFields();
    }
    ColumnModel cm = grid.getColumnModel();

    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      Field<Object> f = (Field<Object>) getItem(i);
      if (GXT.isAriaEnabled()) {
        if (i == 0 && saveBtn != null) {
          saveBtn.getFocusSupport().setNextId(f.getId());
        }
        f.getAriaSupport().setLabel(cm.getColumnHeader(i));
      }
      String dIndex = cm.getDataIndex(i);
      CellEditor ed = cm.getEditor(i);
      Object val = ed != null ? ed.preProcessValue(record.get(dIndex)) : record.get(dIndex);

      f.updateOriginalValue(val);
      f.setValue(val);
    }
    if (cancelBtn != null) {
      cancelBtn.getFocusSupport().setPreviousId(getItem(getItemCount() - 1).getId());
    }
    if (!isVisible()) {
      show();
    }

    el().setXY(getPosition(row));
    verifyLayout(true);
    if (doFocus) {
      deferFocus(null);
    }
    lastValid = false;

    el().scrollIntoView((Element) grid.getView().getEditorParent(), false,
        new int[] {renderButtons ? btns.getHeight() : 0, 0});
  }

  /**
   * Stops editing.
   * 
   * @param saveChanges true to save the changes. false to ignore them.
   */
  public void stopEditing(boolean saveChanges) {
    if (disabled || !editing) {
      return;
    }
    editing = false;

    Map<String, Object> data = new FastMap<Object>();
    boolean hasChange = false;
    ColumnModel cm = grid.getColumnModel();
    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      if (!cm.isHidden(i)) {
        Component c = getItem(i);
        if (c instanceof LabelField) {
          continue;
        } else if (c instanceof Field<?>) {
          Field<?> f = (Field<?>) c;

          String dindex = cm.getDataIndex(i);
          Object oldValue = record.get(dindex);

          CellEditor ed = cm.getEditor(i);
          Object value = ed != null ? ed.postProcessValue(f.getValue()) : f.getValue();
          if ((oldValue == null && value != null) || (oldValue != null && !oldValue.equals(value))) {
            data.put(dindex, value);
            hasChange = true;
          }
        }
      }
    }
    RowEditorEvent ree = new RowEditorEvent(this, rowIndex);
    ree.setRecord(record);
    ree.setChanges(data);

    if (!saveChanges || !isValid()) {
      fireEvent(Events.CancelEdit, ree);
    } else if (hasChange && fireEvent(Events.ValidateEdit, ree)) {
      record.beginEdit();
      for (String k : data.keySet()) {
        record.set(k, data.get(k));
      }
      record.endEdit();
      fireEvent(Events.AfterEdit, ree);
    }
    hide();
  }

  protected void afterRender() {
    super.afterRender();
    positionButtons();

    if (monitorValid) {
      startMonitoring();
    }
    if (renderButtons) {
      btns.setWidth((getMinButtonWidth() * 2) + (frameWidth * 2) + (buttonPad * 4));
    }
  }

  protected void bindHandler() {
    boolean valid = isValid();
    if (!valid) {
      lastValid = false;
      if (errorSummary) {
        showTooltip(getErrorText());
      }
    } else if (valid && !lastValid) {
      hideTooltip();
      lastValid = true;
    }

    if (saveBtn != null) {
      saveBtn.setEnabled(valid);
    }

    if (!isVisible() && tooltip != null && tooltip.isEnabled()) {
      hideTooltip();
    }
  }

  protected void createButtons() {
    btns = new ContentPanel() {
      protected void createStyles(String baseStyle) {
        baseStyle = "x-plain";
        headerStyle = baseStyle + "-header";
        headerTextStyle = baseStyle + "-header-text";
        bwrapStyle = baseStyle + "-bwrap";
        tbarStyle = baseStyle + "-tbar";
        bodStyle = baseStyle + "-body";
        bbarStyle = baseStyle + "-bbar";
        footerStyle = baseStyle + "-footer";
        collapseStyle = baseStyle + "-collapsed";
      }
    };

    btns.setHeaderVisible(false);
    btns.addStyleName("x-btns");
    btns.setLayout(new TableLayout(2));

    cancelBtn = new Button(getMessages().getCancelText(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        stopEditing(false);
      }
    });
    cancelBtn.setMinWidth(getMinButtonWidth());
    btns.add(cancelBtn);

    saveBtn = new Button(getMessages().getSaveText(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        stopEditing(true);
      }
    });
    saveBtn.setMinWidth(getMinButtonWidth());
    btns.add(saveBtn);
    
    cancelBtn.getFocusSupport().setNextId(saveBtn.getId());
    saveBtn.getFocusSupport().setPreviousId(cancelBtn.getId());

    btns.render(getElement("bwrap"));
    btns.layout();

    btns.getElement().removeAttribute("tabindex");
    btns.getFocusSupport().setIgnore(true);
  }

  protected void deferFocus(final int colIndex) {
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        doFocus(colIndex);
      }
    });
  }
  protected void deferFocus(final Point pt) {
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        doFocus(pt);
      }
    });
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(btns);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(btns);
  }

  protected void doFocus(Point pt) {
    if (isVisible()) {
      int index = 0;
      if (pt != null) {
        index = getTargetColumnIndex(pt);
      }
      doFocus(index);
    }
  }

  protected void doFocus(int colIndex) {
    if (isVisible()) {
      ColumnModel cm = this.grid.getColumnModel();
      for (int i = colIndex, len = cm.getColumnCount(); i < len; i++) {
        ColumnConfig c = cm.getColumn(i);
        if (!c.isHidden() && c.getEditor() != null) {
          c.getEditor().getField().focus();
          break;
        }
      }
    }
  }

  protected void ensureVisible(CellEditor editor) {
    if (isVisible()) {
      grid.getView().ensureVisible(this.rowIndex, indexOf(editor), true);
    }
  }

  protected Component findField(Element elem) {
    El e = El.fly(elem).findParent(".x-row-editor-field", 3);
    if (e != null) {
      return ComponentManager.get().get(e.getId());
    }
    return null;
  }

  protected String getErrorText() {
    StringBuffer sb = new StringBuffer();
    sb.append("<ul>");
    for (int i = 0; i < getItemCount(); i++) {

      Field<?> f = (Field<?>) getItem(i);
      if (!f.isValid(true)) {
        sb.append("<li><b>");
        sb.append(grid.getColumnModel().getColumn(i).getHeader());
        sb.append("</b>: ");
        sb.append(f.getErrorMessage());
        sb.append("</li>");
      }
    }
    sb.append("</ul>");
    return sb.toString();
  }

  protected Point getPosition(Element row) {
    return El.fly(row).getXY();
  }

  protected Record getRecord(M model) {
    return grid.getStore().getRecord(model);
  }

  protected int getTargetColumnIndex(Point pt) {
    int x = pt.x;
    int match = -1;
    for (int i = 0; i < grid.getColumnModel().getColumnCount(); i++) {
      ColumnConfig c = grid.getColumnModel().getColumn(i);
      if (!c.isHidden()) {
        if (El.fly(grid.getView().getHeaderCell(i)).getRegion().right >= x) {
          match = i;
          break;
        }
      }
    }
    return match;
  }

  protected void hideTooltip() {
    if (tooltip != null) {
      tooltip.hide();
      tooltip.disable();
    }
  }

  protected void initFields() {
    ColumnModel cm = grid.getColumnModel();
    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      ColumnConfig c = cm.getColumn(i);
      CellEditor ed = c.getEditor();

      Field<?> f = ed != null ? ed.getField() : new LabelField();
      if (f instanceof TriggerField<?>) {
        ((TriggerField<? extends Object>) f).setMonitorTab(true);
      }
      f.setWidth(cm.getColumnWidth(i));
      HBoxLayoutData ld = new HBoxLayoutData();
      if (i == 0) {
        ld.setMargins(new Margins(0, 1, 2, 1));
      } else if (i == len - 1) {
        ld.setMargins(new Margins(0, 0, 2, 1));
      } else {
        ld.setMargins(new Margins(0, 1, 2, 2));
      }

      f.setMessageTarget("tooltip");
      f.addStyleName("x-row-editor-field");
      // needed because we remove it from the celleditor
      clearParent(f);
      insert(f, i, ld);
    }
    initialized = true;
  }

  @SuppressWarnings("unchecked")
  protected boolean isDirty() {
    for (Component f : getItems()) {
      if (((Field<Object>) f).isDirty()) {
        return true;
      }
    }
    return false;
  }

  protected boolean isValid() {
    boolean valid = true;
    for (Component c : getItems()) {
      Field<?> f = (Field<?>) c;
      if (!f.isValid(true)) {
        return false;
      }
    }
    return valid;
  }

  protected void onEnter(ComponentEvent ce) {
    stopEditing(true);
  }

  protected void onEscape(ComponentEvent ce) {
    stopEditing(false);
  }

  protected void onGridKey(GridEvent<M> e) {
    int kc = e.getKeyCode();
    if ((kc == KeyCodes.KEY_ENTER || (kc == 113 && GXT.isWindows)) && !isVisible()) {
      M r = grid.getSelectionModel().getSelectedItem();
      if (r != null) {
        int index = this.grid.store.indexOf(r);
        startEditing(index, true);
        e.cancelBubble();
      }
    }
  }

  protected void onHide() {
    super.onHide();
    stopMonitoring();
    grid.getView().focusRow(rowIndex);
    record = null;
    ComponentHelper.doDetach(this);
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);

    el().makePositionable(true);
    sinkEvents(KeyNav.getKeyEvent().getEventCode());

    swallowEvent(Events.OnKeyDown, el().dom, false);
    swallowEvent(Events.OnKeyUp, el().dom, false);
    swallowEvent(Events.OnKeyPress, el().dom, false);

    if (renderButtons) {
      createButtons();
      ComponentHelper.setParent(this, btns);
    }

  }

  protected void onRowClick(GridEvent<M> e) {
    if (clicksToEdit != ClicksToEdit.TWO) {
      startEditing(e.getRowIndex(), false);
      deferFocus(e.getColIndex());
    }
  }

  protected void onRowDblClick(GridEvent<M> e) {
    if (clicksToEdit == ClicksToEdit.TWO) {
      startEditing(e.getRowIndex(), false);
      deferFocus(e.getColIndex());
    }
  }

  protected void onShow() {
    super.onShow();
    if (monitorValid) {
      startMonitoring();
    }
  }

  protected void onTab(ComponentEvent ce) {
    Element target = ce.getTarget();
    Component c = findField(target);
    if (saveBtn != null && c != null && ce.isShiftKey() && indexOf(c) == 0) {
      ce.stopEvent();
      saveBtn.focus();
      return;
    }
  }

  protected void positionButtons() {
    if (btns != null) {
      int h = el().getClientHeight();
      GridView view = grid.getView();
      int scroll = view.getScrollState().x;
      int mainBodyWidth = view.scroller.getWidth(true);
      int columnWidth = view.getTotalWidth();
      int width = columnWidth < mainBodyWidth ? columnWidth : mainBodyWidth;
      int bw = btns.getWidth(true);
      this.btns.setPosition((width / 2) - (bw / 2) + scroll, h - 2);
    }
  }

  protected void showTooltip(String msg) {
    if (tooltip == null) {
      ToolTipConfig config = new ToolTipConfig();
      config.setAutoHide(false);
      config.setMouseOffset(new int[] {0, 0});
      config.setTitle(getMessages().getErrorTipTitleText());
      config.setAnchor("left");
      tooltip = new ToolTip(this, config);
      tooltip.setMaxWidth(600);
    }
    ToolTipConfig config = tooltip.getToolTipConfig();
    config.setText(msg);
    tooltip.update(config);
    tooltip.enable();
    if (!tooltip.isAttached()) {
      tooltip.show();
      tooltip.el().updateZIndex(0);
    }
  }

  protected void startMonitoring() {
    if (!bound && monitorValid) {
      bound = true;
      if (monitorTimer == null) {
        monitorTimer = new Timer() {
          @Override
          public void run() {
            RowEditor.this.bindHandler();
          }
        };
      }
      monitorTimer.scheduleRepeating(monitorPoll);
    }
  }

  protected void stopMonitoring() {
    bound = false;
    if (monitorTimer != null) {
      monitorTimer.cancel();
    }
    hideToolTip();
  }

  protected void verifyLayout(boolean force) {
    if (initialized && (isVisible() || force)) {
      Element row = (Element) grid.getView().getRow(rowIndex);

      setSize(El.fly(row).getWidth(false), renderButtons ? btns.getHeight() : 0);

      syncSize();

      ColumnModel cm = grid.getColumnModel();
      for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
        if (!cm.isHidden(i)) {
          Field<?> f = (Field<?>) getItem(i);
          f.show();
          f.getElement().setAttribute("gxt-dindex", "" + cm.getDataIndex(i));
          MarginData md = (MarginData) ComponentHelper.getLayoutData(f);
          f.setWidth(cm.getColumnWidth(i) - md.getMargins().left - md.getMargins().right);
        } else {
          getItem(i).hide();
        }
      }
      layout(true);
      positionButtons();
    }
  }

  private native void clearParent(Widget parent) /*-{
    parent.@com.google.gwt.user.client.ui.Widget::parent=null;
  }-*/;
}

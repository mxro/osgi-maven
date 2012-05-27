/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A specialized content panel intended for use as an application window.
 * 
 * </p> Code snippet:
 * 
 * <pre>
   Window w = new Window();        
   w.setHeading("Product Information");
   w.setModal(true);
   w.setSize(600, 400);
   w.setMaximizable(true);
   w.setToolTip("The ExtGWT product page...");
   w.setUrl("http://www.extjs.com/products/gxt");
   w.show();
 * </pre>
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Activate</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been visually activated via
 * {@link #setActive}.</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Deactivate</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been visually deactivated via
 * {@link #setActive}</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Minimize</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been minimized.</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Maximize</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been maximized.</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Restore</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been restored to its original size after
 * being maximized.</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Resize</b> : WindowEvent(window)<br>
 * <div>Fires after the window has been resized.</div>
 * <ul>
 * <li>window : this</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>BeforeHide</b> : WindowEvent(window, buttonClicked)<br>
 * <div>Fires before the window is to be hidden.</div>
 * <ul>
 * <li>window : this</li>
 * <li>buttonClicked : the button that triggered the hide event</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Hide</b> : WindowEvent(window, buttonClicked)<br>
 * <div>Fires after the window has been hidden.</div>
 * <ul>
 * <li>window : this</li>
 * <li>buttonClicked : the button that triggered the hide event</li>
 * </ul>
 * </dd>
 * </dl>
 */
@SuppressWarnings("deprecation")
public class Window extends ContentPanel {

  protected Draggable dragger;
  protected WindowManager manager;
  protected ToolButton restoreBtn, closeBtn;
  protected int ariaMoveResizeDistance = 5;
  protected boolean removeFromParentOnHide = true;

  private boolean closable = true;
  private boolean constrain = true;
  private Widget focusWidget;
  private boolean maximizable;
  private int minHeight = 100;
  private boolean minimizable;
  private int minWidth = 200;
  private int initialWidth = 300;
  private boolean modal;
  private boolean blinkModal = false;
  private boolean onEsc = true;
  private boolean plain;
  private boolean resizable = true;
  private Layer ghost;
  private ToolButton maxBtn, minBtn, moveBtn, resizeBtn;
  private boolean maximized;
  private ModalPanel modalPanel;
  private Resizable resizer;
  private Point restorePos;
  private Size restoreSize;
  private boolean draggable = true;
  private boolean positioned;
  private boolean autoHide;
  private BaseEventPreview eventPreview;
  private boolean resizing;
  private Element container;
  private Boolean restoreShadow;
  private Boolean restoreWindowScrolling;
  private HandlerRegistration modalPreview;
  private boolean dragging;

  /**
   * Creates a new window.
   */
  public Window() {
    baseStyle = "x-window";
    focusable = true;
    frame = true;
    layoutOnAttach = false;
    setShadow(true);
    shim = true;
    hidden = true;
    setDraggable(true);
    setResizable(true);
  }

  /**
   * Adds a listener to receive window events.
   * 
   * @param listener the listener
   */
  public void addWindowListener(WindowListener listener) {
    addListener(Events.Activate, listener);
    addListener(Events.Deactivate, listener);
    addListener(Events.Minimize, listener);
    addListener(Events.Maximize, listener);
    addListener(Events.Restore, listener);
    addListener(Events.Hide, listener);
    addListener(Events.Show, listener);
  }

  /**
   * Aligns the window to the specified element. Should only be called when the
   * window is visible.
   * 
   * @param elem the element to align to.
   * @param pos the position to align to (see {@link El#alignTo} for more
   *          details)
   * @param offsets the offsets
   */
  public void alignTo(Element elem, String pos, int[] offsets) {
    Point p = el().getAlignToXY(elem, pos, offsets);
    setPagePosition(p.x, p.y);
  }

  @Override
  public void setHeading(String text) {
    super.setHeading(text);
    if (ghost != null) {
      ghost.selectNode(".x-window-header-text").setInnerHtml(text);
    }
  }

  /**
   * Centers the window in the viewport. Should only be called when the window
   * is visible.
   */
  public void center() {
    Point p = el().getAlignToXY(XDOM.getBody(), "c-c", null);
    setPagePosition(p.x, p.y);
  }

  @Deprecated
  public void close() {
    hide(null);
  }

  @Deprecated
  public void close(Button b) {
    hide(b);
  }

  /**
   * Focus the window. If a focusWidget is set, it will receive focus, otherwise
   * the window itself will receive focus.
   */
  public void focus() {
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        doFocus();
      }
    });
  }

  /**
   * Returns true if the window is constrained.
   * 
   * @return the constrain state
   */
  public boolean getConstrain() {
    return constrain;
  }

  /**
   * Returns the windows's container element.
   * 
   * @return the container element or null if not specified.
   */
  public Element getContainer() {
    return container;
  }

  /**
   * Returns the window's draggable instance.
   * 
   * @return the draggable instance
   */
  public Draggable getDraggable() {
    if (dragger == null && draggable) {
      dragger = new Draggable(this, head);
      dragger.setConstrainClient(getConstrain());
      dragger.setSizeProxyToSource(false);
      dragger.addDragListener(new DragListener() {
        @Override
        public void dragEnd(DragEvent de) {
          endDrag(de, false);
        }

        @Override
        public void dragCancel(DragEvent de) {
          endDrag(de, true);
        }

        @Override
        public void dragMove(DragEvent de) {
          moveDrag(de);
        }

        @Override
        public void dragStart(DragEvent de) {
          startDrag(de);
        }
      });
    }
    return dragger;
  }

  /**
   * Returns the focus widget.
   * 
   * @return the focus widget
   */
  public Widget getFocusWidget() {
    return focusWidget;
  }

  /**
   * Returns the window's initial width.
   * 
   * @return the width
   */
  public int getInitialWidth() {
    return initialWidth;
  }

  /**
   * Returns the min height.
   * 
   * @return the min height
   */
  public int getMinHeight() {
    return minHeight;
  }

  /**
   * Returns the min width.
   * 
   * @return the min width
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Returns the window's resizable instance.
   * 
   * @return the resizable
   */
  public Resizable getResizable() {
    if (resizer == null && resizable) {
      resizer = new Resizable(this);
      resizer.setMinWidth(getMinWidth());
      resizer.setMinHeight(getMinHeight());
      resizer.addResizeListener(new ResizeListener() {

        @Override
        public void resizeEnd(final ResizeEvent re) {
          // end resize after event preview
          DeferredCommand.addCommand(new Command() {
            public void execute() {
              onEndResize(re);
            }
          });
        }

        @Override
        public void resizeStart(ResizeEvent re) {
          onStartResize(re);
        }

      });
    }
    return resizer;
  }

  @Override
  public void hide() {
    hide(null);
  }

  /**
   * Hides the window.
   * 
   * @param buttonPressed the button that was pressed or null
   */
  public void hide(Button buttonPressed) {
    if (hidden || !fireEvent(Events.BeforeHide, new WindowEvent(this, buttonPressed))) {
      return;
    }

    if (dragger != null) {
      dragger.cancelDrag();
    }

    hidden = true;

    if (!maximized) {
      restoreSize = getSize();
      restorePos = getPosition(true);
    }

    if (modalPreview != null) {
      modalPreview.removeHandler();
      modalPreview = null;
    }

    onHide();
    manager.unregister(this);
    if (removeFromParentOnHide) {
      removeFromParent();
    }

    if (modalPanel != null) {
      ModalPanel.push(modalPanel);
      modalPanel = null;
    }

    eventPreview.remove();
    notifyHide();

    if (restoreWindowScrolling != null) {
      com.google.gwt.dom.client.Document.get().enableScrolling(restoreWindowScrolling.booleanValue());
    }

    fireEvent(Events.Hide, new WindowEvent(this, buttonPressed));
  }

  /**
   * Returns true if auto hide is enabled.
   * 
   * @return the auto hide state
   */
  public boolean isAutoHide() {
    return autoHide;
  }

  /**
   * Returns true if modal blinking is enabled.
   * 
   * @return the blink modal state
   */
  public boolean isBlinkModal() {
    return blinkModal;
  }

  /**
   * Returns true if the window is closable.
   * 
   * @return the closable state
   */
  public boolean isClosable() {
    return closable;
  }

  /**
   * Returns true if the panel is draggable.
   * 
   * @return the draggable state
   */
  public boolean isDraggable() {
    return draggable;
  }

  /**
   * Returns true if window maximizing is enabled.
   * 
   * @return the maximizable state
   */
  public boolean isMaximizable() {
    return maximizable;
  }

  /**
   * Returns true if the window is maximized.
   * 
   * @return the plain style state
   */
  public boolean isMaximized() {
    return maximized;
  }

  /**
   * Returns true if window minimizing is enabled.
   * 
   * @return the minimizable state
   */
  public boolean isMinimizable() {
    return minimizable;
  }

  /**
   * Returns true if modal behavior is enabled.
   * 
   * @return the modal state
   */
  public boolean isModal() {
    return modal;
  }

  /**
   * Returns true if the window is closed when the esc key is pressed.
   * 
   * @return the on esc state
   */
  public boolean isOnEsc() {
    return onEsc;
  }

  /**
   * Returns true if the plain style is enabled.
   * 
   * @return the plain style state
   */
  public boolean isPlain() {
    return plain;
  }

  /**
   * Returns true if window resizing is enabled.
   * 
   * @return the resizable state
   */
  public boolean isResizable() {
    return resizable;
  }

  /**
   * Fits the window within its current container and automatically replaces the
   * 'maximize' tool button with the 'restore' tool button.
   */
  public void maximize() {
    if (!maximized) {
      restoreSize = getSize();
      restorePos = getPosition(true);
      restoreShadow = getShadow();
      if (container == null) {
        String bodyOverflow = com.google.gwt.dom.client.Document.get().isCSS1Compat()
            ? com.google.gwt.dom.client.Document.get().getDocumentElement().getStyle().getProperty("overflow")
            : com.google.gwt.dom.client.Document.get().getBody().getStyle().getProperty("overflow");
        if (!"hidden".equals(bodyOverflow)) {
          restoreWindowScrolling = true;
        }
        com.google.gwt.dom.client.Document.get().enableScrolling(false);
      }
      maximized = true;
      addStyleName("x-window-maximized");
      head.removeStyleName("x-window-draggable");
      if (layer != null) {
        layer.disableShadow();
      }

      boolean cacheSizesRestore = cacheSizes;
      cacheSizes = false;
      fitContainer();
      cacheSizes = cacheSizesRestore;

      if (maximizable) {
        maxBtn.setVisible(false);
        restoreBtn.setVisible(true);
      }
      if (draggable) {
        dragger.setEnabled(false);
      }
      if (resizable) {
        resizer.setEnabled(false);
      }

      fireEvent(Events.Maximize, new WindowEvent(this));
    } else {
      fitContainer();
    }
  }

  /**
   * Placeholder method for minimizing the window. By default, this method
   * simply fires the minimize event since the behavior of minimizing a window
   * is application-specific. To implement custom minimize behavior, either the
   * minimize event can be handled or this method can be overridden.
   */
  public void minimize() {
    fireEvent(Events.Minimize, new WindowEvent(this));
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {

      // dont bring to front on clicks where active is model as active window
      // may have just been opened from this click event
      Window active = manager.getActive();
      if (active != null && active != this && !active.isModal()) {
        manager.bringToFront(this);
      }
    }
  }

  /**
   * Removes a previously added listener.
   * 
   * @param listener the listener to remove
   */
  public void removeWindowListener(WindowListener listener) {
    removeListener(Events.Activate, listener);
    removeListener(Events.Deactivate, listener);
    removeListener(Events.Minimize, listener);
    removeListener(Events.Maximize, listener);
    removeListener(Events.Restore, listener);
    removeListener(Events.Hide, listener);
    removeListener(Events.Show, listener);
  }

  /**
   * Restores a maximized window back to its original size and position prior to
   * being maximized and also replaces the 'restore' tool button with the
   * 'maximize' tool button.
   */
  public void restore() {
    if (maximized) {
      el().removeStyleName("x-window-maximized");
      if (maximizable) {
        restoreBtn.setVisible(false);
        maxBtn.setVisible(true);
      }
      if (restoreShadow != null && restoreShadow.booleanValue() && layer != null) {
        layer.enableShadow();
        restoreShadow = null;
      }
      if (draggable) {
        dragger.setEnabled(true);
      }
      if (resizable) {
        resizer.setEnabled(true);
      }
      head.addStyleName("x-window-draggable");
      if (restorePos != null) {
        setPosition(restorePos.x, restorePos.y);

        boolean cacheSizesRestore = cacheSizes;
        cacheSizes = false;
        setSize(restoreSize.width, restoreSize.height);
        cacheSizes = cacheSizesRestore;
      }
      if (container == null && restoreWindowScrolling != null) {
        com.google.gwt.dom.client.Document.get().enableScrolling(restoreWindowScrolling.booleanValue());
        restoreWindowScrolling = null;
      }
      maximized = false;
      fireEvent(Events.Restore, new WindowEvent(this));
    }
  }

  /**
   * Makes this the active window by showing its shadow, or deactivates it by
   * hiding its shadow. This method also fires the activate or deactivate event
   * depending on which action occurred.
   */
  public void setActive(boolean active) {
    if (active) {
      if (rendered && !maximized && layer != null) {
        if (getShadow()) {
          layer.enableShadow();
        }
        layer.sync(true);
      }
      if (isVisible()) {
        eventPreview.push();

        if (modal && modalPanel == null) {
          modalPanel = ModalPanel.pop();
          modalPanel.setBlink(blinkModal);
          modalPanel.show(this);
        }
      }

      fireEvent(Events.Activate, new WindowEvent(this));
    } else {
      if (modalPanel != null) {
        ModalPanel.push(modalPanel);
        modalPanel = null;
      }
      hideShadow();
      fireEvent(Events.Deactivate, new WindowEvent(this));
    }
  }

  /**
   * True to hide the window when the user clicks outside of the window's bounds
   * (defaults to false, pre-render).
   * 
   * @param autoHide true for auto hide
   */
  public void setAutoHide(boolean autoHide) {
    this.autoHide = autoHide;
  }

  /**
   * True to blink the window when the user clicks outside of the windows bounds
   * (defaults to false). Only applies window model = true.
   * 
   * @param blinkModal true to blink
   */
  public void setBlinkModal(boolean blinkModal) {
    this.blinkModal = blinkModal;
    if (modalPanel != null) {
      modalPanel.setBlink(blinkModal);
    }
  }

  /**
   * True to display the 'close' tool button and allow the user to close the
   * window, false to hide the button and disallow closing the window (default
   * to true).
   * 
   * @param closable true to enable closing
   */
  public void setClosable(boolean closable) {
    this.closable = closable;
  }

  /**
   * True to constrain the window to the {@link Viewport}, false to allow it to
   * fall outside of the Viewport (defaults to true).
   * 
   * @param constrain true to constrain, otherwise false
   */
  public void setConstrain(boolean constrain) {
    this.constrain = constrain;
    if (dragger != null) {
      dragger.setConstrainClient(constrain);
    }
  }

  /**
   * Sets the container element to be used to size and position the window when
   * maximized.
   * 
   * @param container the container element
   */
  public void setContainer(Element container) {
    this.container = container;
  }

  /**
   * True to enable dragging of this Panel (defaults to false).
   * 
   * @param draggable the draggable to state
   */
  public void setDraggable(boolean draggable) {
    this.draggable = draggable;
    if (draggable) {
      head.addStyleName("x-window-draggable");
      getDraggable();
    } else if (dragger != null) {
      dragger.release();
      dragger = null;
      head.removeStyleName("x-window-draggable");
    }
  }

  /**
   * Widget to be given focus when the window is focused).
   * 
   * @param focusWidget the focus widget
   */
  public void setFocusWidget(Widget focusWidget) {
    this.focusWidget = focusWidget;
  }

  /**
   * The width of the window if no width has been specified (defaults to 300).
   * 
   * @param initialWidth the initial width
   */
  public void setInitialWidth(int initialWidth) {
    this.initialWidth = initialWidth;
  }

  /**
   * True to display the 'maximize' tool button and allow the user to maximize
   * the window, false to hide the button and disallow maximizing the window
   * (defaults to false). Note that when a window is maximized, the tool button
   * will automatically change to a 'restore' button with the appropriate
   * behavior already built-in that will restore the window to its previous
   * size.
   * 
   * @param maximizable the maximizable state
   */
  public void setMaximizable(boolean maximizable) {
    this.maximizable = maximizable;
  }

  /**
   * The minimum height in pixels allowed for this window (defaults to 100).
   * Only applies when resizable = true.
   * 
   * @param minHeight the min height
   */
  public void setMinHeight(int minHeight) {
    this.minHeight = minHeight;
    if (resizer != null) {
      resizer.setMinHeight(minHeight);
    }
  }

  /**
   * True to display the 'minimize' tool button and allow the user to minimize
   * the window, false to hide the button and disallow minimizing the window
   * (defaults to false). Note that this button provides no implementation --
   * the behavior of minimizing a window is implementation-specific, so the
   * minimize event must be handled and a custom minimize behavior implemented
   * for this option to be useful.
   * 
   * @param minimizable true to enabled minimizing
   */
  public void setMinimizable(boolean minimizable) {
    this.minimizable = minimizable;
  }

  /**
   * The minimum width in pixels allowed for this window (defaults to 200). Only
   * applies when resizable = true.
   * 
   * @param minWidth the minimum height
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
    if (resizer != null) {
      resizer.setMinWidth(minWidth);
    }
  }

  /**
   * True to make the window modal and mask everything behind it when displayed,
   * false to display it without restricting access to other UI elements
   * (defaults to false).
   * 
   * @param modal true for modal
   */
  public void setModal(boolean modal) {
    this.modal = modal;
  }

  /**
   * Allows override of the built-in processing for the escape key. Default
   * action is to close the Window.
   * 
   * @param onEsc true to close window on esc key press
   */
  public void setOnEsc(boolean onEsc) {
    this.onEsc = onEsc;
  }

  @Override
  public void setPagePosition(int x, int y) {
    super.setPagePosition(x, y);
    positioned = true;
  }

  /**
   * True to render the window body with a transparent background so that it
   * will blend into the framing elements, false to add a lighter background
   * color to visually highlight the body element and separate it more
   * distinctly from the surrounding frame (defaults to false).
   * 
   * @param plain true to enable the plain style
   */
  public void setPlain(boolean plain) {
    this.plain = plain;
  }

  @Override
  public void setPosition(int left, int top) {
    super.setPosition(left, top);
    positioned = true;
  }

  /**
   * True to allow user resizing at each edge and corner of the window, false to
   * disable resizing (defaults to true).
   * 
   * @param resizable true to enabled resizing
   */
  public void setResizable(boolean resizable) {
    this.resizable = resizable;
    if (resizable) {
      getResizable();
    } else if (resizer != null) {
      resizer.release();
      resizer = null;
    }
  }

  @Override
  public void setZIndex(int zIndex) {
    super.setZIndex(zIndex);
    if (ghost != null) {
      ghost.setZIndex(zIndex);
    }
    if (modalPanel != null && modalPanel.rendered) {
      modalPanel.el().setZIndex(zIndex - 9);
    }
  }

  /**
   * Shows the window, rendering it first if necessary, or activates it and
   * brings it to front if hidden.
   */
  public void show() {
    if (!hidden || !fireEvent(Events.BeforeShow, new WindowEvent(this))) {
      return;
    }
    // remove hide style, else layout fails
    removeStyleName(getHideMode().value());
    addStyleName(HideMode.VISIBILITY.value());
    if (!isAttached()) {
      RootPanel.get().add(this);
    }
    
    el().makePositionable(true);
    onShow();
    manager.register(this);

    afterShow();
    notifyShow();
  }

  /**
   * Sends this window to the back of (lower z-index than) any other visible
   * windows.
   */
  public void toBack() {
    manager.sendToBack(this);
  }

  /**
   * Brings this window to the front of any other visible windows.
   */
  public void toFront() {
    manager.bringToFront(this);
  }

  protected void afterShow() {
    hidden = false;

    // layout early to render window's content for size calcs
    if (!layoutExecuted || isLayoutNeeded()) {
      layout();
    }

    if (restorePos != null) {
      setPosition(restorePos.x, restorePos.y);
      if (restoreSize != null) {
        setSize(restoreSize.width, restoreSize.height);
      }
    }
    if (restoreWindowScrolling != null) {
      com.google.gwt.dom.client.Document.get().enableScrolling(false);
    }

    int h = getHeight();
    int w = getWidth();
    if (h < minHeight && w < minWidth) {
      setSize(minWidth, minHeight);
    } else if (h < minHeight) {
      setHeight(minHeight);
    } else if (w < minWidth) {
      setWidth(minWidth);
    }

    // not positioned, then center
    if (!positioned) {
      el().center(true);
    }

    el().updateZIndex(0);
    if (modal) {
      modalPreview = Event.addNativePreviewHandler(new NativePreviewHandler() {
        public void onPreviewNativeEvent(NativePreviewEvent event) {
          if (Element.is(event.getNativeEvent().getEventTarget())) {
            Element target = (Element) Element.as(event.getNativeEvent().getEventTarget());

            String tag = target.getTagName();
            // ignore html and body because of frames
            if (!resizing && !dragging && !tag.equalsIgnoreCase("html") && !tag.equalsIgnoreCase("body")
                && event.getTypeInt() != Event.ONLOAD && manager.getActive() == Window.this
                && (modalPanel == null || (modalPanel != null && !modalPanel.getElement().isOrHasChild(target)))
                && !Window.this.getElement().isOrHasChild(target) && fly(target).findParent(".x-ignore", -1) == null) {
              ArrayList<Component> col = new ArrayList<Component>(ComponentManager.get().getAll());
              for (Component c : col) {
                if (c instanceof TriggerField<?>) {
                  triggerBlur((TriggerField<?>) c);
                } else if (c instanceof Menu) {
                  ((Menu) c).hide(true);
                }
              }
              Window.this.focus();
            }
          }
        }

        private native void triggerBlur(TriggerField<?> field) /*-{
    field.@com.extjs.gxt.ui.client.widget.form.TriggerField::triggerBlur(Lcom/extjs/gxt/ui/client/event/ComponentEvent;)(null);
  }-*/;
      });
    }

    // missing cursor workaround
    if (GXT.isGecko) {
      El e = el().selectNode(".x-window-bwrap");
      if (e != null) {
        e.dom.getStyle().setProperty("overflow", "auto");
        e.dom.getStyle().setProperty("position", "static");
      }
    }

    eventPreview.add();

    if (maximized) {
      maximize();
    }
    removeStyleName(HideMode.VISIBILITY.value());

    if (GXT.isAriaEnabled()) {
      Accessibility.setState(getElement(), "aria-hidden", "false");
    }

    fireEvent(Events.Show, new WindowEvent(this));
    toFront();
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new WindowEvent(this, event);
  }

  protected Layer createGhost() {
    Element div = DOM.createDiv();
    Layer l = new Layer(div);
    if (shim && GXT.useShims) {
      l.enableShim();
    }
    l.dom.setClassName("x-panel-ghost");
    if (head != null) {
      DOM.appendChild(div, el().firstChild().cloneNode(true));
    }
    l.dom.appendChild(DOM.createElement("ul"));
    return l;
  }

  protected void doFocus() {
    if (focusWidget != null) {
      if (focusWidget instanceof Component) {
        ((Component) focusWidget).focus();
      } else {
        fly(focusWidget.getElement()).focus();
      }
    } else {
      Window.super.focus();
    }
  }

  protected void endDrag(DragEvent de, boolean canceled) {
    dragging = false;
    unghost(de);
    if (!canceled) {
      restorePos = getPosition(true);
      positioned = true;
    }

    if (layer != null && getShadow()) {
      layer.enableShadow();
    }
    focus();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        if (Window.this.eventPreview != null && Window.this.ghost != null) {
          Window.this.eventPreview.getIgnoreList().remove(Window.this.ghost.dom);
        }
      }
    });
  }

  protected void fitContainer() {
    if (container != null) {
      Rectangle bounds = fly(container).getBounds();
      setPagePosition(bounds.x, bounds.y);
      setSize(bounds.width, bounds.height);
    } else {
      setPosition(0, 0);
      setSize(XDOM.getViewportWidth(), XDOM.getViewportHeight());
    }
  }

  protected ModalPanel getModalPanel() {
    return modalPanel;
  }

  protected Layer ghost() {
    Layer g = ghost != null ? ghost : createGhost();
    g.setVisibility(false);
    Rectangle box = getBounds(false);
    g.setBounds(box, true);
    int h = bwrap.getHeight();
    g.getChild(1).setHeight(h - 1, true);
    return g;
  }

  protected void initTools() {
    super.initTools();

    if (GXT.isAriaEnabled()) {
      moveBtn = new ToolButton("x-tool-move");
      moveBtn.getAriaSupport().setLabel(GXT.MESSAGES.window_ariaMove());
      moveBtn.getAriaSupport().setDescription(GXT.MESSAGES.window_ariaMoveDescription());
      head.addTool(moveBtn);

      resizeBtn = new ToolButton("x-tool-resize");
      resizeBtn.getAriaSupport().setLabel(GXT.MESSAGES.window_ariaResize());
      resizeBtn.getAriaSupport().setDescription(GXT.MESSAGES.window_ariaResizeDescription());
      head.addTool(resizeBtn);
    }

    if (minimizable) {
      minBtn = new ToolButton("x-tool-minimize");
      minBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
        public void componentSelected(IconButtonEvent ce) {
          minimize();
        }
      });
      head.addTool(minBtn);
    }

    if (maximizable) {
      maxBtn = new ToolButton("x-tool-maximize");
      maxBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
        public void componentSelected(IconButtonEvent ce) {
          maximize();
        }
      });
      head.addTool(maxBtn);

      restoreBtn = new ToolButton("x-tool-restore");
      restoreBtn.setVisible(false);
      restoreBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
        public void componentSelected(IconButtonEvent ce) {
          restore();
        }
      });
      head.addTool(restoreBtn);
    }

    if (closable) {
      closeBtn = new ToolButton("x-tool-close");
      if (GXT.isAriaEnabled()) {
        closeBtn.setTitle(GXT.MESSAGES.messageBox_close());
      }
      closeBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
        public void handleEvent(ComponentEvent ce) {
          hide();
        }
      });
      head.addTool(closeBtn);
    }
  }

  protected void moveDrag(DragEvent de) {

  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (eventPreview != null) {
      eventPreview.remove();
    }
  }

  @Override
  protected void onFocus(ComponentEvent ce) {
    super.onFocus(ce);
    if (GXT.isFocusManagerEnabled()) {
      if (focusWidget != null) {
        El.fly(focusWidget.getElement()).focus();
      }
    }
  }

  protected void onEndResize(ResizeEvent re) {
    resizing = false;
  }

  protected void onKeyPress(WindowEvent we) {
    int keyCode = we.getKeyCode();
    boolean t = getElement().isOrHasChild((com.google.gwt.dom.client.Element) we.getEvent().getEventTarget().cast());
    boolean key = GXT.isFocusManagerEnabled() ? we.isShiftKey() : true;
    if (key && closable && onEsc && keyCode == KeyCodes.KEY_ESCAPE && t) {
      hide();
    }

    if (GXT.isAriaEnabled()) {
      if (we.getTarget() == moveBtn.getElement()) {
        Point p = getPosition(true);
        switch (we.getKeyCode()) {
          case KeyCodes.KEY_LEFT:
            setPosition(p.x - ariaMoveResizeDistance, p.y);
            break;
          case KeyCodes.KEY_RIGHT:
            setPosition(p.x + ariaMoveResizeDistance, p.y);
            break;
          case KeyCodes.KEY_DOWN:
            setPosition(p.x, p.y + ariaMoveResizeDistance);
            break;
          case KeyCodes.KEY_UP:
            setPosition(p.x, p.y - ariaMoveResizeDistance);
            break;
        }
      } else if (we.getTarget() == resizeBtn.getElement()) {
        if (!resizable) {
          return;
        }
        Size s = getSize();
        switch (we.getKeyCode()) {
          case KeyCodes.KEY_LEFT:
            setSize(s.width - ariaMoveResizeDistance, s.height);
            break;
          case KeyCodes.KEY_RIGHT:
            setSize(s.width + ariaMoveResizeDistance, s.height);
            break;
          case KeyCodes.KEY_DOWN:
            setSize(s.width, s.height + ariaMoveResizeDistance);
            break;
          case KeyCodes.KEY_UP:
            setSize(s.width, s.height - ariaMoveResizeDistance);
            break;
        }
      }
    }
  }

  @Override
  protected void onRender(Element parent, int pos) {
    super.onRender(parent, pos);

    el().makePositionable(true);

    if (manager == null) {
      manager = WindowManager.get();
    }

    if (plain) {
      addStyleName("x-window-plain");
    }

    eventPreview = new BaseEventPreview() {

      @Override
      protected boolean onAutoHide(PreviewEvent ce) {
        if (autoHide) {
          if (resizing) {
            return false;
          }
          hide();
          return true;
        }
        return false;
      }

      @Override
      protected void onPreviewKeyPress(PreviewEvent pe) {
        WindowEvent we = new WindowEvent(Window.this, pe.getEvent());
        onKeyPress(we);
      }

    };
    eventPreview.getIgnoreList().add(getElement());

    sinkEvents(Event.ONMOUSEDOWN | Event.ONKEYPRESS);

    el().setTabIndex(0);
    el().setElementAttribute("hideFocus", "true");

    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), "alertdialog");
      Accessibility.setState(getElement(), "aria-labelledby", head.getId() + "-label");
      Accessibility.setState(getElement(), "aria-hidden", "true");
    }

    if (modal || maximizable || constrain) {
      monitorWindowResize = true;
    }

    if (super.width == null) {
      setWidth(Math.max(initialWidth, minWidth));
    }
  }

  protected void onStartResize(ResizeEvent re) {
    resizing = true;
  }

  @Override
  protected void onWindowResize(int width, int height) {
    if (isVisible()) {
      if (maximized) {
        fitContainer();
      } else {
        if (constrain) {
          setPagePosition(el().adjustForConstraints(getPosition(false)));
        }
      }
    }
  }

  protected void showWindow(boolean show) {
    if (show) {
      onShow();
    } else {
      onHide();
    }
  }

  @Override
  protected void onHide() {
    super.onHide();
    if (GXT.isAriaEnabled()) {
      Accessibility.setState(getElement(), "aria-hidden", "true");
    }
  }

  protected void startDrag(DragEvent de) {
    dragging = true;
    WindowManager.get().bringToFront(this);
    hideShadow();
    ghost = ghost();
    if (eventPreview != null && ghost != null) {
      eventPreview.getIgnoreList().add(ghost.dom);
    }
    showWindow(false);
    Draggable d = de.getDraggable();
    d.setProxy(ghost);
  }

  protected void unghost(DragEvent de) {
    showWindow(true);
    setPagePosition(de.getX(), de.getY());
  }
}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.tips;

import java.util.Date;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Region;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

/**
 * A standard tooltip implementation for providing additional information when
 * hovering over a target element.
 */
public class ToolTip extends Tip {

  protected El anchorEl;
  protected String anchorStyle;
  protected Timer dismissTimer;
  protected Timer hideTimer;
  protected Listener<ComponentEvent> listener;
  protected Timer showTimer;
  protected Component target;
  protected Point targetXY = new Point(0, 0);
  protected String title, text;
  protected ToolTipConfig toolTipConfig;

  private Date lastActive;

  /**
   * Creates a new tool tip.
   */
  public ToolTip() {
    toolTipConfig = new ToolTipConfig();
    lastActive = new Date();
    monitorWindowResize = true;
  }

  /**
   * Creates a new tool tip.
   * 
   * @param target the target widget
   */
  public ToolTip(Component target) {
    this();
    initTarget(target);
  }

  /**
   * Creates a new tool tip for the given target.
   * 
   * @param target the target widget
   */
  public ToolTip(Component target, ToolTipConfig config) {
    this();
    updateConfig(config);
    initTarget(target);
  }

  /**
   * Returns the quick show interval.
   * 
   * @return the quick show interval
   */
  public int getQuickShowInterval() {
    return quickShowInterval;
  }

  /**
   * Returns the current tool tip config.
   * 
   * @return the tool tip config
   */
  public ToolTipConfig getToolTipConfig() {
    return toolTipConfig;
  }

  @Override
  public void hide() {
    clearTimers();
    lastActive = new Date();
    super.hide();
  }

  public void initTarget(final Component target) {
    if (this.target != null) {
      this.target.removeListener(Events.OnMouseOver, listener);
      this.target.removeListener(Events.OnMouseOut, listener);
      this.target.removeListener(Events.OnMouseMove, listener);
      this.target.removeListener(Events.Hide, listener);
      this.target.removeListener(Events.Detach, listener);
      this.target.removeListener(Events.Render, listener);
      if (GXT.isFocusManagerEnabled()) {
        this.target.removeListener(Events.OnFocus, listener);
        this.target.removeListener(Events.OnBlur, listener);
        this.target.removeListener(Events.OnKeyDown, listener);
      }
    }

    this.target = target;
    if (listener == null) {
      listener = new Listener<ComponentEvent>() {
        public void handleEvent(ComponentEvent be) {
          Element source = target.getElement();
          EventType type = be.getType();
          if (type == Events.OnMouseOver) {
            EventTarget from = be.getEvent().getRelatedEventTarget();
            if (from == null
                || (Element.is(source) && Element.is(from) && !DOM.isOrHasChild(source, (Element) Element.as(from)))) {
              onTargetOver(be);
            }
          } else if (type == Events.OnMouseOut) {
            EventTarget to = be.getEvent().getRelatedEventTarget();
            if (to == null
                || (Element.is(source) && Element.is(to) && !DOM.isOrHasChild(source, (Element) Element.as(to)))) {
              onTargetOut(be);
            }
          } else if (type == Events.OnMouseMove) {
            onMouseMove(be);
          } else if (type == Events.Hide || type == Events.Detach) {
            hide();
          } else if (type == Events.OnFocus) {
            if (GXT.isFocusManagerEnabled()) {
              targetXY = be.getXY();
              targetXY.y += target.getOffsetHeight();
              targetXY.x += target.getOffsetWidth();
              show();
            }
          } else if (type == Events.OnBlur) {
            if (GXT.isFocusManagerEnabled() && !isClosable()) {
              hide();
            }
          } else if (type == Events.OnKeyDown) {
            if (GXT.isFocusManagerEnabled() && be.getKeyCode() == KeyCodes.KEY_ESCAPE) {
              target.getFocusSupport().setIgnore(true);
              hide();
            }
          }
        }
      };
    }
    if (target != null) {
      target.addListener(Events.OnMouseOver, listener);
      target.addListener(Events.Render, listener);
      target.addListener(Events.OnMouseOut, listener);
      target.addListener(Events.OnMouseMove, listener);
      target.addListener(Events.Hide, listener);
      target.addListener(Events.Detach, listener);
      if (GXT.isFocusManagerEnabled()) {
        this.target.addListener(Events.OnFocus, listener);
        this.target.addListener(Events.OnBlur, listener);
        this.target.addListener(Events.OnKeyDown, listener);
      }
      target.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE);
    }
  }

  /**
   * Sets the quick show interval (defaults to 250).
   * 
   * @param quickShowInterval the quick show interval
   */
  public void setQuickShowInterval(int quickShowInterval) {
    this.quickShowInterval = quickShowInterval;
  }

  @Override
  public void show() {
    if (disabled) return;
    String origAnchor = null;
    boolean origConstrainPosition = false;
    if (toolTipConfig.getAnchor() != null) {
      origAnchor = toolTipConfig.getAnchor();
      // pre-show it off screen so that the el will have dimensions
      // for positioning calcs when getting xy next
      showAt(-1000, -1000);
      origConstrainPosition = this.constrainPosition;
      constrainPosition = false;
    }
    showAt(getTargetXY(0));

    if (toolTipConfig.getAnchor() != null) {
      anchorEl.show();
      syncAnchor();
      constrainPosition = origConstrainPosition;
      toolTipConfig.setAnchor(origAnchor);
    } else {
      anchorEl.hide();
    }
  }

  @Override
  public void showAt(int x, int y) {
    if (disabled) return;
    lastActive = new Date();
    clearTimers();
    super.showAt(x, y);
    if (toolTipConfig.getAnchor() != null) {
      anchorEl.show();
      syncAnchor();
    } else {
      anchorEl.hide();
    }
    if (toolTipConfig.getDismissDelay() > 0 && toolTipConfig.isAutoHide() && !toolTipConfig.isCloseable()) {
      dismissTimer = new Timer() {
        public void run() {
          hide();
        }
      };
      dismissTimer.schedule(toolTipConfig.getDismissDelay());
    }
  }

  /**
   * Updates the tool tip with the given config.
   * 
   * @param config the tool tip config
   */
  public void update(ToolTipConfig config) {
    updateConfig(config);
    if (isRendered() && isAttached()) {
      updateContent();
      doAutoWidth();
    }
  }

  protected void afterRender() {
    super.afterRender();
    anchorEl.setStyleAttribute("zIndex", el().getZIndex() + 1);
  }

  protected void clearTimer(String timer) {
    if (timer.equals("hide")) {
      if (hideTimer != null) {
        hideTimer.cancel();
        hideTimer = null;
      }
    } else if (timer.equals("dismiss")) {
      if (dismissTimer != null) {
        dismissTimer.cancel();
        dismissTimer = null;
      }
    } else if (timer.equals("show")) {
      if (showTimer != null) {
        showTimer.cancel();
        showTimer = null;
      }
    }
  }

  protected void clearTimers() {
    clearTimer("show");
    clearTimer("dismiss");
    clearTimer("hide");
  }

  protected void delayHide() {
    if (isAttached() && hideTimer == null && toolTipConfig.isAutoHide() && !toolTipConfig.isCloseable()) {
      if (toolTipConfig.getHideDelay() == 0) {
        hide();
        return;
      }
      hideTimer = new Timer() {
        public void run() {
          hide();
        }
      };
      hideTimer.schedule(toolTipConfig.getHideDelay());
    }
  }

  protected void delayShow() {
    if (!isAttached() && showTimer == null) {
      if ((new Date().getTime() - lastActive.getTime()) < quickShowInterval) {
        show();
      } else {
        if (toolTipConfig.getShowDelay() > 0) {
          showTimer = new Timer() {
            public void run() {
              show();
            }
          };
          showTimer.schedule(toolTipConfig.getShowDelay());
        } else {
          show();
        }
      }
    } else if (isAttached()) {
      show();
    }
  }

  protected String getAnchorAlign() {
    if (toolTipConfig.getAnchor().equals("top")) {
      return "tl-bl";
    } else if (toolTipConfig.getAnchor().equals("left")) {
      return "tl-tr";
    } else if (toolTipConfig.getAnchor().equals("right")) {
      return "tr-tl";
    }

    return "bl-tl";
  }

  protected int[] getOffsets() {
    int[] offsets;
    char ap = toolTipConfig.getAnchor().charAt(0);
    if (toolTipConfig.isAnchorToTarget() && !toolTipConfig.isTrackMouse()) {
      switch (ap) {
        case 't':
          offsets = new int[] {0, 9};
          break;
        case 'b':
          offsets = new int[] {0, -13};
          break;
        case 'r':
          offsets = new int[] {-13, 0};
          break;
        default:
          offsets = new int[] {9, 0};
          break;
      }
    } else {
      int anchorOffset = toolTipConfig.getAnchorOffset();
      switch (ap) {
        case 't':
          offsets = new int[] {-15 - anchorOffset, 30};
          break;
        case 'b':
          offsets = new int[] {-19 - anchorOffset, -13 - el().dom.getOffsetHeight()};
          break;
        case 'r':
          offsets = new int[] {-15 - el().dom.getOffsetWidth(), -13 - anchorOffset};
          break;
        default:
          offsets = new int[] {25, -13 - anchorOffset};
          break;
      }
    }
    int[] mouseOffset = toolTipConfig.getMouseOffset();
    if (mouseOffset != null) {
      offsets[0] += mouseOffset[0];
      offsets[1] += mouseOffset[1];
    }

    return offsets;
  }

  protected void onMouseMove(ComponentEvent ce) {
    targetXY = ce.getXY();
    if (isRendered() && isAttached() && toolTipConfig.isTrackMouse()) {
      String origAnchor = toolTipConfig.getAnchor();
      Point p = getTargetXY(0);
      toolTipConfig.setAnchor(origAnchor);
      if (constrainPosition) {
        p = el().adjustForConstraints(p);
      }
      setPagePosition(p);
    }
  }

  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    anchorEl = new El(DOM.createDiv());
    anchorEl.addStyleName("x-tip-anchor");
    el().appendChild(anchorEl.dom);
  }

  protected void onTargetOut(ComponentEvent ce) {
    if (disabled) {
      return;
    }
    clearTimer("show");
    delayHide();

  }

  protected void onTargetOver(ComponentEvent ce) {
    if (disabled || !ce.within(target.getElement())) {
      return;
    }

    clearTimer("hide");
    targetXY = ce.getXY();
    delayShow();
  }

  @Override
  protected void onWindowResize(int width, int height) {
    super.onWindowResize(width, height);
    // this can only be reached if the tooltip is already visible, show it again
    // to sync anchor
    show();
  }

  protected void syncAnchor() {
    String anchorPos, targetPos;
    int[] offset;
    int anchorOffset = toolTipConfig.getAnchorOffset();
    switch (toolTipConfig.getAnchor().charAt(0)) {
      case 't':
        anchorPos = "b";
        targetPos = "tl";
        offset = new int[] {20 + anchorOffset, 2};
        break;
      case 'r':
        anchorPos = "l";
        targetPos = "tr";
        offset = new int[] {-2, 11 + anchorOffset};
        break;
      case 'b':
        anchorPos = "t";
        targetPos = "bl";
        offset = new int[] {20 + anchorOffset, -2};
        break;
      default:
        anchorPos = "r";
        targetPos = "tl";
        offset = new int[] {2, 11 + anchorOffset};
        break;
    }
    anchorEl.alignTo(el().dom, anchorPos + "-" + targetPos, offset);
  }

  @Override
  protected void updateContent() {
    getHeader().setText(title);
    // show header or not
    getHeader().el().selectNode("#" + getHeader().getId() + "-label").setVisible(title != null && !"".equals(title));

    if (toolTipConfig.getTemplate() != null) {
      Params p = toolTipConfig.getParams();
      if (p == null) p = new Params();
      p.set("text", text);
      p.set("title", title);
      toolTipConfig.getTemplate().overwrite(getBody().dom, p);
    } else {
      getBody().update(Util.isEmptyString(text) ? "&#160;" : text);
    }
  }

  private Point getTargetXY(int targetCounter) {
    if (toolTipConfig.getAnchor() != null) {
      targetCounter++;
      int[] offsets = getOffsets();
      Point xy = (toolTipConfig.isAnchorToTarget() && !toolTipConfig.isTrackMouse()) ? el().getAlignToXY(
          target.el().dom, getAnchorAlign(), null) : targetXY;

      int dw = XDOM.getViewWidth(false) - 5;
      int dh = XDOM.getViewHeight(false) - 5;
      int scrollX = XDOM.getBodyScrollLeft() + 5;
      int scrollY = XDOM.getBodyScrollTop() + 5;

      int[] axy = new int[] {xy.x + offsets[0], xy.y + offsets[1]};
      Size sz = getSize();
      Region r = target.el().getRegion();
      anchorEl.removeStyleName(anchorStyle);

      // if we are not inside valid ranges we try to switch the anchor
      if (!((toolTipConfig.getAnchor().equals("top") && (sz.height + offsets[1] + scrollY < dh - r.bottom))
          || (toolTipConfig.getAnchor().equals("right") && (sz.width + offsets[0] + scrollX < r.left))
          || (toolTipConfig.getAnchor().equals("bottom") && (sz.height + offsets[1] + scrollY < r.top)) || (toolTipConfig.getAnchor().equals(
          "left") && (sz.width + offsets[0] + scrollX < dw - r.right)))
          && targetCounter < 4) {
        if (sz.width + offsets[0] + scrollX < dw - r.right) {
          toolTipConfig.setAnchor("left");
          return getTargetXY(targetCounter);
        }
        if (sz.width + offsets[0] + scrollX < r.left) {
          toolTipConfig.setAnchor("right");
          return getTargetXY(targetCounter);
        }
        if (sz.height + offsets[1] + scrollY < dh - r.bottom) {
          toolTipConfig.setAnchor("top");
          return getTargetXY(targetCounter);
        }
        if (sz.height + offsets[1] + scrollY < r.top) {
          toolTipConfig.setAnchor("bottom");
          return getTargetXY(targetCounter);
        }
      }

      anchorStyle = "x-tip-anchor-" + toolTipConfig.getAnchor();
      anchorEl.addStyleName(anchorStyle);
      targetCounter = 0;
      return new Point(axy[0], axy[1]);

    } else {
      int x = targetXY.x;
      int y = targetXY.y;

      int[] mouseOffset = toolTipConfig.getMouseOffset();
      if (mouseOffset != null) {
        x += mouseOffset[0];
        y += mouseOffset[1];
      }
      return new Point(x, y);
    }

  }

  protected void updateConfig(ToolTipConfig config) {
    this.toolTipConfig = config;
    if (!config.isEnabled()) {
      clearTimers();
      hide();
    }
    setMinWidth(config.getMinWidth());
    setMaxWidth(config.getMaxWidth());
    setClosable(config.isCloseable());
    text = config.getText();
    title = config.getTitle();
  }

}

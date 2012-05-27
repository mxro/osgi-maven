/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.google.gwt.user.client.Event;

/**
 * Adds scroll support to a given element.
 */
public class ScrollSupport {

  private boolean autoScroll = true;
  private int scrollDelay = 400;
  private int scrollRepeatDelay = 300;
  private Rectangle bottomBounds, topBounds;
  private int scrollRegionHeight = 25;
  private El scrollElement;
  private boolean active;

  private DelayedTask scrollUpTask = new DelayedTask(new Listener<DomEvent>() {
    public void handleEvent(DomEvent de) {
      onScrollUp(de);
    }
  });
  private DelayedTask scrollDownTask = new DelayedTask(new Listener<DomEvent>() {
    public void handleEvent(DomEvent de) {
      onScrollDown(de);
    }
  });

  private BaseEventPreview preview = new BaseEventPreview() {
    protected boolean onPreview(PreviewEvent pe) {
      super.onPreview(pe);
      if (pe.getEventTypeInt() == Event.ONMOUSEMOVE) {
        onMove(pe);
      }
      return true;
    };
  };

  /**
   * Creates a new scroll support instance. The scroll element must be set, see
   * {@link #setScrollElement(El)}.
   */
  public ScrollSupport() {
    preview.setAutoHide(false);
  }

  /**
   * Creates a new scroll support instance.
   * 
   * @param scrollElement the scroll element
   */
  public ScrollSupport(El scrollElement) {
    this();
    this.scrollElement = scrollElement;
  }

  /**
   * Returns the scroll delay.
   * 
   * @return the scroll delay in milliseconds
   */
  public int getScrollDelay() {
    return scrollDelay;
  }

  /**
   * Returns the scroll element.
   * 
   * @return the scroll element
   */
  public El getScrollElement() {
    return scrollElement;
  }

  /**
   * Returns the scroll region height.
   * 
   * @return the scroll region height
   */
  public int getScrollRegionHeight() {
    return scrollRegionHeight;
  }

  /**
   * Returns the scroll repeat delay.
   * 
   * @return the scroll repeat delay
   */
  public int getScrollRepeatDelay() {
    return scrollRepeatDelay;
  }

  /**
   * Returns true if auto scroll is enabled.
   * 
   * @return true if auto scroll is enabled, otherwise false
   */
  public boolean isAutoScroll() {
    return autoScroll;
  }

  /**
   * True to enable auto scroll (defaults to true).
   * 
   * @param autoScroll true if auto scroll is enabled
   */
  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  /**
   * Sets the amount of time before auto scroll is activated (defaults to 400).
   * 
   * @param scrollDelay the scroll delay in milliseconds
   */
  public void setScrollDelay(int scrollDelay) {
    this.scrollDelay = scrollDelay;
  }

  /**
   * Sets the scroll element.
   * 
   * @param scrollElement the scroll element
   */
  public void setScrollElement(El scrollElement) {
    this.scrollElement = scrollElement;
  }

  /**
   * Sets the height of the scroll region (defaults to 25).
   * 
   * @param scrollRegionHeight the scroll region in pixels
   */
  public void setScrollRegionHeight(int scrollRegionHeight) {
    this.scrollRegionHeight = scrollRegionHeight;
  }

  /**
   * Sets the amount of time between scroll changes after auto scrolling is
   * activated (defaults to 300).
   * 
   * @param scrollRepeatDelay the repeat delay in milliseconds
   */
  public void setScrollRepeatDelay(int scrollRepeatDelay) {
    this.scrollRepeatDelay = scrollRepeatDelay;
  }

  public void start() {
    if (!active) {
      active = true;
      onStart();
    }
  }

  public void stop() {
    active = false;
    preview.remove();
    scrollDownTask.cancel();
    scrollUpTask.cancel();
  }

  protected void onMove(DomEvent de) {
    Point p = new Point(de.getClientX(), de.getClientY());
    if (topBounds.contains(p)) {
      scrollUpTask.delay(scrollDelay);
      scrollDownTask.cancel();
    } else if (bottomBounds.contains(p)) {
      scrollDownTask.delay(scrollDelay);
      scrollUpTask.cancel();
    } else {
      scrollUpTask.cancel();
      scrollDownTask.cancel();
    }
  }

  protected void onScrollDown(DomEvent de) {
    scrollElement.setScrollTop(scrollElement.getScrollTop() + scrollRegionHeight);
    scrollDownTask.delay(scrollRepeatDelay);
  }

  protected void onScrollUp(DomEvent de) {
    scrollElement.setScrollTop(Math.max(0, scrollElement.getScrollTop() - scrollRegionHeight));
    scrollUpTask.delay(scrollRepeatDelay);
  }

  protected void onStart() {
    if (!autoScroll) return;

    topBounds = scrollElement.getBounds();
    topBounds.height = 20;

    bottomBounds = scrollElement.getBounds();
    bottomBounds.y = bottomBounds.y + bottomBounds.height - 20;
    bottomBounds.height = 20;

    preview.add();
  }
}

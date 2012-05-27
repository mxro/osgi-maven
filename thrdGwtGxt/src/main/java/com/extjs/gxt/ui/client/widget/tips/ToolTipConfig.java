/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.tips;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.util.Params;

/**
 * Configuration information for a tool tip.
 */
public class ToolTipConfig {

  private String anchor;
  private int anchorOffset = 0;
  private boolean anchorToTarget = true;
  private boolean autoHide = true;
  private int dismissDelay = 5000;
  private boolean enabled = true;
  private int hideDelay = 200;
  private int maxWidth = 300;
  private int minWidth = 40;
  private int[] mouseOffset = new int[] {15, 18};
  private Params params;
  private int showDelay = 500;
  private Template template;
  private String text;
  private String title;
  private boolean trackMouse;
  private boolean closeable;

  /**
   * Creates a new tool tip config.
   */
  public ToolTipConfig() {

  }

  /**
   * Creates a new tool tip config with the given text.
   * 
   * @param text the tool tip text
   */
  public ToolTipConfig(String text) {
    this.setText(text);
  }

  /**
   * Creates a new tool tip config with the given title and text.
   * 
   * @param title the tool tip title
   * @param text the tool tip text
   */
  public ToolTipConfig(String title, String text) {
    this.setTitle(title);
    this.setText(text);
  }

  /**
   * Returns the anchor position.
   * 
   * @return the anchor position
   */
  public String getAnchor() {
    return anchor;
  }

  /**
   * Returns the distance in pixels of the tooltip and target element.
   * 
   * @return the offset
   */
  public int getAnchorOffset() {
    return anchorOffset;
  }

  /**
   * Returns the dismiss delay.
   * 
   * @return the dismiss delay
   */
  public int getDismissDelay() {
    return dismissDelay;
  }

  /**
   * Returns the hide delay in milliseconds.
   * 
   * @return the delay
   */
  public int getHideDelay() {
    return hideDelay;
  }

  /**
   * Returns the tooltip's maximum width.
   * 
   * @return the maximum width
   */
  public int getMaxWidth() {
    return maxWidth;
  }

  /**
   * Returns the tooltip's minimum width.
   * 
   * @return the minimum width
   */
  public int getMinWidth() {
    return minWidth;
  }

  /**
   * Returns the mouse offset.
   * 
   * @return the offset
   */
  public int[] getMouseOffset() {
    return mouseOffset;
  }

  /**
   * Returns the params.
   * 
   * @return the params
   */
  public Params getParams() {
    return params;
  }

  /**
   * Returns the show delay in milliseconds.
   * 
   * @return the delay
   */
  public int getShowDelay() {
    return showDelay;
  }

  /**
   * Returns the template.
   * 
   * @return the template
   */
  public Template getTemplate() {
    return template;
  }

  /**
   * Returns the tool tip text.
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the tool tip title.
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns true if the tooltip is anchored to the target.
   * 
   * @return true if anchored
   */
  public boolean isAnchorToTarget() {
    return anchorToTarget;
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
   * Returns true if the tip is closable.
   * 
   * @return the closable state
   */
  public boolean isCloseable() {
    return closeable;
  }

  /**
   * Returns true if the tool tip is enabled.
   * 
   * @return true for enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns true if mouse tracking is enabled.
   * 
   * @return the mouse track state
   */
  public boolean isTrackMouse() {
    return trackMouse;
  }

  /**
   * Sets the anchor position (defaults to "bottom").
   * 
   * @param anchor the anchor position (top, bottom, left, right)
   */
  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }

  /**
   * A numeric pixel value used to offset the default position of the anchor
   * arrow (defaults to 0). When the anchor position is on the top or bottom of
   * the tooltip, <code>anchorOffset</code> will be used as a horizontal offset.
   * Likewise, when the anchor position is on the left or right side,
   * <code>anchorOffset</code> will be used as a vertical offset.
   * 
   * 
   * @param anchorOffset the offset in pixels
   */
  public void setAnchorOffset(int anchorOffset) {
    this.anchorOffset = anchorOffset;
  }

  /**
   * True to anchor the tooltip to the target element, false to anchor it
   * relative to the mouse coordinates (defaults to true). 
   * 
   * @param anchorToTarget true to anchor the tooltip to the target element
   */
  public void setAnchorToTarget(boolean anchorToTarget) {
    this.anchorToTarget = anchorToTarget;
  }

  /**
   * True to automatically hide the tooltip after the mouse exits the target
   * element or after the {@link #dismissDelay} has expired if set (defaults to
   * true).
   * 
   * @param autoHide the auto hide state
   */
  public void setAutoHide(boolean autoHide) {
    this.autoHide = autoHide;
  }

  /**
   * True to render a close tool button into the tooltip header (defaults to
   * false).
   * 
   * @param closeable the closable state
   */
  public void setCloseable(boolean closeable) {
    this.closeable = closeable;
  }

  /**
   * Delay in milliseconds before the tooltip automatically hides (defaults to
   * 5000). To disable automatic hiding, set dismissDelay = 0.
   * 
   * @param dismissDelay the dismiss delay
   */
  public void setDismissDelay(int dismissDelay) {
    this.dismissDelay = dismissDelay;
  }

  /**
   * Sets whether the tool tip is enabled (defaults to true).
   * 
   * @param enabled true to enable
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Delay in milliseconds after the mouse exits the target element but before
   * the tooltip actually hides (defaults to 200). Set to 0 for the tooltip to
   * hide immediately.
   * 
   * @param hideDelay the hide delay
   */
  public void setHideDelay(int hideDelay) {
    this.hideDelay = hideDelay;
  }

  /**
   * Sets the tooltip's maximum width (defaults to 300).
   * 
   * @param maxWidth the maximum width in pixels
   */
  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
  }

  /**
   * Sets the tooltip's minimum width (defaults to 40).
   * 
   * @param minWidth the minimum width
   */
  public void setMinWidth(int minWidth) {
    this.minWidth = minWidth;
  }

  /**
   * An XY offset from the mouse position where the tooltip should be shown
   * (defaults to [15,18]).
   * 
   * @param mouseOffset the offset
   */
  public void setMouseOffset(int[] mouseOffset) {
    this.mouseOffset = mouseOffset;
  }

  /**
   * The parameters to be used when a custom a {@link #template} is specified.
   * 
   * @param params the parameters
   */
  public void setParams(Params params) {
    this.params = params;
  }

  /**
   * Delay in milliseconds before the tooltip displays after the mouse enters
   * the target element (defaults to 500).
   * 
   * @param showDelay the show delay
   */
  public void setShowDelay(int showDelay) {
    this.showDelay = showDelay;
  }

  /**
   * A optional template to be used to render the tool tip. The {@link #params}
   * will be applied to the template. If specified, {@link #title} and
   * {@link #text} will be added to the params before being applied to the
   * template.
   * 
   * @param template the template
   */
  public void setTemplate(Template template) {
    this.template = template;
  }

  /**
   * The tool tip text.
   * 
   * @param text the text
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Sets the tool tip title.
   * 
   * @param title the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * True to have the tooltip follow the mouse as it moves over the target
   * element (defaults to false).
   * 
   * @param trackMouse the track mouse state
   */
  public void setTrackMouse(boolean trackMouse) {
    this.trackMouse = trackMouse;
  }

}

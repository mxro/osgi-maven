/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;


/**
 * Drag listener.
 */
public class DragListener implements Listener<DragEvent> {

  /**
   * Fires after a drag is cancelled.
   * 
   * @param de the drag event
   */
  public void dragCancel(DragEvent de) {

  }

  /**
   * Fires after a drag ends.
   * 
   * @param de the drag event
   */
  public void dragEnd(DragEvent de) {

  }

  /**
   * Fires after the mouse leaves.
   * 
   * @param de the drag event
   */
  public void dragLeave(DragEvent de) {

  }
  
  /**
   * Fires after the mouse moves.
   * 
   * @param de the drag event
   */
  public void dragMove(DragEvent de) {

  }

  /**
   * Fires after a drag begins.
   * 
   * @param de the drag event
   */
  public void dragStart(DragEvent de) {

  }

  public void handleEvent(DragEvent de) {
    EventType type = de.getType();
    if (type == Events.DragCancel) {
      dragCancel(de);
    } else if (type == Events.DragEnd){
      dragEnd(de);
    } else if (type == Events.DragMove) {
      dragMove(de);
    } else if (type == Events.DragStart) {
      dragStart(de);
    } else if (type == Events.DragLeave) {
      dragLeave(de);
    }
  }

}

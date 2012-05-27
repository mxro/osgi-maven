/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.dnd;

/**
 * DND public constants and enumerations.
 */
public class DND {

  /**
   * Operation public enumeration which sets the operation performed by a drop
   * target.
   */
  public enum Operation {
    COPY, MOVE;
  }

  /**
   * Feedback public enumeration which sets the type of visual feedback a drop
   * target will display.
   */
  public enum Feedback {
    APPEND, INSERT, BOTH;
  }

  /**
   * TreeSource public enumeration which specifies the type of drops that are
   * allowed with a tree drop target.
   */
  public enum TreeSource {
    LEAF, NODE, BOTH;
  }

}

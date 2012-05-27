/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

/**
 * Instances of this class are notified when their containing component is
 * attached and detached.
 */
public interface ComponentAttachable {
  void doAttach();

  void doDetach();
}

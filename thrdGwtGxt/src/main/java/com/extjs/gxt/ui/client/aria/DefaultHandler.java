/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultHandler extends FocusHandler {

  private Element stepout;
  
  @Override
  public boolean canHandleKeyPress(Component component, PreviewEvent pe) {
    return true;
  }

  @Override
  public void onEnter(final Component component, final PreviewEvent pe) {
    if (!isManaged()) return;
    Timer t = new Timer() {
      @Override
      public void run() {
        stepInto(component, pe, true);
      }
    };
    t.schedule(200);
  }
  
  @Override
  public void onEscape(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    ToolTip tip = component.getToolTip();
    if (tip != null && tip.isVisible()) {
      return;
    }
    if (component.getFocusSupport().isIgnore()) {
      component.getFocusSupport().setIgnore(false);
      return;
    }
    stepOut(component);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void onTab(Component component, PreviewEvent pe) {
    if (!isManaged()) return;

    if (component.getFocusSupport().isIgnore()) {
      return;
    }
    if (pe.isShiftKey()) {
      if (focusPreviousWidget(component)) {
        pe.stopEvent();
      } else {
        Widget w = component.getParent();
        while (w != null) {
          if (w instanceof RootPanel) {
            // do nothing leave app
            return;
          } else if (w instanceof Component){
            Component c = (Component)w;
            if (c.getFocusSupport().isIgnore()) {
              w = w.getParent();
            } else {
              w = null;
              pe.stopEvent();
            }
          }
        }
      }
    } else {
      if (focusNextWidget(component)) {
        pe.stopEvent();
      } else {
        if (!(component.getParent() instanceof RootPanel)) {
          Widget p = component.getParent();
          if (p instanceof Container) {
            Container c = (Container)p;
            if (c.getItemCount() == 1) {
              if (c .getParent() instanceof RootPanel) {
                pe.stopEvent();
                onStepOutApp();
                return; 
              }
            }
          }
          pe.stopEvent();
        } else {
          pe.stopEvent();
          onStepOutApp();
        }
      }
    }
  }

  protected void onStepOutApp() {
    if (FocusManager.get().isInsertExitFocusElement()) {
      if (stepout == null) {
        stepout = Document.get().createElement("div");
        stepout.setTitle(GXT.MESSAGES.aria_leaveApplication());
        stepout.getStyle().setProperty("outline", "none");
        El.fly(stepout).setTabIndex(0);
        Element body = XDOM.getBody();
        body.appendChild(stepout);
      }
      El.fly(stepout).focus();
    }
  }

}

/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.aria;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract base class for classes that know how to handle navigation key events
 * for a given component.
 */
public abstract class FocusHandler {

  protected static boolean managed;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static boolean focusNextWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (forwardIfOverride(comp)) {
        return true;
      }
    }
    Widget p = c.getParent();

    List<Widget> widgets = null;

    NavigationHandler handler = null;
    if (p instanceof Component) {
      handler = findNavigationHandler((Component) p);
    }
    if (handler != null) {
      widgets = handler.getOrderedWidgets(p);
    } else if (p instanceof Container) {
      Container con = (Container) p;
      widgets = con.getItems();
    } else if (p instanceof HasWidgets) {
      HasWidgets hs = (HasWidgets) p;
      widgets = new ArrayList<Widget>();
      Iterator<Widget> it = hs.iterator();
      while (it.hasNext()) {
        widgets.add(it.next());
      }
    }
    if (widgets == null || widgets.size() == 1) {
      return false;
    }
    int idx = widgets.indexOf(c);
    if (idx != -1) {
      idx = idx == widgets.size() - 1 ? 0 : ++idx;
      return focusWidget(widgets.get(idx));
    }
    return false;
  }

  public static boolean focusPreviousWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (previousIfOverride(comp)) {
        return true;
      }
    }

    Widget w = findPreviousWidget(c);
    if (w != null) {
      return focusWidget(w, false);
    }
    return false;
  }

  public static boolean focusWidget(Widget w) {
    return focusWidget(w, true);
  }

  public static boolean focusWidget(Widget w, boolean forward) {
    if (w instanceof Component) {
      Component c = (Component) w;
      if (c instanceof Field<?>) {
        c.focus();
        return true;
      }
      if (c.getFocusSupport().isIgnore()) {
        if (isContainer(c)) {
          stepInto(c, null, forward);
        } else {
          if (forward) {
            return focusNextWidget(c);
          } else {
            return focusPreviousWidget(c);
          }
        }
      } else {
        c.focus();
        return true;
      }
    } else {
      El.fly(w.getElement()).focus();
      return true;
    }
    return false;
  }

  public static boolean isContainer(Widget w) {
    if (w instanceof LayoutContainer || w instanceof Panel) {
      return true;
    }
    return false;
  }

  public static boolean isManaged() {
    return FocusManager.get().isManaged();
  }

  public static void stepInto(Widget w, PreviewEvent pe, boolean forward) {
    NavigationHandler handler = findNavigationHandler((Component) w);
    if (handler != null) {
      List<Widget> widgets = handler.getOrderedWidgets(w);
      if (widgets.size() > 0) {
        if (pe != null) pe.stopEvent();
        focusWidget(widgets.get(0));
        return;
      }
    }
    if (w instanceof ContentPanel) {
      if (pe != null) pe.stopEvent();
      ContentPanel panel = (ContentPanel) w;
      if (panel.getTopComponent() != null) {
        focusWidget(panel.getTopComponent());
        return;
      }
    }
    if (w instanceof LayoutContainer || w instanceof HtmlContainer) {
      if (pe != null) pe.stopEvent();
      Container<?> c = (Container<?>) w;
      if (c.getItemCount() > 0) {
        focusWidget(forward ? c.getItem(0) : c.getItem(c.getItemCount() - 1));
      }
    } else if (w instanceof ComplexPanel) {
      if (pe != null) pe.stopEvent();
      ComplexPanel panel = (ComplexPanel) w;
      if (panel.getWidgetCount() > 0) {
        focusWidget(panel.getWidget(0));
      }
    }
  }

  protected static Widget findForwardOverride(Component comp) {
    if (comp.getFocusSupport().getNextId() != null) {
      String id = comp.getFocusSupport().getNextId();
      Component p = ComponentManager.get().get(id);
      if (p != null) {
        return p;
      }
    }
    if (comp.getFocusSupport().hasListeners(FocusManager.TabNext)) {
      if (comp.getFocusSupport().fireEvent(FocusManager.TabNext)) {
        return comp;
      }
    }
    return null;
  }

  protected static NavigationHandler findNavigationHandler(Component comp) {
    return FocusManager.get().findNavigationHandler(comp);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected static Widget findNextWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (findForwardOverride(comp) != null) {
        return findForwardOverride(comp);
      }
    }
    Widget p = c.getParent();

    List<Widget> widgets = null;

    NavigationHandler handler = null;
    if (p instanceof Component) {
      handler = findNavigationHandler((Component) p);
    }
    if (handler != null) {
      widgets = handler.getOrderedWidgets(p);
    } else if (p instanceof Container) {
      Container con = (Container) p;
      widgets = con.getItems();
    } else if (p instanceof HasWidgets) {
      HasWidgets hs = (HasWidgets) p;
      widgets = new ArrayList<Widget>();
      Iterator<Widget> it = hs.iterator();
      while (it.hasNext()) {
        widgets.add(it.next());
      }
    }
    if (widgets == null) {
      return null;
    }
    int idx = widgets.indexOf(c);
    if (idx != -1) {
      Widget w = null;
      if (idx == widgets.size() - 1 && widgets.size() > 1) {
        w = widgets.get(0);
      } else if (idx != widgets.size() - 1) {
        w = widgets.get(idx + 1);

      }
      if (isIgnore(w)) {
        return findNextWidget(w);
      }
      return w;
    }
    return null;
  }

  protected static Widget findPreviousOverride(Component comp) {
    if (comp.getFocusSupport().getPreviousId() != null) {
      String id = comp.getFocusSupport().getPreviousId();
      if ("parent-previous".equals(id)) {
        Widget p = comp.getParent();
        return p;
      }
      Component p = ComponentManager.get().get(id);
      if (p != null) {
        return p;
      }
    }
    if (comp.getFocusSupport().hasListeners(FocusManager.TabPrevious)) {
      if (comp.getFocusSupport().fireEvent(FocusManager.TabPrevious)) {
        return comp;
      }
    }
    return null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected static Widget findPreviousWidget(Widget c) {
    if (c instanceof Component) {
      Component comp = (Component) c;
      if (findPreviousOverride(comp) != null) {
        return findPreviousOverride(comp);
      }
    }
    Widget p = c.getParent();

    List<Widget> widgets = null;

    NavigationHandler handler = p instanceof Component ? findNavigationHandler((Component) p) : null;
    if (handler != null) {
      widgets = handler.getOrderedWidgets(p);
    } else if (p instanceof Container) {
      Container con = (Container) p;
      widgets = con.getItems();
    } else if (p instanceof HasWidgets) {
      HasWidgets hs = (HasWidgets) p;
      widgets = new ArrayList<Widget>();
      Iterator<Widget> it = hs.iterator();
      while (it.hasNext()) {
        widgets.add(it.next());
      }
    }
    if (widgets == null) {
      return null;
    }
    int size = widgets.size();
    int idx = widgets.indexOf(c);
    if (idx != -1) {
      if (idx > 0) {
        return widgets.get(idx - 1);
      } else if (idx != 0 && size > 1) {
        return widgets.get(size - 1);
      } else if (idx == 0 && size > 1) {
        return widgets.get(size - 1);
      }
    }
    return null;
  }

  protected static boolean forwardIfOverride(Component comp) {
    Widget c = findForwardOverride(comp);
    if (c != null) {
      focusWidget(c);
      return true;
    }
    return false;
  }

  protected static boolean isIgnore(Widget w) {
    if (w instanceof Component) {
      Component c = (Component) w;
      if (c.getFocusSupport().isIgnore()) {
        return true;
      }

    }
    return false;
  }

  protected static boolean previousIfOverride(Component comp) {
    Widget c = findPreviousOverride(comp);
    if (c != null) {
      focusWidget(c, false);
      return true;
    }
    return false;
  }

  /**
   * Return true if this handler can handle key events for the given component.
   * 
   * @param component the target component
   * @param pe the preview event
   * @return true if can handle
   */
  public abstract boolean canHandleKeyPress(Component component, PreviewEvent pe);

  public void onEnter(Component component, PreviewEvent pe) {

  }

  public void onEscape(Component component, PreviewEvent pe) {
    if (!isManaged()) return;
    stepOut(component);
  }

  public void onLeft(Component component, PreviewEvent pe) {

  }

  public void onRight(Component component, PreviewEvent pe) {

  }

  public void onTab(Component component, PreviewEvent pe) {

  }

  protected int firstActive(Container<?> c) {
    for (int i = 0; i < c.getItemCount(); i++) {
      Component comp = c.getItem(i);
      if (!comp.isEnabled() || comp.getFocusSupport().isIgnore()) {
        continue;
      }
      return i;
    }
    return -1;
  }

  protected int lastActive(Container<?> c) {
    for (int i = c.getItemCount() - 1; i >= 0; i--) {
      Component comp = c.getItem(i);
      if (!comp.isEnabled() || comp.getFocusSupport().isIgnore()) {
        continue;
      }
      return i;
    }
    return -1;
  }

  protected void stepOut(Widget w) {
    Widget p = w.getParent();
    if (p != null) {
      if (p instanceof TabItem) {
        ((TabItem) p).getTabPanel().focus();
      } else if (p instanceof Component) {
        Component c = (Component) p;
        while (c.getFocusSupport().isIgnore()) {
          p = c.getParent();
          if (p != null) {
            if (p instanceof Component) {
              c = (Component) p;
            } else {
              El.fly(p.getElement()).focus();
              return;
            }
          }
        }
        focusWidget(c);
      } else {
        El.fly(p.getElement()).focus();
        FocusFrame.get().unframe();
      }
    }
  }

}

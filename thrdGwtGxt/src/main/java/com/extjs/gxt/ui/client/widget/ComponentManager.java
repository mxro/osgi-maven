/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.ComponentManagerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;

/**
 * Provides a registry of all attached components. Only components currently
 * part of the page (DOM) are part of the registry as all components register
 * when attached, and unregister when detached.
 * 
 * <pre>
 * Collection<Component> components = ComponentManager.get().getAll();
 * Component comp = ComponentManager.get().get("foo");
 * for (Component c : components) {
 * }
 * </pre>
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>Register</b> : ComponentManagerEvent(source, component)<br>
 * <div>Fires after the component is registered.</div>
 * <ul>
 * <li>source : this</li>
 * <li>component : the registered component</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Unregister</b> : ComponentManagerEvent(source, component)<br>
 * <div>Fires after the component is unregistered.</div>
 * <ul>
 * <li>source : this</li>
 * <li>component : the registered component</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class ComponentManager extends BaseObservable {

  private static ComponentManager instance = new ComponentManager();

  /**
   * Returns the component manager instance.
   * 
   * @return the component manager
   */
  public static ComponentManager get() {
    return instance;
  }

  private Map<String, Component> map;

  private ComponentManager() {
    map = new FastMap<Component>();
  }

  /**
   * Attempts to find a component.
   * 
   * @param target the element or inner element of the component
   * @return the matching component or null if no match
   */
  @SuppressWarnings("unchecked")
  public <X extends Component> X find(Element target) {
    return (X) find(target, null);
  }

  /**
   * Attempts to find a component.
   * 
   * @param target the element or inner element of the component
   * @param clazz the class the component should have
   * @return the matching component or null if no match
   */
  @SuppressWarnings("unchecked")
  public <X extends Component> X find(Element target, Class<X> clazz) {
    while (target != null) {
      Component c = map.get(target.getId());
      if (c != null && (clazz == null || c.getClass().equals(clazz))) {
        return (X) c;
      } else {
        target = (Element) target.getParentElement();
      }

    }
    return null;
  }

  /**
   * Returns all component by class.
   * 
   * @param clazz the class to match
   * @return the list of matching components
   */
  @SuppressWarnings("unchecked")
  public <X extends Component> List<X> get(Class<?> clazz) {
    List<X> temp = new ArrayList<X>();
    for (Component c : map.values()) {
      if (c.getClass() == clazz) {
        temp.add((X) c);
      }
    }
    return temp;
  }

  /**
   * Returns a component by id.
   * 
   * @param id the component id
   * @return the component or null if no match
   */
  public Component get(String id) {
    return map.get(id);
  }

  /**
   * Returns a collection of all registered components.
   * 
   * @return the collection of components
   */
  public Collection<Component> getAll() {
    return map.values();
  }

  /**
   * Registers a component. Should never need to be called directly.
   * 
   * @param component the component to register
   */
  void register(Component component) {
    map.put(component.getId(), component);
    fireEvent(Events.Register, new ComponentManagerEvent(this, component));
  }

  /**
   * Unregisters a component. Should never need to be called directly.
   * 
   * @param component the component to unregister
   */
  void unregister(Component component) {
    map.remove(component.getId());
    fireEvent(Events.Unregister, new ComponentManagerEvent(this, component));
  }

}

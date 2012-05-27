/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.user.client.DOM;

/**
 * ARIA component support.
 */
public class AriaSupport {

  String labelledBy, label;
  String role;
  Map<String, String> states = new HashMap<String, String>();
  boolean ignore;
  String describedBy;
  String description;
  boolean presentation;
  Component c;

  AriaSupport(Component c) {
    this.c = c;
  }

  /**
   * Returns the ARIA described by id.
   * 
   * @return the ARIA described by id
   */
  public String getDescribedBy() {
    return describedBy;
  }

  /**
   * Returns the description.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the ARIA label text.
   * 
   * @return the ARIA label text
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the ARIA labelled by id.
   * 
   * @return the ARIA labelled by id.
   */
  public String getLabelledBy() {
    return labelledBy;
  }

  /**
   * Returns the ARIA role.
   * 
   * @return the ARIA role
   */
  public String getRole() {
    return role;
  }

  /**
   * Returns the ARIA state.
   * 
   * @param stateName the state name
   * @return the state value
   */
  public String getState(String stateName) {
    return states.get(stateName);
  }

  /**
   * Returns the ARIA states.
   * 
   * @return the ARIA states
   */
  public Map<String, String> getStates() {
    return states;
  }



  /**
   * Returns true if the component is a presentation element.
   * 
   * @return true for presentation
   */
  public boolean isPresentation() {
    return presentation;
  }

  /**
   * Sets the ARIA described by attribute on the component.
   * 
   * @param describedBy the id of the element with the label
   */
  public void setDescribedBy(String describedBy) {
    this.describedBy = describedBy;
    if (c.isRendered()) {
      c.setAriaState("aria-describedby", describedBy);
    }
  }
  
  /**
   * Sets the ARIA description. This method creates a hidden div, assigns it an
   * id and then sets the aria-describedby value. Should not be called if
   * {@link #setDescribedBy(String)} is used. This method is useful when there
   * is not an existing element to be used for the description.
   * 
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
    if (c.isRendered()) {
      String id = XDOM.getUniqueId();
      El div = new El(DOM.createDiv());
      div.makePositionable(true);
      div.setLeft(-10000);
      div.setTop(-10000);
      div.setId(id);
      div.setInnerHtml(description);
      XDOM.getBody().appendChild(div.dom);
      c.setAriaState("aria-describedby", id);
    }
  }



  /**
   * Sets the ARIA label attribute on the component.
   * 
   * @param label the label
   */
  public void setLabel(String label) {
    this.label = label;
    if (GXT.isAriaEnabled() && c.isRendered()) {
      c.setAriaState("aria-label", label);
    }
  }

  /**
   * Sets the ARIA labelled by attribute on the component.
   * 
   * @param labelledBy the id of the element with the label
   */
  public void setLabelledBy(String labelledBy) {
    this.labelledBy = labelledBy;
    if (c.isRendered()) {
      c.setAriaState("aria-labelledby", labelledBy);
    }
  }

  /**
   * True to mark this component as a ARIA presentation element.
   * 
   * @param presentation true for presentation
   */
  public void setPresentation(boolean presentation) {
    this.presentation = presentation;
    c.getFocusSupport().setIgnore(true);
    if (c.isRendered() && presentation) {
      c.setAriaRole("presentation");
    }
  }

  /**
   * Sets the ARIA role.
   * 
   * @param role the ARIA role
   */
  public void setRole(String role) {
    this.role = role;
    if (c.isRendered()) {
      c.setAriaRole(role);
    }
  }

  /**
   * Sets the ARIA state.
   * 
   * @param stateName the state name
   * @param value the state value
   */
  public void setState(String stateName, String value) {
    states.put(stateName, value);
    if (c.isRendered()) {
      c.setAriaState(stateName, value);
    }
  }

}

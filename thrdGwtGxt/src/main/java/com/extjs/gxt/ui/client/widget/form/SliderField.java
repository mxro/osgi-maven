/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.form;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Slider;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Field which wraps a <code>Slider</code>.
 */
public class SliderField extends Field<Integer> {
  private Slider slider;
  protected El hidden;
  protected Listener<SliderEvent> listener;

  /**
   * Creates a new slider field.
   * 
   * @param slider the slider to be wrapped.
   */
  public SliderField(Slider slider) {
    super();
    setSlider(slider);
  }

  /**
   * Returns the slider component.
   * 
   * @return the slider.
   */
  public Slider getSlider() {
    return slider;
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    super.setReadOnly(readOnly);
    if (readOnly) {
      slider.disable();
    } else if (!readOnly && isEnabled()) {
      slider.enable();
    }
  }

  /**
   * Sets the slider component.
   * 
   * @param slider the slider
   */
  public void setSlider(Slider slider) {
    assertPreRender();
    if (listener == null) {
      listener = new Listener<SliderEvent>() {
        public void handleEvent(SliderEvent be) {
          if (rendered) {
            updateHiddenField();
          }
        }
      };
    }
    if (this.slider != slider) {
      if (this.slider != null) {
        ComponentHelper.removeFromParent(this.slider);
        this.slider.removeListener(Events.Change, listener);
      }
      this.slider = slider;
      slider.getFocusSupport().setIgnore(true);
      ComponentHelper.setParent(this, slider);
      slider.addListener(Events.Change, listener);
    }
  }

  @Override
  public void setValue(Integer value) {
    if (value == null) {
      int min = slider.getMinValue();
      slider.setValue(min);
      super.setValue(min);
    } else {
      slider.setValue(value);
      super.setValue(value);
    }
  }

  @Override
  protected void afterRender() {
    super.afterRender();
    updateHiddenField();

    El elem = findLabelElement();
    if (elem != null) {
      elem.dom.setAttribute("for", slider.getId());
    }
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(slider);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(slider);
  }

  @Override
  protected El getFocusEl() {
    return slider.getFocusEl();
  }

  @Override
  protected El getInputEl() {
    return hidden;
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    slider.disable();
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    if (!readOnly) {
      slider.enable();
    }
  }

  @Override
  protected void onRender(Element parent, int index) {
    setElement(DOM.createDiv(), parent, index);

    slider.render(getElement());
    hidden = new El((Element) Document.get().createHiddenInputElement().cast());;
    getElement().appendChild(hidden.dom);

    if (GXT.isIE) {
      el().makePositionable();
    }

    super.onRender(parent, index);
  }

  @Override
  protected void onResize(int width, int height) {
    if (rendered) {
      if (slider.isVertical()) {
        slider.setHeight(height);
      } else {
        slider.setWidth(width);
      }
    }
    super.onResize(width, height);
  }

  protected void updateHiddenField() {
    if (rendered) {
      hidden.setValue(slider.getValue() + "");
    }
  }

}

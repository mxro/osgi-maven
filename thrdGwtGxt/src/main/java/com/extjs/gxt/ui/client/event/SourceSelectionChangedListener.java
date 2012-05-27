/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.event;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

/**
 * A <code>SelectionChangedListener</code> that ignores its own selection
 * events. Useful when registering the provider with the
 * <code>SelectionService</code>.
 */
@SuppressWarnings("deprecation")
public class SourceSelectionChangedListener extends SelectionChangedListener<ModelData> {
  private SelectionProvider<? extends ModelData> provider;

  public SourceSelectionChangedListener(SelectionProvider<? extends ModelData> provider) {
    this.provider = provider;
  }

  public void selectionChanged(final SelectionChangedEvent<ModelData> event) {
    SelectionProvider<ModelData> eventProvider = event.getSelectionProvider();
    if (eventProvider != provider) {
      if (provider.getSelection().get(0) != eventProvider.getSelection().get(0)) {
        DeferredCommand.addCommand(new Command() {
          @SuppressWarnings({"unchecked", "rawtypes"})
          public void execute() {
            provider.setSelection((List)event.getSelection());
          }
        });

      }
    }
  }
}

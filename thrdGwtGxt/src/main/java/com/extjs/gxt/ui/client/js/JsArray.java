/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.js;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wraps a native javascript array.
 */
public class JsArray implements JsWrapper {

  public static native JavaScriptObject eval(String code) /*-{
    var x = eval(code);
    return x[0];
  }-*/;

  /**
   * The wrapped javascript object.
   */
  protected JavaScriptObject jsArray;

  /**
   * Creates a new instance.
   */
  public JsArray() {
    jsArray = create();
  }

  /**
   * Adds a boolean value to the array.
   * 
   * @param value the value to add
   */
  public native void add(boolean value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a byte value to the array.
   * 
   * @param value the value to add
   */
  public native void add(byte value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a char value to the array.
   * 
   * @param value the value to add
   */
  public native void add(char value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a double value to the array.
   * 
   * @param value the value to add
   */
  public native void add(double value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a float value to the array.
   * 
   * @param value the value to add
   */
  public native void add(float value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a int value to the array.
   * 
   * @param value the value to add
   */
  public native void add(int value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a native javascript object to the array.
   * 
   * @param object the object to add
   */
  public native void add(JavaScriptObject object) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = object;
  }-*/;

  public void add(Object value) {
    if (value instanceof Boolean) {
      add((boolean) (Boolean) value);
    } else if (value instanceof Long) {
      add((double) (Long) value);
    } else if (value instanceof Integer) {
      add((int) (Integer) value);
    } else if (value instanceof Short) {
      add((short) (Short) value);
    } else if (value instanceof Double) {
      add((double) (Double) value);
    } else if (value instanceof Float) {
      add((float) (Float) value);
    } else if (value instanceof Character) {
      add((char) (Character) value);
    } else if (value instanceof Byte) {
      add((byte) (Byte) value);
    } else if (value instanceof String) {
      add((String) value);
    } else {
      addObjectInternal(value);
    }
  }

  /**
   * Adds a short value to the array.
   * 
   * @param value the value to add
   */
  public native void add(short value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Adds a string value to the array.
   * 
   * @param value the value to add
   */
  public native void add(String value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

  /**
   * Returns a property value.
   * 
   * @param index the index
   * @return the value
   */
  public native Object get(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public native boolean getBoolean(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public native byte getByte(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public native char getChar(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public native double getDouble(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public native float getFloat(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  /**
   * Returns a property value.
   * 
   * @param index the index
   * @return the value
   */
  public native int getInt(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  public JavaScriptObject getJsObject() {
    return jsArray;
  }

  public native short getShort(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  /**
   * Returns a property value.
   * 
   * @param index the index
   * @return the value
   */
  public native String getString(int index) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray
    return js[index];
  }-*/;

  /**
   * Returns the size of the array.
   * 
   * @return the size
   */
  public native int size() /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    return js.length;
  }-*/;

  protected native JavaScriptObject create() /*-{
    return new Array();
  }-*/;

  /**
   * Adds a object to the array.
   * 
   * @param value the object to add
   */
  private native void addObjectInternal(Object value) /*-{
    var js = this.@com.extjs.gxt.ui.client.js.JsArray::jsArray;
    js[js.length] = value;
  }-*/;

}

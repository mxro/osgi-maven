/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.util;

import java.util.Iterator;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Formatting functions.
 */
public class Format {

  /**
   * Camel cases the given string.
   * 
   * @param s the string
   * @return the camel value
   */
  public static native String camelize(String s)/*-{
    return (s.match(/\-/gi) ? s.toLowerCase().replace(/\-(\w)/gi, function(a, c){return c.toUpperCase();}) : s);
  }-*/;

  /**
   * Capitalizes the first character and lower cases the remaining characters.
   * 
   * @param value the value to capitalize
   * @return the capitalized value
   */
  public static String capitalize(String value) {
    return value == null ? value : value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
  }

  /**
   * Provides the standard currency format for the default locale.
   * 
   * @param value the value
   * @return the currency value
   */
  public static String currency(double value) {
    return NumberFormat.getCurrencyFormat().format(value);
  }

  /**
   * Provides the standard decimal format for the default locale.
   * 
   * @param value the value
   * @return the value
   */
  public static String decimal(double value) {
    return NumberFormat.getDecimalFormat().format(value);
  }

  /**
   * Truncate a string and add an ellipsis ('...') to the end if it exceeds the
   * specified length.
   * 
   * @param value the string to truncate
   * @param len the maximum length to allow before truncating
   * @return the converted text
   */
  public static String ellipse(String value, int len) {
    if (value != null && value.length() > len) {
      return value.substring(0, len - 3) + "...";
    }
    return value;
  }

  public static native String escape(String s)/*-{
    return s.replace(/('|\\)/g, "\\$1");
  }-*/;

  public static String fileSize(int size) {
    if (size < 1024) {
      return size + " bytes";
    } else if (size < 1048576) {
      return (Math.round(((size * 10) / 1024)) / 10) + " KB";
    } else {
      return (Math.round(((size * 10) / 1048576)) / 10) + " MB";
    }
  }

  /**
   * Convert certain characters (&, <, >, and ') from their HTML character
   * equivalents.
   * 
   * @param value the value
   * @return the decoded value
   */
  public static String htmlDecode(String value) {
    return value == null ? value : value.replace("&amp;", "&").replace("&gt;", ">").replace("&lt;", "<").replace(
        "&quot;", "\"");
  }

  /**
   * Convert certain characters (&, <, >, and ') to their HTML character equivalents for literal display in web pages.
   * 
   * @param value the value
   * @return the encoded value
   */
  public static String htmlEncode(String value) {
    return value == null ? value : value.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;").replace("\"",
        "&quot;");
  }

  public static native String hyphenize(String name) /*-{
    return name.replace(/([A-Z])/g, "-$1" ).toLowerCase();
  }-*/;

  /**
   * Formats the number.
   * 
   * @param value the number
   * @param format the format using the {@link DateTimeFormat} syntax.
   * @return the formatted string
   */
  public static String number(double value, String format) {
    return NumberFormat.getFormat(format).format(value);
  }

  /**
   * Provides the standard scientific format for the default locale.
   * 
   * @param value the value
   * @return the value
   */
  public static String scientific(double value) {
    return NumberFormat.getScientificFormat().format(value);
  }

  public static native String stripScripts(String v) /*-{
    return !v ? v : String(v).replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "");
  }-*/;

  public static native String stripTags(String v) /*-{
    return !v ? v : String(v).replace(/<\/?[^>]+>/gi, "");
  }-*/;

  /**
   * Substitutes the indexed parameters.
   * 
   * @param text the text
   * @param param the parameter
   * @return the new text
   */
  public static String substitute(String text, int param) {
    return substitute(text, safeRegexReplacement("" + param));
  }

  /**
   * Substitutes the named parameters. The passed keys and values must be
   * Strings.
   * 
   * @param text the text
   * @param params the parameters
   * @return the new text
   */
  public static String substitute(String text, Map<String, Object> params) {
    Iterator<String> it = params.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      text = text.replaceAll("\\{" + key + "}", safeRegexReplacement(params.get(key).toString()));
    }
    return text;
  }

  /**
   * Substitutes the indexed parameters.
   * 
   * @param text the text
   * @param params the parameters
   * @return the new text
   */
  public static String substitute(String text, Object... params) {
    for (int i = 0; i < params.length; i++) {
      Object p = params[i];
      if (p == null) {
        p = "";
      }
      text = text.replaceAll("\\{" + i + "}", safeRegexReplacement(p.toString()));
    }
    return text;
  }

  /**
   * Substitutes the parameters.
   * 
   * @param text the text
   * @param params the parameters
   * @return the new text
   */
  public static String substitute(String text, Params params) {
    if (params.isMap()) {
      return substitute(text, params.getMap());
    } else if (params.isList()) {
      return substitute(text, params.getList().toArray());
    }
    return text;

  }

  /**
   * Substitutes the named parameters. The passed keys and values must be
   * Strings.
   * 
   * @param text the text
   * @param keys the parameter names
   * @param params the parameter values
   * @return the new text
   */
  public static String substitute(String text, String[] keys, Map<String, Object> params) {
    for (int i = 0; i < keys.length; i++) {
      text = text.replaceAll("\\{" + keys[i] + "}", safeRegexReplacement((String) params.get(keys[i])));
    }
    return text;
  }

  /**
   * Replace any \ or $ characters in the replacement string with an escaped \\
   * or \$.
   * 
   * @param replacement the regular expression replacement text
   * @return null if replacement is null or the result of escaping all \ and $
   *         characters
   */
  private static String safeRegexReplacement(String replacement) {
    if (replacement == null) {
      return replacement;
    }

    return replacement.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
  }
}

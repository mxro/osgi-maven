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
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ColorPaletteEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;

/**
 * Basic color component.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeSelect</b> : ColorPaletteEvent(colorPalette, color)<br>
 * <div>Fires before a color selected.</div>
 * <ul>
 * <li>colorPalette : this</li>
 * <li>color : the selected color</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Select</b> : ColorPaletteEvent(colorPalette, color)<br>
 * <div>Fires when a color is selected.</div>
 * <ul>
 * <li>colorPalette : this</li>
 * <li>color : the selected color</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 */
public class ColorPalette extends Component {

  private static Map<String, String> colorNames = new HashMap<String, String>();
  static {
    colorNames.put("FAEBD7", "AntiqueWhite");
    colorNames.put("00FFFF", "Aqua");
    colorNames.put("7FFFD4", "Aquamarine");
    colorNames.put("F0FFFF", "Azure");
    colorNames.put("F5F5DC", "Beige");
    colorNames.put("FFE4C4", "Bisque");
    colorNames.put("000000", "Black");
    colorNames.put("FFEBCD", "BlanchedAlmond");
    colorNames.put("0000FF", "Blue");
    colorNames.put("8A2BE2", "BlueViolet");
    colorNames.put("A52A2A", "Brown");
    colorNames.put("DEB887", "BurlyWood");
    colorNames.put("5F9EA0", "CadetBlue");
    colorNames.put("7FFF00", "Chartreuse");
    colorNames.put("D2691E", "Chocolate");
    colorNames.put("FF7F50", "Coral");
    colorNames.put("6495ED", "CornflowerBlue");
    colorNames.put("FFF8DC", "Cornsilk");
    colorNames.put("DC143C", "Crimson");
    colorNames.put("00FFFF", "Cyan");
    colorNames.put("00008B", "DarkBlue");
    colorNames.put("008B8B", "DarkCyan");
    colorNames.put("B8860B", "DarkGoldenRod");
    colorNames.put("A9A9A9", "DarkGray");
    colorNames.put("006400", "DarkGreen");
    colorNames.put("BDB76B", "DarkKhaki");
    colorNames.put("8B008B", "DarkMagenta");
    colorNames.put("556B2F", "DarkOliveGreen");
    colorNames.put("FF8C00", "Darkorange");
    colorNames.put("9932CC", "DarkOrchid");
    colorNames.put("8B0000", "DarkRed");
    colorNames.put("E9967A", "DarkSalmon");
    colorNames.put("8FBC8F", "DarkSeaGreen");
    colorNames.put("483D8B", "DarkSlateBlue");
    colorNames.put("2F4F4F", "DarkSlateGray");
    colorNames.put("00CED1", "DarkTurquoise");
    colorNames.put("9400D3", "DarkViolet");
    colorNames.put("FF1493", "DeepPink");
    colorNames.put("00BFFF", "DeepSkyBlue");
    colorNames.put("696969", "DimGray");
    colorNames.put("1E90FF", "DodgerBlue");
    colorNames.put("B22222", "FireBrick");
    colorNames.put("FFFAF0", "FloralWhite");
    colorNames.put("228B22", "ForestGreen");
    colorNames.put("FF00FF", "Fuchsia");
    colorNames.put("DCDCDC", "Gainsboro");
    colorNames.put("F8F8FF", "GhostWhite");
    colorNames.put("FFD700", "Gold");
    colorNames.put("DAA520", "GoldenRod");
    colorNames.put("808080", "Gray");
    colorNames.put("008000", "Green");
    colorNames.put("ADFF2F", "GreenYellow");
    colorNames.put("F0FFF0", "HoneyDew");
    colorNames.put("FF69B4", "HotPink");
    colorNames.put("CD5C5C", "IndianRed ");
    colorNames.put("4B0082", "Indigo  ");
    colorNames.put("FFFFF0", "Ivory");
    colorNames.put("F0E68C", "Khaki");
    colorNames.put("E6E6FA", "Lavender");
    colorNames.put("FFF0F5", "LavenderBlush");
    colorNames.put("7CFC00", "LawnGreen");
    colorNames.put("FFFACD", "LemonChiffon");
    colorNames.put("ADD8E6", "LightBlue");
    colorNames.put("F08080", "LightCoral");
    colorNames.put("E0FFFF", "LightCyan");
    colorNames.put("FAFAD2", "LightGoldenRodYellow");
    colorNames.put("D3D3D3", "LightGrey");
    colorNames.put("90EE90", "LightGreen");
    colorNames.put("FFB6C1", "LightPink");
    colorNames.put("FFA07A", "LightSalmon");
    colorNames.put("20B2AA", "LightSeaGreen");
    colorNames.put("87CEFA", "LightSkyBlue");
    colorNames.put("778899", "LightSlateGray");
    colorNames.put("B0C4DE", "LightSteelBlue");
    colorNames.put("FFFFE0", "LightYellow");
    colorNames.put("00FF00", "Lime");
    colorNames.put("32CD32", "LimeGreen");
    colorNames.put("FAF0E6", "Linen");
    colorNames.put("FF00FF", "Magenta");
    colorNames.put("800000", "Maroon");
    colorNames.put("66CDAA", "MediumAquaMarine");
    colorNames.put("0000CD", "MediumBlue");
    colorNames.put("BA55D3", "MediumOrchid");
    colorNames.put("9370D8", "MediumPurple");
    colorNames.put("3CB371", "MediumSeaGreen");
    colorNames.put("7B68EE", "MediumSlateBlue");
    colorNames.put("00FA9A", "MediumSpringGreen");
    colorNames.put("48D1CC", "MediumTurquoise");
    colorNames.put("C71585", "MediumVioletRed");
    colorNames.put("191970", "MidnightBlue");
    colorNames.put("F5FFFA", "MintCream");
    colorNames.put("FFE4E1", "MistyRose");
    colorNames.put("FFE4B5", "Moccasin");
    colorNames.put("FFDEAD", "NavajoWhite");
    colorNames.put("000080", "Navy");
    colorNames.put("FDF5E6", "OldLace");
    colorNames.put("808000", "Olive");
    colorNames.put("6B8E23", "OliveDrab");
    colorNames.put("FFA500", "Orange");
    colorNames.put("FF4500", "OrangeRed");
    colorNames.put("DA70D6", "Orchid");
    colorNames.put("EEE8AA", "PaleGoldenRod");
    colorNames.put("98FB98", "PaleGreen");
    colorNames.put("AFEEEE", "PaleTurquoise");
    colorNames.put("D87093", "PaleVioletRed");
    colorNames.put("FFEFD5", "PapayaWhip");
    colorNames.put("FFDAB9", "PeachPuff");
    colorNames.put("CD853F", "Peru");
    colorNames.put("FFC0CB", "Pink");
    colorNames.put("DDA0DD", "Plum");
    colorNames.put("B0E0E6", "PowderBlue");
    colorNames.put("800080", "Purple");
    colorNames.put("FF0000", "Red");
    colorNames.put("BC8F8F", "RosyBrown");
    colorNames.put("4169E1", "RoyalBlue");
    colorNames.put("8B4513", "SaddleBrown");
    colorNames.put("FA8072", "Salmon");
    colorNames.put("F4A460", "SandyBrown");
    colorNames.put("2E8B57", "SeaGreen");
    colorNames.put("FFF5EE", "SeaShell");
    colorNames.put("A0522D", "Sienna");
    colorNames.put("C0C0C0", "Silver");
    colorNames.put("87CEEB", "SkyBlue");
    colorNames.put("6A5ACD", "SlateBlue");
    colorNames.put("708090", "SlateGray");
    colorNames.put("FFFAFA", "Snow");
    colorNames.put("00FF7F", "SpringGreen");
    colorNames.put("4682B4", "SteelBlue");
    colorNames.put("D2B48C", "Tan");
    colorNames.put("008080", "Teal");
    colorNames.put("D8BFD8", "Thistle");
    colorNames.put("FF6347", "Tomato");
    colorNames.put("40E0D0", "Turquoise");
    colorNames.put("EE82EE", "Violet");
    colorNames.put("F5DEB3", "Wheat");
    colorNames.put("FFFFFF", "White");
    colorNames.put("F5F5F5", "WhiteSmoke");
    colorNames.put("FFFF00", "Yellow");
    colorNames.put("9ACD32", "YellowGreen");
  }
  private boolean allowReselect;
  private String[] colors = new String[] {
      "000000", "993300", "333300", "003300", "003366", "000080", "333399", "333333", "800000", "FF6600", "808000",
      "008000", "008080", "0000FF", "666699", "808080", "FF0000", "FF9900", "99CC00", "339966", "33CCCC", "3366FF",
      "800080", "969696", "FF00FF", "FFCC00", "FFFF00", "00FF00", "00FFFF", "00CCFF", "993366", "C0C0C0", "FF99CC",
      "FFCC99", "FFFF99", "CCFFCC", "CCFFFF", "99CCFF", "CC99FF", "FFFFFF"};
  private int columnCount = 8;

  private NodeList<Element> elements;
  private int rowCount;
  private XTemplate template;

  private String value;

  /**
   * Creates a new color palette.
   */
  public ColorPalette() {
    baseStyle = "x-color-palette";
  }

  /**
   * Returns the colors.
   * 
   * @return the colors
   */
  public String[] getColors() {
    return colors;
  }

  /**
   * Returns the column count.
   * 
   * @return the columnCount
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Returns the xtemplate.
   * 
   * @return the template
   */
  public XTemplate getTemplate() {
    return template;
  }

  /**
   * Returns the current selected color.
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns true if re-selections are allowed.
   * 
   * @return the true if re-selections are allowed.
   */
  public boolean isAllowReselect() {
    return allowReselect;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    switch (ce.getEventTypeInt()) {
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONMOUSEOVER:
        onMouseOver(ce);
        break;
      case Event.ONMOUSEOUT:
        onMouseOut(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
      case Event.ONBLUR:
        onBlur(ce);
        break;
    }
  }

  /**
   * Selects the color.
   * 
   * @param color the color
   */
  public void select(String color) {
    select(color, false);
  }

  /**
   * Selects the color.
   * 
   * @param color the color
   * @param suppressEvent true to suppress the select event
   */
  public void select(String color, boolean suppressEvent) {
    color = color.replace("#", "");
    ColorPaletteEvent ce = new ColorPaletteEvent(this);
    ce.setColor(color);

    if (!suppressEvent) {
      if (!fireEvent(Events.BeforeSelect, ce)) {
        return;
      }
    }

    for (int i = 0; i < elements.getLength(); i++) {
      El el = El.fly(elements.getItem(i));
      el.removeStyleName("x-color-palette-sel");
      el.removeStyleName("x-color-palette-hover");
    }

    if (!color.equals(value) || allowReselect) {
      El a = el().child("a.color-" + color);
      a.addStyleName("x-color-palette-sel");
      getElement().setAttribute("aria-activedescendant", a.getId());

      value = color;
      if (!suppressEvent) {
        fireEvent(Events.Select, ce);
      }
    }
  }

  /**
   * True to fire a select event if the current selected value is selected again
   * (default to false).
   * 
   * @param allowReselect true to fire select events if re-selected
   */
  public void setAllowReselect(boolean allowReselect) {
    this.allowReselect = allowReselect;
  }

  /**
   * Sets the colors for the palette.
   * 
   * @param colors the colors to set
   */
  public void setColors(String[] colors) {
    this.colors = colors;
  }

  /**
   * Sets the column count for the palette.
   * 
   * @param columnCount the columnCount to set
   */
  public void setColumnCount(int columnCount) {
    assertPreRender();
    this.columnCount = columnCount;
  }

  /**
   * Optionally, sets the xtemplate to be used to render the component.
   * 
   * @param template the xtemplate
   */
  public void setTemplate(XTemplate template) {
    this.template = template;
  }

  /**
   * Sets the selected color.
   * 
   * @param value the value to set
   */
  public void setValue(String value) {
    value = value.replace("#", "");
    if (rendered) {
      select(value);
    }
    this.value = value;
  }

  @Override
  protected void afterRender() {
    super.afterRender();
    if (value != null) {
      String s = getValue();
      value = null;
      select(s);
    }
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new ColorPaletteEvent(this, event);
  }

  protected void onBlur(ComponentEvent ce) {
    FocusFrame.get().unframe();
  }

  protected void onClick(ComponentEvent ce) {
    ce.preventDefault();
    if (!disabled) {
      El target = ce.getTarget("a", 3);
      if (target != null) {
        String className = target.getStyleName();
        if (className.indexOf("color-") != -1) {
          select(className.substring(className.indexOf("color-") + 6, className.indexOf("color-") + 12));
        }
      }
    }
  }

  protected void onFocus(ComponentEvent ce) {
    FocusFrame.get().frame(this);
  }

  protected void onKeyDown(ComponentEvent ce) {
    if (value != null) {
      Element a = el().child("a.color-" + getValue()).dom;
      int row = Integer.valueOf(a.getAttribute("row"));
      if (row < (rowCount - 1)) {
        int idx = indexOf(elements, a);
        idx = idx + columnCount;

        if (idx >= 0 && idx < elements.getLength()) {
          a = elements.getItem(idx);
          String color = getColorFromElement(a);
          select(color, true);
        }
      }
    } else {
      select(getColorFromElement(elements.getItem(0)), true);
    }
  }

  protected void onKeyEnter(ComponentEvent ce) {
    if (value != null) {
      allowReselect = true;
      select(value);
      allowReselect = false;
    }
  }

  protected void onKeyLeft(ComponentEvent ce) {
    if (value != null) {
      Element a = el().child("a.color-" + getValue()).dom;
      int col = Integer.valueOf(a.getAttribute("col"));
      if (col == 0) {
        return;
      }
      int idx = indexOf(elements, a);
      if (idx > 0 && idx < elements.getLength()) {
        a = elements.getItem(idx - 1);
        String color = getColorFromElement(a);
        select(color, true);
      }
    }
  }

  protected void onKeyRight(ComponentEvent ce) {
    if (value != null) {
      Element a = el().child("a.color-" + getValue()).dom;
      int col = Integer.valueOf(a.getAttribute("col"));
      if (col == 7) {
        return;
      }
      int idx = indexOf(elements, a);
      if (idx < elements.getLength() - 1) {
        a = elements.getItem(idx + 1);
        String color = getColorFromElement(a);
        select(color, true);
      }
    } else {
      select(getColorFromElement(elements.getItem(0)), true);
    }
  }

  protected void onKeyUp(ComponentEvent ce) {
    if (value != null) {
      Element a = el().child("a.color-" + getValue()).dom;
      int row = Integer.valueOf(a.getAttribute("row"));
      if (row > 0) {
        int idx = indexOf(elements, a);
        idx = idx - columnCount;
        if (idx >= 0 && idx < elements.getLength()) {
          a = elements.getItem(idx);
          String color = getColorFromElement(a);
          select(color, true);
        }
      }
    }
  }

  protected void onMouseOut(ComponentEvent ce) {
    El target = ce.getTarget("a", 3);
    if (target != null) {
      target.removeStyleName("x-color-palette-hover");
    }
  }

  protected void onMouseOver(ComponentEvent ce) {
    El target = ce.getTarget("a", 3);
    if (target != null) {
      target.addStyleName("x-color-palette-hover");
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    setElement(DOM.createDiv(), target, index);
    super.onRender(target, index);

    rowCount = (int) Math.ceil(colors.length / ((double) columnCount));
    Grid grid = new Grid(rowCount, columnCount);
    grid.getElement().setAttribute("role", "presentation");
    grid.setCellPadding(0);
    grid.setCellSpacing(0);
    int mark = 0;

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount && mark < colors.length; j++) {
        String c = colors[mark];
        String name = colorNames.get(c);
        String lbl = name == null ? c : name;
        grid.setHTML(i, j, "<a aria-label=" + lbl + " id=" + XDOM.getUniqueId() + " role=gridcell row=" + i + " col="
            + j + " class=\"color-" + c + "\"><em role=presentation><span role=presentation style=\"background:#" + c
            + "\" unselectable=\"on\">&#160;</span></em></a>");
        mark++;
      }
    }
    getElement().appendChild(grid.getElement());

    if (GXT.isAriaEnabled()) {
      NodeList<Element> trs = el().select("tr");
      for (int i = 0; i < trs.getLength(); i++) {
        trs.getItem(i).setAttribute("role", "row");
      }

      NodeList<Element> tds = el().select("td");
      for (int i = 0; i < tds.getLength(); i++) {
        Element elem = tds.getItem(i);
        if (elem.getAttribute("row").equals("")) {
          elem.setAttribute("role", "presentation");
        }
      }
    }

    new KeyNav<ComponentEvent>(this) {
      @Override
      public void onDown(ComponentEvent ce) {
        onKeyDown(ce);
      }

      @Override
      public void onEnter(ComponentEvent ce) {
        onKeyEnter(ce);
      }

      @Override
      public void onLeft(ComponentEvent ce) {
        onKeyLeft(ce);
      }

      @Override
      public void onRight(ComponentEvent ce) {
        onKeyRight(ce);
      }

      @Override
      public void onUp(ComponentEvent ce) {
        onKeyUp(ce);
      }

    };

    el().setTabIndex(0);
    el().setElementAttribute("hideFocus", "true");

    elements = el().select("a");

    if (GXT.isAriaEnabled()) {
      getElement().setAttribute("role", "grid");
      getAriaSupport().setLabel(GXT.MESSAGES.colorPalette());
    }

    sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.FOCUSEVENTS);
  }

  private String getColorFromElement(Element elem) {
    String className = elem.getClassName();
    if (className.indexOf("color-") != -1) {
      return className.substring(className.indexOf("color-") + 6, className.indexOf("color-") + 12);
    }
    return null;
  }

  private int indexOf(NodeList<Element> elements, Element elem) {
    for (int i = 0; i < elements.getLength(); i++) {
      if (elements.getItem(i) == elem) {
        return i;
      }
    }
    return -1;
  }

}

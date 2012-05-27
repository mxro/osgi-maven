/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core;

import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * A template class that supports advanced functionality like:
 * 
 * <ul>
 * <li>Formatting</li>
 * <li>Autofilling arrays using templates and sub-templates</li>
 * <li>Conditional processing with basic comparison operators</li>
 * <li>Basic math function support</li>
 * <li>Execute arbitrary inline code with special built-in template variables</li>
 * </ul>
 * 
 * <p>
 * Usage
 * </p>
 * <ul>
 * <li><b>Sample Data</b>
 * <p>
 * <br>
 * This is the data used for reference in each code example:
 * </p>
 * 
 * <pre><code>
    class Kid extends BaseModelData {
      public Kid(String name, int age) {
        set("name", name);
        set("age", age);
      }
    }
    
    class Person extends BaseModelData {
      public Person(String name, String company, String product, String location) {
        set("name", name);
        set("company", company);
        set("product", product);
        set("location", location);
      }
      
      public void setKids(List<Kid> kids) {
        set("kids", kids);
      }
      
      public List<Kid> getKids() {
        return get("kids");
      }
    }

    final Person person = new Person("Darrell Meyer", "Sencha Inc", "Ext GWT", "Washington, DC");

    List<Kid> kids = new ArrayList<Kid>();
    kids.add(new Kid("Alec", 4));
    kids.add(new Kid("Lia", 2));
    kids.add(new Kid("Andrew", 1));

    person.setKids(kids);
 * </code></pre>
 * </div></li>
 * 
 * <li><b>Formatting</b>
 * 
 * <p>
 * <br>
 * Values can be formatted using the following syntax:
 * 
 * <br>
 * <ul>
 * <li>{value:formatName} - no format param</li>
 * <li>{value:formatName(format)} - with format param</li></li>
 * </ul>
 * <br>
 * Available formats:
 * <ul>
 * <li>date(format) - format syntax uses GWT DateTimeFomat (example:
 * {mydate:date("m/d/yyyy")})</li>
 * <li>number(format) - format syntax uses GWT NumberFormat (example:
 * {mynumber:number("0000.0000")})</li>
 * <li>currency - no parameters</li>
 * <li>scientific - no parameters</li>
 * <li>decimal - mo parameters</li>
 * 
 * </ul>
 * 
 * </p> </li>
 * 
 * <li><b>Auto filling of arrays</b>
 * <p>
 * <br>
 * The <b><tt>tpl</tt></b> tag and the <b><tt>for</tt></b> operator are used to
 * process the provided data object:
 * <ul>
 * <li>If the value specified in <tt>for</tt> is an array, it will auto-fill,
 * repeating the template block inside the <tt>tpl</tt> tag for each item in the
 * array.</li>
 * <li>If <tt>for="."</tt> is specified, the data object provided is examined.</li>
 * <li>While processing an array, the special variable <tt>{#}</tt> will provide
 * the current array index + 1 (starts at 1, not 0).</li>
 * </ul>
 * </p>
 * 
 * <pre><code>
&lt;tpl <b>for</b>=".">...&lt;/tpl>       // loop through array at root node
&lt;tpl <b>for</b>="foo">...&lt;/tpl>     // loop through array at foo node
&lt;tpl <b>for</b>="foo.bar">...&lt;/tpl> // loop through array at foo.bar node
 * </code></pre>
 * Using the sample data above:
 * 
 * <pre><code>
// generating strings in native methods easier than in Java, can be created in Java as well
public native String getTemplate() /*-{
  return ['&lt;p>Kids: ',
          '&lt;tpl <b>for</b>=".">',       // process the data.kids node
          '&lt;p>{#}. {name}&lt;/p>',  // use current array index to autonumber
          '&lt;/tpl>&lt;/p>'
         ].join("");
);
XTemplate tpl = XTemplate.create(getTemplate());
tpl.overwrite(someElement, Util.getJsObject(person.getKids())); // pass the kids property of the data object
 * </code></pre>
 * <p>
 * An example illustrating how the <b><tt>for</tt></b> property can be leveraged
 * to access specified members of the provided data object to populate the
 * template:
 * </p>
 * 
 * <pre><code>
public native String getTemplate() /*-{
  return ['&lt;p>Name: {name}&lt;/p>',
          '&lt;p>Title: {title}&lt;/p>',
          '&lt;p>Company: {company}&lt;/p>',
          '&lt;p>Kids: ',
          '&lt;tpl <b>for="kids"</b>>',     // interrogate the kids property within the data
          '&lt;p>{name}&lt;/p>',
          '&lt;/tpl>&lt;/p>'
        ].join("");
);
template.overwrite(someElement, Util.getJsObject(person));
 * </code></pre>
 * <p>
 * 
 * When processing a sub-template, for example while looping through a child
 * array, you can access the parent object's members via the <b><tt>parent</tt>
 * </b> object:
 * </p>
 * 
 * <pre><code>
public native String getTemplate() /*-{
  return ['&lt;p>Name: {name}&lt;/p>',
          '&lt;p>Kids: ',
          '&lt;tpl for="kids">',
          '&lt;tpl if="age > 1">',
            '&lt;p>{name}&lt;/p>',
            '&lt;p>Dad: {<b>parent</b>.name}&lt;/p>',
          '&lt;/tpl>',
          '&lt;/tpl>&lt;/p>'
         ].joint("");
);
template.overwrite(someElement, Util.getJsObject(person));
 * </code></pre>
 * </div></li>
 * 
 * 
 * <li><b>Conditional processing with basic comparison operators</b>
 * <p>
 * <br>
 * The <b><tt>tpl</tt></b> tag and the <b><tt>if</tt></b> operator are used to
 * provide conditional checks for deciding whether or not to render specific
 * parts of the template. Notes:<div class="sub-desc">
 * <ul>
 * <li>Double quotes must be encoded if used within the conditional</li>
 * <li>There is no <tt>else</tt> operator &mdash; if needed, two opposite
 * <tt>if</tt> statements should be used.</li>
 * </ul>
 * </div>
 * 
 * <pre><code>
&lt;tpl if="age &gt; 1 &amp;&amp; age &lt; 10">Child&lt;/tpl>
&lt;tpl if="age >= 10 && age < 18">Teenager&lt;/tpl>
&lt;tpl <b>if</b>="id==\'download\'">...&lt;/tpl>
&lt;tpl <b>if</b>="needsIcon">&lt;img src="{icon}" class="{iconCls}"/>&lt;/tpl>
// no good:
&lt;tpl if="name == "Jack"">Hello&lt;/tpl>
// encode &#34; if it is part of the condition, e.g.
&lt;tpl if="name == &#38;quot;Jack&#38;quot;">Hello&lt;/tpl>
 * </code></pre>
 * Using the sample data above:
 * 
 * <pre><code>
public native String getTemplate() /*-{
  return ['&lt;p>Name: {name}&lt;/p>',
          '&lt;p>Kids: ',
          '&lt;tpl for="kids">',
          '&lt;tpl if="age > 1">',
            '&lt;p>{name}&lt;/p>',
          '&lt;/tpl>',
          '&lt;/tpl>&lt;/p>'
        ].join("");
);
template.overwrite(someElement, Util.getJsObject(person));
 * </code></pre>
 * </div></li>
 * 
 * <li><b>Basic math support</b>
 * <p>
 * <br>
 * The following basic math operators may be applied directly on numeric data
 * values:
 * </p>
 * 
 * <pre>
 * + - * /
 * </pre>
 * For example:
 * 
 * <pre><code>
public native String getTemplate() /*-{
  return ['&lt;p>Name: {name}&lt;/p>',
          '&lt;p>Kids: ',
          '&lt;tpl for="kids">',
          '&lt;tpl if="age &amp;gt; 1">',  // <-- Note that the &gt; is encoded
            '&lt;p>{#}: {name}&lt;/p>',  // <-- Auto-number each item
            '&lt;p>In 5 Years: {age+5}&lt;/p>',  // <-- Basic math
            '&lt;p>Dad: {parent.name}&lt;/p>',
          '&lt;/tpl>',
          '&lt;/tpl>&lt;/p>'
        ].join("");
);
template.overwrite(someElement, Util.getJsObject(person));
</code></pre>
 * </li>
 * 
 * <li><b>Execute arbitrary inline code with special built-in template
 * variables</b>
 * <p>
 * <br>
 * Anything between <code>{[ ... ]}</code> is considered code to be executed in
 * the scope of the template. There are some special variables available in that
 * code:
 * <ul>
 * <li><b><tt>values</tt></b>: The values in the current scope. If you are using
 * scope changing sub-templates, you can change what <tt>values</tt> is.</li>
 * <li><b><tt>parent</tt></b>: The scope (values) of the ancestor template.</li>
 * <li><b><tt>xindex</tt></b>: If you are in a looping template, the index of
 * the loop you are in (1-based).</li>
 * <li><b><tt>xcount</tt></b>: If you are in a looping template, the total
 * length of the array you are looping.</li>
 * <li><b><tt>fm</tt></b>: An alias for <tt>Ext.util.Format</tt>.</li>
 * </ul>
 * This example demonstrates basic row striping using an inline code block and
 * the <tt>xindex</tt> variable:
 * </p>
 * 
 * <pre><code>
public native String getTemplate() /*-{
  return ['&lt;p>Name: {name}&lt;/p>',
          '&lt;p>Company: {[values.company.toUpperCase() + ", " + values.title]}&lt;/p>',
          '&lt;p>Kids: ',
          '&lt;tpl for="kids">',
            '&lt;div class="{[xindex % 2 === 0 ? "even" : "odd"]}">',
            '{name}',
            '&lt;/div>',
          '&lt;/tpl>&lt;/p>'
        ].join("");
);
template.overwrite(someElement, Util.getJsObject(person));
 * </code></pre>
 * </div></li>
 * 
 * <li><b>Template member functions</b> <div class="sub-desc">
 * <p><br>
 * One or more member functions can be specified in a configuration object
 * passed into the XTemplate constructor for more complex processing:
 * </p>
 * 
 * <pre><code>
var tpl = new Ext.XTemplate(
    '&lt;p>Name: {name}&lt;/p>',
    '&lt;p>Kids: ',
    '&lt;tpl for="kids">',
        '&lt;tpl if="this.isGirl(name)">',
            '&lt;p>Girl: {name} - {age}&lt;/p>',
        '&lt;/tpl>',
        // use opposite if statement to simulate 'else' processing:
        '&lt;tpl if="this.isGirl(name) == false">',
            '&lt;p>Boy: {name} - {age}&lt;/p>',
        '&lt;/tpl>',
        '&lt;tpl if="this.isBaby(age)">',
            '&lt;p>{name} is a baby!&lt;/p>',
        '&lt;/tpl>',
    '&lt;/tpl>&lt;/p>',
    {
        // XTemplate configuration:
        compiled: true,
        disableFormats: true,
        // member functions:
        isGirl: function(name){
            return name == 'Sara Grace';
        },
        isBaby: function(age){
            return age < 1;
        }
    }
);
tpl.overwrite(panel.body, data);
 * </code></pre>
 * </div></li> </ul>
 * 
 * 
 */
public final class XTemplate extends JavaScriptObject {

  static {
    GXT.init();
    Ext.loadExt();
    Ext.loadDomHelper();
    Ext.loadFormat();
    Ext.loadTemplate();
    Ext.loadXTemplate();
  }

  /**
   * Specifies the maximum number of nested models to search when preparing the
   * templates data (defaults to 4).
   * 
   * @param maxDepth the maximum number of nested children
   */
  public final native void setMaxDepth(int maxDepth) /*-{
    this.maxDepth = maxDepth;
  }-*/;

  /**
   * Returns the maximum number of nested children to process when preparing the
   * template's data.
   * 
   * @return the max depth
   */
  public final native int getMaxDepth() /*-{
    if (!this.maxDepth) {
      this.maxDepth = 4;
    }
    return this.maxDepth;
  }-*/;

  /**
   * Returns a new template instance using the given html.
   * 
   * @param html the template
   * @return a new template instance
   */
  public static native XTemplate create(String html) /*-{
    return new $wnd.GXT.Ext.XTemplate(html);
  }-*/;

  protected XTemplate() {

  }

  public final native Element append(Element elem, JavaScriptObject values) /*-{
    return this.append(elem, values);
  }-*/;

  /**
   * Returns an HTML fragment of this template with the specified values
   * applied.
   * 
   * @param values the substitution values
   * @return the html fragment
   */
  public final native String applyTemplate(JavaScriptObject values) /*-{
    return this.applyTemplate(values);
  }-*/;

  /**
   * Compiles the template into an internal function, eliminating the regex
   * overhead.
   */
  public final native void compile() /*-{
    this.compile();
  }-*/;

  /**
   * Applies the supplied values to the template and inserts the new node(s)
   * after elem.
   * 
   * @param elem the context element
   * @param values the substitution values
   */
  public final native void insertAfter(Element elem, JavaScriptObject values) /*-{
    this.insertAfter(elem, values);
  }-*/;

  /**
   * Applies the supplied values to the template and inserts the new node(s)
   * before elem.
   * 
   * @param elem the context element
   * @param values the substitution values
   */
  public final native void insertBefore(Element elem, JavaScriptObject values) /*-{
    this.insertBefore(elem, values);
  }-*/;

  /**
   * Applies the supplied values to the template and overwrites the content of
   * elem with the new node(s).
   * 
   * @param elem the context element
   * @param values the substitution values
   */
  public final native void overwrite(Element elem, JavaScriptObject values) /*-{
    this.overwrite(elem, values);
  }-*/;

}

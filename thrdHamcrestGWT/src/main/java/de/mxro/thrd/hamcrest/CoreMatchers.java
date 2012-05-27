// Generated source.
package de.mxro.thrd.hamcrest;

import de.mxro.thrd.hamcrest.core.AllOf;


public class CoreMatchers {

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth, de.mxro.thrd.hamcrest.Matcher<? super T> fifth, de.mxro.thrd.hamcrest.Matcher<? super T> sixth) {
    return AllOf.<T>allOf(first, second, third, fourth, fifth, sixth);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(first, second, third, fourth);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth, de.mxro.thrd.hamcrest.Matcher<? super T> fifth) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(first, second, third, fourth, fifth);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(java.lang.Iterable<de.mxro.thrd.hamcrest.Matcher<? super T>> matchers) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(matchers);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<? super T>... matchers) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(matchers);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(first, second);
  }

  /**
   * Evaluates to true only if ALL of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> allOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third) {
    return de.mxro.thrd.hamcrest.core.AllOf.<T>allOf(first, second, third);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth, de.mxro.thrd.hamcrest.Matcher<? super T> fifth, de.mxro.thrd.hamcrest.Matcher<? super T> sixth) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth, fifth, sixth);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third, de.mxro.thrd.hamcrest.Matcher<? super T> fourth, de.mxro.thrd.hamcrest.Matcher<? super T> fifth) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth, fifth);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(java.lang.Iterable<de.mxro.thrd.hamcrest.Matcher<? super T>> matchers) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(matchers);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<? super T>... matchers) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(matchers);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(first, second);
  }

  /**
   * Evaluates to true if ANY of the passed in matchers evaluate to true.
   */
  public static <T> de.mxro.thrd.hamcrest.core.AnyOf<T> anyOf(de.mxro.thrd.hamcrest.Matcher<T> first, de.mxro.thrd.hamcrest.Matcher<? super T> second, de.mxro.thrd.hamcrest.Matcher<? super T> third) {
    return de.mxro.thrd.hamcrest.core.AnyOf.<T>anyOf(first, second, third);
  }

  /**
   * This is useful for fluently combining matchers that must both pass. For example:
   * <pre>
   * assertThat(string, both(containsString("a")).and(containsString("b")));
   * </pre>
   */
  public static <LHS> de.mxro.thrd.hamcrest.core.CombinableMatcher<LHS> both(de.mxro.thrd.hamcrest.Matcher<? super LHS> matcher) {
    return de.mxro.thrd.hamcrest.core.CombinableMatcher.<LHS>both(matcher);
  }

  /**
   * This is useful for fluently combining matchers where either may pass, for example:
   * <pre>
   * assertThat(string, both(containsString("a")).and(containsString("b")));
   * </pre>
   */
  public static <LHS> de.mxro.thrd.hamcrest.core.CombinableMatcher<LHS> either(de.mxro.thrd.hamcrest.Matcher<? super LHS> matcher) {
    return de.mxro.thrd.hamcrest.core.CombinableMatcher.<LHS>either(matcher);
  }

  /**
   * Wraps an existing matcher and overrides the description when it fails.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> describedAs(java.lang.String description, de.mxro.thrd.hamcrest.Matcher<T> matcher, java.lang.Object... values) {
    return de.mxro.thrd.hamcrest.core.DescribedAs.<T>describedAs(description, matcher, values);
  }

  /**
   * 
   * 
   * @param itemMatcher A matcher to apply to every element in a collection.
   * @return Evaluates to TRUE for a collection in which every item matches itemMatcher
   */
  public static <U> de.mxro.thrd.hamcrest.Matcher<java.lang.Iterable<U>> everyItem(de.mxro.thrd.hamcrest.Matcher<U> itemMatcher) {
    return de.mxro.thrd.hamcrest.core.Every.<U>everyItem(itemMatcher);
  }

  /**
   * Decorates another Matcher, retaining the behavior but allowing tests
   * to be slightly more expressive.
   * 
   * For example: assertThat(cheese, equalTo(smelly))
   * vs. assertThat(cheese, is(equalTo(smelly)))
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> is(de.mxro.thrd.hamcrest.Matcher<T> matcher) {
    return de.mxro.thrd.hamcrest.core.Is.<T>is(matcher);
  }

  /**
   * This is a shortcut to the frequently used is(equalTo(x)).
   * 
   * For example: assertThat(cheese, is(equalTo(smelly)))
   * vs. assertThat(cheese, is(smelly))
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<? super T> is(T value) {
    return de.mxro.thrd.hamcrest.core.Is.<T>is(value);
  }


  /**
   * This matcher always evaluates to true.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> anything() {
    return de.mxro.thrd.hamcrest.core.IsAnything.<T>anything();
  }

  /**
   * This matcher always evaluates to true.
   * 
   * @param description A meaningful string used when describing itself.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> anything(java.lang.String description) {
    return de.mxro.thrd.hamcrest.core.IsAnything.<T>anything(description);
  }

  public static <T> de.mxro.thrd.hamcrest.Matcher<java.lang.Iterable<? super T>> hasItem(T element) {
    return de.mxro.thrd.hamcrest.core.IsCollectionContaining.<T>hasItem(element);
  }

  public static <T> de.mxro.thrd.hamcrest.Matcher<java.lang.Iterable<? super T>> hasItem(de.mxro.thrd.hamcrest.Matcher<? super T> elementMatcher) {
    return de.mxro.thrd.hamcrest.core.IsCollectionContaining.<T>hasItem(elementMatcher);
  }

  public static <T> de.mxro.thrd.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(de.mxro.thrd.hamcrest.Matcher<? super T>... elementMatchers) {
    return de.mxro.thrd.hamcrest.core.IsCollectionContaining.<T>hasItems(elementMatchers);
  }

  public static <T> de.mxro.thrd.hamcrest.Matcher<java.lang.Iterable<T>> hasItems(T... elements) {
    return de.mxro.thrd.hamcrest.core.IsCollectionContaining.<T>hasItems(elements);
  }

  /**
   * Is the value equal to another value, as tested by the
   * {@link java.lang.Object#equals} invokedMethod?
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<? super T> equalTo(T operand) {
    return de.mxro.thrd.hamcrest.core.IsEqual.<T>equalTo(operand);
  }

  /**
   * Is the value an instance of a particular type?
   * This version assumes no relationship between the required type and
   * the signature of the method that sets it up, for example in
   * <code>assertThat(anObject, instanceOf(Thing.class));</code>
   */
  /*public static <T> org.hamcrest.Matcher<T> instanceOf(java.lang.Class<?> type) {
    return org.hamcrest.core.IsInstanceOf.<T>instanceOf(type);
  }*/

  /**
   * Is the value an instance of a particular type?
   * Use this version to make generics conform, for example in
   * the JMock clause <code>with(any(Thing.class))</code>
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> any(java.lang.Class<T> type) {
    return de.mxro.thrd.hamcrest.core.IsInstanceOf.<T>any(type); 
  }

  /**
   * Inverts the rule.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> not(de.mxro.thrd.hamcrest.Matcher<T> matcher) {
    return de.mxro.thrd.hamcrest.core.IsNot.<T>not(matcher);
  }

  /**
   * This is a shortcut to the frequently used not(equalTo(x)).
   * 
   * For example: assertThat(cheese, is(not(equalTo(smelly))))
   * vs. assertThat(cheese, is(not(smelly)))
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<? super T> not(T value) {
    return de.mxro.thrd.hamcrest.core.IsNot.<T>not(value);
  }

  /**
   * Matches if value is null.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> nullValue() {
    return de.mxro.thrd.hamcrest.core.IsNull.<T>nullValue();
  }

  /**
   * Matches if value is null. With type inference.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> nullValue(java.lang.Class<T> type) {
    return de.mxro.thrd.hamcrest.core.IsNull.<T>nullValue(type);
  }

  /**
   * Matches if value is not null.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> notNullValue() {
    return de.mxro.thrd.hamcrest.core.IsNull.<T>notNullValue();
  }

  /**
   * Matches if value is not null. With type inference.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> notNullValue(java.lang.Class<T> type) {
    return de.mxro.thrd.hamcrest.core.IsNull.<T>notNullValue(type);
  }

  /**
   * Creates a new instance of IsSame
   * 
   * @param object The predicate evaluates to true only when the argument is
   * this object.
   */
  public static <T> de.mxro.thrd.hamcrest.Matcher<T> sameInstance(T object) {
    return de.mxro.thrd.hamcrest.core.IsSame.<T>sameInstance(object);
  }

  public static de.mxro.thrd.hamcrest.Matcher<java.lang.String> containsString(java.lang.String substring) {
    return de.mxro.thrd.hamcrest.core.StringContains.containsString(substring);
  }

  public static de.mxro.thrd.hamcrest.Matcher<java.lang.String> startsWith(java.lang.String substring) {
    return de.mxro.thrd.hamcrest.core.StringStartsWith.startsWith(substring);
  }

  public static de.mxro.thrd.hamcrest.Matcher<java.lang.String> endsWith(java.lang.String substring) {
    return de.mxro.thrd.hamcrest.core.StringEndsWith.endsWith(substring);
  }

}

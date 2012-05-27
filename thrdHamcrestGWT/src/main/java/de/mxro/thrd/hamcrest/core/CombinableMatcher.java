package de.mxro.thrd.hamcrest.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.CompositeMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

public class CombinableMatcher<T> extends BaseMatcher<T> implements CompositeMatcher {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Matcher<? super T> matcher;

	public CombinableMatcher(Matcher<? super T> matcher) {
		this.matcher= matcher;
	}

	@Override
	public boolean matches(Object item) {
		return matcher.matches(item);
	}

	@Override
	public void describeTo(Description description) {
		description.appendDescriptionOf(matcher);
	}
	
  public CombinableMatcher<T> and(Matcher<? super T> other) {
		return new CombinableMatcher<T>(new AllOf<T>(templatedListWith(other)));
	}

  public CombinableMatcher<T> or(Matcher<? super T> other) {
    return new CombinableMatcher<T>(new AnyOf<T>(templatedListWith(other)));
	}

  private ArrayList<Matcher<? super T>> templatedListWith(Matcher<? super T> other) {
    ArrayList<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>();
    matchers.add(matcher);
    matchers.add(other);
    return matchers;
  }
	
	 /**
   * This is useful for fluently combining matchers that must both pass.  For example:
   * <pre>
   *   assertThat(string, both(containsString("a")).and(containsString("b")));
   * </pre>
   */
	@Factory
  public static <LHS> CombinableMatcher<LHS> both(Matcher<? super LHS> matcher) {
    return new CombinableMatcher<LHS>(matcher);
  }
  
  /**
   * This is useful for fluently combining matchers where either may pass, for example:
   * <pre>
   *   assertThat(string, both(containsString("a")).and(containsString("b")));
   * </pre>
   */
	@Factory
  public static <LHS> CombinableMatcher<LHS> either(Matcher<? super LHS> matcher) {
    return new CombinableMatcher<LHS>(matcher);
  }

	@Override
	public List<Matcher<?>> getMatcher() {
		List<Matcher<?>> matchers = new Vector<Matcher<?>>();
		matchers.add(this.matcher);
		return matchers;
	}

}
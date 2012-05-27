/*  Copyright (c) 2000-2009 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import static de.mxro.thrd.hamcrest.core.IsEqual.equalTo;

import java.util.List;
import java.util.Vector;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.CompositeMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;


/**
 * Calculates the logical negation of a matcher.
 */
public class IsNot<T> extends BaseMatcher<T> implements CompositeMatcher  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Matcher<T> matcher;

    public IsNot(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
	public boolean matches(Object arg) {
        return !matcher.matches(arg);
    }

    @Override
	public void describeTo(Description description) {
        description.appendText("not ").appendDescriptionOf(matcher);
    }

    
    /**
     * Inverts the rule.
     */
    @Factory
    public static <T> Matcher<T> not(Matcher<T> matcher) {
        return new IsNot<T>(matcher);
    }

    /**
     * This is a shortcut to the frequently used not(equalTo(x)).
     *
     * For example:  assertThat(cheese, is(not(equalTo(smelly))))
     *          vs.  assertThat(cheese, is(not(smelly)))
     */
    @Factory
    public static <T> Matcher<? super T> not(T value) {
        return not(equalTo(value));
    }

    @Override
	public List<Matcher<?>> getMatcher() {
		List<Matcher<?>> matchers = new Vector<Matcher<?>>();
		matchers.add(this.matcher);
		return matchers;
	}
}

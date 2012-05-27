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
 * Decorates another Matcher, retaining the behavior but allowing tests
 * to be slightly more expressive.
 *
 * For example:  assertThat(cheese, equalTo(smelly))
 *          vs.  assertThat(cheese, is(equalTo(smelly)))
 */
//pmuetschard: This is the GWT translatable version, which does not use org.hamcrest.core.IsInstanceOf
public class Is<T> extends BaseMatcher<T> implements CompositeMatcher {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Matcher<T> matcher;

    public Is(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
	public boolean matches(Object arg) {
        return matcher.matches(arg);
    }

    @Override
	public void describeTo(Description description) {
        description.appendText("is ").appendDescriptionOf(matcher);
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        // TODO(ngd): unit tests....
        matcher.describeMismatch(item, mismatchDescription);
    }

    /**
     * Decorates another Matcher, retaining the behavior but allowing tests
     * to be slightly more expressive.
     *
     * For example:  assertThat(cheese, equalTo(smelly))
     *          vs.  assertThat(cheese, is(equalTo(smelly)))
     */
    @Factory
    public static <T> Matcher<T> is(Matcher<T> matcher) {
        return new Is<T>(matcher);
    }

    /**
     * This is a shortcut to the frequently used is(equalTo(x)).
     *
     * For example:  assertThat(cheese, is(equalTo(smelly)))
     *          vs.  assertThat(cheese, is(smelly))
     */
    @Factory
    public static <T> Matcher<? super T> is(T value) {
        return is(equalTo(value));
    }

	@Override
	public List<Matcher<?>> getMatcher() {
		List<Matcher<?>> matchers = new Vector<Matcher<?>>();
		matchers.add(this.matcher);
		return matchers;
	}
}

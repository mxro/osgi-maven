/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import static de.mxro.thrd.hamcrest.core.IsNot.not;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

/**
 * Is the value null?
 */
public class IsNull<T> extends BaseMatcher<T> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean matches(Object o) {
        return o == null;
    }

    @Override
	public void describeTo(Description description) {
        description.appendText("null");
    }

    /**
     * Matches if value is null.
     */
    @Factory
    public static <T> Matcher<T> nullValue() {
        return new IsNull<T>();
    }

    /**
     * Matches if value is not null.
     */
    @Factory
    public static <T> Matcher<T> notNullValue() {
        return not(IsNull.<T>nullValue());
    }

    /**
     * Matches if value is null. With type inference.
     */
    @Factory
    public static <T> Matcher<T> nullValue( Class<T> type) {
        return nullValue();
    }

    /**
     * Matches if value is not null. With type inference.
     */
    @Factory
    public static <T> Matcher<T> notNullValue( Class<T> type) {
        return notNullValue();
    }
}


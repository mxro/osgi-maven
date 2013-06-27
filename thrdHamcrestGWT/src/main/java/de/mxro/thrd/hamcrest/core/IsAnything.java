/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

/**
 * A matcher that always returns <code>true</code>.
 */
public class IsAnything<T> extends BaseMatcher<T> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public String message;

    public IsAnything() {
        this("ANYTHING");
    }

    public IsAnything(final String message) {
        this.message = message;
    }

    @Override
    public boolean matches(final Object o) {
        return true;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(message);
    }

    /**
     * This matcher always evaluates to true.
     */
    @Factory
    public static <T> Matcher<T> anything() {
        return new IsAnything<T>();
    }

    /**
     * This matcher always evaluates to true.
     * 
     * @param description
     *            A meaningful string used when describing itself.
     */
    @Factory
    public static <T> Matcher<T> anything(final String description) {
        return new IsAnything<T>(description);
    }
}

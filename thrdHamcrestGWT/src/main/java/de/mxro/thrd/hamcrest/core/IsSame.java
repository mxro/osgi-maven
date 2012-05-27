/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;


/**
 * Is the value the same object as another value?
 */
public class IsSame<T> extends BaseMatcher<T> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final T object;
    
    public IsSame(T object) {
        this.object = object;
    }

    @Override
	public boolean matches(Object arg) {
        return arg == object;
    }

    @Override
	public void describeTo(Description description) {
        description.appendText("sameInstance(")
                .appendValue(object)
                .appendText(")");
    }
    
    /**
     * Creates a new instance of IsSame
     *
     * @param object The predicate evaluates to true only when the argument is
     *               this object.
     */
    @Factory
    public static <T> Matcher<T> sameInstance(T object) {
        return new IsSame<T>(object);
    }
}

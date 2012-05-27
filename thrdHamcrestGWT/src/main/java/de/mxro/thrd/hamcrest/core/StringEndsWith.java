/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

/**
 * Tests if the argument is a string that contains a substring.
 */
public class StringEndsWith extends SubstringMatcher {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringEndsWith(String substring) {
        super(substring);
    }

    @Override
    protected boolean evalSubstringOf(String s) {
        return s.endsWith(substring);
    }

    @Override
    protected String relationship() {
        return "ending with";
    }

    @Factory
    public static Matcher<String> endsWith(String substring) {
        return new StringEndsWith(substring);
    }

}

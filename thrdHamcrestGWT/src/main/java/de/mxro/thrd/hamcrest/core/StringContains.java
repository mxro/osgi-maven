/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

/**
 * Tests if the argument is a string that contains a substring.
 */
public class StringContains extends SubstringMatcher {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringContains(String substring) {
        super(substring);
    }

    @Override
    protected boolean evalSubstringOf(String s) {
        return s.indexOf(substring) >= 0;
    }

    @Override
    protected String relationship() {
        return "containing";
    }

    @Factory
    public static Matcher<String> containsString(String substring) {
        return new StringContains(substring);
    }

}
/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

/**
 * Provides a custom description to another matcher.
 */
public class DescribedAs<T> extends BaseMatcher<T> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String descriptionTemplate;
    private final Matcher<T> matcher;
    private final Object[] values;

    public DescribedAs(String descriptionTemplate, Matcher<T> matcher, Object[] values) {
        this.descriptionTemplate = descriptionTemplate;
        this.matcher = matcher;
        this.values = new Object[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
    }

    @Override
	public boolean matches(Object o) {
        return matcher.matches(o);
    }

    @Override
	public void describeTo(Description description) {
        int p;
        String s = descriptionTemplate;
        while ((p = s.indexOf('%')) >= 0) {
            int q = p + 1;
            while (q < s.length()) {
                int c = s.charAt(q);
                if (c < '0' || c > '9') {
                    break;
                }
                q++;
            }
            description.appendText(s.substring(0, p));
            if (q == p + 1) {
                description.appendText("%");
            } else {
                int idx = Integer.parseInt(s.substring(p + 1, q));
                description.appendValue(values[idx]);
            }
            s = s.substring(q);
        }
        description.appendText(s);
    }

    /**
     * Wraps an existing matcher and overrides the description when it fails.
     */
    @Factory
    public static <T> Matcher<T> describedAs(String description, Matcher<T> matcher, Object... values) {
        return new DescribedAs<T>(description, matcher, values);
    }
}

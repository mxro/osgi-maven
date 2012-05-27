package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.DiagnosingMatcher;

public abstract class StringMatcher extends DiagnosingMatcher<String> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public boolean matches(Object item, Description mismatchDescription) {
        boolean result = false;
        if (item == null || !(item instanceof String)) {
            super.describeMismatch(item, mismatchDescription);
        } else {
            result = matchesSafely((String)item, mismatchDescription);
        }
        return result;
    }

    protected abstract boolean matchesSafely(String string, Description mismatchDescription);

    protected boolean isWhitespace(char ch) {
        return (ch >= 0x09 && ch <= 0x0D) || (ch >= 0x1C && ch <= 0x20);
    }
}

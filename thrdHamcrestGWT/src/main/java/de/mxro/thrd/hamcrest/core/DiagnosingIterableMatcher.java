package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.DiagnosingMatcher;

public abstract class DiagnosingIterableMatcher<I extends Iterable<?>> extends DiagnosingMatcher<I> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
    protected boolean matches(Object item, Description mismatchDescription) {
        boolean result = false;
        if (item == null || !(item instanceof Iterable)) {
            mismatchDescription.appendText("was ").appendValue(item);
        } else {
            result = matchesSafely((I)item, mismatchDescription);
        }
        return result;
    }

    protected abstract boolean matchesSafely(I iterable, Description mismatchDescription);
}

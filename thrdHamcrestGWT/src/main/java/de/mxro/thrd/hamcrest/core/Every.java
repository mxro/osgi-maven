package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;

public class Every<T> extends DiagnosingIterableMatcher<Iterable<T>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Matcher<? super T> matcher;

	public Every(Matcher<? super T> matcher) {
		this.matcher= matcher;
	}
	
	@Override
	public boolean matchesSafely(Iterable<T> collection, Description mismatchDescription) {
		for (T t : collection) {
			if (!matcher.matches(t)) {
				mismatchDescription.appendText("an item ");
				matcher.describeMismatch(t, mismatchDescription);
				return false;
			}
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("every item is ").appendDescriptionOf(matcher);
	}
	
	
	 /**
   * @param itemMatcher A matcher to apply to every element in a collection.
   * @return Evaluates to TRUE for a collection in which every item matches itemMatcher 
   */
	@Factory
	public static <U> Matcher<Iterable<U>> everyItem(final Matcher<U> itemMatcher) {
		return new Every<U>(itemMatcher);
	}

}

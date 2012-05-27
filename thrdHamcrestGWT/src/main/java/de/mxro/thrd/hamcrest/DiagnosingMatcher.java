package de.mxro.thrd.hamcrest;

/**
 * TODO(ngd): Document.
 *
 * @param <T>
 */
public abstract class DiagnosingMatcher<T> extends BaseMatcher<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public final boolean matches(Object item) {
		return matches(item, Description.NONE);
	}

	@Override
  public final void describeMismatch(Object item, Description mismatchDescription) {
		matches(item, mismatchDescription);
	}

	protected abstract boolean matches(Object item, Description mismatchDescription);
}

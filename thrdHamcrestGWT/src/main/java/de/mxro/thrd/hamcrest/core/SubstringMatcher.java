package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Description;

public abstract class SubstringMatcher extends StringMatcher {

    // TODO: Replace String with CharSequence to allow for easy interopability between
    //       String, StringBuffer, StringBuilder, CharBuffer, etc (joe).

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final String substring;

    protected SubstringMatcher(final String substring) {
        this.substring = substring;
    }

    @Override
    public boolean matchesSafely(String item, Description mismatchDescription) {
        if (!evalSubstringOf(item)) {
            mismatchDescription.appendText("was \"").appendText(item).appendText("\"");
            return false;
        }
        return true;
    }

    @Override
	public void describeTo(Description description) {
        description.appendText("a string ")
                .appendText(relationship())
                .appendText(" ")
                .appendValue(substring);
    }

    protected abstract boolean evalSubstringOf(String string);

    protected abstract String relationship();
}
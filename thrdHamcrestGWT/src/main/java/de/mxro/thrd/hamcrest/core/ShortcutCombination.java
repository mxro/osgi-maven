package de.mxro.thrd.hamcrest.core;

import java.util.List;
import java.util.Vector;

import de.mxro.thrd.hamcrest.BaseMatcher;
import de.mxro.thrd.hamcrest.CompositeMatcher;
import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.Matcher;

abstract class ShortcutCombination<T> extends BaseMatcher<T> implements CompositeMatcher {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Iterable<Matcher<? super T>> matchers;

    public ShortcutCombination(Iterable<Matcher<? super T>> matchers) {
        this.matchers = matchers;
    }
    
    @Override
	public abstract boolean matches(Object o);
    
    @Override
	public abstract void describeTo(Description description);
    
    protected boolean matches(Object o, boolean shortcut) {
        for (Matcher<? super T> matcher : matchers) {
            if (matcher.matches(o) == shortcut) {
                return shortcut;
            }
        }
        return !shortcut;
    }
    
    public void describeTo(Description description, String operator) {
        description.appendList("(", " " + operator + " ", ")", matchers);
    }

    @Override
	public List<Matcher<?>> getMatcher() {
		List<Matcher<?>> matchers=new Vector<Matcher<?>>();
		for (Matcher<?> m : this.matchers) {
			matchers.add(m);
		}
		return matchers;
	}
    
    
}

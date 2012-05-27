package de.mxro.thrd.hamcrest.internal;

import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.SelfDescribing;

public class SelfDescribingValue<T> implements SelfDescribing {
    private T value;
    
    public SelfDescribingValue(T value) {
        this.value = value;
    }

    @Override
	public void describeTo(Description description) {
        description.appendValue(value);
    }
}

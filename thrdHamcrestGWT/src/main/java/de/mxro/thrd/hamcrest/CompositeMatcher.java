package de.mxro.thrd.hamcrest;

import java.util.List;


public interface CompositeMatcher {
	public List<Matcher<?>> getMatcher();
}

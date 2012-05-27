/*  Copyright (c) 2000-2006 hamcrest.org
 */
package de.mxro.thrd.hamcrest.core;

import de.mxro.thrd.hamcrest.Description;
import de.mxro.thrd.hamcrest.DiagnosingMatcher;
import de.mxro.thrd.hamcrest.Factory;
import de.mxro.thrd.hamcrest.Matcher;


/**
 * Tests whether the value is an instance of a class.
 * Classes of basic types will be converted to the relevant "Object" classes
 */
public class IsInstanceOf extends DiagnosingMatcher<Object> {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static boolean isSuperclass(Class<? extends Object> superclass, Class<? extends Object> clazz) {
		if (superclass.equals(clazz)) return true;
		
		// all classes are superlcass of Object
		if (clazz.equals(Object.class)) return false;
		
		return isSuperclass(superclass, clazz.getSuperclass());
	}
	
	/**
	 * necessary for GWT as Class.isInstance is not supported
	 * @param clazz
	 * @param object
	 * @return
	 */
	public static boolean instanceOf(Class<? extends Object> clazz, Object object) {
		return isSuperclass(clazz, object.getClass());
		
	}
	
	private final Class<?> expectedClass;
    private final Class<?> matchableClass;

    /**
     * Creates a new instance of IsInstanceOf
     *
     * @param expectedClass The predicate evaluates to true for instances of this class
     *                 or one of its subclasses.
     */
    public IsInstanceOf(Class<?> expectedClass) {
        this.expectedClass = expectedClass;
        this.matchableClass = matchableClass(expectedClass);
    }

    private static Class<?> matchableClass(Class<?> expectedClass) {
      if (boolean.class.equals(expectedClass)) return Boolean.class; 
      if (byte.class.equals(expectedClass)) return Byte.class; 
      if (char.class.equals(expectedClass)) return Character.class; 
      if (double.class.equals(expectedClass)) return Double.class; 
      if (float.class.equals(expectedClass)) return Float.class; 
      if (int.class.equals(expectedClass)) return Integer.class; 
      if (long.class.equals(expectedClass)) return Long.class; 
      if (short.class.equals(expectedClass)) return Short.class; 
      return expectedClass;
    }

    
    
    
    @Override
    protected boolean matches(Object item, Description mismatchDescription) {
        if (null == item) {
          mismatchDescription.appendText("null");
          return false;
        }
        
        if (!instanceOf(matchableClass, item)) {
          mismatchDescription.appendValue(item).appendText(" is a " + item.getClass().getName());
          return false;
        }
        
        return true;
      }

    @Override
	public void describeTo(Description description) {
        description.appendText("an instance of ")
                .appendText(expectedClass.getName());
    }

    /**
     * Is the value an instance of a particular type? 
     * This version assumes no relationship between the required type and
     * the signature of the method that sets it up, for example in
     * <code>assertThat(anObject, instanceOf(Thing.class));</code>
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Matcher<T> instanceOf(Class<?> type) {
        return (Matcher<T>) new IsInstanceOf(type);
    }
    
    /**
     * Is the value an instance of a particular type? 
     * Use this version to make generics conform, for example in 
     * the JMock clause <code>with(any(Thing.class))</code> 
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Matcher<T> any(Class<T> type) {
        return (Matcher<T>) new IsInstanceOf(type);
    }

}

package de.mxro.thrd.hamcrest;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a Hamcrest static factory method so tools recognise them.
 * A factory method is an equivalent to a named constructor.
 * 
 * @author Joe Walnes
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface Factory {
    /**
     * The targets from which to exclude this constructor.
     */
    public String[] excludes() default {};
}

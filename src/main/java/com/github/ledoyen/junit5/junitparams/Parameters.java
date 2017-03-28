package com.github.ledoyen.junit5.junitparams;

import static org.junit.platform.commons.meta.API.Usage.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.meta.API;

/**
 * Tag a JUnit 5 test method for parameter injection. Indicates that a method takes
 * some parameters and define how to obtain them.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestTemplate
@ExtendWith(JUnitParamsExtension.class)
@API(Experimental)
public @interface Parameters {

    /**
     * Parameter values defined as a String array. Each element in the array is
     * a full parameter set, comma-separated or pipe-separated ('|').
     * The values must match the method parameters in order and type.
     * Whitespace characters are trimmed (use source class or method if You need to provide such parameters)
     * <p>
     * Example: <code>@Parameters({
     * "1, joe, 26.4, true",
     * "2, angie, 37.2, false"})</code>
     */
    String[] value() default {};

    /**
     * Parameter values returned by a method within the test class. This way you
     * don't need additional classes and the test code may be a bit cleaner. The
     * format of the data returned by the method is the same as for the source
     * annotation class.
     * Example: <code>@Parameters(method = "examplaryPeople")</code>
     * <p>
     * You can use multiple methods to provide parameters - use comma to do it:
     * Example: <code>@Parameters(method = "womenParams, menParams")</code>
     */
    String method() default "";
}

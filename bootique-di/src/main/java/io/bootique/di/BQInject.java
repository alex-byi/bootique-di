package io.bootique.di;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A runtime annotation for marking injection point.
 * Constructors and fields marked with this annotation will be used as injection points,
 * just like marked with {@link javax.inject.Inject} annotation.
 * Moreover later should be proffered in most cases.
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BQInject {
}

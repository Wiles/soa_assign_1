package ca.setc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for a service method parameter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterAnno {
    /**
     * is the parameter required
     * @return
     */
    boolean required() default true;

    /**
     * the name of the parameter
     * @return
     */
    String name();
}

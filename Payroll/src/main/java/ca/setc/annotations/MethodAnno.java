package ca.setc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for a service method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAnno {
    /**
     * the name
     * @return
     */
    String name();

    /**
     * the descriptions of the returned values
     * @return
     */
    String[] returnDescriptions();

}

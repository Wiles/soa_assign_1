package ca.setc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SOA service annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceAnno {
    /**
     * the name of the service
     * @return
     */
    String name();

    /**
     * The description of the service
     * @return
     */
    String description();

    /**
     * The security level of the service
     * @return
     */
    int securityLevel();
}

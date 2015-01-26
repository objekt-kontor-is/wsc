package org.okis.wsc.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigParameter {

    public static String FIELD_NAME = "<field.name>";

    String value() default FIELD_NAME;
}

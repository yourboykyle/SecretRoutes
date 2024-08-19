package xyz.yourboykyle.secretroutes.config.annotations;

import cc.polyfrost.oneconfig.config.annotations.CustomOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CustomOption(id = "myOption")
public @interface DynamicDropdownAnnotation {
    String name();
    String[] options() default {};
}

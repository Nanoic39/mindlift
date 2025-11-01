package cn.dcstd.web.mindlift3.common;

import java.lang.annotation.*;

/**
 * @author NaNo1c
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {
    String value() default "";
}

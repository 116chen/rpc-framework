package github.ch.annotation;


import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String version() default "";

    String group() default "";
}

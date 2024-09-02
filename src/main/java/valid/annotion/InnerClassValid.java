package valid.annotion;

import java.lang.annotation.*;

/**
 * @author spring
 * @since 2024/9/2 16:43:51
 * @apiNote
 * @version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerClassValid {
    boolean enable() default true;
}

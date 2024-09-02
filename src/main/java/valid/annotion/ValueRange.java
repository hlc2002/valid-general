package valid.annotion;

import java.lang.annotation.*;

/**
 * @author spring
 * @since 2024/9/2 14:18:17
 * @apiNote
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueRange {
    int max() default Integer.MAX_VALUE;

    int min() default Integer.MIN_VALUE;
}

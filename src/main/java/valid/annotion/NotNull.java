package valid.annotion;

import java.lang.annotation.*;

/**
 * @author spring
 * @since 2024/9/2 14:17:47
 * @apiNote
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {
}

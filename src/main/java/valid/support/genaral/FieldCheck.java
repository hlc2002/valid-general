package valid.support.genaral;

import java.lang.reflect.Field;

/**
 * @author spring
 * @since 2024/9/2 11:45:30
 * @apiNote
 * @version 1.0
 */
@FunctionalInterface
public interface FieldCheck {
    void check(Field field, Object fieldValue);
}

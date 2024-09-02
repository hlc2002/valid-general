package valid.support;

import java.lang.reflect.Field;

/**
 * @author spring
 * @since 2024/9/2 11:45:42
 * @apiNote
 * @version 1.0
 */
public interface FieldIgnore {
    boolean ignore(Field field);
}

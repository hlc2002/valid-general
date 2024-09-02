package valid.support.custom;

import java.lang.reflect.Field;

/**
 * @author spring
 * @since 2024/9/2 14:01:21
 * @apiNote
 * @version 1.0
 */
public interface FieldValidRuleInit {
    void init(Field field);
}

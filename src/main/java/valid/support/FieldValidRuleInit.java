package valid.support;

import valid.obj.FieldTypeValidRule;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author spring
 * @since 2024/9/2 14:01:21
 * @apiNote
 * @version 1.0
 */
public interface FieldValidRuleInit {
    void init(Field field);
}

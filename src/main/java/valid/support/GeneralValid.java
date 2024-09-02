package valid.support;

import java.lang.reflect.Field;

/**
 * @author spring
 * @since 2024/9/2 11:11:16
 * @apiNote 无定制校验处理的通用校验器
 * @version 1.0
 */
public interface GeneralValid {
    String validObjectFieldNotNull(Object object, Class<?> clazz);

    String validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck);

    String validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck, String... ignoreFieldNames);

    String validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore);

    String validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore, String... ignoreFieldNames);

    String validSingleField(Field field, Object object, FieldCheck fieldCheck);
}

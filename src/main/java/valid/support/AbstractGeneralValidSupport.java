package valid.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author spring
 * @since 2024/9/2 11:20:40
 * @apiNote 抽象通用校验实现
 * @version 1.0
 */
public class AbstractGeneralValidSupport implements GeneralValid {

    private final static String EMPTY_STRING = "";

    @Override
    public String validObjectFieldNotNull(Object object, Class<?> clazz) {
        return validObjectFieldNotNull(object, clazz, true);
    }

    @Override
    public String validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck) {
        return validObjectFieldNotNull(object, clazz, true, new String[0]);
    }


    @Override
    public String validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck, String... ignoreFieldNames) {
        return EMPTY_STRING;
    }

    @Override
    public String validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore) {
        return validObjectField(object, clazz, fieldCheck, fieldIgnore, new String[0]);
    }

    @Override
    public String validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore, String... ignoreFieldNames) {
        return EMPTY_STRING;
    }

    @Override
    public String validSingleField(String field, Object object, FieldCheck fieldCheck) {
        return EMPTY_STRING;
    }


    // 是否可以检查 非最终修饰与静态修饰的字段是可检查字段
    private final static FieldIgnore CAN_CHECK_FIELD =
            field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
}

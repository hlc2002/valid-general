package valid.support;

import valid.tools.ReflectProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author spring
 * @since 2024/9/2 11:20:40
 * @apiNote 抽象通用校验实现
 * @version 1.0
 */
@SuppressWarnings("all")
public abstract class AbstractGeneralValidSupport implements GeneralValid {

    @Override
    public void validObjectFieldNotNull(Object object, Class<?> clazz) {
        validObjectFieldNotNull(object, clazz, true);
    }

    @Override
    public void validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck) {
        validObjectFieldNotNull(object, clazz, true, new String[0]);
    }


    @Override
    public void validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck, String... ignoreFieldNames) {
        if (objectCheck && object == null) {
            throw new RuntimeException("object is null");
        }
        List<String> ignoreFieldNameList = ignoreFieldNames.length > 0 ? Arrays.asList(ignoreFieldNames) : Collections.emptyList();
        ReflectProvider.doWithLocalFields(clazz, field -> {
            if (CAN_CHECK_FIELD.ignore(field) && !ignoreFieldNameList.contains(field.getName())) {
                Object fieldValue = ReflectProvider.getFieldValue(field, object);
                if (fieldValue == null) {
                    throw new RuntimeException(field.getName() + " is null");
                }
            }
        });
    }

    @Override
    public void validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore) {
        validObjectField(object, clazz, fieldCheck, fieldIgnore, new String[0]);
    }

    @Override
    public void validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore, String... ignoreFieldNames) {
        List<String> ignoreFieldNameList = ignoreFieldNames.length > 0 ? Arrays.asList(ignoreFieldNames) : Collections.emptyList();
        ReflectProvider.doWithLocalFields(clazz, field -> {
            if (ignoreFieldNameList.contains(field.getName()) || fieldIgnore.ignore(field)) {
                return;
            }
            Object fieldValue = ReflectProvider.getFieldValue(field, object);
            if (fieldValue == null) {
                throw new RuntimeException(field.getName() + " is null");
            }
            fieldCheck.check(field, fieldValue);
        });
    }

    @Override
    public void validSingleField(String fieldName, Object object, Class<?> clazz, FieldCheck fieldCheck) {
        if (object == null) {
            throw new RuntimeException("object is null");
        }
        Field field = ReflectProvider.findField(clazz, fieldName);
        if (field == null) {
            throw new RuntimeException("field is null");
        }
        fieldCheck.check(field, ReflectProvider.getFieldValue(field, object));
    }

    // 是否可以检查 非最终修饰与静态修饰的字段是可检查字段
    private final static FieldIgnore CAN_CHECK_FIELD =
            field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
}

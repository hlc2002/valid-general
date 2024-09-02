package valid.support.custom;

import valid.annotion.InnerClassValid;
import valid.build.FieldCheckBuilder;
import valid.build.FieldTypeValidRule;
import valid.support.genaral.AbstractGeneralValidSupport;
import valid.tools.ReflectProvider;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author spring
 * @since 2024/9/2 13:39:48
 * @apiNote 抽象的自定义校验实现
 * @version 1.0
 */
@SuppressWarnings("all")
public class AbstractCustomValidSupport extends AbstractGeneralValidSupport implements CustomValid {
    @Override
    public <T> void valid(Object object, Class<T> clazz) {
        valid(object, clazz, new String[0]);
    }

    @Override
    public <T> void valid(Object object, Class<T> clazz, String... ignoreFieldNames) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        List<String> ignoreFieldNameList = Arrays.asList(ignoreFieldNames);
        Map<String, FieldTypeValidRule> ruleMap = FieldCheckBuilder.buildRuleMap(clazz);
        ReflectProvider.doWithLocalFields(clazz, field -> {
            if (ignoreFieldNameList.contains(field.getName())) {
                return;
            }
            ReflectProvider.makeAccessible(field);
            InnerClassValid innerClassValid = field.getType().getAnnotation(InnerClassValid.class);
            if (innerClassValid != null && innerClassValid.enable()) {
                valid((T) object, field.getType());
            } else {
                validFieldByRuleMap(object, field, ruleMap);
            }
        });
    }

    private static void validFieldByRuleMap(Object object, Field field, Map<String, FieldTypeValidRule> ruleMap) {
        FieldTypeValidRule rule = ruleMap.get(field.getName());
        if (rule != null) {
            Object fieldValue = ReflectProvider.getFieldValue(field, object);
            if (!rule.getNullable() && fieldValue == null) {
                throw new IllegalArgumentException(field.getName() + " is not nullable");
            }
            if (rule.getValueRange() != null) {
                if ((Long) fieldValue > rule.getValueRange().getMax() || (Long) fieldValue < rule.getValueRange().getMin()) {
                    throw new IllegalArgumentException(field.getName() + " is not in [" + rule.getValueRange().getMin() + "," + rule.getValueRange().getMax() + "]");
                }
            }
            if (rule.getArrayLengthRange() != null) {
                if (fieldValue instanceof Collection) {
                    Collection collection = (Collection) fieldValue;
                    if (collection.size() > rule.getArrayLengthRange().getMax() || collection.size() < rule.getArrayLengthRange().getMin()) {
                        throw new IllegalArgumentException(field.getName() + " size is not in [" + rule.getArrayLengthRange().getMin() + "," + rule.getArrayLengthRange().getMax() + "]");
                    }
                }
            }
        }
    }
}

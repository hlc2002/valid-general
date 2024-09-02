package valid.support.custom;

import valid.build.FieldCheckBuilder;
import valid.build.FieldTypeValidRule;
import valid.support.genaral.AbstractGeneralValidSupport;
import valid.tools.ReflectProvider;

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
    public void valid(Object object, Class<?> clazz) {
        valid(object, clazz, new String[0]);
    }

    @Override
    public void valid(Object object, Class<?> clazz, String... ignoreFieldNames) {
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
            FieldTypeValidRule rule = ruleMap.get(field.getName());
            if (rule != null) {
                Object fieldValue = ReflectProvider.getFieldValue(field, object);
                if (!rule.getNullable() && fieldValue == null) {
                    throw new IllegalArgumentException(field.getName() + " is not nullable");
                }
                if (rule.getValueRange() != null) {
                    if ((Integer) fieldValue > (Integer) rule.getValueRange().getMax() || (Integer) fieldValue < (Integer) rule.getValueRange().getMin()) {
                        throw new IllegalArgumentException(field.getName() + " is not in range");
                    }
                }
                if (rule.getArrayLengthRange() != null) {
                    if (fieldValue instanceof Collection) {
                        Collection collection = (Collection) fieldValue;
                        if (collection.size() > rule.getArrayLengthRange().getMax() || collection.size() < rule.getArrayLengthRange().getMin()) {
                            throw new IllegalArgumentException(field.getName() + " is not in array length range");
                        }
                    }
                }
            }
        });
    }
}

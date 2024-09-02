package valid.build;

import valid.annotion.NotNull;
import valid.annotion.SizeRange;
import valid.annotion.ValueRange;
import valid.tools.ReflectProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author spring
 * @since 2024/9/2 13:37:41
 * @apiNote 字段检查模型构建器
 * @version 1.0
 */
public class FieldCheckBuilder {

    public static Map<String, FieldTypeValidRule> buildRuleMap(Class<?> objClazz) {
        Map<String, FieldTypeValidRule> ruleMap = new HashMap<>();
        buildRuleMap(objClazz, ruleMap);
        return ruleMap;
    }


    private static void buildRuleMap(Class<?> clazz, Map<String, FieldTypeValidRule> ruleMap) {
        if (clazz == null) {
            throw new IllegalArgumentException("class is null");
        }
        ReflectProvider.doWithLocalFields(clazz, field -> {
            if (ruleMap.containsKey(field.getName())) {
                throw new IllegalArgumentException(clazz.getSimpleName() + ": " + field.getName() + " exist the same name field");
            }
            FieldTypeValidRule rule = buildRule(field);
            ruleMap.put(field.getName(), rule);
        });
    }

    private static FieldTypeValidRule buildRule(Field field) {
        FieldTypeValidRule rule = new FieldTypeValidRule();
        rule.setFieldName(field.getName());
        rule.setType(field.getType());
        rule.setNullable(NULLABLE.check(field));

        // fixme 这里如何确认内部引用类型字段，而不是jdk内部的包装类型？仅支持确认该类型下的内部类
        if (isInnerClass(field.getType())) {
            Map<String, FieldTypeValidRule> childRuleMap = buildRuleMap(field.getDeclaringClass());
            rule.setChildRuleMap(childRuleMap);
        }


        SizeRange sizeRange = field.getAnnotation(SizeRange.class);
        if (sizeRange != null) {
            FieldTypeValidRule.ArrayLengthRange arrayLengthRange = new FieldTypeValidRule.ArrayLengthRange();
            arrayLengthRange.setMax(sizeRange.max());
            arrayLengthRange.setMin(sizeRange.min());
            rule.setArrayLengthRange(arrayLengthRange);
        }

        ValueRange valueRange = field.getAnnotation(ValueRange.class);
        if (valueRange != null) {
            FieldTypeValidRule.ValueRange valueRange1 = new FieldTypeValidRule.ValueRange();
            valueRange1.setMax(valueRange.max());
            valueRange1.setMin(valueRange.min());
            rule.setValueRange(valueRange1);
        }
        return rule;
    }


    private static boolean isInnerClass(Class<?> clazzB) {
        return clazzB.isAnonymousClass();
    }

    private static final Check NULLABLE = (field) -> {
        NotNull annotation = field.getAnnotation(NotNull.class);
        return annotation == null;
    };


    @FunctionalInterface
    public interface Check {
        boolean check(Field field);
    }

}

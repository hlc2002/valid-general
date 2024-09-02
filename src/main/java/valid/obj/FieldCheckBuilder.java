package valid.obj;

import valid.support.FieldValidRuleInit;
import valid.tools.ReflectProvider;

import java.util.Map;

/**
 * @author spring
 * @since 2024/9/2 13:37:41
 * @apiNote 字段检查模型构建器
 * @version 1.0
 */
public class FieldCheckBuilder {
    private Map<String, FieldTypeValidRule> fieldTypeValidRuleMap;

    public static FieldCheckBuilder build(Class<?> objClazz) {
        FieldCheckBuilder fieldCheckBuilder = new FieldCheckBuilder();
        ReflectProvider.doWithLocalFields(objClazz, field -> {

        });
        return fieldCheckBuilder;
    }
}

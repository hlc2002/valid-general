package valid.test;

import valid.build.FieldCheckBuilder;
import valid.build.FieldTypeValidRule;

import java.util.Map;

/**
 * @author spring
 * @since 2024/9/2 15:10:56
 * @apiNote
 * @version 1.0
 */
public class TestStarter {
    public static void main(String[] args) {
        Map<String, FieldTypeValidRule> ruleMap = FieldCheckBuilder.buildRuleMap(QueryDataRequest.class);
        for (Map.Entry<String, FieldTypeValidRule> entry : ruleMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}

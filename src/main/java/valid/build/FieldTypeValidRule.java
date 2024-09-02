package valid.build;

import java.util.Map;

/**
 * @author spring
 * @since 2024/9/2 13:52:42
 * @apiNote
 * @version 1.0
 */
public class FieldTypeValidRule {
    private String fieldName;
    private Class<?> type;
    private boolean nullable;
    private Map<String, FieldTypeValidRule> childRuleMap;
    private ValueRange valueRange;
    private ArrayLengthRange arrayLengthRange;

    public static class ValueRange {
        private Object min;
        private Object max;

        public Object getMin() {
            return min;
        }

        public void setMin(Object min) {
            this.min = min;
        }

        public Object getMax() {
            return max;
        }

        public void setMax(Object max) {
            this.max = max;
        }

        @Override
        public String toString() {
            return "ValueRange{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }

    public static class ArrayLengthRange {
        private int min;
        private int max;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        @Override
        public String toString() {
            return "ArrayLengthRange{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }

    public FieldTypeValidRule() {
    }

    public FieldTypeValidRule(String fieldName, Class<?> clazz, boolean nullable, ValueRange valueRange, ArrayLengthRange arrayLengthRange) {
        this.fieldName = fieldName;
        this.type = clazz;
        this.nullable = nullable;
        this.valueRange = valueRange;
        this.arrayLengthRange = arrayLengthRange;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean getNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public ValueRange getValueRange() {
        return valueRange;
    }

    public void setValueRange(ValueRange valueRange) {
        this.valueRange = valueRange;
    }

    public ArrayLengthRange getArrayLengthRange() {
        return arrayLengthRange;
    }

    public void setArrayLengthRange(ArrayLengthRange arrayLengthRange) {
        this.arrayLengthRange = arrayLengthRange;
    }

    public Map<String, FieldTypeValidRule> getChildRuleMap() {
        return childRuleMap;
    }

    public void setChildRuleMap(Map<String, FieldTypeValidRule> childRuleMap) {
        this.childRuleMap = childRuleMap;
    }

    @Override
    public String toString() {
        return "FieldTypeValidRule{" +
                "fieldName='" + fieldName + '\'' +
                ", type=" + type +
                ", nullable=" + nullable +
                ", childRuleMap=" + childRuleMap +
                ", valueRange=" + valueRange +
                ", arrayLengthRange=" + arrayLengthRange +
                '}';
    }
}

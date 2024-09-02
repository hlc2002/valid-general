package valid.support.genaral;

/**
 * @author spring
 * @since 2024/9/2 11:11:16
 * @apiNote 无定制校验处理的通用校验器
 * @version 1.0
 */
public interface GeneralValid {
    /**
     * 对象字段非空校验
     * @param object 实例对象（取值用）
     * @param clazz 实例对象真实类型的字节码（分析字段用）
     */
    void validObjectFieldNotNull(Object object, Class<?> clazz);

    /**
     * 对象字段非空校验
     * @param object 实例对象（取值用）
     * @param clazz 实例对象真实类型的字节码（分析字段用）
     * @param objectCheck 是否校验实例对象本身
     */
    void validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck);

    /**
     * 对象字段非空校验
     * @param object 实例对象（取值用）
     * @param clazz 实例对象真实类型的字节码（分析字段用）
     * @param objectCheck 是否校验实例对象本身
     * @param ignoreFieldNames 忽略校验的字段名
     */
    void validObjectFieldNotNull(Object object, Class<?> clazz, boolean objectCheck, String... ignoreFieldNames);

    /**
     * 对象字段校验
     * @param object 实例对象（取值用）
     * @param clazz 实例对象真实类型的字节码（分析字段用）
     * @param fieldCheck 字段校验器
     * @param fieldIgnore 字段忽略器
     */
    void validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore);

    /**
     * 对象字段校验
     * @param object 实例对象（取值用）
     * @param clazz 实例对象真实类型的字节码（分析字段用）
     * @param fieldCheck 字段校验器
     * @param fieldIgnore 字段忽略器
     * @param ignoreFieldNames 忽略校验的字段名
     */
    void validObjectField(Object object, Class<?> clazz, FieldCheck fieldCheck, FieldIgnore fieldIgnore, String... ignoreFieldNames);

    /**
     * 单个字段校验
     * @param field 字段
     * @param object 实例对象（取值用）
     * @param fieldCheck 字段校验器
     */
    void validSingleField(String field, Object object, Class<?> clazz, FieldCheck fieldCheck);
}

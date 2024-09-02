package valid.tools;

import jakarta.annotation.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author spring
 * @version 1.0
 * @apiNote 反射提供者
 * @since 2024/9/2 11:11:26
 */
@SuppressWarnings("all")
public abstract class ReflectProvider {

    private static final Method[] NO_METHODS = {};
    private static final Field[] NO_FIELDS = {};
    // 缓存类与方法，采用线程安全的弱引用缓存
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap<>(256);
    // 缓存类与字段，采用线程安全的弱引用缓存
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap<>(256);

    /**
     * 根据字节码与字段名获取字段
     *
     * @param clazz     字节码
     * @param fieldName 字段名称
     * @return 字段
     */
    @Nullable
    public static Field findField(Class<?> clazz, String fieldName) {
        return findField(clazz, fieldName, null);
    }

    /**
     * 根据字节码和字段名称或类型获取字段
     *
     * @param clazz     字节码
     * @param fieldName 字段名称
     * @param type      字段类型
     * @return 字段
     */
    @Nullable
    public static Field findField(Class<?> clazz, String fieldName, Class<?> type) {
        if (clazz == null)
            throw new IllegalArgumentException("clazz must be not null");
        if (fieldName == null && type == null)
            throw new IllegalArgumentException("either fieldName or type of the field must be not null");
        Class<?> searchType = clazz;
        // 向上查找时，查找到父类是基类时停止查找返回null即可
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(clazz);
            for (Field field : fields) {
                if (fieldName == null || fieldName.equals(field.getName()))
                    return field;
                if (type == null || type.equals(field.getType()))
                    return field;
            }
            // 当前类型中不存在此字段则向上寻找（即可能存在父类中）
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 获取字节码的全部字段
     *
     * @param clazz 字节码
     * @return 字段集合
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("clazz must be not null");
        Field[] cachedFields = declaredFieldsCache.get(clazz);
        if (cachedFields == null || cachedFields.length == 0) {
            try {
                cachedFields = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, cachedFields.length == 0 ? NO_FIELDS : cachedFields);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to introspect Class [" +
                        clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
            }
        }
        return cachedFields;
    }

    /**
     * 设置实例中字段的值
     *
     * @param field 字段
     * @param obj   对象
     * @param value 值
     */
    public static void setFieldValue(Field field, @Nullable Object obj, @Nullable Object value) {
        if (field == null)
            throw new IllegalArgumentException("field must be not null");
        if (CAN_COPY_FIELD.matches(field)) {
            try {
                field.setAccessible(true);
                field.set(obj, value);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to set field [" +
                        field.getName() + "] value [" + value + "]", throwable);
            }
        } else
            throw new IllegalStateException("Field [" +
                    field.getName() + "] is not accessible or field modifier is final or static");
    }

    /**
     * 获取实例中字段的值
     *
     * @param field 字段
     * @param obj   对象
     * @return 值
     */
    public static Object getFieldValue(Field field, @Nullable Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to get field [" + field.getName() + "] value", e);
        }
    }

    /**
     * 根据字节码与方法名获取方法
     *
     * @param clazz      字节码
     * @param methodName 方法名
     * @return 方法
     */
    public static Method findMethod(Class<?> clazz, String methodName) {
        return findMethod(clazz, methodName, new Class<?>[0]);
    }

    /**
     * 根据字节码、方法名与参数类型获取方法
     *
     * @param clazz      字节码
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法
     */
    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
        if (clazz == null)
            throw new IllegalArgumentException("clazz must be not null");
        if (methodName == null)
            throw new IllegalArgumentException("methodName must be not null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType);
            for (Method method : methods) {
                if (methodName.equals(method.getName())
                        && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 获取字节码的全部方法
     *
     * @param clazz 字节码
     * @return 方法集合
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("clazz must be not null");

        Method[] cachedMethods = declaredMethodsCache.get(clazz);
        if (cachedMethods == null) {
            try {
                // 获取当前类声明的方法
                Method[] declaredMethods = clazz.getDeclaredMethods();
                // 获取当前类实现的接口方法
                List<Method> methodsByInterfaces = findMethodsByInterfaces(clazz);
                if (methodsByInterfaces != null) {
                    // 合并方法集合
                    cachedMethods = new Method[declaredMethods.length + methodsByInterfaces.size()];
                    // fixme 控制指针，优化合并效率，目前会浪费资源，当两个集合长度不相等时，多余的M-N的复杂度操作短集合的指针与逻辑判断
                    for (int i = 0, j = 0; i < declaredMethods.length || j < methodsByInterfaces.size(); i++, j++) {
                        if (i < declaredMethods.length) {
                            cachedMethods[i] = declaredMethods[i];
                        }
                        if (j < methodsByInterfaces.size()) {
                            cachedMethods[declaredMethods.length + j] = methodsByInterfaces.get(j);
                        }
                    }
                } else {
                    cachedMethods = declaredMethods;
                }
                declaredMethodsCache.put(clazz, cachedMethods.length == 0 ? NO_METHODS : cachedMethods);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to introspect Class [" +
                        clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
            }
        }
        return cachedMethods;
    }

    /**
     * 获取字节码实现的接口方法
     *
     * @param clazz 字节码
     * @return 方法集合
     */
    @Nullable
    public static List<Method> findMethodsByInterfaces(Class<?> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("clazz must be not null");
        List<Method> methodList = null;
        for (Class<?> interfaceClazz : clazz.getInterfaces()) {
            for (Method method : interfaceClazz.getMethods()) {
                if (!Modifier.isAbstract(method.getModifiers())) {
                    if (methodList == null)
                        methodList = new ArrayList<>();
                    methodList.add(method);
                }
            }
        }
        return methodList;
    }

    /**
     * 调用实例的某个方法
     *
     * @param method 方法
     * @param obj    实例
     * @return 返回值
     */
    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object obj) {
        return invokeMethod(method, obj, new Object[0]);
    }

    /**
     * 调用实例的某个方法
     *
     * @param method 方法
     * @param obj    实例
     * @param args   参数
     * @return 返回值
     */
    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object obj, @Nullable Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to invoke method [" + method.getName() + "]", throwable);
        }
    }

    /**
     * 设置字段可访问
     *
     * @param field 字段
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 设置方法可访问
     *
     * @param method 方法
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 设置构造方法可访问
     *
     * @param ctor 构造方法
     */
    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * 获取可访问的构造方法
     *
     * @param clazz      字节码
     * @param paramTypes 参数类型
     * @param <T>        返回类型
     * @return 构造方法
     * @throws NoSuchMethodException 异常
     */
    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... paramTypes)
            throws NoSuchMethodException {
        Constructor<T> ctor = clazz.getDeclaredConstructor(paramTypes);
        makeAccessible(ctor);
        return ctor;
    }

    /**
     * 执行方法列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     */
    public static void doWithLocalMethods(Class<?> clazz, MethodCallBack callBack) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            try {
                callBack.doWith(method);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to introspect Class [" +
                        clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
            }
        }
    }

    /**
     * 执行方法列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     */
    public static void doWithMethods(Class<?> clazz, MethodCallBack callBack) {
        doWithMethods(clazz, callBack, null);
    }

    /**
     * 执行方法列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     * @param filter   过滤器
     */
    public static void doWithMethods(Class<?> clazz, MethodCallBack callBack, @Nullable MethodFilter filter) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (filter != null || !filter.matches(method))
                continue;
            try {
                callBack.doWith(method);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to introspect Class [" +
                        clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
            }
            if (clazz.getSuperclass() != null)
                doWithMethods(clazz.getSuperclass(), callBack, filter);
            else if (clazz.isInterface())
                for (Class<?> superInterface : clazz.getInterfaces())
                    doWithMethods(superInterface, callBack, filter);
        }
    }

    /**
     * 执行字段列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     */
    public static void doWithLocalFields(Class<?> clazz, FieldCallBack callBack) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                callBack.doWith(field);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Failed to introspect Class [" +
                        clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
            }
        }
    }

    /**
     * 执行字段列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     */
    public static void doWithFields(Class<?> clazz, FieldCallBack callBack) {
        doWithFields(clazz, callBack, null);
    }

    /**
     * 执行字段列表的规则
     *
     * @param clazz    字节码
     * @param callBack 规则
     * @param filter   过滤器
     */
    public static void doWithFields(Class<?> clazz, FieldCallBack callBack, FieldFilter filter) {
        Class<?> operateType = clazz;
        do {
            Field[] fields = getDeclaredFields(operateType);
            for (Field field : fields) {
                if (filter != null && !filter.matches(field))
                    continue;
                try {
                    callBack.doWith(field);
                } catch (Throwable throwable) {
                    throw new IllegalStateException("Failed to introspect Class [" +
                            clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", throwable);
                }
            }
            operateType = operateType.getSuperclass();
        } while (operateType != null && operateType != Object.class);
    }

    /**
     * 浅拷贝
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void shallowCopy(final Object source, final Object target) {
        // 要求source中的字段在target中一定存在
        if (!source.getClass().isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("source and target must be same class or sub class");
        doWithFields(source.getClass(), field -> {
            // 设置字段权限
            makeAccessible(field);
            // 获取字段值
            Object fieldValue = getFieldValue(field, source);
            // 设置字段值
            setFieldValue(field, target, fieldValue);
        }, CAN_COPY_FIELD);
    }

    // 默认可复制字段的字段过滤器 非静态与最终修饰的字段
    public static final FieldFilter CAN_COPY_FIELD =
            field -> (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()));
    // 默认人为编写的方法 非编译产生的方法
    public static final MethodFilter PERSON_WRITE_METHOD =
            method -> !method.isBridge();
    // 默认非桥接方法 非Object类声明的方法
    public static final MethodFilter PERSON_DECLARED_METHOD =
            method -> PERSON_WRITE_METHOD.matches(method) && (method.getDeclaringClass() != Object.class);


    @FunctionalInterface
    public interface MethodFilter {
        /**
         * 确定给定的方法是否匹配，匹配规则在函数中自行定义
         */
        boolean matches(Method method);
    }

    @FunctionalInterface
    public interface MethodCallBack {
        /**
         * 使用给定的方法执行一个操作，操作在函数中自行定义
         */
        void doWith(Method method);
    }

    @FunctionalInterface
    public interface FieldCallBack {
        /**
         * 使用给定的字段执行一个操作，操作在函数中自行定义
         */
        void doWith(Field field);
    }

    @FunctionalInterface
    public interface FieldFilter {
        /**
         * 确定给定的字段是否匹配，匹配规则在函数中自行定义
         */
        boolean matches(Field field);
    }
}

package references;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public class FieldRefQuery {
    public static <T> SelectBuilder<T> select(FieldRef<T> field) {
        return new SelectBuilder<>(field);
    }

    public static <T> UpdateBuilder<T> update(Class<?> clazz) {
        return new UpdateBuilder<>(clazz);
    }

    public static <F> FieldRef<F> field(Class<?> classClazz, String fieldName, Class<F> fieldClass) {
        return new FieldRef<>(classClazz, fieldName, fieldClass);
    }

    public static class Predicate<T> {
        private final FieldRef<T> field;
        private final PredicateType predicateType;
        private final Object value;

        public Predicate(FieldRef<T> field, PredicateType predicateType, T value) {
            this.field = field;
            this.predicateType = predicateType;
            this.value = value;
        }

        public static <V> Predicate<V> eq(FieldRef<V> field, V value) {
            return new Predicate<>(field, PredicateType.eq, value);
        }

        @Override
        public String toString() {
            return field.getName() + predicateType.getOp() + toValue(value);
        }

        private enum PredicateType {
            eq("="), lt("<"), lte("<="), gt(">"), gte(">=");

            private final String op;

            PredicateType(String op) {
                this.op = op;
            }

            public String getOp() {
                return op;
            }
        }
    }

    public static String toValue(Object value) {
        return value instanceof String ? "'" + value + "'" : String.valueOf(value);
    }

    public static class SelectBuilder<T> {
        private FieldRef<T> field;
        private Class<?> clazz;
        private Predicate<?>[] predicates;

        public SelectBuilder(FieldRef<T> field) {
            this.field = field;
        }

        public SelectBuilder<T> from(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public SelectBuilder<T> where(Predicate<?>... predicate) {
            this.predicates = predicate;
            return this;
        }

        public T executeOn(Consumer<String> consumer) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT ");
            stringBuilder.append(field.getName());
            stringBuilder.append(" FROM ");
            stringBuilder.append(clazz.getSimpleName());

            if (predicates != null && predicates.length != 0) {
                stringBuilder.append(" WHERE ");
                stringBuilder.append(Arrays.stream(predicates).map(Predicate::toString).collect(joining(" AND ")));
            }

            consumer.accept(stringBuilder.toString());

            return null;
        }
    }

    public static class UpdateBuilder<T> {
        private Class<?> clazz;
        private Predicate<?>[] predicates;
        private Map<FieldRef<?>, Object> fieldObjectMap = new LinkedHashMap<>();

        public UpdateBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        public <V> UpdateBuilder<T> set(FieldRef<V> name, V value) {
            fieldObjectMap.put(name, value);
            return this;
        }

        public UpdateBuilder<T> where(Predicate<?>... predicate) {
            this.predicates = predicate;
            return this;
        }

        public void executeOn(Consumer<String> consumer) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE ");
            stringBuilder.append(clazz.getSimpleName());
            stringBuilder.append(" SET ");
            stringBuilder.append(fieldObjectMap.entrySet().stream()
                    .map(kv -> kv.getKey().getName() + "=" + toValue(kv.getValue()))
                    .collect(joining(",")));

            if (predicates != null && predicates.length != 0) {
                stringBuilder.append(" WHERE ");
                stringBuilder.append(Arrays.stream(predicates).map(Predicate::toString).collect(joining(" AND ")));
            }

            consumer.accept(stringBuilder.toString());
        }
    }

    public static class FieldRef<F> {
        private final Class<?> enclosingClass;
        private final String fieldName;
        private final Class<F> fieldClass;

        public FieldRef(Class<?> enclosingClass, String fieldName, Class<F> fieldClass) {
            this.enclosingClass = enclosingClass;
            this.fieldName = fieldName;
            this.fieldClass = fieldClass;
        }

        public String getName() {
            return fieldName;
        }
    }
}

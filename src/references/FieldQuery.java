package references;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public class FieldQuery {
    public static SelectBuilder select(Field field) {
        return new SelectBuilder(field);
    }

    public static UpdateBuilder update(Class<?> clazz) {
        return new UpdateBuilder(clazz);
    }

    public static class Predicate {
        private final Field field;
        private final PredicateType predicateType;
        private final Object value;

        public Predicate(Field field, PredicateType predicateType, Object value) {
            this.field = field;
            this.predicateType = predicateType;
            this.value = value;
        }

        static Predicate eq(Field field, Object value) {
            return new Predicate(field, PredicateType.eq, value);
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

    public static class SelectBuilder {
        private Field field;
        private Class<?> clazz;
        private Predicate[] predicates;

        public SelectBuilder(Field field) {
            this.field = field;
        }

        public SelectBuilder from(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public SelectBuilder where(Predicate... predicate) {
            this.predicates = predicate;
            return this;
        }

        public Object executeOn(Consumer<String> consumer) {
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

    public static class UpdateBuilder {
        private Class<?> clazz;
        private Predicate[] predicates;
        private Map<Field, Object> fieldObjectMap = new LinkedHashMap<>();

        public UpdateBuilder(Class<?> clazz) {
            this.clazz = clazz;
        }

        public UpdateBuilder set(Field name, Object value) {
            fieldObjectMap.put(name, value);
            return this;
        }

        public UpdateBuilder where(Predicate... predicate) {
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
}

package references;

import references.example.Person;

import static references.FieldRefQuery.Predicate.eq;
import static references.FieldRefQuery.field;
import static references.FieldRefQuery.select;
import static references.FieldRefQuery.update;

/**
 * Will print
 *
 * SELECT name FROM Person WHERE id=100
 * UPDATE Person SET name='John' WHERE id=100
 */
public class FieldRefQueryMain {
    public static void main(String[] args) throws Exception {
        /*
        String name = select(Person::name)
                .from(Person.class)
                .where(eq(Person::id, 100L))
                .executeOn(System.out::println);

        update(Person.class)
                .set(Person::name, "John")
                .where(eq(Person::id, 100L))
                .executeOn(System.out::println);
        */

        String name = select(field(Person.class, "name", String.class))
                .from(Person.class)
                .where(eq(field(Person.class,"id", Long.class), 100L))
                .executeOn(System.out::println);

        update(Person.class)
                .set(field(Person.class, "name", String.class), "John")
                .where(eq(field(Person.class,"id", Long.class), 100L))
                .executeOn(System.out::println);
    }
}

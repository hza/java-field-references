package references;

import references.example.Person;

import static references.FieldQuery.Predicate.eq;
import static references.FieldQuery.select;
import static references.FieldQuery.update;

/**
 * Will print
 *
 * SELECT name FROM Person WHERE id=100
 * UPDATE Person SET name='John' WHERE id=100
 */
public class FieldQueryMain {
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

        Object name = select(Person.class.getDeclaredField("name"))
                .from(Person.class)
                .where(eq(Person.class.getDeclaredField("id"), 100L))
                .executeOn(System.out::println);

        update(Person.class)
                .set(Person.class.getDeclaredField("name"), "John")
                .where(eq(Person.class.getDeclaredField("id"), 100L))
                .executeOn(System.out::println);
    }
}

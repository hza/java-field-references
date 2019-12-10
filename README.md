Field References in Java
===

In Java version 8 were introduced method references in the format SomeClass::method. Field references do not
exist in the language, but would be useful for compile-time checked, type-safe queries to 
collections, in-memory and external SQL and noSQL databases.

Allowing syntax like:

```java
    Field<String> field = Person::name;
```

it will be easy to write queries to entities:

```java
    class Person { long id; String name; }

    String name = Query.select(Person::name)
        .from(Person.class)
        .where(eq(Person::id, 100L))
        .executeOn(database);
```
 
To keep things simple, constructions like `Person::name` may be just macros to `Person.class.getDeclaredField("name")` 
and `java.lang.reflect.Field` has to be declared as generic type only. 

To check this idea `FieldQueryMain` is a demo using constructions like `Person.class.getDeclaredField("name")`. It is type unsafe.

`FieldRefQueryMain` uses `FieldRef<T>` and is safe. FieldRef<T> is required 
in the example because it is impossible to add generic <T> to the java.lang.reflect.Field code.

Current approach for java libraries that work with databases (hibernate, jOOq) is to 
generate code for type-safety in query builders. Field References can improve coding 
experience at least for simple queries and allowing write java code instead of writing unchecked 
queries in String and multi-line strings.

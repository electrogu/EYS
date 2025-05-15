
package config;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @param <K>
 * @param <V>
 * 
 * A class to represent key-value pairs including any Object. Implements Serializable class.
 */
public class Pair<K extends Object, V extends Object> implements Serializable {

    @Retention(value = RetentionPolicy.RUNTIME) // Retention annotation for value
    @Target(value = {ElementType.PARAMETER}) // Target annotation for value
    
    public @interface NamedArg {

        public String value();

        public String defaultValue() default "";
    }

    private K key;
    private V value;

    // Returning the key of stored Pair
    public K getKey() {
        return key;
    }

    // Returning the value of stored Pair
    public V getValue() {
        return value;
    }

    // Constructor for the class, storing the Pair of different Objects.
    public Pair(@NamedArg(value = "key") K arg0, @NamedArg(value = "value") V arg1) {
        key = arg0;
        value = arg1;
    }
}

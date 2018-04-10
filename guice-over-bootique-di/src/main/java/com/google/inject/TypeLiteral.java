/*
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject;

import static java.util.Objects.requireNonNull;

import com.google.inject.internal.MoreTypes;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a generic type {@code T}. Java doesn't yet provide a way to represent generic types,
 * so this class does. Forces clients to create a subclass of this class which enables retrieval of
 * the type information even at runtime.
 *
 * <p>For example, to create a type literal for {@code List<String>}, you can create an empty
 * anonymous inner class:
 *
 * <p>{@code TypeLiteral<List<String>> list = new TypeLiteral<List<String>>() {};}
 *
 * <p>Along with modeling generic types, this class can resolve type parameters. For example, to
 * figure out what type {@code keySet()} returns on a {@code Map<Integer, String>}, use this code:
 *
 * <pre>{@code
 * TypeLiteral<Map<Integer, String>> mapType
 *     = new TypeLiteral<Map<Integer, String>>() {};
 * TypeLiteral<?> keySetType
 *     = mapType.getReturnType(Map.class.getMethod("keySet"));
 * System.out.println(keySetType); // prints "Set<Integer>"
 * }</pre>
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
public class TypeLiteral<T> {

    private final Class<? super T> rawType;
    final Type type;

    /**
     * Constructs a new type literal. Derives represented class from type parameter.
     *
     * <p>Clients create an empty anonymous subclass. Doing so embeds the type parameter in the
     * anonymous class's type hierarchy so we can reconstitute it at runtime despite erasure.
     */
    @SuppressWarnings("unchecked")
    protected TypeLiteral() {
        this.type = getSuperclassTypeParameter(getClass());
        this.rawType = (Class<? super T>) MoreTypes.getRawType(type);
    }

    /**
     * Unsafe. Constructs a type literal manually.
     */
    @SuppressWarnings("unchecked")
    private TypeLiteral(Type type) {
        this.type = requireNonNull(type, "type");
        this.rawType = (Class<? super T>) MoreTypes.getRawType(this.type);
    }

    /**
     * Returns the type from super class's type parameter.
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    /**
     * Returns the raw (non-generic) type for this type.
     *
     * @since 2.0
     */
    public final Class<? super T> getRawType() {
        return rawType;
    }

    /**
     * Gets underlying {@code Type} instance.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Gets type literal for the given {@code Type} instance.
     */
    public static TypeLiteral<?> get(Type type) {
        return new TypeLiteral<>(type);
    }

    /**
     * Gets type literal for the given {@code Class} instance.
     */
    public static <T> TypeLiteral<T> get(Class<T> type) {
        return new TypeLiteral<>(type);
    }

}
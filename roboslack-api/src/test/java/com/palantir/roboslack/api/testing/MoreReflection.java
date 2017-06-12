/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.roboslack.api.testing;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.function.ThrowingConsumer;

public final class MoreReflection {

    private MoreReflection() {}

    public static boolean fieldIsParameterizedType(Field field,
            Class<?> rawType, Class<?>... typeArguments) {
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        List<Type> parameterizedTypeArgs = Arrays.asList(parameterizedType.getActualTypeArguments());
        return parameterizedType.getRawType().equals(rawType)
                && Arrays.stream(typeArguments).allMatch(parameterizedTypeArgs::contains);
    }

    public static Object getStaticFieldValue(Field field) {
        try {
            // Only need to pass null for static fields
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Unable to access field: %s", field));
        }
    }

    public static Stream<Method> findNoArgStaticFactories(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers())
                        && method.getReturnType().equals(clazz)
                        && method.getParameterCount() == 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> ThrowingConsumer<Method> noArgStaticFactoryConsumer(Consumer<T> delegateConsumer) {
        return staticFactoryMethod -> delegateConsumer.accept((T) staticFactoryMethod.invoke(null));
    }

}

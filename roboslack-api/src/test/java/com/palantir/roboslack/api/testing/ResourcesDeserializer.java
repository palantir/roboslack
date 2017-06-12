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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.palantir.remoting2.ext.jackson.ObjectMappers;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public final class ResourcesDeserializer {

    private static final ObjectMapper MAPPER = ObjectMappers.newClientObjectMapper();

    private ResourcesDeserializer() {}

    private static <T> T readValueOrThrow(Class<T> clazz, File file) {
        try {
            return MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> Stream<T> deserialize(Class<T> clazz,
            String resourcesDirectory) throws Exception {
        File directory = new File(Resources.getResource(resourcesDirectory).getFile());
        checkArgument(directory.isDirectory(), "%s is not a directory", resourcesDirectory);
        File[] files = directory.listFiles();
        checkNotNull(files, "No files found in '%s', or I/O exception occurred", resourcesDirectory);
        return Stream.of(files)
                .filter(File::isFile)
                .map(file -> readValueOrThrow(clazz, file));
    }

}

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.palantir.roboslack.jackson.ObjectMappers;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public final class ResourcesReader {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMappers.newObjectMapper();

    private ResourcesReader() {}

    private static Stream<File> listFiles(String resourcesDirectory) {
        File directory = new File(Resources.getResource(resourcesDirectory).getFile());
        checkArgument(directory.isDirectory(), "%s is not a directory", resourcesDirectory);
        File[] files = directory.listFiles();
        checkNotNull(files, "No files found in '%s', or I/O exception occurred", resourcesDirectory);
        return Stream.of(files).filter(File::isFile);
    }

    private static String readString(File file) {
        try {
            return Joiner.on(System.lineSeparator())
                    .join(Files.readLines(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static JsonNode readJson(File file) {
        try {
            return OBJECT_MAPPER.readTree(readString(file));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Stream<JsonNode> readJson(String resourcesDirectory) throws Exception {
        return listFiles(resourcesDirectory).map(ResourcesReader::readJson);
    }

}

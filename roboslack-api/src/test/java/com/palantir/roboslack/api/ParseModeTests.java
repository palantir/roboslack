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

package com.palantir.roboslack.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ParseModeTests {

    private static boolean isLowerCase(String string) {
        return string.chars().mapToObj(i -> (char) i).allMatch(Character::isLowerCase);
    }

    @ParameterizedTest
    @EnumSource(ParseMode.class)
    void testParseModeToString(ParseMode parseMode) {
        assertTrue(isLowerCase(parseMode.toString()));
    }

    @ParameterizedTest
    @EnumSource(ParseMode.class)
    void testOfCaseInsensitive(ParseMode parseMode) {
        assertThat(ParseMode.of(parseMode.toString().toUpperCase()), is(equalTo(parseMode)));
    }

}

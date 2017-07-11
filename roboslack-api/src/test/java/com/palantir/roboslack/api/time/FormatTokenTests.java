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

package com.palantir.roboslack.api.time;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Strings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FormatTokenTests {

    @ParameterizedTest
    @EnumSource(FormatToken.class)
    void testFormatTokenToString(FormatToken formatToken) {
        assertTrue(formatToken.toString().startsWith("{"));
        assertTrue(formatToken.toString().endsWith("}"));
    }

    @ParameterizedTest
    @EnumSource(FormatToken.class)
    void testFormatTokenPattern(FormatToken formatToken) {
        assertFalse(Strings.isNullOrEmpty(formatToken.pattern()));
    }

}
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

package com.palantir.roboslack.webhook.api.model;


import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WebHookTokenTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX",
            "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX"})
    void testFromString(String input) {
        WebHookToken token = WebHookToken.fromString(input);
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partB()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partT()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partX()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.toString()));
    }

    @Test
    void testFromEnvironment() {
        List<String> keysMissingFromEnvironment = WebHookToken.Env.missingTokenKeys();
        Assumptions.assumeTrue(keysMissingFromEnvironment.isEmpty(),
                () -> String.format("Skipping test, environment keys not found: [%s]",
                        Joiner.on(", ").join(keysMissingFromEnvironment)));
        WebHookToken token = WebHookToken.fromEnvironment();
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partB()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partT()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.partX()));
        Assertions.assertFalse(Strings.isNullOrEmpty(token.toString()));
    }

}

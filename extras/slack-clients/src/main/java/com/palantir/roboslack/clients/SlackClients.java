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

package com.palantir.roboslack.clients;

import com.palantir.roboslack.jackson.ObjectMappers;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Utility class for generating service clients for RPC calls to Slack (intended for internal use only).
 */
public final class SlackClients {

    private SlackClients() {}

    private static String trailingSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/' ? uri : uri + "/";
    }

    private static OkHttpClient createOkHttpClient(String userAgent) {
        return new OkHttpClient.Builder()
                .addInterceptor(UserAgentInterceptor.of(userAgent))
                .connectionPool(new ConnectionPool(100, 10, TimeUnit.MINUTES))
                .build();
    }

    public static <T> T create(Class<T> clazz, String userAgent, String uri,
            Converter.Factory... specialPurposeConverters) {
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .baseUrl(trailingSlash(uri))
                .client(createOkHttpClient(userAgent));
        Stream.of(specialPurposeConverters).forEach(retrofit::addConverterFactory);
        retrofit.addConverterFactory(JacksonConverterFactory.create(ObjectMappers.newObjectMapper()));
        return retrofit.build().create(clazz);
    }

}

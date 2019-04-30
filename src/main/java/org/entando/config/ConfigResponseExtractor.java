/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.config;

import com.google.gson.Gson;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author s.marcelino@entando.com
 */
class ConfigResponseExtractor<T> implements ResponseExtractor<T> {

    private final Gson gson;
    private final Class<T> clazz;

    ConfigResponseExtractor(final Gson gson, final Class<T> clazz) {
        this.gson = gson;
        this.clazz = clazz;
    }

    @Override
    public T extractData(final ClientHttpResponse clientHttpResponse) throws IOException {
        try (final InputStreamReader reader = new InputStreamReader(clientHttpResponse.getBody())) {
            return gson.fromJson(reader, clazz);
        }
    }

}

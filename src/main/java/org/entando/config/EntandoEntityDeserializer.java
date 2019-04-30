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

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author s.marcelino@entando.com
 */
class EntandoEntityDeserializer<T> implements JsonDeserializer<T> {

    private final Gson gson = new Gson();
    private final Class<T> clazz;

    EntandoEntityDeserializer(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(final JsonElement element, final Type type, final JsonDeserializationContext jdc) throws JsonParseException {
        final JsonElement content = element.getAsJsonObject().get("payload");
        return gson.fromJson(content, clazz);
    }
}

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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

/**
 * @author s.marcelino@entando.com
 */
public class ConfigService<T> {

    private static final String KEY = "config";

    private final Gson gson;
    private final RestTemplate restTemplate;
    private final String configServiceUri;
    private final Class<T> configClass;
    private final LoadingCache<String, T> cache;

    public ConfigService(final String clientId, final String clientSecret,
                         final String accessTokenUri, final String configServiceUri,
                         final Class<T> configClass) {
        final OAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
        final ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);
        resourceDetails.setAccessTokenUri(accessTokenUri);

        restTemplate = new RestTemplate();
        restTemplate.setInterceptors(singletonList(new OAuth2Interceptor(clientContext, resourceDetails)));

        this.configClass = configClass;
        this.gson = new GsonBuilder().registerTypeAdapter(configClass, new EntandoEntityDeserializer<>(configClass)).create();
        this.configServiceUri = String.format("%s/config/%s", removeTrailingSlash(configServiceUri), clientId);

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(cacheLoader());
    }

    public T getConfig() {
        try {
            return cache.get(KEY);
        } catch (ExecutionException e) {
            return getInternalConfig();
        }
    }

    public void updateConfig(final T config) {
        restTemplate.put(configServiceUri, config);
        cache.refresh(KEY);
    }

    private T getInternalConfig() {
        return restTemplate.execute(configServiceUri, HttpMethod.GET, null, new ConfigResponseExtractor<>(gson, configClass));
    }

    private String removeTrailingSlash(final String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private CacheLoader<String, T> cacheLoader() {
        return new CacheLoader<String, T>() {
            public T load(final String key) {
                return getInternalConfig();
            }
        };
    }

}

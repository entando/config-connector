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

import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;

/**
 * @author s.marcelino@entando.com
 */
class OAuth2Interceptor implements ClientHttpRequestInterceptor {

    private final OAuth2FeignRequestInterceptor interceptor;

    OAuth2Interceptor(final OAuth2ClientContext clientContext, final ClientCredentialsResourceDetails resourceDetails) {
        this.interceptor = new OAuth2FeignRequestInterceptor(clientContext, resourceDetails);
    }

    private String getAuthorization() {
        final OAuth2AccessToken accessToken = interceptor.getToken();
        return String.format("Bearer %s", accessToken.getValue());
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest httpRequest, final byte[] bytes,
                                        final ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        httpRequest.getHeaders().add("Authorization", getAuthorization());
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}

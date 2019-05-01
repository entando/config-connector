package org.entando.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 8099)
public class ConfigServiceTest {

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Test
    public void testGetConfigAndCache() {
        final String clientId = "my-cliend-id";
        final String clientSecret = "my-ultra-secret-secret";
        final String configServiceUri = "http://localhost:8099";
        final String accessTokenUri = "http://localhost:8099/auth/realms/realm/protocol/openid-connect/token";

        final ConfigService<TestCustomConfig> configService = new ConfigService<>(clientId,
                clientSecret, accessTokenUri, configServiceUri, TestCustomConfig.class);

        stubAuth();
        stubFor(WireMock.get(urlEqualTo(String.format("/config/%s", clientId)))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{ \"payload\": { \"name\": \"this is a sample name\", \"integer\": 122, \"active\": false } }")));

        TestCustomConfig config = configService.getConfig();
        assertThat(config.getName()).isEqualTo("this is a sample name");
        assertThat(config.getInteger()).isEqualTo(122);
        assertThat(config.isActive()).isFalse();

        stubAuth();
        stubFor(WireMock.get(urlEqualTo(String.format("/config/%s", clientId)))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{ \"payload\": { \"name\": \"this is another sample name\", \"integer\": 122, \"active\": false } }")));

        config = configService.getConfig();
        assertThat(config.getName()).isEqualTo("this is a sample name");
        assertThat(config.getInteger()).isEqualTo(122);
        assertThat(config.isActive()).isFalse();

        stubAuth();
        stubFor(WireMock.put(urlEqualTo(String.format("/config/%s", clientId)))
                .withRequestBody(containing("\"name\":\"this is another sample name\""))
                .withRequestBody(containing("\"integer\":18748"))
                .withRequestBody(containing("\"active\":true"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{ \"payload\": { \"name\": \"this is another sample name\", \"integer\": 18748, \"active\": true } }")));

        config.setName("this is another sample name");
        config.setInteger(18748);
        config.setActive(true);
        configService.updateConfig(config);

        stubAuth();
        stubFor(WireMock.get(urlEqualTo(String.format("/config/%s", clientId)))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{ \"payload\": { \"name\": \"this is another sample name\", \"integer\": 18748, \"active\": true } }")));

        config = configService.getConfig();
        assertThat(config.getName()).isEqualTo("this is another sample name");
        assertThat(config.getInteger()).isEqualTo(18748);
        assertThat(config.isActive()).isTrue();
    }

    public static void stubAuth() {
        WireMock.reset();
        final String token = json(new MockAccessToken("4984894", 100000000, Instant.now().toEpochMilli()));
        stubFor(WireMock.post(urlEqualTo("/auth/realms/realm/protocol/openid-connect/token"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(token)));
    }

    public static String json(final Object object) {
        return gson.toJson(object);
    }

    @Value
    private static class MockAccessToken {
        private String accessToken;
        private int expiresIn;
        private long createdTimestamp;
    }

}

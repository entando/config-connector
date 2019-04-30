# Entando Config Service Connector
This library is used to integrate with Entando ConfigService without the need to implement the communication to the service.

## Install

Add the dependency to your project

```xml
<dependency>
    <groupId>org.entando</groupId>
    <artifactId>config-connector</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then create a Configuration class to create the bean

```java
@Configuration
public class ConfigServiceConfiguration {

    @Bean
    public ConfigService<YourCustomConfig> configService(
            @Value("${client-id}") final String clientId,
            @Value("${client-secret}") final String clientSecret,
            @Value("${access-token-uri}") final String accessTokenUri,
            @Value("${config-service-uri}") final String configServiceUri) {
        return new ConfigService<>(clientId, clientSecret, accessTokenUri, configServiceUri, YourCustomConfig.class);
    }

}
```

Now you can use the service, for example a wrapper:

```java
@Service
public class YourCustomConfigService {

    private final ConfigService<YourCustomConfig> configService;

    @Autowired
    public YourCustomConfigService(final ConfigService<YourCustomConfig> configService) {
        this.configService = configService;
    }

    public YourCustomConfig getConfig() {
        return Optional.ofNullable(configService.getConfig())
            .orElseGet(YourCustomConfig::getDefault);
    }

    public void updateAvatarConfig(final YourCustomConfig config) {
        configService.updateConfig(config);
    }

}
```

The configuration is builtin with a memory cache (Guava).
At the moment it is not configurable and has 10 minutes TTL.
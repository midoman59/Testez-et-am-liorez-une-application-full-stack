package com.openclassrooms.starterjwt.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void propertySourcesPlaceholderConfigurer_shouldPointToEnvFile() {
        PropertySourcesPlaceholderConfigurer configurer = AppConfig.propertySourcesPlaceholderConfigurer();

        assertNotNull(configurer);
        assertInstanceOf(PropertySourcesPlaceholderConfigurer.class, configurer);
    }
}

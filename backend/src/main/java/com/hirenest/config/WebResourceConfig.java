package com.hirenest.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@EnableConfigurationProperties(UploadProperties.class)
public class WebResourceConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public WebResourceConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!uploadProperties.isLocal()) {
            return;
        }
        Path root = Path.of(uploadProperties.getDirectory()).toAbsolutePath();
        String location = "file:" + root + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}

package com.hirenest.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(CloudinaryProperties.class)
public class CloudinaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Bean
    public Cloudinary cloudinary(CloudinaryProperties properties) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", properties.getCloudName());
        config.put("api_key", properties.getApiKey());
        config.put("api_secret", properties.getApiSecret());
        config.put("secure", "true");

        if (!properties.isConfigured()) {
            log.warn(
                    "Cloudinary credentials are missing or still use placeholders. "
                            + "Resume and avatar uploads will fail until CLOUDINARY_CLOUD_NAME, "
                            + "CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET are set."
            );
        }

        return new Cloudinary(config);
    }
}

package org.csu.api.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("image.server")
@Component
@Data
public class ImageServerConfig {
    private String url;
    private String username;
    private String password;
}

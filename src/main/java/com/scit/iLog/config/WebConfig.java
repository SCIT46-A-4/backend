package com.scit.iLog.config;

import com.scit.iLog.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final FilePathUtil filePathUtil;
    public static final String HEALTHCHECK_IMAGES_REQUEST_PATH = "/healthCheckImages/**";
    public static final String HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH = "/healthCheckImages/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(HEALTHCHECK_IMAGES_REQUEST_PATH)
                .addResourceLocations(filePathUtil.childHealthCheckImgUploadPath());
    }
}


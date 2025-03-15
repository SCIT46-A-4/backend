package com.scit.iLog.config;

import com.scit.iLog.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    public static final String HEALTHCHECK_IMAGES_REQUEST_PATH = "/healthCheckImages/**";
    public static final String HEALTHCHECK_IMAGES_REQUEST_ROOT_PATH = "/healthCheckImages/";
    public static final String ANALYSIS_FILES_REQUEST_PATH = "/analysisFiles/**";
    public static final String ANALYSIS_FILES_REQUEST_ROOT_PATH = "/analysisFiles/";
    public static final String CHILD_PROFILE_REQUEST_PATH = "/childProfile/**";
    public static final String CHILD_PROFILE_REQUEST_ROOT_PATH = "/childProfile/";
    private final FilePathUtil filePathUtil;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(HEALTHCHECK_IMAGES_REQUEST_PATH)
                .addResourceLocations(filePathUtil.childHealthCheckImgResourceLocation());
        registry.addResourceHandler(ANALYSIS_FILES_REQUEST_PATH)
                .addResourceLocations(filePathUtil.childAnalysisFileResourceLocation());
        registry.addResourceHandler(CHILD_PROFILE_REQUEST_PATH)
                .addResourceLocations(filePathUtil.childProfileImgResourceLocation());
    }
}


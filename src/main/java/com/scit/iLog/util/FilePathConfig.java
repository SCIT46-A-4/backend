package com.scit.iLog.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilePathConfig {
    private final String windowsPath = "C:/files";
    private final String macPath = "/Users/hojooneum/Downloads/scit/springboot/spring7/uploadedFiles";
    @Bean
    String filePath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsPath : macPath;
    }
}

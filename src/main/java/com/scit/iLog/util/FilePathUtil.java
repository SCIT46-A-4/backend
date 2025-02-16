package com.scit.iLog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public final class FilePathUtil {
    @Value("${childProfileImgWindowsPath}")
    private String childProfileImgWindowsPath;
    @Value("${childProfileImgMacPath}")
    private String childProfileImgMacPath;
    @Value("${childHealthCheckImgWindowsPath}")
    private String childHealthCheckImgWindowsPath;
    @Value("${childHealthCheckImgMacPath}")
    private String childHealthCheckImgMacPath;

    public String childProfileImgUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? childProfileImgWindowsPath : childProfileImgMacPath;
    }

    public String childHealthCheckImgUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? childHealthCheckImgWindowsPath : childHealthCheckImgMacPath;
    }
}

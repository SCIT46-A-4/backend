package com.scit.iLog.util;

import org.springframework.beans.factory.annotation.Value;
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
    @Value("${childAnalysisFileImgWindowsPath}")
    private String childAnalysisFileImgWindowsPath;
    @Value("${childAnalysisFileMacPath}")
    private String childAnalysisFileMacPath;

    public String childProfileImgUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? childProfileImgWindowsPath : childProfileImgMacPath;
    }

    public String childHealthCheckImgUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? childHealthCheckImgWindowsPath : childHealthCheckImgMacPath;
    }

    public String analysisFileUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? childAnalysisFileImgWindowsPath : childAnalysisFileMacPath;
    }
}

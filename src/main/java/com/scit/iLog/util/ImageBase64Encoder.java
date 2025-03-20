package com.scit.iLog.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageBase64Encoder {

    public static String encodeImageToBase64(String imagePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(imagePath));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeMultipartFileToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        try {
            byte[] fileBytes = file.getBytes();
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 인코딩에 실패했습니다.", e);
        }
    }
}

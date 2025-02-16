package com.scit.iLog.util;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//Multipart 파일 저장을 돕는 컴포넌트
@Getter
@Component
public final class FileManager {

	public String saveFile(MultipartFile uploadFile, String uploadPath) {
		if (ObjectUtils.isEmpty(uploadFile) || uploadFile.isEmpty()) {
			throw new IllegalArgumentException("저장할 파일이 존재하지 않음.");
		}

		// 디렉토리 객체 생성 및 존재하지 않으면 생성
		File directory = new File(uploadPath);
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IllegalStateException("디렉토리 생성 실패: " + uploadPath);
		}

		String originalFilename = uploadFile.getOriginalFilename();
		if (!StringUtils.hasText(originalFilename)) {
			throw new IllegalArgumentException("업로드 파일에 원본 파일명이 존재하지 않습니다.");
		}

		// 파일명과 확장자 분리 (확장자가 없을 경우에도 안전하게 처리)
		int dotIndex = originalFilename.lastIndexOf('.');
		String baseName = (dotIndex != -1) ? originalFilename.substring(0, dotIndex) : originalFilename;
		String extension = (dotIndex != -1) ? originalFilename.substring(dotIndex) : "";

		// UUID를 이용해 파일명 중복 방지
		String uuid = UUID.randomUUID().toString();
		String savedFileName = String.format("%s_%s%s", baseName, uuid, extension);

		// 시스템에 맞는 파일 경로 조합
		File savedFile = new File(directory, savedFileName);

		try {
			uploadFile.transferTo(savedFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return savedFileName;
	}

	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			return file.delete();
		}
		return false;
	}
}

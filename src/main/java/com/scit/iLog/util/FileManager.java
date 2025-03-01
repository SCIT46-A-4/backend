package com.scit.iLog.util;

import com.scit.iLog.exception.HealthCheckFileDeleteFailException;
import com.scit.iLog.exception.HealthCheckFileSaveFailException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

//Multipart 파일 저장을 돕는 컴포넌트
@Slf4j
@Getter
@Component
public final class FileManager {

	/*
		파일을 저장하고 저장된 파일 이름을 반환하는 메서드
	 */
	public void saveFile(MultipartFile uploadFile, String uploadPath, String savedFileName) 
	{
        if (ObjectUtils.isEmpty(uploadFile)) {
            throw new IllegalArgumentException("저장할 파일이 존재하지 않음.");
        } else if (uploadFile.isEmpty()) {
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

		// 시스템에 맞는 파일 경로 조합
		File savedFile = new File(directory, savedFileName);

		try {
			uploadFile.transferTo(savedFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new HealthCheckFileSaveFailException(
					String.format("HealthCheck 파일 저장 실패: %s", savedFile.getAbsolutePath()));
		}
	}

	/*
		원본파일이름을 받아서 저장할 파일이름을 반환합니다.
	 */
	public static String getSavedFileName(String originalFilename) {
		// 파일명과 확장자 분리 (확장자가 없을 경우에도 안전하게 처리)
		int dotIndex = originalFilename.lastIndexOf('.');
		String baseName = (dotIndex != -1) ? originalFilename.substring(0, dotIndex) : originalFilename;
		String extension = (dotIndex != -1) ? originalFilename.substring(dotIndex) : "";

		// UUID를 이용해 파일명 중복 방지
		String uuid = UUID.randomUUID().toString();
		String savedFileName = String.format("%s_%s%s", baseName, uuid, extension);
		return savedFileName;
	}

	/*
		파일을 삭제하고 반환 성공 여부에 따라 불리언값을 반환합니다.
	 */
	public static void deleteFile(String filePath) {
		try {
			Path path = Paths.get(filePath);

			// 1. 파일 존재 여부 확인
			if (!Files.exists(path)) {
				log.warn("파일이 존재하지 않습니다: {}", filePath);
				throw new IllegalArgumentException(String.format("파일 없음: %s", filePath));
			}

			// 2. 파일인지 확인 (디렉토리가 아닌지)
			if (!Files.isRegularFile(path)) {
				log.error("해당 경로가 파일이 아닙니다: {}", filePath);
				throw new IllegalArgumentException(String.format("파일이 아님: %s", filePath));
			}

			// 3. 삭제 권한 확인
			if (!Files.isWritable(path)) {
				log.error("파일 삭제 권한이 없습니다: {}", filePath);
				throw new IllegalArgumentException(String.format("파일 삭제 권한 없음: %s", filePath));
			}

			// 4. 파일 삭제 수행
			Files.delete(path);
			log.info("파일 삭제 완료: {}", filePath);
		} catch (IOException e) {
			log.error("파일 삭제 중 오류 발생: {}", filePath, e);
			throw new HealthCheckFileDeleteFailException(String.format("HealthCheck 파일 삭제 실패: %s", filePath));
		}
	}

	/**
	 * 파일이나 디렉토리를 재귀적으로 삭제합니다.
	 * 디렉토리인 경우 하위 항목들을 모두 삭제합니다.
	 *
	 * @param path 삭제할 경로
	 * @return 삭제 성공 여부
	 */
	public static boolean deleteRecursively(String path) {
		try {
			Path directoryPath = Paths.get(path);
			if (Files.exists(directoryPath)) {
				Files.walk(directoryPath)
						.sorted(Comparator.reverseOrder())
						.forEach(p -> {
							try {
								Files.delete(p);
								log.info("항목 삭제 완료: {}", p);
							} catch (IOException e) {
								log.error("항목 삭제 실패: {}", p, e);
							}
						});
				return true;
			}
			return false;
		} catch (IOException e) {
			log.error("재귀적 삭제 중 오류 발생: {}", path, e);
			return false;
		}
	}
}

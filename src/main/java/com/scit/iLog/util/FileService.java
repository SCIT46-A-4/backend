package com.scit.iLog.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//Multipart File을 받아서 저장 파일명을 만드는 유틸리티
@Service
public class FileService {
	// 파일이 저장될 기본 경로를 설정
//    @Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@Autowired
	private String filePath;

	public String getUploadPath() {
		return filePath;
	}

	// 1) 서버에 폴더가 없으면 폴더 생성
	public String saveFile(MultipartFile uploadFile, String uploadPath) {
		if (uploadFile.isEmpty())
			return "";
		File path = new File(uploadPath); // 파일(폴더)객체 생성
		if (!path.isDirectory()) {
			path.mkdirs();
		}
		String originalFilename = uploadFile.getOriginalFilename();
		String uuid = UUID.randomUUID().toString();

		String ext;
		String filename;// 파일명 저장할 변수
		String savedFileName;// 저장장치에 저장할 파일명

		int position = originalFilename.lastIndexOf(".");
		if (position == -1) {
			ext = "";
		} else {
			ext = originalFilename.substring(position + 1);
		}
		filename = originalFilename.substring(0, position);
		savedFileName = filename + "_" + uuid + "." + ext;

		File savedFile = new File(uploadPath + "/" + savedFileName);

		try {
			uploadFile.transferTo(savedFile);
		} catch (IOException e) {
			savedFileName = null;
			e.printStackTrace();
		}
		return savedFileName;
	}

	public boolean deleteFile(String filePath) {
		boolean result = false;

		File file = new File(filePath);

		if (file.isFile()) {
			result = file.delete();
		}
		return result;
	}
}

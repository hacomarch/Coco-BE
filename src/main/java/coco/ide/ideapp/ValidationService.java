package coco.ide.ideapp;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    // 파일 또는 폴더 이름 유효성 검사
    private static final String INVALID_FILE_CHARS = "[\\\\/:*?\"<>|]";
    private static final String INVALID_FOLDER_CHARS = "[\\\\/:*?\"<>|]";

    // 파일 이름 검증: 공백 및 유효하지 않은 문자가 없어야 함
    public boolean isValidFileName(String fileName) {
        // 파일 이름에 공백 또는 유효하지 않은 문자가 포함되어 있는지 검사
        return !fileName.matches(".*" + INVALID_FILE_CHARS + ".*") && !fileName.contains(" ");
    }

    // 폴더 이름 검증: 유효하지 않은 문자만 검사
    public boolean isValidFolderProjectName(String folderName) {
        // 폴더 이름에 유효하지 않은 문자가 포함되어 있는지 검사
        return !folderName.matches(".*" + INVALID_FOLDER_CHARS + ".*");
    }
}

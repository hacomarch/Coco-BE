package coco.ide.ideapp;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    // 파일 또는 폴더 이름 유효성 검사
    private static final String INVALID_CHARS = "[\\\\/:*?\"<>|]";

    public boolean isValidName(String folderName) {
        return folderName != null
                && !folderName.trim().isEmpty()
                && !folderName.matches(".*" + INVALID_CHARS + ".*")
                && !folderName.contains(" ");
    }
}

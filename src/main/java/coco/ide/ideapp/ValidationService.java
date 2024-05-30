package coco.ide.ideapp;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private static final String INVALID_CHARS = "[\\\\/:*?\"<>|]";

    public boolean isValidName(String folderName) {
        return folderName != null
                && !folderName.trim().isEmpty()
                && !folderName.matches(".*" + INVALID_CHARS + ".*")
                && !folderName.contains(" ");
    }
}

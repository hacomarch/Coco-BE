package coco.ide.ideapp.files;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.files.requestdto.*;
import coco.ide.ideapp.projects.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")

public class FileController {

    private final FileService fileService;
    private final ValidationService validationService;
    private final ExecuteService executeService;
    private final ProjectService projectService;

    @PostMapping
    public String createFile(@PathVariable Long projectId,
                             @PathVariable Long folderId,
                             @RequestBody CreateFileForm form) {
        boolean isValid = validationService.isValidFileName(form.getName());
        boolean success = fileService.createFile(projectId, folderId, form);

        if (!isValid) {
            return "file name is not valid";
        }

        if (!success) {
            return "file name duplicated";
        }

        return "create file ok";

    }

    @DeleteMapping("/{fileId}")
    public String deleteFile(@PathVariable Long projectId, @PathVariable Long folderId, @PathVariable Long fileId) {
        fileService.deleteFile(projectId, folderId, fileId);
        return "delete file ok";
    }

    @PatchMapping("/{fileId}/name")
    public String updateFileName(@PathVariable Long fileId, @RequestBody UpdateFileNameForm form) {
        boolean isUpdateFileName = fileService.updateFileName(fileId, form.getNewName());
        if (!isUpdateFileName) {
            return "update file name fail";
        }
        return "update file name ok";
    }

    @PatchMapping("/{fileId}/path")
    public String updateFilePath(@PathVariable Long projectId, @PathVariable Long fileId, @RequestBody UpdateFilePathForm form) {
        boolean isUpdateFilePath = fileService.updateFilePath(projectId, form.getFolderId(), fileId);
        if (!isUpdateFilePath) {
            return "update file path fail";
        }
        return "update file path ok";
    }

    @PatchMapping("/{fileId}/content")
    public String updateFileContent(@PathVariable Long fileId, @RequestBody UpdateFileContentForm form) {
        log.info("file content = {}", form.getCode());
        fileService.updateFileContent(fileId, form.getCode());
        return "수정 성공";
    }

    @PostMapping("/{fileId}")
    public String runCommand(@PathVariable Long projectId,
                           @PathVariable Long folderId,
                           @PathVariable Long fileId) throws IOException {

        Long memberId = fileService.getMemberId(projectId);
        String language = projectService.getLanguage(projectId);
        String filePath;
        if (folderId != null) {
            filePath = "~/filedb/" + memberId + "/" + projectId + "/" + folderId + "/";
        } else {
            filePath = "~/filedb/" + memberId + "/" + projectId + "/";
        }

        return executeService.executeCode(filePath, language, fileId);
    }

    @GetMapping("/{fileId}")
    public String getFileContent(@PathVariable Long fileId) {
        return fileService.getFileContent(fileId);
    }
}

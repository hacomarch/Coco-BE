package coco.ide.ideapp.files;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.files.requestdto.*;
import coco.ide.ideapp.files.run.CodeExecuteService;
import coco.ide.ideapp.files.run.JavaExecutionWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")

public class FileController {

    private final FileService fileService;
    private final CodeExecuteService codeExecuteService;
    private final ValidationService validationService;

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
        fileService.updateFileContent(fileId, form.getCode());
        return "수정 성공";
    }

    @PostMapping("/{fileId}")
    public void runCommand(@PathVariable Long projectId,
                           @PathVariable Long folderId,
                           @PathVariable Long fileId,
                           @RequestBody CommandRequest request) throws IOException {
        WebSocketSession session = JavaExecutionWebSocketHandler.getSessionById(request.getSessionId());
        String filePath = projectId + "/" + folderId + "/";
        codeExecuteService.runJavaProgram(filePath, fileId, request.getCommand(), session);
    }
}

package coco.ide.ideapp.files;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.exception.InvalidCreationFormException;
import coco.ide.ideapp.files.requestdto.*;
import coco.ide.ideapp.files.responsedto.FileDto;
import coco.ide.ideapp.projects.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")
public class FileController {

    private final FileService fileService;
    private final ValidationService validationService;
    private final ExecuteService executeService;
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<FileDto> createFile(@PathVariable Long projectId,
                                              @PathVariable Long folderId,
                                              @RequestBody CreateFileForm form) {
        if (validationService.isValidName(form.getName())) {
            FileDto file = fileService.createFile(projectId, folderId, form);
            return ResponseEntity.status(HttpStatus.CREATED).body(file);
        } else {
            throw new InvalidCreationFormException();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long projectId,
                                             @PathVariable Long folderId,
                                             @PathVariable Long fileId) {
        fileService.deleteFile(projectId, folderId, fileId);
        return ResponseEntity.ok("delete file ok");
    }

    @PatchMapping("/{fileId}/name")
    public ResponseEntity<FileDto> updateFileName(@PathVariable Long fileId, @RequestBody UpdateFileNameForm form) {
        if (validationService.isValidName(form.getNewName())) {
            FileDto fileDto = fileService.updateFileName(fileId, form.getNewName());
            return ResponseEntity.status(HttpStatus.OK).body(fileDto);
        } else {
            throw new InvalidCreationFormException();
        }
    }

    @PatchMapping("/{fileId}/path")
    public ResponseEntity<FileDto> updateFilePath(@PathVariable Long projectId,
                                                  @PathVariable Long fileId,
                                                  @RequestBody UpdateFilePathForm form) {
        FileDto fileDto = fileService.updateFilePath(projectId, form.getFolderId(), fileId);
        return ResponseEntity.status(HttpStatus.OK).body(fileDto);
    }

    @PatchMapping("/{fileId}/content")
    public ResponseEntity<String> updateFileContent(@PathVariable Long fileId, @RequestBody UpdateFileContentForm form) {
        fileService.updateFileContent(fileId, form.getCode());
        return ResponseEntity.ok("update file content ok");
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<String> getFileContent(@PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.getFileContent(fileId));
    }

    @GetMapping("/{fileId}/run")
    public String runCommand(@PathVariable Long projectId,
                             @PathVariable Long folderId,
                             @PathVariable Long fileId) throws IOException {
        String language = projectService.getProjectById(projectId).getLanguage();
        String filePath = fileService.buildPath(projectId, folderId == null ? 0 : folderId);
        String fileName = fileService.getFileById(fileId).getName();
        return executeService.executeCode(filePath, language, fileName);
    }
}

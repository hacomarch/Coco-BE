package coco.ide.ideapp.files;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.requestdto.UpdateFileContentForm;
import coco.ide.ideapp.files.requestdto.UpdateFileNameForm;
import coco.ide.ideapp.files.requestdto.UpdateFilePathForm;
import coco.ide.ideapp.files.responsedto.ForExecuteDto;
import coco.ide.ideapp.files.run.CodeExecuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")

//Todo: Scanner등 입력받는거 처리도 해야함
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
    public String updateFileContent (@PathVariable Long fileId, @RequestBody UpdateFileContentForm form) {
        fileService.updateFileContent(fileId, form.getCode());
        return "수정 성공";
    }

    @PostMapping("/{fileId}")
    public String compileAndRunFile(@PathVariable Long fileId) {
        ForExecuteDto response = fileService.getFilePath(fileId);
        //Todo: 언어 받는거 변경해야함
        return codeExecuteService.executeCode(response.getFilePath(), response.getLanguage());
    }
}

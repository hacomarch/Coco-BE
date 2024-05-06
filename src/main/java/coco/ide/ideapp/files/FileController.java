package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.requestdto.UpdateFileNameForm;
import coco.ide.ideapp.files.requestdto.UpdateFilePathForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")
public class FileController {

    private final FileService fileService;

    @PostMapping
    public String createFile(@PathVariable Long projectId,
                             @PathVariable Long folderId,
                             @RequestBody CreateFileForm form) {
        fileService.createFile(projectId, folderId, form);
        return "create file ok";
    }

    @DeleteMapping("/{fileId}")
    public String deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
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
    public String updateFilePath(@PathVariable Long fileId, @RequestBody UpdateFilePathForm form) {
        boolean isUpdateFilePath = fileService.updateFilePath(fileId, form.getFolderId());
        if (!isUpdateFilePath) {
            return "update file path fail";
        }
        return "update file path ok";
    }


}

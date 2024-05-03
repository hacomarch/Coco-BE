package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.requestdto.UpdateFileNameForm;
import coco.ide.ideapp.files.requestdto.UpdateFilePathForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders/{folderId}/files")
//Todo : 서비스에서 boolean으로 리턴된 값에 따라서 응답 스트링 처리 필요 하은
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
        fileService.updateFileName(fileId, form.getNewName());
        return "update file name ok";
    }

    @PatchMapping("/{fileId}/path")
    public String updateFilePath(@PathVariable Long fileId, @RequestBody UpdateFilePathForm form) {
        fileService.updateFilePath(fileId, form.getFolderId());
        return "update file path ok";
    }


}

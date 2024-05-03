package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
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

}

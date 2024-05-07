package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.folders.requestdto.UpdateFolderNameForm;
import coco.ide.ideapp.folders.requestdto.UpdateFolderPathForm;
import coco.ide.ideapp.folders.responsedto.FileListDto;
import coco.ide.ideapp.folders.responsedto.FolderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public String createFolder(@PathVariable Long projectId, @RequestBody CreateFolderForm form) {
        log.info("form = {}", form);
        boolean success = folderService.createFolder(projectId, form);
        if (success) {
            return "create folder ok";
        } else {
            return "folder name duplicate";
        }
    }

    @DeleteMapping("/{folderId}")
    public String deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return "delete folder ok";
    }

    @PatchMapping("/{folderId}/name")
    public String updateFolderName(@PathVariable Long folderId,
                                   @RequestBody UpdateFolderNameForm form) {
        boolean result = folderService.updateFolderName(folderId, form.getNewName());
        if (!result) {
            return "update folder name fail";
        }
        return "update folder name ok";
    }

    @PatchMapping("/{folderId}/path")
    public String updateFolderPath(@PathVariable Long folderId,
                                   @RequestBody UpdateFolderPathForm form) {
        boolean result = folderService.updateFolderPath(folderId, form.getParentId());
        if (!result) {
            return "update folder path fail";
        }
        return "update folder path ok";
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<List<FileListDto>> findFiles(@PathVariable Long folderId) {
        List<FileListDto> files = folderService.findFiles(folderId);
        return ResponseEntity.ok(files);
    }

}

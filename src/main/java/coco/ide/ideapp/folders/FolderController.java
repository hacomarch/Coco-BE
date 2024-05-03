package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.folders.requestdto.UpdateFolderNameForm;
import coco.ide.ideapp.folders.responsedto.FolderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/{projectId}/folders")
    public String createFolder(@PathVariable Long projectId, @RequestBody CreateFolderForm form) {
        log.info("form = {}", form);
        folderService.createFolder(projectId, form);
        return "create folder ok";
    }

    @DeleteMapping("/{projectId}/folders/{folderId}")
    public String deleteFolder(@PathVariable Long projectId, @PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return "delete folder ok";
    }

    @PatchMapping("/{projectId}/folders/{folderId}")
    public ResponseEntity<FolderDto> updateFolderName(@PathVariable Long projectId,
                                                      @PathVariable Long folderId,
                                                      @RequestBody UpdateFolderNameForm form) {
        FolderDto folderDto = folderService.updateFolderName(folderId, form.getNewName());
        return ResponseEntity.ok(folderDto);
    }

}

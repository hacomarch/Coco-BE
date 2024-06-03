package coco.ide.ideapp.folders;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.exception.InvalidCreationFormException;
import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.folders.requestdto.UpdateFolderNameForm;
import coco.ide.ideapp.folders.requestdto.UpdateFolderPathForm;
import coco.ide.ideapp.folders.responsedto.FileListDto;
import coco.ide.ideapp.folders.responsedto.FolderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/folders")
public class FolderController {

    private final FolderService folderService;
    private final ValidationService validationService;

    @PostMapping
    public ResponseEntity<FolderDto> createFolder(@PathVariable Long projectId, @RequestBody CreateFolderForm form) {
        if (validationService.isValidName(form.getName())) {
            FolderDto folder = folderService.createFolder(projectId, form);
            return ResponseEntity.status(HttpStatus.CREATED).body(folder);
        } else {
            throw new InvalidCreationFormException();
        }
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long projectId, @PathVariable Long folderId) {
        folderService.deleteFolder(projectId, folderId);
        return ResponseEntity.ok("delete folder ok");
    }

    @PatchMapping("/{folderId}/name")
    public ResponseEntity<FolderDto> updateFolderName(@PathVariable Long folderId,
                                                      @RequestBody UpdateFolderNameForm form) {
        FolderDto folderDto = folderService.updateFolderName(folderId, form.getNewName());
        return ResponseEntity.status(HttpStatus.OK).body(folderDto);
    }

    @PatchMapping("/{folderId}/path")
    public ResponseEntity<FolderDto> updateFolderPath(@PathVariable Long projectId, @PathVariable Long folderId,
                                   @RequestBody UpdateFolderPathForm form) {
        FolderDto folderDto = folderService.updateFolderPath(projectId, folderId, form.getParentId());
        return ResponseEntity.status(HttpStatus.OK).body(folderDto);
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<List<FileListDto>> findFiles(@PathVariable Long folderId) {
        List<FileListDto> files = folderService.findFiles(folderId);
        return ResponseEntity.ok(files);
    }
}

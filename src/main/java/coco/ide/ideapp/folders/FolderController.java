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
        folderService.createFolder(projectId, form);
        return "create folder ok";
    }

    @DeleteMapping("/{folderId}")
    public String deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return "delete folder ok";
    }


    @PatchMapping("/{folderId}/name")
    public ResponseEntity<FolderDto> updateFolderName(@PathVariable Long folderId,
                                                      @RequestBody UpdateFolderNameForm form) {
        FolderDto folderDto = folderService.updateFolderName(folderId, form.getNewName());
        return ResponseEntity.ok(folderDto);
    }

    //TODO : 수정하고 난 후에 응답값을 넘겨주기
    @PatchMapping("/{folderId}/path")
    public String updateFolderPath(@PathVariable Long folderId,
                                   @RequestBody UpdateFolderPathForm form) {
        folderService.updateFolderPath(folderId, form.getParentId());
        return "update folder path ok";
    }

    //TODO : 파일 만들고 잘 가져오는지 테스트하기
    @GetMapping("/{folderId}")
    public ResponseEntity<List<FileListDto>> findFiles(@PathVariable Long folderId) {
        List<FileListDto> files = folderService.findFiles(folderId);
        return ResponseEntity.ok(files);
    }

}

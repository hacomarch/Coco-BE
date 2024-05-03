package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}

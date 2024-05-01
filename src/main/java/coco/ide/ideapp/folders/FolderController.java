package coco.ide.ideapp.folders;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

}

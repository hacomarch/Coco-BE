package coco.ide.ideapp.folders.requestdto;

import lombok.Data;

@Data
public class CreateFolderForm {
    private String name = null;
    private Long parentId = 0L;
}

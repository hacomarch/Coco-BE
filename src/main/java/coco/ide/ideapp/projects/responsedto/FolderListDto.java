package coco.ide.ideapp.projects.responsedto;

import lombok.Data;

@Data
public class FolderListDto {
    private final Long folderId;
    private final String folderName;
    private final Long parentId;
}

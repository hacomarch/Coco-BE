package coco.ide.ideapp.projects.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderInfoDto {
    private Long folderId;
    private String folderName;
    private Long parentId;
}

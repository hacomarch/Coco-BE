package coco.ide.ideapp.folders.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderDto {
    private final Long folderId;
    private String name;
}

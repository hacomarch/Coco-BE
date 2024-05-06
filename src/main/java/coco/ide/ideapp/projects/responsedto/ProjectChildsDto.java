package coco.ide.ideapp.projects.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectChildsDto {
    private List<FolderInfoDto> folders;
    private List<FileInfoDto> files;
}

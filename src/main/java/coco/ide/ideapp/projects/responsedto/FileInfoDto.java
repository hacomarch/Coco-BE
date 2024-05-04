package coco.ide.ideapp.projects.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfoDto {
    private Long fileId;
    private String fileName;
}

package coco.ide.ideapp.files.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDto {
    private Long fileId;
    private String name;
    private String path;
}

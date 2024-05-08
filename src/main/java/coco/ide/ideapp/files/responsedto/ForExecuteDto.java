package coco.ide.ideapp.files.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForExecuteDto {
    private String filePath;
    private String language;
}

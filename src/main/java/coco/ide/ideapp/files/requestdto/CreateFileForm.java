package coco.ide.ideapp.files.requestdto;

import lombok.Data;

@Data
public class CreateFileForm {
    private final String name;
    private final String content;
}
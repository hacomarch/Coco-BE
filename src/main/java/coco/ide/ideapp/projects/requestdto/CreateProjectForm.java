package coco.ide.ideapp.projects.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateProjectForm {
    private final String name;
    private final String language;
}

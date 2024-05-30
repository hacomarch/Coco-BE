package coco.ide.ideapp.projects.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateProjectForm {
    private final String name;
    private final String language;
    private final Long memberId;
}

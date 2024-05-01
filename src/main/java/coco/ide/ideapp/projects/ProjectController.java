package coco.ide.ideapp.projects;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

}

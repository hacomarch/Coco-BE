package coco.ide.ideapp.projects;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.exception.InvalidProjectCreationFormException;
import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.requestdto.UpdateProjectNameForm;
import coco.ide.ideapp.projects.responsedto.ProjectChildsDto;
import coco.ide.ideapp.projects.responsedto.ProjectDto;
import coco.ide.ideapp.projects.responsedto.ProjectListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ValidationService validationService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectForm form) {
        if (validationService.isValidName(form.getName())) {
            ProjectDto project = projectService.createProject(form);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } else {
            throw new InvalidProjectCreationFormException();
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("delete project ok");
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProjectName(@PathVariable Long projectId,
                                                        @RequestBody UpdateProjectNameForm form) {
        ProjectDto project = projectService.updateProjectName(projectId, form.getNewName());
        return ResponseEntity.ok(project);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<ProjectListDto>> findProjects(@PathVariable Long memberId) {
        List<ProjectListDto> projects = projectService.findProjectsByMemberId(memberId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectChildsDto> findChildren(@PathVariable Long projectId) {
        ProjectChildsDto children = projectService.getProjectChildren(projectId);
        return ResponseEntity.ok(children);
    }
}

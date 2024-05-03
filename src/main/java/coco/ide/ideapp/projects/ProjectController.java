package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.requestdto.UpdateProjectNameForm;
import coco.ide.ideapp.projects.responseDto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("")
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectForm form) {
        log.info("createProjectForm = {}", form);
        ProjectDto project = projectService.createProject(form);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}")
    public String deleteProject(@PathVariable("projectId") Long projectId) {
        try {
            projectService.deleteProject(projectId);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        return "delete project ok";
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProjectName(@PathVariable("projectId") Long projectId, @RequestBody UpdateProjectNameForm form) {
        ProjectDto project = projectService.updateProjectName(projectId, form.getNewName());

        return ResponseEntity.ok(project);
    }
}

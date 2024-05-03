package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.requestdto.UpdateProjectNameForm;
import coco.ide.ideapp.projects.responseDto.FolderListDto;
import coco.ide.ideapp.projects.responseDto.ProjectDto;
import coco.ide.ideapp.projects.responseDto.ProjectListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProjectListDto>> findAllProjects() {
        List<ProjectListDto> allProjects = projectService.findAllProjects();
        return ResponseEntity.ok(allProjects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<FolderListDto>> findFolders(@PathVariable Long projectId) {
        List<FolderListDto> folders = projectService.findFolders(projectId);
        return ResponseEntity.ok(folders);
    }
}

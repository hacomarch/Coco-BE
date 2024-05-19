package coco.ide.ideapp.projects;

import coco.ide.ideapp.ValidationService;
import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.requestdto.UpdateProjectNameForm;
import coco.ide.ideapp.projects.responsedto.ProjectChildsDto;
import coco.ide.ideapp.projects.responsedto.ProjectDto;
import coco.ide.ideapp.projects.responsedto.ProjectListDto;
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
    private final ValidationService validationService;

    @PostMapping()
    public ProjectDto createProject(@RequestBody CreateProjectForm form) {
        log.info("createProjectForm = {}", form);
        boolean isValid = validationService.isValidFolderProjectName(form.getName());
        if (isValid) {
            return projectService.createProject(form);
        }
        //올바르지 않은 프로젝트 명
        return null;
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
    public ResponseEntity<List<ProjectListDto>> findAllProjects(@RequestParam Long memberId) {
        List<ProjectListDto> allProjects = projectService.findAllProjects(memberId);
        return ResponseEntity.ok(allProjects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectChildsDto> findFolders(@PathVariable Long projectId) {
        ProjectChildsDto childs = projectService.findChilds(projectId);
        return ResponseEntity.ok(childs);
    }
}

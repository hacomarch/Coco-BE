package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.responsedto.FolderListDto;
import coco.ide.ideapp.projects.responsedto.ProjectDto;
import coco.ide.ideapp.projects.responsedto.ProjectListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public void createProject(CreateProjectForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("프로젝트 명은 빈 칸일 수 없습니다.");
        }
        Project project = Project.builder()
                .name(form.getName())
                .language(form.getLanguage())
                .build();

        projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long projectId) throws IllegalArgumentException {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("프로젝트 ID" + projectId + "는 존재하지 않습니다.");
        }
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public ProjectDto updateProjectName(Long projectId, String newName) throws IllegalArgumentException{
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        project.changeName(newName);
        return new ProjectDto(project.getProjectId(), project.getName(), project.getLanguage());
    }

    public List<ProjectListDto> findAllProjects() {
         return projectRepository.findAll()
                 .stream()
                .map(p -> new ProjectListDto(p.getProjectId(), p.getName()))
                .toList();
    }

    public List<FolderListDto> findFolders(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        return project.getFolders()
                .stream()
                .map(p -> new FolderListDto(p.getFolderId(), p.getName(), p.getParentFolder() == null ? 0 : p.getParentFolder().getFolderId()))
                .collect(Collectors.toList());
    }
}

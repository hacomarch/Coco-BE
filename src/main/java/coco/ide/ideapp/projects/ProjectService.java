package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.responseDto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDto createProject(CreateProjectForm form) {
        Project project = Project.builder()
                .name(form.getName())
                .language(form.getLanguage())
                .build();

        projectRepository.save(project);
        return new ProjectDto(project.getProjectId(), project.getName(), project.getLanguage());
    }

    @Transactional
    public void deleteProject(Long projectId) throws IllegalArgumentException {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("프로젝트 ID" + projectId + "는 존재하지 않습니다.");
        }
        projectRepository.deleteById(projectId);
    }

}

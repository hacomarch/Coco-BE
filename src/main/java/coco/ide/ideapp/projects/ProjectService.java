package coco.ide.ideapp.projects;

import coco.ide.ideapp.exception.ProjectNotFoundException;
import coco.ide.ideapp.exception.UserNotFoundException;
import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.responsedto.*;
import coco.ide.member.Member;
import coco.ide.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    private static final String ABSOLUTE_PATH = "filedb/";

    @Transactional
    public ProjectDto createProject(CreateProjectForm form) {
        Project project = createNewProject(form);

        Member member = getMember(form.getMemberId());
        project.setMember(member);

        Project savedProject = projectRepository.save(project);

        createProjectDirectory(savedProject);

        return new ProjectDto(savedProject.getProjectId(), savedProject.getName(), savedProject.getLanguage());
    }

    private Project createNewProject(CreateProjectForm form) {
        return Project.builder()
                .name(form.getName())
                .language(form.getLanguage())
                .build();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);
    }

    private void createProjectDirectory(Project project) {
        String dirPath = ABSOLUTE_PATH + project.getMember().getMemberId() + "/" + project.getProjectId();
        File directory = new File(dirPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Transactional
    public void deleteProject(Long projectId) throws IllegalArgumentException {
        Project project = getProjectById(projectId);
        Long memberId = project.getMember().getMemberId();

        deleteProjectDirectory(memberId, projectId);
        projectRepository.deleteById(projectId);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    private void deleteProjectDirectory(Long memberId, Long projectId) {
        String dirPath = ABSOLUTE_PATH + memberId + "/" + projectId;
        Path directory = Paths.get(dirPath);

        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::deletePath);

            File projectDirectory = new File(dirPath);
            projectDirectory.delete();
        } catch (IOException e) {
            log.error("Error walking through directory {}: {}", dirPath, e.getMessage());
        }
    }

    private void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Error deleting file {}:{}", path, e.getMessage());
        }
    }

    @Transactional
    public ProjectDto updateProjectName(Long projectId, String newName) throws IllegalArgumentException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        project.changeName(newName);
        return new ProjectDto(project.getProjectId(), project.getName(), project.getLanguage());
    }

    public List<ProjectListDto> findAllProjects(Long memberId) {
        return projectRepository.findAllByMemberMemberIdWithMember(memberId)
                .stream()
                .map(p -> new ProjectListDto(p.getProjectId(), p.getName()))
                .toList();
    }

    public ProjectChildsDto findChilds(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        List<FolderInfoDto> folderDtos = project.getFolders().stream()
                .map(f -> new FolderInfoDto(f.getFolderId(), f.getName(),
                        f.getParentFolder() != null ? f.getParentFolder().getFolderId() : null))
                .collect(Collectors.toList());

        List<FileInfoDto> fileDtos = project.getFiles().stream()
                .map(f -> new FileInfoDto(f.getFileId(), f.getName(), f.getFolder() == null ? null : f.getFolder().getFolderId()))
                .collect(Collectors.toList());

        return new ProjectChildsDto(folderDtos, fileDtos);
    }

    public String getLanguage(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        return project.getLanguage();
    }
}

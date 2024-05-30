package coco.ide.ideapp.projects;

import coco.ide.ideapp.exception.InvalidProjectCreationFormException;
import coco.ide.ideapp.exception.ProjectNotFoundException;
import coco.ide.ideapp.exception.UserNotFoundException;
import coco.ide.ideapp.folders.Folder;
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
import java.util.Optional;

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
        validateCreateProjectForm(form);
        Project project = createNewProject(form);

        Member member = getMemberById(form.getMemberId());
        project.setMember(member);

        Project savedProject = projectRepository.save(project);

        createProjectDirectory(savedProject);

        return new ProjectDto(savedProject.getProjectId(), savedProject.getName(), savedProject.getLanguage());
    }

    @Transactional
    public void deleteProject(Long projectId) throws IllegalArgumentException {
        Project project = getProjectById(projectId);
        Long memberId = project.getMember().getMemberId();

        deleteProjectDirectory(memberId, projectId);
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public ProjectDto updateProjectName(Long projectId, String newName) throws IllegalArgumentException {
        Project project = getProjectById(projectId);
        project.changeName(newName);

        return new ProjectDto(project.getProjectId(), project.getName(), project.getLanguage());
    }

    public List<ProjectListDto> findProjectsByMemberId(Long memberId) {
        return projectRepository.findAllByMemberMemberIdWithMember(memberId)
                .stream()
                .map(p -> new ProjectListDto(p.getProjectId(), p.getName()))
                .toList();
    }

    public ProjectChildsDto getProjectChildren(Long projectId) {
        Project project = getProjectWithFilesById(projectId);

        List<FolderInfoDto> folderDtos = mapFoldersToFolderInfoDtos(project.getFolders());
        List<FileInfoDto> fileDtos = mapFilesToFileInfoDtos(project.getFiles());

        return new ProjectChildsDto(folderDtos, fileDtos);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    private void validateCreateProjectForm(CreateProjectForm form) {
        if (form == null || form.getName() == null || form.getLanguage() == null || form.getMemberId() == null) {
            throw new InvalidProjectCreationFormException();
        }
    }

    private Project createNewProject(CreateProjectForm form) {
        return Project.builder()
                .name(form.getName())
                .language(form.getLanguage())
                .build();
    }

    private Member getMemberById(Long memberId) {
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

    private void deleteProjectDirectory(Long memberId, Long projectId) {
        String dirPath = ABSOLUTE_PATH + memberId + "/" + projectId;
        Path directory = Paths.get(dirPath);

        try {
            Files.walk(directory) // directory 내의 모든 파일과 하위 디렉토리를 순회
                    .sorted(Comparator.reverseOrder()) //하위 디렉토리와 파일을 먼저 삭제하기 위함
                    .forEach(this::deletePath); //삭제

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

    private List<FolderInfoDto> mapFoldersToFolderInfoDtos(List<Folder> folders) {
        return folders.stream()
                .map(f -> new FolderInfoDto(
                        f.getFolderId(),
                        f.getName(),
                        Optional.ofNullable(f.getParentFolder())
                                .map(Folder::getFolderId).orElse(null)
                ))
                .toList();
    }

    private List<FileInfoDto> mapFilesToFileInfoDtos(List<coco.ide.ideapp.files.File> files) {
        return files.stream()
                .map(f -> new FileInfoDto(
                        f.getFileId(),
                        f.getName(),
                        Optional.ofNullable(f.getFolder())
                                .map(Folder::getFolderId).orElse(null)
                ))
                .toList();
    }

    private Project getProjectWithFilesById(Long projectId) {
        return projectRepository.findProjectWithFilesById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }
}

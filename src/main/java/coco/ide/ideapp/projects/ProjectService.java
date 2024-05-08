package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.responsedto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

        Project savedProject = projectRepository.save(project);

        //프로젝트 생성 시 filedb 밑에 폴더 생성
        String dirPath = "filedb/" + 2 + "/" + savedProject.getProjectId();
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
        }
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

    /*
    Todo : 이 메소드가 프로젝트 들어왔을 때 바로 로딩하는거잖아? 그러니까 폴더 뿐만 아니라 최상위 파일도 가져와야 해서
              Dto도 수정해야하고, 파일 가져오는 코드도 넣어야 할 듯 -> 수정 끝 확인 필요
     */
    public ProjectChildsDto findChilds(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        List<FolderInfoDto> folderDtos = project.getFolders().stream()
                .map(f -> new FolderInfoDto(f.getFolderId(), f.getName(),
                        f.getParentFolder() != null ? f.getParentFolder().getFolderId() : null))
                .collect(Collectors.toList());

        List<FileInfoDto> fileDtos = project.getFiles().stream()
                .map(f -> new FileInfoDto(f.getFileId(), f.getName()))
                .collect(Collectors.toList());

        return new ProjectChildsDto(folderDtos, fileDtos);

    }
}

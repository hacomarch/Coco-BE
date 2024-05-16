package coco.ide.ideapp.projects;

import coco.ide.ideapp.projects.requestdto.CreateProjectForm;
import coco.ide.ideapp.projects.responsedto.*;
import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
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

    @Transactional
    public Long createProject(CreateProjectForm form) {
        Project project = Project.builder()
                .name(form.getName())
                .language(form.getLanguage())
                .build();

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 멤버"));
        project.setMember(member);
        Project savedProject = projectRepository.save(project);

        //프로젝트 생성 시 filedb 밑에 폴더 생성
        String dirPath = "filedb/" + savedProject.getMember().getMemberId() + "/" + savedProject.getProjectId();
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            log.info("created ={}", created);
        }
        return savedProject.getProjectId();
    }

    @Transactional
    public void deleteProject(Long projectId) throws IllegalArgumentException {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("프로젝트 ID" + projectId + "는 존재하지 않습니다.");
        }

        Project findProject = projectRepository.findById(projectId).get();
        Long memberId = findProject.getMember().getMemberId();

        //멤버를 프로젝트 id로 땡겨오는걸로 바꾸기
        String dirPath = "filedb/" + memberId + "/" + projectId;
        Path directory = Paths.get(dirPath);
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        File project = new File(dirPath);
        project.delete();
        projectRepository.deleteById(projectId);

    }

    @Transactional
    public ProjectDto updateProjectName(Long projectId, String newName) throws IllegalArgumentException{
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        project.changeName(newName);
        return new ProjectDto(project.getProjectId(), project.getName(), project.getLanguage());
    }

    //Todo: memberId에 맞는 프로젝트들 가져와야함
    public List<ProjectListDto> findAllProjects() {
         return projectRepository.findAll()
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
}

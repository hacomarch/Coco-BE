package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.requestdto.UpdateFileNameForm;
import coco.ide.ideapp.files.responsedto.ForExecuteDto;
import coco.ide.ideapp.folders.Folder;
import coco.ide.ideapp.folders.FolderRepository;
import coco.ide.ideapp.projects.Project;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.tools.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    //TODO : 일단 memberId를 직접 가져와서 넣는 걸로 하기
    // 추후에 세션 값의 멤버 id를 가져와도 좋을 듯?


    public ForExecuteDto getFilePath(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일이 없습니다."));
        return new ForExecuteDto(file.getPath(), file.getProject().getLanguage());
    }

    @Transactional
    public boolean createFile(Long projectId, Long folderId, CreateFileForm form) {
        Project findProject = projectRepository.findById(projectId).get();
//        Long memberId = findProject.getMember().getMemberId();


        if(isDuplicateName(form.getName(), folderId, projectId)) {
            return false;
        }


        File file = File.builder()
                .name(form.getName())
                .path("")
                .project(findProject)
                .build();


        file.setFolder(folderId != 0 ? folderRepository.findById(folderId).get() : null);

        //filedb에 파일 생성
        String dirPath = folderId != 0 ? "filedb/" + 2 + "/" + projectId + "/" + folderId : "filedb/" + 2 + "/" + projectId;
        java.io.File directory = new java.io.File(dirPath);
        java.io.File createdFile = new java.io.File(directory, form.getName());

        if (!createdFile.exists()) {
            try {
                if (createdFile.createNewFile()) {
                    log.info("파일 생성 성공");
                } else {
                    log.info("파일 생성 실패");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("파일 이미 존재");
        }

        file.setPath(createdFile.getPath());
        fileRepository.save(file);

        return true;
    }

    @Transactional
    public void deleteFile(Long fileId) {
        if (!fileRepository.existsById(fileId)) {
            throw new IllegalArgumentException("파일 ID" + fileId + "는 존재하지 않습니다.");
        }
        fileRepository.deleteById(fileId);
    }

    @Transactional
    public boolean updateFileName(Long fileId, String newName) {
        File file = fileRepository.findById(fileId).get();

        // 파일이 최상위 폴더에 없는 경우, 폴더에 대한 중복 검사 수행
        if (isDuplicateName(newName, file.getFolder() != null ? file.getFolder().getFolderId() : null, file.getProject().getProjectId())) {
            return false;
        }

        file.changeName(newName);
        return true;
    }

    @Transactional
    public boolean updateFilePath(Long fileId, Long folderId) {
        File file = fileRepository.findById(fileId).get();

        if (isDuplicateName(file.getName(), folderId, file.getProject().getProjectId())) {
            return false;
        }

        Folder folder = folderId != 0 ? folderRepository.findById(folderId).get() : null;

        file.setFolder(folder);
        return true;
    }


    private boolean isDuplicateName(String newName, Long parentId, Long projectId) {
        List<File> siblings;
        if (parentId == null || parentId == 0) {
            // 최상위 파일의 경우, 프로젝트 내의 다른 최상위 파일들과 비교
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project does not exist"));
            siblings = project.getFiles().stream()
                    .filter(f -> f.getFolder() == null)
                    .collect(Collectors.toList());
        } else {
            // 하위 파일의 경우, 부모 폴더의 자식 파일들과 비교
            Folder parentFolder = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder does not exist"));
            siblings = parentFolder.getFiles();
        }

        return siblings.stream()
                .anyMatch(f -> f.getName().equals(newName));
    }

    @Transactional
    public void updateFileContent(Long fileId, String newContent) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않습니다."));

        String codePath = file.getPath();
        java.io.File codeFile = new java.io.File(codePath);

        try (FileWriter writer = new FileWriter(codeFile, false)) { // false to overwrite.
            writer.write(newContent); // 새로운 코드로 파일을 덮어씁니다.
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기 중 오류가 발생했습니다.", e);
        }
    }

}

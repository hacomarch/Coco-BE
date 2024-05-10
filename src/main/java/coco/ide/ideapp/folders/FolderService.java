package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.folders.responsedto.FileListDto;
import coco.ide.ideapp.folders.responsedto.FolderDto;
import coco.ide.ideapp.projects.Project;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public boolean createFolder(Long projectId, CreateFolderForm form) {

        if(isDuplicateName(form.getName(), form.getParentId(), projectId)) {
            return false;
        }

        Folder folder = Folder.builder()
                .name(form.getName())
                .parentFolder(form.getParentId() == 0 ? null : folderRepository.findById(form.getParentId()).get())
                .childFolders(new ArrayList<>())
                .files(new ArrayList<>())
                .build();

        folder.setProject(projectRepository.findById(projectId).get());

        Folder savedFolder = folderRepository.save(folder);

        Project findProject = projectRepository.findById(projectId).get();
        Long memberId = findProject.getMember().getMemberId();

        String basicPath = "filedb/" + memberId + "/" + projectId + "/";

        String dirPath = form.getParentId() == 0 ? basicPath + savedFolder.getFolderId() : basicPath + form.getParentId() + "/" + savedFolder.getFolderId();
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return true;
    }

    @Transactional
    public void deleteFolder(Long projectId, Long folderId) throws IllegalArgumentException{
        if (!folderRepository.existsById(folderId)) {
            throw new IllegalArgumentException("폴더 ID" + folderId + "는 존재하지 않습니다.");
        }

        Project findProject = projectRepository.findById(projectId).get();
        Long memberId = findProject.getMember().getMemberId();

        String dirPath = "filedb/" + memberId + "/" + projectId + "/" + folderId;
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

        folderRepository.deleteById(folderId);
    }

    @Transactional
    public boolean updateFolderName(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder does not exist"));

        if (isDuplicateName(newName, folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null, folder.getProject().getProjectId())) {
            return false;
        }
        folder.changeName(newName);
        return true;
    }

    @Transactional
    public boolean updateFolderPath(Long projectId, Long folderId, Long parentId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder does not exist"));
        Folder parentFolder = parentId == 0 ? null : folderRepository.findById(parentId).get();

        if (isDuplicateName(folder.getName(), parentId, folder.getProject().getProjectId())) {
            return false;
        }

        Project findProject = projectRepository.findById(projectId).get();
        Long memberId = findProject.getMember().getMemberId();

        String basicPath = "filedb/" + memberId + "/" + projectId + "/";
        String oldPath = folder.getParentFolder() == null ? basicPath : basicPath + folder.getParentFolder().getFolderId() + "/";

        folder.changeParentFolder(parentFolder);

        String newPath = parentId == 0 ? basicPath : basicPath + parentId + "/";

        // 원래 파일 위치
        java.io.File oldFile = new java.io.File(oldPath + folder.getFolderId());

        // 새 파일 위치
        java.io.File newFile = new java.io.File(newPath + folderId);
        log.info("oldPath = {}", oldPath);
        log.info(newFile.getPath());

        // 파일 이동
        if (oldFile.renameTo(newFile)) {
            System.out.println("File moved successfully");
        } else {
            System.out.println("Failed to move file");
        }

        return true;
    }


    public List<FileListDto> findFiles(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        return folder.getFiles()
                .stream()
                .map(f -> new FileListDto(f.getFileId(), f.getName()))
                .toList();
    }

    private boolean isDuplicateName(String newName, Long parentId, Long projectId) {
        List<Folder> siblings;
        if (parentId == null || parentId == 0) {
            // 최상위 폴더의 경우, 프로젝트 내의 다른 최상위 폴더들과 비교
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project does not exist"));
            siblings = project.getFolders().stream()
                    .filter(f -> f.getParentFolder() == null)
                    .collect(Collectors.toList());
        } else {
            // 하위 폴더의 경우, 부모 폴더의 자식 폴더들과 비교
            Folder parentFolder = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder does not exist"));
            siblings = parentFolder.getChildFolders();
        }

        return siblings.stream()
                .anyMatch(f -> f.getName().equals(newName));
    }


}

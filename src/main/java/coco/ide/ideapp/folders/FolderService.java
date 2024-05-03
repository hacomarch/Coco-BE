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

import java.util.ArrayList;
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
    public void createFolder(Long projectId, CreateFolderForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("폴더 명은 빈 칸일 수 없습니다.");
        }

        Folder folder = Folder.builder()
                .name(form.getName())
                .parentFolder(form.getParentId() == 0 ? null : folderRepository.findById(form.getParentId()).get())
                .childFolders(new ArrayList<>())
                .files(new ArrayList<>())
                .build();

        folder.setProject(projectRepository.findById(projectId).get());

        folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(Long folderId) throws IllegalArgumentException{
        if (!folderRepository.existsById(folderId)) {
            throw new IllegalArgumentException("폴더 ID" + folderId + "는 존재하지 않습니다.");
        }
        folderRepository.deleteById(folderId);
    }

    @Transactional
    public boolean updateFolderName(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("folder does not exist"));

        if (checkName(newName, folder)) return false;

        folder.changeName(newName);
        return true;
    }



    //Todo : 경로 이동 시 이름 중복 확인해야함
    //리턴값 boolean으로 바꾸고 경로 이동 실패 시 false, 성공 시 true 리턴
    @Transactional
    public boolean updateFolderPath(Long folderId, Long parentId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("folder does not exist"));

        Folder parentFolder = parentId == 0 ? null : folderRepository.findById(parentId).get();

//        if (parentId == 0) {
//            Project project = projectRepository.findById(folder.getProject().getProjectId()).get();
//
//        }
        if (checkName(folder.getName(), folder)) return false;

        folder.changeParentFolder(parentFolder);
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

    private boolean checkName(String newName, Folder folder) {
        if (folder.getParentFolder() == null) {
            Project project = projectRepository.findById(folder.getProject().getProjectId()).get();
            boolean isDuplicated = project.getFolders().stream()
                    .map(f -> f.getName())
                    .anyMatch(name -> name.equals(newName));
            log.info("project's folder = {}", project.getFolders());

            if (isDuplicated) {
                return true;
            }
        } else {
            Folder parentFolder = folderRepository.findById(folder.getParentFolder().getFolderId()).get();
            boolean isDuplicated = parentFolder.getChildFolders().stream()
                    .map(f -> f.getName())
                    .anyMatch(name -> name.equals(newName));
            List<String> list = parentFolder.getChildFolders().stream().map(f -> f.getName()).toList();
            for (String s : list) {
                log.info("child folder = {}", s);
            }
            if (isDuplicated) {
                return true;
            }
        }
        return false;
    }

}

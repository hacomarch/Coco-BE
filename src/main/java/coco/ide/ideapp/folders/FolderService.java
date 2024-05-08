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
    public boolean createFolder(Long projectId, CreateFolderForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("폴더 명은 빈 칸일 수 없습니다.");
        }

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

        String dirPath = "filedb/" + 2 + "/" + projectId + "/" + savedFolder.getFolderId();
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
        }
        return true;
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
                .orElseThrow(() -> new RuntimeException("Folder does not exist"));

        if (isDuplicateName(newName, folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null, folder.getProject().getProjectId())) {
            return false;
        }

        folder.changeName(newName);
        return true;
    }

    @Transactional
    public boolean updateFolderPath(Long folderId, Long parentId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder does not exist"));
        Folder parentFolder = parentId == 0 ? null : folderRepository.findById(parentId).get();

        if (isDuplicateName(folder.getName(), parentId, folder.getProject().getProjectId())) {
            return false;
        }

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

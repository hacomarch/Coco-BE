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
                .orElseThrow(() -> new RuntimeException("Folder does not exist"));

        if (isDuplicateName(newName, folder, folder.getParentFolder())) {
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

        if (isDuplicateName(folder.getName(), folder, parentFolder)) {
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

    private boolean isDuplicateName(String newName, Folder folder, Folder newParentFolder) {
        List<Folder> siblings;
        if (newParentFolder == null) {
            // 최상위 폴더의 경우, 폴더가 속한 프로젝트 내의 다른 최상위 폴더들과 비교
            siblings = folder.getProject().getFolders().stream()
                    .filter(f -> f.getParentFolder() == null && !f.equals(folder))
                    .collect(Collectors.toList());
        } else {
            // 하위 폴더의 경우, 부모 폴더의 자식 폴더들과 비교
            siblings = newParentFolder.getChildFolders().stream()
                    .filter(f -> !f.equals(folder))
                    .collect(Collectors.toList());

        }

        return siblings.stream()
                .anyMatch(f -> f.getName().equals(newName));
    }
}

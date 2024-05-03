package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.folders.responsedto.FileListDto;
import coco.ide.ideapp.folders.responsedto.FolderDto;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public FolderDto updateFolderName(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("folder does not exist"));

        folder.changeName(newName);
        return new FolderDto(folder.getFolderId(), folder.getName());
    }

    @Transactional
    public void updateFolderPath(Long folderId, Long parentId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("folder does not exist"));

        Folder parentFolder = parentId == 0 ? null : folderRepository.findById(parentId).get();
        folder.changeParentFolder(parentFolder);
    }

    public List<FileListDto> findFiles(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("project does not exist"));

        return folder.getFiles()
                .stream()
                .map(f -> new FileListDto(f.getFileId(), f.getName()))
                .toList();
    }

}

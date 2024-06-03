package coco.ide.ideapp.folders;

import coco.ide.ideapp.exception.DuplicateNameException;
import coco.ide.ideapp.exception.FolderMoveException;
import coco.ide.ideapp.exception.FolderNotFoundException;
import coco.ide.ideapp.exception.ProjectNotFoundException;
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

import static coco.ide.ideapp.FileDB.ABSOLUTE_PATH;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public FolderDto createFolder(Long projectId, CreateFolderForm form) {
        isDuplicateName(form.getName(), form.getParentId(), projectId);

        Folder folder = buildFolder(form);
        setProjectForFolder(folder, projectId);

        Folder savedFolder = folderRepository.save(folder);
        createFolderDirectory(savedFolder, projectId, form.getParentId());

        return new FolderDto(savedFolder.getFolderId(), savedFolder.getName());
    }

    private Folder buildFolder(CreateFolderForm form) {
        Folder parentFolder = form.getParentId() == 0 ? null : getFolderById(form.getParentId());
        return Folder.builder()
                .name(form.getName())
                .parentFolder(parentFolder)
                .childFolders(new ArrayList<>())
                .files(new ArrayList<>())
                .build();
    }

    private void setProjectForFolder(Folder folder, Long projectId) {
        Project project = getProjectById(projectId);
        folder.setProject(project);
    }

    private void createFolderDirectory(Folder folder, Long projectId, Long parentId) {
        Project project = folder.getProject();
        Long memberId = project.getMember().getMemberId();
        String dirPath = buildDirectoryPath(memberId, projectId, folder.getFolderId(), parentId);

        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private String buildDirectoryPath(Long memberId, Long projectId, Long folderId, Long parentId) {
        String basicPath = ABSOLUTE_PATH + memberId + "/" + projectId + "/";
        return parentId == 0 ? basicPath + folderId : basicPath + parentId + "/" + folderId;
    }

    @Transactional
    public void deleteFolder(Long projectId, Long folderId) {
        Folder folder = getFolderWithProjectAndParentFolderAndChildrenById(folderId);
        Long memberId = folder.getProject().getMember().getMemberId();
        long parentId = folder.getParentFolder() == null ? 0 : folder.getParentFolder().getFolderId();

        String dirPath = buildDirectoryPath(memberId, projectId, folderId, parentId);
        deleteDirectory(Paths.get(dirPath));

        folderRepository.deleteById(folderId);
    }

    private Folder getFolderWithProjectAndParentFolderAndChildrenById(Long folderId) {
        return folderRepository.findFolderWithProjectAndParentFolderAndChildrenById(folderId)
                .orElseThrow(FolderNotFoundException::new);
    }

    private void deleteDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::deletePath);
        } catch (IOException e) {
            log.error("Error walking through directory {}: {}", directory, e.getMessage());
        }
    }

    private void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Error deleting file {}: {}", path, e.getMessage());
        }
    }

    @Transactional
    public FolderDto updateFolderName(Long folderId, String newName) {
        Folder folder = getFolderById(folderId);
        Long parentId = folder.getParentFolder() != null ? folder.getParentFolder().getFolderId() : null;

        isDuplicateName(newName, parentId, folder.getProject().getProjectId());

        folder.changeName(newName);
        return new FolderDto(folderId, folder.getName());
    }

    @Transactional
    public FolderDto updateFolderPath(Long projectId, Long folderId, Long newParentId) {
        Folder folder = getFolderById(folderId);

        isDuplicateName(folder.getName(), newParentId, projectId);

        Long memberId = folder.getProject().getMember().getMemberId();
        Long oldParentId = folder.getParentFolder() == null ? 0 : folder.getParentFolder().getFolderId();
        String oldPath = buildFolderPath(memberId, projectId, oldParentId);
        String newPath = buildFolderPath(memberId, projectId, newParentId);

        moveFolder(oldPath, newPath, folderId);

        Folder newParentFolder = newParentId == 0 ? null : getFolderById(newParentId);
        folder.changeParentFolder(newParentFolder);
        return new FolderDto(folderId, folder.getName());
    }

    private String buildFolderPath(Long memberId, Long projectId, Long parentId) {
        String basicPath = ABSOLUTE_PATH + memberId + "/" + projectId + "/";
        return parentId == 0 ? basicPath : basicPath + parentId + "/";
    }

    private void moveFolder(String oldPath, String newPath, Long folderId) {
        File oldFile = new File(oldPath + folderId);
        File newFile = new File(newPath + folderId);

        if (!oldFile.renameTo(newFile)) {
            throw new FolderMoveException();
        }
    }

    public List<FileListDto> findFiles(Long folderId) {
        return getFolderById(folderId)
                .getFiles()
                .stream()
                .map(f -> new FileListDto(f.getFileId(), f.getName()))
                .toList();
    }

    private void isDuplicateName(String newName, Long parentId, Long projectId) {
        if (getSiblings(parentId, projectId)
                .stream()
                .anyMatch(f -> f.getName().equals(newName))) {
            throw new DuplicateNameException();
        }
    }

    private List<Folder> getSiblings(Long parentId, Long projectId) {
        if (parentId == null || parentId == 0) {
            return getTopLevelSiblings(projectId);
        } else {
            return getChildSiblings(parentId);
        }
    }

    private List<Folder> getTopLevelSiblings(Long projectId) {
        return getProjectById(projectId)
                .getFolders()
                .stream()
                .filter(f -> f.getParentFolder() == null)
                .toList();
    }

    private List<Folder> getChildSiblings(Long parentId) {
        return getFolderById(parentId).getChildFolders();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    private Folder getFolderById(Long folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(FolderNotFoundException::new);
    }
}

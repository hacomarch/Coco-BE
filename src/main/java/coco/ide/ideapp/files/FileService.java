package coco.ide.ideapp.files;

import coco.ide.ideapp.exception.*;
import coco.ide.ideapp.exception.FileNotFoundException;
import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.responsedto.FileDto;
import coco.ide.ideapp.folders.Folder;
import coco.ide.ideapp.folders.FolderRepository;
import coco.ide.ideapp.projects.Project;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static coco.ide.ideapp.FileDB.ABSOLUTE_PATH;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public FileDto createFile(Long projectId, Long folderId, CreateFileForm form) {
        isDuplicateName(form.getName(), folderId, projectId);

        File file = buildFile(projectId, form);

        String dirPath = buildPath(projectId, folderId);
        java.io.File createdFile = new java.io.File(dirPath, form.getName());
        createPhysicalFile(createdFile);

        file.setFolder(folderId == 0 ? null : getFolderById(folderId));
        file.changePath(createdFile.getPath());
        File savedFile = fileRepository.save(file);

        return new FileDto(
                savedFile.getFileId(),
                savedFile.getName(),
                savedFile.getPath());
    }

    private File buildFile(Long projectId, CreateFileForm form) {
        Project project = getProjectById(projectId);
        return File.builder()
                .name(form.getName())
                .path("")
                .project(project)
                .build();
    }

    public String buildPath(Long projectId, Long folderId) {
        Long memberId = getMemberIdByProjectId(projectId);
        String basicPath = ABSOLUTE_PATH + memberId + "/" + projectId + "/";
        return folderId == 0 ? basicPath : basicPath + folderId + "/";
    }

    private void createPhysicalFile(java.io.File createdFile) {
        if (!createdFile.exists()) {
            try {
                createdFile.createNewFile();
            } catch (IOException e) {
                throw new FileException("파일을 생성할 수 없습니다.");
            }
        } else {
            throw new FileException("이미 존재하는 파일입니다.");
        }
    }

    @Transactional
    public void deleteFile(Long projectId, Long folderId, Long fileId) {
        File file = getFileById(fileId);
        String dirPath = buildPath(projectId, folderId) + file.getName();

        deletePhysicalFile(dirPath);
        fileRepository.deleteById(fileId);
    }

    private void deletePhysicalFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new FileException("파일을 삭제할 수 없습니다.");
            }
        } else {
            throw new FileException("파일이 존재하지 않습니다.");
        }
    }

    @Transactional
    public FileDto updateFileName(Long fileId, String newName) {
        File file = getFileById(fileId);
        Long folderId = file.getFolder() == null ? null : file.getFolder().getFolderId();
        isDuplicateName(newName, folderId, file.getProject().getProjectId());

        String oldPath = file.getPath();
        String newPath = getNewFilePath(oldPath, newName);

        renamePhysicalFile(oldPath, newPath);

        file.changeName(newName);
        file.changePath(newPath);
        return new FileDto(fileId, newName, newPath);
    }

    private String getNewFilePath(String oldPath, String newName) {
        Path filePath = Paths.get(oldPath).getParent();
        return filePath + "/" + newName;
    }

    private void renamePhysicalFile(String oldPath, String newPath) {
        java.io.File oldFile = new java.io.File(oldPath);
        java.io.File newFile = new java.io.File(newPath);

        if (!oldFile.renameTo(newFile)) {
            throw new FileException("파일을 변경할 수 없습니다.");
        }
    }

    @Transactional
    public FileDto updateFilePath(Long projectId, Long folderId, Long fileId) {
        File file = getFileById(fileId);
        isDuplicateName(file.getName(), folderId, file.getProject().getProjectId());

        String oldPath = file.getPath();
        String newPath = buildPath(projectId, folderId) + file.getName();

        renamePhysicalFile(oldPath, newPath);

        file.setFolder(folderId == 0 ? null : getFolderById(folderId));
        file.changePath(newPath);
        return new FileDto(fileId, file.getName(), newPath);
    }


    @Transactional
    public void updateFileContent(Long fileId, String newContent) {
        File file = getFileById(fileId);
        updatePhysicalFileContent(file.getPath(), newContent);
    }

    private void updatePhysicalFileContent(String filePath, String newContent) {
        java.io.File file = new java.io.File(filePath);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(newContent);
        } catch (IOException e) {
            throw new FileException("파일에 새로운 내용을 반영할 수 없습니다.");
        }
    }

    public String getFileContent(Long fileId) {
        String filePath = getFileById(fileId).getPath();

        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new FileException("파일을 읽을 수 없습니다.");
        }
    }

    public File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(FileNotFoundException::new);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
    }

    private Folder getFolderById(Long folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(FolderNotFoundException::new);
    }

    private Long getMemberIdByProjectId(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(ProjectNotFoundException::new)
                .getMember().getMemberId();
    }

    private void isDuplicateName(String newName, Long parentId, Long projectId) {
        if (getSiblings(parentId, projectId)
                .stream()
                .anyMatch(f -> f.getName().equals(newName))) {
            throw new DuplicateNameException();
        }
    }

    private List<File> getSiblings(Long parentId, Long projectId) {
        if (parentId == null || parentId == 0) {
            return getTopLevelSiblings(projectId);
        } else {
            return getChildSiblings(parentId);
        }
    }

    private List<File> getTopLevelSiblings(Long projectId) {
        return getProjectById(projectId)
                .getFiles()
                .stream()
                .filter(f -> f.getFolder() == null)
                .toList();
    }

    private List<File> getChildSiblings(Long parentId) {
        return getFolderById(parentId).getFiles();
    }
}

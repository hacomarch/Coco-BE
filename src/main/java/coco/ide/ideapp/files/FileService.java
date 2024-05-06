package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.files.requestdto.UpdateFileNameForm;
import coco.ide.ideapp.folders.Folder;
import coco.ide.ideapp.folders.FolderRepository;
import coco.ide.ideapp.projects.Project;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    //TODO : 일단 memberId를 직접 가져와서 넣는 걸로 하기
    // 추후에 세션 값의 멤버 id를 가져와도 좋을 듯?
    @Transactional
    public void createFile(Long projectId, Long folderId, CreateFileForm form) {
        Project findProject = projectRepository.findById(projectId).get();
        Long memberId = findProject.getMember().getMemberId();

        File file = File.builder()
                .name(form.getName())
                .path("/filedb/" + memberId + "/" + projectId)
                .project(findProject)
                .build();

        if (folderId != 0) {
            file.setFolder(folderRepository.findById(folderId).get());
        }

        fileRepository.save(file);
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
        Optional<Folder> optionalFolder = folderRepository.findById(file.getFolder().getFolderId());

        if (optionalFolder.isEmpty()) { //최상위 파일인 경우
            if (isNameDuplicate(file.getProject().getFiles(), newName)) { //같은 이름의 파일이 있는 경우
                return false;
            }
        } else if (isNameDuplicate(optionalFolder.get().getFiles(), newName)) { //폴더 안에 있는 파일들 중 같은 이름이 있는 경우
            return false;
        }

        file.changeName(newName);
        return true;
    }

    @Transactional
    public boolean updateFilePath(Long fileId, Long folderId) {
        File file = fileRepository.findById(fileId).get();

        if (folderId == 0) { //최상위 위치로 파일 경로를 변경하는 경우
            if (isNameDuplicate(file.getProject().getFiles(), file.getName())) {
                return false;
            }

            file.setFolder(null);
            return true;
        }

        Folder folder = folderRepository.findById(folderId).get();
        if (isNameDuplicate(folder.getFiles(), file.getName())) {
            return false;
        }

        file.setFolder(folder);
        return true;
    }

    private boolean isNameDuplicate(List<File> files, String newName) {
        return files.stream().anyMatch(f -> f.getName().equals(newName));
    }

    //Todo : 파일을 찾아서 읽은 다음에 이 내용을 응답으로 줘야함

}

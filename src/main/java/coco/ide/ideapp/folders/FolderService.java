package coco.ide.ideapp.folders;

import coco.ide.ideapp.folders.requestdto.CreateFolderForm;
import coco.ide.ideapp.projects.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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

}

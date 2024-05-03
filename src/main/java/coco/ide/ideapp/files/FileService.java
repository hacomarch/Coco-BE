package coco.ide.ideapp.files;

import coco.ide.ideapp.files.requestdto.CreateFileForm;
import coco.ide.ideapp.folders.Folder;
import coco.ide.ideapp.folders.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    //TODO : path에는 어떤 값을 넣어야 할까? 로컬 서버에 파일을 저장하기 때문에 찾아올 수 있는 경로를 적는 것이 좋을 것 같다.
    // 그럼 memberId/projectId/안에 넣는게 좋으려나?
    // 그럼 memberId는 현재 세션에 저장되어 있는 멤버의 id를 가져오는게 좋나?
    // 현재 세션에는 멤버의 어떤 정보가 저장되어 있을까? -> 소은이한테 물어보기
    // 그럼 아래 메서드에서 현재 세션에 등록되어 있는 멤버의 정보가 필요하다.
    @Transactional
    public void createFile(Long projectId, Long folderId, CreateFileForm form) {
        File file = File.builder()
                .name(form.getName())
                .path("/memberId/" + projectId)
                .build();

        file.setFolder(folderRepository.findById(folderId).get());

        fileRepository.save(file);
    }

}

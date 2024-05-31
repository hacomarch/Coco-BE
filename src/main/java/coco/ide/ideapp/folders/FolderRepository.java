package coco.ide.ideapp.folders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    @Query("select f from Folder f" +
            " left join fetch f.project" +
            " left join fetch f.parentFolder" +
            " left join fetch f.childFolders" +
            " where f.folderId = :folderId")
    Optional<Folder> findFolderWithProjectAndParentFolderAndChildrenById(Long folderId);
}

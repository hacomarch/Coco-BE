package coco.ide.ideapp.projects;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p " +
            "join fetch p.member m " +
            "where m.memberId = :memberId")
    List<Project> findAllByMemberMemberIdWithMember(Long memberId);

    @Query("select p from Project p" +
            " join fetch p.files f" +
            " where p.projectId = :projectId")
    Optional<Project> findProjectWithFilesById(Long projectId);
}

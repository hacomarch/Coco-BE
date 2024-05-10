package coco.ide.member.repository;

import coco.ide.attendance.Attend;
import coco.ide.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
=======
import java.time.LocalDate;
>>>>>>> 4f6d6f3461241f8afca98008526e98ad349241c6
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}

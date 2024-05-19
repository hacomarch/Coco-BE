package coco.ide.attendance;

import coco.ide.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendRepository extends JpaRepository<Attend, Long> {
    List<Attend> findByMember(Member member);
}

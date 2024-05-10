package coco.ide.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendRepository extends JpaRepository<Attend, Long> {
}

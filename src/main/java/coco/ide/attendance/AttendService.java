package coco.ide.attendance;

import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendService {

    private final AttendRepository attendRepository;

    private final MemberRepository memberRepository;

    // 출석도장 찍기
    public void makeAttendStamp(RequestAttendDto requestAttendDto) {

        Member member = memberRepository.findById(requestAttendDto.getMemberId()).orElseThrow(()
                -> new IllegalArgumentException("No member found with ID: " + requestAttendDto.getMemberId()));
        LocalDate today = LocalDate.now();

        List<Attend> todayList = attendRepository.findAll().stream()
                .filter(attend -> attend.getAttendDate().isEqual(today))
                .collect(Collectors.toList());

        boolean hasAttended = todayList.stream()
                .anyMatch(attend -> attend.getMember().equals(member));

        if (hasAttended) {
            log.info("이미 출석체크를 완료하였습니다!");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 출석체크를 완료하였습니다.");
        }

        Attend attend = Attend.builder()
                .member(member)
                .attendDate(LocalDate.now())
                .build();

        attendRepository.save(attend);
    }
}

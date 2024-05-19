package coco.ide.attendance;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AttendController {

    private final AttendService attendService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/attend")
    public List<ResponseAttendDto> allAttends(@RequestParam(name = "memberId") Long memberId) {
//        log.info("controller, memberId: {}", memberId);
        return attendService.allAttends(memberId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/attend")
    public void makeAttend(@RequestBody RequestAttendDto requestAttendDto) {
        log.info("memberId : {}", requestAttendDto.getMemberId());

        attendService.makeAttendStamp(requestAttendDto);
    }
}

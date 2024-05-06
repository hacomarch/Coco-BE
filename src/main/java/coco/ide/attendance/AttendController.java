package coco.ide.attendance;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AttendController {

    private final AttendService attendService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/attend")
    public void makeAttend(@RequestBody RequestAttendDto requestAttendDto) {
        log.info("memberId : {}", requestAttendDto.getMemberId());

        attendService.makeAttendStamp(requestAttendDto);
    }
}

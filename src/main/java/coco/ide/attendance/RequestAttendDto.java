package coco.ide.attendance;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestAttendDto {

    private Long memberId;

    public RequestAttendDto(Long memberId) {
        this.memberId = memberId;
    }
}

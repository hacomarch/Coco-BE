package coco.ide.attendance;

import coco.ide.chatting.responseDto.ResponseChatDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ResponseAttendDto {

    private Long memberId;
    private List<LocalDate> attendDate;

    @Builder
    public ResponseAttendDto(Long memberId, List<LocalDate> attendDate) {
        this.memberId = memberId;
        this.attendDate = attendDate;
    }
}
